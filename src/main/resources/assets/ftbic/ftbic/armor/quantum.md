---
navigation:
  title: Quantum Armor
  icon: ftbic:quantum_chestplate
  parent: armor/index.md
  position: 2
item_ids:
  - ftbic:quantum_helmet
  - ftbic:quantum_chestplate
  - ftbic:quantum_leggings
  - ftbic:quantum_boots
---

# <Color id="gold">Quantum Armor</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="quantum_chestplate" scale="2" />

  The endgame powered armor. Same protective model as carbon but with a reservoir of **<Energy config="equipment.quantum_armor_capacity" />**, flight, and full damage soaking at EV charge rates.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="quantum_helmet" />
  ### <Color id="aqua">Quantum Helmet</Color>
</Row>

<RecipeFor id="quantum_helmet" />

<Row>
  <ItemImage id="quantum_chestplate" />
  ### <Color id="aqua">Quantum Chestplate</Color>
</Row>

Holds the full set's energy. Double tap jump to hover and fly while charged. Hovering spends <Energy config="equipment.armor_flight_energy" rate="true" />. The chestplate also supports gliding; hold jump to boost or sneak to slow down while gliding.

<RecipeFor id="quantum_chestplate" />

<Row>
  <ItemImage id="quantum_leggings" />
  ### <Color id="aqua">Quantum Leggings</Color>
</Row>

<RecipeFor id="quantum_leggings" />

<Row>
  <ItemImage id="quantum_boots" />
  ### <Color id="aqua">Quantum Boots</Color>
</Row>

<RecipeFor id="quantum_boots" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Tips</Color>
</Column>

* The chestplate consumes <Energy config="equipment.armor_flight_energy" rate="true" /> while hovering or gliding. Boosting and braking during a glide use additional energy.
* Damage absorption uses only stored energy. The pieces never lose durability and provide no protection when the chestplate is empty.
* Place a <ItemLink id="charge_pad" /> over an <ItemLink id="ev_battery_box" /> so stepping on it recharges the whole set instantly.
