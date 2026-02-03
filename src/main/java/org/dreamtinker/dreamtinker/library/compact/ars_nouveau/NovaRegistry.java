package org.dreamtinker.dreamtinker.library.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.DreamtinkerModule;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaBook.ModifiableSpellBook;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.Spell.AugmentTinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.ars.*;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

import java.util.Arrays;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.lib.GlyphLib.prependGlyph;

public class NovaRegistry extends DreamtinkerModule {
    public static final String AugmentTinkerID = prependGlyph("tinker");
    public static final ItemObject<ModifiableSpellBook> per_aspera_scriptum =
            NOVA_MODI_TOOLS.register("per_aspera_scriptum", () -> new ModifiableSpellBook(UNSTACKABLE_PROPS, NovaRegistry.PerAsperaScriptum));
    public static final ToolDefinition PerAsperaScriptum = ToolDefinition.create(NovaRegistry.per_aspera_scriptum);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.RECIPE_SERIALIZERS, Dreamtinker.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.Keys.RECIPE_TYPES, Dreamtinker.MODID);
    public static final RegistryObject<RecipeType<EnchantmentRecipe>> MODIFIABLE_ENCHANTMENT_TYPE =
            RECIPE_TYPES.register("modifier_enchantment", () -> new ModRecipeType<>());
    public static final RegistryObject<RecipeSerializer<EnchantmentRecipe>> MODIFIABLE_ENCHANTMENT_SERIALIZER =
            RECIPE_SERIALIZERS.register("modifier_enchantment", () -> new EnchantmentRecipe.Serializer());
    public static final Capability<ICasterTool> Caster_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    public static ModifierDeferredRegister ARS_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);

    public NovaRegistry() {
        NovaInit();
    }

    @SuppressWarnings({"removal"})
    public void NovaInit() {
        GlyphRegistry.registerSpell(AugmentTinker.INSTANCE);
        ARS_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());

    }


    public static final StaticModifier<ScriptumAttributes> nova_scriptum_attributes =
            ARS_MODIFIERS.register("nova_scriptum_attributes", ScriptumAttributes::new);
    public static final StaticModifier<NovaMagicArmor> nova_magic_armor =
            ARS_MODIFIERS.register("nova_magic_armor", NovaMagicArmor::new);
    public static final StaticModifier<NovaConjurationEssence> nova_conjuration_essence =
            ARS_MODIFIERS.register("nova_conjuration_essence", NovaConjurationEssence::new);
    public static final StaticModifier<NovaCastTool> nova_cast_tool =
            ARS_MODIFIERS.register("nova_cast_tool", NovaCastTool::new);
    public static final StaticModifier<NovaEnchanterSword> nova_enchanter_sword =
            ARS_MODIFIERS.register("nova_enchanter_sword", NovaEnchanterSword::new);
    public static final StaticModifier<NovaWand> nova_wand =
            ARS_MODIFIERS.register("nova_wand", NovaWand::new);
    public static final StaticModifier<NovaSpellBow> nova_spell_bow =
            ARS_MODIFIERS.register("nova_spell_bow", NovaSpellBow::new);
    public static final StaticModifier<NovaEnchanterShield> nova_enchanter_shield =
            ARS_MODIFIERS.register("nova_enchanter_shield", NovaEnchanterShield::new);
    public static final StaticModifier<NovaManaShield> nova_mana_shield =
            ARS_MODIFIERS.register("nova_mana_shield", NovaManaShield::new);

    private static final List<List<List<PerkSlot>>> small_slots = Arrays.asList(
            Arrays.asList(
                    List.of(PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.TWO)
            ),
            Arrays.asList(
                    List.of(PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.TWO)
            ),
            Arrays.asList(
                    List.of(PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
            )
    );
    private static final List<List<List<PerkSlot>>> large_slots = Arrays.asList(
            Arrays.asList(
                    List.of(PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.ONE, PerkSlot.THREE)
            ),
            Arrays.asList(
                    List.of(PerkSlot.ONE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.THREE),
                    Arrays.asList(PerkSlot.ONE, PerkSlot.TWO, PerkSlot.THREE)
            ),
            Arrays.asList(
                    List.of(PerkSlot.TWO),
                    Arrays.asList(PerkSlot.TWO, PerkSlot.THREE),
                    Arrays.asList(PerkSlot.TWO, PerkSlot.TWO, PerkSlot.THREE)
            )
    );

    public static void postInit() {
        PerkRegistry.registerPerkProvider(DreamtinkerTools.underPlate.get(ArmorItem.Type.BOOTS), stack -> new ModifiableArmorPekHolder(stack, small_slots));

        PerkRegistry.registerPerkProvider(DreamtinkerTools.underPlate.get(ArmorItem.Type.CHESTPLATE),
                                          stack -> new ModifiableArmorPekHolder(stack, large_slots));

        PerkRegistry.registerPerkProvider(DreamtinkerTools.underPlate.get(ArmorItem.Type.LEGGINGS), stack -> new ModifiableArmorPekHolder(stack, large_slots));

        PerkRegistry.registerPerkProvider(DreamtinkerTools.underPlate.get(ArmorItem.Type.HELMET), stack -> new ModifiableArmorPekHolder(stack, small_slots));
    }

    private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        private ModRecipeType() {
        }

        public String toString() {
            return ForgeRegistries.RECIPE_TYPES.getKey(this).toString();
        }
    }
}
