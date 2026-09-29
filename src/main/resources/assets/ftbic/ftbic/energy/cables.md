---
navigation:
  title: Cables
  icon: ftbic:mv_cable
  parent: energy/index.md
  position: 1
item_ids:
  - ftbic:lv_cable
  - ftbic:mv_cable
  - ftbic:hv_cable
  - ftbic:ev_cable
  - ftbic:iv_cable
  - ftbic:superconducting_cable
  - ftbic:burnt_cable
  - ftbic:lv_reinforced_cable
  - ftbic:mv_reinforced_cable
  - ftbic:hv_reinforced_cable
  - ftbic:ev_reinforced_cable
  - ftbic:iv_reinforced_cable
  - ftbic:burnt_reinforced_cable
---

# <Color id="gold">Cables</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="hv_cable" scale="2" />

  Cables connect producers, consumers, and batteries into a single network. Energy travels instantly along a connected chain up to the configured **max cable length** (default 300).
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="lv_cable" />
  ### <Color id="aqua">LV Cable (<Energy config="energy.lv_transfer_rate" rate="true" />)</Color>
</Row>

Rubber-coated copper. The default cable for basic generators, the macerator, and the powered furnace.

<RecipeFor id="lv_cable" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="mv_cable" />
  ### <Color id="aqua">MV Cable (<Energy config="energy.mv_transfer_rate" rate="true" />)</Color>
</Row>

Insulated gold wire. Used between MV transformers and mid-tier machines.

<RecipeFor id="mv_cable" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="hv_cable" />
  ### <Color id="aqua">HV Cable (<Energy config="energy.hv_transfer_rate" rate="true" />)</Color>
</Row>

Insulated iron wire. Necessary for advanced machines and the first stage out of a nuclear reactor.

<RecipeFor id="hv_cable" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="ev_cable" />
  ### <Color id="aqua">EV Cable (<Energy config="energy.ev_transfer_rate" rate="true" />)</Color>
</Row>

Insulated iridium wire. Feeds the Teleporter, Antimatter Constructor, and Quantum gear charge pads.

<RecipeFor id="ev_cable" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="iv_cable" />
  ### <Color id="aqua">IV Cable (<Energy config="energy.iv_transfer_rate" rate="true" />)</Color>
</Row>

Glass cable for high-output reactor designs and the endgame antimatter chain.

<RecipeFor id="iv_cable" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="superconducting_cable" />
  ### <Color id="aqua">Superconducting Cable (Unlimited <EnergyUnit rate="true" />)</Color>
</Row>

Combine **two IV glass cables**, **one redstone dust**, and **one glowstone dust** in any crafting grid to make **two Superconducting Cables**.

Superconducting cables have no transfer limit and cannot burn from overload. Machines still obey their own input and output limits, and the configured maximum cable length still applies. Connect superconducting cables to each other; different cable tiers do not connect directly.

A glowing cyan pulse travels through the exposed core while energy is transferred. It fades when the source stops supplying energy or the destination fills. Branches with no accepted transfer stay dark. The glow works without shaders.

<RecipeFor id="superconducting_cable" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="burnt_cable" />
  ### <Color id="aqua">Burnt Cable</Color>
</Row>

What a cable becomes if it receives too much voltage. Break it, replace it with the correct tier, and grind the burnt remains into <ItemLink id="scrap" />.

<RecipeFor id="scrap" fallbackText="Drop a Burnt Cable in any crafting grid for scrap." />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Reinforced Cables</Color>

  <ItemImage id="lv_reinforced_cable" scale="2" />

  A full cube with the toughness of <ItemLink id="reinforced_stone" /> that still passes power at its tier rate. Use these to run cables **through** a reactor containment wall without leaving a gap. Each tier takes its color from the matching regular cable.
</Column>

Reinforced cables connect to regular cables of the same tier, so transitioning at the wall needs no transformer. If a reinforced cable is overloaded it collapses into a <ItemLink id="burnt_reinforced_cable" /> (a dead reinforced-stone block with a scorched core) rather than a flimsy burnt cable, keeping the containment intact.

<Row>
  <ItemImage id="lv_reinforced_cable" />
  ### <Color id="aqua">Reinforced LV Cable (<Energy config="energy.lv_transfer_rate" rate="true" />)</Color>
</Row>

<RecipeFor id="lv_reinforced_cable" />

<Row>
  <ItemImage id="mv_reinforced_cable" />
  ### <Color id="aqua">Reinforced MV Cable (<Energy config="energy.mv_transfer_rate" rate="true" />)</Color>
</Row>

<RecipeFor id="mv_reinforced_cable" />

<Row>
  <ItemImage id="hv_reinforced_cable" />
  ### <Color id="aqua">Reinforced HV Cable (<Energy config="energy.hv_transfer_rate" rate="true" />)</Color>
</Row>

<RecipeFor id="hv_reinforced_cable" />

<Row>
  <ItemImage id="ev_reinforced_cable" />
  ### <Color id="aqua">Reinforced EV Cable (<Energy config="energy.ev_transfer_rate" rate="true" />)</Color>
</Row>

<RecipeFor id="ev_reinforced_cable" />

<Row>
  <ItemImage id="iv_reinforced_cable" />
  ### <Color id="aqua">Reinforced IV Cable (<Energy config="energy.iv_transfer_rate" rate="true" />)</Color>
</Row>

<RecipeFor id="iv_reinforced_cable" />

<Row>
  <ItemImage id="burnt_reinforced_cable" />
  ### <Color id="aqua">Burnt Reinforced Cable</Color>
</Row>

What a reinforced cable becomes when overloaded. It is still wither- and dragon-immune so it stays in place as a containment block, but it no longer conducts energy. Replace it with a fresh reinforced cable of the correct tier.
