package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.MaskService;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
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

public class mei extends BattleModifier {
    private final String mei_key_2 = "modifier.dreamtinker.mei_2";
    private final String mei_key_3 = "modifier.dreamtinker.mei_3";
    private final int max_level_second = RedTime.get();
    private static final ResourceLocation TAG_MLT = Dreamtinker.getLocation("mei_level_time");
    private static final ResourceLocation TAG_MLL = Dreamtinker.getLocation("mei_level");
    private final String tool_attribute_uuid = "1cbcb2dd-4df1-4fcf-a072-301ab051378d";
    private final String player_attribute_uuid = "548eef8b-d4db-44cc-9854-0063e4d2affb";

    @Override
    public @NotNull Component getDisplayName(int level) {
        if (level <= 9)
            return ModifierLevelDisplay.DEFAULT.nameForLevel(this, level);
        else if (level < 50)
            return ModifierLevelDisplay.DEFAULT.nameForLevel(this, 9);
        else if (level <= 100)
            return Component.translatable(this.getTranslationKey()).append(" ").append(RomanNumeralHelper.getNumeral(9))
                            .withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(mei_key_2)));
        else if (level <= 130)//mei_2
            return Component.translatable(this.getTranslationKey()).withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(mei_key_2)));
        else//mei.3
            return Component.translatable(mei_key_3).withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(mei_key_3)));
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        if (level < 130)
            return this.getDescriptionList();
        else if (level <= 150)//mei.2
            return Arrays.asList(Component.translatable(mei_key_2 + ".flavor").withStyle(ChatFormatting.ITALIC),
                                 Component.translatable(mei_key_2 + ".description").withStyle(ChatFormatting.GRAY));
        else//mei.3
            return Arrays.asList(Component.translatable(mei_key_3 + ".flavor").withStyle(ChatFormatting.ITALIC),
                                 Component.translatable(mei_key_3 + ".description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
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
        if (level < 100)
            return Component.translatable(this.getTranslationKey() + ".salvage");
        else if (level < 130)//mei_2
            return Component.translatable(mei_key_2 + ".salvage").withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(mei_key_2)));
        else//mei_3
            return Component.translatable(mei_key_3 + ".salvage").withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(mei_key_3)));
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        int level = context.getModifierLevel(this);
        ToolStats.ATTACK_DAMAGE.add(builder, level * 1.3);
        ToolStats.ATTACK_SPEED.add(builder, level * 0.3);
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (tool.isBroken() || slot != EquipmentSlot.MAINHAND)
            return;
        int level = modifier.getLevel();
        double mod;
        if (200 <= level){
            mod = Math.pow(level - 200, 2) / 100 + 4.2;
        }else if (100 <= level){
            mod = 4.0 * (level - 100) / 100 + .2;
        }else {
            mod = 0.2 * level / 100;
        }
        consumer.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString(tool_attribute_uuid), Attributes.ATTACK_DAMAGE.getDescriptionId(), mod,
                                                                        AttributeModifier.Operation.MULTIPLY_BASE));
        consumer.accept(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString(tool_attribute_uuid), Attributes.ATTACK_SPEED.getDescriptionId(), mod,
                                                                       AttributeModifier.Operation.MULTIPLY_BASE));
        if (100 <= level){
            consumer.accept(Attributes.ARMOR, new AttributeModifier(UUID.fromString(tool_attribute_uuid), Attributes.ARMOR.getDescriptionId(), mod,
                                                                    AttributeModifier.Operation.MULTIPLY_BASE));
            consumer.accept(Attributes.ARMOR_TOUGHNESS,
                            new AttributeModifier(UUID.fromString(tool_attribute_uuid), Attributes.ARMOR_TOUGHNESS.getDescriptionId(), mod,
                                                  AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (130 <= level && tool.getModifierLevel(DreamtinkerModifiers.despair_mist.getId()) < 1)
            consumer.accept(Attributes.MOVEMENT_SPEED, new AttributeModifier(UUID.fromString(tool_attribute_uuid), Attributes.MOVEMENT_SPEED.getDescriptionId(),
                                                                             2.0 * (level - 100) / 100 + .1, AttributeModifier.Operation.MULTIPLY_BASE));
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if (world.getGameTime() % 20 == 0){
            int level = tool.getModifierLevel(this);
            ModDataNBT toolData = tool.getPersistentData();
            int last_second = toolData.getInt(TAG_MLT);
            int cur_exp_cap = (int) Math.ceil(
                    Math.max(1, max_level_second / Math.max(1.0, level / 100.0)));//MAYBE SHOULDNT this fast or this is super slow......i dont know
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
                if (100 <= level){
                    mod = -2.0 * (level - 100) / 100 - .1;
                }else {
                    mod = -0.1 * level / 100;
                }
                if (100 <= level)
                    attributes.addAll(Arrays.asList(Attributes.ARMOR, Attributes.ARMOR_TOUGHNESS));
                if (130 <= level)
                    attributes.add(Attributes.MOVEMENT_SPEED);
                for (Attribute attr : attributes) {
                    AttributeInstance attr_instance = player.getAttribute(attr);
                    if (null != attr_instance){
                        AttributeModifier cur = attr_instance.getModifier(UUID.fromString(player_attribute_uuid));
                        if ((cur == null) || mod < cur.getAmount()){
                            attr_instance.removeModifier(UUID.fromString(player_attribute_uuid));
                            attr_instance.addPermanentModifier(new AttributeModifier(UUID.fromString(player_attribute_uuid), attr.getDescriptionId(), mod,
                                                                                     AttributeModifier.Operation.MULTIPLY_BASE));
                        }
                    }
                }
                if (250 <= level || !RedShadeEnable.get())
                    MaskService.ensureOn(player, 0xDC3D3A3A, -1);
                else if (200 <= level)
                    MaskService.ensureOn(player, 0xAC8A221C, -1);
            }
        }
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity attacker = context.getAttacker();
        LivingEntity target = context.getLivingTarget();
        if (attacker.level().isClientSide || null == target)
            return knockback;
        int level = tool.getModifierLevel(this.getId());
        if (150 <= level){
            DamageSource dam =
                    new DamageSource(attacker.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC_KILL),
                                     attacker, attacker);
            target.hurt(dam, 200 <= level ? level * 2 : damage);
        }
        return knockback;
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage * 170 <= tool.getModifierLevel(this.getId()) ? 10 : 1;
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }
}
