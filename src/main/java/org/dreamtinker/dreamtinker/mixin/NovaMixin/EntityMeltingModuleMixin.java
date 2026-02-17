package org.dreamtinker.dreamtinker.mixin.NovaMixin;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.smeltery.block.entity.module.EntityMeltingModule;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static org.dreamtinker.dreamtinker.Dreamtinker.configCompactDisabled;
import static slimeknights.tconstruct.smeltery.block.entity.module.EntityMeltingModule.getDefaultFluid;

@Mixin(value = EntityMeltingModule.class, remap = false)
public abstract class EntityMeltingModuleMixin {
    @Shadow
    protected abstract Level getLevel();

    @Shadow
    protected abstract EntityMeltingRecipe findRecipe(EntityType<?> type);

    @Shadow
    protected abstract boolean canMeltEntity(LivingEntity entity);

    @Shadow
    protected abstract DamageSource smelteryMagic();

    @Shadow
    protected abstract DamageSource smelteryHeat();

    @Shadow
    @Final
    private Supplier<AABB> bounds;

    @Shadow
    @Final
    private BooleanSupplier canMeltEntities;
    @Shadow
    @Final
    private IFluidHandler tank;

    @Inject(method = "interactWithEntities", at = @At("TAIL"), cancellable = true)
    public void dreamtinker$interactWithEntities(CallbackInfoReturnable<Boolean> cir) {
        if (ModList.get().isLoaded("ars_nouveau") && configCompactDisabled("ars_nouveau"))
            return;
        int minX = Mth.floor(bounds.get().minX);
        int minY = Mth.floor(bounds.get().minY);
        int minZ = Mth.floor(bounds.get().minZ);
        int maxX = Mth.floor(bounds.get().maxX);
        int maxY = Mth.floor(bounds.get().maxY);
        int maxZ = Mth.floor(bounds.get().maxZ);

        Boolean canMelt = null;
        boolean melted = false;
        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            BlockEntity be = getLevel().getBlockEntity(pos);
            if (be instanceof MobJarTile tile && tile.getEntity() instanceof LivingEntity le){
                Entity entity = tile.getEntity();
                EntityType<?> type = entity.getType();
                if (canMelt != Boolean.FALSE && !type.is(TinkerTags.EntityTypes.MELTING_HIDE) && canMeltEntity(le)){
                    // only fetch boolean once, its not the fastest as it tries to consume fuel
                    if (canMelt == null)
                        canMelt = canMeltEntities.getAsBoolean();

                    // ensure we have fuel/any other needed smeltery states
                    if (canMelt){
                        // determine what we are melting
                        FluidStack fluid;
                        int damage;
                        EntityMeltingRecipe recipe = findRecipe(entity.getType());
                        if (recipe != null){
                            fluid = recipe.getOutput((LivingEntity) entity);
                            damage = recipe.getDamage();
                        }else {
                            fluid = getDefaultFluid();
                            damage = 2;
                        }

                        // if the entity is successfully damaged, fill the tank with fluid
                        if (damage < le.getHealth() && entity.hurt(entity.fireImmune() ? smelteryMagic() : smelteryHeat(), damage)){
                            // its fine if we don't fill it all, leftover fluid is just lost
                            tank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                            le.hurtTime = 0;
                            le.hurtDuration = 0;
                            tile.setEntityData(le);
                            melted = true;
                        }
                    }
                }
            }
        }
        if (melted)
            cir.setReturnValue(true);
    }

}
