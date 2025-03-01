#include "editor.h"
#include <string.h>
#include <stdio.h>

int editor_insert_char(char editing_buffer[], int editing_buflen,
                       char to_insert, int pos) {
    // Return if the position is outside the bounds of the buffer
    if (pos < 0 || pos > editing_buflen - 1) {
        return 0;
    }

    // Loop over every character in the buffer, starting from the end
    for (int i = editing_buflen - 1; i > pos; i--) {
        // Do not switch the last character in the buffer
        if (i != editing_buflen) {
            // Swap this item out with the item before it
            editing_buffer[i] = editing_buffer[i - 1];
        }
    }

    // Insert the new item into the space left by the switching
    editing_buffer[pos] = to_insert;
    return 1;
}

int editor_delete_char(char editing_buffer[], int editing_buflen, char to_delete, int offset) {
    // Return if the offset is outside the bounds of the buffer
    if (offset < 0 || offset > editing_buflen - 1) {
        return 0;
    }

    // Record whether we've found (because we can only replace the first incidence)
    int found = 0;

    // Loop over all the characters in the buffer
    for (int i = offset; i < editing_buflen; i++) {
        // If we've found the character and we haven't already replaced it
        if (editing_buffer[i] == to_delete && found == 0) {
            found = 1;
        }

        // Swap this character with the one after it if we've found the character
        if (found) {
            if (i >= editing_buflen) {
                editing_buffer[i] = '\0';
            } else {
                editing_buffer[i] = editing_buffer[i + 1];
            }
        }
    }
    return found;
}

int editor_replace_str(char editing_buffer[], int editing_buflen, const char *str, const char *replacement,
                        int offset) {
    // Record the lengths of the target and the replacement
    size_t len_str = strlen(str);
    size_t len_repl = strlen(replacement);

    if (len_str == 0) return -1;
    int current_match = 0;
    for (int i = offset; i < editing_buflen; i++) {
        // if the current character is the same as the current character in the target
        if (editing_buffer[i] == str[current_match]) {
            // try and match the next character in the next iteration of the for loop
            current_match++;

            // If we've reached the end of the target and we've found a match
            if (current_match == len_str - 1) {
                int start = i-len_str+2;
                int insertions = start;
                for(int index = start; index < len_str+start; index++){
                    editor_delete_char(editing_buffer,editing_buflen, editing_buffer[start],start);
                }
                for(int m = 0; m < len_repl; m++){
                    editor_insert_char(editing_buffer,editing_buflen, replacement[m],start+m);
                    insertions++;
                }
                editing_buffer[editing_buflen-1] = '\0';
                if (insertions > editing_buflen) return editing_buflen-1;
                return insertions-1;
            }
        }
        else {
            current_match = 0;
        }
    }
    return -1;
}

void editor_view(int rows, int cols,
                 char viewing_buffer[rows][cols],
                 const char editing_buffer[], int editing_buflen, int wrap) {
    // Clear the buffer by inserting null everywhere
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            viewing_buffer[r][c] = '\0';
        }
    }

    // write_row and write_col control the current positions of the write heads
    int write_row = 0;
    int write_col = 0;

    // Feed every character from the editing buffer
    for (int i = 0; i < editing_buflen; i++) {
        char e = editing_buffer[i];

        // if we've reached the end of a line, or we need to wrap
        if (e == '\n' || (wrap != 0 && write_col == (cols - 1))) {
            // Move the write head down one row, and reset it to the start of the line
            write_row++;
            write_col = 0;

            // We don't want to write the newline symbol to the new line, so skip the rest of the loop
            if (e == '\n')
                continue;
        }

        // Only write to the write head if we're currently in the bounds of the view
        if (write_col < (cols - 1) && write_row < (rows - 1)) {
            viewing_buffer[write_row][write_col] = e;

            // Move the write head to the right by one character
            write_col++;
        }
    }
}
