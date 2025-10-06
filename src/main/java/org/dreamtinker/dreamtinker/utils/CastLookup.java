package org.dreamtinker.dreamtinker.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CastLookup {
    // 三种常见后缀
    public static final String SUFFIX_CAST = "_cast";
    public static final String SUFFIX_SAND = "_sand_cast";
    public static final String SUFFIX_RED_SAND = "_red_sand_cast";

    /**
     * 结果打包（你也可以换成 ItemObject 或直接 ItemStack）
     */
    public record CastTriple(@Nullable Item cast, @Nullable Item sandCast, @Nullable Item redSandCast) {
        public List<Item> asListPresent() {
            List<Item> out = new ArrayList<>(3);
            if (cast != null)
                out.add(cast);
            if (sandCast != null)
                out.add(sandCast);
            if (redSandCast != null)
                out.add(redSandCast);
            return out;
        }
    }

    /**
     * 从一个 ToolPartItem 找到同名的 *_cast/_sand_cast/_red_sand_cast 物品
     */
    public static CastTriple findCastsForPart(ToolPartItem part) {
        ResourceLocation pid = ForgeRegistries.ITEMS.getKey(part);
        if (pid == null)
            return new CastTriple(null, null, null);

        String baseName = pid.getPath();

        // 1) 优先在 tconstruct 命名空间找（TCon 官方就是这么注册的）
        Item cast = getItem(new ResourceLocation("tconstruct", baseName + SUFFIX_CAST));
        Item sandCast = getItem(new ResourceLocation("tconstruct", baseName + SUFFIX_SAND));
        Item redCast = getItem(new ResourceLocation("tconstruct", baseName + SUFFIX_RED_SAND));

        // 2) 如果部件来自其他命名空间，也尝试在“同命名空间”找（有些扩展模组可能照搬命名规则在自己 namespace 下注册）
        if (cast == null)
            cast = getItem(new ResourceLocation(pid.getNamespace(), baseName + SUFFIX_CAST));
        if (sandCast == null)
            sandCast = getItem(new ResourceLocation(pid.getNamespace(), baseName + SUFFIX_SAND));
        if (redCast == null)
            redCast = getItem(new ResourceLocation(pid.getNamespace(), baseName + SUFFIX_RED_SAND));

        // 3) 兜底：全表扫描（防止 mod 用了非标准 namespace）
        if (cast == null || sandCast == null || redCast == null){
            for (Item it : ForgeRegistries.ITEMS.getValues()) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(it);
                if (id == null)
                    continue;
                String p = id.getPath();
                if (cast == null && p.equals(baseName + SUFFIX_CAST))
                    cast = it;
                if (sandCast == null && p.equals(baseName + SUFFIX_SAND))
                    sandCast = it;
                if (redCast == null && p.equals(baseName + SUFFIX_RED_SAND))
                    redCast = it;
            }
        }

        return new CastTriple(cast, sandCast, redCast);
    }

    /**
     * 批量版本：给 List<ToolPartItem> 直接映射到 CastTriple
     */
    public static Map<ToolPartItem, CastTriple> findCastsForParts(List<ToolPartItem> parts) {
        Map<ToolPartItem, CastTriple> out = new LinkedHashMap<>();
        for (ToolPartItem part : parts) {
            out.put(part, findCastsForPart(part));
        }
        return out;
    }

    private static Item getItem(ResourceLocation id) {
        return ForgeRegistries.ITEMS.getValue(id);
    }

    /**
     * 需要 ItemStack 时可封装一下
     */
    public static List<ItemStack> makeCastStacks(CastTriple triple) {
        List<ItemStack> out = new ArrayList<>(3);
        if (triple.cast() != null)
            out.add(new ItemStack(triple.cast()));
        if (triple.sandCast() != null)
            out.add(new ItemStack(triple.sandCast()));
        if (triple.redSandCast() != null)
            out.add(new ItemStack(triple.redSandCast()));
        return out;
    }
}

