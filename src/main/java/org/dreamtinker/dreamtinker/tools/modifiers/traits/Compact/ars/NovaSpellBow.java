package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.ars;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
import com.hollingsworth.arsnouveau.common.items.SpellArrow;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.CasterCapability;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArrowInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class NovaSpellBow extends NoLevelsModifier implements ArrowInterface {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArrowInterfaceInit(hookBuilder);
        hookBuilder.addModule(CasterCapability.CAST_HANDLER);
        super.registerHooks(hookBuilder);
    }

    private ItemStack findAmmo(Player playerEntity) {
        Predicate<ItemStack> predicate = s -> !s.isEmpty() && s.getItem() instanceof SpellArrow;

        for (int i = 0; i < playerEntity.getInventory().getContainerSize(); ++i) {
            ItemStack s = playerEntity.getInventory().getItem(i);
            if (predicate.test(s)){
                return s; // 或者 return ForgeHooks.getProjectile(playerEntity, shootable, s);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (!(shooter instanceof Player player))
            return;
        if (player.level().isClientSide)
            return;
        ItemStack bowStack = player.getItemInHand(shooter.getUsedItemHand());
        ItemStack specialAMMO = findAmmo(player);
        System.out.println(specialAMMO);

        // 5) 取施法器 & spell
        ISpellCaster caster = CasterCapability.getSpellCaster(tool);
        Spell spell = caster.getSpell();
        if (spell.isEmpty())
            return;
        SpellContext context = new SpellContext(
                player.level(),
                caster.modifySpellBeforeCasting(player.level(), shooter, shooter.getUsedItemHand(), caster.getSpell()),
                player,
                new PlayerCaster(player),
                bowStack
        );
        ArrayList<AbstractSpellPart> recipe = new ArrayList<>();
        recipe.add(MethodProjectile.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
        if (specialAMMO.getItem() instanceof SpellArrow sa){
            sa.modifySpell(spell);
            specialAMMO.shrink(1);
        }
        context.withSpell(spell);

        // 6) 复刻 releaseUsing 的 resolver / SpellContext（含 modifySpellBeforeCasting）
        SpellResolver checkResolver = new SpellResolver(context).withSilent(true);

        // 无箭纯法术分支：只要能 cast 就允许额外射
        if (!checkResolver.canCast(player))
            return;

        // 8) 扣蓝一次（对齐 releaseUsing：转法术箭时 expendMana 一次，split 不重复扣）
        checkResolver.expendMana();

        // 9) 构建法术箭列表：主箭 + split
        List<AbstractArrow> arrows = new ArrayList<>();

        // 主法术箭：尽量复刻 buildSpellArrow(...) 的内容
        EntitySpellArrow main = buildPureSpellArrow(player.level(), player, caster, checkResolver);
        arrows.add(main);

        int numSplits = main.spellResolver.spell.getBuffsAtIndex(0, player, AugmentSplit.INSTANCE);

        for (int i = 0; i < numSplits; i++) {
            EntitySpellArrow sps = buildPureSpellArrow(player.level(), player, caster, checkResolver);
            arrows.add(sps);
        }

        // 10) 发射：交错角度 + shootFromRotation + crit + addArrow
        int opposite = -1;
        int counter = 0;
        double speed = projectile.getDeltaMovement().length();
        float f = (float) (speed / 3.0);

        for (AbstractArrow arr : arrows) {
            float yaw = player.getYRot() + Math.round(counter / 2.0f) * 10.0f * opposite;
            arr.shootFromRotation(player, player.getXRot(), yaw, 0.0F, f * 3.0F, 1.0F);

            opposite *= -1;
            counter++;

            if (null != arrow && arrow.isCritArrow()){
                arr.setCritArrow(true);
            }

            player.level().addFreshEntity(arr);
        }
    }

    /**
     * “无箭纯法术”版 buildSpellArrow：baseDamage=0，spellResolver.withSilent(true)，颜色
     */
    private static EntitySpellArrow buildPureSpellArrow(Level level, Player player, ISpellCaster caster, SpellResolver resolver) {
        EntitySpellArrow spellArrow = new EntitySpellArrow(level, player);

        spellArrow.spellResolver = resolver;
        spellArrow.pierceLeft = resolver.spell.getBuffsAtIndex(0, player, AugmentPierce.INSTANCE);

        spellArrow.setColors(caster.getColor());
        spellArrow.setBaseDamage(0); // 无箭纯法术分支：对齐你说的假定
        return spellArrow;
    }

}
