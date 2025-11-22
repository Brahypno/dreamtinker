package org.dreamtinker.dreamtinker.tools.data.sprite;

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
        buildTool("silence_glove").addHead("glove_hardware").addBinding("glove_wristband")
                                  .addPart("glove_leather", StatlessMaterialStats.CUIRASS.getIdentifier());
        buildTool("mashou").withLarge()
                           .addBreakableHead("mashou_blade").addBreakableHead("mashou_line")
                           .addBreakablePart("mashou_binding", StatlessMaterialStats.BINDING.getIdentifier()).addHandle("mashou_handle")
                           .addHandle("mashou_end");
        buildTool("narcissus_wing").withLarge()
                                   .addHandle("memory_orthant").addHandle("wish_orthant").addHandle("wish_orthant_1")
                                   .addLimb("soul_orthant").addLimb("persona_orthant")
                                   .addHead("reason_emanation");

        buildTool("armor/under_plate/chestplate").disallowAnimated() // the armor model won't be animated, so don't animate the item
                                                 .addPart("plating", PlatingMaterialStats.TYPES.get(ArmorItem.Type.CHESTPLATE.ordinal()).getId())
                                                 .addBreakablePart("maille", StatlessMaterialStats.MAILLE.getIdentifier())
                                                 .addBreakablePart("maille1", StatlessMaterialStats.MAILLE.getIdentifier());

        buildTool("armor/under_plate/boots").disallowAnimated()
                                            .addBreakablePart("plating",
                                                              PlatingMaterialStats.TYPES.get(ArmorItem.Type.BOOTS.ordinal())
                                                                                        .getId())
                                            .addBreakablePart("maille", StatlessMaterialStats.MAILLE.getIdentifier())
                                            .addPart("maille1", StatlessMaterialStats.MAILLE.getIdentifier());
        buildTool("armor/under_plate/helmet").disallowAnimated()
                                             .addPart("plating",
                                                      PlatingMaterialStats.TYPES.get(ArmorItem.Type.HELMET.ordinal())
                                                                                .getId())
                                             .addPart("maille", StatlessMaterialStats.MAILLE.getIdentifier())
                                             .addPart("maille1", StatlessMaterialStats.MAILLE.getIdentifier());
        buildTool("armor/under_plate/leggings").disallowAnimated()
                                               .addBreakablePart("plating",
                                                                 PlatingMaterialStats.TYPES.get(ArmorItem.Type.LEGGINGS.ordinal())
                                                                                           .getId())
                                               .addPart("maille", StatlessMaterialStats.MAILLE.getIdentifier())
                                               .addPart("maille1", StatlessMaterialStats.MAILLE.getIdentifier());

        addTexture("tinker_armor/under_plate/plating_armor", ARMOR_PLATING).disallowAnimated();
        addTexture("tinker_armor/under_plate/plating_leggings", ARMOR_PLATING).disallowAnimated();
        addTexture("tinker_armor/under_plate/maille_armor", ARMOR_MAILLE).disallowAnimated();
        addTexture("tinker_armor/under_plate/maille_leggings", ARMOR_MAILLE).disallowAnimated();
        addTexture("tinker_armor/under_plate/maille_wings", ARMOR_MAILLE).disallowAnimated();
    }
}
