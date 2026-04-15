---
navigation:
  title: Macerator
  icon: ftbic:macerator
  parent: machines/index.md
  position: 3
item_ids:
  - ftbic:macerator
  - ftbic:advanced_macerator
---

# <Color id="gold">Macerator</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="macerator" scale="2" />

  The ore-doubling cornerstone. Grinds ores into **2 dusts** each, ingots into dust, and cobblestone into sand.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="macerator" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Why It Matters</Color>
</Column>

* Raw ore blocks → **2 dust** (smelt in any furnace for double ingot yield).
* Ingots → 1 dust (for recycling or chain processing).
* Cobblestone → sand, gravel → flint, etc.

<RecipesFor id="coal_dust" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="advanced_macerator" />
  ### <Color id="aqua">Advanced Macerator</Color>
</Row>

HV version: 10,000 zap buffer, 16 zap/t, much faster recipe ticks.

<RecipeFor id="advanced_macerator" />
