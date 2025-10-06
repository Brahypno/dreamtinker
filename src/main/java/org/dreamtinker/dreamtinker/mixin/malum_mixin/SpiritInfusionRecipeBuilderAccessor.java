package org.dreamtinker.dreamtinker.mixin.malum_mixin;


import com.sammy.malum.data.recipe.builder.SpiritInfusionRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SpiritInfusionRecipeBuilder.class, remap = false)
public interface SpiritInfusionRecipeBuilderAccessor {
    @Accessor("output")
    ItemStack getOutput();
}

