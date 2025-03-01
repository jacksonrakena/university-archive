#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
// Include necessary header files

/**
 * The main function should be able to accept a command-line argument
 * argv[0]: program name
 * argv[1]: port number
 * 
 * Read the assignment handout for more details about the server program
 * design specifications.
 */

#define BUFFER_SIZE 1024 // Standard receive/read buffer size

#define CLIENT_CLOSED_CONNECTION 1002 // Client closed connection unexpectedly
#define CLIENT_EOF 1003 // Client has ended the connection by sending eof

/*
 * Whether the server is still ready for connections.
 */
volatile sig_atomic_t server_listening = 1;

/*
 * Handle an incoming SIGTERM. Shuts the server down.
 */
void handle_sigterm(int signum) {
    server_listening = 0;
}

/*
 * Prints the error message and then exits with error code -1.
 */
void error_exit(char error[]) {
    printf("error: %s", error);
    exit(-1);
}

/*
 * Transmits the provided char buffer to the provided socket.
 */
void send_const(int socket_id, char* msg) {
    send(socket_id, msg, strlen(msg), 0);
}

/*
 * Handles a GET <filename> operation. Requires the entire original received buffer and the socket descriptor.
 */
void handle_read_operation(char* recv_buffer, int socket_id) {
    char* filename = recv_buffer+4; // Advance by 4 characters to get the filename.
    printf("opening: %s\n", filename);
    FILE*ptr;

    // Do not accept empty filenames.
    if (strlen(filename) == 0) {
        send_const(socket_id, "SERVER 500 Get Error\n");
        return;
    }

    // Do not accept spaces in file names.
    if (strchr(filename, ' ') != NULL) {
        send_const(socket_id, "SERVER 500 Get Error\n");
        return;
    }

    // Return a 404 error if we cannot open the file for reading.
    if ((ptr = fopen(filename,"r")) == NULL) {
        send_const(socket_id, "SERVER 404 Not Found\n");
        return;
    }

    int write_char_buffer; // stores each read char from the file
    send_const(socket_id, "SERVER 200 OK\n\n"); // Send opening headers

    // Read until EOF, and write each char to the stream.
    while ((write_char_buffer = fgetc(ptr)) != EOF) {
        send(socket_id, (const char *) &write_char_buffer, sizeof(write_char_buffer), 0);
    }

    // Send closing headers and close.
    send_const(socket_id, "\n\n\n");
    fclose(ptr);
}

/*
 * Handles a PUT <filename> operation. Reads until two newlines are received and then saves and closes the file.
 */
void handle_write_operation(char* recv_buffer, int socket_id) {
    char* filename = recv_buffer+4;
    printf("writing: %s\n", filename);
    FILE*ptr;

    if (strlen(filename) == 0 || (ptr = fopen(filename,"w")) == NULL) {
        send_const(socket_id, "SERVER 501 Put Error\n");
        return;
    }

    // Do not accept spaces in file names.
    if (strchr(filename, ' ') != NULL) {
        send_const(socket_id, "SERVER 501 Put Error\n");
        return;
    }

    char write_buffer[BUFFER_SIZE] = {}; // this buffer holds received data before it is written
    int was_last_line_newline = 0; // this variable remembers if the last line was a newline
    while ((read(socket_id, write_buffer, BUFFER_SIZE)) > 0) {
        if (write_buffer[0] == '\n') {
            if (was_last_line_newline) {
                // Close and end operation.
                send_const(socket_id, "SERVER 201 Created\n");
                printf("finished writing to %s \n", filename);
                fclose(ptr);
                return;
            }
            was_last_line_newline = 1;
        }
        fputs(write_buffer, ptr); // write the full buffer to the file.
        memset(write_buffer, 0, BUFFER_SIZE); // Clear the buffer for the next read.
    }
}

/*
 * Handles an incoming connection.
 */
int handle_connection(int socket_id) {
    printf("accepted socket %d \n", socket_id);

    char recv_buffer[BUFFER_SIZE] = {};

    // Read the socket until it is empty.
    while ((read(socket_id, recv_buffer, BUFFER_SIZE)) > 0) {
        printf("received: %s \n", recv_buffer);
        recv_buffer[strcspn(recv_buffer, "\n")] = 0; // Remove the newline from the end.

        if (strcasecmp(recv_buffer, "BYE") == 0) {
            printf("received bye. closing\n");
            close(socket_id);
            return CLIENT_CLOSED_CONNECTION;
        } else if (strncasecmp(recv_buffer, "GET", 3) == 0) {
            handle_read_operation(recv_buffer, socket_id);
        } else if (strncasecmp(recv_buffer, "PUT", 3) == 0) {
            handle_write_operation(recv_buffer, socket_id);
        } else {
            send_const(socket_id, "SERVER 502 Command Error\n");
        }

        // Clear the buffer for the next read.
        memset(recv_buffer, 0, BUFFER_SIZE);
    }
    printf("received socket EOF. closing \n");
    close(socket_id);
    return CLIENT_EOF;
}

long parse_port(int argc, char *argv[]) {
    char* port_parse_out;
    if (argc < 2) {
        error_exit("required a port.");
    }
    long port = strtol(argv[1], &port_parse_out, 10);
    if (*port_parse_out) {
        error_exit("failed to parse port.");
    }
    if (port < 1024) {
        error_exit("port number must be higher than or equal to 1024.");
    }
    return port;
}

int main(int argc, char *argv[])
{
    // Set up SIGTERM handler.
    struct sigaction action;
    memset(&action, 0, sizeof(struct sigaction));
    action.sa_handler = handle_sigterm;
    sigaction(SIGTERM, &action, NULL);

    long port = parse_port(argc, argv);

    int socket_descriptor;

    // Initialise socket
    if ((socket_descriptor = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        error_exit("failed to initialise socket");
    }

    struct sockaddr_in address;
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(port);

    if (bind(socket_descriptor, (struct sockaddr*)&address, sizeof(address)) < 0) {
        error_exit("failed to bind to port");
    }

    if (listen(socket_descriptor, 3) < 0) {
        error_exit("failed to listen.");
    }

    int opened_socket;
    int addrlen = sizeof(address);

    printf("listening on port %ld \n", port);

    while (server_listening) {
        if ((opened_socket = accept(socket_descriptor, (struct sockaddr*)&address, (socklen_t*)&addrlen)) < 0) {
            error_exit("failed to accept socket");
        }

        handle_connection(opened_socket);
    }

    printf("shutting down\n");
    shutdown(socket_descriptor, SHUT_RDWR);
    return 0;
}