package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.util.Mth;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.homunculusGiftDiscount;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class merchant {
    @SubscribeEvent
    public static void onMerchantMenuOpened(PlayerContainerEvent.Open e) {
        if (!(e.getContainer() instanceof MerchantMenu menu))
            return;

        // 你的判定：比如玩家/村民有某标签、状态或物品
        int homunculusGift = DTModifierCheck.getEntityModifierNum(e.getEntity(), DreamtinkerModifiers.Ids.homunculusGift);
        if (homunculusGift <= 0)
            return;

        // 获取并修改当次会话的报价列表
        MerchantOffers offers = menu.getOffers();

        for (MerchantOffer o : offers) {
            int dec = Mth.floor(o.getBaseCostA().getCount() * homunculusGift * homunculusGiftDiscount.get());
            o.addToSpecialPriceDiff(-dec);
        }

        // 通知客户端刷新（必要时）
        menu.broadcastChanges();
    }
}
