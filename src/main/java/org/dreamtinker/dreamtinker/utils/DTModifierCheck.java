package org.dreamtinker.dreamtinker.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.utils.CompactUtils.CuriosCompact;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DTModifierCheck {
    public static final EquipmentSlot[] slots =
            new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND};

    public static int getModifierLevel(@NotNull LivingEntity entity, ModifierId id, EquipmentSlot slot) {
        if (null == entity.getItemBySlot(slot))
            return 0;
        ItemStack stack = entity.getItemBySlot(slot);
        return stack.getItem() instanceof IModifiable ? ModifierUtil.getModifierLevel(entity.getItemBySlot(slot), id) : 0;
    }

    public static int getMainhandModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.MAINHAND);

    }

    public static int getOffhandModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.OFFHAND);
    }

    public static boolean ModifierInHand(LivingEntity entity, ModifierId modifierId) {
        return 0 < getMainhandModifierLevel(entity, modifierId) || 0 < getOffhandModifierLevel(entity, modifierId);
    }

    public static int getHeadModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.HEAD);
    }

    public static int getChestModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.CHEST);
    }

    public static int getLegModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.LEGS);
    }

    public static int getFeetModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.FEET);
    }

    public static boolean ModifierInBody(LivingEntity entity, ModifierId modifierId) {
        return 0 < getHeadModifierLevel(entity, modifierId) || 0 < getChestModifierLevel(entity, modifierId) ||
               0 < getLegModifierLevel(entity, modifierId) || 0 < getFeetModifierLevel(entity, modifierId);
    }

    public static boolean ModifierALLBody(LivingEntity entity, ModifierId modifierId) {
        return 0 < getHeadModifierLevel(entity, modifierId) && 0 < getChestModifierLevel(entity, modifierId) &&
               0 < getLegModifierLevel(entity, modifierId) && 0 < getFeetModifierLevel(entity, modifierId);
    }

    public static boolean haveModifierIn(LivingEntity entity, ModifierId modifierId) {
        return ModifierInBody(entity, modifierId) || ModifierInHand(entity, modifierId) ||
               entity instanceof Player player && ItemStack.EMPTY != CuriosCompact.findFirstItemWithModifier(player, modifierId);
    }


    @Nullable
    public static ToolStack getToolWithModifier(LivingEntity entity, ModifierId modifierId) {
        for (EquipmentSlot slot : slots)
            if (0 < getModifierLevel(entity, modifierId, slot))
                return ToolStack.from(entity.getItemBySlot(slot));

        return null;
    }

    @Nullable
    public static ToolStack getPossibleToolWithModifier(LivingEntity entity, ModifierId modifierId) {
        for (EquipmentSlot slot : slots)
            if (0 < getModifierLevel(entity, modifierId, slot))
                return ToolStack.from(entity.getItemBySlot(slot));
        if (entity instanceof Player player)
            for (ItemStack item : player.getInventory().items)
                if (0 < getItemModifierNum(item, modifierId))
                    return ToolStack.from(item);

        return null;
    }

    @Nullable
    public static ToolStack getPossibleToolWithModifierTag(LivingEntity entity, ModifierId modifierId, ResourceLocation flag) {
        for (EquipmentSlot slot : slots)
            if (0 < getModifierLevel(entity, modifierId, slot) && 0 < ModifierUtil.getPersistentInt(entity.getItemBySlot(slot), flag, 0))
                return ToolStack.from(entity.getItemBySlot(slot));
        if (entity instanceof Player player)
            for (ItemStack itemStack : player.getInventory().items)
                if (0 < getItemModifierNum(itemStack, modifierId) && 0 < ModifierUtil.getPersistentInt(itemStack, flag, 0))
                    return ToolStack.from(itemStack);

        return null;
    }

    public static int getItemModifierNum(ItemStack stack, TagKey<Modifier> tag) {
        int matched = 0;
        if (null != stack && !stack.isEmpty() && stack.getItem() instanceof IModifiable){
            ToolStack toolStack = ToolStack.from(stack);
            for (ModifierEntry modifier : toolStack.getModifiers()) {
                matched += modifier.getModifier().is(tag) ? 1 : 0;
            }
        }
        return matched;
    }

    public static int getItemModifierNum(ItemStack stack, ModifierId id) {
        int matched = 0;
        if (null != stack && !stack.isEmpty() && stack.getItem() instanceof IModifiable){
            matched += ModifierUtil.getModifierLevel(stack, id);
        }
        return matched;
    }

    public static int getEntityBodyModifierNum(LivingEntity entity, ModifierId id) {
        int matched = 0;
        matched += getHeadModifierLevel(entity, id);
        matched += getChestModifierLevel(entity, id);
        matched += getLegModifierLevel(entity, id);
        matched += getFeetModifierLevel(entity, id);
        return matched;
    }

    public static int getEntityModifierNum(LivingEntity entity, ModifierId id) {
        int matched = 0;
        matched += getEntityBodyModifierNum(entity, id);
        matched += getMainhandModifierLevel(entity, id);
        matched += getOffhandModifierLevel(entity, id);
        return matched;
    }

    public static float getPersistentTagValue(LivingEntity entity, ModifierId modifierId, ResourceLocation tag) {
        float value = 0;
        for (EquipmentSlot slot : slots) {
            value += getPersistentTagValue(entity, modifierId, tag, slot);
        }
        return value;
    }

    public static float getPersistentTagValue(LivingEntity entity, ModifierId modifierId, ResourceLocation tag, EquipmentSlot slot) {
        float value = 0;
        int level = getModifierLevel(entity, modifierId, slot);
        if (0 < level)
            value += ModifierUtil.getPersistentInt(entity.getItemBySlot(slot), tag, 0) * level;

        return value;
    }

    public static void resetPersistentTagValue(LivingEntity entity, ResourceLocation tag) {
        for (EquipmentSlot slot : slots) {
            ItemStack itemStack = entity.getItemBySlot(slot);
            if (!(itemStack.getItem() instanceof IModifiable))
                continue;
            CompoundTag nbt = itemStack.getTag();
            if (nbt != null && nbt.contains(ToolStack.TAG_PERSISTENT_MOD_DATA, Tag.TAG_COMPOUND)){
                CompoundTag persistent = nbt.getCompound(ToolStack.TAG_PERSISTENT_MOD_DATA);
                String flagString = tag.toString();
                if (persistent.contains(flagString)){
                    persistent.remove(flagString);
                }
            }
        }
    }

    public static float getMeleeDamage(LivingEntity attacker, Entity entity, IToolStackView toolStack, boolean withCritical) {
        ToolAttackContext context = ToolAttackContext.attacker((LivingEntity) attacker).target(entity).cooldown(1).toolAttributes(toolStack)
                                                     .build();
        float baseDamage = context.getBaseDamage();
        float damage = baseDamage;
        List<ModifierEntry> modifiers = toolStack.getModifierList();

        for (ModifierEntry entry : modifiers) {
            damage = (entry.getHook(ModifierHooks.MELEE_DAMAGE)).getMeleeDamage(toolStack, entry, context, baseDamage, damage);
        }
        if (withCritical){
            float criticalModifier = context.getCriticalModifier();
            if (criticalModifier != 1){
                damage += baseDamage * (criticalModifier - 1);
            }
        }
        return damage;
    }

    public static float modifyDamageTakenInventory(ModuleHook<ModifyDamageModifierHook> hook, EquipmentContext context, DamageSource source, float amount, boolean isDirectDamage, EquipmentSlot slotType, IToolStackView toolStack) {
        if (toolStack != null && !toolStack.isBroken()){
            for (ModifierEntry entry : toolStack.getModifierList()) {
                if (entry.getModifier().is(DreamtinkerTagKeys.Modifiers.ArmorWorkingWhenUnequipped)){
                    amount = entry.getHook(hook).modifyDamageTaken(toolStack, entry, context, slotType, source, amount, isDirectDamage);
                    if (amount < 0){
                        return 0;
                    }
                }
            }
        }
        return amount;
    }

    public static EquipmentSlot toSlot(ItemStack stack) {
        if (stack.is(TinkerTags.Items.HELMETS))
            return EquipmentSlot.HEAD;
        else if (stack.is(TinkerTags.Items.CHESTPLATES))
            return EquipmentSlot.CHEST;
        else if (stack.is(TinkerTags.Items.LEGGINGS))
            return EquipmentSlot.LEGS;
        else if (stack.is(TinkerTags.Items.BOOTS))
            return EquipmentSlot.FEET;
        else if (stack.is(TinkerTags.Items.HELD_ARMOR))
            return EquipmentSlot.MAINHAND;
        else
            return EquipmentSlot.OFFHAND;

    }

    public static boolean verifyIfOffArmor(@NotNull IToolStackView tool, EquipmentContext context) {
        for (EquipmentSlot slot : slots) {
            if (tool.equals(context.getToolInSlot(slot)))
                return false;
        }
        return true;
    }

    public static boolean getExpectedMaterialPart(ItemStack stack, ResourceLocation resourceLocation) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Material", Tag.TAG_STRING)){
            String material = tag.getString("Material");
            String targetId = resourceLocation.toString();
            return material.equals(targetId);
        }
        return false;
    }

    public static int getExpectedMaterial(ItemStack stack, ResourceLocation resourceLocation) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(ToolStack.TAG_MATERIALS, Tag.TAG_LIST)){
            var list = tag.getList(ToolStack.TAG_MATERIALS, Tag.TAG_STRING);
            String targetId = resourceLocation.toString();
            for (int i = 0; i < list.size(); i++) {
                if (list.getString(i).equals(targetId)){
                    return i;
                }
            }
            return -1;
        }
        return -1;
    }

    private static final ConcurrentHashMap<Integer, List<MaterialVariantId>> MATERIALS_FOR_TIER_CACHE = new ConcurrentHashMap<>();

    public static MaterialVariantId getMaterialForTier(int tier, RandomSource rand, MaterialStatsId statsId) {
        int clampedTier = Math.max(0, tier);
        List<MaterialVariantId> candidates =
                MATERIALS_FOR_TIER_CACHE.computeIfAbsent(clampedTier, DTModifierCheck::computeMaterialsForTier).stream()
                                        .filter(m -> statsId.canUseMaterial(m.getId())).toList();
        return candidates.get(rand.nextInt(candidates.size()));
    }

    private static List<MaterialVariantId> computeMaterialsForTier(int tier) {

        // Best-effort: gather materials with exact tier. Prefer tconstruct namespace for predictability.
        var registry = MaterialRegistry.getInstance();
        List<MaterialVariantId> allChoices = registry.getAllMaterials().stream()
                                                     .filter(m -> m.getTier() == tier)
                                                     .map(IMaterial::getIdentifier)
                                                     .collect(Collectors.toList());
        if (!allChoices.isEmpty())
            return allChoices;
        // Last resort: never empty/null.
        return List.of(MaterialIds.wood);
    }

    public static float getDamage(Projectile projectile) {
        if (projectile instanceof ProjectileWithPower withPower){
            return withPower.getDamage();
        }
        if (projectile instanceof AbstractArrow arrow){
            return (float) Mth.ceil(Mth.clamp(arrow.getBaseDamage() * projectile.getDeltaMovement().length(), 0, Integer.MAX_VALUE));
        }
        return 0;
    }
}
