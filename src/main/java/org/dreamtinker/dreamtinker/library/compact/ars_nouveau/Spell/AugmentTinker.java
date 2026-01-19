package org.dreamtinker.dreamtinker.library.compact.ars_nouveau.Spell;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.TierSortingRegistry;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaBook.ModifiableSpellBook;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;
import java.util.Set;

import static org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaRegistry.AugmentTinkerID;
import static org.dreamtinker.dreamtinker.utils.CompactUtils.arsNovaUtils.isMelee;

public class AugmentTinker extends AbstractAugment {
    public static AugmentTinker INSTANCE = new AugmentTinker();


    public AugmentTinker() {
        super(Dreamtinker.getLocation(AugmentTinkerID), "tinker_usage");
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, LivingEntity shooter, SpellContext spellContext) {
        ItemStack casterTool = spellContext.getCasterTool();
        if (casterTool.getItem() instanceof ModifiableSpellBook){
            ToolStack tool = ToolStack.from(casterTool);
            InteractionHand hand = shooter.getMainHandItem().equals(casterTool) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            float damage = 0;
            if (isMelee(spellContext)){
                Entity target = rayTraceResult instanceof EntityHitResult er ? er.getEntity() : shooter;
                ToolAttackContext context =
                        ToolAttackContext.attacker(shooter).target(target).hand(hand).applyStats(tool).defaultCooldown().build();
                float baseDamage = context.getBaseDamage();
                damage = baseDamage;
                List<ModifierEntry> modifiers = tool.getModifierList();

                for (ModifierEntry entry : modifiers) {
                    damage = ((MeleeDamageModifierHook) entry.getHook(ModifierHooks.MELEE_DAMAGE)).getMeleeDamage(tool, entry, context, baseDamage, damage);
                }

            }else {
                float velocity = ConditionalStatModifierHook.getModifiedStat(tool, shooter, ToolStats.VELOCITY);
                float proj_damage = tool.getStats().get(ToolStats.PROJECTILE_DAMAGE);
                builder.addAccelerationModifier(velocity * 6);
                damage = Mth.ceil(Mth.clamp((double) velocity * 3 * proj_damage, (double) 0.0F, (double) Integer.MAX_VALUE));
            }
            builder.addDamageModifier(damage);
            Tier tier = tool.getStats().get(ToolStats.HARVEST_TIER);
            int idx = Math.min(TierSortingRegistry.getSortedTiers().indexOf(tier) - 1, TierSortingRegistry.getSortedTiers().size() - 1);
            builder.addAmplification(idx);
        }

        return super.applyModifiers(builder, spellPart, rayTraceResult, world, shooter, spellContext);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

    @Override
    public @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAccelerate.INSTANCE,
                            AugmentDecelerate.INSTANCE,
                            AugmentSplit.INSTANCE,
                            AugmentAmplify.INSTANCE,
                            AugmentAOE.INSTANCE,
                            AugmentExtendTime.INSTANCE,
                            AugmentPierce.INSTANCE,
                            AugmentDampen.INSTANCE,
                            AugmentExtract.INSTANCE,
                            AugmentFortune.INSTANCE,
                            AugmentDurationDown.INSTANCE,
                            AugmentSensitive.INSTANCE,
                            AugmentRandomize.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Use the ability of Tinker traits and stats, only working in the Per Aspera Scriptum";
    }
}

