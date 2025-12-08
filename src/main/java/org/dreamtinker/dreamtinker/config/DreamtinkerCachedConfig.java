package org.dreamtinker.dreamtinker.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DreamtinkerCachedConfig {
    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading e) {
        if (e.getConfig().getModId().equals(Dreamtinker.MODID))
            ConfigLazy.invalidateAll();
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading e) {
        if (e.getConfig().getModId().equals(Dreamtinker.MODID))
            ConfigLazy.invalidateAll();
    }

    public static final ConfigLazy<Integer> TNT_ARROW_GRAVITY = lazIntInSpec(DreamtinkerConfig.tnt_arrow_gravity);
    public static final ConfigLazy<Integer> TNT_ARROW_RADIUS = lazIntInSpec(DreamtinkerConfig.tnt_arrow_radius);
    public static final ConfigLazy<Integer> StrongExplodeDamageBoost = lazIntInSpec(DreamtinkerConfig.StrongExplodeDamageBoost);
    public static final ConfigLazy<Double> ForceExplosionPower = lazDoubleInSpec(DreamtinkerConfig.ForceExplosionPower);
    public static final ConfigLazy<Double> UnderPlateBoostMax = lazDoubleInSpec(DreamtinkerConfig.UnderPlateBoostMax);
    public static final ConfigLazy<Double> UnderPlateBoostArmorFactor = lazDoubleInSpec(DreamtinkerConfig.UnderPlateBoostArmorFactor);
    public static final ConfigLazy<Double> UnderPlateBoostToughnessFactor = lazDoubleInSpec(DreamtinkerConfig.UnderPlateBoostToughnessFactor);
    public static final ConfigLazy<Integer> StarRegulusMaxHP = lazIntInSpec(DreamtinkerConfig.StarRegulusMaxHP);
    public static final ConfigLazy<Boolean> StarRegulusAdvancement = lazBoolInSpec(DreamtinkerConfig.StarRegulusAdvancement);
    public static final ConfigLazy<Integer> SilverNameBeeNum = lazIntInSpec(DreamtinkerConfig.SilverNameBeeNum);
    public static final ConfigLazy<Double> AntimonyLootChance = lazDoubleInSpec(DreamtinkerConfig.AntimonyLootChance);
    public static final ConfigLazy<Double> voidPearlDropRate = lazDoubleInSpec(DreamtinkerConfig.voidPearlDropRate);
    public static final ConfigLazy<Double> voidPearlDamage = lazDoubleInSpec(DreamtinkerConfig.voidPearlDamage);
    public static final ConfigLazy<Double> WhitePeachLootChance = lazDoubleInSpec(DreamtinkerConfig.WhitePeachLootChance);
    public static final ConfigLazy<Double> SoulCastLoveLootChance = lazDoubleInSpec(DreamtinkerConfig.SoulCastLoveLootChance);
    public static final ConfigLazy<Double> glacialRiverPortion = lazDoubleInSpec(DreamtinkerConfig.glacialRiverPortion);
    public static final ConfigLazy<Integer> glacialRiverRange = lazIntInSpec(DreamtinkerConfig.glacialRiverRange);
    public static final ConfigLazy<Boolean> glacialRiverKillPlayer = lazBoolInSpec(DreamtinkerConfig.glacialRiverKillPlayer);
    public static final ConfigLazy<Integer> EchoAttackCharge = lazIntInSpec(DreamtinkerConfig.EchoAttackCharge);
    public static final ConfigLazy<Double> EchoAttackChargingChance = lazDoubleInSpec(DreamtinkerConfig.EchoAttackChargingChance);
    public static final ConfigLazy<Double> EchoDefenceRange = lazDoubleInSpec(DreamtinkerConfig.EchoDefenceRange);
    public static final ConfigLazy<Double> EchoDefenceSpeed = lazDoubleInSpec(DreamtinkerConfig.EchoDefenceSpeed);
    public static final ConfigLazy<Double> flamingMemoryStatusBoost = lazDoubleInSpec(DreamtinkerConfig.flamingMemoryStatusBoost);
    public static final ConfigLazy<Double> thunderCurse = lazDoubleInSpec(DreamtinkerConfig.thunderCurse);
    public static final ConfigLazy<Double> FragileDodge = lazDoubleInSpec(DreamtinkerConfig.FragileDodge);
    public static final ConfigLazy<Integer> homunculusLifeCurseMaxEffectLevel = lazIntInSpec(DreamtinkerConfig.homunculusLifeCurseMaxEffectLevel);
    public static final ConfigLazy<Double> homunculusGiftDiscount = lazDoubleInSpec(DreamtinkerConfig.homunculusGiftDiscount);
    public static final ConfigLazy<Double> AbsorptionDefenseRate = lazDoubleInSpec(DreamtinkerConfig.AbsorptionDefenseRate);
    public static final ConfigLazy<Double> AbsorptionHitRate = lazDoubleInSpec(DreamtinkerConfig.AbsorptionHitRate);
    public static final ConfigLazy<Double> DespairShade = lazDoubleInSpec(DreamtinkerConfig.DespairShade);
    public static final ConfigLazy<Double> rainbowHoneyRate = lazDoubleInSpec(DreamtinkerConfig.rainbowHoneyRate);
    public static final ConfigLazy<Integer> ChainSawEnergyCost = lazIntInSpec(DreamtinkerConfig.ChainSawEnergyCost);

    public static final ConfigLazy<java.util.List<Double>> TheSplendourHeart =
            lazDoubleList(DreamtinkerConfig.TheSplendourHeart,
                    /*sort*/ true, /*distinct*/ false,
                    /*min*/ 0.0, /*max*/ 1.0,
                    /*requiredSize*/ 5);

    public static ConfigLazy<Integer> lazIntInSpec(net.minecraftforge.common.ForgeConfigSpec.IntValue v) {
        return ConfigLazy.of(
                v,
                v::getDefault,
                x -> true,
                x -> x
        );
    }

    // double：范围来自 spec 定义（defineInRange）
    public static ConfigLazy<Double> lazDoubleInSpec(net.minecraftforge.common.ForgeConfigSpec.DoubleValue v) {
        return ConfigLazy.of(
                v,
                v::getDefault,
                x -> true,
                x -> x
        );
    }

    public static ConfigLazy<Boolean> lazBoolInSpec(ForgeConfigSpec.BooleanValue v) {
        return ConfigLazy.of(
                v,
                v::getDefault,
                x -> true,
                x -> x
        );
    }

    // 明确签名：返回 ConfigLazy<List<Double>>
    private static ConfigLazy<java.util.List<Double>> lazDoubleList(
            net.minecraftforge.common.ForgeConfigSpec.ConfigValue<java.util.List<? extends Number>> cfg,
            boolean sortAscending,
            boolean distinct,
            double minInclusive, double maxInclusive,
            int requiredSizeOrMinusOne
    ) {
        // 把 List<? extends Number> 映射为 List<Double>
        java.util.function.Function<java.util.List<? extends Number>, java.util.List<Double>> mapToDoubles = xs -> {
            if (xs == null)
                return java.util.List.of();
            java.util.ArrayList<Double> out = new java.util.ArrayList<>(xs.size());
            for (Number n : xs)
                if (n != null)
                    out.add(n.doubleValue());
            return out;
        };

        // 原始读取（运行时配置）
        java.util.function.Supplier<java.util.List<Double>> raw = () -> mapToDoubles.apply(cfg.get());

        // 默认读取（来自 spec 的默认值，不用你额外维护）
        java.util.function.Supplier<java.util.List<Double>> defSup = () -> {
            java.util.List<? extends Number> def = cfg.getDefault(); // 捕获通配符
            java.util.List<Double> mapped = mapToDoubles.apply(def);
            // 若默认值意外为空/null，则给个空只读表，后续校验可拦截并回退
            return java.util.List.copyOf(mapped);
        };

        // 用类型实参把 T 钉死为 List<Double>
        return ConfigLazy.<java.util.List<Double>>of(
                raw,
                defSup,
                (java.util.List<Double> list) -> {                // validator：长度/边界
                    if (list == null)
                        return false;
                    if (requiredSizeOrMinusOne >= 0 && list.size() != requiredSizeOrMinusOne)
                        return false;
                    for (Double d : list)
                        if (d == null || d < minInclusive || d > maxInclusive)
                            return false;
                    return true;
                },
                (java.util.List<Double> list) -> {                // normalizer：拷贝→排序/去重→只读
                    java.util.ArrayList<Double> out = new java.util.ArrayList<>(list);
                    if (sortAscending)
                        out.sort(java.util.Comparator.naturalOrder());
                    if (distinct)
                        out = new java.util.ArrayList<>(new java.util.LinkedHashSet<>(out));
                    return java.util.Collections.unmodifiableList(out);
                }
        );
    }

    private static ConfigLazy<java.util.List<String>> lazStringList(
            net.minecraftforge.common.ForgeConfigSpec.ConfigValue<java.util.List<? extends String>> cfg,
            boolean sort, boolean distinct, boolean lowerCase
    ) {
        // 将 List<? extends String> 统一映射为 List<String>
        java.util.function.Function<java.util.List<? extends String>, java.util.List<String>> toStrings = xs -> {
            if (xs == null)
                return java.util.List.of();
            return java.util.List.copyOf(xs); // 已经是 String，无需拷贝元素类型
        };

        // 原始读取（运行时）
        java.util.function.Supplier<java.util.List<String>> raw = () -> toStrings.apply(cfg.get());
        // 默认读取（来自 spec，不用你手写 def）
        java.util.function.Supplier<java.util.List<String>> defSup = () ->
                java.util.List.copyOf(toStrings.apply(cfg.getDefault()));

        return ConfigLazy.<java.util.List<String>>of(
                raw,
                defSup,
                (java.util.List<String> xs) -> xs != null,                 // 验证：非空即可
                (java.util.List<String> xs) -> {                           // 规范化：trim/小写/排序/去重
                    java.util.ArrayList<String> out = new java.util.ArrayList<>();
                    for (String s : xs) {
                        if (s == null)
                            continue;
                        String t = s.trim();
                        if (lowerCase)
                            t = t.toLowerCase(java.util.Locale.ROOT);
                        if (!t.isEmpty())
                            out.add(t);
                    }
                    if (sort)
                        out.sort(java.util.Comparator.naturalOrder());
                    if (distinct)
                        out = new java.util.ArrayList<>(new java.util.LinkedHashSet<>(out));
                    return java.util.Collections.unmodifiableList(out);
                }
        );
    }


    /**
     * 通用的“懒加载配置缓存”。
     */
    public static final class ConfigLazy<T> {
        private static final ConcurrentLinkedQueue<ConfigLazy<?>> REGISTRY = new ConcurrentLinkedQueue<>();

        private final Supplier<T> rawSupplier;         // 通常是 () -> cfgValue.get()
        private final Supplier<T> defaultSupplier;     // 默认值（永不为 null）
        private final Predicate<? super T> validator;  // 整体校验（允许始终为 true）
        private final Function<? super T, ? extends T> normalizer; // 规范化（可为 Function.identity()）
        private final AtomicReference<T> cache = new AtomicReference<>(null);

        private ConfigLazy(
                Supplier<T> rawSupplier,
                Supplier<T> defaultSupplier,
                Predicate<? super T> validator,
                Function<? super T, ? extends T> normalizer) {
            this.rawSupplier = Objects.requireNonNull(rawSupplier, "rawSupplier");
            this.defaultSupplier = Objects.requireNonNull(defaultSupplier, "defaultSupplier");
            this.validator = Objects.requireNonNull(validator, "validator");
            this.normalizer = Objects.requireNonNull(normalizer, "normalizer");
            REGISTRY.add(this);
        }

        /**
         * 获取值：首次加载并缓存；失败时回退默认值；永不返回 null。
         */
        public T get() {
            T v = cache.get();
            if (v != null)
                return v;

            T loaded = safeLoad();
            cache.compareAndSet(null, loaded);
            return cache.get();
        }

        /**
         * 主动失效缓存：下次 get() 会重新读取配置。
         */
        public void invalidate() {
            cache.set(null);
        }

        private T safeLoad() {
            T raw;
            try {
                raw = rawSupplier.get();
            }
            catch (Throwable t) {
                return defaultSupplier.get();
            }
            if (raw == null)
                return defaultSupplier.get();

            T norm;
            try {
                norm = normalizer.apply(raw);
            }
            catch (Throwable t) {
                return defaultSupplier.get();
            }
            return validator.test(norm) ? norm : defaultSupplier.get();
        }

        /* ------------ 工厂方法 ------------- */

        public static <T> ConfigLazy<T> of(
                Supplier<T> rawSupplier,
                Supplier<T> defaultSupplier,
                Predicate<? super T> validator,
                Function<? super T, ? extends T> normalizer) {
            return new ConfigLazy<>(rawSupplier, defaultSupplier, validator, normalizer);
        }

        public static <T> ConfigLazy<T> of(Supplier<T> rawSupplier, Supplier<T> defaultSupplier) {
            return of(rawSupplier, defaultSupplier, x -> true, Function.identity());
        }

        /**
         * 在 ModConfigEvent(Loading/Reloading) 时调用：统一失效所有缓存。
         */
        public static void invalidateAll() {
            REGISTRY.forEach(ConfigLazy::invalidate);
        }
    }


}

