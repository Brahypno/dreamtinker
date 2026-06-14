package org.brahypno.dreamtinker.library.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.esotericismtinker.EsotericismTinker;
import org.brahypno.esotericismtinker.library.compact.ars_nouveau.recipe.ModifiableEnchantmentRecipe;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;

// 按你的实际 Ids 路径修改
// import org.brahypno.dreamtinker.common.Ids;

public class ReactiveModifiableEnchantmentRecipe extends ModifiableEnchantmentRecipe {
    private static final Component NOT_VALID_TOOL =
            Component.translatable("recipe." + EsotericismTinker.MODID + ".modifiable_enchantment.not_valid_tool");

    private static final Component NOT_MODIFIABLE =
            Component.translatable("recipe." + EsotericismTinker.MODID + ".modifiable_enchantment.not_modifiable");

    private static final Component BAD_REACTIVE_LEVEL =
            Component.translatable("ars_nouveau.enchanting.bad_level");

    public ReactiveModifiableEnchantmentRecipe(
            ResourceLocation id,
            Ingredient tools,
            List<Ingredient> pedestalItems,
            int sourceCost,
            @Nullable SlotType.SlotCount slots,
            boolean allowCrystal,
            boolean checkTraitLevel
    ) {
        super(
                id,
                tools,
                pedestalItems,
                DreamtinkerModifiers.Ids.nova_reactive,
                LevelRange.exact(1),
                sourceCost,
                slots,
                allowCrystal,
                checkTraitLevel
        );
    }

    private static void copyReactiveCasterFromParchment(List<ItemStack> pedestalItems, ItemStack resultStack) {
        ItemStack parchment = getParchment(pedestalItems);
        if (parchment.isEmpty()){
            return;
        }

        ISpellCaster parchmentCaster = CasterUtil.getCaster(parchment);
        if (parchmentCaster.getSpell().isEmpty()){
            return;
        }

        ReactiveCaster reactiveCaster = new ReactiveCaster(resultStack);
        reactiveCaster.setColor(parchmentCaster.getColor());
        reactiveCaster.setSpell(parchmentCaster.getSpell());
    }

    public static @NotNull ItemStack getParchment(List<ItemStack> pedestalItems) {
        for (ItemStack stack : pedestalItems) {
            if (stack.getItem() instanceof SpellParchment){
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeType<?> getType() {
        return NovaRegistry.REACTIVE_MODIFIABLE_ENCHANTMENT_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NovaRegistry.REACTIVE_MODIFIABLE_ENCHANTMENT_SERIALIZER.get();
    }

    @Override
    public boolean isMatch(
            List<ItemStack> pedestalItems,
            ItemStack reagent,
            EnchantingApparatusTile enchantingApparatusTile,
            @Nullable Player player
    ) {
        if (!super.isMatch(pedestalItems, reagent, enchantingApparatusTile, player)){
            return false;
        }

        ItemStack parchment = getParchment(pedestalItems);
        return !parchment.isEmpty()
               && !CasterUtil.getCaster(parchment).getSpell().isEmpty();
    }

    /**
     * Reactive 固定判定：
     * <p>
     * 只读取 Ars Reactive enchantment 等级；
     * 只允许 current == 0，也就是原版 Reactive 0 -> 1 的语义。
     * <p>
     * 输出侧仍然只增加 Ids.nova_reactive modifier，不写 enchantment。
     */
    @Override
    @Nullable
    protected Component validateReagent(ItemStack stack) {
        if (stack.isEmpty()){
            return NOT_VALID_TOOL;
        }

        if (!getTools().test(stack)){
            return NOT_VALID_TOOL;
        }

        if (!(stack.getItem() instanceof IModifiable)){
            return NOT_MODIFIABLE;
        }

        ToolStack tool = ToolStack.from(stack);

        Component slotError = checkSlots(tool, getSlots());
        if (slotError != null){
            return slotError;
        }

        int currentReactiveLevel = EnchantmentHelper.getTagEnchantmentLevel(
                EnchantmentRegistry.REACTIVE_ENCHANTMENT.get(),
                stack
        );

        if (currentReactiveLevel != 0){
            return BAD_REACTIVE_LEVEL;
        }

        /*
         * 输出只 add modifier，所以仍然模拟 TConstruct modifier 写入，
         * 用来触发 requirement / conflict / max level / tryValidate。
         */
        ToolStack simulated = tool.copy();
        consumeSlots(simulated);
        simulated.addModifier(getResultModifier(), 1);

        return simulated.tryValidate();
    }

    /**
     * 输出：
     * 1. super.getResult(...) 只增加 Ids.nova_reactive modifier；
     * 2. 写入 Spell Parchment 的 spell/color 到 ReactiveCaster；
     * 3. 不写 Ars Reactive enchantment。
     */
    @Override
    public ItemStack getResult(
            List<ItemStack> pedestalItems,
            ItemStack reagent,
            EnchantingApparatusTile enchantingApparatusTile
    ) {
        ItemStack resultStack = super.getResult(pedestalItems, reagent, enchantingApparatusTile);
        copyReactiveCasterFromParchment(pedestalItems, resultStack);
        return resultStack;
    }
}