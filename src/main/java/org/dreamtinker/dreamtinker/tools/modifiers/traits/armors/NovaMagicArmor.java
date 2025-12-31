package org.dreamtinker.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.SORCERER_BOOTS;

public class NovaMagicArmor extends ArmorModifier {
    @SuppressWarnings({"removal"})
    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (holder instanceof Player player)
            SORCERER_BOOTS.asItem().onArmorTick(stack, world, player);
    }

    public boolean isNoLevels() {return false;}

    public @NotNull Component getDisplayName(int level) {
        return this.applyStyle(Component.translatable(this.getTranslationKey() + "." + level));
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        final String fulfill = this.getTranslationKey() + "_fulfill";
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(fulfill + ".description").withStyle(ChatFormatting.GRAY));
    }

}
