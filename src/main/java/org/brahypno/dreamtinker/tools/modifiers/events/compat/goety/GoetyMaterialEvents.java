package org.brahypno.dreamtinker.tools.modifiers.events.compat.goety;

import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.items.armor.ModArmorMaterials;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.brahypno.dreamtinker.library.compat.goety.GoetyMaterialUtil;

public final class GoetyMaterialEvents {
    private GoetyMaterialEvents() {}

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onServantKill(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        if (killer == null){
            return;
        }

        LivingEntity immediateOwner = MobUtil.getOwner(killer);
        if (immediateOwner == null){
            return;
        }
        LivingEntity outerOwner = MobUtil.getOwner(immediateOwner);
        Player player = outerOwner instanceof Player outerPlayer ? outerPlayer
                                                                 : immediateOwner instanceof Player immediatePlayer ? immediatePlayer : null;
        if (player == null || player instanceof FakePlayer || !GoetyMaterialUtil.hasFullDarkMetalSet(player)){
            return;
        }

        // Goety already grants these kills through one of its native paths; only fill the custom-set gap.
        if (MobsConfig.ServantsAlwaysGiveSE.get() || CuriosFinder.hasDarkRobe(player)
            || CuriosFinder.hasUndeadSet(player)
            || ItemHelper.armorSet(immediateOwner, ModArmorMaterials.BLACK_IRON)
            || killer instanceof RaiderServant){
            return;
        }

        SEHelper.handleKill(player, event.getEntity(), event.getSource());
    }
}
