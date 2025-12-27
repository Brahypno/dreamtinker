package org.dreamtinker.dreamtinker.tools.modifiers.events.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaBook.ModifiableSpellBook;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;

public class ArsPlayerCraftEvent {
    public static void PlayerCraftEvent(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().level().isClientSide)
            return;
        ItemStack item = event.getCrafting();
        if (item.getItem() instanceof ModifiableSpellBook && event.getEntity() instanceof ServerPlayer player){
            int level = DTModifierCheck.getItemModifierNum(item, DreamtinkerModifiers.Ids.nova_spell_tiers);
            ResourceLocation novice = ItemsRegistry.NOVICE_SPELLBOOK.registryObject.getId();
            ResourceLocation mage = ItemsRegistry.APPRENTICE_SPELLBOOK.registryObject.getId();
            ResourceLocation arch = ItemsRegistry.ARCHMAGE_SPELLBOOK.registryObject.getId();
            if (3 <= level)
                grandAdvancementById(player, arch);
            else if (2 == level)
                grandAdvancementById(player, mage);
            else
                grandAdvancementById(player, novice);


        }

    }

    private static void grandAdvancementById(ServerPlayer player, ResourceLocation id) {
        Advancement adv = player.server.getAdvancements().getAdvancement(id);
        if (adv == null)
            return; // ID 不对或数据未加载

        AdvancementProgress prog = player.getAdvancements().getOrStartProgress(adv);
        if (prog.isDone())
            return;

        // 3) 直接完成所有剩余 criteria
        for (String crit : prog.getRemainingCriteria()) {
            player.getAdvancements().award(adv, crit);
        }
    }
}
