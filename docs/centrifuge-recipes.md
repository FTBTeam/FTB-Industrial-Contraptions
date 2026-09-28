# Centrifuge recipes

Both centrifuges use the `ftbic:separating` recipe type. Recipes can consume items, fluids, or both, and produce items, fluids, or both.

| Limit | Centrifuge | Advanced Centrifuge |
|---|---|---|
| Item ingredients | 1 | 1 |
| Fluid ingredients | 1 | 1 |
| Item results | Up to 2 | Up to 3 |
| Fluid results | Up to 1 | Up to 1 |
| Input tank | 16,000 mB | 16,000 mB |
| Output tank | 16,000 mB | 16,000 mB |

Each tank holds one fluid, including its data components. Fluid amounts are in millibuckets. A recipe with three item results only runs in the Advanced Centrifuge. JEI identifies these recipes.

## Default lava recipe

`ftbic:separating/lava_to_nuggets` consumes 1,000 mB of lava with no item input and produces two tin nuggets, one copper nugget, and a 25% chance of one gold nugget. It requires the Advanced Centrifuge because it has three possible item results. It produces no fluid and uses the standard processing time multiplier of 1.0.

Tin nuggets support standard crafting conversions: nine nuggets make one ingot, and one ingot makes nine nuggets.

## Example

This illustrative recipe is not included in the default recipe set. Place it in a data pack at `data/example/recipe/separating/clay_slurry.json` to use it.

```json
{
  "type": "ftbic:separating",
  "inputs": [
    { "ingredient": "minecraft:clay_ball", "count": 2 }
  ],
  "input_fluids": [
    { "ingredient": "minecraft:water", "amount": 1000 }
  ],
  "outputs": [
    { "item": { "id": "minecraft:flint", "count": 1 } },
    { "item": { "id": "minecraft:iron_nugget", "count": 1 } },
    { "item": { "id": "minecraft:gold_nugget", "count": 1 }, "chance": 0.1 }
  ],
  "output_fluids": [
    { "id": "minecraft:water", "amount": 500 }
  ],
  "processing_time": 1.0
}
```

- Omit `inputs` for a fluid-only recipe, or `input_fluids` for an item-only recipe. At least one input is required.
- Omit either output list when that kind of result is not needed. Item results support `chance`; fluid results are guaranteed.
- A fluid ingredient can name a fluid ID or a tag, such as `"#example:slurry"`.
- `processing_time` is multiplied by the configured machine recipe base ticks, then adjusted for machine speed and upgrades.
- The machine checks all output space before spending energy. Inputs are consumed and results produced together when processing finishes. Space is reserved for every possible item result, including chance results.
- A mixed recipe takes priority over a matching item-only recipe when its fluid ingredient is present.

Fluid pipes and fluid containers obey the machine's fluid side configuration. They insert into the input tank and extract from the output tank.
