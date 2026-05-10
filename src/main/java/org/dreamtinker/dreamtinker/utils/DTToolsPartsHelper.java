package org.dreamtinker.dreamtinker.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipBuilder;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTToolsPartsHelper {
    private static final Map<MaterialStatsId, List<ToolPartItem>> CACHE = new HashMap<>();

    public static List<ToolPartItem> getPartList(MaterialStatsId statsId) {
        return CACHE.computeIfAbsent(statsId, DTToolsPartsHelper::scanParts);
    }

    private static List<ToolPartItem> scanParts(MaterialStatsId statsId) {
        return ForgeRegistries.ITEMS.getValues().stream()
                                    .filter(item -> item instanceof ToolPartItem part
                                                    && part.getStatType() == statsId)
                                    .map(item -> (ToolPartItem) item)
                                    .toList();
    }

    public static ItemStack getPart(MaterialId id, MaterialStatsId statsId, @Nullable RandomSource rdm) {
        if (!MaterialRegistry.isFullyLoaded())
            return ItemStack.EMPTY;
        MaterialVariantId mli = MaterialRegistry.getMaterial(id).getIdentifier();

        List<ToolPartItem> Parts = getPartList(statsId);
        if (Parts.isEmpty())
            return ItemStack.EMPTY;
        ToolPartItem part = Parts.get(0);
        if (rdm != null)
            part = Parts.get(rdm.nextInt(Parts.size()));
        return part.withMaterial(mli);
    }

    public static boolean startToolInteract(Player player, EquipmentSlot slotType, TooltipKey modifierKey) {
        if (!player.isSpectator()){
            ItemStack helmet = player.getItemBySlot(slotType);
            if (helmet.is(TinkerTags.Items.MELEE) || helmet.is(TinkerTags.Items.RANGED)){
                ToolStack tool = ToolStack.from(helmet);
                for (ModifierEntry entry : tool.getModifierList()) {
                    if (entry.getHook(ModifierHooks.ARMOR_INTERACT).startInteract(tool, entry, player, slotType, modifierKey)){
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static ItemStack randomTinkerTool(TagKey<Item> itemTag, boolean exclude_from_loot, RandomSource random, @Nullable ModifierId includedId) {
        ITagManager<Item> ItemTags = ForgeRegistries.ITEMS.tags();
        if (null != ItemTags){
            List<Item> lists = ItemTags.getTag(itemTag).stream()
                                       .filter(item -> item instanceof IModifiable)
                                       .toList();
            if (lists.isEmpty())
                return ItemStack.EMPTY;
            Item tool = lists.get(random.nextInt(lists.size()));

            ToolStack ts = ToolBuildHandler.buildToolRandomMaterials((IModifiable) tool, random);
            if (null != includedId)
                ts.addModifier(includedId, 1);
            return ts.createStack();
        }
        List<Item> lists = ForgeRegistries.ITEMS.getValues().stream()
                                                // ①：有这个 tag
                                                .filter(item -> item.getDefaultInstance().is(itemTag))
                                                // ②：符合你定义的类别
                                                .filter(item -> item instanceof IModifiable)
                                                .toList();
        return ItemStack.EMPTY;
    }

    public static List<Component> getMeleeStats(IToolStackView tool, List<Component> tooltip) {
        TooltipBuilder builder = new TooltipBuilder(tool, tooltip);
        builder.addWithAttribute(ToolStats.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE);
        return builder.getTooltips();
    }

    private static boolean isMatch(ItemStack stack, ModifierNBT target, boolean compareUpgrades) {
        if (stack.isEmpty() || !stack.is(TinkerTags.Items.MODIFIABLE))
            return false;
        ToolStack tool = ToolStack.from(stack);
        ModifierNBT mine = compareUpgrades ? tool.getUpgrades() : tool.getModifiers();
        return mine.equals(target);
    }

    public static ItemStack findItemByModifierNBT(LivingEntity entity, ModifierNBT target, boolean compareUpgrades) {
        ItemStack main = entity.getMainHandItem();
        if (isMatch(main, target, compareUpgrades)){
            return main;
        }
        ItemStack off = entity.getOffhandItem();
        if (isMatch(off, target, compareUpgrades)){
            return off;
        }
        if (entity instanceof Player player)
            for (int i = 0; i < 9; i++) {
                ItemStack hot = player.getInventory().getItem(i);
                if (isMatch(hot, target, compareUpgrades)){
                    return hot;
                }
            }
        return null;
    }
}
