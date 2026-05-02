package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.utils.DTMessages;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;

import static org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys.Materials.*;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.UnbuildLimits;
import static org.dreamtinker.dreamtinker.tools.modifiers.traits.common.not_like_was.TAG_CHANGE_TIMES;
import static org.dreamtinker.dreamtinker.utils.DTModifierCheck.getMaterialForTier;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralPlayerCraftEvent {
    @SubscribeEvent
    public static void PlayerCraftEvent(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().level().isClientSide)
            return;
        ItemStack item = event.getCrafting();
        if (item.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(item);
            if (!tool.getModifiers().getEntry(DreamtinkerModifiers.not_like_was.getId()).equals(ModifierEntry.EMPTY)){
                int times = tool.getPersistentData().getInt(TAG_CHANGE_TIMES);
                tool.getPersistentData().putInt(TAG_CHANGE_TIMES, ++times);
                tool.updateStack(item);
                if (UnbuildLimits.get() <= times){
                    event.getEntity().sendSystemMessage(Component.translatable("modifier.dreamtinker.tooltip.not_like_was_1").append(String.valueOf(times))
                                                                 .withStyle(DreamtinkerModifiers.not_like_was.get().getDisplayName().getStyle()));
                }else {
                    event.getEntity().sendSystemMessage(Component.translatable("modifier.dreamtinker.not_like_was.flavor")
                                                                 .withStyle(DreamtinkerModifiers.not_like_was.get().getDisplayName().getStyle()));
                }
            }
            int mei_level = tool.getModifiers().getLevel(DreamtinkerModifiers.mei.getId());
            if (0 < mei_level &&
                event.getEntity().level().getLevelData().isHardcore()){
                tool.removeModifier(DreamtinkerModifiers.mei.getId(), mei_level);
                tool.addModifier(DreamtinkerModifiers.acheron.getId(), mei_level);
                event.getEntity().sendSystemMessage(Component.translatable("modifier.dreamtinker.acheron.flavor")
                                                             .withStyle(DreamtinkerModifiers.acheron.get().getDisplayName().getStyle()));
                tool.updateStack(item);
            }
            MaterialNBT mats = tool.getMaterials();
            if (3 <= mats.size()){
                int thrown = -1, fire = -1, spiral = -1;
                for (int i = 0; i < mats.size(); i++) {
                    if (-1 == thrown && MaterialRegistry.getInstance().isInTag(mats.get(i).getId(), THROW_STONE)){
                        thrown = i;
                    }else if (-1 == fire && MaterialRegistry.getInstance().isInTag(mats.get(i).getId(), FIRE_FLAME)){
                        fire = i;
                    }else if (-1 == spiral && MaterialRegistry.getInstance().isInTag(mats.get(i).getId(), ROTATING_WHEEL)){
                        spiral = i;
                    }
                }
                List<MaterialStatsId> statList = tool.getDefinition().getHook(ToolHooks.TOOL_MATERIALS).getStatTypes(tool.getDefinition());
                if (-1 != thrown && -1 != fire && -1 != spiral){
                    RandomSource rand = event.getEntity().getRandom();
                    mats = mats.replaceMaterial(thrown, DreamtinkerMaterialIds.RuinWheelSteel);
                    mats = mats.replaceMaterial(fire, getMaterialForTier(1, rand, statList.get(fire)));
                    mats = mats.replaceMaterial(spiral, getMaterialForTier(2, rand, statList.get(spiral)));
                    tool.setMaterials(mats);
                    tool.updateStack(item);
                    DTMessages.clientChat(Component.translatable("material.dreamtinker.ruin_wheel_steel_transform")
                                                   .withStyle(DreamtinkerModifiers.doom_track.get().getDisplayName().getStyle()), false);
                }
            }
        }

    }
}
