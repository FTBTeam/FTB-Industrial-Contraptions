---
navigation:
  title: Pump
  icon: ftbic:pump
  parent: machines/index.md
  position: 11
item_ids:
  - ftbic:pump
---

# <Color id="gold">Pump</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="pump" scale="2" />

  A self-extending fluid miner. Drops a pipe straight down, drains any pool of lava, water, or mod fluid it finds, and stores the result in its internal 128-bucket tank.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="pump" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Operation</Color>
</Column>

* Place on top of a block. The pump extends a pipe downward through air and replaceable blocks until it hits fluid.
* 40 ticks to mine a fluid block, 10 ticks to move the pipe down one step.
* By default, drained fluids are replaced with <ItemLink id="exfluid" /> to prevent infinite-source exploits. Disable in config if you prefer hollow holes.
* Pipe connected machines or <ItemLink id="fluid_cell" />s to the side to export the fluid.

**Stats:** HV tier, 10,000 zap buffer, 3 zap/t use.
