package org.dreamtinker.dreamtinker.library.modifiers.modules.weapon;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.build.SwappableSlotModule;
import slimeknights.tconstruct.library.modifiers.util.ModuleWithKey;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class SwappableCircleWeaponAttack implements ModifierModule, MeleeHitModifierHook, DisplayNameModifierHook, ModuleWithKey {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS;
    public static final RecordLoadable<SwappableCircleWeaponAttack> LOADER;
    @Nullable
    private final ResourceLocation key;
    private final String match;
    private final Component component;
    private final float diameter;

    public SwappableCircleWeaponAttack(@Nullable ResourceLocation key, String match, float diameter) {
        this.key = key;
        this.match = match;
        this.diameter = diameter;
        this.component = Component.translatable("stat.dreamtinker.tool.display." + match);
    }

    public @NotNull RecordLoadable<? extends SwappableCircleWeaponAttack> getLoader() {
        return LOADER;
    }

    public @NotNull List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    public @NotNull Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
        return (Component) (this.match.equals(tool.getPersistentData().getString(this.getKey(entry.getModifier()))) ?
                            Component.translatable(SwappableSlotModule.FORMAT, new Object[]{name.plainCopy(), this.component}).withStyle(name.getStyle()) :
                            name);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!(this.match.equals(tool.getPersistentData().getString(this.getKey(modifier.getModifier())))))
            return;
        double range = (double) (this.diameter + (float) tool.getModifierLevel(TinkerModifiers.expanded.getId()));
        if (range > (double) 0.0F){
            double rangeSq = range * range;
            LivingEntity attacker = context.getAttacker();
            Entity target = context.getTarget();
            Level level = attacker.level();

            for (LivingEntity aoeTarget : level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(range, (double) 0.25F, range))) {
                if (aoeTarget != attacker && aoeTarget != target && !attacker.isAlliedTo(aoeTarget)){
                    if (aoeTarget instanceof ArmorStand){
                        ArmorStand stand = (ArmorStand) aoeTarget;
                        if (stand.isMarker()){
                            continue;
                        }
                    }

                    if (target.distanceToSqr(aoeTarget) < rangeSq){
                        float angle = attacker.getYRot() * ((float) Math.PI / 180F);
                        aoeTarget.knockback((double) 0.4F, (double) Mth.sin(angle), (double) (-Mth.cos(angle)));
                        ToolAttackUtil.extraEntityAttack(tool, attacker, context.getHand(), aoeTarget);
                    }
                }
            }

            level.playSound((Player) null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F,
                            1.0F);
            if (attacker instanceof Player player){
                player.sweepAttack();
            }
        }

    }

    public float diameter() {
        return this.diameter;
    }

    @Nullable
    public ResourceLocation key() {
        return this.key;
    }

    static {
        DEFAULT_HOOKS = HookProvider.defaultHooks(new ModuleHook[]{ModifierHooks.MELEE_HIT, ModifierHooks.DISPLAY_NAME});
        LOADER = RecordLoadable.create(ModuleWithKey.FIELD, StringLoadable.DEFAULT.requiredField("match", (m) -> m.match),
                                       FloatLoadable.ANY.defaultField("diameter", 0.0F, true, SwappableCircleWeaponAttack::diameter)
                , SwappableCircleWeaponAttack::new);


    }
}
