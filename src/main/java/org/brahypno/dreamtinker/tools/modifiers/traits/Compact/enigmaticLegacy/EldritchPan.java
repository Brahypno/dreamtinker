package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.utils.CompactUtils.EnigmaticLegacyCompact;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class EldritchPan extends Modifier implements MeleeHitModifierHook, DamageBlockModifierHook, InventoryTickModifierHook, ModifierRemovalHook, TooltipModifierHook, AttributesModifierHook, ValidateModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.DAMAGE_BLOCK, ModifierHooks.INVENTORY_TICK,
                            ModifierHooks.REMOVE, ModifierHooks.TOOLTIP, ModifierHooks.ATTRIBUTES, ModifierHooks.VALIDATE);
        hookBuilder.addModule(new ModifierTraitModule(DreamtinkerModifiers.cursed_ring_bound.getId(), 1, true));
        hookBuilder.addModule(new ModifierTraitModule(TinkerModifiers.blocking.getId(), 1, true));
        super.registerHooks(hookBuilder);
    }

    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().putInt(CursedRingBound.TAG_DEEP_CURSE, Math.max(0, tool.getPersistentData().getInt(CursedRingBound.TAG_DEEP_CURSE) - 1));
        return null;
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        tool.getPersistentData().putInt(CursedRingBound.TAG_DEEP_CURSE, 1);
        return null;
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        LivingEntity entity = context.getEntity();
        if (entity.isBlocking() && entity instanceof Player player){
            Entity projectile = source.getDirectEntity();

            if (!(projectile instanceof Projectile))
                return false;

            if (!source.is(DamageTypeTags.BYPASSES_SHIELD)){
                Vec3 sourcePos = source.getSourcePosition();

                if (sourcePos != null){
                    Vec3 lookVec = entity.getViewVector(1.0F);
                    Vec3 sourceToSelf = sourcePos.vectorTo(entity.position()).normalize();
                    sourceToSelf = new Vec3(sourceToSelf.x, 0.0D, sourceToSelf.z);

                    if (sourceToSelf.dot(lookVec) < 0.0D){
                        projectile.kill();

                        FoodData data = player.getFoodData();
                        data.eat(4, 0.5F);

                        entity.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                                 SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F,
                                                 player.level().random.nextFloat() * 0.1F + 0.9F);

                        player.gameEvent(GameEvent.EAT);

                        if (projectile instanceof LargeFireball fireball){
                            fireball.explosionPower = 0;
                        }

                        if (player.level() instanceof ServerLevel level){
                            Vec3 angle = player.getLookAngle();
                            angle.multiply(1, 0, 1).normalize().multiply(0.5, 0.5, 0.5);

                            level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM,
                                                                       new ItemStack(Items.FIRE_CHARGE)), player.getX() + angle.x,
                                                player.getY() + player.getEyeHeight() - 0.1, player.getZ() + angle.z,
                                                10, 0.3D, 0.3D, 0.3D, 0.03D);
                        }

                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static final ResourceLocation TAG_PAN = new ResourceLocation(Dreamtinker.MODID, "eldritch_pan");
    private static final ResourceLocation TAG_PAN_TICKS = new ResourceLocation(Dreamtinker.MODID, "eldritch_tick");

    public static MobEffect getBloodlust() {
        return EnigmaticLegacyCompact.growingBloodlust();
    }

    public static MobEffect getHunger() {
        return EnigmaticLegacyCompact.growingHunger();
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            int kills = tool.getPersistentData().getInt(TAG_PAN);
            EnigmaticLegacyCompact.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPanKills1", ChatFormatting.GOLD, kills);
            if (kills >= EnigmaticLegacyCompact.eldritchPanUniqueGainLimit()){
                EnigmaticLegacyCompact.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPanKillsMax");
            }
        }
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;

        if (holder instanceof Player player)
            if (isSelected){
                int currentTicks = tool.getPersistentData().getInt(TAG_PAN_TICKS);

                if (EnigmaticLegacyCompact.cannotHunger(player)){
                    int bloodlustAmplifier = currentTicks / EnigmaticLegacyCompact.bloodlustTicksPerLevel();

                    bloodlustAmplifier = Math.min(bloodlustAmplifier, 9);

                    if (null != getBloodlust())
                        player.addEffect(new MobEffectInstance(getBloodlust(),
                                                               MobEffectInstance.INFINITE_DURATION, bloodlustAmplifier, true, true));
                }else {
                    int hungerAmplifier = currentTicks / EnigmaticLegacyCompact.bloodlustTicksPerLevel();

                    hungerAmplifier = Math.min(hungerAmplifier, 9);

                    if (null != getHunger())
                        player.addEffect(new MobEffectInstance(getHunger(),
                                                               MobEffectInstance.INFINITE_DURATION, hungerAmplifier, true, true));
                }

                EnigmaticLegacyCompact.setEldritchPanHoldingDuration(player, ++currentTicks);
                tool.getPersistentData().putInt(TAG_PAN_TICKS, ++currentTicks);
            }else {
                tool.getPersistentData().putInt(TAG_PAN_TICKS, 0);
                if (null != getHunger())
                    player.removeEffect(getHunger());
                if (null != getBloodlust())
                    player.removeEffect(getBloodlust());
            }

    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (context.getAttacker() instanceof Player player && !player.level().isClientSide){
            if (EnigmaticLegacyCompact.isTheWorthyOne(player)){
                float lifesteal = (float) (damageDealt * EnigmaticLegacyCompact.eldritchPanLifeSteal());

                if (null != getBloodlust() && player.hasEffect(getBloodlust())){
                    int amplifier = 1 + player.getEffect(getBloodlust()).getAmplifier();
                    lifesteal += (float) ((damageDealt) * (EnigmaticLegacyCompact.bloodlustLifestealBoost() * amplifier));
                }

                player.heal(lifesteal);
                float hungersteal = (float) EnigmaticLegacyCompact.eldritchPanHungerSteal();
                boolean noHunger = EnigmaticLegacyCompact.cannotHunger(player);

                if (context.getTarget() instanceof ServerPlayer victim){
                    FoodData victimFood = victim.getFoodData();
                    FoodData attackerFood = player.getFoodData();

                    int foodSteal = Math.min((int) Math.ceil(hungersteal), victimFood.getFoodLevel());
                    float saturationSteal = Math.min(hungersteal / 5F, victimFood.getSaturationLevel());

                    victimFood.setSaturation(victimFood.getSaturationLevel() - saturationSteal);
                    victimFood.setFoodLevel(victimFood.getFoodLevel() - foodSteal);

                    if (noHunger){
                        player.heal((float) foodSteal / 2);
                    }else {
                        attackerFood.eat(foodSteal, saturationSteal);
                    }
                }else {
                    if (noHunger){
                        player.heal(hungersteal / 2);
                    }else {
                        player.getFoodData().eat((int) Math.ceil(hungersteal), hungersteal / 5F);
                    }
                }
            }
        }
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!tool.isBroken() && modifier.getLevel() > 0 && EquipmentSlot.MAINHAND == slot){
            ModDataNBT nbt = tool.getPersistentData();
            int kills = nbt.getInt(TAG_PAN);
            if (kills > 0){
                String tool_attribute_uuid = "50c030b6-e8ef-4a99-9a6a-9c231b2365a8";
                consumer.accept(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                      this.getTranslationKey(),
                                                      EnigmaticLegacyCompact.eldritchPanUniqueDamageGain() * kills,
                                                      AttributeModifier.Operation.ADDITION));
                consumer.accept(Attributes.ARMOR,
                                new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                      this.getTranslationKey(),
                                                      EnigmaticLegacyCompact.eldritchPanUniqueArmorGain() * kills,
                                                      AttributeModifier.Operation.ADDITION));
            }
        }
    }
}
