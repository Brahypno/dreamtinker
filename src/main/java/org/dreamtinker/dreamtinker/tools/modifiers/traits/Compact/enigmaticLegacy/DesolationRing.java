package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class DesolationRing extends ArmorModifier {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new ModifierTraitModule(DreamtinkerModifiers.cursed_ring_bound.getId(), 1, true));
        super.registerHooks(hookBuilder);
    }

    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(CursedRingBound.TAG_DEEP_CURSE);
        return null;
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        tool.getPersistentData().putBoolean(CursedRingBound.TAG_DEEP_CURSE, true);
        return null;
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
                                livingEntity.setHealth(1.0F);
                                ;
                                return;
                            }
                    }
                }
    }
}
