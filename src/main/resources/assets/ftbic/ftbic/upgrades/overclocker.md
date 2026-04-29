---
navigation:
  title: Overclocker
  icon: ftbic:overclocker_upgrade
  parent: upgrades/index.md
  position: 1
item_ids:
  - ftbic:overclocker_upgrade
---

# <Color id="gold">Overclocker Upgrade</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="overclocker_upgrade" scale="2" />

  Multiplies recipe speed by **1.45×** and energy use by **1.6×** per upgrade. Throughput scales exponentially with stacked upgrades — at the cost of a much higher power draw.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="overclocker_upgrade" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

* Maximum stack size: **4 per slot**, so a single slot gives up to 1.45⁴ ≈ 4.42× speed for 1.6⁴ ≈ 6.55× energy.
* Fill all 4 upgrade slots for 16 upgrades total — a macerator draws 2 zap/t by default, 16 overclockers push that to ~55,000 zap/t.
* Always pair heavy overclocking with a matching <ItemLink id="transformer_upgrade" /> and adequate cabling.
