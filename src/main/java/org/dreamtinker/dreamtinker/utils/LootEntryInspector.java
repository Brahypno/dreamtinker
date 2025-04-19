package org.dreamtinker.dreamtinker.utils;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.lang.reflect.Field;
import java.util.Set;

public class LootEntryInspector {
    private static Field conditionsField;

    static {
        try {
            conditionsField = LootPoolEntryContainer.class.getDeclaredField("conditions");
            conditionsField.setAccessible(true);

        } catch (Exception e) {
            //System.err.println("[LootEntryInspector] 反射字段失败: " + e);
        }
    }

    public static LootItemCondition[] getConditions(LootPoolEntryContainer entry) {
        try {
            return (LootItemCondition[]) conditionsField.get(entry);
        } catch (Exception e) {
            //System.err.println("[ERROR] 没condition？");
            return new LootItemCondition[0];
        }
    }

    public static ItemStack getItemStack(LootPoolEntryContainer entry){
        if (entry instanceof net.minecraft.world.level.storage.loot.entries.LootItem lootItem) {
            try {
                Field itemField = LootItem.class.getDeclaredField("item");
                itemField.setAccessible(true);
                Item item = (Item) itemField.get(lootItem);
                return new ItemStack(item);
            } catch (Exception e) {
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    public static ResourceLocation getConditionTypeId(LootItemConditionType type) {
        for (ResourceLocation id : Registry.LOOT_CONDITION_TYPE.keySet()) {
            if (Registry.LOOT_CONDITION_TYPE.get(id) == type) {
                return id;
            }
        }
        return null;
    }
    public static boolean isLowChanceCondition(LootItemCondition condition) {

        ResourceLocation typeId = LootEntryInspector.getConditionTypeId(condition.getType());
        if (typeId == null) return false;

        String path = typeId.getPath().toLowerCase();

        if (path.equals("random_chance") || path.equals("random_chance_with_looting")) {
            try {
                Field chanceField = condition.getClass().getDeclaredField("chance");
                chanceField.setAccessible(true);
                float chance = chanceField.getFloat(condition);
                //System.out.println("[DEBUG] 检测到概率条件 " + path + "，掉率: " + chance);
                if (chance < 0.05f) {
                    //System.out.println("[DEBUG] 掉率低于10%，标记为稀有");
                    return true;
                }
            } catch (Exception e) {
                //System.err.println("[isLowChanceCondition] 无法读取 chance 字段: " + e);
            }
        }
        return false;
    }
    public static boolean matchRareKeys(LootItemCondition condition) {
        Set<String> rareKeywords = Set.of(
                "match_tool", "entity", "damage_source", "survives_explosion",
                "location", "time", "weather", "value_check");

        ResourceLocation typeId = LootEntryInspector.getConditionTypeId(condition.getType());
        //System.out.println("[DEBUG] 条件类型ID: " + typeId);
        String path;
        if (typeId != null)
            path = typeId.getPath().toLowerCase();
        else{
            //System.out.println("[DEBUG] 条件类名: " + path);
            path = condition.getClass().getName().toLowerCase();
        }

        for (String keyword : rareKeywords) {
            if (path.contains(keyword)){
                //System.out.println("[DEBUG] 条件命中稀有关键词: " + keyword + " ← " + path);
                return true;}
        }
        return false;
    }
    public static boolean hasRareFunctionCondition(LootPoolEntryContainer entry, LootContext context) {
        try {
            final boolean[] found = {false};
            entry.expand(context, generator -> {
                //System.out.println("[DEBUG] 所有字段:");
                for (Field f : generator.getClass().getDeclaredFields()) {
                    f.setAccessible(true);
                    //System.out.println("- " + f.getName() + ": " + f.getType().getName());
                }

                //System.out.println("[DEBUG] 展开 entry 成功: " + generator.getClass().getName());

                LootItemFunction[] functions = tryExtractFunctions(generator);
                //System.out.println("[DEBUG] 获取到函数数量: " + functions.length);

                for (LootItemFunction function : functions) {
                    //System.out.println("[DEBUG] 分析函数类型: " + function.getClass().getName());
                    LootItemCondition[] conditions = extractConditionsFromFunction(function);
                    //System.out.println("[DEBUG] 函数绑定条件数量: " + conditions.length);

                    for (LootItemCondition condition : conditions) {
                        if (isLowChanceCondition(condition) || matchRareKeys(condition)) {
                            //System.out.println("[DEBUG] 函数中命中稀有条件");
                            found[0] = true;
                            return;
                        }
                    }
                }
            });
            //System.out.println("[DEBUG] 函数条件稀有判断结果: " + found[0]);
            return found[0];
        } catch (Exception e) {
            //System.err.println("[hasRareFunctionCondition] expand 失败: " + e);
        }
        return false;
    }
    private static LootItemCondition[] extractConditionsFromFunction(Object obj) {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField("predicates");
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value instanceof LootItemCondition[]) {
                    //System.out.println("[DEBUG] 成功提取 predicates 来自类: " + clazz.getName());
                    return (LootItemCondition[]) value;
                }
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass(); // 继续往上找
            } catch (Exception e) {
                //System.err.println("[tryExtractPredicates] 读取失败: " + e);
                break;
            }
        }
        //System.out.println("[DEBUG] 无 predicates 字段: " + obj.getClass().getName());
        return new LootItemCondition[0];
    }
    private static LootItemFunction[] tryExtractFunctions(Object obj) {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            //System.out.println("[DEBUG] 尝试在类中查找 functions: " + clazz.getName());
            try {
                Field field = clazz.getDeclaredField("functions");
                field.setAccessible(true);
                Object val = field.get(obj);
                if (val instanceof LootItemFunction[]) {
                    return (LootItemFunction[]) val;
                }
            } catch (NoSuchFieldException ignored) {
                // continue
            } catch (Exception e) {
                //System.err.println("[tryExtractFunctions] 访问失败: " + e);
                break;
            }

            // 特殊处理匿名内部类（可能是 LootPoolSingletonContainer$1）持有外部类 this$0
            try {
                Field outerField = clazz.getDeclaredField("this$0");
                outerField.setAccessible(true);
                Object outerInstance = outerField.get(obj);
                if (outerInstance != null && outerInstance != obj) {
                    //System.out.println("[DEBUG] 追溯到封闭外部类: " + outerInstance.getClass().getName());
                    return tryExtractFunctions(outerInstance);
                }
            } catch (Exception ignored) {}

            clazz = clazz.getSuperclass();
        }
        //System.err.println("[tryExtractFunctions] no such field: functions");
        return new LootItemFunction[0];
    }


    public static String describeLootEntry(LootPoolEntryContainer entry) {
        if (entry instanceof net.minecraft.world.level.storage.loot.entries.LootItem lootItem) {
            try {
                Field itemField = LootItem.class.getDeclaredField("item");
                itemField.setAccessible(true);
                Item item = (Item) itemField.get(lootItem);
                return "LootItem → " + item.getDescriptionId();
            } catch (Exception e) {
                return "LootItem(未知物品)";
            }
        } else if (entry instanceof net.minecraft.world.level.storage.loot.entries.LootTableReference tableRef) {
            try {
                Field tableField = LootTableReference.class.getDeclaredField("name");
                tableField.setAccessible(true);
                ResourceLocation ref = (ResourceLocation) tableField.get(tableRef);
                return "LootTableReference → " + ref.toString();
            } catch (Exception e) {
                return "LootTableReference(未知表)";
            }
        } else if (entry.getClass().getSimpleName().toLowerCase().contains("tag")) {
            return "TagEntry（标签条目）";
        } else {
            return entry.getClass().getSimpleName() + "（未知类型）";
        }
    }
    public static boolean rarityfromitem(LootPoolEntryContainer entry) {
        if (entry instanceof net.minecraft.world.level.storage.loot.entries.LootItem lootItem) {
            try {
                Field itemField = LootItem.class.getDeclaredField("item");
                itemField.setAccessible(true);
                Item item = (Item) itemField.get(lootItem);
                return Rarity.RARE == item.getRarity(item.getDefaultInstance()) || Rarity.RARE == item.getRarity(item.getDefaultInstance());
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}