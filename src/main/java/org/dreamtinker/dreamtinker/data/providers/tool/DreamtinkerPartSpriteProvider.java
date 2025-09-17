package org.dreamtinker.dreamtinker.data.providers.tool;

import net.minecraft.world.item.ArmorItem;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import static slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider.ARMOR_MAILLE;
import static slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider.ARMOR_PLATING;

public class DreamtinkerPartSpriteProvider extends AbstractPartSpriteProvider {
    public DreamtinkerPartSpriteProvider() {
        super(Dreamtinker.MODID);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Part Sprite Provider";
    }

    @Override
    protected void addAllSpites() {
        addHead("explode_core");
        addHead("memory_orthant");
        addHandle("wish_orthant");
        addLimb("soul_orthant");
        addLimb("persona_orthant");
        addHead("reason_emanation");
        buildTool("tntarrow").addHead("explode_core").addHandle("arrow_handle").addHandle("arrow_wing");
        buildTool("masu").withLarge()
                         .addBreakableHead("masu_blade").addBreakableHead("masu_line")
                         .addBreakablePart("masu_binding", StatlessMaterialStats.BINDING.getIdentifier()).addHandle("masu_handle")
                         .addHandle("masu_end");
        buildTool("narcissus_wing").withLarge()
                                   .addHandle("memory_orthant").addHandle("wish_orthant")
                                   .addLimb("soul_orthant").addLimb("persona_orthant")
                                   .addHead("reason_emanation");
        for (ArmorItem.Type slot : ArmorItem.Type.values()) {
            buildTool("armor/under_plate/" + slot.getName()).disallowAnimated() // the armor model won't be animated, so don't animate the item
                                                            .addBreakablePart("plating", PlatingMaterialStats.TYPES.get(slot.ordinal()).getId())
                                                            .addBreakablePart("maille", StatlessMaterialStats.MAILLE.getIdentifier())
                                                            .addBreakablePart("maille1", StatlessMaterialStats.MAILLE.getIdentifier());
        }
        addTexture("tinker_armor/under_plate/plating_armor", ARMOR_PLATING).disallowAnimated();
        addTexture("tinker_armor/under_plate/plating_leggings", ARMOR_PLATING).disallowAnimated();
        addTexture("tinker_armor/under_plate/maille_armor", ARMOR_MAILLE).disallowAnimated();
        addTexture("tinker_armor/under_plate/maille_leggings", ARMOR_MAILLE).disallowAnimated();
        //addTexture("tinker_armor/plate/maille_wings", ARMOR_MAILLE).disallowAnimated();
    }
}
