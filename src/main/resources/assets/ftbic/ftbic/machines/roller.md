---
navigation:
  title: Roller
  icon: ftbic:roller
  parent: machines/index.md
  position: 6
item_ids:
  - ftbic:roller
  - ftbic:advanced_roller
---

# <Color id="gold">Roller</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="roller" scale="2" />

  Produces **plates** by rolling ingots flat. An alternative to the <ItemLink id="extruder" /> plate recipes: same inputs, same outputs, different machine.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="roller" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

**Stats:** LV tier, <Energy config="machines.roller_capacity" /> buffer, <Energy config="machines.roller_use" rate="true" /> use.

The Roller exists so you can split plate production off the Extruder and parallelize, which is useful once you start chewing through copper plates for cables.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="advanced_roller" />
  ### <Color id="aqua">Advanced Roller</Color>
</Row>

MV tier. <Energy config="machines.advanced_roller_use" rate="true" />, <Energy config="machines.advanced_roller_capacity" /> buffer. Same recipes and speed, and it accepts <ItemLink id="parallel_processing_upgrade" />s.

<RecipeFor id="advanced_roller" />
