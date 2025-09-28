package org.dreamtinker.dreamtinker.tools.items;

import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

public class DTtoolsDefinition {
    public static final ToolDefinition TNTARROW = ToolDefinition.create(DreamtinkerTools.tntarrow);
    public static final ToolDefinition MASU = ToolDefinition.create(DreamtinkerTools.mashou);
    public static final ToolDefinition narcissus_wing = ToolDefinition.create(DreamtinkerTools.narcissus_wing);
    public static final ModifiableArmorMaterial UNDER_PLATE =
            ModifiableArmorMaterial.create(Dreamtinker.getLocation("under_plate"), Sounds.EQUIP_PLATE.getSound());
}
