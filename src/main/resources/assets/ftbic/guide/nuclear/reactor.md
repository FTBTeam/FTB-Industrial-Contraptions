---
navigation:
  title: Nuclear Reactor
  icon: ftbic:nuclear_reactor
  parent: nuclear/index.md
  position: 1
item_ids:
  - ftbic:nuclear_reactor
  - ftbic:nuclear_reactor_chamber
---

# <Color id="gold">Nuclear Reactor</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="nuclear_reactor" scale="2" />

  The reactor core. A 3×3 interior grid of component slots, expandable with up to **6 reactor chambers** (one on each face) for a total of 9 + 6×3 = 27 slots.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="nuclear_reactor" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Reactor Chamber</Color>
</Column>

Attach a chamber to any face of the reactor to add 3 extra slots. Chambers also provide extra surface for heat dissipation to air, water, or lava — and look very menacing.

<RecipeFor id="nuclear_reactor_chamber" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Operation</Color>
</Column>

* **Tier:** output is EV by default; maximal designs can push into IV territory.
* **Internal buffer:** 50,000 zaps.
* Open the GUI and drop components into the grid — fuel rods, heat vents, coolant cells, reflectors, plating.
* Toggle the reactor with redstone. Without a signal it pauses — letting you cool between pulses.
* Jade / Top shows live heat % and output.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Environmental Cooling</Color>
</Column>

The reactor casing dissipates heat to its surroundings — **water** on all six sides roughly doubles a chamber's passive cooling. Encase the reactor in source blocks (using <ItemLink id="reinforced_stone" /> or <ItemLink id="reinforced_glass" /> walls) before you first push it past low-power designs.

See [Fuel Rods](fuel_rods.md), [Cooling](cooling.md), [Reflectors](reflectors.md), and [Plating](plating.md) for the pieces that go in the slots.
