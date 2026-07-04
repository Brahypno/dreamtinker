package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.ars;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;

import java.util.Arrays;
import java.util.List;

public class NovaAshenResolve extends NoLevelsModifier {
    @Override
    public @NotNull Component getDisplayName(int level) {
        final String fulfill = this.getTranslationKey() + "_fulfill";
        return Component.translatable(fulfill).append(" ")
                        .withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(this.getTranslationKey())));
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        final String fulfill = this.getTranslationKey() + "_fulfill";
        return Arrays.asList(Component.translatable(fulfill + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(fulfill + ".description").withStyle(ChatFormatting.GRAY));
    }
}
