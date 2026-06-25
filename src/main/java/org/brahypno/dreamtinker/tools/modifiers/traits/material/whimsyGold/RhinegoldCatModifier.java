package org.brahypno.dreamtinker.tools.modifiers.traits.material.whimsyGold;


import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.Tags;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.utils.LootHelper.GlobalLootModifierItemScanner;
import org.brahypno.dreamtinker.utils.LootHelper.LootScanCommon;
import org.brahypno.dreamtinker.utils.LootHelper.LootScanCommon.CandidateFilter;
import org.brahypno.dreamtinker.utils.LootHelper.LootTableItemScanner;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class RhinegoldCatModifier extends Modifier implements ProcessLootModifierHook, MeleeHitModifierHook {
    private static final ResourceLocation PAW_PITY = new ResourceLocation(Dreamtinker.MODID, "rhinegold_cat_pity");
    private static final String PAW_MARK = Dreamtinker.MODID + "_rhinegold_cat_paw_mark";
    private static final String PAW_EXPIRE = Dreamtinker.MODID + "_rhinegold_cat_paw_expire";
    private static final String TARGET_CD = Dreamtinker.MODID + "_rhinegold_cat_target_cd";

    private static final TagKey<Item> ITEM_BLACKLIST = TagKey.create(Registries.ITEM, new ResourceLocation(Dreamtinker.MODID, "rhinegold_cat_blacklist"));
    private static final TagKey<EntityType<?>> ENTITY_BLACKLIST =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(Dreamtinker.MODID, "rhinegold_cat_blacklist"));

    private static void tryPickpocket(ServerLevel level, LivingEntity target, IToolStackView tool, float lv, int looting) {
        long now = level.getGameTime();
        long next = target.getPersistentData().getLong(TARGET_CD);
        if (now < next)
            return;

        int pity = getPity(tool);
        float chance = Mth.clamp(0.035F + 0.018F * lv + 0.006F * looting + 0.018F * pity, 0.0F, 0.28F);

        if (level.random.nextFloat() >= chance){
            setPity(tool, pity + 1);
            return;
        }

        target.getPersistentData().putLong(TARGET_CD, now + 20L * 8L);

        List<ItemStack> stolen = tryTable(level, target, 0.18F + 0.035F * lv, looting, pawPickpocketFilter(lv));
        stolen.addAll(tryGlm(level, target, 0.12F + 0.025F * lv, looting, pawPickpocketFilter(lv)));

        if (stolen.isEmpty()){
            setPity(tool, pity + 1);
            return;
        }

        addPawMark(target, lv);
        setPity(tool, 0);
    }

    private static void addDeathRummageLoot(ServerLevel level, LivingEntity victim, List<ItemStack> items, float lv, int looting, int marks, CandidateFilter filter) {
        RandomSource random = level.random;

        int rolls = (int) Mth.clamp(marks + lv, 2, 8);
        int maxAdded = (int) Mth.clamp(1 + marks + lv / 2, 2, 7);
        int added = 0;

        for (int i = 0; i < rolls && added < maxAdded; i++) {
            float decay = switch (i) {
                case 0 -> 1.00F;
                case 1 -> 0.76F;
                case 2 -> 0.58F;
                case 3 -> 0.42F;
                case 4 -> 0.30F;
                case 5 -> 0.20F;
                default -> 0.12F;
            };

            float base = 0.115F + 0.048F * lv + 0.018F * looting + 0.025F * marks;
            float tableTrigger = Mth.clamp(base * decay, 0.0F, 0.78F);
            float glmTrigger = Mth.clamp(base * 0.80F * decay, 0.0F, 0.60F);

            if (addGenerated(items, tryTable(level, victim, tableTrigger, looting, filter), lv, random))
                added++;
            if (added >= maxAdded)
                break;
            if (addGenerated(items, tryGlm(level, victim, glmTrigger, looting, filter), lv, random))
                added++;
        }
    }

    private static List<ItemStack> tryTable(ServerLevel level, LivingEntity victim, float triggerRate, int looting, CandidateFilter filter) {
        try {
            return LootTableItemScanner.tryExtractLoot(level, victim, triggerRate, looting, LootScanCommon.LootRollMode.RARE, filter,
                                                       LootScanCommon::pickByInverseRate);
        }
        catch (Throwable ignored) {
            return List.of();
        }
    }

    private static List<ItemStack> tryGlm(ServerLevel level, LivingEntity victim, float triggerRate, int looting, CandidateFilter filter) {
        try {
            return GlobalLootModifierItemScanner.tryExtractLoot(level, victim, triggerRate, looting, LootScanCommon.LootRollMode.NATURAL, filter,
                                                                LootScanCommon::pickByInverseRate);
        }
        catch (Throwable ignored) {
            return List.of();
        }
    }

    private static boolean addGenerated(List<ItemStack> items, List<ItemStack> generated, float lv, RandomSource random) {
        if (generated == null || generated.isEmpty())
            return false;

        for (ItemStack stack : generated) {
            if (stack.isEmpty())
                continue;
            if (stack.is(ITEM_BLACKLIST))
                continue;

            ItemStack copy = stack.copy();
            copy.setCount(getStolenCount(copy, lv, random));
            if (!copy.isEmpty()){
                items.add(copy);
                return true;
            }
        }

        return false;
    }

    private static int getStolenCount(ItemStack stack, float lv, RandomSource random) {
        if (stack.isEmpty())
            return 0;
        if (stack.getMaxStackSize() <= 1)
            return 1;

        int natural = Math.max(1, stack.getCount());
        int cap = switch ((int) lv) {
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> 6;
            default -> 8;
        };

        if (isGemLike(stack) || LootScanCommon.itemRarityAtLeast(stack, Rarity.RARE))
            cap = Math.max(1, cap / 2);
        if (isGoldLike(stack))
            cap += 1;

        int upper = Math.max(1, Math.min(Math.min(natural, stack.getMaxStackSize()), cap));
        return upper <= 1 ? 1 : 1 + random.nextInt(upper);
    }

    private static CandidateFilter pawPickpocketFilter(float lv) {
        return candidate -> {
            ItemStack stack = candidate.stack();
            if (stack.isEmpty() || stack.getItem() == Items.AIR || stack.is(ITEM_BLACKLIST))
                return false;

            double rate = candidate.estimatedRate();
            if (rate <= 0.0D || Double.isNaN(rate) || Double.isInfinite(rate))
                return false;
            if (rate > 0.35D && !isShiny(stack))
                return false;
            if (stack.getMaxStackSize() <= 1 && LootScanCommon.itemRarityAtLeast(stack, Rarity.EPIC))
                return false;

            boolean catLikes = isGoldLike(stack) || isGemLike(stack) || isMetalLike(stack) || isShiny(stack);
            boolean rareButNotAbsurd = candidate.dropRateRarity().atLeast(LootScanCommon.DropRateRarity.RARE) && rate >= 0.008D;

            return catLikes || rareButNotAbsurd || lv >= 3 && rate <= 0.12D;
        };
    }

    private static CandidateFilter rhinegoldDeathFilter(float lv) {
        return candidate -> {
            ItemStack stack = candidate.stack();
            if (stack.isEmpty() || stack.getItem() == Items.AIR || stack.is(ITEM_BLACKLIST))
                return false;

            double rate = candidate.estimatedRate();
            if (rate <= 0.0D || Double.isNaN(rate) || Double.isInfinite(rate))
                return false;

            boolean valuable = isShiny(stack) || isGoldLike(stack) || isGemLike(stack) || isMetalLike(stack);
            boolean rareByRate = candidate.dropRateRarity().atLeast(LootScanCommon.DropRateRarity.RARE);
            boolean veryRareByRate = candidate.dropRateRarity().atLeast(LootScanCommon.DropRateRarity.VERY_RARE);
            boolean rareByItem = LootScanCommon.itemRarityAtLeast(stack, Rarity.RARE);

            boolean interestingCondition = candidate.hasCondition("random_chance")
                                           || candidate.hasCondition("random_chance_with_looting")
                                           || candidate.hasCondition("killed")
                                           || candidate.hasCondition("match_tool")
                                           || candidate.hasCondition("damage_source_properties")
                                           || candidate.hasCondition("entity_properties");

            if (isJunk(stack) && rate > 0.10D)
                return false;

            if (valuable)
                return rate <= 0.90D;
            if (rareByItem)
                return rate <= 0.70D;
            if (veryRareByRate)
                return true;
            if (rareByRate && rate <= 0.32D)
                return true;
            if (interestingCondition && rate <= 0.42D)
                return true;

            return lv >= 3 && rate <= 0.18D;
        };
    }

    private static void addPawMark(LivingEntity target, float lv) {
        int old = target.getPersistentData().getInt(PAW_MARK);
        int max = (int) Mth.clamp(2 + lv, 3, 6);
        target.getPersistentData().putInt(PAW_MARK, Mth.clamp(old + 1, 1, max));
        target.getPersistentData().putLong(PAW_EXPIRE, target.level().getGameTime() + 20L * 12L);
    }

    private static int consumeValidPawMarks(LivingEntity target) {
        long expire = target.getPersistentData().getLong(PAW_EXPIRE);
        if (target.level().getGameTime() > expire){
            target.getPersistentData().remove(PAW_MARK);
            target.getPersistentData().remove(PAW_EXPIRE);
            return 0;
        }

        int marks = Math.max(0, target.getPersistentData().getInt(PAW_MARK));
        target.getPersistentData().remove(PAW_MARK);
        target.getPersistentData().remove(PAW_EXPIRE);
        return marks;
    }

    private static int getPity(IToolStackView tool) {
        return tool.getPersistentData().getInt(PAW_PITY);
    }

    private static void setPity(IToolStackView tool, int value) {
        tool.getPersistentData().put(PAW_PITY, IntTag.valueOf(Mth.clamp(value, 0, 10)));
    }

    private static boolean isGoldLike(ItemStack stack) {
        return stack.is(Items.GOLD_INGOT) || stack.is(Items.GOLD_NUGGET) || stack.is(Items.RAW_GOLD) || stack.is(Tags.Items.INGOTS_GOLD) ||
               stack.is(Tags.Items.NUGGETS_GOLD) || stack.is(Tags.Items.RAW_MATERIALS_GOLD);
    }

    private static boolean isGemLike(ItemStack stack) {
        return stack.is(Tags.Items.GEMS) || stack.is(Items.DIAMOND) || stack.is(Items.EMERALD) || stack.is(Items.AMETHYST_SHARD) ||
               stack.is(Items.PRISMARINE_CRYSTALS);
    }

    private static boolean isMetalLike(ItemStack stack) {
        return stack.is(Tags.Items.INGOTS) || stack.is(Tags.Items.NUGGETS) || stack.is(Tags.Items.RAW_MATERIALS);
    }

    private static boolean isShiny(ItemStack stack) {
        return isGoldLike(stack) || isGemLike(stack) || stack.is(Items.GLOWSTONE_DUST) || stack.is(Items.BLAZE_ROD) || stack.is(Items.BLAZE_POWDER) ||
               stack.is(Items.ENDER_PEARL) || stack.is(Items.ECHO_SHARD) || stack.is(Items.NETHER_STAR);
    }

    private static boolean isJunk(ItemStack stack) {
        return stack.is(Items.ROTTEN_FLESH) || stack.is(Items.BONE) || stack.is(Items.STRING) || stack.is(Items.ARROW) || stack.is(Items.SPIDER_EYE) ||
               stack.is(Items.GUNPOWDER);
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROCESS_LOOT, ModifierHooks.MELEE_HIT);
        hookBuilder.addModule(new VolatileFlagModule(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY));
        super.registerHooks(hookBuilder);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!(context.getAttacker().level() instanceof ServerLevel level))
            return;
        LivingEntity target = ETHelper.getLivingTarget(context.getTarget());
        if (null == target || target instanceof Player)
            return;
        if (target.getType().is(ENTITY_BLACKLIST))
            return;

        float lv = modifier.getEffectiveLevel();
        if (lv <= 0)
            return;

        tryPickpocket(level, target, tool, lv,
                      Math.max(0, net.minecraft.world.item.enchantment.EnchantmentHelper.getMobLooting(context.getAttacker())));
    }

    @Override
    public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> items, LootContext context) {
        ServerLevel level = context.getLevel();

        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof LivingEntity victim))
            return;
        if (victim instanceof Player)
            return;
        if (victim.getType().is(ENTITY_BLACKLIST))
            return;

        Entity killer = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        Entity directKiller = context.getParamOrNull(LootContextParams.DIRECT_KILLER_ENTITY);
        if (!(killer instanceof LivingEntity) && !(directKiller instanceof LivingEntity))
            return;

        float lv = modifier.getEffectiveLevel();
        if (lv <= 0)
            return;

        int marks = Math.max(0, consumeValidPawMarks(victim));

        int looting = Math.max(0, context.getLootingModifier());
        addDeathRummageLoot(level, victim, items, lv, looting, marks, rhinegoldDeathFilter(lv));
    }
}
