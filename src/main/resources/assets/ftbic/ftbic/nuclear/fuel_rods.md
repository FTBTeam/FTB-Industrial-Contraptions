---
navigation:
  title: Fuel Rods
  icon: ftbic:uranium_fuel_rod
  parent: nuclear/index.md
  position: 2
item_ids:
  - ftbic:uranium_fuel_rod
  - ftbic:dual_uranium_fuel_rod
  - ftbic:quad_uranium_fuel_rod
---

# <Color id="gold">Fuel Rods</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="quad_uranium_fuel_rod" scale="2" />

  Fuel rods produce **heat** and **output**. Doubling up rods (dual, quad) exponentially increases both — at the cost of much heavier cooling requirements.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="uranium_fuel_rod" />
  ### <Color id="aqua">Uranium Fuel Rod</Color>
</Row>

Fabricated in a <ItemLink id="canning_machine" /> from depleted uranium. Base unit — 1× output, 1× heat.

<RecipeFor id="uranium_fuel_rod" />

<Row>
  <ItemImage id="dual_uranium_fuel_rod" />
  ### <Color id="aqua">Dual Uranium Fuel Rod</Color>
</Row>

Two rods bonded together. 4× output, 2× heat — efficient per cell, but twice the cooling load.

<RecipeFor id="dual_uranium_fuel_rod" />

<Row>
  <ItemImage id="quad_uranium_fuel_rod" />
  ### <Color id="aqua">Quad Uranium Fuel Rod</Color>
</Row>

The core of high-output designs. 10× output, 4× heat. Needs careful placement next to heavy cooling.

<RecipeFor id="quad_uranium_fuel_rod" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Placement Rule</Color>
</Column>

A rod emits heat/output for each of its **valid neighbors** (rod, reflector, or heat-dissipating component). Empty slots count as nothing. Design rule of thumb:

* A single rod with 4 reflectors produces as much as a quad rod with 2 reflectors — start there.
* Scale up to dual/quad rods only after your cooling scales.
