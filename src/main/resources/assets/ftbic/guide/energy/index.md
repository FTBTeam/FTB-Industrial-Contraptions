---
navigation:
  title: Energy Network
  icon: ftbic:lv_cable
  position: 10
---

# <Color id="gold">Energy Network</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="lv_cable" scale="2" />

  FTBIC runs on its own power unit — the **zap**. Every cable, battery, and machine uses zaps internally. FTBIC generators and cables can power FE-consuming machines directly; the opposite direction — feeding FE **into** an FTBIC machine — requires a <ItemLink id="lv_rectifier" />.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Voltage Tiers</Color>
</Column>

Every cable and every block is rated for one of five voltage tiers. Feed more into a block than its tier allows and it **burns out**.

| Tier | Abbrev | Transfer rate | Typical use |
|------|--------|---------------|-------------|
| Low     | **LV** | 32 zaps/t    | First generator, first machines |
| Medium  | **MV** | 128 zaps/t   | Ore processing chains |
| High    | **HV** | 512 zaps/t   | Advanced machines |
| Extreme | **EV** | 2,048 zaps/t | Nuclear, teleporters, quantum |
| Insane  | **IV** | 8,192 zaps/t | Antimatter, endgame |

**Rule of thumb:** match the cable tier to the highest producer or consumer connected to it. Use <ItemLink id="lv_transformer" />-family blocks to step voltage up or down between tiers.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Burn-out and Fuses</Color>
</Column>

A cable that receives too high a voltage collapses into a <ItemLink id="burnt_cable" />. A machine will also melt — its block entity becomes a burnt shell until repaired with a <ItemLink id="fuse" />.

Burnt cables can be recycled for <ItemLink id="scrap" />.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Subsections</Color>
</Column>

<SubPages icons={true} />
