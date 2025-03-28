package org.dreamtinker.dreamtinker.tools.TNTarrow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
<<<<<<< HEAD
=======
import org.dreamtinker.dreamtinker.entity.TNTArrowEntity;
>>>>>>> 4b7a4cc (tnt arroe)
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;

public class TNTarrow extends ModifiableItem {

    public TNTarrow(Properties properties, ToolDefinition toolDefinition) {
        super(properties, toolDefinition);
    }

    public @NotNull AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity shooter) {
        return new TNTArrowEntity(world, shooter, stack);
    }

    public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
        return false;
    }

}
