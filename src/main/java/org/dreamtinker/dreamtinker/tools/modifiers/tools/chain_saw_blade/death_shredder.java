package org.dreamtinker.dreamtinker.tools.modifiers.tools.chain_saw_blade;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;
import static slimeknights.tconstruct.library.tools.helper.ToolAttackUtil.NO_COOLDOWN;

public class death_shredder extends BattleModifier {

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
        return 72000;//Shouldn`t be this long, will see
    }

    @Override
    public @NotNull UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
        return BlockingModifier.blockWhileCharging(tool, UseAnim.BOW);
    }

    @Override
    public @NotNull InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK){
            FluidStack fluid = TANK_HELPER.getFluid(tool);
            if (fluid.isEmpty())
                return InteractionResult.PASS;
            MeltingFuel recipe = findRecipe(fluid.getFluid());
            if (null != recipe){//life can transform to blood soul, and we leave 1 HP(half heart)
                int amount = recipe.getAmount(fluid.getFluid());
                if (fluid.getAmount() >= amount){
                    GeneralInteractionModifierHook.startUsingWithDrawtime(tool, modifier.getId(), player, hand, 1.5f);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
        double range = 2.5D;
        Vec3 srcVec = entity.getEyePosition();
        Vec3 lookVec = entity.getViewVector(1.0F);
        Vec3 destVec = srcVec.add(lookVec.x() * range, lookVec.y() * range, lookVec.z() * range);
        float var9 = 1.0F;
        List<Entity> possibleList = entity.level().getEntities(entity, entity.getBoundingBox()
                                                                             .expandTowards(lookVec.x() * range, lookVec.y() * range, lookVec.z() * range)
                                                                             .inflate(var9, var9, var9));

        boolean flag = false;
        for (Entity victim : possibleList) {
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
                    ToolAttackUtil.attackEntity(tool, entity, InteractionHand.MAIN_HAND, victim, NO_COOLDOWN, false);
                    double d0 = (entity.level().getRandom().nextFloat() - 0.5F) + victim.getDeltaMovement().x;
                    double d1 = (entity.level().getRandom().nextFloat() - 0.5F) + victim.getDeltaMovement().y;
                    double d2 = (entity.level().getRandom().nextFloat() - 0.5F) + victim.getDeltaMovement().z;
                    double dist = 1F + entity.level().getRandom().nextFloat() * 0.2F;
                    double d3 = d0 * dist;
                    double d4 = d1 * dist;
                    double d5 = d2 * dist;
                    victim.level().addParticle(ParticleTypes.LAVA, victim.getX(), victim.getEyeY() - 0.1D + (victim.getEyePosition().y - victim.getEyeY()),
                                               victim.getZ(), d3, d4, d5);
                }
            }
        }
    }

}
