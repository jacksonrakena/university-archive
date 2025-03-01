/**
 * abstractdb.cpp
 * C++ source file that should contain implementation for member functions
 * - loadCSV()
 * - saveCSV()
 * 
 * You need to modify this file to implement the above-mentioned member functions
 * as specified in the hand-out (Tasks 4 and 5)
 */

#include <fstream>
#include "abstractdb.hpp"
#include <cstring>
#include <sstream>
#include <limits>

bool nwen::AbstractDbTable::loadCSV(const string& filename) {
    ifstream input;
    input.open(filename.c_str());
    if (input.fail()) return false;
    string buffer;
    while (getline(input, buffer)) {
        movie entry{};
        std::stringstream stream(buffer);
        int phase = 0;
        while (stream.good()) {
            string bit;

            getline(stream, bit, ',');
            switch (phase) {
                case 0:
                    entry.id = stoi(bit);
                    break;
                case 1:
                    strcpy(entry.title, bit.c_str());
                    break;
                case 2:
                    if (stoi(bit) < std::numeric_limits<short>::lowest() || stoi(bit) > std::numeric_limits<short>::max()) return false;
                    entry.year = (short) stoi(bit);
                    break;
                case 3:
                    strcpy(entry.director, bit.c_str());
                    break;
                default:
                    return false;
            }
            phase++;
        }
        if (phase != 4) return false;
        this->add(entry);
    }
    input.close();
    return true;
}

bool nwen::AbstractDbTable::saveCSV(const string& filename) {
    ofstream output;
    output.open(filename);
    if (output.fail()) return false;
    for (int i = 0; i < this->rows(); i++) {
        movie* row = this->get(i);
        output << row->id << ",\"" << row->title << "\"," << row->year << ",\"" << row->director << "\"" << std::endl;
    }
    output.close();
    return true;
}