---
navigation:
  title: Powered Furnace
  icon: ftbic:powered_furnace
  parent: machines/index.md
  position: 2
item_ids:
  - ftbic:powered_furnace
  - ftbic:advanced_powered_furnace
---

# <Color id="gold">Powered Furnace</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="powered_furnace" scale="2" />

  An electric furnace that runs any vanilla smelting recipe. <Energy config="machines.powered_furnace_use" rate="true" />, <Energy config="machines.powered_furnace_capacity" /> buffer, LV tier.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="powered_furnace" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="advanced_powered_furnace" />
  ### <Color id="aqua">Advanced Powered Furnace</Color>
</Row>

MV version with a buffer of <Energy config="machines.advanced_powered_furnace_capacity" /> and <Energy config="machines.advanced_powered_furnace_use" rate="true" /> throughput. Same recipes and speed, and it accepts <ItemLink id="parallel_processing_upgrade" />s.

<RecipeFor id="advanced_powered_furnace" />

Accepts all vanilla smelting recipes plus any mod-added smelting recipes. Use with <ItemLink id="overclocker_upgrade" /> to push throughput further.
