---
navigation:
  title: Canning Machine
  icon: ftbic:canning_machine
  parent: machines/index.md
  position: 8
item_ids:
  - ftbic:canning_machine
  - ftbic:empty_can
  - ftbic:canned_food
---

# <Color id="gold">Canning Machine</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="canning_machine" scale="2" />

  Packs food into <ItemLink id="canned_food" /> for efficient storage, fills spray paint cans with dye, and assembles <ItemLink id="uranium_fuel_rod" /> cartridges.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="canning_machine" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="empty_can" />
  ### <Color id="aqua">Empty Can</Color>
</Row>

Stamped from tin plates. The container for every canned recipe.

<RecipeFor id="empty_can" />

<Row>
  <ItemImage id="canned_food" />
  ### <Color id="aqua">Canned Food</Color>
</Row>

Takes any food item plus an empty can. Canned variants stack larger and preserve the full nutrition of the original.

<Row>
  <ItemImage id="light_spray_paint_can" />
  ### <Color id="aqua">Spray Paint Cans</Color>
</Row>

Fill an <ItemLink id="empty_can" /> with any dye to make a <ItemLink id="light_spray_paint_can" /> or <ItemLink id="dark_spray_paint_can" />. See [Spray Paint](../utilities/spray_paint.md).

<ItemImage id="minecraft:air" scale="0.25"/>

**Stats:** LV tier, 1,200 zap buffer, 1 zap/t use.
