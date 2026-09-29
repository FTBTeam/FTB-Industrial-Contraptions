---
navigation:
  title: Energy Rectifiers
  icon: ftbic:mv_rectifier
  parent: energy/index.md
  position: 4
item_ids:
  - ftbic:lv_rectifier
  - ftbic:mv_rectifier
  - ftbic:hv_rectifier
  - ftbic:ev_rectifier
  - ftbic:iv_rectifier
---

# <Color id="gold">Energy Rectifiers</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="hv_rectifier" scale="2" />

  FTBIC cables and generators already **push FE** into any FE-accepting block. You do not need a rectifier to run vanilla quarries, mekanism machines, or any other FE-consuming mod.

  <ZapMode>

  You **do** need a rectifier when an FE-producing source needs to feed power **into** an FTBIC machine or network. FTBIC machines do not accept FE directly; the rectifier is the one-way adapter that converts incoming FE into zaps.

  </ZapMode>

  <FEMode>

  **Full FE mode** is enabled, so rectifiers are hidden and cannot be crafted. Every FTBIC machine accepts FE directly on its faces. FE input is capped at the machine's input rate and never burns it out.

  </FEMode>
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<ZapMode>

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">How They Work</Color>
</Column>

A rectifier is directional. Its **FE face** accepts Forge Energy from an adjacent FE-producing cable, generator, or storage block. The remaining faces output the converted power as zaps into the FTBIC network.

The conversion rate is **1 zap = 8 FE** by default (configurable). Each tier's rectifier converts at the matching voltage transfer rate:

| Rectifier | Zap rate |
|-----------|----------|
| LV | <Energy config="energy.lv_transfer_rate" rate="true" /> |
| MV | <Energy config="energy.mv_transfer_rate" rate="true" /> |
| HV | <Energy config="energy.hv_transfer_rate" rate="true" /> |
| EV | <Energy config="energy.ev_transfer_rate" rate="true" /> |
| IV | <Energy config="energy.iv_transfer_rate" rate="true" /> |

If you only need to power **FE** machines from FTBIC generators, skip the rectifier entirely. Any FTBIC cable or machine output face pushes FE on its own.

<ItemImage id="minecraft:air" scale="0.25"/>
***

</ZapMode>

<Row>
  <ItemImage id="lv_rectifier" />
  ### <Color id="aqua">LV Energy Rectifier</Color>
</Row>

<RecipeFor id="lv_rectifier" fallbackText="Not craftable while full FE mode is enabled." />

<Row>
  <ItemImage id="mv_rectifier" />
  ### <Color id="aqua">MV Energy Rectifier</Color>
</Row>

<RecipeFor id="mv_rectifier" fallbackText="Not craftable while full FE mode is enabled." />

<Row>
  <ItemImage id="hv_rectifier" />
  ### <Color id="aqua">HV Energy Rectifier</Color>
</Row>

<RecipeFor id="hv_rectifier" fallbackText="Not craftable while full FE mode is enabled." />

<Row>
  <ItemImage id="ev_rectifier" />
  ### <Color id="aqua">EV Energy Rectifier</Color>
</Row>

<RecipeFor id="ev_rectifier" fallbackText="Not craftable while full FE mode is enabled." />

<Row>
  <ItemImage id="iv_rectifier" />
  ### <Color id="aqua">IV Energy Rectifier</Color>
</Row>

<RecipeFor id="iv_rectifier" fallbackText="Not craftable while full FE mode is enabled." />
