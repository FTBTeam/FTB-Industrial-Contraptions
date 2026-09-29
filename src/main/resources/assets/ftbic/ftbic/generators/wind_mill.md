---
navigation:
  title: Wind Mill
  icon: ftbic:wind_mill
  parent: generators/index.md
  position: 4
item_ids:
  - ftbic:wind_mill
---

# <Color id="gold">Wind Mill</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="wind_mill" scale="2" />

  A height-dependent generator that scales from **<ZapMode>0.3 z/t</ZapMode><FEMode><Energy config="machines.wind_mill_min_output" rate="true" /></FEMode>** near sea level to **<ZapMode>6.5 z/t</ZapMode><FEMode><Energy config="machines.wind_mill_max_output" rate="true" /></FEMode>** at build-limit altitudes.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="wind_mill" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Placement Rules</Color>
</Column>

* Minimum effective **Y=64**. Any lower and it produces nothing.
* Output scales linearly from Y=64 to **Y=319**.
* Needs clear space around the blades. Walls block airflow.
* **Rain** multiplies output by 1.2×.
* **Thunderstorms** multiply output by 1.5×.

<ItemImage id="minecraft:air" scale="0.25"/>
***

Useful as a passive top-up and as a reason to build tall. A pillar of 10 to 20 wind mills on a mountain is a steady early-MV power source.
