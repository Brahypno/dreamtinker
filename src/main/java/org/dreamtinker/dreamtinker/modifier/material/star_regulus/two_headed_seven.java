package org.dreamtinker.dreamtinker.modifier.material.star_regulus;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.UUID;

public class two_headed_seven extends BattleModifier {
    private static final UUID ARMOR_ID   = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1");
    @Override
    public void onProjectileHitBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
        if(null==attacker) return;
        if (attacker.level().isClientSide) return;
        ServerLevel level = (ServerLevel) attacker.level();

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt == null) return;
        BlockPos ep=attacker.getOnPos();
        BlockPos pos=hit.getBlockPos();
        attacker.teleportToWithTicket(pos.getX(),pos.getY()+1,pos.getZ());

        bolt.moveTo(ep.getX(), ep.getY()+1, ep.getZ());
        if(attacker instanceof ServerPlayer player) bolt.setCause(player);
        bolt.setVisualOnly(false);
        level.addFreshEntity(bolt);
    }
    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if(null==target) return false;
        AttributeModifier neg = new AttributeModifier(ARMOR_ID, "def_suppress", Integer.MIN_VALUE, AttributeModifier.Operation.ADDITION);
        AttributeInstance attr=target.getAttribute(Attributes.ARMOR);
        if(null!=attr&&attr.getModifier(ARMOR_ID) == null) attr.addTransientModifier(neg);
        attr=target.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if(null!=attr&&attr.getModifier(ARMOR_ID) == null) attr.addTransientModifier(neg);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,10,10));
        if(null==attacker) return false;
        return false;
    }
    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        float current_speed=builder.getStat(ToolStats.DRAW_SPEED);
        ToolStats.PROJECTILE_DAMAGE.add(builder, current_speed);
    }
    @Override
    public boolean isNoLevels(){return true;}
}
