package org.brahypno.dreamtinker.tools.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorItem;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.client.modifiers.model.TankModifierModel;
import slimeknights.tconstruct.library.data.AbstractModifierModelMapProvider;
import slimeknights.tconstruct.tools.data.ModifierIds;

/**
 * Modifier overlays shared by the normal, blocking, broken, and pulling tool models.
 */
public class DreamtinkerModifierModelMapProvider extends AbstractModifierModelMapProvider {
    public DreamtinkerModifierModelMapProvider(PackOutput output) {
        super(output, Dreamtinker.MODID);
    }

    @Override
    protected void addModels() {
        tool(DreamtinkerTools.mashou).large('/')
                                     .basic(ModifierIds.reinforced, ModifierIds.emerald, ModifierIds.diamond, ModifierIds.netherite,
                                            ModifierIds.overforced, ModifierIds.blasting, ModifierIds.hydraulic, ModifierIds.knockback,
                                            ModifierIds.experienced, ModifierIds.magnetic, ModifierIds.antiaquatic, ModifierIds.baneOfSssss,
                                            ModifierIds.cooling, ModifierIds.smite, ModifierIds.sharpness, ModifierIds.luck, ModifierIds.silky)
                                     .luminosity(7, ModifierIds.haste)
                                     .luminosity(15, ModifierIds.lightspeed)
                                     .luminosity(10, ModifierIds.fiery)
                                     .luminosity(2, ModifierIds.unbreakable)
                                     .tank()
                                     .fluid(ModifierIds.bucketing);

        Builder chainSaw = tool(DreamtinkerTools.chain_saw_blade).large('/')
                                                                 .basic(ModifierIds.emerald, ModifierIds.diamond, ModifierIds.netherite, ModifierIds.overforced,
                                                                        ModifierIds.blasting, ModifierIds.hydraulic, ModifierIds.experienced,
                                                                        ModifierIds.antiaquatic,
                                                                        ModifierIds.baneOfSssss, ModifierIds.cooling, ModifierIds.sharpness, ModifierIds.luck)
                                                                 .luminosity(7, ModifierIds.haste)
                                                                 .luminosity(15, ModifierIds.lightspeed)
                                                                 .luminosity(10, ModifierIds.fiery);

        // The built-in tank is visible even without a crafted tank upgrade, before other overlays.
        String tank = "chain_saw_blade/modifiers/tconstruct_tank";
        String largeTank = "chain_saw_blade/large/modifiers/tconstruct_tank";
        chainSaw.constant("_tank",
                          new TankModifierModel(toolMaterial(tank + "_partial"), toolMaterial(tank + "_full"),
                                                toolMaterial(largeTank + "_partial"), toolMaterial(largeTank + "_full"), 0),
                          new NormalModifierModel(toolMaterial(tank), toolMaterial(largeTank)));

        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            tool(DreamtinkerTools.underPlate.get(type))
                    .smallFolder(TConstruct.getResource("armor/plate/" + type.getName() + "/modifiers/"))
                    .basic(ModifierIds.diamond, ModifierIds.netherite)
                    .trim(type);
        }
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Modifier Model Maps";
    }
}
