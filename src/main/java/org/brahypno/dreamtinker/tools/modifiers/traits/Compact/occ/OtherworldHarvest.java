package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.occ;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.esotericismtinker.library.modifiers.EsotericismTinkerHook;
import org.brahypno.esotericismtinker.library.modifiers.hook.LeftClickHook;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedContext;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtherworldHarvest extends NoLevelsModifier implements LeftClickHook, BreakSpeedModifierHook {
    private static final Map<ResourceLocation, Float> BLOCK_SPEED_MULTIPLIER_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, Float> BLOCK_SPEED_T_CACHE = new HashMap<>();
    Property<Boolean> UNCOVERED = BooleanProperty.create("uncovered");

    public static float speedMultiplierFromBlockId(BlockState state, float min, float max) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (id == null){
            return 1.0f;
        }

        float t = BLOCK_SPEED_T_CACHE.computeIfAbsent(id, key -> {
            int h = key.toString().hashCode();
            h ^= (h >>> 16);
            h *= 0x7feb352d;
            h ^= (h >>> 15);
            h *= 0x846ca68b;
            h ^= (h >>> 16);

            return Math.floorMod(h, 10_000) / 9999.0f;
        });

        return min + (max - min) * t;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, EsotericismTinkerHook.LEFT_CLICK, ModifierHooks.BREAK_SPEED);
    }

    @Override
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
        if (!tool.isBroken() && !level.isClientSide && player.getMainHandItem().is(TinkerTags.Items.HARVEST)){
            if (state.hasProperty(UNCOVERED) && !state.getValue(UNCOVERED)){
                BlockState newState = state.setValue(UNCOVERED, true);
                level.setBlock(pos, newState, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        final String fulfill = this.getTranslationKey() + "_fulfill";
        return Arrays.asList(Component.translatable(fulfill + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(fulfill + ".description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, PlayerEvent.BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
        event.setNewSpeed(event.getNewSpeed() * speedMultiplierFromBlockId(event.getState(), 0.5f, 2.5f));
    }

    @Override
    public float modifyBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeedContext context, float speed) {
        return speed * speedMultiplierFromBlockId(context.state(), 0.5f, 2.5f);
    }
}
