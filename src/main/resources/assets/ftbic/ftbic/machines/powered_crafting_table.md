---
navigation:
  title: Powered Crafting Table
  icon: ftbic:powered_crafting_table
  parent: machines/index.md
  position: 10
item_ids:
  - ftbic:powered_crafting_table
---

# <Color id="gold">Powered Crafting Table</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="powered_crafting_table" scale="2" />

  An auto-crafting table. Lock a 3×3 pattern and it consumes items from its inventory to produce the output, spending <ZapMode>1 zap</ZapMode><FEMode><Energy config="machines.powered_crafting_table_use" /></FEMode> per craft.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="powered_crafting_table" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

**Stats:** LV tier, <Energy config="machines.powered_crafting_table_capacity" /> buffer, <ZapMode>1 zap</ZapMode><FEMode><Energy config="machines.powered_crafting_table_use" /></FEMode> per craft.

Set up against an output hopper to pipe components in and crafted items out. Works with any shaped or shapeless vanilla or modded recipe.

Drag items from **JEI** onto the crafting grid to assign [input slot filters](side_configuration.md), or use the **L** panel. These ghosts keep each ingredient assigned after crafting consumes the real items. A Configuration Card copies the filters to another Powered Crafting Table; real ingredients must still be supplied.
