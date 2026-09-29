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

  Starts the [ore refining chain](ore_refining.md): supported ore blocks become **3 raw ore**, each raw ore becomes **2 crushed ore**, and each block of raw ore becomes **18 crushed ore**. It also grinds ingots into dust and cobblestone into sand.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="macerator" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Why It Matters</Color>
</Column>

* Supported ore blocks yield **3 raw ore**, then **6 crushed ore**. Wash and centrifuge for **15 ingots**.
* Ingots → 1 dust (for recycling or chain processing).
* Cobblestone → sand, gravel → flint, etc.

<RecipesFor id="ftbic:coal_dust" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="advanced_macerator" />
  ### <Color id="aqua">Advanced Macerator</Color>
</Row>

MV version: <Energy config="machines.advanced_macerator_capacity" /> buffer, <Energy config="machines.advanced_macerator_use" rate="true" />. Same recipe speed, and it accepts <ItemLink id="parallel_processing_upgrade" />s.

<RecipeFor id="advanced_macerator" />
