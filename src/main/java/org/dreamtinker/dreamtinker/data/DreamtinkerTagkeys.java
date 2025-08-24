package org.dreamtinker.dreamtinker.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

public class DreamtinkerTagkeys {
    public static class Modifiers {
        private static TagKey<Modifier> DreamtinkerTag(String name) {
            return ModifierManager.getTag(new ResourceLocation(Dreamtinker.MODID, name));
        }

        public static final TagKey<Modifier> CURSED_MODIFIERS = DreamtinkerTag("cursed_modifiers");
    }
}
