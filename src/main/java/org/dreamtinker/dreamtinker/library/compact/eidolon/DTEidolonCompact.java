package org.dreamtinker.dreamtinker.library.compact.eidolon;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

public final class DTEidolonCompact {
    private static boolean injectedCodex = false;

    private DTEidolonCompact() {}

    public static void init() {
        addAltarEntry(DreamtinkerCommon.potted_narcissus.get(), "plant", 5.0D, 0.0D);
        addAltarEntry(TinkerSmeltery.scorchedLantern.get(), "light", 0.0D, 5.0D);
        addAltarEntry(TinkerSmeltery.searedLantern.get(), "light", 5.0D, 0.0D);
        addAltarEntry(TinkerWorld.heads.get(TinkerHeadType.PIGLIN_BRUTE), "skull", 5.0D, 5.0D);
    }

    public static void onAnyForgeEvent(Event event) {
        if (injectedCodex)
            return;
        if (!ModList.get().isLoaded("eidolon"))
            return;

        String name = event.getClass().getName();
        if (!name.equals("elucent.eidolon.codex.CodexEvents$PostInit"))
            return;

        injectedCodex = true;
        appendAltarCodexListEntry("ALTAR_HERBS", 1, "narcissus", new ItemStack(DreamtinkerCommon.narcissus.get()), DreamtinkerCommon.potted_narcissus.get());
        appendAltarCodexListEntry("ALTAR_LIGHTS", 1, "scorched_lantern", new ItemStack(TinkerSmeltery.scorchedLantern), TinkerSmeltery.scorchedLantern.get());
        appendAltarCodexListEntry("ALTAR_LIGHTS", 1, "seared_lantern", new ItemStack(TinkerSmeltery.searedLantern), TinkerSmeltery.searedLantern.get());
        appendAltarCodexListEntry("ALTAR_SKULLS", 1, "piglin_brute", new ItemStack(TinkerWorld.heads.get(TinkerHeadType.PIGLIN_BRUTE)),
                                  TinkerWorld.heads.get(TinkerHeadType.PIGLIN_BRUTE));

    }

    public static void addAltarEntry(Block block, String eidolonKeyPath, double power, double capacity) {
        if (!ModList.get().isLoaded("eidolon"))
            return;

        try {
            Class<?> altarEntriesClass = Class.forName("elucent.eidolon.registries.AltarEntries");
            Class<?> altarEntryClass = Class.forName("elucent.eidolon.api.altar.AltarEntry");

            Field entriesField = altarEntriesClass.getDeclaredField("entries");
            entriesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<Block, Object> entries = (Map<Block, Object>) entriesField.get(null);

            Object altarEntry = altarEntryClass
                    .getConstructor(ResourceLocation.class)
                    .newInstance(new ResourceLocation("eidolon", eidolonKeyPath));

            if (power != 0.0D)
                altarEntryClass.getMethod("setPower", double.class).invoke(altarEntry, power);
            if (capacity != 0.0D)
                altarEntryClass.getMethod("setCapacity", double.class).invoke(altarEntry, capacity);

            entries.put(block, altarEntry);
        }
        catch (ReflectiveOperationException e) {
            Dreamtinker.LOGGER.error("Failed to add Eidolon altar entry: block={}, key={}", BuiltInRegistries.BLOCK.getKey(block), eidolonKeyPath, e);
        }
    }

    public static void appendAltarCodexListEntry(String chapterFieldName, int pageIndex, String key, ItemStack icon, Block altarBlock) {
        if (!ModList.get().isLoaded("eidolon"))
            return;

        try {
            Class<?> codexChaptersClass = Class.forName("elucent.eidolon.codex.CodexChapters");
            Class<?> chapterClass = Class.forName("elucent.eidolon.codex.Chapter");
            Class<?> listPageClass = Class.forName("elucent.eidolon.codex.ListPage");
            Class<?> listEntryClass = Class.forName("elucent.eidolon.codex.ListPage$ListEntry");

            Field chapterField = codexChaptersClass.getDeclaredField(chapterFieldName);
            Object chapter = chapterField.get(null);

            Object page = chapterClass.getMethod("get", int.class).invoke(chapter, pageIndex);
            if (page == null || !listPageClass.isInstance(page)){
                Dreamtinker.LOGGER.warn("Eidolon codex chapter {} page {} is not a ListPage", chapterFieldName, pageIndex);
                return;
            }

            Constructor<?> listEntryCtor = listEntryClass.getConstructor(String.class, ItemStack.class, Block.class);
            Object newEntry = listEntryCtor.newInstance(key, icon, altarBlock);

            Field entriesField = listPageClass.getDeclaredField("entries");
            entriesField.setAccessible(true);

            Object oldArray = entriesField.get(page);
            int oldLength = Array.getLength(oldArray);

            Object newArray = Array.newInstance(listEntryClass, oldLength + 1);
            for (int i = 0; i < oldLength; i++)
                Array.set(newArray, i, Array.get(oldArray, i));
            Array.set(newArray, oldLength, newEntry);

            entriesField.set(page, newArray);
        }
        catch (ReflectiveOperationException e) {
            Dreamtinker.LOGGER.error("Failed to append Eidolon codex list entry: chapter={}, page={}, key={}", chapterFieldName, pageIndex, key, e);
        }
    }
}
