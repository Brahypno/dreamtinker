package org.dreamtinker.dreamtinker.register;

import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;

public class DreamtinkerFluid {

    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(MODID);

    public static FluidType.Properties createFluidType(String name, int temperature, int lightLevel,int viscosity, int density) {
        return FluidType.Properties.create()
                .temperature(temperature) // 设置流体的温度
                .lightLevel(lightLevel)    // 设置流体的亮度
                .viscosity(viscosity)      // 设置流体的粘度
                .density(density);         // 设置流体的密度
    }

    private static FlowingFluidObject<ForgeFlowingFluid> registerFluid(String name, int temp, int viscosity, int density, int lightLevel, Material material) {
         return FLUIDS.register(name).type(createFluidType(name,temp,lightLevel,viscosity,density)).block(material, lightLevel).bucket().flowing();
    }

    public static final FluidObject<ForgeFlowingFluid> molten_echo_shard = registerFluid("molten_echo_shard", 1500,2000,10000,0,Material.LAVA);

}
