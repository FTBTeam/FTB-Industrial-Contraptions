---
navigation:
  title: Alloy Smelter
  icon: ftbic:alloy_smelter
  parent: machines/index.md
  position: 14
item_ids:
  - ftbic:alloy_smelter
---

# <Color id="gold">Alloy Smelter</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="alloy_smelter" scale="2" />

  Three-input alloying machine. Combines ingots into composite metals — bronze, electrum, invar, constantan, steel, enderium, netherite, advanced alloy — without the dust-and-shape detour.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="alloy_smelter" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Slots & The Unique-Input Rule</Color>
</Column>

The Alloy Smelter has **three input slots** and **one output slot**. Each input slot holds one ingredient at any size; counts on the recipe are matched against the slot's stack count, not split across slots.

The slots enforce a **unique-item constraint** — once an item type lands in one slot, the other two reject the same item. This prevents accidentally splitting a stack across slots and stranding a recipe that needed `2 gold` in one place. Put 4 gold ingots in slot 1, not 2 in slot 1 and 2 in slot 2.

When inputs could match more than one recipe, the smelter prefers the recipe with the **most ingredients** — fill all three slots and you'll always get the 3-input alloy over a 2-input subset.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Alloy Recipes</Color>
</Column>

<Row>
  <ItemImage id="bronze_ingot" />
  ### <Color id="aqua">Bronze</Color>
</Row>

3 copper + 1 tin → 4 bronze ingot.

<Row>
  <ItemImage id="electrum_ingot" />
  ### <Color id="aqua">Electrum</Color>
</Row>

1 silver + 1 gold → 2 electrum ingot.

<Row>
  <ItemImage id="invar_ingot" />
  ### <Color id="aqua">Invar</Color>
</Row>

2 iron + 1 nickel → 3 invar ingot.

<Row>
  <ItemImage id="constantan_ingot" />
  ### <Color id="aqua">Constantan</Color>
</Row>

1 copper + 1 nickel → 2 constantan ingot.

<Row>
  <ItemImage id="steel_ingot" />
  ### <Color id="aqua">Steel</Color>
</Row>

1 <ItemLink id="industrial_grade_metal" /> + 1 coal **or** 1 charcoal → 1 steel ingot. Steel is the gateway material for advanced alloy — see the <ItemLink id="advanced_alloy" /> recipes below.

<Row>
  <ItemImage id="minecraft:netherite_ingot" />
  ### <Color id="aqua">Netherite</Color>
</Row>

2 gold + 2 netherite scrap → 1 netherite ingot. Skips the vanilla diamond+netherite-ingot smithing step entirely.

<Row>
  <ItemImage id="enderium_ingot" />
  ### <Color id="aqua">Enderium</Color>
</Row>

3 lead + 1 diamond dust + 2 ender pearls → 2 enderium ingots. The only way to make enderium ingots — there is no shaped-craft recipe.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Advanced Alloy</Color>
</Column>

The Alloy Smelter produces <ItemLink id="advanced_alloy" /> through three steel-based recipes. **Steel is required for every path** — produce it first by alloying industrial grade metal with coal or charcoal.

* 1 steel + 2 bronze + 1 aluminum → 1 advanced alloy
* 1 steel + 1 electrum + 1 aluminum → 1 advanced alloy
* 1 steel + 2 invar + 1 aluminum → 1 advanced alloy

<RecipesFor id="advanced_alloy" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Stats</Color>
</Column>

* **Tier:** MV (Medium Voltage input)
* **Energy buffer:** 10,000 zaps
* **Energy use:** 48 zap/t — three times the <ItemLink id="advanced_powered_furnace" />
* **Default cycle:** 200 ticks per recipe (modifiable with <ItemLink id="overclocker_upgrade" />)

<ItemImage id="minecraft:air" scale="0.25"/>
