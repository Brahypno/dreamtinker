package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.ars;

import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.brahypno.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.concurrent.atomic.AtomicReference;

import static org.brahypno.dreamtinker.config.DreamtinkerConfig.ArtsManaShieldBase;

public class NovaManaShield extends Modifier implements ModifyDamageModifierHook {
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("nova_mana_shield"));

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_HURT);
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < level){
            LivingEntity entity = context.getEntity();
            AtomicReference<Float> absorbed = new AtomicReference<>((float) 0);
            if (entity instanceof Player player){
                CapabilityRegistry.getMana(player).ifPresent(iMana -> {
                    double mana = iMana.getCurrentMana();
                    int max_mana = iMana.getMaxMana();
                    float curve = (float) Math.pow((float) mana / max_mana, 3.0);
                    float damageReduction = (float) (ArtsManaShieldBase.get().floatValue() * Math.sqrt(level / 12.0) * curve);
                    absorbed.set(damageReduction * amount);
                    float ManaCost = (float) (absorbed.get() * (0.8 + 0.6 * Math.sqrt(level / 12.0)));
                    if (mana < ManaCost){
                        absorbed.set((float) (mana / (0.8 + 0.6 * Math.sqrt(level / 12.0))));
                        ManaCost = (float) mana;
                    }
                    iMana.setMana(mana - ManaCost);
                    //System.out.println("Mana: " + mana + "after Mana: " + iMana.getCurrentMana());
                });
                //System.out.println("Original  " + amount + "absorbed: " + absorbed.get() + "after" + (amount - absorbed.get()) + "percentage" + (absorbed.get()) / amount);
                return amount - absorbed.get();
            }
        }
        return amount;
    }
}
