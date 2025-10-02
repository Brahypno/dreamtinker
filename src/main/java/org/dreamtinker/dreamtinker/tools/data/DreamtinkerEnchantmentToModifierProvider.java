package org.dreamtinker.dreamtinker.tools.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractEnchantmentToModifierProvider;

public class DreamtinkerEnchantmentToModifierProvider extends AbstractEnchantmentToModifierProvider {
    public DreamtinkerEnchantmentToModifierProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addEnchantmentMappings() {
        addOptional(new ResourceLocation("malum", "rebound"), DreamtinkerModifiers.Ids.malum_rebound, true);
        addOptional(new ResourceLocation("malum", "ascension"), DreamtinkerModifiers.Ids.malum_ascension, true);
        addOptional(new ResourceLocation("malum", "animated"), DreamtinkerModifiers.Ids.malum_animated, true);
        addOptional(new ResourceLocation("malum", "replenishing"), DreamtinkerModifiers.Ids.malum_replenishing, true);
        addOptional(new ResourceLocation("malum", "haunted"), DreamtinkerModifiers.Ids.malum_haunted, true);
        addOptional(new ResourceLocation("malum", "spirit_plunder"), DreamtinkerModifiers.Ids.malum_spirit_plunder, true);

    }

    @Override
    public @NotNull String getName() {
        return "Dream Tinker Enchantment To Modifier";
    }
}
