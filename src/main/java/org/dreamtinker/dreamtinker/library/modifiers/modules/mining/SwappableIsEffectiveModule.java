package org.dreamtinker.dreamtinker.library.modifiers.modules.mining;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.build.SwappableSlotModule;
import slimeknights.tconstruct.library.modifiers.util.ModuleWithKey;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningTierToolHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

public class SwappableIsEffectiveModule implements ModifierModule, IsEffectiveToolHook, DisplayNameModifierHook, ModuleWithKey, ToolModule {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS;
    public static final RecordLoadable<SwappableIsEffectiveModule> LOADER;
    @Nullable
    private final ResourceLocation key;
    private final String match;
    private final Component component;
    private final IJsonPredicate<BlockState> predicate;
    private final boolean ignoreTier;

    public SwappableIsEffectiveModule(@Nullable ResourceLocation key, String match, IJsonPredicate<BlockState> predicate, boolean ignoreTier) {
        this.key = key;
        this.match = match;
        this.predicate = predicate;
        this.ignoreTier = ignoreTier;
        this.component = Component.translatable("stat.dreamtinker.tool.display." + match);
    }

    public RecordLoadable<? extends SwappableIsEffectiveModule> getLoader() {
        return LOADER;
    }

    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
        return (Component) (this.match.equals(tool.getPersistentData().getString(this.getKey(entry.getModifier()))) ?
                            Component.translatable(SwappableSlotModule.FORMAT, new Object[]{name.plainCopy(), this.component}).withStyle(name.getStyle()) :
                            name);
    }

    @Override
    public boolean isToolEffective(@NotNull IToolStackView tool, @NotNull BlockState state) {
        System.out.println(this.predicate.matches(state) + "" + MiningTierToolHook.getTier(tool));
        System.out.println(state + "" + TierSortingRegistry.isCorrectTierForDrops(MiningTierToolHook.getTier(tool), state));
        return this.predicate.matches(state) && (this.ignoreTier || TierSortingRegistry.isCorrectTierForDrops(MiningTierToolHook.getTier(tool), state));
    }

    public IJsonPredicate<BlockState> predicate() {
        return this.predicate;
    }

    public boolean ignoreTier() {
        return this.ignoreTier;
    }


    @Nullable
    public ResourceLocation key() {
        return this.key;
    }

    static {
        DEFAULT_HOOKS = HookProvider.defaultHooks(new ModuleHook[]{ToolHooks.IS_EFFECTIVE, ModifierHooks.DISPLAY_NAME});
        LOADER = RecordLoadable.create(ModuleWithKey.FIELD, StringLoadable.DEFAULT.requiredField("match", (m) -> m.match),
                                       BlockPredicate.LOADER.directField("predicate_type", SwappableIsEffectiveModule::predicate),
                                       BooleanLoadable.INSTANCE.defaultField("ignore_tier", false, false, SwappableIsEffectiveModule::ignoreTier)
                , SwappableIsEffectiveModule::new);


    }
}
