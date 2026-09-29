---
navigation:
  title: Geothermal Generator
  icon: ftbic:geothermal_generator
  parent: generators/index.md
  position: 2
item_ids:
  - ftbic:geothermal_generator
---

# <Color id="gold">Geothermal Generator</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="geothermal_generator" scale="2" />

  Consumes **lava** from an internal 8,000 mB tank to produce **<Energy config="machines.geothermal_generator_output" rate="true" />**. Twice the output of a Basic Generator, and entirely renewable if you have a nether portal tap or a lava lake nearby.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="geothermal_generator" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Feeding Lava</Color>
</Column>

* Right-click the block with a lava bucket to fill 1,000 mB.
* Or pipe lava into it from a <ItemLink id="pump" /> or any fluid-capable cable / pipe.
* Burns 1 mB of lava per tick while its buffer has room, so one bucket gives <Energy config="machines.geothermal_generator_output" times="1000" />.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Stats</Color>
</Column>

* **Tier:** LV
* **Internal buffer:** <Energy config="machines.geothermal_generator_capacity" />
* **Tank:** 8,000 mB
* **Output:** <Energy config="machines.geothermal_generator_output" rate="true" />
