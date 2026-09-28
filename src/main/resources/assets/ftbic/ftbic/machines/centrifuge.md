---
navigation:
  title: Centrifuge
  icon: ftbic:centrifuge
  parent: machines/index.md
  position: 7
item_ids:
  - ftbic:centrifuge
  - ftbic:advanced_centrifuge
---

# <Color id="gold">Centrifuge</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="centrifuge" scale="2" />

  Separates materials into their components. Extracts <ItemLink id="ftbic:silicon_gem" /> from quartz or sand, breaks down gravel and flint, and handles slime/magma processing.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="centrifuge" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Notable Separating Recipes</Color>
</Column>

* Quartz or sand → <ItemLink id="ftbic:silicon_gem" />
* Gravel → flint
* Glowstone → glowstone dust
* Magma Cream → slime ball (+ a chance of blaze powder)
* Small Coolant Cell from blaze rods and ice

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="advanced_centrifuge" />
  ### <Color id="aqua">Advanced Centrifuge</Color>
</Row>

MV tier. Runs separating recipes at the same speed for <Energy config="machines.advanced_centrifuge_use" rate="true" />, with a buffer of <Energy config="machines.advanced_centrifuge_capacity" />. It has **three item output slots** and **one fluid output tank**. Recipes with three item results require this machine.

<RecipeFor id="advanced_centrifuge" />

## Fluid Processing

Both centrifuges have a **16,000 mB input tank** on the left and a separate **16,000 mB output tank** on the right. Each tank holds one fluid at a time.

Separating recipes can use an item, a fluid, or both, and can produce items, a fluid, or both. The regular Centrifuge has two item output slots; the Advanced Centrifuge has three. Both accept one item ingredient and one fluid ingredient, and produce at most one fluid result per recipe.

Use fluid pipes or use a bucket or Fluid Cell on the machine to transfer fluids. Containers fill from the output tank or empty into the input tank. Choose **Input**, **Output**, or **Input/Output** on the [I/O configuration](side_configuration.md) fluid tab to control each face. Hover either tank to see its fluid and amount.

Processing waits until every required input is available and all item and fluid results fit. A full output tank or a different fluid already in that tank blocks processing. Existing item-only recipes work with empty tanks.

The **Advanced Centrifuge** separates **1,000 mB of lava** (one bucket) into **2 <ItemLink id="ftbic:tin_nugget" />**, **1 <ItemLink id="minecraft:copper_nugget" />**, and a **25% chance of 1 <ItemLink id="minecraft:gold_nugget" />**. No item input is needed. Craft nine tin nuggets into one tin ingot, or split an ingot back into nine nuggets.

## Ore refining

Process **3 washed ore into 5 refined concentrate**, then smelt each concentrate into one ingot. See [Ore Refining](ore_refining.md) for the full chain and automation.
