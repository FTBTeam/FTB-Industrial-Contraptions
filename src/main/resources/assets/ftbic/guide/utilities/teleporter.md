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

  A player-targeted teleportation pad. Pair two teleporters by name, feed one MV power or higher, and stand on it to snap to the other. A linked pair also acts as a 2-way item and fluid pipe — see below.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Setup</Color>
</Column>

1. Place a teleporter and open its GUI.
2. Set its **ID** (the name other teleporters will see it by).
3. Choose whether to **publish** it — public teleporters appear in everyone's list; unpublished ones only show to the owner.
4. Wire in MV power or higher — the teleporter behaves as if it had max transformer upgrades installed, so any tier from MV up to IV feeds it without burning. LV works but trickles.
5. From another teleporter's GUI, pick the destination from the list. Picking a destination automatically links the other end back as well, provided the other end is not already pointed at something else.

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

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Item and Fluid Pipe</Color>
</Column>

A linked pair of teleporters is also a bi-directional pipe. Hoppers, pipes, pumps — anything that can push items or fluids into a block — work against either teleporter. Whatever goes in one side comes out near the other.

Each teleporter has two internal buffers for each resource type:

* **Sending storage**: 9 slots + a 16 000 mB fluid tank. External pipes, hoppers, and conduits that push into the teleporter drop their resources here.
* **Receiving storage**: 9 slots + a 16 000 mB fluid tank. External pipes that pull from the teleporter draw from here.

Once every few ticks the teleporter flushes its sending buffer to the peer's receiving buffer. Both teleporters do this, so the same pair carries traffic in both directions at once. Throughput caps at roughly 8 items and 2 000 mB per flush per side.

Because everything external happens through the two internal buffers, any mod pipe or conduit that can push into an inventory can feed the sending storage, and any mod pipe or conduit that can extract from an inventory can drain the receiving storage. No special mod integration required.

The teleporter item model exposes sending storage as "insert only" slots and receiving storage as "extract only" slots, so most mods' auto-detection will route correctly without extra configuration.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Transport Power</Color>
</Column>

Moving resources through the link costs a small idle drain on the **sending** teleporter — not per item, but once per second while traffic is flowing. If nothing has moved in the last second, the drain stops.

The pair also **shares power**: whichever teleporter has more zaps quietly topping up the other, so you only need to wire one side to keep both humming. If both buffers drop to zero, transfers halt until you feed power back in.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Clear Buttons</Color>
</Column>

If the peer is unreachable (unlinked, unloaded peer chunk, buffer full on the other side), contents will pile up in the sending storage. Two buttons in the GUI deal with this:

* **Clear Storage** moves everything from the sending item slots into the receiving item slots on the same teleporter, so an ordinary extract pipe on the receive side can retrieve them.
* **Clear Fluids** does the same for the fluid tanks.

The block itself drops its item contents to the world when broken, so work is not lost if you pick up a half-full teleporter.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Cross-Dimension Pipes</Color>
</Column>

Item and fluid transfer works across dimensions, same as player teleport. While a transfer is active, the peer's chunk is **force-loaded automatically** so you don't need a separate chunk loader at the far end. The chunk releases after about a minute of idle traffic.
