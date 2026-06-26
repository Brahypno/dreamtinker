package org.brahypno.dreamtinker.common;

import com.sammy.malum.registry.common.MobEffectRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.effect.*;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.esotericismtinker.utils.MessagesUtil;

import java.util.List;

import static org.brahypno.dreamtinker.DreamtinkerModule.*;
import static org.brahypno.esotericismtinker.utils.ETModifierCheck.getPossibleToolWithModifier;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DreamtinkerEffects {
    public DreamtinkerEffects() {}

    private static final String CURSED_END_TIME_KEY = "dreamtinker:cursed_end_time";

    public static final RegistryObject<MobEffect> SilverNameBee =
            EFFECT.register("silver_name_bee", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x7f7f7f) {});
    public static final RegistryObject<MobEffect> RealDarkness = EFFECT.register("real_darkness", realDarkness::new);
    public static final RegistryObject<MobEffect> unholy = EFFECT.register("unholy", org.brahypno.dreamtinker.common.effect.unholy::new);
    public static final RegistryObject<MobEffect> cursed = EFFECT.register("cursed", () -> new MobEffect(MobEffectCategory.HARMFUL, 0xA64DFF) {});
    public static final RegistryObject<thirsty> thirsty = MALUM_EFFECT.register("thirsty", thirsty::new);

    public static final RegistryObject<MobEffect> Ahimsa = EFFECT.register("ahimsa", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0xD8B7A1) {
        @Override
        public List<ItemStack> getCurativeItems() {return List.of();}
    });
    public static final RegistryObject<MobEffect> EdictOfStillness =
            EFFECT.register("edict_of_stillness", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0x8E7AAE) {
                @Override
                public List<ItemStack> getCurativeItems() {return List.of();}
            });
    public static final RegistryObject<MobEffect> LawOfTheSilentStep =
            EFFECT.register("law_of_the_silent_step", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0x6FAAB2) {
                @Override
                public List<ItemStack> getCurativeItems() {return List.of();}
            });
    public static final RegistryObject<MobEffect> InterdictOfAscent =
            EFFECT.register("interdict_of_ascent", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0xC6A86B) {
                @Override
                public List<ItemStack> getCurativeItems() {return List.of();}
            });
    public static final RegistryObject<MobEffect> InterdictOfGuard =
            EFFECT.register("interdict_of_guard", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0x7E8892) {
                @Override
                public List<ItemStack> getCurativeItems() {return List.of();}
            });
    public static final RegistryObject<MobEffect> InterdictOfRestoration =
            EFFECT.register("interdict_of_restoration", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0xA7C97F) {
                @Override
                public List<ItemStack> getCurativeItems() {return List.of();}
            });
    public static final RegistryObject<MobEffect> EdictOfUntouched =
            EFFECT.register("edict_of_untouched", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0xD8D4E8) {
                @Override
                public List<ItemStack> getCurativeItems() {return List.of();}
            });
    public static final RegistryObject<MobEffect> LawOfLoweredEyes =
            EFFECT.register("law_of_the_lowered_eyes", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0x5F6C9B) {
                @Override
                public List<ItemStack> getCurativeItems() {return List.of();}
            });
    public static final RegistryObject<MobEffect> SoulFire = EFFECT.register("soul_fire", soulFire::new);
    public static final RegistryObject<PressingFrontEffect> PressingFront = EFFECT.register("pressing_front", PressingFrontEffect::new);
    public static final RegistryObject<BurdenBearerEffect> BurdenBearer = EFFECT.register("burden_bearer", BurdenBearerEffect::new);
    public static final RegistryObject<DaylostEffect> Daylost = EFFECT.register("daylost", DaylostEffect::new);
    public static final RegistryObject<MobEffect> Temptation =
            EFFECT.register("temptation", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0x2B2730) {
                @Override
                public List<ItemStack> getCurativeItems() {return List.of();}
            });

    public static final RegistryObject<MobEffect> ParadoxWound =
            EFFECT.register("paradox_wound", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0xD8D4E8) {});
    public static final RegistryObject<Potion> ParadoxWoundPotion =
            POTIONS.register("paradox_wound", () -> new Potion(new MobEffectInstance(DreamtinkerEffects.ParadoxWound.get(), 20 * 45)));
    public static final RegistryObject<Potion> SoulFirePotion =
            POTIONS.register("soul_fire", () -> new Potion(new MobEffectInstance(DreamtinkerEffects.SoulFire.get(), 20 * 45)));
    public static final RegistryObject<Potion> TemptationPotion =
            POTIONS.register("temptation", () -> new Potion(new MobEffectInstance(DreamtinkerEffects.Temptation.get(), 20 * 45)));
    public static final RegistryObject<Potion> ArcanaJuicePotion =
            MALUM_POTIONS.register("arcana_juice", () -> new Potion(new MobEffectInstance(MobEffectRegistry.ECHOING_ARCANA.get(), 10 * 20, 3)));

    @SubscribeEvent
    public static void applyTemptationEdicts(MobEffectEvent.Added event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance.getEffect() != DreamtinkerEffects.Temptation.get()){
            return;
        }

        LivingEntity entity = event.getEntity();
        entity.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT)
              .getTag(DreamtinkerTagKeys.MobEffects.EDICTS)
              .ifPresent(edicts -> {
                  if (entity instanceof Player){
                      MessagesUtil.clientChat(Component.translatable("tooltip.dreamtinker.temptation_edicts"), true);
                  }
                  for (Holder<MobEffect> effect : edicts) {
                      entity.addEffect(new MobEffectInstance(
                              effect.value(),
                              instance.getDuration(),
                              instance.getAmplifier(),
                              instance.isAmbient(),
                              instance.isVisible(),
                              instance.showIcon()
                      ));
                  }
              });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void allowBossEffects(MobEffectEvent.Applicable event) {
        MobEffectInstance instance = event.getEffectInstance();

        if (instance.getEffect() == DreamtinkerEffects.SoulFire.get() ||
            (instance.getEffect() == DreamtinkerEffects.cursed.get() && !hasAsOneCurseImmunity(event.getEntity()))){
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void recordAddedCursed(MobEffectEvent.Added event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance.getEffect() != DreamtinkerEffects.cursed.get() || hasAsOneCurseImmunity(event.getEntity()))
            return;
        recordCursedTime(event.getEntity(), instance);
    }

    private static boolean hasAsOneCurseImmunity(LivingEntity entity) {
        if (entity.level().isClientSide)
            return false;
        return getPossibleToolWithModifier(entity, DreamtinkerModifiers.as_one.getId()) != null;
    }

    private static long getEffectEndTime(LivingEntity entity, MobEffectInstance instance) {
        int duration = instance.getDuration();
        if (duration < 0)
            return Long.MAX_VALUE;
        return entity.level().getGameTime() + duration;
    }

    private static void recordCursedTime(LivingEntity entity, MobEffectInstance instance) {
        if (entity.level().isClientSide)
            return;

        long endTime = getEffectEndTime(entity, instance);
        CompoundTag data = entity.getPersistentData();
        data.putLong(CURSED_END_TIME_KEY, Math.max(data.getLong(CURSED_END_TIME_KEY), endTime));
    }

    public static boolean hasActiveCursedTime(LivingEntity entity) {
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(CURSED_END_TIME_KEY))
            return false;

        long endTime = data.getLong(CURSED_END_TIME_KEY);
        if (endTime > entity.level().getGameTime())
            return true;

        data.remove(CURSED_END_TIME_KEY);
        return false;
    }

    @SubscribeEvent
    void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            //brewing(experiencedPotion, Potions.AWKWARD, Ingredient.of(TinkerWorld.congealedSlime.get(SlimeType.EARTH)));
        });
    }

    /**
     * Registers recipes for brewing, longer and stronger potions for the given object

     private static void brewing(EnumObject<PotionType, Potion> potion, Potion base, Ingredient ingredient) {
     Potion normal = potion.get(PotionType.NORMAL);
     PotionBrewing.POTION_MIXES.add(new PotionBrewing.Mix<>(ForgeRegistries.POTIONS, base, ingredient, normal));
     Potion longer = potion.getOrNull(PotionType.LONG);
     if (longer != null){
     PotionBrewing.addMix(normal, Items.REDSTONE, longer);
     }
     Potion strong = potion.getOrNull(PotionType.STRONG);
     if (strong != null){
     PotionBrewing.addMix(normal, Items.GLOWSTONE_DUST, strong);
     }
     }     */
}
