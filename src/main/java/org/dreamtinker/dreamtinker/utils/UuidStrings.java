package org.dreamtinker.dreamtinker.utils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class UuidStrings {
    // 简单 UUID 正则（大小写皆可）
    private static final Pattern UUID_RE = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    /**
     * 写入：把多个 UUID 转为一个字符串，使用分号分隔
     */
    public static String serialize(Collection<UUID> uuids) {
        if (uuids == null || uuids.isEmpty())
            return "";
        // 去重并保证固定顺序（可选）
        return uuids.stream()
                    .filter(Objects::nonNull)
                    .map(UUID::toString)
                    .distinct()
                    .sorted()                // 需要稳定序时开启
                    .collect(Collectors.joining(";"));
    }

    /**
     * 读取：从分号分隔的字符串还原为 List<UUID>（自动跳过非法/空条目）
     */
    public static List<UUID> deserialize(String s) {
        if (s == null || s.isBlank())
            return List.of();
        String[] parts = s.split(";");
        List<UUID> out = new ArrayList<>(parts.length);
        for (String p : parts) {
            String t = p.trim();
            if (UUID_RE.matcher(t).matches()){
                try {
                    out.add(UUID.fromString(t));
                }
                catch (IllegalArgumentException ignored) {}
            }
        }
        return out;
    }

    /**
     * 方便地追加一个 UUID（保持去重）
     */
    public static String add(String s, UUID u) {
        if (u == null)
            return (s == null ? "" : s);
        Set<UUID> set = new LinkedHashSet<>(deserialize(s));
        set.add(u);
        return serialize(set);
    }

    /**
     * 删除一个 UUID
     */
    public static String remove(String s, UUID u) {
        if (u == null)
            return (s == null ? "" : s);
        Set<UUID> set = new LinkedHashSet<>(deserialize(s));
        set.remove(u);
        return serialize(set);
    }
}
