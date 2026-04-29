---
navigation:
  title: Cooling Components
  icon: ftbic:heat_vent
  parent: nuclear/index.md
  position: 3
item_ids:
  - ftbic:small_coolant_cell
  - ftbic:medium_coolant_cell
  - ftbic:large_coolant_cell
  - ftbic:heat_vent
  - ftbic:reactor_heat_vent
  - ftbic:overclocked_heat_vent
  - ftbic:component_heat_vent
  - ftbic:advanced_heat_vent
  - ftbic:heat_exchanger
  - ftbic:reactor_heat_exchanger
  - ftbic:component_heat_exchanger
  - ftbic:advanced_heat_exchanger
---

# <Color id="gold">Cooling Components</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="heat_vent" scale="2" />

  Two kinds of cooling: **cells** absorb heat into an internal buffer until they saturate, and **vents / exchangers** continuously move heat from rods → vents → air.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Coolant Cells</Color>
</Column>

Store heat in a finite buffer. When a cell saturates, it stops absorbing — designs that rely solely on cells are **pulse** designs with planned downtime.

<Row>
  <ItemImage id="small_coolant_cell" />
  ### <Color id="aqua">Small Coolant Cell</Color>
</Row>

<RecipeFor id="small_coolant_cell" />

<Row>
  <ItemImage id="medium_coolant_cell" />
  ### <Color id="aqua">Medium Coolant Cell</Color>
</Row>

<RecipeFor id="medium_coolant_cell" />

<Row>
  <ItemImage id="large_coolant_cell" />
  ### <Color id="aqua">Large Coolant Cell</Color>
</Row>

<RecipeFor id="large_coolant_cell" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Heat Vents</Color>
</Column>

Vents dissipate heat from the reactor hull or from neighboring components. Required for **continuous** designs.

<Row>
  <ItemImage id="heat_vent" />
  ### <Color id="aqua">Heat Vent</Color>
</Row>

Base unit. Pulls heat from the reactor hull and vents it to air.

<RecipeFor id="heat_vent" />

<Row>
  <ItemImage id="reactor_heat_vent" />
  ### <Color id="aqua">Reactor Heat Vent</Color>
</Row>

Stronger hull-dissipation vent. Pulls more heat per tick from the reactor chamber itself.

<RecipeFor id="reactor_heat_vent" />

<Row>
  <ItemImage id="component_heat_vent" />
  ### <Color id="aqua">Component Heat Vent</Color>
</Row>

Pulls heat from adjacent **components** (coolant cells) rather than the hull. The keystone of cell-recycling designs.

<RecipeFor id="component_heat_vent" />

<Row>
  <ItemImage id="overclocked_heat_vent" />
  ### <Color id="aqua">Overclocked Heat Vent</Color>
</Row>

Hybrid vent — dissipates to both hull and neighbors. Self-damaging under heavy load but potent when placed correctly.

<RecipeFor id="overclocked_heat_vent" />

<Row>
  <ItemImage id="advanced_heat_vent" />
  ### <Color id="aqua">Advanced Heat Vent</Color>
</Row>

The endgame vent. Used in IV-class designs.

<RecipeFor id="advanced_heat_vent" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Heat Exchangers</Color>
</Column>

Exchangers **equalize** heat — they move heat from hotter neighbors to cooler ones, or from the reactor hull into a component. Without exchangers, heat stays concentrated around fuel rods.

<Row>
  <ItemImage id="heat_exchanger" />
  ### <Color id="aqua">Heat Exchanger</Color>
</Row>

<RecipeFor id="heat_exchanger" />

<Row>
  <ItemImage id="reactor_heat_exchanger" />
  ### <Color id="aqua">Reactor Heat Exchanger</Color>
</Row>

Exchanges heat specifically with the reactor hull — pulls hull heat into the component grid to be dissipated by vents.

<RecipeFor id="reactor_heat_exchanger" />

<Row>
  <ItemImage id="component_heat_exchanger" />
  ### <Color id="aqua">Component Heat Exchanger</Color>
</Row>

Only balances heat among adjacent components. Cheap.

<RecipeFor id="component_heat_exchanger" />

<Row>
  <ItemImage id="advanced_heat_exchanger" />
  ### <Color id="aqua">Advanced Heat Exchanger</Color>
</Row>

Endgame exchanger.

<RecipeFor id="advanced_heat_exchanger" />
