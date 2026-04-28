---
navigation:
  title: Nuclear
  icon: ftbic:nuclear_reactor
  position: 40
---

# <Color id="gold">Nuclear Power</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="nuclear_reactor" scale="2" />

  The highest-output power source in the mod — and the most punishing mistake. A working reactor produces **hundreds to thousands of zaps per tick**. A failing one **vaporizes your base**.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">The Three Variables</Color>
</Column>

A reactor layout is a puzzle of three interacting quantities.

* <Color id="red">**Heat**</Color> — produced by fuel rods. Accumulates in the reactor core.
* <Color id="aqua">**Cooling**</Color> — coolant cells and vents pull heat from adjacent fuel rods and disperse it.
* <Color id="gold">**Output**</Color> — zaps per tick, multiplied by adjacent neutron reflectors and dual/quad rods.

If heat reaches 100% the reactor **explodes**, scaling with base radius **10** up to a cap of **80 blocks** — enough to carve out a crater that reaches bedrock.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Subsections</Color>
</Column>

<SubPages icons={true} />
