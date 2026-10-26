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
import net.minecraftforge.fml.ModList;
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

import java.util.EnumMap;
import java.util.Map;

public class annihilatorArmorPower extends NoLevelsModifier implements InventoryTickModifierHook {
    private static final Map<EquipmentSlot, ResourceLocation> IDS = new EnumMap<>(EquipmentSlot.class);
    private static final Map<EquipmentSlot, Item> ITEMS = new EnumMap<>(EquipmentSlot.class);
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

        item.inventoryTick(stack, level, entity, slotId, selected);
        if (slot == EquipmentSlot.CHEST && entity instanceof Player player)
            item.onArmorTick(stack, level, player);
    }

    private static void resolve() {
        if (resolved)
            return;
        resolved = true;

        if (!ModList.get().isLoaded("legendary_monsters"))
            return;
        for (Map.Entry<EquipmentSlot, ResourceLocation> entry : IDS.entrySet()) {
            Item item = ForgeRegistries.ITEMS.getValue(entry.getValue());
            if (item != null)
                ITEMS.put(entry.getKey(), item);
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
