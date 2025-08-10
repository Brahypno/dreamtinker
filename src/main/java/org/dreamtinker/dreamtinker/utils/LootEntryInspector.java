package org.dreamtinker.dreamtinker.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LootEntryInspector {
    private static Field conditionsField;
    private static final ConcurrentMap<String, Field> FIELDS = new ConcurrentHashMap<>();
    private static final float RARE_CHANCE_THRESHOLD = 0.10f;

    @SuppressWarnings("unchecked")
    public static <T> java.util.List<T> get(Object obj, String name) {
        if (obj == null)
            return null;
        Class<?> c = obj.getClass();
        String key = c.getName() + "#" + name;
        try {
            Field f = FIELDS.computeIfAbsent(key, k -> {
                try {
                    Field x = c.getDeclaredField(name); // 如需兼容父类，可换成递归查找
                    x.setAccessible(true);
                    return x;
                }
                catch (NoSuchFieldException e) {
                    return null;
                }
            });
            if (f == null)
                return null;

            Object val = f.get(obj);
            if (val == null)
                return null;

            // 1.20.1 多为 List
            if (val instanceof java.util.List<?> list){
                return (java.util.List<T>) list;
            }
            // 兼容旧版本/其它映射：数组 -> List
            if (val.getClass().isArray()){
                int n = java.lang.reflect.Array.getLength(val);
                java.util.ArrayList<T> out = new java.util.ArrayList<>(n);
                for (int i = 0; i < n; i++) {
                    out.add((T) java.lang.reflect.Array.get(val, i));
                }
                return out;
            }
            return null;
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }

    static {
        try {
            conditionsField = LootPoolEntryContainer.class.getDeclaredField("conditions");
            conditionsField.setAccessible(true);

        }
        catch (Exception e) {
            //System.err.println("[LootEntryInspector] 反射字段失败: " + e);
        }
    }

    public static LootItemCondition[] getConditions(LootPoolEntryContainer entry) {
        try {
            return (LootItemCondition[]) conditionsField.get(entry);
        }
        catch (Exception e) {
            //System.err.println("[ERROR] 没condition？");
            return new LootItemCondition[0];
        }
    }

    public static ItemStack getItemStack(LootPoolEntryContainer entry) {
        if (entry instanceof net.minecraft.world.level.storage.loot.entries.LootItem lootItem){
            try {
                Field itemField = LootItem.class.getDeclaredField("item");
                itemField.setAccessible(true);
                Item item = (Item) itemField.get(lootItem);
                return new ItemStack(item);
            }
            catch (Exception e) {
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    private static Float readChance(LootItemCondition condition) {
        Class<?> cls = condition.getClass();
        String key = cls.getName() + "#chance";
        Field f = FIELDS.computeIfAbsent(key, k -> {
            try {
                Field x = cls.getDeclaredField("chance");
                x.setAccessible(true);
                return x;
            }
            catch (NoSuchFieldException e) {return null;}
        });
        if (f == null)
            return null;
        try {return f.getFloat(condition);}
        catch (IllegalAccessException e) {return null;}
    }


    public static boolean isLowChanceCondition(LootItemCondition condition) {
        ResourceLocation typeId = BuiltInRegistries.LOOT_CONDITION_TYPE.getKey(condition.getType());
        if (typeId == null)
            return false;
        String path = typeId.getPath(); // 已小写

        if ("random_chance".equals(path) || "random_chance_with_looting".equals(path)){
            Float chance = readChance(condition);
            return chance != null && chance < RARE_CHANCE_THRESHOLD;
        }
        return false;
    }

    public static boolean matchRareKeys(LootItemCondition condition) {
        ResourceLocation typeId = BuiltInRegistries.LOOT_CONDITION_TYPE.getKey(condition.getType());
        String key = (typeId != null ? typeId.getPath() : condition.getClass().getName().toLowerCase(Locale.ROOT));

        return key.contains("match_tool") || key.contains("entity") || key.contains("damage_source") || key.contains("survives_explosion") || key.contains("location") || key.contains("time") || key.contains("weather") || key.contains("value_check");
    }


    public static boolean hasRareFunctionCondition(LootPoolEntryContainer entry) {
        // entry 自身 conditions
        List<LootItemCondition> entryConds = get(entry, "conditions");
        if (anyRare(entryConds))
            return true;

        // entry 自身 functions -> 其上的条件
        List<LootItemFunction> funcs = get(entry, "functions");
        if (functionsHaveRare(funcs))
            return true;

        // 组合条目（Alternatives/Sequence/EntryGroup）：递归 children
        List<LootPoolEntryContainer> children = get(entry, "children");
        if (children != null){
            for (LootPoolEntryContainer child : children) {
                if (hasRareFunctionCondition(child))
                    return true;
            }
        }
        return false;
    }

    private static boolean functionsHaveRare(List<LootItemFunction> funcs) {
        if (funcs == null)
            return false;
        for (LootItemFunction fn : funcs) {
            List<LootItemCondition> conds = getFunctionConds(fn);
            if (anyRare(conds))
                return true;
        }
        return false;
    }

    /* 兼容不同实现里对函数条件数组的命名 */
    private static List<LootItemCondition> getFunctionConds(LootItemFunction fn) {
        List<LootItemCondition> c;
        if ((c = get(fn, "predicates")) != null)
            return c;
        if ((c = get(fn, "conditions")) != null)
            return c;
        if ((c = get(fn, "terms")) != null)
            return c;
        return null;
    }

    private static boolean anyRare(List<LootItemCondition> conds) {
        if (conds == null)
            return false;
        for (LootItemCondition c : conds) {
            if (c != null && (isLowChanceCondition(c) || matchRareKeys(c)))
                return true;
        }
        return false;
    }

    public static String describeLootEntry(LootPoolEntryContainer entry) {
        if (entry instanceof net.minecraft.world.level.storage.loot.entries.LootItem lootItem){
            try {
                Field itemField = LootItem.class.getDeclaredField("item");
                itemField.setAccessible(true);
                Item item = (Item) itemField.get(lootItem);
                return "LootItem → " + item.getDescriptionId();
            }
            catch (Exception e) {
                return "LootItem(未知物品)";
            }
        }else if (entry instanceof net.minecraft.world.level.storage.loot.entries.LootTableReference tableRef){
            try {
                Field tableField = LootTableReference.class.getDeclaredField("name");
                tableField.setAccessible(true);
                ResourceLocation ref = (ResourceLocation) tableField.get(tableRef);
                return "LootTableReference → " + ref.toString();
            }
            catch (Exception e) {
                return "LootTableReference(未知表)";
            }
        }else if (entry.getClass().getSimpleName().toLowerCase().contains("tag")){
            return "TagEntry（标签条目）";
        }else {
            return entry.getClass().getSimpleName() + "（未知类型）";
        }
    }

    public static boolean rarityfromitem(LootPoolEntryContainer entry) {
        if (entry instanceof net.minecraft.world.level.storage.loot.entries.LootItem lootItem){
            try {
                Field itemField = LootItem.class.getDeclaredField("item");
                itemField.setAccessible(true);
                Item item = (Item) itemField.get(lootItem);
                return Rarity.RARE == item.getRarity(item.getDefaultInstance()) || Rarity.RARE == item.getRarity(item.getDefaultInstance());
            }
            catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}