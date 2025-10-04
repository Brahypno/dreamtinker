package org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.*;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.library.client.PlayerKeyStateProvider;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.library.modifiers.hook.LeftClickHook;
import org.dreamtinker.dreamtinker.network.KeyStateMsg;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class foundationWill extends Modifier implements LeftClickHook, ProcessLootModifierHook {
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROCESS_LOOT, DreamtinkerHook.LEFT_CLICK);
        super.registerHooks(hookBuilder);
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void processLoot(IToolStackView iToolStackView, ModifierEntry modifierEntry, List<ItemStack> list, LootContext lootContext) {
        var ent = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(ent instanceof ServerPlayer sp))
            return;
        for (ItemStack st : list) {
            if (!st.isEmpty())
                net.minecraftforge.items.ItemHandlerHelper.giveItemToPlayer(sp, st);
        }
        list.clear();
    }

    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        foundationWillWrapper(entry, player, level, equipmentSlot);
    }

    @Override
    public void onLeftClickBlock(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
        foundationWillWrapper(entry, player, level, equipmentSlot);
    }

    private void foundationWillWrapper(ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        boolean weapon_interact = player.getCapability(PlayerKeyStateProvider.PlayerKeyState.CAP)
                                        .map(cap -> cap.isDown(KeyStateMsg.KeyKind.TOOL_INTERACT))
                                        .orElse(false);
        if (!level.isClientSide && EquipmentSlot.MAINHAND == equipmentSlot && weapon_interact){
            List<BlockPos> cluster = pickSameBlocksAlongLook(player, /*每节点最多扩展*/ 3 * entry.getLevel(), /*最大总数*/ 16 * entry.getLevel());
            for (BlockPos pos : cluster)
                if (!ToolStack.from(player.getMainHandItem()).isBroken()){
                    if (entry.getLevel() < player.totalExperience && ToolHarvestLogic.handleBlockBreak(player.getMainHandItem(), pos, player)){
                        ((ServerLevel) player.level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                                                                     pos.getX(), pos.getY(), pos.getZ(),
                                                                     1,                    // count
                                                                     0.01, 0.01, 0.01,     // dx, dy, dz（随机扩散）
                                                                     0.0                   // speed（0 表示静止；想要缓慢前进的观感可以设 0.02）
                        );
                        player.giveExperiencePoints(-entry.getLevel());
                    }
                }
        }
    }

    final static List<String> avoid_path = List.of("machine", "furnace", "smoker", "comparator", "repeater", "observer", "dropper", "dispenser");
    final static List<String> avoid_namespace = List.of("ae2", "mekanism", "thermal");

    private static boolean canHarvest(Player player, BlockState state) {
        ItemStack itemStack = player.getMainHandItem();
        if (!(itemStack.getItem() instanceof ModifiableItem))
            return false;
        if (!ForgeHooks.isCorrectToolForDrops(state, player))
            return false;
        ToolStack tool = ToolStack.from(itemStack);
        if (tool.isBroken())
            return false;
        //1)filter the block--we don`t care ores right
        if (state.is(Tags.Blocks.ORES) || state.is(Tags.Blocks.STORAGE_BLOCKS)){
            Item block = state.getBlock().asItem();
            ResourceLocation rs = ForgeRegistries.ITEMS.getKey(block);
            if (null == rs || avoid_path.stream().anyMatch(e -> rs.getPath().contains(e)) ||
                avoid_namespace.stream().anyMatch(e -> rs.getNamespace().contains(e)))
                return false;
        }

        int extra_tiers = tool.getModifierLevel(DreamtinkerModifiers.Ids.full_concentration);

        // 2) harvest tier
        Tier tier = tool.getStats().get(ToolStats.HARVEST_TIER);
        int idx = Math.min(TierSortingRegistry.getSortedTiers().indexOf(tier) - 1 + extra_tiers, TierSortingRegistry.getSortedTiers().size() - 1);
        if (0 <= idx){
            return TierSortingRegistry.isCorrectTierForDrops(TierSortingRegistry.getSortedTiers().get(idx), state);
        }
        return false;
    }

    public static List<BlockPos> pickSameBlocksAlongLook(Player player, int xPerNode, int yMax) {
        if (yMax <= 0)
            return List.of();

        Level level = player.level();
        // 1) 计算 reach 并做方块射线检测
        double reach = getReach(player);
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 end = eye.add(player.getLookAngle().scale(reach));
        AABB pathBox = new AABB(eye, end).inflate(0.5); // 适度膨胀避免高速漏判
        EntityHitResult monsterHit = ProjectileUtil.getEntityHitResult(
                level, player, eye, end, pathBox,
                e -> e instanceof Monster && e.isPickable() && e.isAlive());

        if (monsterHit != null)
            return List.of(); // 视线被怪物“挡住”，不选方块

        BlockHitResult bhr = level.clip(new ClipContext(
                eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

        if (bhr.getType() != HitResult.Type.BLOCK)
            return List.of();
        BlockPos start = bhr.getBlockPos();
        if (!level.isLoaded(start))
            return List.of();

        BlockState startState = level.getBlockState(start);
        if (startState.isAir())
            return List.of();
        if (!canHarvest(player, startState))
            return List.of();
        Vec3 from = eye.add(player.getLookAngle().scale(0.5));     // 从眼前0.5m开始，避免贴脸
        Vec3 to = bhr.getLocation();                              // 命中点更精确，也可用 Vec3.atCenterOf(startPos)
        emitSoulFireTrailServer(level, from, to);

        // 2) 受限 BFS
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        HashSet<BlockPos> visited = new HashSet<>(Math.min(yMax * 2, 4096));
        ArrayList<BlockPos> result = new ArrayList<>(yMax);

        queue.add(start);
        visited.add(start);
        result.add(start);

        while (!queue.isEmpty() && result.size() < yMax) {
            BlockPos cur = queue.poll();

            int expanded = 0; // 本节点已扩展的相邻数，限制为 xPerNode
            for (Direction d : Direction.values()) {
                if (expanded >= xPerNode)
                    break;

                BlockPos np = cur.relative(d);
                if (visited.contains(np) || !level.isLoaded(np))
                    continue;

                BlockState ns = level.getBlockState(np);
                if (isSameKind(startState, ns)){
                    visited.add(np);
                    queue.add(np);
                    result.add(np);
                    expanded++;
                    if (result.size() >= yMax)
                        break;
                }
            }
        }
        return result;
    }

    /**
     * 判定“同类”：默认同 Block（不强求完全相同的属性）。需要更严格可改成 ns.equals(startState)
     */
    private static boolean isSameKind(BlockState start, BlockState ns) {
        return ns.getBlock() == start.getBlock();
    }

    /**
     * 取得玩家方块触达距离（Forge 属性），退化到默认 4.5
     */
    private static double getReach(Player player) {
        var inst = player.getAttribute(ForgeMod.BLOCK_REACH.get());
        return inst != null ? inst.getValue() * 4 : 8D;
    }

    private static void emitSoulFireTrailServer(Level level, Vec3 start, Vec3 end) {
        if (!(level instanceof ServerLevel sl))
            return;               // 只在服务端播
        double dist = start.distanceTo(end);
        int steps = Mth.clamp((int) Math.ceil(dist * 6.0), 6, 80);     // 每米约6点，限幅防刷屏
        Vec3 dir = end.subtract(start).scale(1.0 / steps);            // 均匀步进

        for (int i = 0; i <= steps; i++) {
            Vec3 p = start.add(dir.scale(i));
            // count=1，轻微抖动；speed 给一点点沿视线的推进感
            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                             p.x, p.y, p.z,
                             1,                    // count
                             0.01, 0.01, 0.01,     // dx, dy, dz（随机扩散）
                             0.02                   // speed（0 表示静止；想要缓慢前进的观感可以设 0.02）
            );
        }
    }


}
