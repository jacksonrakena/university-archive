#include <string.h>
#include "dbms.h"
#include <stdio.h>
#include <stdlib.h>

/**
 * Prints a text string, forcing it to be 'size' wide,
 * by either padding it if it's too short, or trimming the end
 * if it's too wide.
 * @param text The char array to print.
 * @param size The required size.
 */
void print_sized(char text[], unsigned int size) {
    unsigned int array_size = strlen(text);
    unsigned int padding = 0;
    if (array_size < size) {
        padding = size-array_size;
    }

    if (padding > 0) {
        for (int i = 0; i < padding; i++) {
            printf(" ");
        }
    }

    for (int i = 0; i < size-padding; i++) {
        printf("%c", text[i]);
    }
}

int db_show_row(const struct db_table *db, unsigned int row) {
    if (row > db->rows_used) return 0;
    struct album target = db->table[row];
    // Buffer to store int -> char* conversions
    char int_buf[100];

    // Write id
    sprintf(int_buf, "%lu", target.id);
    print_sized(int_buf, 6);
    
    printf(":");

    // Write title
    print_sized(target.title, 20);

    printf(":");

    // Write artist
    print_sized(target.artist, 20);

    printf(":");

    // Write year
    sprintf(int_buf, "%hu", target.year);
    print_sized(int_buf, 4);

    printf("\n");
    return 1;
}

int db_add_row(struct db_table *db, struct album *a) {
    // Initialise the table if it is null
    if (db->table == NULL) {
        db->rows_used = 0;
        db->table = calloc(5, sizeof(struct album));
        if (db->table == NULL) return 0; // memory allocation forgor
        db->rows_total = 5;
    }

    // Jump to the end of the array
    struct album* ptr = db->table + db->rows_used;

    // Resize array if it is too small
    if (db->rows_used >= db->rows_total) {
        struct album* check = realloc(db->table, (db->rows_used+5)*sizeof(struct album));
        if (check == NULL) return 0;
        db->rows_total += 5;
    }

    // Copy album into array
    memcpy(ptr, a, sizeof(struct album));
    db->rows_used++;
    return 1;
}

int db_remove_row(struct db_table *db, unsigned long id) {
    // Look for row with specified ID
    for (int i = 0; i < db->rows_used; i++) {
        struct album* ptr = db->table + i;
        if (ptr->id == id) {
            // Iterate through rest of array, moving each element
            // one index forward
            for (int start = i; start < db->rows_used-1; start++) {
                struct album* content = db->table+start+1;
                struct album* target = db->table+start;
                memcpy(target, content, sizeof(struct album));
            }

            // Account for the missing row by reducing the size
            db->rows_used--;

            // Deallocate rows if there's 5 spare rows
            if (db->rows_total - db->rows_used == 5) {
                db->table = realloc(db->table, (db->rows_total - 5)*sizeof(struct album));
                if (db->table == NULL) return 0;
                db->rows_total -= 5;
            }
            return 1;
        }
    }
    return 0;
}