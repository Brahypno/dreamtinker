package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class not_like_was extends BattleModifier {
    private final ResourceLocation TAG_CHANGE_TIMES = Dreamtinker.getLocation("not_like_was_changing");
    private final ResourceLocation TAG_material_lists = Dreamtinker.getLocation("not_like_was_material_lists");

    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        float value = 0.05f + context.getPersistentData().getInt(TAG_CHANGE_TIMES) * 0.01f;
        float armor_value = 0.1f + context.getPersistentData().getInt(TAG_CHANGE_TIMES) * 0.01f;
        float range_value = 0.02f + context.getPersistentData().getInt(TAG_CHANGE_TIMES) * 0.01f;
        if (20 < context.getPersistentData().getInt(TAG_CHANGE_TIMES))
            ToolStats.HARVEST_TIER.update(builder, Tiers.NETHERITE);
        else if (10 < context.getPersistentData().getInt(TAG_CHANGE_TIMES))
            ToolStats.HARVEST_TIER.update(builder, Tiers.DIAMOND);
        ToolStats.ATTACK_DAMAGE.multiply(builder, value);
        ToolStats.ATTACK_SPEED.multiply(builder, value);
        ToolStats.MINING_SPEED.multiply(builder, value / 5);
        ToolStats.ARMOR.multiply(builder, armor_value);
        ToolStats.ARMOR_TOUGHNESS.multiply(builder, armor_value);
        ToolStats.KNOCKBACK_RESISTANCE.multiply(builder, value);
        ToolStats.DRAW_SPEED.multiply(builder, range_value);
        ToolStats.VELOCITY.multiply(builder, range_value);
        ToolStats.ACCURACY.multiply(builder, range_value);
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && world.getGameTime() % 20 == 0){
            String material_lists = tool.getMaterials().toString();
            ModDataNBT data = tool.getPersistentData();
            if (!data.contains(TAG_material_lists)){
                data.putString(TAG_material_lists, material_lists);
                return;
            }
            String stored_list = data.getString(TAG_material_lists);
            int times = data.getInt(TAG_CHANGE_TIMES);
            if (!stored_list.equals(material_lists)){
                data.putString(TAG_material_lists, material_lists);
                data.putInt(TAG_CHANGE_TIMES, ++times);
            }
        }
    }
}
