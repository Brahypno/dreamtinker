package org.dreamtinker.dreamtinker.library.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaCast.ModifiableSpellCaster;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.function.Supplier;

public record CasterCapability(Supplier<? extends IToolStackView> tool) implements ICasterTool {
    public static final ModifierModule CAST_HANDLER = new ModifierTraitModule(DreamtinkerModifiers.nova_cast_tool.getId(), 1, true);

    public static @NotNull ISpellCaster getSpellCaster(IToolStackView tool) {
        return new ModifiableSpellCaster(tool);
    }

    @Override
    public @NotNull ISpellCaster getSpellCaster(ItemStack stack) {
        ToolStack toolStack = ToolStack.from(stack);
        return new ModifiableSpellCaster(toolStack);
    }

    public static class Provider implements ToolCapabilityProvider.IToolCapabilityProvider {
        private final LazyOptional<ICasterTool> toolCaster;

        public Provider(Supplier<? extends IToolStackView> toolStack) {
            this.toolCaster = LazyOptional.of(() -> new CasterCapability(toolStack));
        }

        @Override
        public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> cap) {
            if (cap == NovaRegistry.Caster_CAP){
                return toolCaster.cast();
            }
            return LazyOptional.empty();
        }
    }
}
