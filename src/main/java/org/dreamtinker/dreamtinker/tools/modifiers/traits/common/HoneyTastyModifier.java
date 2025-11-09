package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.util.Lazy;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.armor.CounterModule;

import java.util.List;

public class HoneyTastyModifier extends Modifier implements GeneralInteractionModifierHook, OnAttackedModifierHook, ProcessLootModifierHook {
    private static final Lazy<ItemStack> HONEY_STACK = Lazy.of(() -> new ItemStack(Items.HONEY_BOTTLE));

    public HoneyTastyModifier() {
    }

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.GENERAL_INTERACT, ModifierHooks.ON_ATTACKED, ModifierHooks.PROCESS_LOOT);
    }

    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        if (source == InteractionSource.RIGHT_CLICK && !tool.isBroken() && player.canEat(false)){
            GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
            return InteractionResult.CONSUME;
        }else {
            return InteractionResult.PASS;
        }
    }

    private void eat(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
        int level = modifier.intEffectiveLevel();
        if (level > 0 && entity instanceof Player player){
            if (player.canEat(false)){
                Level world = entity.level();
                player.getFoodData().eat(level, 0.4F * level);
                ModifierUtil.foodConsumer.onConsume(player, HONEY_STACK.get(), level, 0.6F);
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 15 * 20, level));
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, level));
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 15 * 20, level));
                world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F,
                                1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
                world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 0.5F,
                                world.random.nextFloat() * 0.1F + 0.9F);
                if (ToolDamageUtil.directDamage(tool, 30 * level, player, player.getUseItem())){
                    player.broadcastBreakEvent(player.getUsedItemHand());
                }
            }
        }

    }

    public void onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
        if (!tool.isBroken()){
            this.eat(tool, modifier, entity);
        }

    }

    public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
        return UseAnim.EAT;
    }

    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
        return 16;
    }

    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (tool.hasTag(TinkerTags.Items.ARMOR)){
            float level = CounterModule.getLevel(tool, modifier, slotType, context.getEntity());
            if (RANDOM.nextFloat() < level * 0.15F){
                this.eat(tool, modifier, context.getEntity());
            }
        }

    }

    public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
        if (context.hasParam(LootContextParams.DAMAGE_SOURCE)){
            Entity entity = (Entity) context.getParamOrNull(LootContextParams.THIS_ENTITY);
            if (entity != null && (entity.getType().is(TinkerTags.EntityTypes.SLIMES) || entity.getType() == EntityType.BEE)){
                int looting = context.getLootingModifier();
                if (RANDOM.nextInt(24 / modifier.intEffectiveLevel()) <= looting){
                    generatedLoot.add(new ItemStack(Items.HONEY_BLOCK));
                }else if (RANDOM.nextInt(36 / modifier.intEffectiveLevel()) <= looting){
                    generatedLoot.add(new ItemStack(Items.HONEYCOMB_BLOCK));
                }else if (RANDOM.nextInt(48 / modifier.intEffectiveLevel()) <= looting){
                    generatedLoot.add(new ItemStack(Items.HONEY_BOTTLE));
                }
            }

        }
    }
}
