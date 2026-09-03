package org.brahypno.dreamtinker.tools.modifiers.tools.chain_saw_blade;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerSounds;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.brahypno.esotericismtinker.utils.MessagesUtil;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.json.RandomLevelingValue;
import slimeknights.tconstruct.library.json.predicate.tool.ToolStackPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.*;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.MeleeAttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.MobEffectModule;
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
import java.util.Comparator;
import java.util.List;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.ChainSawEnergyCost;
import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

public class DeathShredder extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook, InventoryTickModifierHook, UsingToolModifierHook, GeneralInteractionModifierHook, TooltipModifierHook, KeybindInteractModifierHook {
    public DeathShredder() {this.tiers = TierSortingRegistry.getSortedTiers();}

    private enum Modes {
        FUEL,
        ELECTRIC,
        MIX
    }

    private final List<Tier> tiers;

    private static final ResourceLocation TAG_MOD = Dreamtinker.getLocation("working_mode");
    private final ResourceLocation TAG_FUEL_DURATION = Dreamtinker.getLocation("fuel_duration");
    private final ResourceLocation TAG_FUEL_RATE = Dreamtinker.getLocation("fuel_rate");
    private final ResourceLocation TAG_FUEL_TEMPERATURE = Dreamtinker.getLocation("fuel_temperature");
    private final ResourceLocation TAG_HEAT = Dreamtinker.getLocation("chainsaw_heat");
    private final ResourceLocation TAG_ROTATIONAL_FORCE = Dreamtinker.getLocation("rotational_force");
    private final int MAX_FORCE_FUEL = 120;//This is minecraft!
    private final float MAX_HEAT_FUEL = 150;
    private final int MAX_FORCE_ELECTRIC = 80;
    private final float MAX_HEAT_ELECTRIC = 90;
    private final int MAX_FORCE_MIX = 120;
    private final float MAX_HEAT_MIX = 120;

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
        MessagesUtil.clientChat(Component.translatable("modifier.dreamtinker.tooltip.death_shredder")
                                         .append(Component.translatable("modifier.dreamtinker.mod.death_shredder" + "_" + mod))
                                         .withStyle(this.getDisplayName().getStyle()), false);
        return true;
    }

    @Nullable
    protected MeltingFuel findRecipe(Fluid fluid) {
        // The lookup is already indexed, and follows recipe reloads unlike a modifier-wide cache.
        return MeltingFuelLookup.findFuel(fluid);
    }

    private float maxHeat(int mode) {
        return Modes.MIX.ordinal() == mode ? MAX_HEAT_MIX : Modes.FUEL.ordinal() == mode ? MAX_HEAT_FUEL : MAX_HEAT_ELECTRIC;
    }

    private int maxForce(int mode) {
        return Modes.MIX.ordinal() == mode ? MAX_FORCE_MIX : Modes.FUEL.ordinal() == mode ? MAX_FORCE_FUEL : MAX_FORCE_ELECTRIC;
    }

    private boolean isUsableFuel(@Nullable MeltingFuel recipe, FluidStack fluid) {
        return recipe != null && !fluid.isEmpty() && recipe.getAmount(fluid.getFluid()) > 0
               && recipe.getRate() > 0 && recipe.getDuration() > 0;
    }

    private record FuelState(int duration, int rate, int temperature) {}

    /**
     * Buy whole fuel batches only when needed; keep their unspent burn time on this tool.
     */
    private FuelState prepareFuel(IToolStackView tool, int requestedUnits) {
        ModDataNBT data = tool.getPersistentData();
        FluidStack fluid = TANK_HELPER.getFluid(tool);
        MeltingFuel recipe = fluid.isEmpty() ? null : findRecipe(fluid.getFluid());
        int duration = Math.max(0, data.getInt(TAG_FUEL_DURATION));
        int rate = data.getInt(TAG_FUEL_RATE);
        int temperature = data.getInt(TAG_FUEL_TEMPERATURE);
        if (rate <= 0){
            // Old saves did not store the properties of fuel already burned out of the tank.
            rate = isUsableFuel(recipe, fluid) ? recipe.getRate() : 1;
            temperature = isUsableFuel(recipe, fluid) ? recipe.getTemperature() : 0;
        }

        if (duration < (long) rate * requestedUnits && isUsableFuel(recipe, fluid)){
            int amount = recipe.getAmount(fluid.getFluid());
            int newRate = recipe.getRate();
            int burnTime = recipe.getDuration();
            long missing = Math.max(0L, (long) newRate * requestedUnits - duration);
            long neededBatches = (missing + burnTime - 1L) / burnTime;
            int batches = (int) Math.min(neededBatches,
                                         Math.min(fluid.getAmount() / amount, (Integer.MAX_VALUE - duration) / burnTime));
            if (batches > 0){
                fluid.shrink(batches * amount);
                TANK_HELPER.setFluid(tool, fluid);
                duration += batches * burnTime;
                rate = newRate;
                temperature = recipe.getTemperature();
            }
        }

        data.putInt(TAG_FUEL_DURATION, duration);
        data.putInt(TAG_FUEL_RATE, rate);
        data.putInt(TAG_FUEL_TEMPERATURE, temperature);
        return new FuelState(duration, rate, temperature);
    }

    private static final ToolStackPredicate isElectric =
            ToolStackPredicate.simple(iToolContext -> iToolContext.getPersistentData().getInt(TAG_MOD) == (Modes.ELECTRIC.ordinal()));
    private static final ToolStackPredicate isMIX =
            ToolStackPredicate.simple(iToolContext -> iToolContext.getPersistentData().getInt(TAG_MOD) == (Modes.MIX.ordinal()));

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        builder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT,
                        ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOL_USING, ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOLTIP,
                        ModifierHooks.ARMOR_INTERACT);
        builder.addModule(ToolTankHelper.TANK_HANDLER);
        builder.addModule(ToolEnergyCapability.ENERGY_HANDLER);
        builder.addModule(StatBoostModule.add(ToolEnergyCapability.MAX_STAT).flat(5000 * 10));
        builder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).flat(FluidType.BUCKET_VOLUME * 10));
        builder.addModule(MeleeAttributeModule.builder(Attributes.ARMOR, AttributeModifier.Operation.MULTIPLY_BASE).tool(isElectric).flat(-0.4f));
        builder.addModule(
                MeleeAttributeModule.builder(Attributes.ARMOR_TOUGHNESS, AttributeModifier.Operation.MULTIPLY_BASE).tool(isElectric).flat(-0.4f));
        builder.addModule(MobEffectModule.builder(TinkerEffects.bleeding.get())
                                         .level(RandomLevelingValue.flat(1))
                                         .time(RandomLevelingValue.perLevel(20 * 5, 10))
                                         .tool(isElectric.inverted())
                                         .build());
        builder.addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN)
                                         .level(RandomLevelingValue.flat(1))
                                         .time(RandomLevelingValue.perLevel(20 * 5, 10))
                                         .tool(isMIX)
                                         .build());

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
            if (0.8 < heat / maxHeat(mode))
                return InteractionResult.PASS;
            boolean fluid_valid = false, energy_valid = false;
            if (Modes.ELECTRIC.ordinal() != mode){
                ModDataNBT data = tool.getPersistentData();
                fluid_valid = data.getInt(TAG_FUEL_DURATION) >= Math.max(1, data.getInt(TAG_FUEL_RATE));
                FluidStack fluid = TANK_HELPER.getFluid(tool);
                MeltingFuel recipe = fluid.isEmpty() ? null : findRecipe(fluid.getFluid());
                if (isUsableFuel(recipe, fluid)){
                    int amount = recipe.getAmount(fluid.getFluid());
                    if (fluid.getAmount() >= amount)
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
                if (!player.level().isClientSide){
                    player.level().playSound(
                            null,                      // 播给周围所有玩家
                            player.getX(), player.getY(), player.getZ(),
                            DreamtinkerSounds.CHAINSAW_START.get(),
                            SoundSource.PLAYERS,
                            1.0F,                      // 音量
                            1.0F                       // 音高
                    );
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
        // Combat, resource mutations and authoritative particles must only run on the server.
        if (!(entity.level() instanceof ServerLevel level))
            return;
        if (tool.isBroken()){
            entity.stopUsingItem();
            return;
        }

        ModDataNBT dataNBT = tool.getPersistentData();
        int mode = dataNBT.getInt(TAG_MOD);
        int rotation = dataNBT.getInt(TAG_ROTATIONAL_FORCE);
        float heat = dataNBT.getFloat(TAG_HEAT);
        float maxHeat = maxHeat(mode);
        if (heat > 0.9F * maxHeat){
            entity.stopUsingItem();
            return;
        }

        AttributeInstance reach = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
        double range = null != reach ? reach.getValue() : 2.5D;
        Vec3 srcVec = entity.getEyePosition();
        Vec3 movement = entity.getLookAngle().scale(range);
        Vec3 destVec = srcVec.add(movement);
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class, entity.getBoundingBox().expandTowards(movement).inflate(1.0D),
                victim -> {
                    if (victim == entity || !victim.isAlive())
                        return false;
                    AABB collisionBB = victim.getBoundingBox().inflate(0.5D);
                    return collisionBB.contains(srcVec) || collisionBB.clip(srcVec, destVec).isPresent();
                }
        );
        targets.sort(Comparator.comparingDouble(entity::distanceToSqr));

        boolean usesFuel = mode != Modes.ELECTRIC.ordinal();
        boolean usesElectricity = mode != Modes.FUEL.ordinal();
        int cost = Math.max(0, ChainSawEnergyCost.get());
        int energy = Math.max(0, ToolEnergyCapability.getEnergy(tool));
        int available = Math.max(1, targets.size()); // Retain the existing idle/warm-up running cost.
        if (usesElectricity && cost > 0)
            available = Math.min(available, energy / cost);
        if (available == 0){
            entity.stopUsingItem();
            return;
        }

        FuelState fuel = usesFuel ? prepareFuel(tool, available) : new FuelState(0, 1, 0);
        if (usesFuel)
            available = Math.min(available, fuel.duration() / fuel.rate());
        if (available == 0){
            entity.stopUsingItem();
            return;
        }

        if (usesFuel){
            rotation = (int) Math.min(maxForce(mode), (long) Math.max(0, rotation) + fuel.rate());
            heat += fuel.temperature() / 10000.0F;
        }
        if (usesElectricity){
            rotation = maxForce(mode);
            heat += level.random.nextFloat();
        }
        dataNBT.putInt(TAG_ROTATIONAL_FORCE, rotation);
        dataNBT.putFloat(TAG_HEAT, Math.min(heat, maxHeat));

        int usedUnits = 1;
        int hits = 0;
        if (rotation >= MAX_FORCE_FUEL / 2 && heat <= 0.9F * maxHeat){
            for (LivingEntity victim : targets) {
                if (hits >= available || tool.isBroken())
                    break;
                if (!victim.isAlive())
                    continue;
                usedUnits = Math.max(1, ++hits);
                ToolAttackUtil.performAttack(tool, ToolAttackContext.attacker(entity).target(victim).cooldown(1).applyAttributes().build());

                heat += level.random.nextFloat() / 5F;
                AttributeInstance armor = victim.getAttribute(Attributes.ARMOR);
                if (armor != null)
                    heat += (float) (armor.getValue() * 0.01F);
                int coolingCost = Math.round(cost * 0.5F);
                if (mode == Modes.FUEL.ordinal() && energy >= coolingCost && heat > 0.7F * maxHeat){
                    energy -= coolingCost;
                    heat -= level.random.nextFloat() / 5F;
                }
                if (usesElectricity)
                    heat += level.random.nextFloat() / 5F;

                dataNBT.putFloat(TAG_HEAT, Math.min(heat, maxHeat));
                spawnHitParticles(level, victim, heat / maxHeat, (float) rotation / maxForce(mode));
                if (heat > 0.9F * maxHeat || !entity.isUsingItem())
                    break;
            }
        }

        // Charge only the work actually done (one idle unit, or the number of attempted hits).
        // In MIX mode both resources must fund every unit; integer division prevents overspending.
        if (usesFuel)
            dataNBT.putInt(TAG_FUEL_DURATION, fuel.duration() - usedUnits * fuel.rate());
        if (usesElectricity)
            energy -= usedUnits * cost;
        ToolEnergyCapability.setEnergy(tool, energy);
        dataNBT.putFloat(TAG_HEAT, Math.min(heat, maxHeat));
        if (heat > 0.9F * maxHeat)
            entity.stopUsingItem();
    }

    private void spawnHitParticles(ServerLevel level, LivingEntity victim, float heatRatio, float forceRatio) {
        double dist = 1.0D + level.random.nextFloat() * 0.2D;
        double vx = (level.random.nextFloat() - 0.5D + victim.getDeltaMovement().x) * dist;
        double vy = (level.random.nextFloat() - 0.5D + victim.getDeltaMovement().y) * dist;
        double vz = (level.random.nextFloat() - 0.5D + victim.getDeltaMovement().z) * dist;
        SimpleParticleType temp = heatRatio > 0.5F ? ParticleTypes.LAVA : ParticleTypes.FLAME;
        SimpleParticleType sharp = forceRatio > 0.5F ? ParticleTypes.ELECTRIC_SPARK : ParticleTypes.SCRAPE;
        level.sendParticles(temp, victim.getX(), victim.getEyeY() - 0.1D, victim.getZ(), 0, vx, vy, vz, 1.0D);
        level.sendParticles(sharp, victim.getX(), victim.getEyeY() - 0.1D, victim.getZ(), 0, vx, vy, vz, 1.0D);
    }

    @Override
    public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
        if (entity.level().isClientSide)
            return;
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
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
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
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = ETHelper.getLivingTarget(context.getTarget());
        if (null != target && !target.level().isClientSide){//always clear this
            Tier tier = tool.getStats().get(ToolStats.HARVEST_TIER);
            int idx = tiers.indexOf(tier);
            target.invulnerableTime -= 2 * (idx + 1);
        }
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (!context.getAttacker().isUsingItem())
            return damage;
        ModDataNBT dataNBT = tool.getPersistentData();
        int mode = dataNBT.getInt(TAG_MOD);
        int rotation = dataNBT.getInt(TAG_ROTATIONAL_FORCE);
        float heat = dataNBT.getFloat(TAG_HEAT) / maxHeat(mode);
        damage += damage * ((float) rotation / MAX_FORCE_FUEL - .8f);
        damage *= computeHeatEfficiencyFromPercent(heat);
        if (Modes.ELECTRIC.ordinal() != mode){
            int burningRate = dataNBT.getInt(TAG_FUEL_RATE);
            if (dataNBT.getInt(TAG_FUEL_DURATION) > 0 && burningRate > 0)
                return damage * (1.0F + burningRate / 100.0F);
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
            float max_heat = maxHeat(mod);
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

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }
}
