package org.dreamtinker.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.library.modifiers.hook.ProjectileHurtHook;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.nbt.Tag.TAG_INT;

public class knockArts extends Modifier implements ModifyDamageModifierHook, MeleeDamageModifierHook, ProjectileHurtHook, TooltipModifierHook {
    public static final ResourceLocation TAG_KNOCK = Dreamtinker.getLocation("knock");

    public boolean isNoLevels() {
        return false;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_HURT, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE,
                            DreamtinkerHook.PROJECTILE_HURT, ModifierHooks.TOOLTIP);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(
            IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType,
            DamageSource source, float amount, boolean isDirectDamage) {
        tool.getPersistentData().putInt(TAG_KNOCK, tool.getPersistentData().getInt(TAG_KNOCK) + 1);
        return amount;
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage + tool.getPersistentData().getInt(TAG_KNOCK) * modifier.getLevel();
    }

    @Override
    public float modifyProjectileHurt(
            ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile,
            DamageSource source, @Nullable LivingEntity attacker, LivingEntity target, float amount) {
        return amount + persistentData.getInt(TAG_KNOCK) * modifier.getLevel();
    }

    @Override
    public void addTooltip(
            IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip,
            TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            if (nbt.contains(TAG_KNOCK, TAG_INT)){
                int count = nbt.getInt(TAG_KNOCK);
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.knock_arts").append(String.valueOf(count))
                                     .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }
}
