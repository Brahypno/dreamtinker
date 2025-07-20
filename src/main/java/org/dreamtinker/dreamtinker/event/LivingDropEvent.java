package org.dreamtinker.dreamtinker.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerEffect;
import org.dreamtinker.dreamtinker.utils.LootEntryInspector;

import java.util.*;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class LivingDropEvent {
    private static final Random random = new Random();

    @SubscribeEvent
    public static void onSilverNameBeeDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level;
        DamageSource source = event.getSource();

        // 仅在服务端执行
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) return;

        Entity attacker = source.getEntity();  // 尝试先获取伤害归属者

        // 如果攻击者为空，则尝试从弹射物中还原
        if (attacker == null && source.getDirectEntity() instanceof Projectile projectile) {
            attacker = projectile.getOwner();  // 可为玩家、骷髅、猪灵等
        }

        // 如果攻击者是生物，则可以检查药水效果
        if (!(attacker instanceof LivingEntity livingAttacker && livingAttacker.hasEffect(DreamtinkerEffect.SilverNameBee.get())))
            return;

        LootContext.Builder builder = new LootContext.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, entity)
                .withParameter(LootContextParams.ORIGIN, entity.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, event.getSource())
                .withOptionalParameter(LootContextParams.KILLER_ENTITY, attacker)
                .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, event.getSource().getDirectEntity())
                .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, attacker instanceof ServerPlayer ? (ServerPlayer) attacker : null);

        ItemStack originalTool = livingAttacker.getMainHandItem();
        ItemStack lootingTool = originalTool.copy();

        // 设置或追加 Looting 附魔为 1000 级
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(lootingTool);
        enchantments.put(Enchantments.MOB_LOOTING, 1000);
        EnchantmentHelper.setEnchantments(enchantments, lootingTool);

        builder.withOptionalParameter(LootContextParams.TOOL, lootingTool);

        LootContext context = builder.create(LootContextParamSets.ENTITY);


        // 获取原始 loot table
        ResourceLocation lootId = entity.getLootTable();
        //System.out.println("[DEBUG] 实体: " + entity.getName().getString() + " 使用掉落表: " + lootId);

        LootTable table = serverLevel.getServer().getLootTables().get(lootId);

        // 从稀有条目中 roll 掉落
        rollRareEntries(table, entity, context);
    }

    private static void rollRareEntries(LootTable table, LivingEntity entity, LootContext context) {
        List<LootPoolEntryContainer> rareEntries = new ArrayList<>();

        for (LootPool pool : table.pools)
            for (LootPoolEntryContainer entry : pool.entries)
                if (isRare(entry, context))
                    rareEntries.add(entry);



        if (!rareEntries.isEmpty()) {
            LootPoolEntryContainer chosen = rareEntries.get(random.nextInt(rareEntries.size()));
            entity.spawnAtLocation(LootEntryInspector.getItemStack(chosen));
        } else {
            Map<Item, Integer> totals = new HashMap<>();

            for (int i = 0; i < 100; i++) {
                List<ItemStack> round = table.getRandomItems(context);
                for (ItemStack stack : round) {
                    if (!stack.isEmpty() && stack.getItem() != Items.AIR) {
                        totals.merge(stack.getItem(), stack.getCount(), Integer::sum);
                    }
                }
            }


            if (!totals.isEmpty()) {
                Map.Entry<Item, Integer> smallestEntry = totals.entrySet().stream()
                        .min(Map.Entry.comparingByValue())
                        .orElse(null);

                if (smallestEntry != null) {
                    ItemStack smallest = new ItemStack(smallestEntry.getKey(), 1);
                    //System.out.println("[DEBUG] 最小掉落: " + smallest.getCount() + " × " + smallest.getItem().getDescriptionId());
                    entity.spawnAtLocation(smallest);
                }
            }

        }

    }

    private static boolean isRare(LootPoolEntryContainer entry, LootContext context) {
        LootItemCondition[] conditions = LootEntryInspector.getConditions(entry);
        //System.out.println("[Warning] 尝试判断是否: " + LootEntryInspector.describeLootEntry(entry));
        for (LootItemCondition condition : conditions) {
            if (LootEntryInspector.isLowChanceCondition(condition) || LootEntryInspector.matchRareKeys(condition))
                return true;
        }
        return LootEntryInspector.hasRareFunctionCondition(entry, context) || LootEntryInspector.rarityfromitem(entry);
    }

}
