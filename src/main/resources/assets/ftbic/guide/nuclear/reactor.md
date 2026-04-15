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

Attach a chamber to any face of the reactor to unlock a new column of 3 component slots (up to a maximum of 6 chambers → 9 columns → 27 total slots). Chambers also forward piped-in **power** and **items** through to the central reactor — plug a cable or hopper into a chamber face and it reaches the reactor's network and inventory exactly as if you'd attached it to the reactor directly.

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

The reactor samples every outward-facing face of its hull and attached chambers once per cycle. For each face touching a **water** block, the reactor-hull cooling of every <ItemLink id="heat_vent" /> / <ItemLink id="reactor_heat_vent" /> / <ItemLink id="overclocked_heat_vent" /> / <ItemLink id="advanced_heat_vent" /> inside gets a proportional boost:

* 0 water-adjacent faces → **1×** cooling (baseline)
* Fully submerged → **2×** cooling

Cheap trick: drop the reactor into a shallow pool and seal the top with glass — every `reactorCool` value doubles for free. Flowing or still water both count; lava and air don't.

<ItemImage id="minecraft:air" scale="0.25"/>

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Containment</Color>
</Column>

Reactors always explode when heat hits 100%. Wall the reactor with blast-resistant blocks (<ItemLink id="reinforced_stone" /> or <ItemLink id="reinforced_glass" />) before pushing aggressive designs — the explosion scales from **10 blocks base radius** to a hard cap of **80 blocks**, enough to reach bedrock.

See [Fuel Rods](fuel_rods.md), [Cooling](cooling.md), [Reflectors](reflectors.md), and [Plating](plating.md) for the pieces that go in the slots.
