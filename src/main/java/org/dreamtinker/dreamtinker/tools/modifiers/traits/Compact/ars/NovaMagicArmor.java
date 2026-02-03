package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.ars;

import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.fml.ModList;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaRegistry;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.dreamtinker.dreamtinker.tools.items.UnderArmorItem;
import org.dreamtinker.dreamtinker.utils.CompactUtils.arsNovaUtils;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.SORCERER_BOOTS;
import static org.dreamtinker.dreamtinker.Dreamtinker.configCompactDisabled;

public class NovaMagicArmor extends ArmorModifier {
    @SuppressWarnings({"removal"})
    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (holder instanceof Player player)
            SORCERER_BOOTS.asItem().onArmorTick(stack, world, player);
    }

    {
        MinecraftForge.EVENT_BUS.addListener(this::NovaWearAttributes);
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

    private void NovaWearAttributes(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof ArmorItem armorItem && ModList.get().isLoaded("ars_nouveau") && !configCompactDisabled("ars_nouveau")){
            if (stack.is(TinkerTags.Items.ARMOR) && 0 < DTModifierCheck.getItemModifierNum(stack, NovaRegistry.nova_magic_armor.getId())){
                if (!(stack.getItem() instanceof UnderArmorItem) && null != PerkRegistry.getPerkProvider(stack.getItem()))
                    return;//They would have their own ones.
                EquipmentSlot slot = event.getSlotType();
                if (slot.isArmor()){
                    for (Map.Entry<Attribute, AttributeModifier> e : arsNovaUtils.getAttributeModifiers(slot, stack, armorItem.getType()).entries()) {
                        Attribute attr = e.getKey();
                        AttributeModifier mod = e.getValue();
                        AttributeModifier scaled = new AttributeModifier(
                                mod.getId(), mod.getName(), mod.getAmount(), mod.getOperation()
                        );
                        event.addModifier(attr, scaled);
                    }
                }
            }
        }

    }
}
