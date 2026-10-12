package org.brahypno.dreamtinker.library.modifiers.modules.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.library.compat.lodestone.LodestoneArmorAttributeCompat;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.DoubleLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Copies a Lodestone armor item's extra attributes, including runtime integration additions.
 * Optionally trades armor for extra modifier levels beyond the first.
 */
public record LodestoneArmorAttributesModule(ResourceLocation armor, double armorPenaltyPerLevel)
        implements ModifierModule, AttributesModifierHook, ToolStatsModifierHook {
    public static final RecordLoadable<LodestoneArmorAttributesModule> LOADER = RecordLoadable.create(
            Loadables.RESOURCE_LOCATION.requiredField("armor", LodestoneArmorAttributesModule::armor),
            DoubleLoadable.FROM_ZERO.defaultField("armor_penalty_per_level", 0d, LodestoneArmorAttributesModule::armorPenaltyPerLevel),
            LodestoneArmorAttributesModule::new);
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.defaultHooks(ModifierHooks.ATTRIBUTES, ModifierHooks.TOOL_STATS);

    private Multimap<Attribute, AttributeModifier> sourceAttributes(ArmorItem.Type type) {
        return ForgeRegistries.ITEMS.containsKey(armor)
               ? LodestoneArmorAttributeCompat.sourceAttributes(armor, type) : ImmutableMultimap.of();
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        ArmorItem.Type type = switch (slot) {
            case HEAD -> ArmorItem.Type.HELMET;
            case CHEST -> ArmorItem.Type.CHESTPLATE;
            case LEGS -> ArmorItem.Type.LEGGINGS;
            case FEET -> ArmorItem.Type.BOOTS;
            default -> null;
        };
        if (type != null){
            for (var entry : sourceAttributes(type).entries()) {
                Attribute attribute = entry.getKey();
                AttributeModifier source = entry.getValue();
                consumer.accept(attribute, new AttributeModifier(
                        UUID.nameUUIDFromBytes((slot.getName() + "." + modifier.getId() + "." + attribute.getDescriptionId()).getBytes()),
                        modifier.getModifier().getTranslationKey(), source.getAmount() * modifier.getLevel(), source.getOperation()));
            }
        }
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        // This is an addition based on the already-built stat, not a base-stat multiplier.
        ToolStats.ARMOR.add(builder, -armorPenaltyPerLevel * builder.getStat(ToolStats.ARMOR) * (modifier.getLevel() - 1));
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<LodestoneArmorAttributesModule> getLoader() {
        return LOADER;
    }
}
