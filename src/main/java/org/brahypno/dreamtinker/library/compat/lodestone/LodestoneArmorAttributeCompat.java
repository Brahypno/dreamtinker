package org.brahypno.dreamtinker.library.compat.lodestone;

import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.registries.ForgeRegistries;
import team.lodestar.lodestone.systems.item.LodestoneArmorItem;

/**
 * Keeps Lodestone's armor class out of the module's unconditional loader registration.
 */
public final class LodestoneArmorAttributeCompat {
    private LodestoneArmorAttributeCompat() {}

    public static Multimap<Attribute, AttributeModifier> sourceAttributes(ResourceLocation armor, ArmorItem.Type type) {
        return ((LodestoneArmorItem) ForgeRegistries.ITEMS.getValue(armor)).createExtraAttributes(type);
    }
}
