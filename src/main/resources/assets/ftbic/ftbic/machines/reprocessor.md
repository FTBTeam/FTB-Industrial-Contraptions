---
navigation:
  title: Reprocessor
  icon: ftbic:reprocessor
  parent: machines/index.md
  position: 9
item_ids:
  - ftbic:reprocessor
  - ftbic:scrap
  - ftbic:scrap_box
---

# <Color id="gold">Reprocessor</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="reprocessor" scale="2" />

  Turns **any junk item** into <ItemLink id="scrap" />. Scrap is a premium Basic Generator fuel and can be crafted into <ItemLink id="scrap_box" /> for a random reward.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="reprocessor" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

* Input: cobblestone, dirt, rotten flesh, sticks, or anything tagged recyclable.
* Output: ~12.5% chance of scrap per operation (configurable).
* **Stats:** MV tier, <Energy config="machines.reprocessor_capacity" /> buffer, <Energy config="machines.reprocessor_use" rate="true" /> use.

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="scrap_box" />
  ### <Color id="aqua">Scrap Box</Color>
</Row>

9 scrap compress into a Scrap Box. Use it to drop one random item, ranging from common materials to rare iridium dust. Crouch while using a stack to open every box at once.

<RecipeFor id="scrap_box" />
