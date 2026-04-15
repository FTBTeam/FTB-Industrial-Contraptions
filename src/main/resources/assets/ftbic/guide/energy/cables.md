---
navigation:
  title: Cables
  icon: ftbic:mv_cable
  parent: energy/index.md
  position: 1
item_ids:
  - ftbic:lv_cable
  - ftbic:mv_cable
  - ftbic:hv_cable
  - ftbic:ev_cable
  - ftbic:iv_cable
  - ftbic:burnt_cable
---

# <Color id="gold">Cables</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="hv_cable" scale="2" />

  Cables connect producers, consumers, and batteries into a single network. Energy travels instantly along a connected chain up to the configured **max cable length** (default 300).
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="lv_cable" />
  ### <Color id="aqua">LV Cable — 32 zaps/t</Color>
</Row>

Rubber-coated copper. The default cable for basic generators, the macerator, and the powered furnace.

<RecipeFor id="lv_cable" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="mv_cable" />
  ### <Color id="aqua">MV Cable — 128 zaps/t</Color>
</Row>

Insulated gold wire. Used between MV transformers and mid-tier machines.

<RecipeFor id="mv_cable" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="hv_cable" />
  ### <Color id="aqua">HV Cable — 512 zaps/t</Color>
</Row>

Insulated iron wire. Necessary for advanced machines and the first stage out of a nuclear reactor.

<RecipeFor id="hv_cable" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="ev_cable" />
  ### <Color id="aqua">EV Cable — 2,048 zaps/t</Color>
</Row>

Insulated iridium wire. Feeds the Teleporter, Antimatter Constructor, and Quantum gear charge pads.

<RecipeFor id="ev_cable" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="iv_cable" />
  ### <Color id="aqua">IV Cable — 8,192 zaps/t</Color>
</Row>

The highest transfer rate in the mod. Reserved for maxed-out reactor designs and the endgame antimatter chain.

<RecipeFor id="iv_cable" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="burnt_cable" />
  ### <Color id="aqua">Burnt Cable</Color>
</Row>

What a cable becomes if it receives too much voltage. Break it, replace it with the correct tier, and grind the burnt remains into <ItemLink id="scrap" />.

<RecipeFor id="scrap_from_burnt_cable" fallbackText="Drop a Burnt Cable in any crafting grid for scrap." />
