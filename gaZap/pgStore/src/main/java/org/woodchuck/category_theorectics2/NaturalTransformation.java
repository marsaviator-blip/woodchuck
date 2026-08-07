package org.woodchuck.category_theorectics2.models;

import org.woodchuck.category_theorectics2.models.Morphism;

/**
 * Models a Natural Transformation α : F -> G between two Functors.
 *
 * @param <I_Obj>   Informal Category Object (Domain)
 * @param <F_Obj>   Formal Category Object (Codomain)
 * @param <F_Morph> Formal Category Morphism
 */
@FunctionalInterface
public interface NaturalTransformation<I_Obj, F_Obj, F_Morph extends Morphism<F_Obj, F_Obj>> {

    /**
     * Component of the natural transformation at object A.
     * Evaluates the mapping arrow α_A : F(A) -> G(A).
     */
    F_Morph componentAt(I_Obj informalObject);
}
