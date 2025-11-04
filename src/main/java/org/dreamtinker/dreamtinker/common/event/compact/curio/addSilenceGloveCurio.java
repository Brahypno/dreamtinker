package org.dreamtinker.dreamtinker.common.event.compact.curio;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.items.SilenceGlove;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class addSilenceGloveCurio {
    private static final ResourceLocation KEY = Dreamtinker.getLocation("curio_silence_glove");

    public static void attachCaps(AttachCapabilitiesEvent<ItemStack> e) {
        ItemStack stack = e.getObject();
        if (!(stack.getItem() instanceof SilenceGlove))
            return;
        if (!net.minecraftforge.fml.ModList.get().isLoaded("curios"))
            return;

        e.addCapability(KEY, new ICapabilityProvider() {
            private final LazyOptional<top.theillusivec4.curios.api.type.capability.ICurio> curio =
                    LazyOptional.of(() -> new CuriosCompact(stack));

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                return cap == top.theillusivec4.curios.api.CuriosCapability.ITEM ? curio.cast() : LazyOptional.empty();
            }
        });
    }

    private record CuriosCompact(ItemStack stack) implements ICurio {
        // 关键：在这里添加“增加 ring 槽位”的 Slot Modifier
        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
            Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
            ToolStack tool = ToolStack.from(stack);
            int extraRings = 0; // ← 你已有的“按内部最高攻值换算”逻辑
            if (!tool.isBroken())
                extraRings = Math.min(6, (int) (Math.ceil(tool.getStats().getInt(ToolStats.ATTACK_DAMAGE) / 2.0f) + 1));
            if (extraRings > 0){
                top.theillusivec4.curios.api.CuriosApi.addSlotModifier(
                        attributes, "ring", UUID.nameUUIDFromBytes(stack.getItem().toString().getBytes()), extraRings, AttributeModifier.Operation.ADDITION);
                ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                BiConsumer<Attribute, AttributeModifier> attributeConsumer = builder::put;
                for (ModifierEntry entry : tool.getModifierList()) {
                    entry.getHook(ModifierHooks.ATTRIBUTES).addAttributes(tool, entry, EquipmentSlot.MAINHAND, attributeConsumer);
                }
                for (Map.Entry<Attribute, AttributeModifier> e : builder.build().entries()) {
                    Attribute attr = e.getKey();
                    AttributeModifier mod = e.getValue();
                    AttributeModifier scaled = new AttributeModifier(
                            UUID.nameUUIDFromBytes((stack + mod.getId().toString()).getBytes()), mod.getName(), mod.getAmount(), mod.getOperation()
                    );
                    attributes.put(attr, scaled);
                }
            }

            return attributes;
        }

        @Override
        public ItemStack getStack() {
            return this.stack;
        }

        @Override

        public boolean canEquip(SlotContext slotContext) {
            // 允许条件：该实体当前 Curios 中没有“同种物品”（按需要选择判等方式）
            return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
                                                         .map(inv -> inv.findCurios(other -> other.is(stack.getItem())).isEmpty())
                                                         .orElse(true);
        }

    }

}
