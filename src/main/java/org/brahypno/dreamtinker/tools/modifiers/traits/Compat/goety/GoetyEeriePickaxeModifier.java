package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.goety;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Reproduces the Eerie Pickaxe utility behavior on cursed-metal primary harvest tools.
 */
public class GoetyEeriePickaxeModifier extends NoLevelsModifier
        implements InventoryTickModifierHook, ProcessLootModifierHook, AttributesModifierHook {
    private static final double ATTRACTION_RANGE = 10.0;
    private static final float SOUND_CHANCE_PER_TICK = 0.00075f;

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK, ModifierHooks.PROCESS_LOOT, ModifierHooks.ATTRIBUTES);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void addAttributes(
            IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot,
            BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot != EquipmentSlot.MAINHAND || tool.isBroken()
            || !tool.hasTag(TinkerTags.Items.HARVEST_PRIMARY)){
            return;
        }
        Attribute attribute = ForgeMod.BLOCK_REACH.get();
        consumer.accept(attribute, new AttributeModifier(
                UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                getTranslationKey(), 3.0, AttributeModifier.Operation.ADDITION));
    }

    @Override
    public void onInventoryTick(
            IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder,
            int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!(world instanceof ServerLevel serverLevel) || !(holder instanceof ServerPlayer player)
            || !isSelected || !tool.hasTag(TinkerTags.Items.HARVEST)){
            return;
        }

        if (!player.isShiftKeyDown()){
            for (ExperienceOrb orb : serverLevel.getEntitiesOfClass(
                    ExperienceOrb.class, player.getBoundingBox().inflate(ATTRACTION_RANGE))) {
                orb.playerTouch(player);
            }
        }

        RandomSource random = serverLevel.getRandom();
        if (random.nextFloat() <= SOUND_CHANCE_PER_TICK){
            BlockPos randomPos = BlockPos.containing(
                    player.getX() + random.nextInt(17) - 8,
                    player.getEyeY() + random.nextInt(17) - 8,
                    player.getZ() + random.nextInt(17) - 8);
            serverLevel.playSound(null, extendedSoundPos(player, randomPos),
                                  pickEerieSound(serverLevel, player, randomPos, random),
                                  SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    private static BlockPos extendedSoundPos(ServerPlayer player, BlockPos randomPos) {
        double targetX = randomPos.getX() + 0.5;
        double targetY = randomPos.getY() + 0.5;
        double targetZ = randomPos.getZ() + 0.5;
        double offsetX = targetX - player.getX();
        double offsetY = targetY - player.getEyeY();
        double offsetZ = targetZ - player.getZ();
        double distance = Math.sqrt(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ);
        if (distance < 1.0E-6){
            return randomPos;
        }
        double extendedDistance = distance + 2.0;
        return BlockPos.containing(
                player.getX() + offsetX / distance * extendedDistance,
                player.getEyeY() + offsetY / distance * extendedDistance,
                player.getZ() + offsetZ / distance * extendedDistance);
    }

    private static SoundEvent pickEerieSound(
            ServerLevel level, ServerPlayer player, BlockPos randomPos,
            RandomSource random) {
        SoundEvent sound = SoundEvents.AMBIENT_CAVE.value();
        if (random.nextFloat() <= 0.01f){
            sound = SoundEvents.GOAT_SCREAMING_AMBIENT;
        }else if (random.nextFloat() <= 0.05f){
            sound = SoundEvents.WARDEN_NEARBY_CLOSE;
        }else if (random.nextFloat() <= 0.15f){
            sound = SoundEvents.SCULK_SHRIEKER_SHRIEK;
        }else if (random.nextFloat() <= 0.25f){
            sound = SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.value();
            if (random.nextBoolean()
                && (!level.dimensionType().hasSkyLight()
                    || (player.getY() >= level.getSeaLevel() && level.canSeeSky(randomPos)))){
                sound = SoundEvents.PHANTOM_FLAP;
            }
        }
        return sound;
    }

    @Override
    public void processLoot(
            IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot,
            LootContext context) {
        if (!tool.hasTag(TinkerTags.Items.HARVEST_PRIMARY)
            || context.getParamOrNull(LootContextParams.BLOCK_STATE) == null){
            return;
        }
        if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player){
            generatedLoot.removeIf(player::addItem);
        }
    }
}
