package org.dreamtinker.dreamtinker.data.providers.model;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.registration.object.IdAwareObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.AbstractToolItemModelProvider;

import java.io.IOException;

/**
 * Provider for tool models, mostly used for duplicating displays
 */
public class DreamtinkerToolItemModelProvider extends AbstractToolItemModelProvider {
    public DreamtinkerToolItemModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, existingFileHelper, Dreamtinker.MODID);
    }

    @Override
    protected void addModels() throws IOException {
        JsonObject toolBlocking = readJson(TConstruct.getResource("base/tool_blocking"));
        //JsonObject shieldBlocking = readJson(Dreamtinker.getLocation("base/shield_blocking"));

        // blocking //
        // pickaxe
        tool(DreamtinkerItems.mashou, toolBlocking, "mashou_blade", "mashou_line", "mashou_binding");
        // armor
        armor("under_plate", DreamtinkerItems.underPlate, "plating", "maille", "maille1");
        //shield("plate", TinkerTools.plateShield, readJson(getResource("base/shield_large_blocking")), "plating", "core");
        pulling_wo_broken(DreamtinkerItems.narcissus_wing, readJson(Dreamtinker.getLocation("base/narcissus_wing_blocking")), AmmoType.NONE, 1, "wish");
    }

    private void pulling_wo_broken(IdAwareObject bow, JsonObject properties, AmmoHandler ammo, int pullingCount, String... pullingParts) throws IOException {
        ResourceLocation id = bow.getId();
        String name = id.getPath();
        JsonObject base = this.readJson(id);
        base.remove("overrides");
        this.withDisplay("tool/" + name + "/blocking", id, properties);
        ammo.apply(this, name, base, properties, pullingCount, pullingParts);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Tool Item Model Provider";
    }
}
