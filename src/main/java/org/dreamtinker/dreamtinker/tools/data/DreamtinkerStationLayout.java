package org.dreamtinker.dreamtinker.tools.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerToolParts;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractStationSlotLayoutProvider;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;

public class DreamtinkerStationLayout extends AbstractStationSlotLayoutProvider {
    public DreamtinkerStationLayout(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addLayouts() {
        defineModifiable(DreamtinkerTools.mashou)
                .sortIndex(SORT_WEAPON * 2 + SORT_LARGE)
                .addInputItem(TinkerToolParts.broadBlade, 45, 62)
                .addInputItem(TinkerToolParts.broadBlade, 45, 26)
                .addInputItem(TinkerToolParts.largePlate, 45, 46)
                .addInputItem(TinkerToolParts.toughHandle, 7, 62)
                .addInputItem(TinkerToolParts.toughHandle, 25, 46)
                .build();
        defineModifiable(DreamtinkerTools.tntarrow)
                .sortIndex(SORT_WEAPON + SORT_RANGED)
                .addInputItem(DreamtinkerToolParts.explode_core.get(), 48, 26)
                .addInputItem(TinkerToolParts.toolHandle, 12, 62)
                .addInputItem(TinkerToolParts.toughHandle, 30, 44)
                .build();
        defineModifiable(DreamtinkerTools.chain_saw_blade)
                .sortIndex(SORT_WEAPON + SORT_LARGE)
                .addInputItem(TinkerToolParts.broadBlade, 45, 26)
                .addInputItem(DreamtinkerToolParts.chainSawTeeth.get(), 7, 62)
                .addInputItem(DreamtinkerToolParts.chainSawCore.get(), 25, 46)
                .addInputItem(TinkerToolParts.toughHandle, 45, 46)
                .build();
        define(Dreamtinker.getLocation("under_plate"))
                .sortIndex(SORT_ARMOR * 2)
                .translationKey(Dreamtinker.makeTranslationKey("item", "under_plate"))
                .icon(new Pattern("dreamtinker:under_plate"))
                .addInputPattern(Patterns.PLATING, 33, 29, Ingredient.of(TinkerToolParts.plating.values().toArray(new Item[0])))
                .addInputItem(TinkerToolParts.maille, 13, 53)
                .addInputItem(TinkerToolParts.maille, 53, 53)
                .addInputItem(new Pattern("dreamtinker:silky_cloth"), TinkerModifiers.silkyCloth, 13, 29)
                .addInputItem(new Pattern("dreamtinker:silky_cloth"), TinkerModifiers.silkyCloth, 33, 53)
                .build();
        defineModifiable(DreamtinkerTools.narcissus_wing)
                .sortIndex(SORT_WEAPON + SORT_RANGED + SORT_LARGE)
                .addInputItem(DreamtinkerToolParts.memoryOrthant.get(), 7, 26)
                .addInputItem(DreamtinkerToolParts.wishOrthant.get(), 25, 26)
                .addInputItem(DreamtinkerToolParts.soulOrthant.get(), 45, 26)
                .addInputItem(DreamtinkerToolParts.personaOrthant.get(), 25, 62)
                .addInputItem(DreamtinkerToolParts.reasonEmanation.get(), 25, 46)
                .build();

    }

    @Override
    public @NotNull String getName() {
        return "DreamTinker Station Slot Layouts";
    }

}
