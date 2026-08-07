package org.woodchuck.category_theorectics2.models;

import java.time.Instant;
import java.util.UUID;

public class InteractionMonoidalCategory implements MonoidalCategory<InteractionObject, InteractionMorphism> {

    @Override
    public InteractionMorphism identity(InteractionObject object) {
        return new InteractionMorphism(object, object, "IDENTITY_" + object.type());
    }

    @Override
    public InteractionMorphism compose(InteractionMorphism f, InteractionMorphism g) {
        if (!f.codomain().id().equals(g.domain().id())) {
            throw new IllegalArgumentException("Morphisms fail to commute: Codomain/Domain structural mismatch.");
        }
        return new InteractionMorphism(f.domain(), g.codomain(), f.label() + " ∘ " + g.label());
    }

    @Override
    public InteractionObject tensorIdentity() {
        return new InteractionObject("IDENTITY_I", InteractionType.IDENTITY_I, "", Instant.EPOCH);
    }

    @Override
    public InteractionObject tensorProduct(InteractionObject objA, InteractionObject objB) {
        if (objA.type() == InteractionType.IDENTITY_I) return objB;
        if (objB.type() == InteractionType.IDENTITY_I) return objA;

        String combinedId = UUID.randomUUID().toString();
        // Synthesizes human prompt and AI response logs into a single tensor block
        String combinedContent = String.format("[%s]: %s\n⊗\n[%s]: %s", 
            objA.type(), objA.content(), objB.type(), objB.content());
        
        return new InteractionObject(combinedId, InteractionType.NOTE, combinedContent, Instant.now());
    }

    @Override
    public InteractionMorphism tensorMorphism(InteractionMorphism morphF, InteractionMorphism morphG) {
        InteractionObject combinedDomain = tensorProduct(morphF.domain(), morphG.domain());
        InteractionObject combinedCodomain = tensorProduct(morphF.codomain(), morphG.codomain());
        String combinedLabel = morphF.label() + " ⊗ " + morphG.label();
        
        return new InteractionMorphism(combinedDomain, combinedCodomain, combinedLabel);
    }
}
