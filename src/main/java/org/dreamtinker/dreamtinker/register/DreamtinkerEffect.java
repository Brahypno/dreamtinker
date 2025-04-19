package org.dreamtinker.dreamtinker.register;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.effect.SilverNameBee;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;

public class DreamtinkerEffect {
    public static final DeferredRegister<MobEffect> EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final RegistryObject<MobEffect> SilverNameBee = EFFECT.register("effectsilvernamebee", SilverNameBee::new);
}
