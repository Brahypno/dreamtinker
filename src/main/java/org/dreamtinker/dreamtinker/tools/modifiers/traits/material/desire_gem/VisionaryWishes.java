package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.desire_gem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.library.client.particle.ColoredSweepBurst;
import org.dreamtinker.dreamtinker.library.client.utils.MaskService;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.tools.modifiers.events.VisionaryDrops;
import org.dreamtinker.dreamtinker.utils.DTDamageUtils;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.data.ModifierIds;

import java.util.List;

import static slimeknights.tconstruct.library.tools.helper.ArmorUtil.getDamageBeforeArmorAbsorb;

public class VisionaryWishes extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook, TooltipModifierHook, InventoryTickModifierHook, ModifierTraitHook {
    private static final int BASE_HIT_GAIN = 6;
    private static final int BOOSTED_HIT_GAIN = 10;

    private static final int BASE_KILL_GAIN = 12;
    private static final int BOOSTED_KILL_GAIN = 20;

    public static final int COOLDOWN_DURATION = 20 * 3;

    public static void updateStack(ItemStack stack, ServerPlayer player, boolean on) {
        MaterialVariantId replace = on ? DreamtinkerMaterialIds.musou : DreamtinkerMaterialIds.desire_gem;
        MaterialVariantId target = on ? DreamtinkerMaterialIds.desire_gem : DreamtinkerMaterialIds.musou;
        ToolStack tool = ToolStack.from(stack);
        MaterialNBT mats = tool.getMaterials();
        for (int i = 0; i < mats.size(); i++) {
            if (mats.get(i).sameVariant(target))
                mats = mats.replaceMaterial(i, replace);
        }
        tool.setMaterials(mats);
        tool.updateStack(stack);
        if (on)
            MaskService.atmosphere(player, Dreamtinker.getLocation("modifier/musou"), 0xDD241236, 0.10F, 0.55F, 40, 8);
        else
            MaskService.remove(player, Dreamtinker.getLocation("modifier/musou"));
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!(context.getAttacker() instanceof ServerPlayer player)){
            return;
        }

        LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
        if (target == null){
            return;
        }


        boolean boosted = WishPowerData.boosted(tool, player.level());
        int gain = boosted ? BOOSTED_HIT_GAIN : BASE_HIT_GAIN;

        if (!target.isAlive() || target.isDeadOrDying()){
            gain += boosted ? BOOSTED_KILL_GAIN : BASE_KILL_GAIN;
        }

        WishPowerData.add(tool, gain * modifier.getLevel());
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MELEE_HIT, ModifierHooks.TOOLTIP,
                            ModifierHooks.INVENTORY_TICK,
                            ModifierHooks.MODIFIER_TRAITS);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull ToolAttackContext context, float baseDamage, float damage) {
        if (WishPowerData.boosted(tool, context.getLevel())){
            damage *= 1.935F + 0.5f * (modifier.getLevel() - 1);
        }
        LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
        float armor = 0;
        if (target != null){
            armor = target.getArmorValue();
        }
        if (armor <= 0)
            return damage;
        float toughness = (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        float b = CombatRules.getDamageAfterAbsorb(damage, armor, toughness);
        float desired = Mth.lerp(0.2f * modifier.getLevel(), b, damage);
        damage = getDamageBeforeArmorAbsorb(desired, armor, toughness);

        return damage;
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
        if (context.getLevel() instanceof ServerLevel sl && null != target && target.isAlive() && WishPowerData.boosted(tool, context.getLevel())){
            target.getPersistentData().putBoolean(VisionaryDrops.Visionary, true);
            ColoredSweepBurst.create()
                             .color(0x9A55F0, 0xC0)
                             .shape(0.78F, 1.35F, 0.75F)
                             .angle(0.25F)
                             .offset(0.95F, -0.05F, 0.32F)
                             .spawnFrom(context.getAttacker());
        }
        return knockback;
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        afterMeleeHit(tool, modifier, context, damageAttempted);
        if (WishPowerData.boosted(tool, context.getLevel())){
            DamageSource dmg =
                    DreamtinkerDamageTypes.source(context.getLevel().registryAccess(), DreamtinkerDamageTypes.many_wishes, context.makeDamageSource());
            DTDamageUtils.damageHandler(context.getTarget(), dmg, damageAttempted);
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        int wish = WishPowerData.get(tool);

        tooltip.add(Component.translatable(modifier.getModifier().getTranslationKey() + ".tooltip", wish, WishPowerData.MAX_WISH)
                             .withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && world.getGameTime() % 20 == 1){
            if (WishPowerData.updateState(tool, world, COOLDOWN_DURATION) && holder instanceof ServerPlayer sp){
                updateStack(stack, sp, false);
            }
        }
    }

    @Override
    public void addTraits(IToolContext context, ModifierEntry self, ModifierTraitHook.TraitBuilder builder, boolean firstEncounter) {
        MaterialNBT mats = context.getMaterials();
        for (int i = 0; i < mats.size(); i++) {
            if (mats.get(i).sameVariant(DreamtinkerMaterialIds.musou)){
                builder.add(ModifierIds.shiny, 1);
                break;
            }
        }
    }
}
