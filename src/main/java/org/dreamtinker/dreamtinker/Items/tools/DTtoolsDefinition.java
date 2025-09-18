package org.dreamtinker.dreamtinker.Items.tools;

import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

public class DTtoolsDefinition {
    public static final ToolDefinition TNTARROW = ToolDefinition.create(DreamtinkerItems.tntarrow);
    public static final ToolDefinition MASU = ToolDefinition.create(DreamtinkerItems.mashou);
    public static final ToolDefinition narcissus_wing = ToolDefinition.create(DreamtinkerItems.narcissus_wing);
    public static final ModifiableArmorMaterial UNDER_PLATE =
            ModifiableArmorMaterial.create(Dreamtinker.getLocation("under_plate"), Sounds.EQUIP_PLATE.getSound());
}
