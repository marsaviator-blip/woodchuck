#pragma once
#include <string>

// Enforce standard C-linkage so Bun can map the binary symbols directly
extern "C" {
    // Structural representation of a Category Theory Morphism arrow (f: A -> B)
    struct CppMorphism {
        const char* id;
        const char* sourceId;
        const char* targetId;
        const char* type;
    };

    /**
     * Category Theory Law Check: Composition Validation
     * Verifies if Morphism F (A -> B) can validly compose with Morphism G (B -> C).
     * Returns true if F's target perfectly matches G's source.
     */
    bool validate_composition(const CppMorphism* f, const CppMorphism* g);
}
