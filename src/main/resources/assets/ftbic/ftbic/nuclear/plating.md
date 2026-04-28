---
navigation:
  title: Reactor Plating
  icon: ftbic:reactor_plating
  parent: nuclear/index.md
  position: 4
item_ids:
  - ftbic:reactor_plating
  - ftbic:heat_capacity_reactor_plating
  - ftbic:containment_reactor_plating
---

# <Color id="gold">Reactor Plating</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="reactor_plating" scale="2" />

  Plating modifies the **reactor hull** itself — its total heat capacity and its explosion radius if the worst happens.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="reactor_plating" />
  ### <Color id="aqua">Reactor Plating</Color>
</Row>

Reduces explosion damage if the reactor melts down.

<RecipeFor id="reactor_plating" />

<Row>
  <ItemImage id="heat_capacity_reactor_plating" />
  ### <Color id="aqua">Heat-Capacity Reactor Plating</Color>
</Row>

Increases the reactor's total heat buffer — slower to reach critical, easier to run pulsed designs.

<RecipeFor id="heat_capacity_reactor_plating" />

<Row>
  <ItemImage id="containment_reactor_plating" />
  ### <Color id="aqua">Containment Reactor Plating</Color>
</Row>

Strongly dampens explosion output if containment fails. The safety net for aggressive designs.

<RecipeFor id="containment_reactor_plating" />
