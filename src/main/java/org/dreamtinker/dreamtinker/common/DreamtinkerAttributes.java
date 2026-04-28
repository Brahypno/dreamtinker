package org.dreamtinker.dreamtinker.common;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
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

    public static float getEvadeChance(LivingEntity target) {
        AttributeInstance attr = target.getAttribute(DreamtinkerAttributes.FATE_VEIL.get());
        return attr == null ? 0.0f : Mth.clamp((float) attr.getValue(), 0.0f, 0.98f);
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

    @SubscribeEvent(priority = EventPriority.LOW)
    static void livingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        // client side always returns false, so this should be fine?
        if (entity.level().isClientSide() || entity.isDeadOrDying()){
            return;
        }
        // I cannot think of a reason to run when invulnerable
        DamageSource source = event.getSource();
        if (entity.isInvulnerableTo(source)){
            return;
        }
        float evadeChance = getEvadeChance(entity);
        if (evadeChance <= 0)
            return;

        if (entity.level().getRandom().nextFloat() < evadeChance){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHeal(LivingHealEvent event) {
        if (event.isCanceled()){
            return;
        }

        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide){
            return;
        }

        AttributeInstance attr = entity.getAttribute(DreamtinkerAttributes.BLOOD_IN_SHELL.get());
        if (attr == null){
            return;
        }

        float cap = (float) attr.getValue();
        if (cap <= 0.0F){
            return;
        }

        float healAmount = event.getAmount();
        if (healAmount <= 0.0F){
            return;
        }
        float currentAbsorption = entity.getAbsorptionAmount();
        if (currentAbsorption >= cap){
            return;
        }

        float added = Math.min(healAmount, cap - currentAbsorption);
        entity.setAbsorptionAmount(currentAbsorption + added);
    }

    @SubscribeEvent
    void addAttributes(EntityAttributeModificationEvent event) {
        // general attributes
        addToAll(event, FATE_VEIL);
        addToAll(event, BLOOD_IN_SHELL);
    }
}
