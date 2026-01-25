package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.ars;

import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.CasterCapability;
import org.dreamtinker.dreamtinker.utils.DTMessages;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class NovaCastTool extends NoLevelsModifier implements SlotStackModifierHook, TooltipModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.SLOT_STACK, ModifierHooks.TOOLTIP);
    }

    @Override
    public boolean overrideStackedOnOther(IToolStackView heldTool, ModifierEntry modifier, Slot slot, Player player) {
        ItemStack stack = slot.getItem();
        if (stack.getItem() instanceof SpellParchment){
            ISpellCaster caster = ((SpellParchment) ItemsRegistry.SPELL_PARCHMENT.asItem()).getSpellCaster(stack);
            updateToolSpell(heldTool, caster.getSpell(), player);
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(IToolStackView slotTool, ModifierEntry modifier, ItemStack held, Slot slot, Player player, SlotAccess access) {
        if (held.getItem() instanceof SpellParchment){
            ISpellCaster caster = ((SpellParchment) ItemsRegistry.SPELL_PARCHMENT.asItem()).getSpellCaster(held);
            updateToolSpell(slotTool, caster.getSpell(), player);
        }
        return false;
    }

    private void updateToolSpell(IToolStackView heldTool, Spell spell, Player player) {
        if (spell.recipe.stream().noneMatch(s -> s instanceof AbstractCastMethod)){
            ISpellCaster caster = CasterCapability.getSpellCaster(heldTool);
            caster.setSpell(spell);
            if (!player.level().isClientSide)
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.alert.spell_set"));
            else
                DTMessages.clientChat(Component.translatable("ars_nouveau.alert.spell_set").withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), false);
        }else {
            if (!player.level().isClientSide)
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.sword.invalid"));
            else
                DTMessages.clientChat(Component.translatable("ars_nouveau.sword.invalid").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)), false);
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            ISpellCaster caster = CasterCapability.getSpellCaster(tool);
            if (caster.getSpell().isEmpty()){
                tooltip.add(Component.translatable("modifier.dreamtinker.nova_cast_tool.description"));
                return;
            }
            if (!caster.getSpellName().isEmpty()){
                tooltip.add(Component.literal(caster.getSpellName()));
            }
            if (caster.isSpellHidden()){
                tooltip.add(Component.literal(caster.getHiddenRecipe()).withStyle(
                        Style.EMPTY.withFont(new ResourceLocation("minecraft", "alt")).withColor(ChatFormatting.GOLD)));
            }else {
                Spell spell = caster.getSpell();
                tooltip.add(Component.translatable("modifier.dreamtinker.nova_cast_tool")
                                     .append(": ")
                                     .append(Component.literal(spell.getDisplayString()))
                                     .withStyle(this.getDisplayName().getStyle()));
            }
            if (!caster.getFlavorText().isEmpty())
                tooltip.add(Component.literal(caster.getFlavorText()).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE)));
        }
    }
}
