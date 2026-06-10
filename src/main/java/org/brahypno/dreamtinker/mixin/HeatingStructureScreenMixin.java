package org.brahypno.dreamtinker.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.brahypno.dreamtinker.smeltery.block.entity.controller.TransmuteBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;
import slimeknights.tconstruct.smeltery.client.screen.HeatingStructureScreen;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiMeltingModule;
import slimeknights.tconstruct.smeltery.client.screen.module.HeatingStructureSideInventoryScreen;
import slimeknights.tconstruct.smeltery.menu.HeatingStructureContainerMenu;

import javax.annotation.Nullable;

@Mixin(value = HeatingStructureScreen.class, remap = false)
public abstract class HeatingStructureScreenMixin {
    @Shadow
    @Final
    @Mutable
    public GuiMeltingModule melting;
    @Final
    @Shadow
    @Nullable
    private HeatingStructureBlockEntity te;
    @Final
    @Shadow
    @Nullable
    private HeatingStructureSideInventoryScreen sideInventory;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void dt$replaceMelting(
            HeatingStructureContainerMenu container,
            Inventory playerInventory,
            Component title,
            CallbackInfo ci) {
        HeatingStructureScreen self = (HeatingStructureScreen) (Object) this;

        if (this.te instanceof TransmuteBlockEntity tbe && this.sideInventory != null){
            FuelModule fuelModule = this.te.getFuelModule();

            this.melting = new GuiMeltingModule(
                    self,
                    this.te.getMeltingInventory(),
                    2,
                    () -> fuelModule.getTemperature() + tbe.getHeatTemperatureBuff(),
                    this.sideInventory::shouldDrawSlot,
                    HeatingStructureScreen.BACKGROUND
            );
        }
    }
}
