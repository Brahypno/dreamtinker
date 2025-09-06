package org.dreamtinker.dreamtinker.modifier.Compact.enigmaticLegacy;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.ArmorModifier;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class desolation_ring extends ArmorModifier {
    @Override
    public void addTraits(IToolContext var1, ModifierEntry var2, TraitBuilder var3, boolean var4) {
        if (var4 && var1.getModifierLevel(DreamtinkerModifers.cursed_ring_bound.getId()) < 20)
            var3.add(DreamtinkerModifers.cursed_ring_bound.getId(), 20);
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if (isCorrectSlot || isSelected)
            if (holder instanceof Player player)
                if (SuperpositionHandler.isTheWorthyOne(player)){
                    List<Entity> nearbyEntities =
                            player.level().getEntities(null, SuperpositionHandler.getBoundingBoxAroundEntity(player, 128));

                    // 遍历实体列表
                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof LivingEntity livingEntity)
                            if (entity instanceof Piglin || entity instanceof ZombifiedPiglin || entity instanceof IronGolem
                                || entity instanceof EnderMan){
                                livingEntity.invulnerableTime = 0;
                                livingEntity.setHealth(0.0F);
                                livingEntity.die(world.damageSources().playerAttack(player));
                                return;
                            }
                    }
                }
    }
}
