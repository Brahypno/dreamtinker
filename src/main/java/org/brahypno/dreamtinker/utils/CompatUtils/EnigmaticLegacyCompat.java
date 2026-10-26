package org.brahypno.dreamtinker.utils.CompatUtils;

import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.effects.GrowingBloodlustEffect;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.AstralBreaker;
import com.aizistral.enigmaticlegacy.items.CursedRing;
import com.aizistral.enigmaticlegacy.items.EldritchPan;
import com.aizistral.enigmaticlegacy.items.EnderSlayer;
import com.aizistral.enigmaticlegacy.items.TheInfinitum;
import com.aizistral.enigmaticlegacy.items.TheTwist;
import com.aizistral.enigmaticlegacy.objects.RegisteredMeleeAttack;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.enigmaticlegacy.triggers.UseUnholyGrailTrigger;
import org.brahypno.dreamtinker.Dreamtinker;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class EnigmaticLegacyCompat {
    private static final String MODID = "enigmaticlegacy";
    private static final String PAN_UNIQUE_KILLS = "PanUniqueKills";
    private static boolean loadedResolved;
    private static boolean loaded;
    private static boolean bloodlustResolved;
    private static MobEffect bloodlust;
    private static boolean hungerResolved;
    private static MobEffect hunger;
    private static boolean unholyGrailResolved;
    private static Item unholyGrail = Items.AIR;
    private static boolean cursedScrollResolved;
    private static Item cursedScroll = Items.AIR;
    private static final AtomicBoolean FAILURE_REPORTED = new AtomicBoolean();

    private EnigmaticLegacyCompat() {}

    public static boolean isTheCursedOne(Player player) {
        return player != null && loaded() && Loaded.isTheCursedOne(player);
    }

    public static boolean isTheWorthyOne(Player player) {
        return player != null && loaded() && Loaded.isTheWorthyOne(player);
    }

    public static boolean isTheBlessedOne(Player player) {
        return player != null && loaded() && Loaded.isTheBlessedOne(player);
    }

    public static boolean hasArchitectsFavor(Player player) {
        return player != null && loaded() && Loaded.hasArchitectsFavor(player);
    }

    public static boolean cannotHunger(Player player) {
        return player != null && loaded() && Loaded.cannotHunger(player);
    }

    public static int getCurseAmount(Player player) {
        return player != null && loaded() ? Loaded.getCurseAmount(player) : 0;
    }

    public static int getCurseAmount(ItemStack stack) {
        return stack != null && !stack.isEmpty() && loaded() ? Loaded.getCurseAmount(stack) : 0;
    }

    public static boolean haveConsumedForbiddenFruit(Player player) {
        return player != null && loaded() && Loaded.haveConsumedForbiddenFruit(player);
    }

    public static boolean isBossOrPlayer(LivingEntity living) {
        return living != null && loaded() && Loaded.isBossOrPlayer(living);
    }

    public static boolean isEndDweller(LivingEntity living) {
        return living != null && loaded() && Loaded.isEndDweller(living);
    }

    public static boolean isTheEnd(Level level) {
        return level != null && level.dimension().equals(Level.END);
    }

    public static float registeredAttackStrength(LivingEntity attacker) {
        return attacker != null && loaded() ? Loaded.registeredAttackStrength(attacker) : 1F;
    }

    public static boolean isUnholyGrail(ItemStack stack) {
        Item item = unholyGrail();
        return stack != null && !stack.isEmpty() && item != Items.AIR && stack.is(item);
    }

    public static void triggerUseUnholyGrail(ServerPlayer player, boolean isTheWorthyOne) {
        if (player != null && loaded())
            Loaded.triggerUseUnholyGrail(player, isTheWorthyOne);
    }

    public static boolean isCursedScroll(ItemStack stack) {
        Item item = cursedScroll();
        return stack != null && !stack.isEmpty() && item != Items.AIR && stack.is(item);
    }

    public static Item unholyGrailItem() {
        return unholyGrail();
    }

    public static Item cursedScrollItem() {
        return cursedScroll();
    }

    public static MobEffect growingBloodlust() {
        if (!loaded())
            return null;

        if (!bloodlustResolved){
            bloodlustResolved = true;
            bloodlust = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(MODID, "growing_bloodlust"));
        }

        return bloodlust;
    }

    public static MobEffect growingHunger() {
        if (!loaded())
            return null;

        if (!hungerResolved){
            hungerResolved = true;
            hunger = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(MODID, "growing_hunger"));
        }

        return hunger;
    }

    public static double eldritchPanLifeSteal() {
        return loaded() ? Loaded.eldritchPanLifeSteal() : 0D;
    }

    public static double eldritchPanHungerSteal() {
        return loaded() ? Loaded.eldritchPanHungerSteal() : 0D;
    }

    public static double eldritchPanUniqueDamageGain() {
        return loaded() ? Loaded.eldritchPanUniqueDamageGain() : 0D;
    }

    public static double eldritchPanUniqueArmorGain() {
        return loaded() ? Loaded.eldritchPanUniqueArmorGain() : 0D;
    }

    public static int eldritchPanUniqueGainLimit() {
        return loaded() ? Loaded.eldritchPanUniqueGainLimit() : 100;
    }

    public static int bloodlustTicksPerLevel() {
        return loaded() ? Math.max(1, Loaded.bloodlustTicksPerLevel()) : 200;
    }

    public static double bloodlustLifestealBoost() {
        return loaded() ? Loaded.bloodlustLifestealBoost() : 0D;
    }

    public static double cursedRingSuperCursedTime() {
        return loaded() ? Loaded.cursedRingSuperCursedTime() : 1D;
    }

    public static float enderSlayerEndDamageBonusModifier() {
        return loaded() ? Loaded.enderSlayerEndDamageBonusModifier() : 0F;
    }

    public static float enderSlayerEndKnockbackBonusModifier() {
        return loaded() ? Loaded.enderSlayerEndKnockbackBonusModifier() : 0F;
    }

    public static int astralBreakerMiningRadius() {
        return loaded() ? Loaded.astralBreakerMiningRadius() : -1;
    }

    public static int astralBreakerMiningDepth() {
        return loaded() ? Loaded.astralBreakerMiningDepth() : 1;
    }

    public static double twistKnockbackBonusModifier() {
        return loaded() ? Loaded.twistKnockbackBonusModifier() : 0D;
    }

    public static double twistBossDamageBonusModifier() {
        return loaded() ? Loaded.twistBossDamageBonusModifier() : 0D;
    }

    public static double infinitumKnockbackBonusModifier() {
        return loaded() ? Loaded.infinitumKnockbackBonusModifier() : 0D;
    }

    public static double infinitumBossDamageBonusModifier() {
        return loaded() ? Loaded.infinitumBossDamageBonusModifier() : 0D;
    }

    public static double infinitumUndeadProbabilityMultiplier() {
        return loaded() ? Loaded.infinitumUndeadProbabilityMultiplier() : 0D;
    }

    public static double etheriumShieldThresholdMultiplier(Player player) {
        return loaded() ? Loaded.etheriumShieldThresholdMultiplier(player) : 0D;
    }

    public static double etheriumShieldReductionModifierInverted() {
        return loaded() ? Loaded.etheriumShieldReductionModifierInverted() : 1D;
    }

    public static int etheriumAOEBoost(Player player) {
        return player != null && loaded() ? Loaded.etheriumAOEBoost(player) : 0;
    }

    public static SoundEvent etheriumShieldTriggerSound() {
        return loaded() ? Loaded.etheriumShieldTriggerSound() : SoundEvents.SHIELD_BLOCK;
    }

    public static String sufferingTime(@Nullable Player player) {
        return loaded() ? Loaded.sufferingTime(player) : "0%";
    }

    public static void setEldritchPanHoldingDuration(Player player, int ticks) {
        if (player != null && loaded())
            Loaded.setEldritchPanHoldingDuration(player, ticks);
    }

    public static BlockHitResult calcRayTrace(Level level, Player player, ClipContext.Fluid fluid) {
        Vec3 from = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        double reach = player.getBlockReach();
        Vec3 to = from.add(look.x * reach, look.y * reach, look.z * reach);
        return level.clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, fluid, player));
    }

    public static void spawnFlameParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel server){
            server.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                                 18, 0.35D, 0.35D, 0.35D, 0.02D);
        }
    }

    public static void indicateCursedOnesOnly(List<Component> list, @Nullable Player player) {
        ChatFormatting format = player != null && isTheCursedOne(player) ? ChatFormatting.GOLD : ChatFormatting.DARK_RED;
        list.add(Component.translatable("tooltip.enigmaticlegacy.cursedOnesOnly1").withStyle(format));
        list.add(Component.translatable("tooltip.enigmaticlegacy.cursedOnesOnly2").withStyle(format));
    }

    public static void indicateWorthyOnesOnly(List<Component> list, @Nullable Player player) {
        ChatFormatting format = player != null && isTheWorthyOne(player) ? ChatFormatting.GOLD : ChatFormatting.DARK_RED;
        double requiredCurse = roundToPlaces(100D * cursedRingSuperCursedTime(), 1);

        list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly1"));
        list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly2",
                                        Component.literal(requiredCurse + "%").withStyle(ChatFormatting.GOLD)).withStyle(format));
        list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly3"));
        list.add(Component.translatable("tooltip.enigmaticlegacy.void"));
        list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly4").withStyle(format)
                          .append(Component.literal(" " + sufferingTime(player)).withStyle(ChatFormatting.LIGHT_PURPLE)));
    }

    public static void indicateBlessedOnesOnly(List<Component> list, @Nullable Player player) {
        ChatFormatting format = player != null && isTheBlessedOne(player) ? ChatFormatting.GOLD : ChatFormatting.DARK_RED;
        list.add(Component.translatable("tooltip.enigmaticlegacy.blessedOnesOnly1").withStyle(format));
        list.add(Component.translatable("tooltip.enigmaticlegacy.blessedOnesOnly2").withStyle(format));
    }

    public static void addLocalizedString(List<Component> list, String key) {
        list.add(Component.translatable(key));
    }

    public static void addLocalizedFormattedString(List<Component> list, String key, ChatFormatting format) {
        list.add(Component.translatable(key).withStyle(format));
    }

    public static void addLocalizedString(List<Component> list, String key, @Nullable ChatFormatting format, Object... values) {
        Component[] components = new Component[values.length];

        for (int i = 0; i < values.length; i++) {
            MutableComponent component = values[i] instanceof Component comp ? comp.copy() : Component.literal(String.valueOf(values[i]));
            components[i] = format == null ? component : component.withStyle(format);
        }

        list.add(Component.translatable(key, (Object[]) components));
    }

    public static boolean astralBreakerMineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (stack == null || stack.isEmpty() || level == null || state == null || pos == null || entity == null || !loaded())
            return false;
        return Loaded.astralBreakerMineBlock(stack, level, state, pos, entity);
    }

    public static int eldritchPanKillCount(ItemStack pan) {
        CompoundTag tag = pan == null ? null : pan.getTag();
        if (tag != null && tag.contains(PAN_UNIQUE_KILLS, Tag.TAG_LIST))
            return tag.getList(PAN_UNIQUE_KILLS, Tag.TAG_STRING).size();

        return 0;
    }

    public static List<ResourceLocation> eldritchPanUniqueKills(ItemStack pan) {
        CompoundTag tag = pan == null ? null : pan.getTag();
        if (tag == null || !tag.contains(PAN_UNIQUE_KILLS, Tag.TAG_LIST))
            return Collections.emptyList();

        ListTag list = tag.getList(PAN_UNIQUE_KILLS, Tag.TAG_STRING);
        List<ResourceLocation> kills = new ArrayList<>(list.size());

        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(list.getString(i));
            if (id != null)
                kills.add(id);
        }

        return kills;
    }

    public static void addEldritchPanUniqueKill(ItemStack pan, ResourceLocation mob) {
        if (pan == null || pan.isEmpty() || mob == null)
            return;

        CompoundTag tag = pan.getOrCreateTag();
        ListTag list = tag.contains(PAN_UNIQUE_KILLS, Tag.TAG_LIST) ? tag.getList(PAN_UNIQUE_KILLS, Tag.TAG_STRING) : new ListTag();

        list.add(StringTag.valueOf(mob.toString()));
        tag.put(PAN_UNIQUE_KILLS, list);
        pan.setTag(tag);
    }

    public static boolean addEldritchPanKillIfNotPresent(ItemStack pan, ResourceLocation mob) {
        if (pan == null || pan.isEmpty() || mob == null)
            return false;

        List<ResourceLocation> kills = eldritchPanUniqueKills(pan);
        if (kills.size() >= eldritchPanUniqueGainLimit() || kills.contains(mob))
            return false;

        addEldritchPanUniqueKill(pan, mob);
        return true;
    }

    private static Item unholyGrail() {
        if (!unholyGrailResolved) {
            unholyGrailResolved = true;
            unholyGrail = resolveItem("unholy_grail");
        }
        return unholyGrail;
    }

    private static Item cursedScroll() {
        if (!cursedScrollResolved) {
            cursedScrollResolved = true;
            cursedScroll = resolveItem("cursed_scroll");
        }
        return cursedScroll;
    }

    private static Item resolveItem(String path) {
        if (!loaded())
            return Items.AIR;
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, path));
        return item == null ? Items.AIR : item;
    }

    private static boolean loaded() {
        if (!loadedResolved) {
            loadedResolved = true;
            loaded = ModList.get().isLoaded(MODID);
        }
        return loaded;
    }

    private static double roundToPlaces(double value, int places) {
        double scale = Math.pow(10D, places);
        return Math.round(value * scale) / scale;
    }

    // No EL classes occur in facade signatures or static initialization.
    private static final class Loaded {
        private static <T> T call(Supplier<T> action, T fallback) {
            try {
                T value = action.get();
                return value != null ? value : fallback;
            } catch (RuntimeException exception) {
                if (FAILURE_REPORTED.compareAndSet(false, true))
                    Dreamtinker.LOGGER.warn("Failed to read or invoke Enigmatic Legacy data; later attempts remain enabled", exception);
                return fallback;
            }
        }

        private static double number(Supplier<? extends Number> action, double fallback) {
            Number value = call(action::get, null);
            return value != null ? value.doubleValue() : fallback;
        }

        private static boolean isTheCursedOne(Player player) {
            return call(() -> SuperpositionHandler.isTheCursedOne(player), false);
        }

        private static boolean isTheWorthyOne(Player player) {
            return call(() -> SuperpositionHandler.isTheWorthyOne(player), false);
        }

        private static boolean isTheBlessedOne(Player player) {
            return call(() -> SuperpositionHandler.isTheBlessedOne(player), false);
        }

        private static boolean hasArchitectsFavor(Player player) {
            return call(() -> SuperpositionHandler.hasArchitectsFavor(player), false);
        }

        private static boolean cannotHunger(Player player) {
            return call(() -> SuperpositionHandler.cannotHunger(player), false);
        }

        private static int getCurseAmount(Player player) {
            return call(() -> SuperpositionHandler.getCurseAmount(player), 0);
        }

        private static int getCurseAmount(ItemStack stack) {
            return call(() -> SuperpositionHandler.getCurseAmount(stack), 0);
        }

        private static boolean haveConsumedForbiddenFruit(Player player) {
            return call(() -> EnigmaticItems.FORBIDDEN_FRUIT.haveConsumedFruit(player), false);
        }

        private static boolean isBossOrPlayer(LivingEntity living) {
            return call(() -> OmniconfigHandler.isBossOrPlayer(living), false);
        }

        private static boolean isEndDweller(LivingEntity living) {
            return call(() -> EnigmaticItems.ENDER_SLAYER.isEndDweller(living), false);
        }

        private static float registeredAttackStrength(LivingEntity living) {
            return call(() -> RegisteredMeleeAttack.getRegisteredAttackStregth(living), 1F);
        }

        private static void triggerUseUnholyGrail(ServerPlayer player, boolean worthy) {
            call(() -> {
                UseUnholyGrailTrigger.INSTANCE.trigger(player, worthy);
                return true;
            }, false);
        }

        private static double eldritchPanLifeSteal() {
            return number(() -> EldritchPan.lifeSteal.getValue(), 0D);
        }

        private static double eldritchPanHungerSteal() {
            return number(() -> EldritchPan.hungerSteal.getValue(), 0D);
        }

        private static double eldritchPanUniqueDamageGain() {
            return number(() -> EldritchPan.uniqueDamageGain.getValue(), 0D);
        }

        private static double eldritchPanUniqueArmorGain() {
            return number(() -> EldritchPan.uniqueArmorGain.getValue(), 0D);
        }

        private static int eldritchPanUniqueGainLimit() {
            return (int) number(() -> EldritchPan.uniqueGainLimit.getValue(), 100);
        }

        private static int bloodlustTicksPerLevel() {
            return (int) number(() -> GrowingBloodlustEffect.ticksPerLevel.getValue(), 200);
        }

        private static double bloodlustLifestealBoost() {
            return number(() -> GrowingBloodlustEffect.lifestealBoost.getValue(), 0D);
        }

        private static double cursedRingSuperCursedTime() {
            return number(() -> CursedRing.superCursedTime.getValue(), 1D);
        }

        private static float enderSlayerEndDamageBonusModifier() {
            return (float) number(() -> EnderSlayer.endDamageBonus.getValue().asModifier(false), 0F);
        }

        private static float enderSlayerEndKnockbackBonusModifier() {
            return (float) number(() -> EnderSlayer.endKnockbackBonus.getValue().asModifier(false), 0F);
        }

        private static int astralBreakerMiningRadius() {
            return (int) number(() -> AstralBreaker.miningRadius.getValue(), -1);
        }

        private static int astralBreakerMiningDepth() {
            return (int) number(() -> AstralBreaker.miningDepth.getValue(), 1);
        }

        private static double twistKnockbackBonusModifier() {
            return number(() -> TheTwist.knockbackBonus.getValue().asModifier(false), 0D);
        }

        private static double twistBossDamageBonusModifier() {
            return number(() -> TheTwist.bossDamageBonus.getValue().asModifier(false), 0D);
        }

        private static double infinitumKnockbackBonusModifier() {
            return number(() -> TheInfinitum.knockbackBonus.getValue().asModifier(false), 0D);
        }

        private static double infinitumBossDamageBonusModifier() {
            return number(() -> TheInfinitum.bossDamageBonus.getValue().asModifier(false), 0D);
        }

        private static double infinitumUndeadProbabilityMultiplier() {
            return number(() -> TheInfinitum.undeadProbability.getValue().asMultiplier(false), 0D);
        }

        private static double etheriumShieldThresholdMultiplier(Player player) {
            return number(() -> EtheriumConfigHandler.instance().getShieldThreshold(player).asMultiplier(false), 0D);
        }

        private static double etheriumShieldReductionModifierInverted() {
            return number(() -> EtheriumConfigHandler.instance().getShieldReduction().asModifierInverted(), 1D);
        }

        private static int etheriumAOEBoost(Player player) {
            return call(() -> EtheriumConfigHandler.instance().getAOEBoost(player), 0);
        }

        private static SoundEvent etheriumShieldTriggerSound() {
            SoundEvent sound = call(() -> EtheriumConfigHandler.instance().getShieldTriggerSound(), null);
            if (sound == null)
                sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(MODID, "shield_trigger"));
            return sound != null ? sound : SoundEvents.SHIELD_BLOCK;
        }

        private static String sufferingTime(Player player) {
            return call(() -> SuperpositionHandler.getSufferingTime(player), "0%");
        }

        private static void setEldritchPanHoldingDuration(Player player, int ticks) {
            EldritchPan.HOLDING_DURATIONS.put(player, ticks);
        }

        private static boolean astralBreakerMineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
            return call(() -> EnigmaticItems.ASTRAL_BREAKER.mineBlock(stack, level, state, pos, entity), false);
        }
    }
}
