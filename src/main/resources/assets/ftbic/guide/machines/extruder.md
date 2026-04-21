---
navigation:
  title: Extruder
  icon: ftbic:extruder
  parent: machines/index.md
  position: 5
item_ids:
  - ftbic:extruder
---

# <Color id="gold">Extruder</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="extruder" scale="2" />

  Forms metal into **ingots**, **plates**, and **rods**. The source of the bulk ingot/plate/rod pipeline that feeds crafting.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="extruder" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Recipe Categories</Color>
</Column>

The Extruder has three recipe output modes, selected per-recipe:

* **Ingot** — 2 dusts → 1 ingot
* **Plate** — 1 ingot → 1 plate
* **Rod** — 1 ingot → 2 rods

Every metal registered in FTB Materials has all three recipes unless disabled in config. Plates and rods used by crafting (gears, wire, machine components) almost always come from here.

<ItemImage id="minecraft:air" scale="0.25"/>

**Stats:** LV tier, 1,200 zap buffer, 3 zap/t use.
