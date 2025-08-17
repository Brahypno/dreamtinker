package org.dreamtinker.dreamtinker.register;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.fluids.block.BurningLiquidBlock;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;

public class DreamtinkerFluid {

    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(MODID);

    public static FluidType.Properties createFluidType(int temperature, int lightLevel, int viscosity, int density) {
        return FluidType.Properties.create().temperature(temperature) // 设置流体的温度
                .lightLevel(lightLevel)    // 设置流体的亮度
                .viscosity(viscosity)      // 设置流体的粘度
                .density(density)         // 设置流体的密度
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA) //Does there real difference between FILL WATER and LAVA? I dont know
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
    }

    private static FlowingFluidObject<ForgeFlowingFluid> registerFluid(String name, int temp, int viscosity, int density, int lightLevel, Function<Supplier<? extends FlowingFluid>, LiquidBlock> blockFunction) {
        return FLUIDS.register(name).type(createFluidType(temp, lightLevel, viscosity, density)).block(blockFunction).bucket().flowing();
    }

    public static final FluidObject<ForgeFlowingFluid> molten_echo_shard = registerFluid("molten_echo_shard", 1500, 2000, 10000, 0, supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 0, 8) {
        @Override
        public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
            super.entityInside(state, level, pos, entity);
            if (entity instanceof LivingEntity living){
                living.addEffect(new MobEffectInstance(MobEffects.DARKNESS));
                living.addEffect(new MobEffectInstance(DreamtinkerEffect.RealDarkness.get()));
            }
        }
    });
    public static final FluidObject<ForgeFlowingFluid> molten_nigrescence_antimony = registerFluid("molten_nigrescence_antimony", 600, 5000, 6666, 0, supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 0, 16) {
        @Override
        public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
            super.entityInside(state, level, pos, entity);
            if (entity instanceof LivingEntity living){
                living.addEffect(new MobEffectInstance(MobEffects.HARM, 10, 2));
            }
        }
    });
    public static final FluidObject<ForgeFlowingFluid> molten_albedo_stibium = registerFluid("molten_albedo_stibium", 600, 1000, 3190, 15, supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_LIGHT_GRAY, 15), 0, 8) {
        @Override
        public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
            super.entityInside(state, level, pos, entity);
            if (entity instanceof LivingEntity living)
                living.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION));

        }
    });
    public static final FluidObject<ForgeFlowingFluid> molten_lupi_antimony = registerFluid("molten_lupi_antimony", 1500, 44, 7676, 15, supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 30000, 8) {
        @Override
        public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
            super.entityInside(state, level, pos, entity);
            if (entity instanceof LivingEntity living)
                living.addEffect(new MobEffectInstance(MobEffects.HARM, 10, 3));

        }
    });
    public static final FluidObject<ForgeFlowingFluid> molten_ascending_antimony = registerFluid("molten_ascending_antimony", 600, 1000, 3190, 15, supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 10, 0) {
        @Override
        public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
            super.entityInside(state, level, pos, entity);
            if (entity instanceof LivingEntity living)
                living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST));

        }
    });
    public static final FluidObject<ForgeFlowingFluid> liquid_smoky_antimony = registerFluid("liquid_smoky_antimony", 600, 1000, 3190, 15, supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 10, 0) {
        @Override
        public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
            super.entityInside(state, level, pos, entity);
            if (entity instanceof LivingEntity living){
                living.addEffect(new MobEffectInstance(MobEffects.HEAL));
                living.addEffect(new MobEffectInstance(MobEffects.REGENERATION));
            }

        }
    });
}
