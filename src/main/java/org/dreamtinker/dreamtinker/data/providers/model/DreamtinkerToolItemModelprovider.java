package org.dreamtinker.dreamtinker.data.providers.model;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.AbstractToolItemModelProvider;

import java.io.IOException;

/**
 * Provider for tool models, mostly used for duplicating displays
 */
public class DreamtinkerToolItemModelprovider extends AbstractToolItemModelProvider {
    public DreamtinkerToolItemModelprovider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, existingFileHelper, TConstruct.MOD_ID);
    }

    @Override
    protected void addModels() throws IOException {
        JsonObject toolBlocking = readJson(TConstruct.getResource("base/tool_blocking"));
        //JsonObject shieldBlocking = readJson(Dreamtinker.getLocation("base/shield_blocking"));

        // blocking //
        // pickaxe
        tool(DreamtinkerItems.masu, toolBlocking, "masu_blade", "masu_line", "masu_binding");
        // armor
        armor("under_plate", DreamtinkerItems.underPlate, "plating", "maille", "maille1");
        // shield
        //shield("plate", TinkerTools.plateShield, readJson(getResource("base/shield_large_blocking")), "plating", "core");
    }

    @Override
    public String getName() {
        return "Dreamtinker Tool Item Model Provider";
    }
}
