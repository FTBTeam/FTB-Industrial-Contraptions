# Industrial Contraptions — 26.1 NeoForge

## Git Policy (CRITICAL)
**NEVER commit, push, merge, rebase, or run any destructive git command.** Read-only commands (`status`, `log`, `diff`, `branch`) are fine. The developer handles all writes manually.

## Imports (CRITICAL)
**NEVER use inline fully-qualified class names.** Always add a real `import` at the top and use the bare type name. Inline FQNs like `sp.containerMenu instanceof dev.ftb.mods.industrialcontraptions.screen.TeleporterMenu menu` or `new dev.ftb.mods.industrialcontraptions.recipe.MachineRecipe(...)` are forbidden, regardless of namespace (IC, `net.minecraft.*`, `net.neoforged.*`, etc.). Reasons: refactor-safe (renames update one import line, not every call site), readable (no visual noise), tooling-friendly (IDEs and code search work properly).

The single accepted exception: the `@EventBusSubscriber(modid = IC.MOD_ID, value = Dist.CLIENT)` style annotation parameters where you can't use the imported `Dist` constant in a constant expression — those are unavoidable.

## DeferredHolder over raw `Supplier<T>` (CRITICAL)
Always prefer `DeferredHolder` / `DeferredItem` / `DeferredBlock` for registry references, not raw `Supplier<T>`.

`DeferredRegister.register(...)` returns a `DeferredHolder` that implements `Supplier<T>`, so declaring the field as `Supplier<T>` compiles, but that throws away the `ResourceKey` / `Identifier` carried by the holder and loses integration with NeoForge's registration ordering and tag/data lookups. **As a general rule, field types always declare `DeferredHolder` (or the typed `DeferredItem` / `DeferredBlock` subclass), never `Supplier<T>`**, even when `Supplier<T>` would technically work.

Method parameters that genuinely only need lazy resolution can stay `Supplier<T>`, but registry fields and anything that might need the registered key later must be `DeferredHolder`.

## Comments (CRITICAL)
No narrative class/method headers that describe what the code does, how it flows, or what 1.18.2 behaviour it mirrors. Good code and good names speak for themselves. Delete on sight any multi-paragraph Javadoc, any comment that restates the signature, any `// section:` banner, any breadcrumb about legacy source.

Javadoc is reserved for genuinely non-obvious invariants or warnings a reader cannot derive from reading the code itself. When one is actually warranted, keep it to one short sentence. Do not use dashes or semicolons in comments. Prefer short plain sentences.

Example of what NOT to write:
```java
/**
 * Fluid-source pump — walks the same 5x5 column grid as the Quarry, but instead of breaking blocks
 * it targets fluid source blocks (water, lava) and accumulates them into an internal fluid tank
 * ({@link #fluidAmount} mB of {@link #storedFluid}). The tank is exposed via a
 * {@code ResourceHandler<FluidResource>} (see {@code PumpTankHandler}) so foreign pipes can drain
 * the accumulated fluid out.
 *
 * Convenience behaviour: an empty bucket in input slot 0 will be auto-filled with 1000 mB from the
 * tank (matching fluid type) and pushed to the output slot, preserving the 1.18.2 bucket workflow.
 */
```
That is AI narration and must not exist in this codebase.

## Target
- Minecraft **26.1.2** / NeoForge **26.1.2.73** / Java **25** / FML **11.x**
- Mod ID: `ic`, base package: `dev.ftb.mods.industrialcontraptions`
- Old 1.18.2 reference at `C:/Users/Saereth/Documents/code/ftbicsource/` (read-only — signatures are wrong for 26.1, but balance/behavior intent is authoritative).

## Legacy `ftbic` namespace (CRITICAL)
The mod was renamed from "FTB Industrial Contraptions" (`ftbic`) to "Industrial Contraptions" (`ic`). Existing worlds are carried over by `LegacyRegistryAliases`, which registers a `ftbic:<path>` alias for every `ic:<path>` entry across all content registries. NeoForge's patched `MappedRegistry.getValue`/`get` resolve aliases, so old IDs in chunk palettes, inventories, and block entity NBT still load.

- Every new registry added to `IC`'s constructor must also be passed to `LegacyRegistryAliases.apply(...)` or its content will not load in pre-rebrand worlds.
- `addAlias` throws once `RegisterEvent` has fired, so the call has to stay in the mod constructor.
- Aliases cover registries only. Tag files, recipe IDs, and datapack paths under `data/ftbic/` are **not** aliased, so third-party packs referencing those need updating by hand.
- `LegacyConfigMigration` copies `config/ftbic-common.toml` to `config/ic-common.toml` on first run. `ReactorPresetLibrary` moves `local/ftbic/reactor_layout` to `local/ic/reactor_layout`.
- `/ftbic` is registered as a Brigadier redirect to `/ic`.
- **Do not add a second `[[mods]]` entry for `ftbic` to `neoforge.mods.toml`.** It was tried and reverted. Jade enumerates mod containers and scans each one's jar for `@WailaPlugin` classes, so a single jar declaring two mods offers `ICJadePlugin` twice and Jade aborts with "Duplicate plugin class", which is a fatal startup error. Any integration that scans per mod container rather than per mod file has the same problem. If the retired mod ID ever has to be declared again, it needs its own code-free nested mod file (`modLoader = "lowcodefml"`) via JarJar, because scan data is per mod file.
- The regression test `legacy_ids_resolve_to_current_entries` in `ICGameTestFunctions` guards the alias mechanism. Run it with `./gradlew runGameTestServer`.

## Build
```bash
./gradlew compileJava    # fast iteration
./gradlew build          # → build/libs/industrial-contraptions-*.jar
./gradlew runClient      # launch dev MC
./gradlew runClientData  # regenerate datagen output (src/generated/resources/)
```

## Recipes — ALL come from datagen

Every recipe type is emitted by `dev.ftb.mods.industrialcontraptions.datagen.ICRecipeProvider`. There are **no hand-written recipe JSONs** — `src/main/resources/data/ic/recipe/` is empty; everything lives in `src/generated/resources/data/ic/recipe/`.

- After adding/changing a recipe in Java: `./gradlew runClientData`
- Tag-based ingredients automatically get a `neoforge:not(tag_empty)` condition — no manual wrapping needed; the `conditionsFor(...)` helper scans each Ingredient and emits the right condition.
- The `ICRecipeProvider.tag(TagKey)` / `commonTag(String)` helpers build tag-backed Ingredients correctly during datagen (regular `Ingredient.of(TagKey)` is not available; must go through `items.getOrThrow(tag)`).
- Cooking recipes use `new Recipe.CommonInfo(...)` + `CraftingBookInfo` + `ItemStackTemplate` (not raw ItemStack — components aren't bound at datagen time).
- `CompoundIngredient.of(a, b, ...)` for OR-style ingredients (the mixed_metal_blend recipes).
- `FluidCellIngredient` wraps a fluid as an item input (canning, compressing, separating with water cells).

## 26.1 API Gotchas

### Renames
| Old | New |
|---|---|
| `ResourceLocation` | `Identifier` (`Identifier.fromNamespaceAndPath`); `ResourceKey#location()` → `#identifier()` |
| `GuiGraphics` / `Screen#render` | `GuiGraphicsExtractor` / `Screen#extractRenderState` (state-extraction model); `drawString` → `text` |
| `InteractionResultHolder<T>` / `ItemInteractionResult` | `InteractionResult` (merged) |
| `CompoundTag` direct access | `ValueInput` / `ValueOutput` (`getIntOr`, `putString`, codec-via-`store`/`read`) |
| `Level#isClientSide` (field) | `Level#isClientSide()` (method) |
| `PacketDistributor.sendToServer()` | `ClientPacketDistributor.sendToServer()` |
| `stack.getContainerItem()` | `stack.getCraftingRemainder()` → `ItemStackTemplate` (call `.toStack()` to rehydrate) |
| `player.displayClientMessage` | `player.sendSystemMessage(Component)` |
| `Block.use(...)` | `useWithoutItem(...)` + `useItemOn(...)` (split) |
| `Block.onRemove(...)` | `affectNeighborsAfterRemoval(state, level, pos, bool)` |
| `neighborChanged(..., BlockPos, bool)` | `neighborChanged(..., Orientation, bool)` |
| `BE.load/saveAdditional(CompoundTag)` | `loadAdditional(ValueInput)` / `saveAdditional(ValueOutput)` — must call `super` |
| `DirectionProperty` | `EnumProperty<Direction>` |
| `java.util.Random` (in tick/animateTick) | `RandomSource` |
| `TextComponent`/`TranslatableComponent` | `Component.literal/translatable` |

### Capabilities (rewritten in 26.1.1.x)
- Use `Capabilities.Energy.BLOCK` → `net.neoforged.neoforge.transfer.energy.EnergyHandler`
- Use `Capabilities.Item.BLOCK` → `ResourceHandler<ItemResource>`
- Use `Capabilities.Fluid.BLOCK` → `ResourceHandler<FluidResource>`
- See `ElectricBlockResourceHandler` + `ElectricBlockEnergyHandler` for the canonical transaction-safe `SnapshotJournal` pattern. FE↔zap conversion via `ZAP_TO_FE_CONVERSION_RATE`.

### Registration (since 1.21.2)
`DeferredRegister#register` factory receives the `Identifier`. `Block.Properties` and `Item.Properties` must call `.setId(ResourceKey.create(Registries.X, name))`:

```java
DeferredBlock<MyBlock> MY = REGISTRY.register("my_block",
    name -> new MyBlock(BlockBehaviour.Properties.of()
        .setId(ResourceKey.create(Registries.BLOCK, name))
        .strength(3f).sound(SoundType.METAL)));
```

### JEI (version 29.5 / 1.21.1)
- Text and animated drawables MUST go through `createRecipeExtras(IRecipeExtrasBuilder, ...)` with `builder.addText(...)` / `addAnimatedRecipeArrow(...)` / `addAnimatedRecipeFlame(...)`. Calling `graphics.text()` inside `draw()` renders nothing in JEI's extraction pipeline.
- Slot backgrounds: chain `.setStandardSlotBackground()` or `.setOutputSlotBackground()` on the builder.

### BlockEntity update-tag trap
- `BlockEntity.getUpdateTag()` default returns empty. Any BE with client-visible state (inventory, energy, progress, tanks) **must** override `getUpdateTag` to return `saveCustomOnly(registries)`. See `ElectricBlockEntity`.

### Damage hook
- `LivingDamageEvent` is split: `Pre` (mutable, use `setNewDamage(float)`) and `Post`. See `EnergyArmorDamageHandler`.

### GameTests (26.1)
Working example lives in `src/main/java/dev/ftb/mods/industrialcontraptions/test/`. Run with `./gradlew runGameTestServer`. Key 26.1 API shifts the gradle-cached decompiled sources get wrong:

- `@GameTest` / `@GameTestHolder` are **gone**. Build a `GameTestInstance` subclass that holds a `Consumer<GameTestHelper>` and a `MapCodec`, register the codec in a `DeferredRegister<MapCodec<? extends GameTestInstance>>` against `Registries.TEST_INSTANCE_TYPE`, then call `event.registerTest(Identifier, GameTestInstance)` from `RegisterGameTestsEvent`.
- `helper.getBlockEntity(BlockPos)` **is gone** — must pass a class: `helper.getBlockEntity(pos, MyBlockEntity.class)`. Returns `null` (no `Optional`) on mismatch.
- `assertValueEqual(expected, actual, name)` — **expected is first**. Getting this backwards produces confusing error messages where the reported "expected" is actually your real value.
- `ServerLevel.setDayTime(long)` **doesn't exist** in 26.1.2 (the decompiled sources in the gradle cache still show it — they're stale). Real API is `level.clockManager().setTotalTicks(Holder<WorldClock>, long)`:
  ```java
  Holder<WorldClock> clock = level.registryAccess()
      .lookupOrThrow(Registries.WORLD_CLOCK).getOrThrow(WorldClocks.OVERWORLD);
  level.clockManager().setTotalTicks(clock, 6000L);
  level.updateSkyBrightness();
  ```
  The `updateSkyBrightness()` call matters: `Level.isBrightOutside()` reads the `skyDarken` field, which only refreshes when updateSkyBrightness fires (normally once per level tick). Even then, environment-attribute propagation for clock changes can lag a few ticks inside the gametest harness — assert weaker invariants when you can.
- `BlockEntity.loadCustomOnly(CompoundTag, HolderLookup.Provider)` is gone — now `loadCustomOnly(ValueInput)`. Wrap a saved tag via `TagValueInput.create(ProblemReporter.DISCARDING, registries, tag)` to roundtrip a BE in a test.
- `ProblemReporter.Collector` is **not** `AutoCloseable` — can't use try-with-resources. Use `ProblemReporter.DISCARDING` for throwaway loads.
- `ElectricBlockEntity.loadAdditional` calls `initProperties() + upgradesChanged()` at the end with the upgrade inventory still empty (super runs before subclass deserializes upgrades). `BasicMachineBlockEntity.loadAdditional` re-calls them after `upgradeInventory.deserialize` so properties end up correct after a chunk load. `UpgradeInventory.setStackInSlot` also calls them via `onContentsChanged`. **Don't manually call `upgradesChanged()`** after `loadCustomOnly` in tests — you'll apply upgrade multipliers twice (classic 1.45² → 1.45⁴ bug). Just trust the load to leave properties correct.
- Structure templates: place a binary `.nbt` at `src/main/resources/data/<modid>/structure/empty.nbt` (SimpleTomb's 109-byte empty template copies cleanly). `.snbt` via `StructureTemplateManager.loadFromSnbt` also works but is harder to author.

## Where to look when the compiler complains
1. **This codebase** — patterns are consistent; grep first.
2. **Decompiled NeoForge sources**: `C:/Users/Saereth/.gradle/caches/ng_execute/*/transformed/net/minecraft/`
3. **Reference project**: `C:/Users/Saereth/Documents/code/auroral/` — working 26.1 NeoForge mod.
4. Old 1.18.2 source at `C:/Users/Saereth/Documents/code/ftbicsource/` for behavior/balance intent (do **not** copy code — signatures are wrong, but tick counts, config defaults, recipe timings, and design parity should match).
