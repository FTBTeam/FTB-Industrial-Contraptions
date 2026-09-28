---
navigation:
  title: Energy Network
  icon: ftbic:lv_cable
  position: 10
---

# <Color id="gold">Energy Network</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="lv_cable" scale="2" />

  <ZapMode>

  FTBIC runs on its own power unit, the **zap**. Every cable, battery, and machine uses zaps internally. FTBIC generators and cables can power FE-consuming machines directly. Feeding FE **into** an FTBIC machine requires an <ItemLink id="lv_rectifier" />.

  </ZapMode>

  <FEMode>

  **Full FE mode** is enabled, so FTBIC works in Forge Energy (FE). Every GUI, tooltip, JEI page, and Jade readout shows energy in FE. FTBIC blocks exchange FE directly with other mods: generators and cables push FE into other mods' machines, and every FTBIC machine accepts FE on its faces, so no rectifier is needed.

  </FEMode>

  When FTBIC pushes into an FE consumer through a cable, the flow is capped at the cable tier's transfer rate. An LV cable running into an FE machine that would happily drink thousands of FE per tick still only delivers <Energy config="energy.lv_transfer_rate" rate="true" /><ZapMode> worth of FE</ZapMode>, keeping the cable safe instead of burning it out.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Voltage Tiers</Color>
</Column>

Every cable and every block is rated for one of five voltage tiers. Feed more into a block than its tier allows and it **burns out**.

| Tier | Abbrev | Transfer rate | Typical use |
|------|--------|---------------|-------------|
| Low     | **LV** | <Energy config="energy.lv_transfer_rate" rate="true" /> | First generator, first machines |
| Medium  | **MV** | <Energy config="energy.mv_transfer_rate" rate="true" /> | Ore processing chains |
| High    | **HV** | <Energy config="energy.hv_transfer_rate" rate="true" /> | Advanced machines |
| Extreme | **EV** | <Energy config="energy.ev_transfer_rate" rate="true" /> | Nuclear, teleporters, quantum |
| Insane  | **IV** | <Energy config="energy.iv_transfer_rate" rate="true" /> | Antimatter, endgame |

**Rule of thumb:** match the cable tier to the highest producer or consumer connected to it. Use <ItemLink id="lv_transformer" />-family blocks to step voltage down to a lower tier.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Burn-out and Fuses</Color>
</Column>

A cable that receives too high a voltage collapses into a <ItemLink id="burnt_cable" />. A machine will also melt: its block entity becomes a burnt shell until repaired with a <ItemLink id="fuse" />.

<FEMode>

FE input never burns anything out. When another mod sends FE into an FTBIC machine, the machine takes up to its own input rate and refuses the rest. Overvoltage from FTBIC cables still burns LV and MV machines, battery boxes, and transformers.

</FEMode>

Burnt cables can be recycled for <ItemLink id="scrap" />.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Subsections</Color>
</Column>

<SubPages icons={true} />
