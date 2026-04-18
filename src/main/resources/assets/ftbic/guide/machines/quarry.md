---
navigation:
  title: Quarry
  icon: ftbic:quarry
  parent: machines/index.md
  position: 12
item_ids:
  - ftbic:quarry
  - ftbic:landmark
---

# <Color id="gold">Quarry</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="quarry" scale="2" />

  Mines out a rectangular region defined by a pair of <ItemLink id="landmark" /> posts. Outputs to its internal inventory; pipe out with a hopper or ejector.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="quarry" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Setup</Color>
</Column>

1. Place two <ItemLink id="landmark" /> posts at opposite corners of the area you want mined.
2. Place the Quarry adjacent to one of them.
3. Feed it HV power — it starts mining downward.
4. 8 ticks to mine a block, 2 ticks to move the head. Fluids are replaced with <ItemLink id="exfluid" /> to prevent leaks.

<ItemImage id="minecraft:air" scale="0.25"/>

**Stats:** HV tier, 10,000 zap buffer, 3 zap/t use.

The Quarry is one of the few machines without an advanced variant — instead, use <ItemLink id="overclocker_upgrade" />s to speed it up.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Pickaxe Slot</Color>
</Column>

The Quarry has a pickaxe slot between the pause button and the battery slot. Any item tagged `#minecraft:pickaxes` fits. When a pickaxe sits in that slot, the Quarry simulates mining each block with it, so every enchantment on the pickaxe applies to the resulting drops:

- <ItemLink id="minecraft:diamond_pickaxe" /> with **Silk Touch** drops stone, raw ores, glass and spawners intact.
- <ItemLink id="minecraft:diamond_pickaxe" /> with **Fortune III** multiplies gem, redstone, and lapis drops the same way a player would.
- **Efficiency** grants a 10% mining-speed bonus per level, stacking multiplicatively with <ItemLink id="overclocker_upgrade" />s. Efficiency V on its own cuts the 8-tick mine time roughly in half.
- Other enchants like Unbreaking have no effect — the Quarry does not depend on tool durability.

By default the pickaxe never takes durability damage. To mirror vanilla behaviour where each block mined costs one durability, set `quarry_pickaxe_takes_damage = true` in `ftbic-common.toml`. If the pickaxe breaks, the slot empties and mining falls back to the no-tool drop table.
