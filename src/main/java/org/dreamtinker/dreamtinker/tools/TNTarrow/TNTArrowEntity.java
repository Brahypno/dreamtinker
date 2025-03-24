package org.dreamtinker.dreamtinker.tools.TNTarrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.dreamtinker.dreamtinker.register.DreamtinkerEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TNTArrowEntity extends AbstractArrow {
    public TNTArrowEntity(EntityType<? extends AbstractArrow> type, Level world) {
        super(type, world);
    }
    private ToolStack toolStack;

    public TNTArrowEntity(Level world, LivingEntity shooter, ToolStack toolStack) {
        super(DreamtinkerEntity.TNTARROW.get(), shooter, world);
        this.toolStack = toolStack;
        System.out.println("TNT Arrow entity created and ready to fire.");
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (!this.level.isClientSide) {



            //this.discard();
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY; // 让箭矢无法被回收
    }
}

