package org.dreamtinker.dreamtinker.Entity;

import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.DreamtinkerModule;
import org.dreamtinker.dreamtinker.common.Items.IronBallItem;
import org.dreamtinker.dreamtinker.common.Items.voidPearl.ThrownVoidPearl;
import org.dreamtinker.dreamtinker.library.client.entity.*;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DreamtinkerEntityTypes extends DreamtinkerModule {

    public static final RegistryObject<EntityType<ThrownVoidPearl>> VOID_PEARL =
            ENTITIES.register("void_pearl", () -> EntityType.Builder.<ThrownVoidPearl>of(ThrownVoidPearl::new, MobCategory.MISC)
                                                                    .sized(0.25F, 0.25F).clientTrackingRange(4)
                                                                    .updateInterval(10));
    public static final RegistryObject<EntityType<NarcissusFluidProjectile>> NarcissusSpitEntity =
            ENTITIES.register("narcissus_fluid_spit",
                              () -> EntityType.Builder.<NarcissusFluidProjectile>of(NarcissusFluidProjectile::new, MobCategory.MISC).sized(1F, 1F)
                                                      .clientTrackingRange(4)
                                                      .updateInterval(10));
    public static final RegistryObject<EntityType<SlashOrbitEntity>> SLASH_ORBIT =
            ENTITIES.register("slash_orbit",
                              () -> EntityType.Builder.<SlashOrbitEntity>of(SlashOrbitEntity::new, MobCategory.MISC)
                                                      .sized(0.5f, 0.5f).clientTrackingRange(64).updateInterval(2));
    public static final RegistryObject<EntityType<AggressiveFox>> AggressiveFOX =
            ENTITIES.register("aggressive_fox", () ->
                    EntityType.Builder.<AggressiveFox>of(AggressiveFox::new, MobCategory.CREATURE)
                                      .sized(0.6F, 0.7F) // 和原版狐狸一样
            );
    public static final RegistryObject<EntityType<IronBallItem.ThrownIronBall>> ThrownIronBall =
            ENTITIES.register("ironball",
                              () -> EntityType.Builder.<IronBallItem.ThrownIronBall>of(IronBallItem.ThrownIronBall::new, MobCategory.MISC).sized(0.25F, 0.25F)
                                                      .clientTrackingRange(4)
                                                      .updateInterval(10));

    public static final RegistryObject<EntityType<WiserLightBolt>> LIGHTNING_ENTITY =
            ENTITIES.register("wiser_lightning", () ->
                    EntityType.Builder.<WiserLightBolt>of(WiserLightBolt::new, MobCategory.MISC)
                                      .sized(0.0F, 0.0F)
                                      .clientTrackingRange(16)
                                      .updateInterval(Integer.MAX_VALUE
                                      ).setShouldReceiveVelocityUpdates(true).setUpdateInterval(60));
    public static final RegistryObject<EntityType<WingSlashProjectile>> WING_SLASH =
            ENTITIES.register("wing_slash", () ->
                    EntityType.Builder.<WingSlashProjectile>of(WingSlashProjectile::new, MobCategory.MISC)
                                      .sized(0.5F, 0.5F)
                                      .clientTrackingRange(4)
                                      .updateInterval(1).setShouldReceiveVelocityUpdates(true).setUpdateInterval(10));
    public static final RegistryObject<EntityType<org.dreamtinker.dreamtinker.Entity.CrescentSlashProjectile>> CRESCENT_SLASH =
            ENTITIES.register("crescent_slash", () ->
                    EntityType.Builder.<org.dreamtinker.dreamtinker.Entity.CrescentSlashProjectile>of(
                                      org.dreamtinker.dreamtinker.Entity.CrescentSlashProjectile::new, MobCategory.MISC)
                                      .sized(0.5F, 0.5F)
                                      .clientTrackingRange(4)
                                      .updateInterval(1).setShouldReceiveVelocityUpdates(true).setUpdateInterval(10));

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DreamtinkerEntityTypes.VOID_PEARL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(DreamtinkerEntityTypes.NarcissusSpitEntity.get(), NarcissusFluidProjectileRenderer::new);
        event.registerEntityRenderer(DreamtinkerEntityTypes.SLASH_ORBIT.get(), SlashOrbitRenderer::new);
        event.registerEntityRenderer(DreamtinkerEntityTypes.AggressiveFOX.get(), AggressiveFoxRender::new);
        event.registerEntityRenderer(DreamtinkerEntityTypes.LIGHTNING_ENTITY.get(), LightningBoltRenderer::new);
        event.registerEntityRenderer(DreamtinkerEntityTypes.ThrownIronBall.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(DreamtinkerEntityTypes.WING_SLASH.get(), WingSlashProjectileRenderer::new);
        event.registerEntityRenderer(DreamtinkerEntityTypes.CRESCENT_SLASH.get(), CrescentSlashProjectileRenderer::new);
    }
}
