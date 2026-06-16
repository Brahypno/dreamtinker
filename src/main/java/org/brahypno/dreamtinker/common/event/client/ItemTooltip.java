package org.brahypno.dreamtinker.common.event.client;

import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.library.compact.ars_nouveau.NovaRegistry;
import org.brahypno.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.brahypno.dreamtinker.tools.items.UnderArmorItem;
import org.brahypno.dreamtinker.utils.CompactUtils.EnigmaticLegacyCompact;
import org.brahypno.dreamtinker.utils.CompactUtils.arsNovaUtils;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tools.TinkerToolParts;

import static org.brahypno.esotericismtinker.EsotericismTinker.configCompactDisabled;

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
                0 < ETModifierCheck.getItemModifierNum(e.getItemStack(), NovaRegistry.nova_magic_armor.getId())){
                if (!(e.getItemStack().getItem() instanceof UnderArmorItem) && null != PerkRegistry.getPerkProvider(e.getItemStack().getItem()))
                    return;//They would have their own ones.
                arsNovaUtils.appendHoverText(e.getItemStack(), e.getEntity().level(), e.getToolTip(), e.getFlags());
            }
            if (e.getItemStack().getItem().equals(BlockRegistry.MOB_JAR.get().asItem())){
                e.getToolTip().add(Component.translatable("tooltip.dreamtinker.mob_jar").withStyle(ChatFormatting.GREEN));
            }
        }
        if (ModList.get().isLoaded("enigmaticlegacy") && !configCompactDisabled("enigmaticlegacy")){
            if (Screen.hasShiftDown() && EnigmaticLegacyCompact.isCursedScroll(e.getItemStack())){
                e.getToolTip().add(Component.translatable("tooltip.dreamtinker.cursed_scroll").withStyle(ChatFormatting.RED));
            }
        }
        if (e.getItemStack().getItem().equals(TinkerToolParts.fakeIngot.asItem()) &&
            ETModifierCheck.getExpectedMaterialPart(e.getItemStack(), DreamtinkerMaterialIds.RuinWheelSteel)){
            e.getToolTip()
             .add(Component.translatable("material.dreamtinker.ruin_wheel_steel.hint").withStyle(ChatFormatting.ITALIC)
                           .withStyle(Style.EMPTY.withColor(0xFFF0B0)));
        }
    }
}
