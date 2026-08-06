package org.woodchuck.category_theorectics2.models;

/**
 * Models a basic structural Morphism (arrow) in Category Theory.
 * 
 * @param <Dom> The domain (source) object type.
 * @param <Cod> The codomain (target) object type.
 */
public interface Morphism<Dom, Cod> {
    
    /**
     * The source object where the morphism originates.
     */
    Dom domain();

    /**
     * The target object where the morphism terminates.
     */
    Cod codomain();
}
