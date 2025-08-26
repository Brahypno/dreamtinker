package org.dreamtinker.dreamtinker.data.providers;

import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.library.client.data.material.AbstractPartSpriteProvider;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

public class DreamtinkerPartSpriteProvider extends AbstractPartSpriteProvider {
    public DreamtinkerPartSpriteProvider() {
        super(Dreamtinker.MODID);
    }

    @Override
    public String getName() {
        return "Dreamtinker Part Sprite Provider";
    }

    @Override
    protected void addAllSpites() {
        addHead("explode_core");
        //addSprite("item/tool/parts/explode_core", HeadMaterialStats.ID);
        buildTool("tntarrow").addHead("explode_core").addHandle("arrow_handle").addHandle("arrow_wing");
        buildTool("masu").withLarge()
                         .addBreakableHead("masu_blade").addBreakableHead("masu_line")
                         .addBreakablePart("masu_binding", StatlessMaterialStats.BINDING.getIdentifier()).addHandle("masu_handle")
                         .addHandle("masu_end");
    }
}
