package org.dreamtinker.dreamtinker.modifier.Compact.enigmaticLegacy;

import com.aizistral.enigmaticlegacy.effects.GrowingBloodlustEffect;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.EldritchPan;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.UUID;
import java.util.function.BiConsumer;

public class eldritch_pan extends BattleModifier {
    public static final ResourceLocation BLOODLUST_ID =
            new ResourceLocation("enigmaticlegacy", "growing_bloodlust");
    public static final ResourceLocation HUNGER_ID =
            new ResourceLocation("enigmaticlegacy", "growing_hunger");

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    public static final ResourceLocation TAG_PAN = new ResourceLocation(Dreamtinker.MODID, "eldritch_pan");
    private static final ResourceLocation TAG_PAN_TICKS = new ResourceLocation(Dreamtinker.MODID, "eldritch_tick");
    private final String tool_attribute_uuid = "50c030b6-e8ef-4a99-9a6a-9c231b2365a8";

    @Override
    public void addTraits(IToolContext var1, ModifierEntry var2, TraitBuilder var3, boolean var4) {
        if (var4 && 1 == var2.getLevel() && var1.getModifierLevel(DreamtinkerModifers.cursed_ring_bound.getId()) < 20)
            var3.add(DreamtinkerModifers.cursed_ring_bound.getId(), 20);
    }

    public static MobEffect getBloodlust() {
        if (!ModList.get().isLoaded("enigmaticlegacy"))
            return null;
        return ForgeRegistries.MOB_EFFECTS.getValue(BLOODLUST_ID);
    }

    public static MobEffect getHunger() {
        if (!ModList.get().isLoaded("enigmaticlegacy"))
            return null;
        return ForgeRegistries.MOB_EFFECTS.getValue(HUNGER_ID);
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;

        if (holder instanceof Player player)
            if (isSelected){
                int currentTicks = tool.getPersistentData().getInt(TAG_PAN_TICKS);

                if (SuperpositionHandler.cannotHunger(player)){
                    int bloodlustAmplifier = currentTicks / GrowingBloodlustEffect
                            .ticksPerLevel.getValue();

                    bloodlustAmplifier = Math.min(bloodlustAmplifier, 9);

                    if (null != getBloodlust())
                        player.addEffect(new MobEffectInstance(getBloodlust(),
                                                               MobEffectInstance.INFINITE_DURATION, bloodlustAmplifier, true, true));
                }else {
                    int hungerAmplifier = currentTicks / GrowingBloodlustEffect
                            .ticksPerLevel.getValue();

                    hungerAmplifier = Math.min(hungerAmplifier, 9);

                    if (null != getBloodlust())
                        player.addEffect(new MobEffectInstance(getBloodlust(),
                                                               MobEffectInstance.INFINITE_DURATION, hungerAmplifier, true, true));
                }

                EldritchPan.HOLDING_DURATIONS.put(player, ++currentTicks);
                tool.getPersistentData().putInt(TAG_PAN_TICKS, ++currentTicks);
            }else {
                tool.getPersistentData().putInt(TAG_PAN_TICKS, 0);
                if (null != getHunger())
                    player.removeEffect(getHunger());
                if (null != getBloodlust())
                    player.removeEffect(getBloodlust());
            }

    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (context.getAttacker() instanceof Player player && !player.level().isClientSide){
            if (SuperpositionHandler.isTheWorthyOne(player)){
                float lifesteal = (float) (damageDealt * EldritchPan.lifeSteal.getValue());

                if (null != getBloodlust() && player.hasEffect(getBloodlust())){
                    int amplifier = 1 + player.getEffect(getBloodlust()).getAmplifier();
                    lifesteal += (float) ((damageDealt) * (GrowingBloodlustEffect.lifestealBoost.getValue() * amplifier));
                }

                player.heal(lifesteal);
                float hungersteal = (float) EldritchPan.hungerSteal.getValue();
                boolean noHunger = SuperpositionHandler.cannotHunger(player);

                if (context.getTarget() instanceof ServerPlayer victim){
                    FoodData victimFood = victim.getFoodData();
                    FoodData attackerFood = player.getFoodData();

                    int foodSteal = Math.min((int) Math.ceil(hungersteal), victimFood.getFoodLevel());
                    float saturationSteal = Math.min(hungersteal / 5F, victimFood.getSaturationLevel());

                    victimFood.setSaturation(victimFood.getSaturationLevel() - saturationSteal);
                    victimFood.setFoodLevel(victimFood.getFoodLevel() - foodSteal);

                    if (noHunger){
                        player.heal((float) foodSteal / 2);
                    }else {
                        attackerFood.eat(foodSteal, saturationSteal);
                    }
                }else {
                    if (noHunger){
                        player.heal(hungersteal / 2);
                    }else {
                        player.getFoodData().eat((int) Math.ceil(hungersteal), hungersteal / 5F);
                    }
                }
            }
        }
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (modifier.getLevel() > 0 && EquipmentSlot.MAINHAND == slot){
            ModDataNBT nbt = tool.getPersistentData();
            int kills = nbt.getInt(TAG_PAN);
            if (kills > 0){
                consumer.accept(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                      Attributes.ATTACK_DAMAGE.getDescriptionId(),
                                                      EldritchPan.uniqueDamageGain.getValue() * kills,
                                                      AttributeModifier.Operation.ADDITION));
                consumer.accept(Attributes.ARMOR,
                                new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                      Attributes.ATTACK_KNOCKBACK.getDescriptionId(),
                                                      EldritchPan.uniqueArmorGain.getValue() * kills,
                                                      AttributeModifier.Operation.ADDITION));
            }
        }
    }
}
