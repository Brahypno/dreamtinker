# Narcissus charging sigil

One right-click charge displays one sigil at the held tool's tip. A multishot
volley shares that sigil. Uncharged Flaming Memory attacks never display it.
Cancelling use removes the charging geometry without a firing flash.

The client config `DreamTinkerClientConfig.toml` contains:

```toml
["Narcissus Sigil"]
sizeFactor = 1.0
```

This scales the whole sigil around its muzzle anchor. `0.75` displays it at 75%
of the initial size; `1.25` displays it at 125%. The initial diameter is about
two thirds of the baked tool's length. It changes neither projectile size nor
server gameplay.

`NarcissusWing.getDrawProgress()` reuses TConstruct's stored drawtime and
`GeneralInteractionModifierHook.getToolCharge()`, matching `MemoryBase` and
its draw-speed modifiers. Cached paths reveal their actual lengths in the four
design stages. Holding full charge keeps the completed structure with a slight
brightness pulse.

Each charge selects one of four cached, sparse patterns (perimeter string art,
rosette, interlocking squares or orbital crown) and a random initial angle.
The choice remains fixed throughout charging and the firing fade. The primary
head material (slot 0, following the projectile's existing color selection)
supplies both line-layer colors through TConstruct's material render info.
Only a missing material/render-info entry uses the original cyan-white palette.

The outer circle uses 48 segments and petal ellipses use 32. Ribbon joins are
cached with each path, so drawing them no longer recomputes segment normals.
The main line width is 20% wider, and only the outer frame and central core get
one faint perpendicular ribbon to give them depth from an oblique angle.
Movement leaves at most two low-detail outer-contour afterimages. They sample
at most once per tick, fade quadratically over three ticks and do not accumulate
while stationary. Cancellation removes them with the charging sigil.

`NarcissusHandRendererMixin` runs at the shared first/third-person held-item
entry point. It applies the same resolved model and Forge hand transformations
as the item renderer. The tip comes from the baked geometry, including
TConstruct's large-tool offset. The plane faces the shooting direction.

Only a successful, charged server volley sends `S2CNarcissusSigilShot`, once
after projectile spawning. Tracking clients and the shooter receive the event.
It preserves the released charge's completeness, briefly contracts and fades
the sigil over three ticks, and draws short ribbons to the spawned projectiles.
First-person projection conversion accounts for the hand/world FOV difference.
The authoritative spawn position, trajectory and collision remain unchanged.

All new rendering uses Minecraft/Forge and DreamTinker's render types. Neither
Malum nor Lodestone is used. The two unlit line layers provide a clear core and
a wider faint glow, without world lighting changes or a bloom dependency.
Inactive client charge samples expire and are cleared when the level changes; no new
persistent player or tool NBT is introduced.

## Validation

Offline Java compilation and resource processing check the integration and
Mixin mapping. Geometry validation checks finite coordinates, nonzero segment
lengths, monotonic reveal, phase bounds, incomplete partial charges and complete
full charges. No generated data provider changed; `runData` is unnecessary.

The in-game acceptance pass still needs:

- First/third person, right/left main arm, main/off hand, steep aim angles.
- Partial/full charge, modified draw speed, holding full charge and rapid shots.
- One shared sigil for multishot; no sigil for uncharged automatic attacks.
- Cancel, switch item and death without a flash or leftover geometry.
- Another player's charging and firing effects in multiplayer.
- Near-wall/close-target shots, different FOV values and graphics modes.

The generated geometry preview checks the design, rather than in-game placement
or shader compatibility. Those require a running Minecraft client.
