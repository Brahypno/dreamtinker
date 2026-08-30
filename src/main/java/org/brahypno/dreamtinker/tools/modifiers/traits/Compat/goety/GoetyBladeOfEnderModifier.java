package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.goety;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.projectiles.VoidSlash;
import com.Polarice3.Goety.common.events.TimedEvents;
import com.Polarice3.Goety.common.items.equipment.BladeOfEnderItem;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.esotericismtinker.library.modifiers.EsotericismTinkerHook;
import org.brahypno.esotericismtinker.library.modifiers.hook.LeftClickHook;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Reproduces Goety's Blade of Ender behavior on a modifiable sword.
 */
public final class GoetyBladeOfEnderModifier extends NoLevelsModifier
        implements LeftClickHook, MeleeHitModifierHook, GeneralInteractionModifierHook, AttributesModifierHook {
    private static final int VOID_TOUCHED_DURATION = 5 * 20;
    private static final int BACKSTEP_COOLDOWN = 20;
    private static final int DASH_COOLDOWN = 7 * 20;
    private static final float DAMAGE = 9.0f;

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, EsotericismTinkerHook.LEFT_CLICK, ModifierHooks.MELEE_HIT,
                            ModifierHooks.GENERAL_INTERACT, ModifierHooks.ATTRIBUTES);
        super.registerHooks(hookBuilder);
    }

    private static boolean isCoolingDown(Player player, ItemStack weapon) {
        return player.getCooldowns().isOnCooldown(weapon.getItem());
    }

    private static void applyVoidTouched(LivingEntity target) {
        target.addEffect(new MobEffectInstance(
                GoetyEffects.VOID_TOUCHED.get(), VOID_TOUCHED_DURATION, 0, false, true));
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        if (damageDealt > 0 && target != null && !target.level().isClientSide)
            applyVoidTouched(target);
    }

    private static void strike(
            IToolStackView tool, Player player, Level level, EquipmentSlot equipmentSlot) {
        ItemStack weapon = player.getItemBySlot(equipmentSlot);
        if (level.isClientSide || tool.isBroken() || player.isSpectator() || isCoolingDown(player, weapon)
            || player.getAttackStrengthScale(0.5f) <= 0.9f){
            return;
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.OBSIDIAN_CLAYMORE_SWING.get(), SoundSource.PLAYERS,
                        2.0f, player.getVoicePitch());
        Vec3 view = player.getViewVector(1.0f);
        ModifierVoidSlash slash = new ModifierVoidSlash(weapon, level, player);
        slash.setPos(player.getX() + view.x / 2.0, player.getEyeY() - 0.2,
                     player.getZ() + view.z / 2.0);
        slash.setDamage(DAMAGE);
        slash.setMaxLifeSpan(10);

        int velocityLevel = tool.getModifierLevel(DreamtinkerModifiers.Ids.goety_velocity);
        int radiusLevel = tool.getModifierLevel(DreamtinkerModifiers.Ids.goety_radius);
        float velocityScale = 1.0f + velocityLevel / 3.0f;
        float radiusBonus = radiusLevel / 4.0f;
        slash.setRadius(Math.max(0.1f, slash.getRadius() / velocityScale + radiusBonus));
        slash.setMaxRadius(Math.max(0.1f, slash.getMaxRadius() / velocityScale + radiusBonus));
        slash.slash(view, 0.5 + velocityLevel / 3.0);
        slash.setVoidLevel(1);
        level.addFreshEntity(slash);
    }

    @Override
    public void onLeftClickEmpty(
            IToolStackView tool, ModifierEntry entry, Player player,
            Level level, EquipmentSlot equipmentSlot) {
        strike(tool, player, level, equipmentSlot);
    }

    @Override
    public void onLeftClickEntity(
            AttackEntityEvent event, IToolStackView tool, ModifierEntry entry,
            Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {
        strike(tool, player, level, equipmentSlot);
    }

    @Override
    public InteractionResult onToolUse(
            IToolStackView tool, ModifierEntry modifier, Player player,
            InteractionHand hand, InteractionSource source) {
        ItemStack weapon = player.getItemInHand(hand);
        if (source != InteractionSource.RIGHT_CLICK || tool.isBroken() || isCoolingDown(player, weapon))
            return InteractionResult.PASS;

        if (!player.level().isClientSide){
            if (player.isSprinting()){
                dash(player);
                player.getCooldowns().addCooldown(weapon.getItem(), DASH_COOLDOWN);
            }else {
                backstep(player);
                player.getCooldowns().addCooldown(weapon.getItem(), BACKSTEP_COOLDOWN);
            }
            player.swing(hand, true);
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    private static void dash(Player player) {
        float xDirection = (float) Math.cos(Math.toRadians(player.getYRot() + 90.0f));
        float zDirection = (float) Math.sin(Math.toRadians(player.getYRot() + 90.0f));
        Vec3 view = player.getViewVector(1.0f);
        float distance = Mth.clamp((float) Mth.square(player.distanceToSqr(view)), 0.0f, 10.0f);
        player.push(xDirection * 0.9f * distance, 0.0, zDirection * 0.9f * distance);
        player.hurtMarked = true;

        if (player.level() instanceof ServerLevel serverLevel){
            player.level().playSound(null, player.blockPosition(), ModSounds.VHOE_CHARGE.get(),
                                     SoundSource.PLAYERS, 3.0f, player.getVoicePitch());
            TimedEvents.submitTask(
                    "dreamtinker:blade_of_ender_charge/" + player.getUUID(),
                    new BladeOfEnderItem.ChargeTask(player.getUUID(), serverLevel, DAMAGE));
        }
    }

    private static void backstep(Player player) {
        float force = -2.5f;
        float radians = (float) Math.toRadians(player.getYRot() + 90.0f);
        Vec3 movement = player.getDeltaMovement().add(
                force * Math.cos(radians), 0.0, force * Math.sin(radians));
        player.setDeltaMovement(movement.x, 0.4, movement.z);
        player.hurtMarked = true;
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT,
                                 SoundSource.PLAYERS, 2.0f, 1.0f);
    }

    @Override
    public void addAttributes(
            IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot,
            BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot != EquipmentSlot.MAINHAND || tool.isBroken())
            return;
        Attribute reach = ForgeMod.ENTITY_REACH.get();
        UUID uuid = UUID.nameUUIDFromBytes(
                (slot.getName() + "." + getId() + "." + reach.getDescriptionId())
                        .getBytes(StandardCharsets.UTF_8));
        consumer.accept(reach, new AttributeModifier(
                uuid, getTranslationKey(), 1.0, AttributeModifier.Operation.ADDITION));
    }

    /**
     * Keeps Goety's projectile collision while extending Void Touched to the requested five seconds.
     */
    private static final class ModifierVoidSlash extends VoidSlash {
        private final ItemStack weapon;

        private ModifierVoidSlash(ItemStack weapon, Level level, LivingEntity owner) {
            super(weapon, level, owner);
            this.weapon = weapon.copy();
        }

        @Override
        public void damageEntity(Entity entity) {
            Entity owner = getOwner();
            DamageSource source = owner != null
                                  ? ModDamageSource.sword(owner, owner)
                                  : damageSources().thrown(this, this);
            float damage = getDamage();
            if (entity instanceof LivingEntity living){
                MobType mobType = living.getMobType();
                damage += EnchantmentHelper.getDamageBonus(weapon, mobType);
            }
            if (entity.hurt(source, damage) && entity instanceof LivingEntity living)
                applyVoidTouched(living);
        }
    }
}
