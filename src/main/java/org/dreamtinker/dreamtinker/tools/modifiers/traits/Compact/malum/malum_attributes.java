package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import com.sammy.malum.common.item.curiosities.armor.SoulHunterArmorItem;
import com.sammy.malum.registry.common.item.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RomanNumeralHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class malum_attributes extends Modifier implements AttributesModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void addAttributes(IToolStackView iToolStackView, ModifierEntry modifierEntry, EquipmentSlot equipmentSlot, BiConsumer<Attribute, AttributeModifier> biConsumer) {
        ArmorItem.Type type = armorTypeFromSlot(equipmentSlot);
        if (null != type)
            for (Map.Entry<Attribute, AttributeModifier> e :
                    ((SoulHunterArmorItem) ItemRegistry.SOUL_HUNTER_CLOAK.get()).createExtraAttributes(type).entries()) {
                Attribute attr = e.getKey();
                AttributeModifier mod = e.getValue();
                AttributeModifier scaled = new AttributeModifier(
                        mod.getId(), mod.getName(), mod.getAmount() * modifierEntry.getLevel(), mod.getOperation()
                );
                biConsumer.accept(attr, scaled);
            }
        biConsumer.accept(Attributes.ATTACK_SPEED,
                          new AttributeModifier(UUID.fromString("3f9c2b1e-1d84-4f2a-b6c9-2e6c6df3a9b1"),
                                                Attributes.ATTACK_SPEED.getDescriptionId(),
                                                -0.05 * modifierEntry.getLevel(),
                                                AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Nullable
    public static ArmorItem.Type armorTypeFromSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> ArmorItem.Type.HELMET;
            case CHEST -> ArmorItem.Type.CHESTPLATE;
            case LEGS -> ArmorItem.Type.LEGGINGS;
            case FEET -> ArmorItem.Type.BOOTS;
            default -> null; // MAINHAND / OFFHAND
        };
    }

    private final String real_key = "modifier.dreamtinker.malum_spirit_attributes_fulfill";

    @Override
    public @NotNull Component getDisplayName(int level) {
        return Component.translatable(real_key).append(" ").append(RomanNumeralHelper.getNumeral(level))
                        .withStyle((style) -> style.withColor(this.getTextColor()));
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        return Arrays.asList(Component.translatable(real_key + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(real_key + ".description").withStyle(ChatFormatting.GRAY));
    }
}