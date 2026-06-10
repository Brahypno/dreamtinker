package org.brahypno.dreamtinker.tools.modifiers.traits.material.livingSoulSteel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.brahypno.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class AdaptionAlgorithm extends NoLevelsModifier implements ToolDamageModifierHook, InventoryTickModifierHook {
    private static final ResourceLocation TAG_ADAPTION = new ResourceLocation(Dreamtinker.MODID, "adaption_algorithm");
    private static final int MAX_STAGE = 20;
    private static final int DAMAGE_PER_STAGE = 100;
    private static final int MAX_PROGRESS = MAX_STAGE * DAMAGE_PER_STAGE;
    private static final int START_DAMAGE_OFFSET = 10;
    private static final int REPAIR_AMOUNT = 1;

    private static int getDamageOffset(int progress) {
        return START_DAMAGE_OFFSET - getStage(progress);
    }

    private static int getStage(int progress) {
        return Math.min(MAX_STAGE, Math.max(0, progress) / DAMAGE_PER_STAGE);
    }

    private static boolean shouldRepairThisTick(Level world, int stage) {
        if (stage >= MAX_STAGE){
            return true;
        }
        int interval = Math.max(1, MAX_STAGE - stage);
        return world.getGameTime() % interval == 0;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE, ModifierHooks.INVENTORY_TICK);
        super.registerHooks(hookBuilder);
    }

    @Override
    public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
        ModDataNBT data = tool.getPersistentData();
        int progress = data.getInt(TAG_ADAPTION);
        if (progress < MAX_PROGRESS && amount > 0){
            progress = Math.min(MAX_PROGRESS, progress + amount);
            data.putInt(TAG_ADAPTION, progress);
        }
        return Math.max(0, amount + getDamageOffset(progress));
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide || tool.getDamage() <= 0){
            return;
        }
        int stage = getStage(tool.getPersistentData().getInt(TAG_ADAPTION));
        if (!shouldRepairThisTick(world, stage)){
            return;
        }
        tool.setDamage(Math.max(0, tool.getDamage() - REPAIR_AMOUNT));
    }
}
