package org.brahypno.dreamtinker.utils.CompatUtils;

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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class EnigmaticLegacyCompat {
    private static final String MODID = "enigmaticlegacy";
    private static final String ITEMS = "com.aizistral.enigmaticlegacy.registries.EnigmaticItems";
    private static final String SUPERPOSITION = "com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler";
    private static final String OMNICONFIG = "com.aizistral.enigmaticlegacy.config.OmniconfigHandler";
    private static final String ETHERIUM_CONFIG = "com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler";
    private static final String ELDRITCH_PAN = "com.aizistral.enigmaticlegacy.items.EldritchPan";
    private static final String CURSED_RING = "com.aizistral.enigmaticlegacy.items.CursedRing";
    private static final String ENDER_SLAYER = "com.aizistral.enigmaticlegacy.items.EnderSlayer";
    private static final String ASTRAL_BREAKER = "com.aizistral.enigmaticlegacy.items.AstralBreaker";
    private static final String THE_TWIST = "com.aizistral.enigmaticlegacy.items.TheTwist";
    private static final String THE_INFINITUM = "com.aizistral.enigmaticlegacy.items.TheInfinitum";
    private static final String BLOODLUST_EFFECT = "com.aizistral.enigmaticlegacy.effects.GrowingBloodlustEffect";
    private static final String REGISTERED_ATTACK = "com.aizistral.enigmaticlegacy.objects.RegisteredMeleeAttack";
    private static final String USE_UNHOLY_GRAIL_TRIGGER = "com.aizistral.enigmaticlegacy.triggers.UseUnholyGrailTrigger";

    private static final String PAN_UNIQUE_KILLS = "PanUniqueKills";
    private static final CachedParam PAN_LIFE_STEAL = new CachedParam(ELDRITCH_PAN, "lifeSteal");
    private static final CachedParam PAN_HUNGER_STEAL = new CachedParam(ELDRITCH_PAN, "hungerSteal");
    private static final CachedParam PAN_UNIQUE_DAMAGE = new CachedParam(ELDRITCH_PAN, "uniqueDamageGain");
    private static final CachedParam PAN_UNIQUE_ARMOR = new CachedParam(ELDRITCH_PAN, "uniqueArmorGain");
    private static final CachedParam PAN_UNIQUE_LIMIT = new CachedParam(ELDRITCH_PAN, "uniqueGainLimit");
    private static final CachedParam CURSED_RING_SUPER_CURSED_TIME = new CachedParam(CURSED_RING, "superCursedTime");
    private static final CachedParam BLOODLUST_TICKS_PER_LEVEL = new CachedParam(BLOODLUST_EFFECT, "ticksPerLevel");
    private static final CachedParam BLOODLUST_LIFESTEAL_BOOST = new CachedParam(BLOODLUST_EFFECT, "lifestealBoost");
    private static final CachedParam ENDER_DAMAGE_BONUS = new CachedParam(ENDER_SLAYER, "endDamageBonus");
    private static final CachedParam ENDER_KNOCKBACK_BONUS = new CachedParam(ENDER_SLAYER, "endKnockbackBonus");
    private static final CachedParam ASTRAL_MINING_RADIUS = new CachedParam(ASTRAL_BREAKER, "miningRadius");
    private static final CachedParam ASTRAL_MINING_DEPTH = new CachedParam(ASTRAL_BREAKER, "miningDepth");
    private static final CachedParam TWIST_KNOCKBACK_BONUS = new CachedParam(THE_TWIST, "knockbackBonus");
    private static final CachedParam TWIST_BOSS_DAMAGE_BONUS = new CachedParam(THE_TWIST, "bossDamageBonus");
    private static final CachedParam INFINITUM_KNOCKBACK_BONUS = new CachedParam(THE_INFINITUM, "knockbackBonus");
    private static final CachedParam INFINITUM_BOSS_DAMAGE_BONUS = new CachedParam(THE_INFINITUM, "bossDamageBonus");
    private static final CachedParam INFINITUM_UNDEAD_PROBABILITY = new CachedParam(THE_INFINITUM, "undeadProbability");
    private static boolean loadedResolved;
    private static boolean loaded;
    private static boolean forbiddenFruitResolved;
    private static Object forbiddenFruit;
    private static Method haveConsumedFruit;
    private static boolean superpositionResolved;
    private static Method isTheCursedOne;
    private static Method isTheWorthyOne;
    private static Method isTheBlessedOne;
    private static Method hasArchitectsFavor;
    private static Method cannotHunger;
    private static Method getSufferingTime;
    private static Method getCurseAmountPlayer;
    private static Method getCurseAmountStack;
    private static boolean omniconfigResolved;
    private static Method isBossOrPlayer;
    private static boolean etheriumResolved;
    private static Object etheriumInstance;
    private static Method getShieldThreshold;
    private static Method getShieldReduction;
    private static Method getAOEBoost;
    private static Method getShieldTriggerSound;
    private static boolean registeredAttackResolved;
    private static Method getRegisteredAttackStrength;
    private static boolean panHoldingResolved;
    private static Map<Player, Integer> panHoldingDurations;
    private static boolean enderSlayerResolved;
    private static Object enderSlayer;
    private static Method isEndDweller;
    private static boolean bloodlustResolved;
    private static MobEffect bloodlust;
    private static boolean hungerResolved;
    private static MobEffect hunger;
    private static boolean shieldTriggerSoundResolved;
    private static SoundEvent shieldTriggerSound;
    private static boolean unholyGrailResolved;
    private static Item unholyGrail = Items.AIR;
    private static boolean useUnholyGrailTriggerResolved;
    private static Object useUnholyGrailTrigger;
    private static Method triggerUseUnholyGrail;
    private static boolean cursedScrollResolved;
    private static Item cursedScroll = Items.AIR;
    private static boolean astralBreakerResolved;
    private static Object astralBreaker;
    private static Method astralBreakerMineBlock;

    private EnigmaticLegacyCompat() {}

    public static boolean isTheCursedOne(Player player) {
        if (player == null || !loaded())
            return false;
        resolveSuperposition();
        return callBoolean(isTheCursedOne, null, player);
    }

    public static boolean isTheWorthyOne(Player player) {
        if (player == null || !loaded())
            return false;
        resolveSuperposition();
        return callBoolean(isTheWorthyOne, null, player);
    }

    public static boolean isTheBlessedOne(Player player) {
        if (player == null || !loaded())
            return false;
        resolveSuperposition();
        return callBoolean(isTheBlessedOne, null, player);
    }

    public static boolean hasArchitectsFavor(Player player) {
        if (player == null || !loaded())
            return false;
        resolveSuperposition();
        return callBoolean(hasArchitectsFavor, null, player);
    }

    public static boolean cannotHunger(Player player) {
        if (player == null || !loaded())
            return false;
        resolveSuperposition();
        return callBoolean(cannotHunger, null, player);
    }

    public static int getCurseAmount(Player player) {
        if (player == null || !loaded())
            return 0;
        resolveSuperposition();
        return (int) callNumber(getCurseAmountPlayer, null, player, 0D);
    }

    public static int getCurseAmount(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !loaded())
            return 0;
        resolveSuperposition();
        return (int) callNumber(getCurseAmountStack, null, stack, 0D);
    }

    public static boolean haveConsumedForbiddenFruit(Player player) {
        if (player == null || !loaded())
            return false;
        resolveForbiddenFruit();
        return callBoolean(haveConsumedFruit, forbiddenFruit, player);
    }

    public static boolean isBossOrPlayer(LivingEntity living) {
        if (living == null || !loaded())
            return false;
        resolveOmniconfig();
        return callBoolean(isBossOrPlayer, null, living);
    }

    public static boolean isEndDweller(LivingEntity living) {
        if (living == null || !loaded())
            return false;
        resolveEnderSlayer();
        return callBoolean(isEndDweller, enderSlayer, living);
    }

    public static boolean isTheEnd(Level level) {
        return level != null && level.dimension().equals(Level.END);
    }

    public static float registeredAttackStrength(LivingEntity attacker) {
        if (attacker == null || !loaded())
            return 1F;
        resolveRegisteredAttack();
        return (float) callNumber(getRegisteredAttackStrength, null, attacker, 1D);
    }

    public static boolean isUnholyGrail(ItemStack stack) {
        Item item = unholyGrail();
        return stack != null && !stack.isEmpty() && item != Items.AIR && stack.is(item);
    }

    public static void triggerUseUnholyGrail(ServerPlayer player, boolean isTheWorthyOne) {
        if (player == null || !loaded())
            return;

        resolveUseUnholyGrailTrigger();
        if (useUnholyGrailTrigger == null || triggerUseUnholyGrail == null)
            return;

        try {
            triggerUseUnholyGrail.invoke(useUnholyGrailTrigger, player, isTheWorthyOne);
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
        }
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
        return loaded() ? PAN_LIFE_STEAL.doubleValue(0D) : 0D;
    }

    public static double eldritchPanHungerSteal() {
        return loaded() ? PAN_HUNGER_STEAL.doubleValue(0D) : 0D;
    }

    public static double eldritchPanUniqueDamageGain() {
        return loaded() ? PAN_UNIQUE_DAMAGE.doubleValue(0D) : 0D;
    }

    public static double eldritchPanUniqueArmorGain() {
        return loaded() ? PAN_UNIQUE_ARMOR.doubleValue(0D) : 0D;
    }

    public static int eldritchPanUniqueGainLimit() {
        return loaded() ? PAN_UNIQUE_LIMIT.intValue(100) : 100;
    }

    public static int bloodlustTicksPerLevel() {
        return loaded() ? Math.max(1, BLOODLUST_TICKS_PER_LEVEL.intValue(200)) : 200;
    }

    public static double bloodlustLifestealBoost() {
        return loaded() ? BLOODLUST_LIFESTEAL_BOOST.doubleValue(0D) : 0D;
    }

    public static double cursedRingSuperCursedTime() {
        return loaded() ? CURSED_RING_SUPER_CURSED_TIME.doubleValue(1D) : 1D;
    }

    public static float enderSlayerEndDamageBonusModifier() {
        return (float) (loaded() ? ENDER_DAMAGE_BONUS.modifierValue(0D) : 0D);
    }

    public static float enderSlayerEndKnockbackBonusModifier() {
        return (float) (loaded() ? ENDER_KNOCKBACK_BONUS.modifierValue(0D) : 0D);
    }

    public static int astralBreakerMiningRadius() {
        return loaded() ? ASTRAL_MINING_RADIUS.intValue(-1) : -1;
    }

    public static int astralBreakerMiningDepth() {
        return loaded() ? ASTRAL_MINING_DEPTH.intValue(1) : 1;
    }

    public static double twistKnockbackBonusModifier() {
        return loaded() ? TWIST_KNOCKBACK_BONUS.modifierValue(0D) : 0D;
    }

    public static double twistBossDamageBonusModifier() {
        return loaded() ? TWIST_BOSS_DAMAGE_BONUS.modifierValue(0D) : 0D;
    }

    public static double infinitumKnockbackBonusModifier() {
        return loaded() ? INFINITUM_KNOCKBACK_BONUS.modifierValue(0D) : 0D;
    }

    public static double infinitumBossDamageBonusModifier() {
        return loaded() ? INFINITUM_BOSS_DAMAGE_BONUS.modifierValue(0D) : 0D;
    }

    public static double infinitumUndeadProbabilityMultiplier() {
        return loaded() ? INFINITUM_UNDEAD_PROBABILITY.multiplierValue(0D) : 0D;
    }

    public static double etheriumShieldThresholdMultiplier(Player player) {
        if (!loaded())
            return 0D;

        resolveEtherium();
        Object value = callObject(getShieldThreshold, etheriumInstance, player);
        return perhapsMultiplier(value, 0D);
    }

    public static double etheriumShieldReductionModifierInverted() {
        if (!loaded())
            return 1D;

        resolveEtherium();
        Object value = callObject(getShieldReduction, etheriumInstance);
        return perhapsModifierInverted(value, 1D);
    }

    public static int etheriumAOEBoost(Player player) {
        if (player == null || !loaded())
            return 0;

        resolveEtherium();
        return (int) callNumber(getAOEBoost, etheriumInstance, player, 0D);
    }

    public static SoundEvent etheriumShieldTriggerSound() {
        if (!loaded())
            return SoundEvents.SHIELD_BLOCK;

        resolveEtherium();
        Object value = callObject(getShieldTriggerSound, etheriumInstance);
        if (value instanceof SoundEvent sound)
            return sound;

        if (!shieldTriggerSoundResolved){
            shieldTriggerSoundResolved = true;
            shieldTriggerSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(MODID, "shield_trigger"));
        }

        return shieldTriggerSound != null ? shieldTriggerSound : SoundEvents.SHIELD_BLOCK;
    }

    public static String sufferingTime(@Nullable Player player) {
        if (!loaded())
            return "0%";

        resolveSuperposition();
        if (getSufferingTime == null)
            return "0%";

        try {
            Object value = getSufferingTime.invoke(null, player);
            return value instanceof String string ? string : "0%";
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            return "0%";
        }
    }

    public static void setEldritchPanHoldingDuration(Player player, int ticks) {
        if (player == null || !loaded())
            return;

        resolvePanHoldingDurations();
        if (panHoldingDurations != null)
            panHoldingDurations.put(player, ticks);
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

        resolveAstralBreaker();
        if (astralBreaker == null || astralBreakerMineBlock == null)
            return false;

        try {
            return Boolean.TRUE.equals(astralBreakerMineBlock.invoke(astralBreaker, stack, level, state, pos, entity));
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            return false;
        }
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

    private static void resolveForbiddenFruit() {
        if (forbiddenFruitResolved)
            return;

        forbiddenFruitResolved = true;
        if (!loaded())
            return;

        try {
            Class<?> items = Class.forName(ITEMS, false, loader());
            Field field = items.getField("FORBIDDEN_FRUIT");
            Object fruit = field.get(null);
            Method method = method(fruit.getClass(), "haveConsumedFruit", boolean.class, Player.class);

            forbiddenFruit = fruit;
            haveConsumedFruit = method;
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            forbiddenFruit = null;
            haveConsumedFruit = null;
        }
    }

    private static void resolveSuperposition() {
        if (superpositionResolved)
            return;

        superpositionResolved = true;
        if (!loaded())
            return;

        try {
            Class<?> handler = Class.forName(SUPERPOSITION, false, loader());
            isTheCursedOne = method(handler, "isTheCursedOne", boolean.class, Player.class);
            isTheWorthyOne = method(handler, "isTheWorthyOne", boolean.class, Player.class);
            isTheBlessedOne = method(handler, "isTheBlessedOne", boolean.class, Player.class);
            hasArchitectsFavor = method(handler, "hasArchitectsFavor", boolean.class, Player.class);
            cannotHunger = method(handler, "cannotHunger", boolean.class, Player.class);
            getSufferingTime = method(handler, "getSufferingTime", String.class, Player.class);
            getCurseAmountPlayer = method(handler, "getCurseAmount", int.class, Player.class);
            getCurseAmountStack = method(handler, "getCurseAmount", int.class, ItemStack.class);
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            isTheCursedOne = null;
            isTheWorthyOne = null;
            isTheBlessedOne = null;
            hasArchitectsFavor = null;
            cannotHunger = null;
            getSufferingTime = null;
            getCurseAmountPlayer = null;
            getCurseAmountStack = null;
        }
    }

    private static void resolveOmniconfig() {
        if (omniconfigResolved)
            return;

        omniconfigResolved = true;
        if (!loaded())
            return;

        try {
            Class<?> clazz = Class.forName(OMNICONFIG, false, loader());
            isBossOrPlayer = method(clazz, "isBossOrPlayer", boolean.class, LivingEntity.class);
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            isBossOrPlayer = null;
        }
    }

    private static void resolveEtherium() {
        if (etheriumResolved)
            return;

        etheriumResolved = true;
        if (!loaded())
            return;

        try {
            Class<?> clazz = Class.forName(ETHERIUM_CONFIG, false, loader());
            Method instance = method(clazz, "instance", clazz);
            Object config = instance.invoke(null);
            if (config == null)
                return;

            etheriumInstance = config;
            getShieldThreshold = method(clazz, "getShieldThreshold", Object.class, Player.class);
        }
        catch (NoSuchMethodException ignored) {
            try {
                Class<?> clazz = Class.forName(ETHERIUM_CONFIG, false, loader());
                Method instance = method(clazz, "instance", clazz);
                Object config = instance.invoke(null);
                if (config == null)
                    return;

                etheriumInstance = config;
                getShieldThreshold = clazz.getMethod("getShieldThreshold", Player.class);
                getShieldThreshold.setAccessible(true);
                getShieldReduction = clazz.getMethod("getShieldReduction");
                getShieldReduction.setAccessible(true);
                getAOEBoost = clazz.getMethod("getAOEBoost", Player.class);
                getAOEBoost.setAccessible(true);
                getShieldTriggerSound = clazz.getMethod("getShieldTriggerSound");
                getShieldTriggerSound.setAccessible(true);
            }
            catch (ReflectiveOperationException | LinkageError | ClassCastException ignored2) {
                etheriumInstance = null;
                getShieldThreshold = null;
                getShieldReduction = null;
                getAOEBoost = null;
                getShieldTriggerSound = null;
            }
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            etheriumInstance = null;
            getShieldThreshold = null;
            getShieldReduction = null;
            getAOEBoost = null;
            getShieldTriggerSound = null;
        }

        if (etheriumInstance != null && getShieldReduction == null){
            try {
                Class<?> clazz = Class.forName(ETHERIUM_CONFIG, false, loader());
                getShieldReduction = clazz.getMethod("getShieldReduction");
                getShieldReduction.setAccessible(true);
                getAOEBoost = clazz.getMethod("getAOEBoost", Player.class);
                getAOEBoost.setAccessible(true);
                getShieldTriggerSound = clazz.getMethod("getShieldTriggerSound");
                getShieldTriggerSound.setAccessible(true);
            }
            catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
                getShieldReduction = null;
                getAOEBoost = null;
                getShieldTriggerSound = null;
            }
        }
    }

    private static void resolveRegisteredAttack() {
        if (registeredAttackResolved)
            return;

        registeredAttackResolved = true;
        if (!loaded())
            return;

        try {
            Class<?> clazz = Class.forName(REGISTERED_ATTACK, false, loader());
            getRegisteredAttackStrength = method(clazz, "getRegisteredAttackStregth", float.class, LivingEntity.class);
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            getRegisteredAttackStrength = null;
        }
    }

    private static void resolveUseUnholyGrailTrigger() {
        if (useUnholyGrailTriggerResolved)
            return;

        useUnholyGrailTriggerResolved = true;
        if (!loaded())
            return;

        try {
            Class<?> clazz = Class.forName(USE_UNHOLY_GRAIL_TRIGGER, false, loader());
            Field field = clazz.getField("INSTANCE");
            Object instance = field.get(null);
            Method method = clazz.getMethod("trigger", ServerPlayer.class, boolean.class);

            method.setAccessible(true);
            useUnholyGrailTrigger = instance;
            triggerUseUnholyGrail = method;
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            useUnholyGrailTrigger = null;
            triggerUseUnholyGrail = null;
        }
    }

    private static void resolveEnderSlayer() {
        if (enderSlayerResolved)
            return;

        enderSlayerResolved = true;
        if (!loaded())
            return;

        try {
            Class<?> items = Class.forName(ITEMS, false, loader());
            Field field = items.getField("ENDER_SLAYER");
            Object item = field.get(null);
            Method method = method(item.getClass(), "isEndDweller", boolean.class, LivingEntity.class);

            enderSlayer = item;
            isEndDweller = method;
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            enderSlayer = null;
            isEndDweller = null;
        }
    }

    @SuppressWarnings("unchecked")
    private static void resolvePanHoldingDurations() {
        if (panHoldingResolved)
            return;

        panHoldingResolved = true;
        if (!loaded())
            return;

        try {
            Class<?> pan = Class.forName(ELDRITCH_PAN, false, loader());
            Field field = pan.getField("HOLDING_DURATIONS");
            Object value = field.get(null);
            if (value instanceof Map<?, ?> map)
                panHoldingDurations = (Map<Player, Integer>) map;
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            panHoldingDurations = null;
        }
    }

    private static Item unholyGrail() {
        if (!unholyGrailResolved){
            unholyGrailResolved = true;
            unholyGrail = resolveItem("unholy_grail");
        }

        return unholyGrail;
    }

    private static Item cursedScroll() {
        if (!cursedScrollResolved){
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

    private static boolean callBoolean(Method method, Object receiver, Object arg) {
        if (method == null || arg == null)
            return false;

        try {
            return Boolean.TRUE.equals(method.invoke(receiver, arg));
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            return false;
        }
    }

    private static double callNumber(Method method, Object receiver, Object arg, double fallback) {
        if (method == null || arg == null)
            return fallback;

        try {
            Object value = method.invoke(receiver, arg);
            return value instanceof Number number ? number.doubleValue() : fallback;
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            return fallback;
        }
    }

    private static Object callObject(Method method, Object receiver, Object... args) {
        if (method == null)
            return null;

        try {
            return method.invoke(receiver, args);
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            return null;
        }
    }

    private static double perhapsMultiplier(Object perhaps, double fallback) {
        return callPerhapsMethod(perhaps, "asMultiplier", fallback, boolean.class, false);
    }

    private static double perhapsModifierInverted(Object perhaps, double fallback) {
        return callPerhapsMethod(perhaps, "asModifierInverted", fallback);
    }

    private static double callPerhapsMethod(Object perhaps, String name, double fallback, Class<?> argType, Object arg) {
        if (perhaps == null)
            return fallback;

        try {
            Method method = perhaps.getClass().getMethod(name, argType);
            method.setAccessible(true);
            Object value = method.invoke(perhaps, arg);
            return value instanceof Number number ? number.doubleValue() : fallback;
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            return fallback;
        }
    }

    private static double callPerhapsMethod(Object perhaps, String name, double fallback) {
        if (perhaps == null)
            return fallback;

        try {
            Method method = perhaps.getClass().getMethod(name);
            method.setAccessible(true);
            Object value = method.invoke(perhaps);
            return value instanceof Number number ? number.doubleValue() : fallback;
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            return fallback;
        }
    }

    private static Method method(Class<?> clazz, String name, Class<?> returnType, Class<?>... parameters) throws NoSuchMethodException {
        Method method = clazz.getMethod(name, parameters);
        if (returnType != Object.class && method.getReturnType() != returnType)
            throw new NoSuchMethodException(name);

        method.setAccessible(true);
        return method;
    }

    private static boolean loaded() {
        if (!loadedResolved){
            loadedResolved = true;
            loaded = ModList.get().isLoaded(MODID);
        }

        return loaded;
    }

    private static ClassLoader loader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader != null ? loader : EnigmaticLegacyCompat.class.getClassLoader();
    }

    private static double roundToPlaces(double value, int places) {
        double scale = Math.pow(10D, places);
        return Math.round(value * scale) / scale;
    }

    private static void resolveAstralBreaker() {
        if (astralBreakerResolved)
            return;

        astralBreakerResolved = true;
        if (!loaded())
            return;

        try {
            Class<?> items = Class.forName(ITEMS, false, loader());
            Field field = items.getField("ASTRAL_BREAKER");
            Object item = field.get(null);
            if (item == null)
                return;

            Method method = item.getClass().getMethod("mineBlock", ItemStack.class, Level.class, BlockState.class, BlockPos.class, LivingEntity.class);
            if (method.getReturnType() != boolean.class)
                return;

            method.setAccessible(true);
            astralBreaker = item;
            astralBreakerMineBlock = method;
        }
        catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
            astralBreaker = null;
            astralBreakerMineBlock = null;
        }
    }

    private static final class CachedParam {
        private final String className;
        private final String fieldName;
        private boolean resolved;
        private Object parameter;
        private Method getValue;

        private boolean asModifierResolved;
        private Method asModifier;

        private boolean asMultiplierResolved;
        private Method asMultiplier;

        private CachedParam(String className, String fieldName) {
            this.className = className;
            this.fieldName = fieldName;
        }

        private double doubleValue(double fallback) {
            Object value = value();
            return value instanceof Number number ? number.doubleValue() : fallback;
        }

        private int intValue(int fallback) {
            Object value = value();
            return value instanceof Number number ? number.intValue() : fallback;
        }

        private double modifierValue(double fallback) {
            Object value = value();
            if (value instanceof Number number)
                return number.doubleValue();

            return callValueMethod(value, modifierMethod(value), fallback);
        }

        private double multiplierValue(double fallback) {
            Object value = value();
            if (value instanceof Number number)
                return number.doubleValue();

            return callValueMethod(value, multiplierMethod(value), fallback);
        }

        private Object value() {
            resolve();
            if (parameter == null || getValue == null)
                return null;

            try {
                return getValue.invoke(parameter);
            }
            catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
                return null;
            }
        }

        private void resolve() {
            if (resolved)
                return;

            resolved = true;
            if (!loaded())
                return;

            try {
                Class<?> clazz = Class.forName(className, false, loader());
                Field field = clazz.getField(fieldName);
                Object param = field.get(null);
                Method method = param.getClass().getMethod("getValue");

                method.setAccessible(true);
                parameter = param;
                getValue = method;
            }
            catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
                parameter = null;
                getValue = null;
            }
        }

        private Method modifierMethod(Object value) {
            if (asModifierResolved)
                return asModifier;

            asModifierResolved = true;
            asModifier = booleanArgMethod(value, "asModifier");
            return asModifier;
        }

        private Method multiplierMethod(Object value) {
            if (asMultiplierResolved)
                return asMultiplier;

            asMultiplierResolved = true;
            asMultiplier = booleanArgMethod(value, "asMultiplier");
            return asMultiplier;
        }

        private Method booleanArgMethod(Object value, String name) {
            if (value == null)
                return null;

            try {
                Method method = value.getClass().getMethod(name, boolean.class);
                method.setAccessible(true);
                return method;
            }
            catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
                return null;
            }
        }

        private double callValueMethod(Object value, Method method, double fallback) {
            if (value == null || method == null)
                return fallback;

            try {
                Object result = method.invoke(value, false);
                return result instanceof Number number ? number.doubleValue() : fallback;
            }
            catch (ReflectiveOperationException | LinkageError | ClassCastException ignored) {
                return fallback;
            }
        }
    }

}