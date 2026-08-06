package org.woodchuck.category_theorectics2.models;

public record InteractionMorphism(
    InteractionObject domain,
    InteractionObject codomain,
    String label
) implements Morphism<InteractionObject, InteractionObject> {}

