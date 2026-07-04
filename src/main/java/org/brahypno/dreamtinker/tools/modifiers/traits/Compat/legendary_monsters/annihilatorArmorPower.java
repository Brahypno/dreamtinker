package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.legendary_monsters;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierRequirementsModule;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.data.ModifierIds;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;

public class annihilatorArmorPower extends NoLevelsModifier implements InventoryTickModifierHook {
    private static final Map<EquipmentSlot, ResourceLocation> IDS = new EnumMap<>(EquipmentSlot.class);
    private static final Map<EquipmentSlot, Item> ITEMS = new EnumMap<>(EquipmentSlot.class);
    private static Method inventoryTick;
    private static Method onArmorTick;
    private static boolean resolved;

    static {
        IDS.put(EquipmentSlot.CHEST, new ResourceLocation("legendary_monsters", "annihilator_chestplate"));
        IDS.put(EquipmentSlot.FEET, new ResourceLocation("legendary_monsters", "annihilator_boots"));
    }

    public static void tickAnnihilatorArmorLike(EquipmentSlot slot, ItemStack stack, Level level, Entity entity, int slotId, boolean selected) {
        resolve();

        Item item = ITEMS.get(slot);
        if (item == null)
            return;

        try {
            if (inventoryTick != null)
                inventoryTick.invoke(item, stack, level, entity, slotId, selected);

            if (slot == EquipmentSlot.CHEST && onArmorTick != null && entity instanceof Player player)
                onArmorTick.invoke(item, stack, level, player);
        }
        catch (Throwable ignored) {}
    }

    private static void resolve() {
        if (resolved)
            return;
        resolved = true;

        try {
            Class<?> clazz = Class.forName("net.miauczel.legendary_monsters.item.custom.customArmor.armorItem.AnnihilatorArmorItem");

            inventoryTick = clazz.getMethod("inventoryTick", ItemStack.class, Level.class, Entity.class, int.class, boolean.class);
            inventoryTick.setAccessible(true);

            onArmorTick = clazz.getMethod("onArmorTick", ItemStack.class, Level.class, Player.class);
            onArmorTick.setAccessible(true);

            for (Map.Entry<EquipmentSlot, ResourceLocation> entry : IDS.entrySet()) {
                Item item = ForgeRegistries.ITEMS.getValue(entry.getValue());
                if (item != null && clazz.isInstance(item))
                    ITEMS.put(entry.getKey(), item);
            }
        }
        catch (Throwable ignored) {
            ITEMS.clear();
            inventoryTick = null;
            onArmorTick = null;
        }
    }

    private static EquipmentSlot getEquippedSlot(LivingEntity holder, ItemStack stack) {
        if (holder.getItemBySlot(EquipmentSlot.CHEST) == stack)
            return EquipmentSlot.CHEST;
        if (holder.getItemBySlot(EquipmentSlot.FEET) == stack)
            return EquipmentSlot.FEET;
        return null;
    }

    private static int armorSlotIndex(EquipmentSlot slot) {
        return switch (slot) {
            case FEET -> 0;
            case CHEST -> 2;
            default -> -1;
        };
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        hookBuilder.addModule(StatBoostModule.add(ToolStats.ARMOR).flat(3));
        hookBuilder.addModule(StatBoostModule.add(ToolStats.ARMOR_TOUGHNESS).flat(1));
        hookBuilder.addModule(StatBoostModule.add(ToolStats.KNOCKBACK_RESISTANCE).flat(1f));
        hookBuilder.addModule(ModifierRequirementsModule.builder().requireModifier(ModifierIds.netherite, 1)
                                                        .modifierKey(DreamtinkerModifiers.annihilator_armor_power.getId()).build());
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!isCorrectSlot)
            return;

        EquipmentSlot slot = getEquippedSlot(holder, stack);
        if (slot != EquipmentSlot.CHEST && slot != EquipmentSlot.FEET)
            return;

        tickAnnihilatorArmorLike(slot, stack, world, holder, armorSlotIndex(slot), isSelected);
    }
}