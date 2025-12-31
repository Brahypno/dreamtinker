package org.dreamtinker.dreamtinker.library.compact.ars_nouveau;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class ModifiableEnchantmentRecipe extends EnchantingApparatusRecipe {
    public ModifierId Modifierid;
    public int modifier_level;
    private final SlotType.SlotCount slots;
    protected static final String KEY_NOT_ENOUGH_SLOTS = TConstruct.makeTranslationKey("recipe", "modifier.not_enough_slots");
    protected static final String KEY_NOT_ENOUGH_SLOT = TConstruct.makeTranslationKey("recipe", "modifier.not_enough_slot");

    public ModifiableEnchantmentRecipe(List<Ingredient> pedestalItems, ModifierId id, int level, int manaCost, @Nullable SlotType.SlotCount slots) {
        this.pedestalItems = pedestalItems;
        this.Modifierid = id;
        this.modifier_level = level;
        this.sourceCost = manaCost;
        this.slots = slots;
        String var10004 = id.getPath();
        this.id = new ResourceLocation(Dreamtinker.MODID, var10004 + "_" + level);
    }

    public RecipeType<?> getType() {
        return (RecipeType) NovaRegistry.MODIFIABLE_ENCHANTMENT_TYPE.get();
    }

    public boolean doesReagentMatch(ItemStack stack, Player playerEntity) {
        if (stack.isEmpty()){
            return false;
        }else {
            if (!(stack.getItem() instanceof IModifiable)){
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.enchanting.bad_level"));
            }else {
                int level = ModifierUtil.getModifierLevel(stack, this.Modifierid);
                if (this.modifier_level - level != 1){
                    PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.enchanting.bad_level"));
                    return false;
                }
                return checkItemMatch(stack);
            }
        }
        return false;
    }

    public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
        pedestalItems = (List) pedestalItems.stream().filter((itemStack) -> !itemStack.isEmpty()).collect(Collectors.toList());
        return this.pedestalItems.size() == pedestalItems.size() && doItemsMatch(pedestalItems, this.pedestalItems) && this.doesReagentMatch(reagent, player);
    }

    public boolean doesReagentMatch(ItemStack stack) {
        int level = ModifierUtil.getModifierLevel(stack, this.Modifierid);
        return this.modifier_level - level == 1 &&
               checkItemMatch(stack);
    }

    public @NotNull ItemStack assemble(EnchantingApparatusTile inv, RegistryAccess p_267165_) {
        ItemStack stack = inv.getStack().getItem() == Items.BOOK ? new ItemStack(Items.ENCHANTED_BOOK) : inv.getStack().copy();
        ToolStack toolStack = ToolStack.from(stack);
        toolStack.addModifier(Modifierid, modifier_level);
        toolStack.updateStack(stack);
        return stack;
    }

    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer) NovaRegistry.MODIFIABLE_ENCHANTMENT_SERIALIZER.get();
    }

    public JsonElement asRecipe() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", Dreamtinker.MODID + ":enchantment");
        jsonobject.addProperty("modifier", String.valueOf(Modifierid));
        jsonobject.addProperty("level", this.modifier_level);
        jsonobject.addProperty("sourceCost", this.getSourceCost());
        JsonArray pedestalArr = new JsonArray();

        for (Ingredient i : this.pedestalItems) {
            JsonObject object = new JsonObject();
            object.add("item", i.toJson());
            pedestalArr.add(object);
        }

        jsonobject.add("pedestalItems", pedestalArr);
        return jsonobject;
    }

    private boolean checkItemMatch(ItemStack stack) {
        if (stack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(stack);
            // common errors
            Component commonError = validatePrerequisites(tool);
            if (commonError != null){
                return false;
            }

            // consume slots
            tool = tool.copy();
            ToolDataNBT persistentData = tool.getPersistentData();
            SlotType.SlotCount slots = this.slots;
            if (slots != null){
                persistentData.addSlots(slots.type(), -slots.count());
            }

            // add modifier
            tool.addModifier(this.Modifierid, 1);

            // ensure no modifier problems
            Component toolValidation = tool.tryValidate();
            if (toolValidation != null){
                return false;
            }
            return true;
        }
        return false;
    }

    @Nullable
    protected Component validatePrerequisites(IToolStackView tool) {
        return this.validatePrerequisites(tool, (false/*this.checkTraitLevel*/ ? tool.getModifiers() : tool.getUpgrades()).getLevel(this.Modifierid) + 1);
    }

    @Nullable
    protected Component validatePrerequisites(IToolStackView tool, int resultLevel) {
        Component error = this.validateLevel(resultLevel);
        return error != null ? error : checkSlots(tool, this.slots);
    }

    @Nullable
    protected static Component checkSlots(IToolStackView tool, @Nullable SlotType.SlotCount slots) {
        if (slots != null){
            int count = slots.count();
            if (tool.getFreeSlots(slots.type()) < count){
                if (count == 1){
                    return Component.translatable(KEY_NOT_ENOUGH_SLOT, new Object[]{slots.type().getDisplayName()});
                }

                return Component.translatable(KEY_NOT_ENOUGH_SLOTS, new Object[]{count, slots.type().getDisplayName()});
            }
        }

        return null;
    }

    @Nullable
    protected Component validateLevel(int resultLevel) {
        if (resultLevel != this.modifier_level){
            return Component.translatable("dreamtinker.modifiable_enchantment,err");
        }
        return null;
    }
}
