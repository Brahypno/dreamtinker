package org.dreamtinker.dreamtinker.modifier.Compact.enigmaticLegacy;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.ArmorModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class cursed_ring_bound extends ArmorModifier {
    private static final ResourceLocation TAG_DEEP_CURSE = new ResourceLocation(Dreamtinker.MODID, "deeper_curse");

    private boolean check(IToolStackView tool, ServerPlayer player) {
        boolean worthy_check = 20 <= tool.getModifierLevel(this.getId());
        boolean pass = player.getAbilities().instabuild ||
                       (SuperpositionHandler.isTheCursedOne(player) &&
                        (!worthy_check || SuperpositionHandler.isTheWorthyOne(player)));
        return pass;
    }

    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        if (!player.level().isClientSide && player instanceof ServerPlayer sp && this.check(tool, sp))
            return InteractionResult.PASS;
        return InteractionResult.FAIL;
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        LivingEntity entity = context.getEntity();
        if (entity.level().isClientSide)
            return;

        boolean pass = entity instanceof ServerPlayer player && this.check(tool, player);
        if (!pass){
            if (entity instanceof Player p){
                if (!p.getInventory().add(context.getReplacement().copy())){
                    p.drop(context.getReplacement().copy(), true); // 背包满则掉落
                }
            }else {
                entity.spawnAtLocation(context.getReplacement().copy());
            }
            entity.setItemSlot(context.getChangedSlot(), ItemStack.EMPTY);
        }
    }
}
