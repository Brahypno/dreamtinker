package org.dreamtinker.dreamtinker.mixin.malum_mixin;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sammy.malum.data.recipe.builder.SpiritInfusionRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// 把这里改成目标类的全限定名
@Mixin(value = SpiritInfusionRecipeBuilder.Result.class, remap = false)
public abstract class SpiritInfusionRecipeBuilderResultMixin {

    // 编译器生成的外部类引用（内部类 Result -> 外部类 SpiritInfusionRecipeBuilder）
    @Unique
    @Final
    SpiritInfusionRecipeBuilder this$0;

    /**
     * 只拦截 serializeRecipeData 里对 JsonObject.add(String, JsonElement) 的调用。
     * 当 key == "output" 时，如果成品有 NBT，就把 NBT 以 SNBT 字符串写入到 outputObject 里。
     * 然后再调用原始 add。
     */
    @Redirect(
            method = "serializeRecipeData",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/gson/JsonObject;add(Ljava/lang/String;Lcom/google/gson/JsonElement;)V"
            )
    )
    private void dreamtinker$appendNbtBeforeAdd(JsonObject instance, String key, JsonElement element) {
        if ("output".equals(key)){
            ItemStack out = ((SpiritInfusionRecipeBuilderAccessor) this.this$0).getOutput();
            if (out != null && out.hasTag()){
                // 只在 element 是对象时追加（理论上 Ingredient.toJson() 返回的就是对象）
                if (element != null && element.isJsonObject()){
                    JsonObject obj = element.getAsJsonObject();
                    // 将 NBT 以 SNBT 字符串写入，保持 Forge/常规写法
                    obj.addProperty("nbt", out.getTag().toString());
                }
            }
        }
        // 调回原方法，完成 json.add(key, element)
        instance.add(key, element);
    }
}

