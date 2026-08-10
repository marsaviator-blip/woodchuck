#include "engine.hpp"
#include <string_view>
#include <iostream>

extern "C" {
    bool validate_composition(const CppMorphism* f, const CppMorphism* g) {
        // Guard against invalid null pointer inputs from the web runtime
        if (!f || !g) return false;

        std::string_view f_target(f->targetId);
        std::string_view g_source(g->sourceId);

        // Core Categorical Constraint: Target of the first arrow must equal Source of the second arrow
        bool is_valid = (f_target == g_source);

        if (is_valid) {
            std::cout << "Valid mathematical composition detected: (" 
                      << f->type << " ∘ " << g->type << ")" << std::endl;
        } else {
            std::cerr << "Category Law Violation! Cannot compose target '" 
                      << f_target << "' with source '" << g_source << "'." << std::endl;
        }

        return is_valid;
    }
}
