package org.dreamtinker.dreamtinker.library.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaCast.ModifiableSpellCaster;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.function.Supplier;

public record CasterCapability(Supplier<? extends IToolStackView> tool)  implements ICasterTool {
    public static final ModifierModule CAST_HANDLER = new ModifierTraitModule(DreamtinkerModifiers.Ids.nova_caster_tool, 1, true);
    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack tableStack) {
        ItemStack heldStack = player.getItemInHand(handIn);
        ISpellCaster thisCaster = CasterUtil.getCaster(tableStack);
        if (!((heldStack.getItem() instanceof SpellBook) || (heldStack.getItem() instanceof SpellParchment) || (heldStack.getItem() == ItemsRegistry.MANIPULATION_ESSENCE.asItem())))
            return false;
        boolean success;

        Spell spell = new Spell();
        if (0<DTModifierCheck.getItemModifierNum(heldStack, DreamtinkerModifiers.Ids.nova_caster_tool)) {
            ToolStack cast_tool =ToolStack.from(heldStack);
            ISpellCaster heldCaster = CasterCapability.getSpellCaster(cast_tool);
            spell = heldCaster.getSpell();
            thisCaster.setColor(heldCaster.getColor());
            thisCaster.setFlavorText(heldCaster.getFlavorText());
            thisCaster.setSpellName(heldCaster.getSpellName());
            thisCaster.setSound(heldCaster.getCurrentSound());
        } else if (heldStack.getItem() == ItemsRegistry.MANIPULATION_ESSENCE.asItem()) {
            // Thanks mojang
            String[] words = new String[]{"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale", "phnglui", "mglwnafh", "cthulhu", "rlyeh", "wgahnagl", "fhtagn", "baguette"};
            // Pick between 3 and 5 words
            int numWords = world.random.nextInt(3) + 3;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numWords; i++) {
                sb.append(words[world.random.nextInt(words.length)]).append(" ");
            }
            thisCaster.setSpellHidden(true);
            thisCaster.setHiddenRecipe(sb.toString());
            PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.spell_hidden"));
            return true;
        }
        if (isScribedSpellValid(thisCaster, player, handIn, tableStack, spell)) {
            success = setSpell(thisCaster, player, handIn, tableStack, spell);
            if (success) {
                sendSetMessage(player);
                return true;
            }
        } else {
            sendInvalidMessage(player);
        }
        return false;
    }
    public static @NotNull ISpellCaster getSpellCaster(IToolStackView tool) {
        return new ModifiableSpellCaster(tool);
    }
    public static class Provider implements ToolCapabilityProvider.IToolCapabilityProvider {
        private final LazyOptional<ICasterTool> toolCaster;
        public Provider(Supplier<? extends IToolStackView> toolStack) {
            this.toolCaster = LazyOptional.of(() -> new CasterCapability(toolStack));
        }

        @Override
        public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> cap) {
            if (cap == NovaRegistry.Caster_CAP) {
                return toolCaster.cast();
            }
            return LazyOptional.empty();
        }
    }
}
