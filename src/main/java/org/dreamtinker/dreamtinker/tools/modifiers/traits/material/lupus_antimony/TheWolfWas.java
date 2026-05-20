package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.ArrayList;
import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.*;
import static org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds.metallivorous_stibium_lupus;

public class TheWolfWas extends BattleModifier {
    public static final ResourceLocation TAG_WOLF = new ResourceLocation(Dreamtinker.MODID, "twwc");

    @Override
    public int modifierDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
        if (holder == null)
            return amount;
        ModDataNBT nbt = tool.getPersistentData();
        int count = nbt.getInt(TAG_WOLF);
        nbt.putInt(TAG_WOLF, Math.min(count + amount, Integer.MAX_VALUE));
        return amount;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new VolatileFlagModule(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY));
        hookBuilder.addModule(new RarityModule(Rarity.RARE));
        super.registerHooks(hookBuilder);
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tool instanceof ToolStack && tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            int count = nbt.getInt(TAG_WOLF);
            if (count > 0){
                tooltip.add(
                        Component.translatable("modifier.dreamtinker.tooltip.the_wolf_was").append(String.valueOf(count)).append(" / " + TheWolfWasDamage.get())
                                 .withStyle(this.getDisplayName().getStyle()));
            }
        }
    }

    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(TAG_WOLF);
        return null;
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        ToolStack toolstack = ToolStack.from(stack);
        if (holder == null)
            return;
        if (stack.isEmpty())
            return;
        ModDataNBT nbt = toolstack.getPersistentData();
        int count = nbt.getInt(TAG_WOLF);
        if (count < TheWolfWasDamage.get())
            return;
        if (1 < tool.getModifierLevel(DreamtinkerModifiers.despair_mist.getId()))
            return;
        if (!TheWolfWasEnable.get())
            return;

        // 2. 拿到原生 MaterialNBT
        MaterialNBT mats = tool.getMaterials();
        int slots = mats.size();
        if (slots == 0)
            return;
        // 3. 收集所有“原材料 Tier ≤ maxTier”的槽位索引
        List<Integer> eligibleSlots = new ArrayList<>();
        boolean keep1lupus = false;
        for (int i = 0; i < slots; i++) {
            MaterialVariant originalId = mats.get(i);
            IMaterial origMat = MaterialRegistry.getInstance().getMaterial(originalId.getId());
            if (metallivorous_stibium_lupus.matches(origMat) && !keep1lupus){
                keep1lupus = true;
                continue;
            }
            if (origMat.getTier() <= TheWolfWasMaxTier.get()){
                eligibleSlots.add(i);
            }
        }
        if (eligibleSlots.isEmpty())
            return;
        // 4. 随机从 eligibleSlots 中取一个
        RandomSource rand = holder.getRandom();
        int slot = eligibleSlots.get(rand.nextInt(eligibleSlots.size()));

        ToolMaterialHook materialsHook = tool.getDefinition().getHook(ToolHooks.TOOL_MATERIALS);
        List<MaterialStatsId> statList = materialsHook.getStatTypes(tool.getDefinition());
        MaterialStatsId statsId = statList.get(slot);

        // 4. 原槽位材料对应的 tier
        MaterialVariant original = mats.get(slot);
        IMaterial mat = MaterialRegistry.getMaterial(original.getId());
        int tier = mat.getTier();
        int possible_tier = Math.min(tier + 1, TheWolfWasMaxTier.get());

        // 6. 随机挑一个新的 MaterialVariantId
        MaterialVariantId choice = DTModifierCheck.getMaterialForTier(possible_tier, rand, statsId);
        while (choice == MaterialIds.wood && tier <= possible_tier) {
            choice = DTModifierCheck.getMaterialForTier(--possible_tier, rand, statsId);
        }
        if (MaterialRegistry.getMaterial(choice.getId()).getTier() < tier)
            return;
        nbt.putInt(TAG_WOLF, count - TheWolfWasDamage.get());

        // 7. 使用 replaceMaterial 生成新的 MaterialNBT
        MaterialNBT newMats = mats.replaceMaterial(slot, choice);
        //System.out.println(newMats);

        // 8. 直接在 ToolStack 上设置并保存
        toolstack.setMaterials(newMats);
        toolstack.updateStack(stack);
    }
}
