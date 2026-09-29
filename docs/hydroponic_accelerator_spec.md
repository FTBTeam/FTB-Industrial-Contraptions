# Hydroponic Accelerator specification

Status: implemented for Minecraft 26.1.2. This document also records the recipe and automation contract for datapack authors.

## Machines and operating modes

| | Hydroponic Accelerator | Advanced Hydroponic Accelerator |
|---|---|---|
| Growth inputs | One seed slot and one soil slot | Four independent seed and soil pairs |
| Mutation inputs | No mutation mode | Adjacent pairs form two independent crosses: 1 + 2 and 3 + 4; each parent keeps its own soil |
| Outputs | Three slots: product, seed, and byproduct | Twelve slots: product or mutant, seed, and byproduct for each of four lanes |
| Progress | One growth operation | Four independent growth operations, or two independent mutation operations |
| Water tank | 16,000 mB | 32,000 mB |
| Input locks | Exact-item ghost locks on the seed and soil slots | Exact-item ghost locks on all eight seed and soil slots |

The advanced machine has an explicit **Growth / Mutation** mode switch. Its mode and input locks survive closing the screen, unloading the chunk, and restarting the world. Changing mode cancels unfinished progress without consuming inputs. The advanced machine's four physical lanes provide parallelism; the Parallel Processing Upgrade does not multiply them further. Other compatible upgrades follow the four-per-type limit.

The advanced machine has one item handler with four input pairs and twelve output slots. Each lane's outputs are reserved for that lane, so a full wheat buffer cannot occupy another crop's slots. In mutation mode, the lead lane of a cross receives the successful mutation result, and each participating lane receives its own returned parent seeds on a failed roll.

## Growth recipes

A growth recipe specifies a seed ingredient, allowed soil options, base duration, water use, guaranteed product, guaranteed seed return, and an optional chanced byproduct. The input seed is consumed once per completed cycle; the soil is a catalyst and is never consumed or damaged. At least one returned seed in the default recipes makes a stocked machine self-sustaining, while datapacks may define different seed yields.

Soil options belong to the **crop recipe**, not to a global whitelist. Each option has its own speed multiplier. A crop runs only when its current soil matches one of its options. If a recipe allows dirt at `1.0` and moss at `1.25`, a 200-tick base cycle takes 200 ticks on dirt and 160 ticks on moss before upgrades. A recipe may omit moss entirely if that crop cannot grow on it. A matching soil is required throughout a cycle; changing either the soil or seed resets that lane's progress. Item automation may replace soil but may not extract a locked input through an automated side.

Vanilla starter example:

| Input | Soil | Duration | Water | Guaranteed output | Optional output |
|---|---|---:|---:|---|---|
| 1 wheat seed | Dirt `1.0x`; moss block `1.25x` | 200 ticks | 250 mB | 2 wheat and 2 wheat seeds | 10% extra wheat seed |

The guaranteed seed output includes the replacement for the consumed input seed. The byproduct roll is independent per cycle. Additional vanilla crop recipes should be added for beetroot, carrot, potato, melon, pumpkin, and other plants whose harvest and planting items are known. Plants without a sensible machine harvest are left to datapacks.

Water is a required fluid input in the supplied recipes. A datapack recipe may set its water cost to zero for plants that do not need it. The soil multiplier changes time only; it does not change the number of outputs, water per operation, or energy per tick. Effective duration is `ceil(base ticks / (soil multiplier × upgrade speed multiplier))`, with a minimum of one tick.

## Mutation recipes

Mutation recipes are **explicit crosses** between two seed ingredients. Parent order does not matter. They specify a base duration, water use, success chance, and one result item stack. The result can be a seed or another plant item; the recipe declares which output type it is. No mutation is inferred merely because two seeds exist in the game.

Both parents must have a matching growth recipe for their respective soil before the cross can run. The advanced machine checks each parent against its own paired soil. The slower of the two soil speed multipliers governs a cross, so a fast soil on only one side does not accelerate both parents. If the roll succeeds, consume one of each parent and output the declared mutant. If it fails, consume one of each parent and return one of each parent seed in their dedicated seed outputs. Thus a failed cross costs time, water, and energy, but does not silently destroy the parents. A successful cross does not also return the parents unless its recipe explicitly makes them the result.

Mutation results and failed-parent returns need output space **before** inputs or water are consumed. The machine reserves capacity for either outcome, then rolls once when the operation completes and consumes the inputs and water. A blocked output pauses progress without rolling. Mutation progress survives save and reload.

## Datapack and compatibility contract

The `ftbic:hydroponic_growth` and `ftbic:hydroponic_mutation` recipe types have separate JEI categories. A growth recipe uses the shared machine-recipe fields plus `soil_options`:

```json
{
  "type": "ftbic:hydroponic_growth",
  "inputs": [{"ingredient": "minecraft:wheat_seeds"}],
  "soil_options": [
    {"ingredient": "minecraft:dirt", "speed": 1.0},
    {"ingredient": "minecraft:moss_block", "speed": 1.25}
  ],
  "input_fluids": [{"ingredient": "minecraft:water", "amount": 250}],
  "processing_time": 1.0,
  "outputs": [
    {"item": {"id": "minecraft:wheat", "count": 2}},
    {"item": {"id": "minecraft:wheat_seeds", "count": 2}},
    {"item": {"id": "minecraft:wheat_seeds"}, "chance": 0.10}
  ]
}
```

The field shapes above match the generated recipes. Mutation recipes declare two entries in `inputs`, one success result in `outputs` with its `chance`, `processing_time`, and optional `input_fluids`. Each parent needs a matching growth recipe. Recipes accept item or tag ingredients, including modded seeds and soils. Datapack authors supply mappings for modded crops; the machine does not guess harvest results from arbitrary items. Soil speeds must be positive and finite. Growth operations use one seed input and two or three outputs; mutation operations use two one-item parent inputs and one result output. Avoid overlapping crop and soil matches or duplicate unordered parent pairs; the first matching recipe wins.

JEI shows each crop's allowed soils, speed, water, power, outputs, and byproduct chance. Mutation JEI shows both parents, their compatible soils, success chance/result, and failed-parent return. Input ghost locks accept normal slot clicks and JEI dragged ingredients using the existing exact-item lock behavior. A lock filters insertion; it is not a virtual seed or soil and never satisfies a recipe by itself.

## Power, automation, and UI

Base operating values: basic 8 zaps/t, advanced 8 zaps/t **per active lane or cross**. Four active advanced growth lanes therefore draw up to 32 zaps/t before upgrades. The existing energy storage and transformer rules apply. If power or water is unavailable, progress pauses rather than consuming ingredients. When resources are scarce, the advanced scheduler rotates its first lane each tick so lane 1 cannot permanently starve lane 4.

Expose item, fluid, and energy faces through the existing side-configuration screen. Seed and soil inputs must remain distinct to automation; soil is never extracted as a recipe ingredient. Output ejection respects the configured output sides. A comparator can report overall output-buffer fullness using the established machine behavior.

The basic screen shows its single growth lane in a compact panel. The advanced screen labels lanes 1 to 4, shows each lane's progress, and groups its three output slots beside that lane. Mutation mode labels the crosses 1+2 and 3+4. The dedicated screens keep their controls and tooltips within the machine panel.

## Acceptance checks

The shipped game tests cover dirt versus moss timing, catalyst persistence, separate advanced crop lanes, and failed mutation returning both parents. In-game checks should also cover UI spacing, JEI ghost dragging, water containers and side configuration, output backpressure, and save/reload behavior.

The central no-duplication rule is that an operation checks the required seed, matching soil, water, energy, and all possible output capacity on the server before consuming anything. The soil stack remains unchanged after every successful or failed cycle.
