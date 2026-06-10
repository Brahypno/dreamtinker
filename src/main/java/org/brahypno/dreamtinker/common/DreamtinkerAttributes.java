package org.brahypno.dreamtinker.common;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.brahypno.dreamtinker.Dreamtinker;
import slimeknights.mantle.registration.deferred.AttributeDeferredRegister;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Dreamtinker.MODID)
public class DreamtinkerAttributes {
    private static final AttributeDeferredRegister ATTRIBUTES = new AttributeDeferredRegister(Dreamtinker.MODID);
    public static final RegistryObject<Attribute> FATE_VEIL = ATTRIBUTES.register("generic.fate_veil", 0.0, 0, 4096f, true);
    public static final RegistryObject<Attribute> BLOOD_IN_SHELL = ATTRIBUTES.register("generic.blood_in_shell", 0.0, 0, 4096f, true);
    public static final RegistryObject<Attribute> SHELL_HEART_TOUGHNESS = ATTRIBUTES.register("generic.shell_heart_toughness", 1.0, 0.001, 4096f, true);

    @SuppressWarnings({"removal"})
    public DreamtinkerAttributes() {
        ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }


    @SubscribeEvent
    void addAttributes(EntityAttributeModificationEvent event) {
        // general attributes
        addToAll(event, FATE_VEIL);
        addToAll(event, BLOOD_IN_SHELL);
        addToAll(event, SHELL_HEART_TOUGHNESS);
    }


    /**
     * Adds an attribute to all entities
     */
    private static void addToAll(EntityAttributeModificationEvent event, RegistryObject<Attribute> attribute, double defaultValue) {
        Attribute attr = attribute.get();
        for (EntityType<? extends LivingEntity> entity : event.getTypes()) {
            event.add(entity, attr, defaultValue);
        }
    }

    /**
     * Adds an attribute to all entities
     */
    private static void addToAll(EntityAttributeModificationEvent event, RegistryObject<Attribute> attribute) {
        addToAll(event, attribute, attribute.get().getDefaultValue());
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    static void FateVeilDamageReduction(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide() || entity.isDeadOrDying())
            return;

        DamageSource source = event.getSource();
        if (entity.isInvulnerableTo(source))
            return;

        AttributeInstance attr = entity.getAttribute(DreamtinkerAttributes.FATE_VEIL.get());
        float points = attr == null ? 0.0F : Math.max(0.0F, (float) attr.getValue());

        if (points <= 0.0F)
            return;

        float health = Math.max(1.0F, entity.getHealth());
        float damage = Math.max(0.0F, event.getAmount());

        if (damage <= 0.0F)
            return;

        float baseReduction = 0.98F * points / (points + 100.0F);
        float pressure = damage / (damage + health);
        float lifeFactor = Mth.lerp(pressure, 1.10F, 0.85F);

        float reduction = Mth.clamp(baseReduction * lifeFactor, 0.0F, 0.98F);
        event.setAmount(damage * (1.0F - reduction));
    }
}
