package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class SignalAxe extends BattleModifier {
    private final ResourceLocation TAG_ATTACK_TIME = Dreamtinker.getLocation("attack_time");
    public static final ResourceLocation TAG_RIGHT_TIME = Dreamtinker.getLocation("right_attack_time");

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity attacker = context.getAttacker();
        if (attacker instanceof Player player){
            float bonus = player.getAttackStrengthScale(0) - 0.8f;
            damage += damage * bonus;
            if (bonus < 0){
                attacker.sendSystemMessage(Component.translatable("modifier.dreamtinker.signal_axe.not_ready").withStyle(this.getDisplayName().getStyle()));
            }else
                damage += damage * bonus;
        }

        return damage;
    }

    @Override
    public void onLeftClickEntity(AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {
        if (!level.isClientSide){
            ModDataNBT dataNBT = tool.getPersistentData();
            CompoundTag tag = dataNBT.contains(TAG_ATTACK_TIME) ? (CompoundTag) dataNBT.get(TAG_ATTACK_TIME) : new CompoundTag();
            Float attack_speed = tool.getStats().get(ToolStats.ATTACK_SPEED);
            float attack_time = 20f / attack_speed;
            double g = Mth.clamp(level.random.nextGaussian(), -1.0, 1.0); // ~N(0,0.35^2)
            double t01 = (g + 1.0) * 0.5;                                  // 映射到 [0,1]
            float time = attack_time + 1 + (int) Math.round(t01 * (2 * attack_time));
            System.out.println(time);
            tag.putLong(TAG_ATTACK_TIME.getPath(), level.getGameTime() + (long) time);
            dataNBT.put(TAG_ATTACK_TIME, tag);
            dataNBT.putInt(TAG_RIGHT_TIME, dataNBT.getInt(TAG_RIGHT_TIME) - 1);
        }
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide || !isSelected)
            return;
        ModDataNBT nbt = tool.getPersistentData();
        if (nbt.contains(TAG_ATTACK_TIME)){
            CompoundTag tag = (CompoundTag) nbt.get(TAG_ATTACK_TIME);
            Float attack_speed = tool.getStats().get(ToolStats.ATTACK_SPEED);
            float attack_time = 20f / attack_speed;

            if (nbt.getInt(TAG_RIGHT_TIME) < 2 &&
                attack_time * world.random.nextFloat() * (world.random.nextInt(2) + 1) < world.getGameTime() - tag.getLong(TAG_ATTACK_TIME.getPath())){
                holder.sendSystemMessage(Component.translatable("modifier.dreamtinker.signal_axe.ready").withStyle(this.getDisplayName().getStyle()));
                nbt.putInt(TAG_RIGHT_TIME, 2);
            }
        }
    }

    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(TAG_ATTACK_TIME);
        tool.getPersistentData().remove(TAG_RIGHT_TIME);
        return null;
    }

}
