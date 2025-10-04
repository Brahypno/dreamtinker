package org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.entity.NarcissusFluidProjectile;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableLauncherItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

public class memoryBase extends BattleModifier {
    private final Fluid fallback_fluid = DreamtinkerFluids.blood_soul.get();

    @Override
    public @NotNull Component getDisplayName(@NotNull IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
        if (0 < tool.getModifierLevel(DreamtinkerModifiers.flaming_memory.getId()))
            return Component.translatable(this.getTranslationKey()).withStyle((style) -> style.withColor(this.getTextColor()).withStrikethrough(true));
        return entry.getDisplayName();
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        super.registerHooks(builder);
        builder.addModule(ToolTankHelper.TANK_HANDLER);
        builder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
    }

    @Override
    public int getPriority() {
        return 2700; // my custom splt, so should be earlier enough
    }

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
        return 72000;
    }

    @Override
    public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
        return BlockingModifier.blockWhileCharging(tool, UseAnim.BOW);
    }

    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK && tool.getModifierLevel(DreamtinkerModifiers.flaming_memory.getId()) < 1){
            // launch if the fluid has effects, cannot simulate as we don't know the target yet
            FluidStack fluid = TANK_HELPER.getFluid(tool);
            if (fluid.isEmpty())
                fluid = new FluidStack(fallback_fluid, (int) (player.getAbilities().instabuild ? Integer.MAX_VALUE : Math.max(0, player.getHealth() - 1) * 5));
            if (ForgeRegistries.FLUIDS.getHolder(fluid.getFluid()).map(h -> h.is(DreamtinkerTagkeys.Fluids.narcissus_wing_used)).orElse(false) &&
                FluidEffectManager.INSTANCE.find(fluid.getFluid()).hasEffects() &&
                1 + 2 * (modifier.getLevel() - 1) < fluid.getAmount()){//life can transform to blood soul, and we leave 1 HP(half heart)
                GeneralInteractionModifierHook.startUsingWithDrawtime(tool, modifier.getId(), player, hand, 1.5f);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
        Level world = entity.level();
        if (!world.isClientSide){
            int chargeTime = getUseDuration(tool, modifier) - timeLeft;
            if (chargeTime > 0){
                // find the fluid to spit
                float possible_amount = (int) (entity instanceof Player player && player.getAbilities().instabuild ? Integer.MAX_VALUE :
                                               Math.max(0, entity.getHealth() - 1) * 5);
                FluidStack fluid = TANK_HELPER.getFluid(tool);
                float original_amount = fluid.getAmount();
                if (fluid.isEmpty())
                    fluid = new FluidStack(fallback_fluid, (int) possible_amount);
                FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
                if (recipe.hasEffects()){
                    // projectile stats
                    float charge = GeneralInteractionModifierHook.getToolCharge(tool, chargeTime);
                    // power - size of each individual projectile
                    float power = charge * ConditionalStatModifierHook.getModifiedStat(tool, entity, ToolStats.PROJECTILE_DAMAGE);
                    // level acts like multishot level, meaning higher produces more projectiles
                    int level = modifier.intEffectiveLevel();
                    // amount is the amount per projectile, total cost is amount times level (every other shot is free)
                    // if its 0, that means we have only a couple mb left
                    int max_possible_amount = (int) (fluid.getAmount() + ((fluid.getFluid().equals(fallback_fluid)) ? 0 : possible_amount));
                    int amount = Math.min(max_possible_amount, (int) (recipe.getAmount(fluid.getFluid()) * power) * level) / level;
                    if (amount > 0){
                        // other stats now that we know we are shooting
                        // velocity determines how far it goes, does not impact damage unlike bows
                        float velocity = ConditionalStatModifierHook.getModifiedStat(tool, entity, ToolStats.VELOCITY) * charge * 3.0f;
                        float inaccuracy = ModifierUtil.getInaccuracy(tool, entity);

                        // multishot stuff
                        int shots = 1 + 2 * (level - 1);
                        float startAngle = ModifiableLauncherItem.getAngleStart(shots);
                        int primaryIndex = shots / 2;
                        for (int shotIndex = 0; shotIndex < shots; shotIndex++) {
                            NarcissusFluidProjectile spit = new NarcissusFluidProjectile(world, entity, new FluidStack(fluid, amount), power, tool);

                            // setup projectile target
                            Vec3 upVector = entity.getUpVector(1.0f);
                            float angle = startAngle + (10 * shotIndex);
                            Vector3f targetVector = entity.getViewVector(1.0f).toVector3f()
                                                          .rotate((new Quaternionf()).setAngleAxis(angle * Math.PI / 180F, upVector.x, upVector.y,
                                                                                                   upVector.z));
                            spit.shoot(targetVector.x(), targetVector.y(), targetVector.z(), velocity, inaccuracy);

                            // store all modifiers on the spit
                            spit.getCapability(EntityModifierCapability.CAPABILITY).ifPresent(cap -> cap.setModifiers(tool.getModifiers()));

                            // fetch the persistent data for the arrow as modifiers may want to store data
                            ModDataNBT arrowData = PersistentDataCapability.getOrWarn(spit);
                            // let modifiers set properties
                            for (ModifierEntry entry : tool.getModifierList()) {
                                entry.getHook(ModifierHooks.PROJECTILE_LAUNCH)
                                     .onProjectileLaunch(tool, entry, entity, ItemStack.EMPTY, spit, null, arrowData, shotIndex == primaryIndex);
                            }

                            // finally, fire the projectile
                            world.addFreshEntity(spit);
                            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS, 1.0F,
                                            1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + charge * 0.5F + (angle / 10f));

                        }

                        // consume the fluid and durability
                        if (!(entity instanceof Player p && p.getAbilities().instabuild)){
                            if (original_amount < amount * level){//That means Use Blood to pay
                                entity.setHealth(Math.max(1, entity.getHealth() - (amount * level - original_amount) / 5.0f));
                            }
                            fluid.setAmount((int) original_amount);
                            fluid.shrink(amount * level);
                            TANK_HELPER.setFluid(tool, fluid);
                        }

                        ToolDamageUtil.damageAnimated(tool, shots, entity, entity.getUsedItemHand());
                    }
                }
            }

        }
    }
}
