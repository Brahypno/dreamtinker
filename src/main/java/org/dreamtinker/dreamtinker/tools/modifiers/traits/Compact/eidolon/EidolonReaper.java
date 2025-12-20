package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.eidolon;

import elucent.eidolon.network.CrystallizeEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.registries.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class EidolonReaper extends NoLevelsModifier implements ProcessLootModifierHook {
    private static final ResourceLocation soul_shard =
            new ResourceLocation("eidolon", "soul_shard");
    private static MobEffect SoulShard;

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROCESS_LOOT);
    }

    public static MobEffect soul_shard() {
        if (!ModList.get().isLoaded("eidolon"))
            return null;
        if (null == SoulShard)
            SoulShard = ForgeRegistries.MOB_EFFECTS.getValue(soul_shard);
        return SoulShard;
    }

    @Override
    public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
        if (context.hasParam(LootContextParams.DAMAGE_SOURCE)){
            Entity entity = (Entity) context.getParamOrNull(LootContextParams.THIS_ENTITY);
            DamageSource dmg = context.getParam(LootContextParams.DAMAGE_SOURCE);
            if (dmg.getEntity() != null && dmg.getEntity() instanceof LivingEntity source && entity instanceof LivingEntity lv && lv.isInvertedHealAndHarm()){
                if (!(entity instanceof Player))
                    generatedLoot.removeIf(i -> !(i.getItem() instanceof ArmorItem));
                int looting = context.getLootingModifier();
                if (null != soul_shard() && source.hasEffect(soul_shard()))
                    looting += 2;
                ItemStack drop = new ItemStack(Registry.SOUL_SHARD.get(), source.level().random.nextInt(2 + looting));
                generatedLoot.add(drop);
                Networking.sendToTracking(entity.level(), entity.blockPosition(), new CrystallizeEffectPacket(entity.blockPosition()));
            }

        }
    }
}
