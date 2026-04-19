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

  A height-dependent generator that scales from **0.3 zap/t** near sea level to **6.5 zap/t** at build-limit altitudes.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="wind_mill" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Placement Rules</Color>
</Column>

* Minimum effective **Y=64** — any lower and it produces nothing.
* Output scales linearly from Y=64 to **Y=319**.
* Needs clear space around the blades — walls block airflow.
* **Rain** multiplies output by 1.2×.
* **Thunderstorms** multiply output by 1.5×.

<ItemImage id="minecraft:air" scale="0.25"/>
***

Useful as a passive top-up and as a reason to build tall. A pillar of 10–20 wind mills on a mountain is a quiet early-MV power source.
