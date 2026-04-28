---
navigation:
  title: Neutron Reflectors
  icon: ftbic:thick_neutron_reflector
  parent: nuclear/index.md
  position: 5
item_ids:
  - ftbic:neutron_reflector
  - ftbic:thick_neutron_reflector
  - ftbic:iridium_neutron_reflector
---

# <Color id="gold">Neutron Reflectors</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="iridium_neutron_reflector" scale="2" />

  Reflectors count as a **valid rod neighbor** for output calculations — a rod next to a reflector behaves as if it had another rod there. Each reflector has a finite lifetime except the iridium one.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="neutron_reflector" />
  ### <Color id="aqua">Neutron Reflector</Color>
</Row>

Cheap, single-use. Burns out over time.

<RecipeFor id="neutron_reflector" />

<Row>
  <ItemImage id="thick_neutron_reflector" />
  ### <Color id="aqua">Thick Neutron Reflector</Color>
</Row>

Longer lifespan. Standard in mid-game designs.

<RecipeFor id="thick_neutron_reflector" />

<Row>
  <ItemImage id="iridium_neutron_reflector" />
  ### <Color id="aqua">Iridium Neutron Reflector</Color>
</Row>

Infinite lifespan. Expensive, but the default in every permanent design.

<RecipeFor id="iridium_neutron_reflector" />
