package org.dreamtinker.dreamtinker.tools;

import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

public class DTtoolsDefinition {
    public static final ToolDefinition TNTARROW = ToolDefinition.create(DreamtinkerTools.tntarrow);
    public static final ToolDefinition MASHOU = ToolDefinition.create(DreamtinkerTools.mashou);
    public static final ToolDefinition NarcissusWing = ToolDefinition.create(DreamtinkerTools.narcissus_wing);
    public static final ToolDefinition SilenceGlove = ToolDefinition.create(DreamtinkerTools.silence_glove);
    public static final ToolDefinition ChainSawBlade = ToolDefinition.create(DreamtinkerTools.chain_saw_blade);
    public static final ToolDefinition PerAsperaScriptum = ToolDefinition.create(DreamtinkerTools.per_aspera_scriptum);
    public static final ModifiableArmorMaterial UNDER_PLATE =
            ModifiableArmorMaterial.create(Dreamtinker.getLocation("under_plate"), Sounds.EQUIP_PLATE.getSound());
}
