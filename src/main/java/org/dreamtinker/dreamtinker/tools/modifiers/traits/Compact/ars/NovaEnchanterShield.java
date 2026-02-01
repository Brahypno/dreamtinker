package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.ars;

import com.hollingsworth.arsnouveau.common.perk.RepairingPerk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.InteractionInterface;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class NovaEnchanterShield extends NoLevelsModifier implements InteractionInterface {
    private static MobEffect MANA_REGEN_EFFECT = null;
    private static final ResourceLocation mana_regen = new ResourceLocation("ars_nouveau", "mana_regen");
    private static MobEffect SPELL_DAMAGE_EFFECT = null;
    private static final ResourceLocation spell_damage = new ResourceLocation("ars_nouveau", "spell_damage");

    public NovaEnchanterShield() {
        MANA_REGEN_EFFECT = ForgeRegistries.MOB_EFFECTS.getValue(mana_regen);
        SPELL_DAMAGE_EFFECT = ForgeRegistries.MOB_EFFECTS.getValue(spell_damage);
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.InteractionInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    {
        MinecraftForge.EVENT_BUS.addListener(this::shieldEvent);
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (holder instanceof Player player)
            RepairingPerk.attemptRepair(stack, player);
    }

    private void shieldEvent(ShieldBlockEvent e) {
        if (!e.getEntity().level().isClientSide && e.getEntity() instanceof Player player && player.isBlocking()){
            if (0 < DTModifierCheck.getItemModifierNum(player.getUseItem(), this.getId())){
                if (null != MANA_REGEN_EFFECT)
                    player.addEffect(new MobEffectInstance(MANA_REGEN_EFFECT, 200, 1));
                if (null != SPELL_DAMAGE_EFFECT)
                    player.addEffect(new MobEffectInstance(SPELL_DAMAGE_EFFECT, 200, 1));
            }
        }
    }
}
