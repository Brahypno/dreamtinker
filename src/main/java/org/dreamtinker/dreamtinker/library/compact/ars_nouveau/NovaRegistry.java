package org.dreamtinker.dreamtinker.library.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import net.minecraft.world.item.ArmorItem;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.Spell.AugmentTinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;

import java.util.Arrays;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.lib.GlyphLib.prependGlyph;

public class NovaRegistry {
    public static final String AugmentTinkerID = prependGlyph("tinker");

    public NovaRegistry() {
        NovaInit();
    }

    public void NovaInit() {
        GlyphRegistry.registerSpell(AugmentTinker.INSTANCE);
    }

    private static final List<List<List<PerkSlot>>> small_slots = Arrays.asList(
            Arrays.asList(
                    List.of(PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.TWO)
            ),
            Arrays.asList(
                    List.of(PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.TWO)
            ),
            Arrays.asList(
                    List.of(PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
            )
    );
    private static final List<List<List<PerkSlot>>> large_slots = Arrays.asList(
            Arrays.asList(
                    List.of(PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.THREE)
            ),
            Arrays.asList(
                    List.of(PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.THREE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
            ),
            Arrays.asList(
                    List.of(PerkSlot.TWO),
                    Arrays.asList(PerkSlot.TWO, PerkSlot.THREE),
                    Arrays.asList(PerkSlot.TWO, PerkSlot.TWO, PerkSlot.THREE)
            )
    );

    public static void postInit() {
        PerkRegistry.registerPerkProvider(DreamtinkerTools.underPlate.get(ArmorItem.Type.BOOTS), stack -> new ModifiableArmorPekHolder(stack, small_slots));

        PerkRegistry.registerPerkProvider(DreamtinkerTools.underPlate.get(ArmorItem.Type.CHESTPLATE),
                                          stack -> new ModifiableArmorPekHolder(stack, large_slots));

        PerkRegistry.registerPerkProvider(DreamtinkerTools.underPlate.get(ArmorItem.Type.LEGGINGS), stack -> new ModifiableArmorPekHolder(stack, large_slots));

        PerkRegistry.registerPerkProvider(DreamtinkerTools.underPlate.get(ArmorItem.Type.HELMET), stack -> new ModifiableArmorPekHolder(stack, small_slots));
    }
}
