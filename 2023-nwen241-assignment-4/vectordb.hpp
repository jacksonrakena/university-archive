/**
 * vectordb.hpp
 * C++ header file that should contain declaration for
 * - VectorDbTable class
 * 
 * You need to modify this file to declare VectorDbTable class 
 * as specified in the hand-out (Task 2)
 */

#include <vector>
#include "abstractdb.hpp"

namespace nwen {
    class VectorDbTable : public AbstractDbTable {
    public:
        int rows() const override;
        movie* get(int index) const override;
        bool add(movie entry) override;
        bool update(unsigned long id, movie parameters) override;
        bool remove(unsigned long id) override;
    private:
        std::vector<movie> items;
    };
}