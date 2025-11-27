package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class BlockMultiplier extends Modifier implements ProcessLootModifierHook {
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROCESS_LOOT);
    }

    private final ResourceLocation location;
    private final float rate;
    private final int times;

    public BlockMultiplier(ResourceLocation location, float rate, int time) {
        this.location = location;
        this.rate = rate;
        this.times = time;
    }

    private static boolean in_progress = false;

    public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
        if (context.hasParam(LootContextParams.BLOCK_STATE) && !in_progress){
            BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
            ServerLevel level = context.getLevel();
            if (state != null && state.is(TagKey.create(Registries.BLOCK, location)) && level.random.nextFloat() < rate * modifier.getLevel()){
                System.out.println(state);
                in_progress = true;
                for (int i = 0; i < Math.min(times, (modifier.getLevel() + 1) / 2); i++) {
                    LootParams.Builder builder = new LootParams.Builder(level)
                            .withParameter(LootContextParams.BLOCK_STATE, state)
                            .withParameter(LootContextParams.ORIGIN, context.getParam(LootContextParams.ORIGIN));

                    if (context.hasParam(LootContextParams.TOOL)){
                        builder.withParameter(LootContextParams.TOOL, context.getParam(LootContextParams.TOOL));
                    }

                    LootParams params = builder.create(LootContextParamSets.BLOCK);
                    LootTable lootTable = level.getServer().getLootData().getLootTable(state.getBlock().getLootTable());
                    List<ItemStack> extra = lootTable.getRandomItems(params);
                    generatedLoot.addAll(extra);
                }
                in_progress = false;
            }

        }
    }
}
