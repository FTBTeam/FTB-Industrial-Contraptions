---
navigation:
  title: Alloys
  icon: ftbic:iridium_alloy
  parent: materials/index.md
  position: 2
item_ids:
  - ftbic:mixed_metal_blend
  - ftbic:advanced_alloy
  - ftbic:industrial_grade_metal
  - ftbic:iridium_alloy
  - ftbic:machine_block
  - ftbic:advanced_machine_block
  - ftbic:enderium_ingot
  - ftbic:enderium_nugget
  - ftbic:enderium_plate
  - ftbic:enderium_rod
  - ftbic:enderium_gear
  - ftbic:enderium_wire
  - ftbic:enderium_block
---

# <Color id="gold">Alloys</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="iridium_alloy" scale="2" />

  Composite metals combine multiple ingots into new high-tier materials.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="mixed_metal_blend" />
  ### <Color id="aqua">Mixed Metal Blend</Color>
</Row>

Iron + copper + tin plates. The raw ingredient for <ItemLink id="industrial_grade_metal" />.

<RecipesFor id="mixed_metal_blend" />

<Row>
  <ItemImage id="industrial_grade_metal" />
  ### <Color id="aqua">Industrial Grade Metal</Color>
</Row>

Compressed <ItemLink id="mixed_metal_blend" />. The core crafting material for HV machines and the <ItemLink id="advanced_machine_block" />.

<RecipeFor id="industrial_grade_metal" />

<Row>
  <ItemImage id="advanced_alloy" />
  ### <Color id="aqua">Advanced Alloy</Color>
</Row>

<RecipeFor id="carbon_fiber_mesh" fallbackText="See crafting recipes." />

Used in armor, reinforced machine blocks, and the <ItemLink id="nuclear_reactor" />.

<Row>
  <ItemImage id="iridium_alloy" />
  ### <Color id="aqua">Iridium Alloy</Color>
</Row>

Iridium plates reinforced with <ItemLink id="advanced_alloy" />. Final tier — required for <ItemLink id="iridium_circuit" />, <ItemLink id="iridium_neutron_reflector" />, and <ItemLink id="quantum_helmet" />.

<RecipeFor id="iridium_alloy" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Enderium</Color>
</Column>

Purple-blue magical alloy — used in the <ItemLink id="teleporter" />, <ItemLink id="iv_cable" />, and advanced circuits.

<Row>
  <ItemImage id="enderium_dust" />
  ### <Color id="aqua">Enderium Dust</Color>
</Row>

<RecipeFor id="enderium_dust" />

<Row>
  <ItemImage id="enderium_ingot" />
  ### <Color id="aqua">Enderium Ingot</Color>
</Row>

<RecipeFor id="enderium_ingot" />

<Row>
  <ItemImage id="enderium_block" />
  ### <Color id="aqua">Block of Enderium</Color>
</Row>

9 ingots → block. Bulk storage.

<RecipeFor id="enderium_block" />

Enderium comes in every standard shape: <ItemLink id="enderium_nugget" />, <ItemLink id="enderium_plate" />, <ItemLink id="enderium_rod" />, <ItemLink id="enderium_gear" />, <ItemLink id="enderium_wire" />.

<RecipeFor id="enderium_wire" />
