package org.dreamtinker.dreamtinker.register;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Items.tools.TNTarrow.TNTarrow;
import org.dreamtinker.dreamtinker.entity.NarcissusFluidProjectile;
import slimeknights.mantle.registration.deferred.EntityTypeDeferredRegister;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;

public class DreamtinkerEntity {
    public static final EntityTypeDeferredRegister ENTITIES = new EntityTypeDeferredRegister(MODID);

    public static final RegistryObject<EntityType<TNTarrow.TNTArrowEntity>> TNTARROW =
            ENTITIES.register("tnt_arrow",
                              () -> EntityType.Builder.<TNTarrow.TNTArrowEntity>of(TNTarrow.TNTArrowEntity::new, MobCategory.MISC)
                                                      .sized(0.5F, 0.5F) // 确保箭矢有合适的 hitbox
                                                      .clientTrackingRange(4) // 追踪范围，避免箭矢丢失
                                                      .updateInterval(20)

            );

    public static final RegistryObject<EntityType<NarcissusFluidProjectile>> NarcissusSpitEntity =
            ENTITIES.register("narcissus_fluid_spit",
                              () -> EntityType.Builder.<NarcissusFluidProjectile>of(NarcissusFluidProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F)
                                                      .clientTrackingRange(4)
                                                      .updateInterval(10)
                                                      .setShouldReceiveVelocityUpdates(false));
}
