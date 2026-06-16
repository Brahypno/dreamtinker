package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.ars;

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
import org.brahypno.dreamtinker.library.compact.ars_nouveau.NovaRegistry;
import org.brahypno.dreamtinker.tools.items.UnderArmorItem;
import org.brahypno.dreamtinker.utils.CompactUtils.arsNovaUtils;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.SORCERER_BOOTS;
import static org.brahypno.esotericismtinker.EsotericismTinker.configCompactDisabled;

public class NovaMagicArmor extends Modifier implements InventoryTickModifierHook {
    @SuppressWarnings({"removal"})
    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
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
            if (stack.is(TinkerTags.Items.ARMOR) && 0 < ETModifierCheck.getItemModifierNum(stack, NovaRegistry.nova_magic_armor.getId())){
                if (!(stack.getItem() instanceof UnderArmorItem) && null != PerkRegistry.getPerkProvider(stack.getItem()))
                    return;//They would have their own ones.
                EquipmentSlot slot = event.getSlotType();
                if (slot.isArmor()){
                    for (Map.Entry<Attribute, AttributeModifier> e : arsNovaUtils.getAttributeModifiers(slot, stack, armorItem.getType()).entries()) {
                        Attribute attr = e.getKey();
                        AttributeModifier mod = e.getValue();
                        AttributeModifier scaled = new AttributeModifier(
                                mod.getId(), this.getTranslationKey(), mod.getAmount(), mod.getOperation()
                        );
                        event.addModifier(attr, scaled);
                    }
                }
            }
        }

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        super.registerHooks(hookBuilder);
    }
}
