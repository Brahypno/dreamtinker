package org.dreamtinker.dreamtinker.mixin.NovaMixin;

import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkProvider;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaRegistry;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.common.TinkerTags;

import static org.dreamtinker.dreamtinker.Dreamtinker.configCompactDisabled;

@Mixin(value = PerkUtil.class, remap = false)
public class PerkUtilMixin {
    @Inject(method = "getPerkHolder", at = @At("RETURN"), cancellable = true)
    private static void dreamtinker$getPerkHolder(ItemStack stack, CallbackInfoReturnable<IPerkHolder<ItemStack>> cir) {
        if (null == cir.getReturnValue()){
            if (stack.getItem() instanceof ArmorItem armorItem && ModList.get().isLoaded("ars_nouveau") && !configCompactDisabled("ars_nouveau")){
                if (stack.is(TinkerTags.Items.ARMOR) && 0 < DTModifierCheck.getItemModifierNum(stack, NovaRegistry.nova_magic_armor.getId())){
                    IPerkProvider<ItemStack> holder = PerkRegistry.getPerkProvider(DreamtinkerTools.underPlate.get(armorItem.getType()));
                    if (holder != null){
                        cir.setReturnValue(holder.getPerkHolder(stack));
                    }
                }
            }
        }
    }
}
