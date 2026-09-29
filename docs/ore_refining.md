# Ore refining materials

FTBIC discovers conventional metals when the loaded item tags contain matching `c:raw_materials/<material>` and `c:ingots/<material>` families. Every accepted raw item must have an ordinary smelting recipe yielding one ingot from that ingot tag. `c:ores/<material>` adds the optional ore-block entry, and `c:storage_blocks/raw_<material>` adds the optional raw-block entry. Gems, materials without a raw form, incomplete mappings, and overlapping raw-material tags are not automatically processed. Skipped materials are logged with the reason.

The selected output is deterministic: Minecraft items first, FTBIC items next, then other namespaces; ties use the complete item ID. An explicit `raw_output` or `ingot` overrides this choice. Each stage produces that chosen item. Accepted input tags can contain equivalent items from multiple mods.

## Default yields

| Operation | Input | Output |
|---|---|---|
| Macerating ore | 1 ore block | 3 raw ore |
| Macerating raw | 1 raw ore | 2 crushed ore |
| Macerating raw block | 1 raw storage block | 18 crushed ore |
| Washing | 2 crushed ore + 1,000 mB water | 3 washed ore |
| Separating | 3 washed ore | 5 refined concentrate |
| Smelting | 1 crushed, washed, or refined item | 1 ingot |

The complete chain produces **15 ingots per ore block** or **5 per raw ore**. Fortune affects the raw items collected before processing. A raw storage block counts as nine raw items: it yields nine times the crushed ore of one raw item and takes nine times as long, so yield and energy per raw item stay the same. Primary yields are guaranteed; byproducts are additional.

Each intermediate carries `ftbic:refining_material`, an identifier such as `c:iron`. Stages use component-aware ingredients. Ordinary dust and ingots cannot enter the multiplication chain. Do not add all component variants to a common material-specific item tag: item tags cannot distinguish the components.

## Material definitions

Place a recipe JSON at `data/<pack_namespace>/recipe/refining/<name>.json`:

```json
{
  "type": "ftbic:refining_material",
  "definition": {
    "material": "c:osmium",
    "name": "Osmium",
    "color": 8636888,
    "raw_input": "#c:raw_materials/osmium",
    "raw_block_input": "#c:storage_blocks/raw_osmium",
    "ore_input": "#c:ores/osmium",
    "raw_output": "examplemod:raw_osmium",
    "ingot": "examplemod:osmium_ingot",
    "yields": {
      "ore_to_raw": 3,
      "raw_to_crushed": 2,
      "wash_input": 2,
      "wash_output": 3,
      "refine_input": 3,
      "refine_output": 5
    },
    "costs": {
      "crush_time": 1.0,
      "wash_time": 2.0,
      "refine_time": 3.0,
      "wash_fluid": "minecraft:water",
      "fluid_amount": 1000
    },
    "byproducts": [
      { "item": { "id": "minecraft:iron_nugget" }, "chance": 0.25 }
    ]
  }
}
```

Replace the example item IDs with registered items. Only `material` is required; other fields default as shown, except `name`, selectors, and output overrides default to automatic, `color` defaults to `-1` (automatic), and `byproducts` defaults to empty.

- Definitions override automatic profiles for the same material ID. More than one active definition for the same material is an error; override the same JSON path to replace an existing definition.
- `material`: stable namespaced identity. Automatically discovered families use `c:<material>`. Changing it changes the identity stored on existing stacks.
- `ore_input` / `raw_input` / `raw_block_input`: an item ID or a tag prefixed with `#`. Empty selectors use `#c:ores/<material path>`, `#c:raw_materials/<material path>` and `#c:storage_blocks/raw_<material path>` respectively.
- `raw_output`: the ore-crushing output. It must be included in `raw_input`.
- `ingot`: the final output item. Supplying it explicitly bypasses automatic raw-smelting inference, allowing unusual materials to opt in.
- `name`: display label, otherwise derived from the material path.
- `color`: a decimal RGB integer (`0` through `16777215`), or `-1` for automatic color. It has priority over built-in colors and texture sampling.
- Yield counts accept `1` through `64`. Final ingots per raw equal `raw_to_crushed * wash_output / wash_input * refine_output / refine_input`; batches must be complete. Multiply by `ore_to_raw` for the yield per ore block. The raw block stage outputs `9 * raw_to_crushed` crushed ore and is skipped with a log warning when that exceeds one stack; supply a recipe at its generated ID to handle larger yields.
- Times multiply the configured base machine recipe duration (200 ticks by default). Machine energy usage and upgrades determine energy cost. Default times are 10 seconds for crushing (90 for a raw block), 20 for washing, and 30 for separating before upgrades.
- `wash_fluid` accepts a fluid ID or a fluid tag prefixed with `#`; `fluid_amount` accepts `1` through `16000` mB per wash batch.
- Up to two byproduct entries are supported. Use valid item stacks and chance values from `0` to `1`. Primary concentrate plus two byproducts requires the Advanced Centrifuge's three output slots.
- Missing mappings are skipped with a log warning. Narrow conflicting raw-input definitions or disable the unwanted material family to resolve overlaps. A raw block claimed by more than one material only skips that material's raw block stage.

To disable automatic refining of one material:

```json
{
  "type": "ftbic:refining_material",
  "definition": { "material": "c:osmium", "enabled": false }
}
```

NeoForge `neoforge:conditions` can be placed on the definition recipe in the usual way. A condition that does not match contributes no override; use `enabled: false` to deliberately suppress automatic discovery.

## Individual recipe overrides

The generator creates ordinary recipes with stable IDs:

```text
ftbic:refining/c/iron/ore_to_raw
ftbic:refining/c/iron/crushing
ftbic:refining/c/iron/raw_block_crushing
ftbic:refining/c/iron/washing
ftbic:refining/c/iron/centrifuging
ftbic:refining/c/iron/smelting/crushed_ore
ftbic:refining/c/iron/smelting/washed_ore
ftbic:refining/c/iron/smelting/refined_concentrate
```

For a custom material such as `pack:my_metal`, the path is `ftbic:refining/pack/my_metal/<stage>`. A recipe supplied at the exact generated ID takes precedence, including its load conditions. This supports changing a single stage without changing the material profile. Disabling a profile suppresses generated recipes, but does not remove explicit recipes supplied by the pack.

Example ingredient for washed iron:

```json
{
  "neoforge:ingredient_type": "ftbic:refining",
  "item": "ftbic:washed_ore",
  "material": "c:iron"
}
```

Example output of five iron concentrate:

```json
{
  "id": "ftbic:refined_concentrate",
  "count": 5,
  "components": { "ftbic:refining_material": "c:iron" }
}
```

The washer recipe type is `ftbic:washing`. It uses the same `inputs`, `input_fluids`, `outputs`, `output_fluids`, and `processing_time` fields as other FTBIC machine recipes. Its hardware accepts one item input/output and one fluid input/output.

## Reloads, JEI, and colors

Discovery runs during server recipe loading, using the incoming tags. Every reload rebuilds generated recipes from the current datapacks; removed tags and disabled profiles stop generating recipes. Machines invalidate their cached recipe when the recipe map changes. Resolved definitions and recipes sync on login and successful reload, so clients use the server's chosen outputs.

JEI distinguishes material components and shows generated variants. Machine input locks and Batch Feeder patterns preserve them. Colors are presentation only: stacks store the material ID, never sampled pixels.

Color priority is explicit override, known material palette, then dominant midtone from the chosen ingot's model particle sprite. Transparent pixels, near-black outlines, and near-white highlights are excluded. Sampling preserves neutral metals and derives a bright tint for the shaded intermediate sprite. Unsupported/missing models use a neutral fallback. Resource reloads clear the color cache, including when a resource pack changes an ingot texture. For unusual multi-layer or pre-tinted ingot models, use an explicit color for an exact match.
