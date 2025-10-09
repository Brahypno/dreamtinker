package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import com.google.common.collect.Multimap;
import com.sammy.malum.common.item.curiosities.armor.MalignantStrongholdArmorItem;
import com.sammy.malum.common.item.curiosities.armor.SoulHunterArmorItem;
import com.sammy.malum.common.item.curiosities.armor.SoulStainedSteelArmorItem;
import com.sammy.malum.registry.common.item.ItemRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiConsumer;

public class malum_attributes extends Modifier implements AttributesModifierHook, ToolStatsModifierHook {
    private final int tier;

    public malum_attributes(int tier) {
        this.tier = tier;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES, ModifierHooks.TOOL_STATS);
        super.registerHooks(hookBuilder);
    }

    private final SoulHunterArmorItem soulHunterArmorItem = (SoulHunterArmorItem) ItemRegistry.SOUL_HUNTER_CLOAK.get();
    private final SoulStainedSteelArmorItem soulStainedSteelArmorItem = (SoulStainedSteelArmorItem) ItemRegistry.SOUL_STAINED_STEEL_LEGGINGS.get();
    private final MalignantStrongholdArmorItem malignantStrongholdArmorItem = (MalignantStrongholdArmorItem) ItemRegistry.MALIGNANT_STRONGHOLD_BOOTS.get();

    @Override
    public void addAttributes(IToolStackView iToolStackView, ModifierEntry modifierEntry, EquipmentSlot equipmentSlot, BiConsumer<Attribute, AttributeModifier> biConsumer) {
        ArmorItem.Type type = armorTypeFromSlot(equipmentSlot);
        if (null != type)
            for (Map.Entry<Attribute, AttributeModifier> e : createExtraAttributes(type).entries()) {
                Attribute attr = e.getKey();
                AttributeModifier mod = e.getValue();
                AttributeModifier scaled = new AttributeModifier(
                        mod.getId(), mod.getName(), mod.getAmount() * modifierEntry.getLevel(), mod.getOperation()
                );
                biConsumer.accept(attr, scaled);
            }
    }

    private Multimap<Attribute, AttributeModifier> createExtraAttributes(ArmorItem.Type type) {
        switch (tier) {
            case 2 -> {return soulStainedSteelArmorItem.createExtraAttributes(type);}
            case 3 -> {return malignantStrongholdArmorItem.createExtraAttributes(type);}
        }
        return soulHunterArmorItem.createExtraAttributes(type);
    }

    @Nullable
    public static ArmorItem.Type armorTypeFromSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> ArmorItem.Type.HELMET;
            case CHEST -> ArmorItem.Type.CHESTPLATE;
            case LEGS -> ArmorItem.Type.LEGGINGS;
            case FEET -> ArmorItem.Type.BOOTS;
            default -> null; // MAINHAND / OFFHAND
        };
    }

    @Override
    public void addToolStats(IToolContext iToolContext, ModifierEntry modifierEntry, ModifierStatsBuilder modifierStatsBuilder) {
        ToolStats.ARMOR.add(modifierStatsBuilder, -0.1 * modifierStatsBuilder.getStat(ToolStats.ARMOR) * (modifierEntry.getLevel() - 1));
    }
}