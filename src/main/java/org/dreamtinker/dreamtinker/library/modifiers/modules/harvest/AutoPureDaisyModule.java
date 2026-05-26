package org.dreamtinker.dreamtinker.library.modifiers.modules.harvest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.ApiStatus;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.LauncherHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.PlantHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.ShearsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;

import static slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule.TAG_SLOT;

public record AutoPureDaisyModule(float multiplier, InventoryModule input, InventoryModule output)
        implements ModifierModule, MeleeHitModifierHook, MonsterMeleeHitModifierHook.RedirectAfter, LauncherHitModifierHook,
        BlockHarvestModifierHook, ProjectileLaunchModifierHook, OnAttackedModifierHook, PlantHarvestModifierHook, ShearsModifierHook,
        SlingLaunchModifierHook {
    public static final RecordLoadable<AutoPureDaisyModule> LOADER = RecordLoadable.create(
            FloatLoadable.FROM_ZERO.requiredField("multiplier", AutoPureDaisyModule::multiplier),
            InventoryModule.LOADER.directField(AutoPureDaisyModule::input),
            OutputKeyField.INSTANCE,
            Pattern.PARSER.defaultField("output_pattern", Patterns.RESULT, true, module -> module.output.pattern()),
            AutoPureDaisyModule::new
    );
    private static final String BOTANIA_MODID = "botania";
    private static final String TAG_TIME = "dreamtinker_daisy_remaining_time";
    private static final int NO_RECIPE = -1;
    private static final ResourceLocation PURE_DAISY_TYPE_ID = new ResourceLocation("botania", "pure_daisy");
    private static final BlockPos DUMMY_POS = BlockPos.ZERO;
    private static final Map<Item, DaisyResult> CACHE = new HashMap<>();
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.defaultHooks(
            ModifierHooks.MELEE_HIT,
            ModifierHooks.MONSTER_MELEE_HIT,
            ModifierHooks.LAUNCHER_HIT,
            ModifierHooks.BLOCK_HARVEST,
            ModifierHooks.PROJECTILE_LAUNCH,
            ModifierHooks.ON_ATTACKED,
            ModifierHooks.PLANT_HARVEST,
            ModifierHooks.SHEAR_ENTITY,
            ModifierHooks.SLING_LAUNCH
    );
    private static boolean initialized = false;
    private static boolean available = false;
    private static boolean built = false;
    private static Method MATCHES;
    private static Method GET_OUTPUT_STATE;
    private static Method GET_TIME;

    @ApiStatus.Internal
    public AutoPureDaisyModule {}

    public AutoPureDaisyModule(float multiplier, InventoryModule inventory, @Nullable ResourceLocation outputKey, Pattern outputPattern) {
        this(multiplier, inventory,
             InventoryModule.builder().from(inventory).key(outputKey).pattern(outputPattern).filter(ItemPredicate.NONE).slots(inventory.slots()));
    }

    public AutoPureDaisyModule(float multiplier, InventoryModule inventory) {
        this(multiplier, inventory, null, Patterns.RESULT);
    }

    private static boolean isBotaniaLoaded() {
        return ModList.get().isLoaded(BOTANIA_MODID);
    }

    private static void invalidateDaisyCache() {
        CACHE.clear();
        built = false;
    }

    @Nullable
    private static DaisyResult getDaisyResult(Level level, ItemStack stack) {
        if (!isBotaniaLoaded())
            return null;
        if (level == null || level.isClientSide || stack.isEmpty() || !(stack.getItem() instanceof BlockItem))
            return null;
        if (!built)
            rebuildDaisyCache(level);
        return CACHE.get(stack.getItem());
    }

    private static void rebuildDaisyCache(Level level) {
        CACHE.clear();
        built = true;

        if (!isBotaniaLoaded())
            return;
        if (level == null || level.isClientSide || !initReflection())
            return;

        RecipeType<?> type = BuiltInRegistries.RECIPE_TYPE.getOptional(PURE_DAISY_TYPE_ID).orElse(null);
        if (type == null)
            return;

        List<?> recipes;
        try {
            recipes = level.getRecipeManager().getAllRecipesFor((RecipeType) type);
        }
        catch (Throwable ignored) {
            return;
        }

        for (Object recipe : recipes) {
            scanPureDaisyRecipe(level, recipe);
        }
    }

    private static void scanPureDaisyRecipe(Level level, Object recipe) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof BlockItem blockItem))
                continue;

            for (BlockState inputState : blockItem.getBlock().getStateDefinition().getPossibleStates()) {
                try {
                    boolean matched = Boolean.TRUE.equals(MATCHES.invoke(recipe, level, DUMMY_POS, null, inputState));
                    if (!matched)
                        continue;

                    Item inputItem = inputState.getBlock().asItem();
                    if (inputItem == Items.AIR)
                        continue;

                    BlockState outputState = (BlockState) GET_OUTPUT_STATE.invoke(recipe);
                    if (outputState == null || outputState.isAir())
                        continue;

                    Item outputItem = outputState.getBlock().asItem();
                    if (outputItem == Items.AIR)
                        continue;

                    int time = 150;
                    if (GET_TIME != null){
                        Object raw = GET_TIME.invoke(recipe);
                        if (raw instanceof Integer i && i > 0)
                            time = i;
                    }

                    CACHE.putIfAbsent(inputItem, new DaisyResult(new ItemStack(outputItem), time));
                    break;
                }
                catch (Throwable ignored) {
                    // 特殊 Pure Daisy recipe 不适合工具内库存处理，直接跳过，保持兼容静默。
                }
            }
        }
    }

    private static boolean initReflection() {
        if (!isBotaniaLoaded())
            return false;
        if (initialized)
            return available;

        initialized = true;

        try {
            Class<?> pureDaisyRecipe = Class.forName("vazkii.botania.api.recipe.PureDaisyRecipe");
            Class<?> specialFlowerBE = Class.forName("vazkii.botania.api.block_entity.SpecialFlowerBlockEntity");

            MATCHES = pureDaisyRecipe.getMethod("matches", Level.class, BlockPos.class, specialFlowerBE, BlockState.class);
            GET_OUTPUT_STATE = pureDaisyRecipe.getMethod("getOutputState");

            try {
                GET_TIME = pureDaisyRecipe.getMethod("getTime");
            }
            catch (NoSuchMethodException ignored) {
                GET_TIME = null;
            }

            available = true;
        }
        catch (Throwable ignored) {
            available = false;
        }

        return available;
    }

    @Override
    public RecordLoadable<AutoPureDaisyModule> getLoader() {
        return LOADER;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return isBotaniaLoaded() ? DEFAULT_HOOKS : Collections.emptyList();
    }

    @Override
    public void addModules(ModuleHookMap.Builder builder) {
        if (!isBotaniaLoaded())
            return;

        builder.addModule(input);
        builder.addModule(output);
        RecipeCacheInvalidator.addReloadListener(client -> {
            if (!client)
                invalidateDaisyCache();
        });
    }

    private void processItems(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, float amount) {
        processItems(tool, modifier, holder.level(), holder, amount);
    }

    private void processItems(IToolStackView tool, ModifierEntry modifier, Level level, @Nullable LivingEntity holder, float amount) {
        if (!isBotaniaLoaded())
            return;
        if (!input.condition().matches(tool, modifier) || amount <= 0)
            return;

        ModDataNBT data = tool.getPersistentData();
        ResourceLocation key = input.getKey(modifier.getModifier());
        if (!data.contains(key, Tag.TAG_LIST))
            return;

        ListTag list = data.get(key, InventoryModule.GET_COMPOUND_LIST);
        float daisyPower = amount * multiplier;

        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            int time = entry.getInt(TAG_TIME);
            ItemStack stack = null;
            DaisyResult recipe = null;

            if (time == 0){
                time = NO_RECIPE;
                stack = ItemStack.of(entry);
                recipe = getDaisyResult(level, stack);
                if (recipe != null)
                    time = recipe.time();
                entry.putInt(TAG_TIME, time);
            }

            if (time < 0)
                continue;

            if (time > daisyPower){
                entry.putInt(TAG_TIME, Math.max(1, Math.round(time - daisyPower)));
                continue;
            }

            int slot = entry.getInt(TAG_SLOT);
            ItemStack currentResult = output.getStack(tool, modifier, slot);
            int maxStackSize = 0;

            if (!currentResult.isEmpty()){
                maxStackSize = Math.min(currentResult.getMaxStackSize(), output.getSlotLimit(tool, modifier, slot));
                if (currentResult.getCount() >= maxStackSize){
                    entry.putInt(TAG_TIME, 1);
                    continue;
                }
            }

            if (stack == null)
                stack = ItemStack.of(entry);
            if (!stack.isEmpty() && recipe == null)
                recipe = getDaisyResult(level, stack);

            if (recipe == null){
                entry.putInt(TAG_TIME, NO_RECIPE);
                continue;
            }

            ItemStack result = recipe.output().copy();
            if (result.isEmpty()){
                entry.putInt(TAG_TIME, NO_RECIPE);
                continue;
            }

            if (maxStackSize == 0)
                maxStackSize = Math.min(result.getMaxStackSize(), output.getSlotLimit(tool, modifier, slot));

            if (result.getCount() + currentResult.getCount() > maxStackSize ||
                (!currentResult.isEmpty() && !ItemStack.isSameItemSameTags(currentResult, result))){
                entry.putInt(TAG_TIME, 1);
                continue;
            }

            if (currentResult.isEmpty()){
                output.setStack(tool, modifier, slot, result);
            }else {
                currentResult.grow(result.getCount());
                output.setStack(tool, modifier, slot, currentResult);
            }

            stack.shrink(1);
            if (stack.isEmpty()){
                list.remove(i);
                i--;
            }else {
                InventoryModule.writeStack(stack, slot, entry);
                entry.putInt(TAG_TIME, recipe.time());
            }

            if (holder != null){
                level.playSound(null, holder.getX(), holder.getY(), holder.getZ(), SoundEvents.GRASS_PLACE, holder.getSoundSource(), 0.8f, 1.15f);
            }
        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        processItems(tool, modifier, context.getAttacker(), damageDealt);
    }

    @Override
    public void onLauncherHitEntity(IToolStackView tool, ModifierEntry modifier, Projectile projectile, LivingEntity attacker, Entity target, @Nullable LivingEntity livingTarget, float damageDealt) {
        processItems(tool, modifier, attacker, damageDealt);
    }

    @Override
    public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, int harvested) {
        if (tool.hasTag(TinkerTags.Items.HARVEST))
            processItems(tool, modifier, context.getLiving(), harvested);
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        float amount;
        if (arrow != null)
            amount = (float) arrow.getBaseDamage();
        else if (projectile instanceof ProjectileWithPower withPower)
            amount = withPower.getPower();
        else
            amount = ToolStats.PROJECTILE_DAMAGE.getDefaultValue();
        processItems(tool, modifier, shooter, amount);
    }

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (tool.hasTag(TinkerTags.Items.ARMOR))
            processItems(tool, modifier, context.getEntity(), amount);
    }

    @Override
    public void afterSlingLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, LivingEntity target, ModifierEntry slingSource, float force, float multiplier, Vec3 angle) {
        processItems(tool, modifier, holder, force * 2 / multiplier);
    }

    @Override
    public void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, net.minecraft.server.level.ServerLevel world, BlockState state, BlockPos pos) {
        processItems(tool, modifier, context.getLevel(), context.getPlayer(), 1);
    }

    @Override
    public void afterShearEntity(IToolStackView tool, ModifierEntry modifier, net.minecraft.world.entity.player.Player player, Entity entity, boolean isTarget) {
        processItems(tool, modifier, player, 1);
    }

    private enum OutputKeyField implements LoadableField<ResourceLocation, AutoPureDaisyModule> {
        INSTANCE;

        @Override
        public String key() {
            return "output_key";
        }

        @Override
        public ResourceLocation get(JsonObject json, String key, TypedMap context) {
            if (json.has(key))
                return JsonHelper.getResourceLocation(json, key);
            ResourceLocation id = context.get(ContextKey.ID);
            if (id != null)
                return id.withSuffix("_output");
            throw new JsonParseException("Unable to fetch ID from context, this usually indicates a broken JSON deserializer");
        }

        @Override
        public void serialize(AutoPureDaisyModule module, JsonObject json) {
            ResourceLocation key = module.output.key();
            if (key != null)
                json.addProperty(key(), key.toString());
        }

        @Override
        public ResourceLocation decode(FriendlyByteBuf buffer, TypedMap context) {
            return buffer.readResourceLocation();
        }

        @Override
        public void encode(FriendlyByteBuf buffer, AutoPureDaisyModule module) {
            buffer.writeResourceLocation(Objects.requireNonNull(module.output.key()));
        }
    }

    public record DaisyResult(ItemStack output, int time) {}
}
