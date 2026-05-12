package org.dreamtinker.dreamtinker.utils.LootHelper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.InputStream;
import java.util.*;

public final class LootTableFallbackResolver {
    private LootTableFallbackResolver() {}


    public static List<ResourceLocation> resolveLootTableCandidates(ServerLevel level, LivingEntity victim) {
        LinkedHashSet<ResourceLocation> candidates = new LinkedHashSet<>();

        ResourceLocation current = victim.getLootTable();
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType());
        String namespace = entityId.getNamespace();
        String entityPath = entityId.getPath();

        if (!BuiltInLootTables.EMPTY.equals(current)){
            candidates.add(current);
        }

        addIfLootTableExists(level, candidates, new ResourceLocation(namespace, "entities/" + entityPath));
        addIfLootTableExists(level, candidates, new ResourceLocation(namespace, entityPath));

        for (String constant : extractLootTableLikeConstants(victim.getClass())) {
            ResourceLocation direct = ResourceLocation.tryParse(constant);
            if (direct != null && direct.getNamespace() != null && constant.contains(":")){
                addIfLootTableExists(level, candidates, direct);
            }

            if (isPossibleLootPathConstant(constant)){
                addIfLootTableExists(level, candidates, new ResourceLocation(namespace, constant));
                addIfLootTableExists(level, candidates, new ResourceLocation(namespace, "entities/" + constant));
            }
        }

        candidates.remove(BuiltInLootTables.EMPTY);
        return new ArrayList<>(candidates);
    }

    private static void addIfLootTableExists(ServerLevel level, Set<ResourceLocation> out, ResourceLocation tableId) {
        if (lootTableJsonExists(level, tableId)){
            out.add(tableId);
        }
    }

    private static boolean lootTableJsonExists(ServerLevel level, ResourceLocation tableId) {
        ResourceLocation jsonId = new ResourceLocation(
                tableId.getNamespace(),
                "loot_tables/" + tableId.getPath() + ".json"
        );

        Optional<Resource> resource = level.getServer().getResourceManager().getResource(jsonId);
        return resource.isPresent();
    }

    private static List<String> extractLootTableLikeConstants(Class<?> entityClass) {
        LinkedHashSet<String> constants = new LinkedHashSet<>();

        String classResource = "/" + entityClass.getName().replace('.', '/') + ".class";

        try (InputStream in = entityClass.getResourceAsStream(classResource)) {
            if (in == null){
                return List.of();
            }

            ClassReader reader = new ClassReader(in);
            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    boolean target =
                            ("getDefaultLootTable".equals(name) || "m_7582_".equals(name))
                            && "()Lnet/minecraft/resources/ResourceLocation;".equals(descriptor);

                    if (!target){
                        return null;
                    }

                    return new MethodVisitor(Opcodes.ASM9) {
                        @Override
                        public void visitLdcInsn(Object value) {
                            if (value instanceof String s){
                                constants.add(s);
                            }
                        }
                    };
                }
            }, ClassReader.SKIP_FRAMES);

            return new ArrayList<>(constants);
        }
        catch (Throwable ignored) {
            return List.of();
        }
    }

    private static boolean isPossibleLootPathConstant(String s) {
        if (s == null || s.isEmpty()){
            return false;
        }

        if (s.length() > 128){
            return false;
        }

        if (s.contains(" ") || s.contains("{") || s.contains("}") || s.contains("\n")){
            return false;
        }

        if (s.startsWith("block.") || s.startsWith("entity.") || s.startsWith("item.")){
            return false;
        }

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean ok =
                    c >= 'a' && c <= 'z'
                    || c >= '0' && c <= '9'
                    || c == '_'
                    || c == '/'
                    || c == '-'
                    || c == '.'
                    || c == ':';

            if (!ok){
                return false;
            }
        }

        return true;
    }
}