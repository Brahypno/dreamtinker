package org.dreamtinker.dreamtinker.common;

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
import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.mantle.registration.deferred.AttributeDeferredRegister;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Dreamtinker.MODID)
public class DreamtinkerAttributes {
    private static final AttributeDeferredRegister ATTRIBUTES = new AttributeDeferredRegister(Dreamtinker.MODID);
    public static final RegistryObject<Attribute> FATE_VEIL = ATTRIBUTES.register("generic.fate_veil", 0.0, 0, 0.98f, true);
    public static final RegistryObject<Attribute> BLOOD_IN_SHELL = ATTRIBUTES.register("generic.blood_in_shell", 0.0, 0, 4096f, true);

    @SuppressWarnings({"removal"})
    public DreamtinkerAttributes() {
        ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }


    @SubscribeEvent
    void addAttributes(EntityAttributeModificationEvent event) {
        // general attributes
        addToAll(event, FATE_VEIL);
        addToAll(event, BLOOD_IN_SHELL);
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
        if (entity.level().isClientSide() || entity.isDeadOrDying()){
            return;
        }
        DamageSource source = event.getSource();
        if (entity.isInvulnerableTo(source)){
            return;
        }
        AttributeInstance attr = entity.getAttribute(DreamtinkerAttributes.FATE_VEIL.get());
        float veil = attr == null ? 0.0f : Mth.clamp((float) attr.getValue(), 0.0f, 0.98f);
        if (veil <= 0)
            return;
        float damage = event.getAmount();
        float pool = entity.getHealth();
        if (damage > 0.0F && pool > 0.0F){
            float reduction = (veil * pool + veil * veil * damage) / (pool + damage);
            event.setAmount(damage * (1.0F - reduction));
        }
    }
}
