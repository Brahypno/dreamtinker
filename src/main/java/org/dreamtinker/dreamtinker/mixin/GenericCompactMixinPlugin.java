package org.dreamtinker.dreamtinker.mixin;


import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.forgespi.language.IModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.*;

public class GenericCompactMixinPlugin implements IMixinConfigPlugin {
    /**
     * 约定：包名里出现 ".compact.<modid>." 就认为这是可选联动 mixin
     */
    private static final String COMPACT_SEGMENT = ".compact.";

    /**
     * 可选：别名映射（写错/习惯写法 -> 真实modid）
     * 例如 Ars Nouveau 的真实 modid 常见是 "ars_nouveau"
     */
    private static final Map<String, String> MOD_ID_ALIASES = Map.of(
            "ars_nouveau", "malum"
    );

    private final Map<String, Boolean> cache = new HashMap<>();
    private Set<String> loadedModIds = Collections.emptySet();

    @Override
    public void onLoad(String mixinPackage) {
        // 在 Mixin plugin 加载时就抓取“正在加载的 mod 列表”（此时 ModList 可能还不可用，但 LoadingModList 可用）
        try {
            Set<String> ids = new HashSet<>();
            for (IModInfo info : LoadingModList.get().getMods()) {
                ids.add(info.getModId());
            }
            loadedModIds = Collections.unmodifiableSet(ids);
        }
        catch (Throwable t) {
            // 极端情况下兜底：保持空集合，意味着所有 compat mixin 都不会应用（宁可少应用也别崩）
            loadedModIds = Collections.emptySet();
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String modid = extractCompactModId(mixinClassName);
        if (modid == null){
            return true; // 非 compact mixin 永远应用
        }

        // alias -> real modid
        modid = MOD_ID_ALIASES.getOrDefault(modid, modid);

        return cache.computeIfAbsent(modid, id -> loadedModIds.contains(id));
    }

    /**
     * 从 mixin 类全名里提取 compact modid：xxx.compact.<modid>.yyy
     */
    private static String extractCompactModId(String mixinClassName) {
        int idx = mixinClassName.indexOf(COMPACT_SEGMENT);
        if (idx < 0)
            return null;

        int start = idx + COMPACT_SEGMENT.length();
        if (start >= mixinClassName.length())
            return null;

        int end = mixinClassName.indexOf('.', start);
        if (end < 0)
            return null;

        String modid = mixinClassName.substring(start, end).trim();
        if (modid.isEmpty())
            return null;

        return modid;
    }

    // ---- 下面这些方法按接口要求提供空实现即可 ----
    @Override
    public String getRefMapperConfig() {return null;}

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {return null;}

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}

