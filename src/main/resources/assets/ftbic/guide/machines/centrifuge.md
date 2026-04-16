---
navigation:
  title: Centrifuge
  icon: ftbic:centrifuge
  parent: machines/index.md
  position: 7
item_ids:
  - ftbic:centrifuge
  - ftbic:advanced_centrifuge
---

# <Color id="gold">Centrifuge</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="centrifuge" scale="2" />

  Separates materials into their components. Extracts <ItemLink id="ftbmaterials:silicon_gem" /> from quartz or sand, breaks down gravel and flint, and handles slime/magma processing.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="extractor" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Notable Separating Recipes</Color>
</Column>

* Quartz or sand → <ItemLink id="ftbmaterials:silicon_gem" />
* Gravel → flint
* Glowstone → glowstone dust
* Magma Cream → slime ball (+ a chance of blaze powder)
* Small Coolant Cell from blaze rods and ice

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="advanced_centrifuge" />
  ### <Color id="aqua">Advanced Centrifuge</Color>
</Row>

HV tier. Same recipes, faster, 16 zap/t, 10,000 zap buffer.

<RecipeFor id="advanced_centrifuge" />
