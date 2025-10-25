package org.dreamtinker.dreamtinker.tools.modifiers.events.compact.curio;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class curio_hurt_handler {
    public static void LivingHurtEvent(LivingHurtEvent event) {
        DamageSource dmg = event.getSource();
        if (null != dmg.getEntity() && dmg.getEntity() instanceof LivingEntity entity){
            int sand_level = DTModifierCheck.getMainhandModifierlevel(entity, DreamtinkerModifiers.Ids.AsSand);
            if (0 < sand_level)
                damageAllCurios(event.getEntity(), (int) (event.getAmount() / 10 * sand_level), stack -> true);
        }
    }

    public static void damageAllCurios(LivingEntity target, int amount, Predicate<ItemStack> filter) {
        if (target.level().isClientSide)
            return;
        if (amount <= 0)
            return;

        Optional<ICuriosItemHandler> opt = CuriosApi.getCuriosInventory(target).resolve();
        if (opt.isEmpty())
            return;

        int applied = 0;
        ICuriosItemHandler inv = opt.get();

        for (Map.Entry<String, ICurioStacksHandler> entry : inv.getCurios().entrySet()) {
            IDynamicStackHandler stacks = entry.getValue().getStacks();
            for (int i = 0; i < stacks.getSlots(); i++) {
                ItemStack stack = stacks.getStackInSlot(i);
                if (stack.isEmpty() || !stack.isDamageableItem())
                    continue;
                if (filter != null && !filter.test(stack))
                    continue;

                // 扣耐久（尊重不损、耐久等），破碎时 Curios/MC 自己处理移除与动画
                stack.hurtAndBreak(amount, target, e -> {});
                applied++;
            }
        }
    }
}
