package org.dreamtinker.dreamtinker.tools.modifiers.tools.chain_saw_blade;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerSounds;
import org.dreamtinker.dreamtinker.library.client.sound.ClientSoundChecker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.tools.capability.ToolEnergyCapability;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import javax.annotation.Nullable;
import java.util.*;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.ChainSawEnergyCost;
import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

public class DeathShredder extends BattleModifier implements KeybindInteractModifierHook {
    public DeathShredder() {this.tiers = TierSortingRegistry.getSortedTiers();}

    private enum Modes {
        FUEL,
        ELECTRIC,
        MIX
    }

    private final List<Tier> tiers;

    private final ResourceLocation TAG_MOD = Dreamtinker.getLocation("working_mode");
    private final ResourceLocation TAG_FUEL_DURATION = Dreamtinker.getLocation("fuel_duration");
    private final ResourceLocation TAG_HEAT = Dreamtinker.getLocation("chainsaw_heat");
    private final ResourceLocation TAG_ROTATIONAL_FORCE = Dreamtinker.getLocation("rotational_force");
    private final int MAX_FORCE_FUEL = 120;//This is minecraft!
    private final float MAX_HEAT_FUEL = 150;
    private final int MAX_FORCE_ELECTRIC = 80;
    private final float MAX_HEAT_ELECTRIC = 90;
    private final int MAX_FORCE_MIX = 120;
    private final float MAX_HEAT_MIX = 120;


    private final ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(Attributes.ARMOR, Attributes.ARMOR_TOUGHNESS));
    private final UUID uuid = UUID.nameUUIDFromBytes(("death_shredder").getBytes());

    @Override
    public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
        if (player.level().isClientSide)
            return false;
        if (player.isUsingItem())
            return false;
        ModDataNBT dataNBT = tool.getPersistentData();
        int mod = (dataNBT.getInt(TAG_MOD) + 1) % 3;
        dataNBT.putInt(TAG_MOD, mod);
        //ToolEnergyCapability.setEnergy(tool, 500000);
        player.sendSystemMessage(Component.translatable("modifier.dreamtinker.tooltip.death_shredder")
                                          .append(Component.translatable("modifier.dreamtinker.mod.death_shredder" + "_" + mod))
                                          .withStyle(this.getDisplayName().getStyle()));
        return true;
    }

    @Nullable
    private MeltingFuel lastRecipe;

    @Nullable
    protected MeltingFuel findRecipe(Fluid fluid) {
        if (lastRecipe != null && lastRecipe.matches(fluid)){
            return lastRecipe;
        }
        MeltingFuel recipe = MeltingFuelLookup.findFuel(fluid);
        if (recipe != null){
            lastRecipe = recipe;
        }
        return recipe;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        builder.addModule(ToolTankHelper.TANK_HANDLER);
        builder.addModule(ToolEnergyCapability.ENERGY_HANDLER);
        builder.addModule(StatBoostModule.add(ToolEnergyCapability.MAX_STAT).flat(5000 * 10));
        builder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).flat(FluidType.BUCKET_VOLUME * 10));
        builder.addHook(this, ModifierHooks.ARMOR_INTERACT);
        super.registerHooks(builder);
    }

    @Override
    public int getPriority() {
        return 2700;
    }

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
        return 72000;//Shouldn`t be this long, will see
    }

    @Override
    public @NotNull UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
        return BlockingModifier.blockWhileCharging(tool, UseAnim.BOW);
    }

    @Override
    public @NotNull InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK && player.getOffhandItem().isEmpty() && hand == InteractionHand.MAIN_HAND){
            int mode = tool.getPersistentData().getInt(TAG_MOD);
            float heat = tool.getPersistentData().getFloat(TAG_HEAT);
            if (0.8 < heat / MAX_HEAT_FUEL)
                return InteractionResult.PASS;
            boolean fluid_valid = false, energy_valid = false;
            if (Modes.ELECTRIC.ordinal() != mode){
                FluidStack fluid = TANK_HELPER.getFluid(tool);
                if (fluid.isEmpty())
                    return InteractionResult.PASS;
                MeltingFuel recipe = findRecipe(fluid.getFluid());
                if (null != recipe){
                    int amount = recipe.getAmount(fluid.getFluid());
                    if (fluid.getAmount() >= amount)//even if we have duration
                        fluid_valid = true;
                }
            }
            if (Modes.FUEL.ordinal() != mode){
                int energy = ToolEnergyCapability.getEnergy(tool);
                if (ChainSawEnergyCost.get() * 10 <= energy)
                    energy_valid = true;
            }
            if (Modes.MIX.ordinal() != mode && (fluid_valid || energy_valid) || (fluid_valid && energy_valid)){
                GeneralInteractionModifierHook.startUsingWithDrawtime(tool, modifier.getId(), player, hand, 1.5f);
                player.level().playSound(
                        null,                      // 播给周围所有玩家
                        player.getX(), player.getY(), player.getZ(),
                        DreamtinkerSounds.CHAINSAW_START.get(),
                        SoundSource.PLAYERS,
                        1.0F,                      // 音量
                        1.0F                       // 音高
                );
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
        AttributeInstance reach = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
        double range = null != reach ? reach.getValue() : 2.5D;
        Vec3 srcVec = entity.getEyePosition();
        Vec3 lookVec = entity.getViewVector(1.5F);
        Vec3 destVec = srcVec.add(lookVec.x() * range, lookVec.y() * range, lookVec.z() * range);
        float var9 = 1.0F;
        List<Entity> possibleList = entity.level().getEntities(entity, entity.getBoundingBox()
                                                                             .expandTowards(lookVec.x() * range, lookVec.y() * range, lookVec.z() * range)
                                                                             .inflate(var9, var9, var9));
        possibleList.sort(Comparator.comparingDouble(entity::distanceToSqr));
        ModDataNBT dataNBT = tool.getPersistentData();
        int mode = dataNBT.getInt(TAG_MOD);
        int rotation = dataNBT.getInt(TAG_ROTATIONAL_FORCE);
        float heat = dataNBT.getFloat(TAG_HEAT);
        float max_heat = Modes.MIX.ordinal() == mode ? MAX_HEAT_MIX : Modes.FUEL.ordinal() == mode ? MAX_HEAT_FUEL : MAX_HEAT_ELECTRIC;
        int fuel_duration = tool.getPersistentData().getInt(TAG_FUEL_DURATION);
        boolean electric_valid = false;
        boolean fuel_valid = false;
        int fuel_availables = Math.max(1, possibleList.size());
        int electric_availables = Math.max(1, possibleList.size());
        int energy = ToolEnergyCapability.getEnergy(tool);
        if (!entity.level().isClientSide){
            if (Modes.ELECTRIC.ordinal() != mode){
                FluidStack fluid = TANK_HELPER.getFluid(tool);
                MeltingFuel recipe = findRecipe(fluid.getFluid());
                if (null != recipe){// No Fuel, not working
                    int fuel_rate = recipe.getRate();
                    if (fuel_duration <= fuel_rate * fuel_availables){
                        int amount = recipe.getAmount(fluid.getFluid());
                        while (amount <= fluid.getAmount()) {//in case fuel duration < fuel_rate
                            fluid.shrink(amount);
                            fuel_duration += recipe.getDuration();
                            if (fuel_rate * fuel_availables < fuel_duration)
                                break;
                        }
                    }
                    fuel_availables = Math.max(1, Math.floorDiv(fuel_duration, fuel_rate));
                    if (0 < fuel_duration)
                        fuel_valid = true;
                    fuel_duration -= fuel_availables * fuel_rate;
                    TANK_HELPER.setFluid(tool, fluid);
                    if (fuel_valid){
                        if (rotation < MAX_FORCE_FUEL)
                            rotation += fuel_rate;
                        heat += recipe.getTemperature() / 10000.0f;
                    }
                    dataNBT.putInt(TAG_FUEL_DURATION, Math.max(0, fuel_duration));
                }
            }
            if (Modes.FUEL.ordinal() != mode){
                electric_availables = Math.min(electric_availables, Math.round((float) energy / ChainSawEnergyCost.get()));
                if (1 <= electric_availables){
                    electric_valid = true;
                    energy -= electric_availables * ChainSawEnergyCost.get();
                    heat += entity.level().random.nextFloat();
                    if (rotation < (Modes.ELECTRIC.ordinal() == mode ? MAX_FORCE_ELECTRIC : MAX_FORCE_MIX))
                        rotation = (Modes.ELECTRIC.ordinal() == mode ? MAX_FORCE_ELECTRIC : MAX_FORCE_MIX);
                    ToolEnergyCapability.setEnergy(tool, energy);
                }
            }
            dataNBT.putInt(TAG_ROTATIONAL_FORCE, rotation);
            if (!fuel_valid && !electric_valid)
                entity.stopUsingItem();
            if (rotation < MAX_FORCE_FUEL / 2)//Minimum starting one
                return;
        }

        ClientSoundChecker.playWorldSound(entity, (byte) 1);
        boolean flag = false;
        for (int i = 0; i < Math.min(fuel_availables, electric_availables) && i < possibleList.size(); i++) {
            Entity victim = possibleList.get(i);
            if (victim instanceof LivingEntity){
                float borderSize = 0.5F;
                AABB collisionBB = victim.getBoundingBox().inflate(borderSize, borderSize, borderSize);
                Optional<Vec3> interceptPos = collisionBB.clip(srcVec, destVec);
                if (collisionBB.contains(srcVec)){
                    flag = true;
                }else if (interceptPos.isPresent()){
                    flag = true;
                }

                if (flag){
                    ToolAttackUtil.performAttack(tool, ToolAttackContext.attacker(entity).target(victim).cooldown(1).applyAttributes()
                                                                        .build());

                    if (fuel_valid || electric_valid){
                        heat += entity.level().random.nextFloat() / 5f;
                        AttributeInstance attr = ((LivingEntity) victim).getAttribute(Attributes.ARMOR);
                        if (null != attr)
                            heat += (float) (attr.getValue() * 0.01f);
                        if (fuel_valid && Modes.FUEL.ordinal() == mode && ChainSawEnergyCost.get() * 0.5 < energy && 0.7 < heat / max_heat){
                            energy -= Math.round(ChainSawEnergyCost.get() * 0.5f);
                            heat -= entity.level().random.nextFloat() / 5f;
                        }
                        if (electric_valid)
                            heat += entity.level().random.nextFloat() / 5f;
                        if (0.9 < heat / max_heat){
                            entity.stopUsingItem();
                            dataNBT.putFloat(TAG_HEAT, Math.min(heat, MAX_HEAT_FUEL));
                            if (Modes.FUEL.ordinal() == mode)
                                ToolEnergyCapability.setEnergy(tool, energy);
                            return;
                        }
                    }
                    double d0 = (entity.level().getRandom().nextFloat() - 0.5F) + victim.getDeltaMovement().x;
                    double d1 = (entity.level().getRandom().nextFloat() - 0.5F) + victim.getDeltaMovement().y;
                    double d2 = (entity.level().getRandom().nextFloat() - 0.5F) + victim.getDeltaMovement().z;
                    double dist = 1F + entity.level().getRandom().nextFloat() * 0.2F;
                    double d3 = d0 * dist;
                    double d4 = d1 * dist;
                    double d5 = d2 * dist;
                    SimpleParticleType temp;
                    SimpleParticleType sharp;
                    if (0.5 < heat / MAX_HEAT_FUEL)
                        temp = ParticleTypes.LAVA;
                    else
                        temp = ParticleTypes.FLAME;
                    victim.level().addParticle(temp, victim.getX(), victim.getEyeY() - 0.1D + (victim.getEyePosition().y - victim.getEyeY()),
                                               victim.getZ(), d3, d4, d5);
                    if (0.5 < (double) rotation / MAX_FORCE_FUEL)
                        sharp = ParticleTypes.ELECTRIC_SPARK;
                    else
                        sharp = ParticleTypes.SCRAPE;
                    victim.level().addParticle(sharp, victim.getX(),
                                               victim.getEyeY() - 0.1D + (victim.getEyePosition().y - victim.getEyeY()),
                                               victim.getZ(), d3, d4, d5);

                }
            }
        }
        if (!entity.level().isClientSide){
            dataNBT.putFloat(TAG_HEAT, Math.min(heat, MAX_HEAT_FUEL));
            ToolEnergyCapability.setEnergy(tool, energy);
        }
    }

    @Override
    public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
        ClientSoundChecker.clearSoundCacheFor(entity);
        entity.level().playSound(
                null,                      // 播给周围所有玩家
                entity.getX(), entity.getY(), entity.getZ(),
                DreamtinkerSounds.CHAINSAW_STOP.get(),
                SoundSource.PLAYERS,
                1.0F,                      // 音量
                1.0F                       // 音高
        );
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && !holder.getUseItem().equals(stack)){
            ModDataNBT dataNBT = tool.getPersistentData();
            int rotation = dataNBT.getInt(TAG_ROTATIONAL_FORCE);
            float heat = dataNBT.getFloat(TAG_HEAT);
            if (0 < rotation)
                dataNBT.putInt(TAG_ROTATIONAL_FORCE, Math.max(0, rotation - 30));
            if (0 < heat)
                dataNBT.putFloat(TAG_HEAT, (float) Math.max(0, heat - 0.1));
        }

    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        if (!context.getAttacker().isUsingItem())
            return 0;
        int mode = tool.getPersistentData().getInt(TAG_MOD);
        LivingEntity target = context.getLivingTarget();
        if (null != target && !target.level().isClientSide){
            if (Modes.ELECTRIC.ordinal() != mode)
                for (Attribute attr : attributes) {
                    AttributeInstance attr_instance = target.getAttribute(attr);
                    if (null != attr_instance){
                        AttributeModifier cur = attr_instance.getModifier(uuid);
                        if (cur == null){
                            attr_instance.removeModifier(uuid);
                            attr_instance.addPermanentModifier(new AttributeModifier(uuid, attr.getDescriptionId(), -0.4f,
                                                                                     AttributeModifier.Operation.MULTIPLY_BASE));
                        }
                    }
                }
            if (Modes.ELECTRIC.ordinal() != mode){
                target.addEffect(new MobEffectInstance(TinkerEffects.bleeding.get(), 20 * 5, 1));
            }
            if (Modes.MIX.ordinal() == mode){
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 5, 1));
            }
        }

        return 0;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        if (null != target && !target.level().isClientSide){//always clear this
            for (Attribute attr : attributes) {
                AttributeInstance attr_instance = target.getAttribute(attr);
                if (null != attr_instance)
                    attr_instance.removeModifier(uuid);
            }
            Tier tier = tool.getStats().get(ToolStats.HARVEST_TIER);
            int idx = tiers.indexOf(tier);
            context.getLivingTarget().invulnerableTime = Math.round((float) context.getLivingTarget().invulnerableTime / idx);
        }
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (!context.getAttacker().isUsingItem())
            return damage;
        ModDataNBT dataNBT = tool.getPersistentData();
        int mode = dataNBT.getInt(TAG_MOD);
        int rotation = dataNBT.getInt(TAG_ROTATIONAL_FORCE);
        float heat = dataNBT.getFloat(TAG_HEAT) / MAX_HEAT_FUEL;
        damage += damage * ((float) rotation / MAX_FORCE_FUEL - .8f);
        damage *= computeHeatEfficiencyFromPercent(heat);
        if (Modes.ELECTRIC.ordinal() != mode){
            FluidStack fluid = TANK_HELPER.getFluid(tool);
            if (fluid.isEmpty())
                return damage;
            MeltingFuel recipe = findRecipe(fluid.getFluid());
            if (null != recipe){
                float rate = recipe.getRate();
                return damage * (1.0f + rate / 100.0f);
            }
        }

        return damage;
    }


    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tool instanceof ToolStack && tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            int mod = nbt.getInt(TAG_MOD);
            int fuel_duration = nbt.getInt(TAG_FUEL_DURATION);
            float heat = nbt.getFloat(TAG_HEAT);
            float max_heat = Modes.MIX.ordinal() == mod ? MAX_HEAT_MIX : Modes.FUEL.ordinal() == mod ? MAX_HEAT_FUEL : MAX_HEAT_ELECTRIC;
            tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.death_shredder")
                                 .append(Component.translatable("modifier.dreamtinker.mod.death_shredder" + "_" + mod))
                                 .withStyle(this.getDisplayName().getStyle()));
            if (Modes.ELECTRIC.ordinal() != mod){
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.death_shredder_fuel")
                                     .append(String.valueOf(fuel_duration))
                                     .withStyle(this.getDisplayName().getStyle()));
            }
            tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.death_shredder_heat")
                                 .append(String.format("%.2f", heat / max_heat * 100) + "%")
                                 .withStyle(this.getDisplayName().getStyle()));


        }
    }

    private float computeHeatEfficiencyFromPercent(float percent) {
        // clamp 到 0~1
        float p = Math.max(0f, Math.min(1f, percent));

        if (p < 0.2f){
            // 冷启动：0.0-0.2 → 0.5 ~ 0.85
            float x = p / 0.2f;
            return 0.5f + 0.35f * x;

        }else if (p < 0.4f){
            // 暖机：0.2-0.4 → 0.85 ~ 1.0
            float x = (p - 0.2f) / 0.2f;
            return 0.85f + 0.15f * x;

        }else if (p < 0.7f){
            // 稳定：0.4-0.7 → 1.0 ~ 1.2
            float x = (p - 0.4f) / 0.3f;
            return 1.0f + 0.2f * x;

        }else {
            // 过热：0.7-1.0 → 1.0 ~ 0.4
            float x = (p - 0.7f) / 0.3f;
            return 1.0f - 0.6f * x;
        }
    }
}
