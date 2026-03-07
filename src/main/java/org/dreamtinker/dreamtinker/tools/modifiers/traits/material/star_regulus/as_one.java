package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.star_regulus;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.nbt.Tag.TAG_INT;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.*;
import static org.dreamtinker.dreamtinker.utils.DTModifierCheck.getPossibleToolWithModifierTag;


public class as_one extends ArmorModifier {
    private final int as_one_life = AsOneRe.get();
    private static final int SECOND_THRESHOLD = AsOneT.get();
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("as_one"));

    private static final ResourceLocation TAG_AS_ONE = Dreamtinker.getLocation("as_one");
    private static final ResourceLocation TAG_LAST = Dreamtinker.getLocation("rev_back");
    private static final Set<ResourceLocation> CONFIG_BLACKLIST = new HashSet<>();
    private static boolean Blacklist_inited = false;

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addModule(new VolatileFlagModule(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY));
        super.registerHooks(hookBuilder);
    }

    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        ModDataNBT nbt = tool.getPersistentData();
        if (!nbt.contains(TAG_AS_ONE, TAG_INT))
            nbt.putInt(TAG_AS_ONE, as_one_life);
    }

    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || event.isCanceled())
            return;
        ToolStack tool = getPossibleToolWithModifierTag(entity, this.getId(), TAG_AS_ONE);
        if (null == tool)
            return;
        ModDataNBT toolData = tool.getPersistentData();
        int count = toolData.getInt(TAG_AS_ONE);
        if (count <= 0)
            return;
        toolData.putInt(TAG_AS_ONE, --count);
        event.setCanceled(true);
        entity.deathTime = 0;
        entity.setHealth(Math.max(1F, entity.getMaxHealth() * 0.15F));
        entity.invulnerableTime = Math.max(entity.invulnerableTime, 360);
        entity.setRemainingFireTicks(0);     // 清火
        entity.fallDistance = 0.0F;          // 清坠落
        entity.setLastHurtByMob(null);       // 清仇恨，防止立刻被同一来源补刀
        entity.hurtMarked = true;            // 强制一次位置/生命同步
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tool instanceof ToolStack && tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            int count = nbt.getInt(TAG_AS_ONE);
            if (count > 0){
                tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.as_one").append(String.valueOf(count))
                                     .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if (!isCorrectSlot && !isSelected)
            return;
        long this_time;
        if ((this_time = world.getGameTime()) % 20 == 0){
            ModDataNBT toolData = tool.getPersistentData();
            int as_one_cnt = Math.min(toolData.getInt(TAG_AS_ONE) + 1, 99);
            if (as_one_cnt < 99){
                CompoundTag tag = toolData.contains(TAG_LAST) ? toolData.getCompound(TAG_LAST) : new CompoundTag();
                long last_second = tag.contains(TAG_LAST.getPath()) ? tag.getLong(TAG_LAST.getPath()) : this_time;
                if (last_second + SECOND_THRESHOLD * 20L <= this_time){
                    tag.putLong(TAG_LAST.getPath(), this_time);
                    toolData.put(TAG_LAST, tag);
                    toolData.putInt(TAG_AS_ONE, as_one_cnt);
                }
            }
            if (tool.isBroken())
                tool.setDamage(0);
        }
        List<MobEffectInstance> snapshot = holder.getActiveEffects().stream().filter(as_one::filterMobEffects).toList();
        for (MobEffectInstance inst : snapshot) {
            int amp = inst.getAmplifier();
            if (amp < AsOneA.get()){
                MobEffect type = inst.getEffect();
                int duration = inst.getDuration();
                if (AsOneTT.get() < duration)
                    holder.removeEffect(type);
            }
        }
    }

    @Override
    public int modifierDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @org.jetbrains.annotations.Nullable LivingEntity holder) {return 0;}

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < level){
            amount *= AsOneS.get().floatValue();
            if (context.getEntity().getMaxHealth() < amount)
                context.getEntity().setAbsorptionAmount((float) (Math.min(amount, AsOneAB.get()) * 2.0f));
            return amount;
        }
        return amount;
    }

    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(TAG_AS_ONE);
        tool.getPersistentData().remove(TAG_LAST);
        return null;
    }

    public static void loadConfigBlacklist(List<? extends String> ids) {
        CONFIG_BLACKLIST.clear();
        for (String s : ids) {
            try {CONFIG_BLACKLIST.add(new ResourceLocation(s));} catch (Exception ignored) {}
        }
    }

    public static boolean filterMobEffects(MobEffectInstance effect) {
        if (!Blacklist_inited){
            Blacklist_inited = true;
            loadConfigBlacklist(TheAsOneBlackList.get());
        }

        ResourceLocation key = ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect());
        return null != key &&
               !CONFIG_BLACKLIST.contains(key) && !key.getPath().contains("test") && !key.getPath().contains("ceshi");//exclude testing effect as well
    }
}

