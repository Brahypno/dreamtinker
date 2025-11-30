package org.dreamtinker.dreamtinker.tools;

import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

public class DTtoolsDefinition {
    public static final ToolDefinition TNTARROW = ToolDefinition.create(DreamtinkerTools.tntarrow);
    public static final ToolDefinition MASHOU = ToolDefinition.create(DreamtinkerTools.mashou);
    public static final ToolDefinition narcissus_wing = ToolDefinition.create(DreamtinkerTools.narcissus_wing);
    public static final ToolDefinition silence_glove = ToolDefinition.create(DreamtinkerTools.silence_glove);
    public static final ToolDefinition chain_saw_blade = ToolDefinition.create(DreamtinkerTools.chain_saw_blade);
    public static final ModifiableArmorMaterial UNDER_PLATE =
            ModifiableArmorMaterial.create(Dreamtinker.getLocation("under_plate"), Sounds.EQUIP_PLATE.getSound());
}
