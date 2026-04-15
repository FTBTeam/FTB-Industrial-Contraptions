---
navigation:
  title: Antimatter Constructor
  icon: ftbic:antimatter_constructor
  parent: machines/index.md
  position: 13
item_ids:
  - ftbic:antimatter_constructor
  - ftbic:antimatter
  - ftbic:antimatter_crystal
---

# <Color id="gold">Antimatter Constructor</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="antimatter_constructor" scale="2" />

  The endgame machine. Fabricates <ItemLink id="antimatter" /> and <ItemLink id="antimatter_crystal" /> — the only materials used in <ItemLink id="quantum_helmet" /> and the <ItemLink id="iv_rectifier" /> line.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="antimatter_fabricator" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Operation</Color>
</Column>

* **Tier:** EV
* **Buffer:** 1,000,000 zaps
* Slowly accumulates antimatter fuel from raw energy. Requires a sustained EV feed — plan for a nuclear reactor or a wall of <ItemLink id="ev_solar_panel" />s.
* Accepts "boost" items (see `antimatter_boost` recipes) that temporarily multiply output.

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="antimatter_crystal" />
  ### <Color id="aqua">Antimatter Crystal</Color>
</Row>

Compressed antimatter — the gem form, used directly in late-game recipes.

<RecipeFor id="antimatter_crystal" />
