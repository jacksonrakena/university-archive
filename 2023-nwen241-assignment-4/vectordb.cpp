/**
 * vectordb.cpp
 * C++ source file that should contain implementation for member functions
 * - rows()
 * - get()
 * - add()
 * - update()
 * - remove()
 * 
 * You need to modify this file to implement the above-mentioned member functions
 * as specified in the hand-out (Task 3)
 */

#include <algorithm>
#include "vectordb.hpp"
#include "abstractdb.hpp"

bool nwen::VectorDbTable::add(movie entry) {
    for (movie m : this->items) {
        if (m.id == entry.id) return false;
    }
    this->items.push_back(entry);
    return true;
}

nwen::movie *nwen::VectorDbTable::get(int index) const {
    if (index >= this->rows()) return nullptr;
    const movie* ptr = &this->items.at(index);
    return const_cast<movie *>(ptr);
}

int nwen::VectorDbTable::rows() const {
    return this->items.size();
}

bool nwen::VectorDbTable::update(unsigned long id, movie parameters) {
    bool result = false;
    for (auto i : this->items) {
        if (i.id == id) {
            result = true;
        }
    }
    std::replace_if(this->items.begin(), this->items.end(), [&](movie m) {return m.id == id;}, parameters);
    return result;
}

bool nwen::VectorDbTable::remove(unsigned long id) {
    auto return_from_remove = std::remove_if(this->items.begin(), this->items.end(), [&](movie m) {
        return m.id == id;
    });
    if (return_from_remove != this->items.end()) {
        this->items.erase(return_from_remove, this->items.end());
        return true;
    }
    return false;
}