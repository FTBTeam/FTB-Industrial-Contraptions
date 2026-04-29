---
navigation:
  title: Basic Generator
  icon: ftbic:basic_generator
  parent: generators/index.md
  position: 1
item_ids:
  - ftbic:basic_generator
---

# <Color id="gold">Basic Generator</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="basic_generator" scale="2" />

  Your first power source — a tiny furnace-grade engine that burns solid fuel for **10 zap/t**.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="basic_generator" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Stats</Color>
</Column>

* **Tier:** LV (outputs 10 zap/t)
* **Internal buffer:** 4,000 zaps
* **Fuel slot:** one stack

Place fuel in the only inventory slot. The generator keeps burning until the fuel value is exhausted — it will **not** stop when the buffer is full, so wire it straight into an <ItemLink id="lv_battery_box" /> to avoid waste.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Accepted Fuels</Color>
</Column>

Anything that burns in a vanilla furnace works, plus a few extras:

* Coal, charcoal, coal blocks, logs, planks, saplings, sticks
* Sugar cane, cactus
* <ItemLink id="scrap" /> — surprisingly potent per item

<RecipesFor id="scrap" />
