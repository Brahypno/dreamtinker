package org.dreamtinker.dreamtinker.fluids;

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
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.tconstruct.fluids.block.BurningLiquidBlock;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.dreamtinker.dreamtinker.DreamtinkerModule.*;

public class DreamtinkerFluids {


    public static FluidType.Properties createFluidType(int temperature, int lightLevel, int viscosity, int density) {
        return FluidType.Properties.create().temperature(temperature) // 设置流体的温度
                                   .lightLevel(lightLevel)    // 设置流体的亮度
                                   .viscosity(viscosity)      // 设置流体的粘度
                                   .density(density)         // 设置流体的密度
                                   .sound(SoundActions.BUCKET_FILL,
                                          SoundEvents.BUCKET_FILL_LAVA) //Does there real difference between FILL WATER and LAVA? I dont know
                                   .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
    }

    private static FlowingFluidObject<ForgeFlowingFluid> registerFluid(FluidDeferredRegister register, String name, int temp, int viscosity, int density, int lightLevel, Function<Supplier<? extends FlowingFluid>, LiquidBlock> blockFunction) {
        return register.register(name).type(createFluidType(temp, lightLevel, viscosity, density)).block(blockFunction).bucket().flowing();
    }

    public static final FlowingFluidObject<ForgeFlowingFluid> molten_echo_shard =
            registerFluid(FLUIDS, "molten_echo_shard", 900, 200, 1000, 0,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 0, 8) {
                              @Override
                              public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                                  super.entityInside(state, level, pos, entity);
                                  if (entity instanceof LivingEntity living){
                                      living.addEffect(
                                              new MobEffectInstance(MobEffects.DARKNESS, 100));
                                      living.addEffect(new MobEffectInstance(
                                              DreamtinkerEffects.RealDarkness.get(), 100));
                                  }
                              }
                          });
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_echo_alloy =
            registerFluid(FLUIDS, "molten_echo_alloy", 1800, 2000, 10000, 0,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 0, 8) {
                              @Override
                              public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                                  super.entityInside(state, level, pos, entity);
                                  if (entity instanceof LivingEntity living){
                                      living.addEffect(
                                              new MobEffectInstance(MobEffects.DARKNESS, 100));
                                      living.addEffect(new MobEffectInstance(
                                              DreamtinkerEffects.RealDarkness.get(), 100));
                                  }
                              }
                          });
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_nigrescence_antimony =
            registerFluid(FLUIDS, "molten_nigrescence_antimony", 900, 5000, 6666, 0,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 0, 16) {
                              @Override
                              public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                                  super.entityInside(state, level, pos, entity);
                                  if (entity instanceof LivingEntity living){
                                      living.addEffect(
                                              new MobEffectInstance(
                                                      MobEffects.HARM,
                                                      10, 2));
                                  }
                              }
                          });
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_albedo_stibium =
            registerFluid(FLUIDS, "molten_albedo_stibium", 600, 1000, 3190, 15,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_LIGHT_GRAY, 15), 0, 8) {
                              @Override
                              public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                                  super.entityInside(state, level, pos, entity);
                                  if (entity instanceof LivingEntity living)
                                      living.addEffect(new MobEffectInstance(
                                              MobEffects.NIGHT_VISION, 100));

                              }
                          });
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_lupi_antimony =
            registerFluid(FLUIDS, "molten_lupi_antimony", 1800, 44, 7676, 15,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 30000, 8) {
                              @Override
                              public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                                  super.entityInside(state, level, pos, entity);
                                  if (entity instanceof LivingEntity living)
                                      living.addEffect(
                                              new MobEffectInstance(MobEffects.HARM,
                                                                    10, 3));

                              }
                          });
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_ascending_antimony =
            registerFluid(FLUIDS, "molten_ascending_antimony", 600, 1000, 3190, 15,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 10, 0) {
                              @Override
                              public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                                  super.entityInside(state, level, pos, entity);
                                  if (entity instanceof LivingEntity living)
                                      living.addEffect(new MobEffectInstance(
                                              MobEffects.DAMAGE_BOOST, 100));

                              }
                          });
    public static final FlowingFluidObject<ForgeFlowingFluid> liquid_smoky_antimony =
            registerFluid(FLUIDS, "liquid_smoky_antimony", 600, 1000, 3190, 15,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 10, 0) {
                              @Override
                              public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                                  super.entityInside(state, level, pos, entity);
                                  if (entity instanceof LivingEntity living){
                                      living.addEffect(
                                              new MobEffectInstance(MobEffects.HEAL, 100));
                                      living.addEffect(new MobEffectInstance(
                                              MobEffects.REGENERATION, 100));
                                  }

                              }
                          });
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_crying_obsidian =
            registerFluid(FLUIDS, "molten_crying_obsidian", 1300, 1000, 3190, 4,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 10, 0) {});

    public static final FlowingFluidObject<ForgeFlowingFluid> liquid_trist =
            registerFluid(FLUIDS, "liquid_trist", 500, 1000, 10, 4,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 10, 0) {});

    public static final FlowingFluidObject<ForgeFlowingFluid> molten_void =
            registerFluid(FLUIDS, "molten_void", 1300, 1000, 3190, 4,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_BLACK, 0), 10, 0) {});

    public static final FlowingFluidObject<ForgeFlowingFluid> unstable_liquid_aether =
            registerFluid(EL_FLUIDS, "unstable_liquid_aether", 1600, 400, 10, 15,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.SNOW, 15), 10, 0) {});
    public static final FlowingFluidObject<ForgeFlowingFluid> liquid_pure_soul =
            registerFluid(EL_FLUIDS, "liquid_pure_soul", 600, 400, 10, 15,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.LAPIS, 15), 10, 0) {});
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_nefariousness =
            registerFluid(EL_FLUIDS, "molten_nefariousness", 1200, 400, 10, 6,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_PURPLE, 9), 10, 0) {});
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_evil =
            registerFluid(EL_FLUIDS, "molten_evil", 1400, 400, 10, 6,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_PURPLE, 9), 10, 0) {});
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_soul_aether =
            registerFluid(EL_FLUIDS, "molten_soul_aether", 6000, 400, 10, 15,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.CRIMSON_STEM, 15), 10, 0) {});
    public static final FlowingFluidObject<ForgeFlowingFluid> unholy_water =
            registerFluid(EL_FLUIDS, "unholy_water", 15, 10, 1, 2,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.WATER, 15), 0, 0) {
                              @Override
                              public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                                  super.entityInside(state, level, pos, entity);
                                  if (entity instanceof LivingEntity living){
                                      living.addEffect(
                                              new MobEffectInstance(DreamtinkerEffects.unholy.get(), 1000));
                                  }

                              }
                          });
    public static final FlowingFluidObject<ForgeFlowingFluid> reversed_shadow =
            registerFluid(FLUIDS, "reversed_shadow", 1500, 300, 10, 15,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.CRIMSON_NYLIUM, 15), 20, 10) {});

    public static final FlowingFluidObject<ForgeFlowingFluid> blood_soul =
            registerFluid(FLUIDS, "blood_soul", 37, 100, 10, 7,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.CRIMSON_NYLIUM, 7), 0, 0) {});

    public static final FlowingFluidObject<ForgeFlowingFluid> molten_soul_stained_steel =
            registerFluid(MALUM_FLUIDS, "malum_molten_soul_stained_steel", 1200, 100, 10, 7,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_PURPLE, 7), 0, 0) {});
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_malignant_pewter =
            registerFluid(MALUM_FLUIDS, "malum_molten_malignant_pewter", 1800, 100, 10, 7,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_PURPLE, 7), 0, 0) {});
    public static final FlowingFluidObject<ForgeFlowingFluid> molten_malignant_gluttony =
            registerFluid(MALUM_FLUIDS, "malum_molten_malignant_gluttony", 2200, 100, 10, 7,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_LIGHT_GREEN, 7), 0, 0) {});
    public static final FlowingFluidObject<ForgeFlowingFluid> liquid_concentrated_gluttony =
            registerFluid(MALUM_FLUIDS, "liquid_concentrated_gluttony", 300, 100, 10, 7,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_GREEN, 7), 0, 0) {});

    public static final FlowingFluidObject<ForgeFlowingFluid> liquid_arcana_juice =
            registerFluid(MALUM_FLUIDS, "liquid_arcana_juice", 300, 100, 10, 7,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_PURPLE, 7), 0, 0) {});

    public static final FlowingFluidObject<ForgeFlowingFluid> liquid_amber =
            registerFluid(FLUIDS, "liquid_amber", 637, 100, 10, 7,
                          supplier -> new BurningLiquidBlock(supplier, FluidDeferredRegister.createProperties(MapColor.COLOR_YELLOW, 7), 0, 0) {});

}
