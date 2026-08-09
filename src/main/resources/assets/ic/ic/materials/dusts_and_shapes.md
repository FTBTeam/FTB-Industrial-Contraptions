---
navigation:
  title: Dusts, Plates, Rods
  icon: ic:dense_copper_plate
  parent: materials/index.md
  position: 1
item_ids:
  - ic:coal_dust
  - ic:charcoal_dust
  - ic:diamond_dust
  - ic:obsidian_dust
  - ic:dense_copper_plate
  - ic:copper_coil
  - ic:coal_ball
  - ic:compressed_coal_ball
  - ic:graphene
---

# <Color id="gold">Dusts, Plates & Shapes</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="dense_copper_plate" scale="2" />

  Intermediate shapes that feed almost every recipe. Dusts come from the <ItemLink id="macerator" />; plates from the <ItemLink id="roller" /> or <ItemLink id="extruder" />; rods and gears from the extruder chain.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Specialty Dusts</Color>
</Column>

<Row>
  <ItemImage id="ic:coal_dust" />
  ### <Color id="aqua">Coal Dust</Color>
</Row>

Ground coal. Crafting input for carbon fibers and coal balls.

<RecipesFor id="ic:coal_dust" />

<Row>
  <ItemImage id="ic:charcoal_dust" />
  ### <Color id="aqua">Charcoal Dust</Color>
</Row>

<Row>
  <ItemImage id="ic:obsidian_dust" />
  ### <Color id="aqua">Obsidian Dust</Color>
</Row>

<Row>
  <ItemImage id="ic:diamond_dust" />
  ### <Color id="aqua">Diamond Dust</Color>
</Row>

Grind a diamond in a macerator, compress two dusts back into a diamond — a free +100% when you want diamonds from recycled gear.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Specialty Plates & Shapes</Color>
</Column>

<Row>
  <ItemImage id="dense_copper_plate" />
  ### <Color id="aqua">Dense Copper Plate</Color>
</Row>

9 copper plates compressed into one. Used by the <ItemLink id="nuclear_reactor" /> recipe and advanced components.

<Row>
  <ItemImage id="copper_coil" />
  ### <Color id="aqua">Copper Coil</Color>
</Row>

Copper wire wrapped around an iron rod. Used in generators and transformers.

<RecipeFor id="copper_coil" />

<Row>
  <ItemImage id="coal_ball" />
  ### <Color id="aqua">Coal Ball</Color>
</Row>

4 coal dust + 4 flint → coal ball. A rung on the diamond recycling ladder.

<RecipeFor id="coal_ball" />

<Row>
  <ItemImage id="compressed_coal_ball" />
  ### <Color id="aqua">Compressed Coal Ball</Color>
</Row>

Compress 8 coal balls. Feeds further into diamond recipes.

<Row>
  <ItemImage id="graphene" />
  ### <Color id="aqua">Graphene</Color>
</Row>

Extruded from carbon and silicon. Used in endgame circuits.

<RecipeFor id="graphene" />
