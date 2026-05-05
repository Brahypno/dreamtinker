package org.dreamtinker.dreamtinker.library.LootModifier;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.dreamtinker.dreamtinker.DreamtinkerModule;
import org.jetbrains.annotations.NotNull;

public class KillerIsEnemyCondition implements LootItemCondition {
    public static final KillerIsEnemyCondition INSTANCE = new KillerIsEnemyCondition();

    private KillerIsEnemyCondition() {}

    public static LootItemCondition.Builder builder() {
        return () -> INSTANCE;
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return DreamtinkerModule.KILLER_IS_ENEMY.get();
    }

    @Override
    public boolean test(LootContext context) {
        Entity killer = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        return killer instanceof Enemy;
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<KillerIsEnemyCondition> {
        @Override
        public void serialize(JsonObject json, KillerIsEnemyCondition condition, JsonSerializationContext context) {
        }

        @Override
        public KillerIsEnemyCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return INSTANCE;
        }
    }
}
