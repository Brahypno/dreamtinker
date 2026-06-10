package org.brahypno.dreamtinker.common.event;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.Entity.capabilities.ShellHeartProvider;
import org.brahypno.dreamtinker.common.DreamtinkerAttributes;
import org.brahypno.dreamtinker.common.capabilities.IShellHeart;

import java.util.Collection;
import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Dreamtinker.MODID)
public class ShellHeartHandler {
    private static final DynamicCommandExceptionType INVALID_HEX =
            new DynamicCommandExceptionType(value ->
                                                    Component.translatable("command.dreamtinker.shellheart.invalid_colour", value));
    private static final int DEFAULT_HEART_COLOUR = 0xFFC4B5D6;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHealRegenShellHeart(LivingHealEvent event) {
        if (event.isCanceled()){
            return;
        }
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide){
            return;
        }

        AttributeInstance attr = entity.getAttribute(DreamtinkerAttributes.BLOOD_IN_SHELL.get());
        if (attr == null){
            return;
        }

        float cap = (float) attr.getValue();
        if (cap <= 0.0F){
            return;
        }

        float healAmount = event.getAmount();
        if (healAmount <= 0.0F){
            return;
        }
        ShellHeartProvider.getShellHeart(entity).ifPresent(shellHeart -> {
            float currentShellHeart = shellHeart.get();

            if (currentShellHeart >= cap){
                return;
            }

            float added = Math.min(healAmount, cap - currentShellHeart);
            shellHeart.add(added);
            if (entity instanceof ServerPlayer player){
                ShellHeartProvider.syncToClient(player);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    static void ShellHeartHurtReduction(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide() || entity.isDeadOrDying() || event.isCanceled()){
            return;
        }

        DamageSource source = event.getSource();
        if (entity.isInvulnerableTo(source)){
            return;
        }

        ShellHeartProvider.getShellHeart(entity).ifPresent(shellHeart -> {
            float damage = event.getAmount();
            float currentShellHeart = shellHeart.get();
            float toughness = getShellHeartToughness(entity);
            if (damage <= 0.0F || currentShellHeart <= 0.0F || toughness <= 0.0F){
                return;
            }

            float blocked = Math.min(damage, currentShellHeart * toughness);
            float consumed = blocked / toughness;

            shellHeart.add(-consumed);
            event.setAmount(damage - blocked);

            if (entity instanceof ServerPlayer player){
                ShellHeartProvider.syncToClient(player);
            }
        });
    }

    private static float getShellHeartToughness(LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(DreamtinkerAttributes.SHELL_HEART_TOUGHNESS.get());
        return attr == null ? 1.0F : Mth.clamp((float) attr.getValue(), 0.001F, 4096.0F);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(new ResourceLocation(Dreamtinker.MODID, "shell_heart"), new ShellHeartProvider());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        oldPlayer.reviveCaps();

        try {
            Optional<IShellHeart> newShellOptional = ShellHeartProvider.getShellHeart(newPlayer);

            if (newShellOptional.isEmpty()){
                return;
            }

            IShellHeart newShell = newShellOptional.get();

            ShellHeartProvider.getShellHeart(oldPlayer)
                              .ifPresent(newShell::copyFrom);
        }
        finally {
            oldPlayer.invalidateCaps();
        }
        if (!event.getEntity().level().isClientSide && newPlayer instanceof ServerPlayer sp){
            ShellHeartProvider.syncToClient(sp);
        }
    }

    @SubscribeEvent
    public static void EntityJoinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !event.getLevel().isClientSide){
            ShellHeartProvider.syncToClient(player);
        }
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("dreamtinker")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("shell_heart")

                                      .then(Commands.literal("set")
                                                    .then(Commands.argument("targets", EntityArgument.players())
                                                                  .then(Commands.argument("amount", FloatArgumentType.floatArg(0F))
                                                                                .executes(ctx -> setShellHeart(ctx.getSource(),
                                                                                                               EntityArgument.getPlayers(ctx, "targets"),
                                                                                                               FloatArgumentType.getFloat(ctx, "amount"))))))

                                      .then(Commands.literal("add")
                                                    .then(Commands.argument("targets", EntityArgument.players())
                                                                  .then(Commands.argument("amount", FloatArgumentType.floatArg())
                                                                                .executes(ctx -> addShellHeart(ctx.getSource(),
                                                                                                               EntityArgument.getPlayers(ctx, "targets"),
                                                                                                               FloatArgumentType.getFloat(ctx, "amount"))))))

                                      .then(Commands.literal("clear")
                                                    .then(Commands.argument("targets", EntityArgument.players())
                                                                  .executes(ctx -> setShellHeart(ctx.getSource(),
                                                                                                 EntityArgument.getPlayers(ctx, "targets"), 0F))))

                                      .then(Commands.literal("color")
                                                    .then(Commands.argument("targets", EntityArgument.players())
                                                                  .then(Commands.argument("hex", StringArgumentType.word())
                                                                                .executes(ctx -> setShellHeartColour(ctx.getSource(),
                                                                                                                     EntityArgument.getPlayers(ctx, "targets"),
                                                                                                                     StringArgumentType.getString(ctx,
                                                                                                                                                  "hex"))))))

                                      .then(Commands.literal("resetColor")
                                                    .then(Commands.argument("targets", EntityArgument.players())
                                                                  .executes(ctx -> setShellHeartColour(ctx.getSource(),
                                                                                                       EntityArgument.getPlayers(ctx, "targets"),
                                                                                                       DEFAULT_HEART_COLOUR))))
                        )
        );
    }

    private static int setShellHeart(CommandSourceStack source, Collection<ServerPlayer> targets, float amount) {
        int changed = 0;
        float value = Math.max(0F, amount);

        for (ServerPlayer player : targets) {
            Optional<IShellHeart> optional = ShellHeartProvider.getShellHeart(player);
            if (optional.isEmpty())
                continue;

            IShellHeart shellHeart = optional.get();
            shellHeart.set(value);

            ShellHeartProvider.syncToClient(player);
            changed++;
        }

        final int count = changed;
        source.sendSuccess(() -> Component.translatable("command.dreamtinker.shellheart.set", value, count), false);

        return changed;
    }

    private static int addShellHeart(CommandSourceStack source, Collection<ServerPlayer> targets, float amount) {
        int changed = 0;

        for (ServerPlayer player : targets) {
            Optional<IShellHeart> optional = ShellHeartProvider.getShellHeart(player);
            if (optional.isEmpty())
                continue;

            IShellHeart shellHeart = optional.get();
            shellHeart.set(Math.max(0F, shellHeart.get() + amount));

            ShellHeartProvider.syncToClient(player);
            changed++;
        }

        final int count = changed;
        source.sendSuccess(() -> Component.translatable("command.dreamtinker.shellheart.add", amount, count), false);

        return changed;
    }

    private static int setShellHeartColour(CommandSourceStack source, Collection<ServerPlayer> targets, String rawHex)
            throws CommandSyntaxException {
        return setShellHeartColour(source, targets, parseForcedOpaqueColour(rawHex));
    }

    private static int setShellHeartColour(CommandSourceStack source, Collection<ServerPlayer> targets, int colour) {
        int changed = 0;
        int opaqueColour = 0xFF000000 | (colour & 0x00FFFFFF);

        for (ServerPlayer player : targets) {
            Optional<IShellHeart> optional = ShellHeartProvider.getShellHeart(player);
            if (optional.isEmpty())
                continue;

            IShellHeart shellHeart = optional.get();
            shellHeart.setHeartColour(opaqueColour);

            ShellHeartProvider.syncToClient(player);
            changed++;
        }

        final int count = changed;
        final String hex = String.format("0x%08X", opaqueColour);
        source.sendSuccess(() -> Component.translatable("command.dreamtinker.shellheart.color", hex, count), false);
        return changed;
    }

    private static int parseForcedOpaqueColour(String raw) throws CommandSyntaxException {
        String text = raw.trim();

        if (text.startsWith("#")){
            text = text.substring(1);
        }else if (text.startsWith("0x") || text.startsWith("0X")){
            text = text.substring(2);
        }

        if (!(text.length() == 6 || text.length() == 8)){
            throw INVALID_HEX.create(raw);
        }

        try {
            long parsed = Long.parseLong(text, 16);
            return 0xFF000000 | ((int) parsed & 0x00FFFFFF);
        }
        catch (NumberFormatException e) {
            throw INVALID_HEX.create(raw);
        }
    }
}
