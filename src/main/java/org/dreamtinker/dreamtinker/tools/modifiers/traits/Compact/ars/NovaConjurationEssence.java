package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.ars;

import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalPowerModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;

public class NovaConjurationEssence extends BattleModifier {
    private static final LivingEntityPredicate isSwiming=LivingEntityPredicate.simple(LivingEntity::isSwimming);
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(ConditionalMeleeDamageModule.builder().target(isSwiming).eachLevel(2.0f));
        hookBuilder.addModule(ConditionalPowerModule.builder().target(isSwiming).eachLevel(2.0f));
        super.registerHooks(hookBuilder);
    }
@Override
    public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
        if(owner==null)
            return false;
    Level world=owner.level();
    List<CrushRecipe> recipes = world.getRecipeManager().getAllRecipesFor(RecipeRegistry.CRUSH_TYPE.get());
    CrushRecipe lastHit = null; // Cache this for AOE hits
    for (BlockPos p : SpellUtil.calcAOEBlocks(owner, hit.getBlockPos(), hit, modifier.getEffectiveLevel(), projectile instanceof AbstractArrow arrow?arrow.getPierceLevel():0)) {
        BlockState state = world.getBlockState(p);
        Item item = state.getBlock().asItem();
        if (lastHit == null || !lastHit.matches(item.getDefaultInstance(), world)) {
            lastHit = null;
            for (CrushRecipe recipe : recipes) {
                if (recipe.matches(item.getDefaultInstance(), world)) {
                    lastHit = recipe;
                    break;
                }
            }
        }

        if (lastHit == null)
            continue;

        List<ItemStack> outputs = lastHit.getRolledOutputs(world.random);
        boolean placedBlock = false;
        for (ItemStack i : outputs) {
            if (!placedBlock && i.getItem() instanceof BlockItem blockItem && !lastHit.shouldSkipBlockPlace()) {
                world.setBlockAndUpdate(p, blockItem.getBlock().defaultBlockState());
                placedBlock = true;
                i.shrink(1);
                //Just mock EffectCrush, but I dont want SHAPERS_FOCUS, too complex
            }
            if (!i.isEmpty()) {
                world.addFreshEntity(new ItemEntity(world, p.getX() + 0.5, p.getY(), p.getZ() + 0.5, i));
            }
        }
        if (!placedBlock) {
            world.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
            //same reason as above.
        }
    }

    return false;
    }
}
