package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import com.sammy.malum.registry.common.item.ItemRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class malum_base extends BattleModifier {
    public InteractionResult onToolUse(IToolStackView var1, ModifierEntry var2, Player var3, InteractionHand var4, InteractionSource var5) {
        return ItemRegistry.CRUDE_SCYTHE.get().use(var3.level(), var3, var4).getResult();
    }

}
