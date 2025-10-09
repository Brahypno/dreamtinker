package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArrowInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class range_accelerator extends Modifier implements ArrowInterface {
    private @Nullable TagKey<Item> consume_itemTagKey = null;
    private @Nullable Item cunsume_item = null;

    public range_accelerator(@NotNull TagKey<Item> itemTagKey) {
        this.consume_itemTagKey = itemTagKey;
    }

    public range_accelerator(@NotNull Item item) {
        this.cunsume_item = item;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArrowInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @javax.annotation.Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter instanceof Player player && !player.level().isClientSide){
            if (null == consume_itemTagKey && null == cunsume_item)
                return;
            int needed = (int) Math.pow(modifier.getLevel(), 2);
            var inv = player.getInventory();
            for (int i = 0; i < inv.items.size(); i++) {
                ItemStack item = inv.items.get(i);
                if (item.is(cunsume_item) || item.is(consume_itemTagKey)){
                    inv.removeItem(i, Math.min(needed, item.getCount()));
                    needed -= Math.min(needed, item.getCount());
                }
                if (needed <= 0){
                    projectile.setDeltaMovement(projectile.getDeltaMovement().scale(modifier.getLevel()));
                    if (projectile instanceof AbstractArrow arr){
                        arr.setBaseDamage(arr.getBaseDamage() + .5 * modifier.getLevel());
                    }
                    return;
                }
            }
        }
    }

}
