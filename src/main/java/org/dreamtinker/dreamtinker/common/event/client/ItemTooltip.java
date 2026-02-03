package org.dreamtinker.dreamtinker.common.event.client;

import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaRegistry;
import org.dreamtinker.dreamtinker.tools.items.UnderArmorItem;
import org.dreamtinker.dreamtinker.utils.CompactUtils.arsNovaUtils;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;

import static org.dreamtinker.dreamtinker.Dreamtinker.configCompactDisabled;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemTooltip {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent e) {
        if (e.getItemStack().getItem().equals(DreamtinkerCommon.narcissus.get().asItem())){
            e.getToolTip().add(Component.translatable("tooltip.dreamtinker.narcissus_1").withStyle(ChatFormatting.GREEN));
            e.getToolTip().add(Component.translatable("tooltip.dreamtinker.narcissus_2").withStyle(ChatFormatting.GREEN));
        }
        if (ModList.get().isLoaded("ars_nouveau") && !configCompactDisabled("ars_nouveau")){
            if (null != e.getEntity() && e.getItemStack().is(TinkerTags.Items.ARMOR) &&
                0 < DTModifierCheck.getItemModifierNum(e.getItemStack(), NovaRegistry.nova_magic_armor.getId())){
                if (!(e.getItemStack().getItem() instanceof UnderArmorItem) && null != PerkRegistry.getPerkProvider(e.getItemStack().getItem()))
                    return;//They would have their own ones.
                arsNovaUtils.appendHoverText(e.getItemStack(), e.getEntity().level(), e.getToolTip(), e.getFlags());
            }
        }
    }
}
