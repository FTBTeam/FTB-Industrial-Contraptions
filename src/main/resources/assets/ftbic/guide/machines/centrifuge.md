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

  Separates materials into their components. Extracts <ItemLink id="silicon" /> from quartz or sand, breaks down gravel, flint, and slime drops, and — if **Myrtrees** is installed — processes latex from rubber trees into bulk <ItemLink id="rubber" />.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="extractor" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Notable Separating Recipes</Color>
</Column>

* Quartz or sand → <ItemLink id="silicon" />
* Gravel → flint
* Glowstone → glowstone dust
* Magma Cream → slime ball (+ a chance of blaze powder)
* Small Coolant Cell from blaze rods and ice
* **Myrtrees latex → <ItemLink id="rubber" /> (3 per latex), if the mod is loaded**

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="advanced_centrifuge" />
  ### <Color id="aqua">Advanced Centrifuge</Color>
</Row>

HV tier. Same recipes, faster, 16 zap/t, 10,000 zap buffer.

<RecipeFor id="advanced_centrifuge" />
