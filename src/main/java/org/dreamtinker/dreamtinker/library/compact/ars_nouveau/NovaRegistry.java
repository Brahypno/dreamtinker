package org.dreamtinker.dreamtinker.library.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.Spell.AugmentTinker;

import static com.hollingsworth.arsnouveau.common.lib.GlyphLib.prependGlyph;

public class NovaRegistry {
    public static final String AugmentTinkerID = prependGlyph("tinker");

    public NovaRegistry() {
        NovaInit();
    }

    public void NovaInit() {
        GlyphRegistry.registerSpell(AugmentTinker.INSTANCE);
    }
}
