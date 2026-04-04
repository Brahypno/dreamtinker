package org.dreamtinker.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Arrays;
import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.VirtualDodge;

public class VirtualDodge extends ArmorModifier {
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("virtual_dodge"));

    public boolean isNoLevels() {return false;}

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(this.getTranslationKey() + ".description", VirtualDodge.get() * 100)
                                      .withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        super.registerHooks(hookBuilder);
    }

    private float buffRate(int level) {
        return Math.min(0.95f, level * VirtualDodge.get().floatValue());
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        ToolStats.ARMOR.multiply(builder, 1 - buffRate(modifier.getLevel()));
        ToolStats.ARMOR_TOUGHNESS.multiply(builder, 1 - buffRate(modifier.getLevel()));
    }


    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < level){
            float buffRate = buffRate(level);
            ServerLevel world = (ServerLevel) context.getLevel();
            if (world.random.nextFloat() < buffRate){
                LivingEntity entity = context.getEntity();
                /*
                world.playSound(null, context.getEntity(), SoundEvents.ILLUSIONER_MIRROR_MOVE,
                                entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 3.0F, 1.0F);

                 */
                world.sendParticles(
                        ParticleTypes.REVERSE_PORTAL,
                        entity.getX(),
                        entity.getY() + 1.0,
                        entity.getZ(),
                        8,
                        0.25, 0.4, 0.25,
                        0.0
                );
                world.sendParticles(
                        ParticleTypes.SOUL,
                        entity.getX(),
                        entity.getY() + 1.0,
                        entity.getZ(),
                        6,
                        0.2, 0.3, 0.2,
                        0.01
                );
                return true;
            }
        }
        return false;
    }
}
