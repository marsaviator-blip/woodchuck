package org.woodchuck.category_theorectics2.models;

/**
 * Models a Monoidal Category where objects can be tensor-combined.
 * @param <Obj> The type representing objects in the category.
 * @param <M>   The type representing valid morphisms between objects.
 */
public interface MonoidalCategory<Obj, M extends Morphism<Obj, Obj>> {
    
    // --- Standard Category Operations ---
    M identity(Obj object);
    M compose(M f, M g);

    // --- Monoidal Structural Additions ---
    
    /**
     * The Tensor Identity Object (I). 
     * Combining any object with this must yield an isomorphic object.
     */
    Obj tensorIdentity();

    /**
     * The Monoidal Bifunctor Product Object (A ⊗ B).
     * Combines two independent objects into a unified joint object.
     */
    Obj tensorProduct(Obj objA, Obj objB);

    /**
     * The Monoidal Bifunctor Product Morphism (f ⊗ g).
     * Combines two parallel relations into a unified joint relation.
     */
    M tensorMorphism(M morphF, M morphG);
}
