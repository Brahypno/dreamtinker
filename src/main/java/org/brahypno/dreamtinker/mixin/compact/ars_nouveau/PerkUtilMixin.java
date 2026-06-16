package org.brahypno.dreamtinker.mixin.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkProvider;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.brahypno.dreamtinker.library.compact.ars_nouveau.NovaRegistry;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.common.TinkerTags;

import static org.brahypno.esotericismtinker.EsotericismTinker.configCompactDisabled;

@Mixin(value = PerkUtil.class, remap = false)
public class PerkUtilMixin {
    @Inject(method = "getPerkHolder", at = @At("RETURN"), cancellable = true)
    private static void dreamtinker$getPerkHolder(ItemStack stack, CallbackInfoReturnable<IPerkHolder<ItemStack>> cir) {
        if (!configCompactDisabled("ars_nouveau") && null == cir.getReturnValue()){
            if (stack.getItem() instanceof ArmorItem armorItem && ModList.get().isLoaded("ars_nouveau") && !configCompactDisabled("ars_nouveau")){
                if (stack.is(TinkerTags.Items.ARMOR) && 0 < ETModifierCheck.getItemModifierNum(stack, NovaRegistry.nova_magic_armor.getId())){
                    IPerkProvider<ItemStack> holder = PerkRegistry.getPerkProvider(DreamtinkerTools.underPlate.get(armorItem.getType()));
                    if (holder != null){
                        cir.setReturnValue(holder.getPerkHolder(stack));
                    }
                }
            }
        }
    }
}
