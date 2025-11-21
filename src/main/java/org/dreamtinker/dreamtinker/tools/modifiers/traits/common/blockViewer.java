package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.utils.BlockViewerService;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class blockViewer extends Modifier implements InventoryTickModifierHook, EquipmentChangeModifierHook {
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK, ModifierHooks.EQUIPMENT_CHANGE);
    }

    private final ResourceLocation location;
    private final float rate;

    public blockViewer(ResourceLocation location, float rate) {
        this.location = location;
        this.rate = rate;
    }

    @Override
    public void onInventoryTick(IToolStackView iToolStackView, ModifierEntry modifierEntry, Level level, LivingEntity livingEntity, int i, boolean b, boolean b1, ItemStack itemStack) {
        if (!level.isClientSide && (b || b1) && livingEntity instanceof ServerPlayer player && level.getGameTime() % 20 == 0){
            if (level.random.nextFloat() < rate * modifierEntry.getLevel())
                BlockViewerService.ensureOn(player, location, modifierEntry.getLevel() * 4);
        }
    }

    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (!context.getEntity().level().isClientSide && context.getEntity() instanceof ServerPlayer player)
            BlockViewerService.ensureOff(player);
    }
}
