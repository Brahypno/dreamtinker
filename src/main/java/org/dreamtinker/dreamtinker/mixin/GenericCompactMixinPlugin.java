package org.dreamtinker.dreamtinker.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.forgespi.language.IModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.*;

public class GenericCompactMixinPlugin implements IMixinConfigPlugin {
    private static final String COMPACT_SEGMENT = ".compact.";

    private final Map<String, Boolean> cache = new HashMap<>();
    private Set<String> loadedModIds = Collections.emptySet();

    private static boolean hasClassResource(String className) {
        if (className == null || className.isBlank())
            return false;

        String path = className.replace('.', '/') + ".class";
        ClassLoader context = Thread.currentThread().getContextClassLoader();
        if (context != null && context.getResource(path) != null)
            return true;

        ClassLoader fallback = GenericCompactMixinPlugin.class.getClassLoader();
        return fallback != null && fallback.getResource(path) != null;
    }

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
        return modid.isEmpty() ? null : modid;
    }

    @Override
    public void onLoad(String mixinPackage) {
        try {
            Set<String> ids = new HashSet<>();
            for (IModInfo info : LoadingModList.get().getMods())
                ids.add(info.getModId());
            loadedModIds = Collections.unmodifiableSet(ids);
        }
        catch (Throwable ignored) {
            loadedModIds = Collections.emptySet();
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String modid = extractCompactModId(mixinClassName);
        if (modid == null)
            return true;

        return cache.computeIfAbsent(mixinClassName, ignored -> loadedModIds.contains(modid) && hasClassResource(targetClassName));
    }

    @Override
    public String getRefMapperConfig() {return null;}

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {return null;}

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}