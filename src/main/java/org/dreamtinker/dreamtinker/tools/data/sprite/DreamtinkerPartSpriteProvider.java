package org.dreamtinker.dreamtinker.tools.data.sprite;

import net.minecraft.world.item.ArmorItem;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
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
        addHead("chain_saw_core");
        addHandle("chain_saw_teeth");
        addLimb("nova_cover");
        addHead("nova_rostrum");
        addLimb("nova_wrapper");
        addHandle("nova_misc");

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
        buildTool("chain_saw_blade").withLarge()
                                    .addBreakableHead("teeth").addHead("head").addHandle("core").addBreakablePart("handle", HandleMaterialStats.ID);
        buildTool("per_aspera_scriptum").addHandle("nova_misc").addLimb("nova_wrapper").addHead("nova_rostrum").addLimb("nova_cover");

        for (ArmorItem.Type slot : ArmorItem.Type.values()) {//have to like this since armor dont like partial breakable one
            if (slot != ArmorItem.Type.HELMET)
                buildTool("armor/under_plate/" + slot.getName()).disallowAnimated() // the armor model won't be animated, so don't animate the item
                                                                .addBreakablePart("plating", PlatingMaterialStats.TYPES.get(slot.ordinal()).getId())
                                                                .addBreakablePart("maille", StatlessMaterialStats.MAILLE.getIdentifier())
                                                                .addBreakablePart("maille1", StatlessMaterialStats.MAILLE.getIdentifier());
        }
        buildTool("armor/under_plate/helmet").disallowAnimated()
                                             .addPart("plating",
                                                      PlatingMaterialStats.TYPES.get(ArmorItem.Type.HELMET.ordinal())
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
