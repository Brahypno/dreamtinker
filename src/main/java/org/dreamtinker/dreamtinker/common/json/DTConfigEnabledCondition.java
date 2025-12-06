package org.dreamtinker.dreamtinker.common.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DTConfigEnabledCondition implements ICondition {
    public static final ResourceLocation ID = Dreamtinker.getLocation("mod_compact_config");
    public static final ConfigSerializer SERIALIZER = new ConfigSerializer();
    /* Map of config names to condition cache */
    private static final Map<String, DTConfigEnabledCondition> PROPS = new HashMap<>();

    private final String configName;

    public DTConfigEnabledCondition(String modid) {
        configName = modid;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext context) {
        return !Dreamtinker.configCompactDisabled(configName);
    }

    private static class ConfigSerializer implements Serializer<DTConfigEnabledCondition>, IConditionSerializer<DTConfigEnabledCondition> {
        @Override
        public ResourceLocation getID() {
            return ID;
        }

        @Override
        public void write(JsonObject json, DTConfigEnabledCondition value) {
            json.addProperty("modid", value.configName);
        }

        @Override
        public DTConfigEnabledCondition read(JsonObject json) {
            String modid = GsonHelper.getAsString(json, "modid");
            return new DTConfigEnabledCondition(modid);
        }

        @Override
        public void serialize(JsonObject json, DTConfigEnabledCondition condition, JsonSerializationContext context) {
            write(json, condition);
        }

        @Override
        public @NotNull DTConfigEnabledCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return read(json);
        }
    }

    /**
     * Adds a condition
     *
     * @param modid modiderty name
     * @return Added condition
     */
    public static DTConfigEnabledCondition add(String modid) {
        DTConfigEnabledCondition conf = new DTConfigEnabledCondition(modid);
        PROPS.put(modid.toLowerCase(Locale.ROOT), conf);
        return conf;
    }


    @Override
    public String toString() {
        return "config_setting_enabled(\"" + this.configName + "\")";
    }

}
