package org.dreamtinker.dreamtinker.tools.items;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;

import java.util.List;

public class DtTiers {
    public static final Tier WOLF_TIER = TierSortingRegistry.registerTier(
            new ForgeTier(
                    6,               // level：相对顺序值。钻石≈3，黑曜石/下界合金≈4，随需求
                    2031,            // 耐久（用于基于 Tier 的原版工具）
                    9.0F,            // 挖掘速度
                    4.0F,            // 额外攻击
                    15,              // 附魔性
                    DreamtinkerTagKeys.Blocks.need_lupus, // “需要此等级工具”的方块标签
                    () -> Ingredient.of(DreamtinkerCommon.metallivorous_stibium_lupus.get()) // 修复材料（可随意）
            ),
            Dreamtinker.getLocation("lupus"),
            List.of(Tiers.NETHERITE),
            List.of()
    );
    public static final Tier Netheritte = TierSortingRegistry.registerTier(
            new ForgeTier(
                    2,               // level：相对顺序值。钻石≈3，黑曜石/下界合金≈4，随需求
                    300,            // 耐久（用于基于 Tier 的原版工具）
                    6.0F,            // 挖掘速度
                    2.0F,            // 额外攻击
                    14,              // 附魔性
                    DreamtinkerTagKeys.Blocks.need_netheritte, // “需要此等级工具”的方块标签
                    () -> Ingredient.of(DreamtinkerCommon.metallivorous_stibium_lupus.get()) // 修复材料（可随意）
            ),
            Dreamtinker.getLocation("netheritte"),
            List.of(Tiers.IRON),
            List.of(Tiers.DIAMOND)
    );

    public static void init() {}
}
