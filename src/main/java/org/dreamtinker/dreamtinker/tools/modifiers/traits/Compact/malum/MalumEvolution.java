package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import com.sammy.malum.core.handlers.SpiritHarvestHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import team.lodestar.lodestone.registry.common.LodestoneAttributeRegistry;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.CancerousPredatorFactor;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.CancerousPredatorRate;

public class MalumEvolution extends BattleModifier {
    private static final ResourceLocation Haunted = Dreamtinker.getLocation("haunted");
    private static final ResourceLocation haunted_change = Dreamtinker.getLocation("haunted_charge");
    private static final UUID magic_damage = UUID.fromString("a3c5d2f1-7b24-4e8a-9f0b-12c4d6e8fa90");

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        ItemStack item = context.getAttacker().getItemInHand(context.getHand());
        for (AttributeModifier mod : item.getAttributeModifiers(EquipmentSlot.MAINHAND).get(LodestoneAttributeRegistry.MAGIC_DAMAGE.get()))
            damage += (float) mod.getAmount();
        return damage;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (0 < damageDealt && null != context.getLivingTarget() &&
            0 < SpiritHarvestHandler.getSpiritData(context.getLivingTarget()).map((d) -> d.totalSpirits).orElse(0)){
            int haunted = tool.getPersistentData().getInt(Haunted);
            float charge = tool.getPersistentData().getFloat(haunted_change) + damageDealt;
            while (charge >= req(haunted + 1)) {
                charge -= req(haunted + 1);
                haunted++;
            }
            tool.getPersistentData().putInt(Haunted, haunted);
            tool.getPersistentData().putFloat(haunted_change, charge);
        }
    }


    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown()){
            ModDataNBT nbt = tool.getPersistentData();
            float count = nbt.getFloat(haunted_change);
            tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.evolution").append(Math.round(count) + "/" + req(nbt.getInt(Haunted) + 1))
                                 .withStyle(this.getDisplayName().getStyle()));
        }
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!tool.isBroken() && slot == EquipmentSlot.MAINHAND && 0 < tool.getPersistentData().getInt(Haunted))
            consumer.accept(LodestoneAttributeRegistry.MAGIC_DAMAGE.get(),
                            new AttributeModifier(magic_damage,
                                                  LodestoneAttributeRegistry.MAGIC_DAMAGE.get().getDescriptionId(),
                                                  2.0 * tool.getPersistentData().getInt(Haunted),
                                                  AttributeModifier.Operation.ADDITION));
    }


    private int req(int nextA) {
        return Math.round((float) (CancerousPredatorFactor.get() * Math.pow(CancerousPredatorRate.get(), nextA - 1)));
    }

}
