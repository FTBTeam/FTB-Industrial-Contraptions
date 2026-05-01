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
  - ftbic:steel_ingot
  - ftbic:bronze_ingot
  - ftbic:electrum_ingot
  - ftbic:invar_ingot
  - ftbic:constantan_ingot
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

  Composite metals combine multiple ingots into new high-tier materials. The <ItemLink id="alloy_smelter" /> is the workhorse — every metallic alloy goes through it.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Base Alloys</Color>
</Column>

Made in the <ItemLink id="alloy_smelter" /> from raw ingots. Each can be re-macerated to dust and re-smelted for recovery.

<Row>
  <ItemImage id="bronze_ingot" />
  ### <Color id="aqua">Bronze</Color>
</Row>

3 copper + 1 tin → 4 bronze. Mid-tier alloy used in the advanced alloy bronze pathway.

<RecipeFor id="bronze_ingot" />

<Row>
  <ItemImage id="electrum_ingot" />
  ### <Color id="aqua">Electrum</Color>
</Row>

1 silver + 1 gold → 2 electrum. Used in the advanced alloy electrum pathway and in conductive alloy chains.

<RecipeFor id="electrum_ingot" />

<Row>
  <ItemImage id="invar_ingot" />
  ### <Color id="aqua">Invar</Color>
</Row>

2 iron + 1 nickel → 3 invar. Used in the advanced alloy invar pathway and as a heat-resistant intermediate.

<RecipeFor id="invar_ingot" />

<Row>
  <ItemImage id="constantan_ingot" />
  ### <Color id="aqua">Constantan</Color>
</Row>

1 copper + 1 nickel → 2 constantan. General-purpose intermediate alloy.

<RecipeFor id="constantan_ingot" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Steel — The Advanced Alloy Gateway</Color>
</Column>

<Row>
  <ItemImage id="steel_ingot" />
  ### <Color id="aqua">Steel Ingot</Color>
</Row>

The most important alloy in the chain — **every advanced alloy recipe begins with steel.** Made in the <ItemLink id="alloy_smelter" /> by alloying <ItemLink id="industrial_grade_metal" /> with carbon:

* 1 industrial grade metal + 1 coal → 1 steel ingot
* 1 industrial grade metal + 1 charcoal → 1 steel ingot

<RecipesFor id="steel_ingot" />

Stockpile steel before tackling the <ItemLink id="advanced_machine_block" />, <ItemLink id="reactor_plating" />, or any HV+ machine, since each requires advanced alloy as a downstream ingredient.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Mixed Metal Blend & Industrial Grade Metal</Color>
</Column>

<Row>
  <ItemImage id="mixed_metal_blend" />
  ### <Color id="aqua">Mixed Metal Blend</Color>
</Row>

Recovered by macerating <ItemLink id="advanced_alloy" /> back into a blend that can be re-smelted into advanced alloy. There is no shaped craft for blend — the alloy smelter recipes skip this stage entirely.

<RecipesFor id="mixed_metal_blend" />

<Row>
  <ItemImage id="industrial_grade_metal" />
  ### <Color id="aqua">Industrial Grade Metal</Color>
</Row>

Compressed iron — three iron ingots crushed into one. Feeds the <ItemLink id="machine_block" /> and is the primary ingredient for steel.

<RecipeFor id="industrial_grade_metal" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Advanced Alloy</Color>
</Column>

<Row>
  <ItemImage id="advanced_alloy" />
  ### <Color id="aqua">Advanced Alloy</Color>
</Row>

The cornerstone of every late-game machine, armor piece, reactor component, and the <ItemLink id="iridium_alloy" />. Made in the <ItemLink id="alloy_smelter" /> through one of three steel-based recipes:

* 1 steel + 2 bronze + 1 aluminum → 1 advanced alloy
* 1 steel + 1 electrum + 1 aluminum → 1 advanced alloy
* 1 steel + 2 invar + 1 aluminum → 1 advanced alloy

Pick whichever path matches your available alloys — bronze is the cheapest if you have copper/tin spare, invar takes more raw iron, electrum is the most efficient ingot count if you have silver and gold to burn.

<RecipesFor id="advanced_alloy" />

<Row>
  <ItemImage id="iridium_alloy" />
  ### <Color id="aqua">Iridium Alloy</Color>
</Row>

Iridium plates reinforced with <ItemLink id="advanced_alloy" />. Final-tier alloy — required for <ItemLink id="iridium_circuit" />, <ItemLink id="iridium_neutron_reflector" />, and <ItemLink id="quantum_helmet" />.

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

Crafted shaped from 3 lead dust + 1 diamond dust + 2 ender pearls. A recovery path for enderium ingots macerated back to dust.

<RecipeFor id="enderium_dust" />

<Row>
  <ItemImage id="enderium_ingot" />
  ### <Color id="aqua">Enderium Ingot</Color>
</Row>

Made exclusively in the <ItemLink id="alloy_smelter" />: 3 lead + 1 diamond dust + 2 ender pearls → 2 enderium ingots. There is no shaped-craft path.

<RecipeFor id="enderium_ingot" />

<Row>
  <ItemImage id="enderium_block" />
  ### <Color id="aqua">Block of Enderium</Color>
</Row>

9 ingots → block. Bulk storage.

<RecipeFor id="enderium_block" />

Enderium comes in every standard shape: <ItemLink id="enderium_nugget" />, <ItemLink id="enderium_plate" />, <ItemLink id="enderium_rod" />, <ItemLink id="enderium_gear" />, <ItemLink id="enderium_wire" />.
