package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.library.utils.RomanNumeralHelper;

import java.util.Arrays;
import java.util.List;

public class MalumSolTiferet extends BattleModifier {
    @Override
    public boolean isNoLevels() {return false;}

    @Override
    public @NotNull Component getDisplayName(int level) {
        final String fulfill = this.getTranslationKey() + "_fulfill";
        return Component.translatable(fulfill).append(" ").append(RomanNumeralHelper.getNumeral(level))
                        .withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(this.getTranslationKey())));
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        final String fulfill = this.getTranslationKey() + "_fulfill";
        return Arrays.asList(Component.translatable(fulfill + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(fulfill + ".description").withStyle(ChatFormatting.GRAY));
    }
}
