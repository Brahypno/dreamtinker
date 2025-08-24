package org.dreamtinker.dreamtinker.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifer;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class meiUpdate {
    @SubscribeEvent
    public static void onPlayerLevelChange(PlayerXpEvent.LevelChange event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        // 只在“升级”的时候做（降级/其他不处理）
        if (event.getLevels() <= 0 || player.level().isClientSide)
            return;

        List<ItemStack> hits = findStacksWithModifier(player, DreamtinkerModifer.mei.getId());

        if (!hits.isEmpty()){
            for (ItemStack stack : hits) {
                ToolStack ts = ToolStack.from(stack);
                ts.addModifier(DreamtinkerModifer.mei.getId(), event.getLevels());//I know this may cause thread overwritten,but who cares
                ts.updateStack(stack);
            }
        }
    }

    private static List<ItemStack> findStacksWithModifier(ServerPlayer player, ModifierId target) {
        var inv = player.getInventory();
        List<ItemStack> result = new ArrayList<>();

        // 主物品栏
        for (ItemStack stack : inv.items) {
            if (0 < ModifierUtil.getModifierLevel(stack, target))
                result.add(stack);
        }
        // 盔甲
        for (ItemStack stack : inv.armor) {
            if (0 < ModifierUtil.getModifierLevel(stack, target))
                result.add(stack);
        }
        // 盾手
        for (ItemStack stack : inv.offhand) {
            if (0 < ModifierUtil.getModifierLevel(stack, target))
                result.add(stack);
        }
        return result;
    }
}
