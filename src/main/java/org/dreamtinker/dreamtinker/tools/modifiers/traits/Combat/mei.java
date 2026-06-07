package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.library.client.utils.MaskService;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DamageProbe;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.RomanNumeralHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.RedShadeEnable;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.RedTime;

public class mei extends Modifier implements ModifierRemovalHook, ValidateModifierHook, ToolStatsModifierHook, AttributesModifierHook, InventoryTickModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook, MeleeDamageModifierHook {
    private final String mei_key_2 = "modifier.dreamtinker.mei_2";
    private final String mei_key_3 = "modifier.dreamtinker.mei_3";
    private final int max_level_second = RedTime.get();
    private static final ResourceLocation TAG_MLT = Dreamtinker.getLocation("mei_level_time");
    private static final ResourceLocation TAG_MLL = Dreamtinker.getLocation("mei_level");
    private final String tool_attribute_uuid = "1cbcb2dd-4df1-4fcf-a072-301ab051378d";
    private final String player_attribute_uuid = "548eef8b-d4db-44cc-9854-0063e4d2affb";

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.REMOVE, ModifierHooks.VALIDATE, ModifierHooks.TOOL_STATS, ModifierHooks.ATTRIBUTES,
                            ModifierHooks.INVENTORY_TICK,
                            ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE);
        super.registerHooks(hookBuilder);
    }

    @Override
    public @NotNull Component getDisplayName(int level) {
        if (level <= 9)
            return ModifierLevelDisplay.DEFAULT.nameForLevel(this, level);
        else if (level < 100)
            return ModifierLevelDisplay.DEFAULT.nameForLevel(this, 9);
        else if (level <= 200)
            return Component.translatable(this.getTranslationKey()).append(" ").append(RomanNumeralHelper.getNumeral(9))
                            .withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(mei_key_2)));
        else if (level <= 300)//mei_2
            return Component.translatable(this.getTranslationKey()).withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(mei_key_2)));
        else//mei.3
            return Component.translatable(mei_key_3).withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(mei_key_3)));
    }

    public @NotNull List<Component> getDescriptionList(int level) {
        if (level < 200)
            return this.getDescriptionList();
        else if (level <= 300)//mei.2
            return Arrays.asList(Component.translatable(mei_key_2 + ".flavor").withStyle(ChatFormatting.ITALIC),
                                 Component.translatable(mei_key_2 + ".description").withStyle(ChatFormatting.GRAY));
        else//mei.3
            return Arrays.asList(Component.translatable(mei_key_3 + ".flavor").withStyle(ChatFormatting.ITALIC),
                                 Component.translatable(mei_key_3 + ".description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        if (modifier.getId().equals(this.getId()))
            return refuseRemoveMessage(tool);
        return null;
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        if (modifier.getId().equals(this.getId()))
            if (modifier.getLevel() < tool.getPersistentData().getInt(TAG_MLL))
                return refuseRemoveMessage(tool);
        return null;
    }

    private Component refuseRemoveMessage(IToolStackView tool) {
        int level = tool.getModifierLevel(this);
        if (level < 200)
            return Component.translatable(this.getTranslationKey() + ".salvage");
        else if (level < 300)//mei_2
            return Component.translatable(mei_key_2 + ".salvage").withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(mei_key_2)));
        else//mei_3
            return Component.translatable(mei_key_3 + ".salvage").withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(mei_key_3)));
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        int level = context.getModifierLevel(this);
        float amt = 0;
        if (level < 50){
            amt += level * 1.3f;
        }else if (level < 100){
            amt += 65 + (level - 50) * 0.9f;
        }else if (level < 200){
            amt += 110 + (level - 100) * 0.3f;
        }else {
            amt += 140 + (level - 200) * 0.1f;
        }
        ToolStats.ATTACK_DAMAGE.add(builder, amt);
        ToolStats.ATTACK_SPEED.add(builder, amt * .1f);
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (tool.isBroken() || slot != EquipmentSlot.MAINHAND)
            return;
        int level = modifier.getLevel();
        double mod;
        if (400 < level){
            mod = Math.pow(level - 400, 1.5) / 100 + 4.26;
        }else if (200 <= level){
            mod = 2.0 * (level - 200) / 100 + .26;
        }else {
            mod = 0.13 * level / 100;
        }
        consumer.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString(tool_attribute_uuid), this.getTranslationKey(), mod,
                                                                        AttributeModifier.Operation.MULTIPLY_BASE));
        consumer.accept(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString(tool_attribute_uuid), this.getTranslationKey(), mod,
                                                                       AttributeModifier.Operation.MULTIPLY_BASE));
        if (200 <= level){
            consumer.accept(Attributes.ARMOR, new AttributeModifier(UUID.fromString(tool_attribute_uuid), this.getTranslationKey(), mod,
                                                                    AttributeModifier.Operation.MULTIPLY_BASE));
            consumer.accept(Attributes.ARMOR_TOUGHNESS,
                            new AttributeModifier(UUID.fromString(tool_attribute_uuid), this.getTranslationKey(), mod,
                                                  AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (300 <= level && tool.getModifierLevel(DreamtinkerModifiers.despair_mist.getId()) < 1)
            consumer.accept(Attributes.MOVEMENT_SPEED, new AttributeModifier(UUID.fromString(tool_attribute_uuid), this.getTranslationKey(),
                                                                             2.0 * (level - 100) / 100 + .1, AttributeModifier.Operation.MULTIPLY_BASE));
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if (world.getGameTime() % 20 == 0){
            int level = tool.getModifierLevel(this);
            ModDataNBT toolData = tool.getPersistentData();
            int last_second = toolData.getInt(TAG_MLT);
            double cap_speed = Math.min(10, Math.max(1.0, level / 100.0));
            int cur_exp_cap = (int) Math.ceil(Math.max(1, max_level_second / cap_speed));//MAYBE SHOULDN'T this fast or this is super slow......i dont know
            if (cur_exp_cap <= last_second + 1){
                ToolStack ts = ToolStack.from(stack);
                ts.addModifier(this.getId(), 1);
                ts.getPersistentData().putInt(TAG_MLT, last_second + 1 - cur_exp_cap);
                ts.getPersistentData().putInt(TAG_MLL, level + 1);
                ts.updateStack(stack);
            }else {
                toolData.putInt(TAG_MLT, last_second + 1);
                if (tool.getPersistentData().getInt(TAG_MLL) < level)
                    tool.getPersistentData().putInt(TAG_MLL, level);
            }
            if (holder instanceof ServerPlayer player && tool.getModifierLevel(DreamtinkerModifiers.despair_mist.getId()) < 1){
                double mod;
                ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(Attributes.ATTACK_DAMAGE, Attributes.ATTACK_SPEED));
                if (200 <= level){
                    mod = -1.5 * (level - 100) / 100 - .1;
                }else {
                    mod = -0.1 * level / 100;
                }
                if (200 <= level)
                    attributes.addAll(Arrays.asList(Attributes.ARMOR, Attributes.ARMOR_TOUGHNESS));
                if (300 <= level)
                    attributes.add(Attributes.MOVEMENT_SPEED);
                for (Attribute attr : attributes) {
                    AttributeInstance attr_instance = player.getAttribute(attr);
                    if (null != attr_instance){
                        AttributeModifier cur = attr_instance.getModifier(UUID.fromString(player_attribute_uuid));
                        if ((cur == null) || mod < cur.getAmount()){
                            attr_instance.removeModifier(UUID.fromString(player_attribute_uuid));
                            attr_instance.addPermanentModifier(new AttributeModifier(UUID.fromString(player_attribute_uuid), this.getTranslationKey(), mod,
                                                                                     AttributeModifier.Operation.MULTIPLY_BASE));
                        }
                    }
                }
                if (100 <= level && tool.getModifierLevel(DreamtinkerModifiers.despair_mist.getId()) < 1)
                    MaskService.colorIsolation(player, Dreamtinker.getLocation("modifier/mei"), RedShadeEnable.get() ? 0xFF8A221C : 0x6E3D3A3A, 60, 0.65F,
                                               1.28F, 10, -1);
            }
        }
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity attacker = context.getAttacker();
        if (attacker.level().isClientSide)
            return knockback;
        int level = tool.getModifierLevel(this.getId());
        if (400 <= level){
            DamageSource dam =
                    new DamageSource(
                            attacker.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DreamtinkerDamageTypes.NULL_VOID),
                            attacker, attacker);
            DamageProbe.damageHandler(context.getTarget(), dam, damage * level * 2);
        }
        return knockback;
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage * (500 <= tool.getModifierLevel(this.getId()) ? 10 : 1);
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        beforeMeleeHit(tool, modifier, context, damage, 0, 0);
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }
}
