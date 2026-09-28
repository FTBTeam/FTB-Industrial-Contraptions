---
navigation:
  title: Mechanical Elytra
  icon: ftbic:mechanical_elytra
  parent: armor/index.md
  position: 3
item_ids:
  - ftbic:mechanical_elytra
---

# <Color id="gold">Mechanical Elytra</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="mechanical_elytra" scale="2" />

  A powered elytra with iron chestplate protection and a dedicated wing texture. It recharges in daylight under open sky and glides while it has charge.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="mechanical_elytra" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

* Capacity: **<Energy config="equipment.mechanical_elytra_capacity" />**.
* Passive recharge rate: <Energy config="equipment.mechanical_elytra_recharge" rate="true" /> while worn in daylight under open sky, and not while gliding. Full recharge takes about 40 minutes from empty.
* Can also be charged in a <ItemLink id="charge_pad" /> or battery box.
* Uses the chestplate slot, so it cannot combine with a powered armor chestplate. Hold sneak while gliding to slow down.
