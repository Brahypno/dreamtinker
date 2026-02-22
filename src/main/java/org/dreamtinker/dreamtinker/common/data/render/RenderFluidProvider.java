package org.dreamtinker.dreamtinker.common.data.render;

import net.minecraft.data.PackOutput;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.smeltery.DreamTinkerSmeltery;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.data.datamap.BlockStateDataMapProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;

import java.util.List;

public class RenderFluidProvider extends BlockStateDataMapProvider<List<FluidCuboid>> {
    public RenderFluidProvider(PackOutput output) {
        super(output, PackOutput.Target.RESOURCE_PACK, FluidCuboid.REGISTRY, Dreamtinker.MODID);
    }

    @Override
    protected void addEntries() {
        // tanks
        String tank = "templates/tank";
        /*
        entry(TConstruct.getResource(tank), List.of(
                FluidCuboid.builder()
                           .from(0.08f, 0.08f, 0.08f)
                           .to(15.92f, 15.92f, 15.92f)
                           .build()));

         */
        for (SearedTankBlock.TankType type : SearedTankBlock.TankType.values()) {
            block(DreamTinkerSmeltery.ashenTank.get(type)).variant(TConstruct.getResource(tank));
        }
    }

    @Override
    public String getName() {
        return "Dream Tinkers' block render fluid provider";
    }
}
