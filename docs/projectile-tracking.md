# Projectile tracking module

`dreamtinker:projectile_tracking` enables the existing `ProjectileMixin` tracking
through the `tconstruct:projectile_launch` modifier hook. Add the module to a
data-driven modifier's `modules` list or attach it with `hookBuilder.addModule()`
in a Java modifier.

```json
{
  "type": "dreamtinker:projectile_tracking",
  "target": {
    "type": "mantle:and",
    "predicates": [
      {
        "type": "mantle:tag",
        "tag": "forge:ender_entity"
      },
      "mantle:on_fire"
    ]
  },
  "holder": "dreamtinker:has_absorption"
}
```

This example enables tracking when the shooter has absorption and selects burning
living entities in `forge:ender_entity`. Both `target` and `holder` use the same
`LivingEntityPredicate.LOADER` as Tinkers' Construct's conditional combat modules.
They support registered predicates, entity sets, entity tags, `mantle:and`,
`mantle:or`, and inversion. Omitting either field defaults it to `mantle:any`.
`holder` is checked at launch. In the default area-search mode, `target` is checked
during tracking, including for the cached target. The standard `tool` and
`modifier_level` conditions are also supported.

To exclude players, use an inverted entity set:

```json
{
  "type": "dreamtinker:projectile_tracking",
  "target": {
    "type": "mantle:inverted",
    "inverted_type": "mantle:set",
    "entities": ["minecraft:player"]
  }
}
```

Tracking applies to primary and secondary projectiles, including multishot. The
existing tracker excludes dead entities, the owner, and the owner's allies,
searches a bounding box inflated by 12 blocks, and reuses a valid cached target.
When the cached target stops matching, another search can run every 4 ticks.
Collisions clear tracking and projectiles on the ground do not steer.
Setting a tracking predicate resets the previous target and hard lock, just like
the existing Java tracking mode; if several launch hooks set tracking, the last
one to run supplies the mode.

For provider code:

```java
ProjectileTrackingModule.builder()
    .target(LivingEntityPredicate.tag(DreamtinkerTagKeys.EntityTypes.ENDER_ENTITY))
    .holder(DreamtinkerModifiers.HAS_ABSORPTION)
    .minLevel(2)
    .build();
```

`DreamtinkerModifierProvider` generates both migrated modifier definitions:

- `dreamtinker:love_shooting` uses
  `LivingEntityPredicate.set(EntityType.PLAYER).inverted()` and retains the
  multishot trait.
- `dreamtinker:view_tracing` uses `.lookTarget(16.0f)`, retains priority 10, and
  locks the exact entity resolved by the shooter's ray trace at launch.

Both retain their fixed behavior and `NO_LEVELS` display. Their Java modifier
classes and static registrations have been removed; recipes, tags, and material
traits reference the same modifier IDs through `DreamtinkerModifiers.Ids`.

The look-target mode can be configured in JSON:

```json
{
  "type": "dreamtinker:projectile_tracking",
  "look_target": true,
  "look_range": 16.0
}
```

`look_target` defaults to false; `look_range` defaults to 16 blocks. In this mode,
the ray trace respects block occlusion and evaluates `target` against the selected
living entity at launch. If there is no matching look target, tracking is cleared.
The projectile retains that hard lock while the target is alive, not allied to the
owner, and within 20 blocks of the projectile. If the target is lost, the projectile
does not search for a replacement. Look-target mode applies to secondary
projectiles too.

For a Java launch hook, call
`tracker.dreamtinker$setTargetPredicate(IJsonPredicate<LivingEntity>)` directly.
It adapts `matches()` to the existing Java predicate mode. The
`dreamtinker$setMode(Predicate<Entity>)` entry point remains available.
