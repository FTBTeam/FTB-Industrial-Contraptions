---
navigation:
  title: Dusts, Plates, Rods
  icon: ftbic:dense_copper_plate
  parent: materials/index.md
  position: 1
item_ids:
  - ftbic:coal_dust
  - ftbic:charcoal_dust
  - ftbic:diamond_dust
  - ftbic:obsidian_dust
  - ftbic:enderium_dust
  - ftbic:dense_copper_plate
  - ftbic:copper_coil
  - ftbic:coal_ball
  - ftbic:compressed_coal_ball
  - ftbic:graphene
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
  <ItemImage id="ftbic:coal_dust" />
  ### <Color id="aqua">Coal Dust</Color>
</Row>

Ground coal. Crafting input for carbon fibers and coal balls.

<RecipesFor id="ftbic:coal_dust" />

<Row>
  <ItemImage id="ftbic:charcoal_dust" />
  ### <Color id="aqua">Charcoal Dust</Color>
</Row>

<Row>
  <ItemImage id="ftbic:obsidian_dust" />
  ### <Color id="aqua">Obsidian Dust</Color>
</Row>

<Row>
  <ItemImage id="ftbic:diamond_dust" />
  ### <Color id="aqua">Diamond Dust</Color>
</Row>

Grind a diamond in a macerator, compress two dusts back into a diamond — a free +100% when you want diamonds from recycled gear.

<Row>
  <ItemImage id="enderium_dust" />
  ### <Color id="aqua">Enderium Dust</Color>
</Row>

Crafting input for <ItemLink id="enderium_ingot" />.

<RecipeFor id="enderium_dust" />

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
