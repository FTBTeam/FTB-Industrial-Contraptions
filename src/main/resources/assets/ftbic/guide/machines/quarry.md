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
