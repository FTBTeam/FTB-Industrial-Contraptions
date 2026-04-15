---
navigation:
  title: Teleporter
  icon: ftbic:teleporter
  parent: utilities/index.md
  position: 1
item_ids:
  - ftbic:teleporter
---

# <Color id="gold">Teleporter</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="teleporter" scale="2" />

  A player-targeted teleportation pad. Pair two teleporters by name, feed one EV power, and stand on it to snap to the other.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Setup</Color>
</Column>

1. Place a teleporter and open its GUI.
2. Set its **ID** (the name other teleporters will see it by).
3. Choose whether to **publish** it — public teleporters appear in everyone's list; unpublished ones only show to the owner.
4. Wire in EV power — capacity 1,000,000 zaps.
5. From another teleporter's GUI, pick the destination from the list.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Energy Cost</Color>
</Column>

* **Minimum:** 100 zaps (distances up to 16 blocks).
* **Maximum:** 10,000 zaps (distances at or above 1,200 blocks).
* Scales linearly between those bounds.

The destination chunk **must be loaded** — if the target is far away, pre-load it with a chunk loader or the teleport will error out.

<ItemImage id="minecraft:air" scale="0.25"/>
***

Only the teleporter's **owner** can rename, delete, or unpublish it.
