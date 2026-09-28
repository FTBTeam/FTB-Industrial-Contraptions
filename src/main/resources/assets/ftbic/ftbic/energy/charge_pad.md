---
navigation:
  title: Charge Pad
  icon: ftbic:charge_pad
  parent: energy/index.md
  position: 5
item_ids:
  - ftbic:charge_pad
---

# <Color id="gold">Charge Pad</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="charge_pad" scale="2" />

  A flat block that recharges any powered item a player is wearing or carrying, simply by standing on it.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="charge_pad" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Usage</Color>
</Column>

* Wire power into any face. The pad accepts up to **IV** input.
* Internal buffer: <Energy config="machines.charge_pad_capacity" />.
* Stand on top to recharge worn armor (<ItemLink id="carbon_chestplate" />, <ItemLink id="quantum_chestplate" />, <ItemLink id="mechanical_elytra" />) and any batteries in the inventory.
* Open the pad to charge up to four inserted rechargeable items, including Carbon and Quantum chestplates.
* Standing on the pad and charging inserted items both draw from its energy buffer.

Pair with an <ItemLink id="ev_battery_box" /> above a player station for quick charging after every expedition.
