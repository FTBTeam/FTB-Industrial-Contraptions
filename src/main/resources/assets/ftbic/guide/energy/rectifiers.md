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

  FTBIC cables and generators already **push FE** into any FE-accepting block — you do not need a rectifier to run vanilla quarries, mekanism machines, or any other FE-consuming mod.

  You **do** need a rectifier when an FE-producing source needs to feed power **into** an FTBIC machine or network. FTBIC machines do not accept FE directly; the rectifier is the one-way adapter that converts incoming FE into zaps.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">How They Work</Color>
</Column>

A rectifier is directional. Its **FE face** accepts Forge Energy from an adjacent FE-producing cable, generator, or storage block. The remaining faces output the converted power as zaps into the FTBIC network.

The conversion rate is **1 zap = 10 FE** by default (configurable). Each tier's rectifier converts at the matching voltage transfer rate:

| Rectifier | Zap rate |
|-----------|----------|
| LV | 32 zap/t |
| MV | 128 zap/t |
| HV | 512 zap/t |
| EV | 2,048 zap/t |
| IV | 8,192 zap/t |

If you only need to power **FE** machines from FTBIC generators, skip the rectifier entirely — any FTBIC cable or machine output face pushes FE on its own.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="lv_rectifier" />
  ### <Color id="aqua">LV Energy Rectifier</Color>
</Row>

<RecipeFor id="lv_rectifier" />

<Row>
  <ItemImage id="mv_rectifier" />
  ### <Color id="aqua">MV Energy Rectifier</Color>
</Row>

<RecipeFor id="mv_rectifier" />

<Row>
  <ItemImage id="hv_rectifier" />
  ### <Color id="aqua">HV Energy Rectifier</Color>
</Row>

<RecipeFor id="hv_rectifier" />

<Row>
  <ItemImage id="ev_rectifier" />
  ### <Color id="aqua">EV Energy Rectifier</Color>
</Row>

<RecipeFor id="ev_rectifier" />

<Row>
  <ItemImage id="iv_rectifier" />
  ### <Color id="aqua">IV Energy Rectifier</Color>
</Row>

<RecipeFor id="iv_rectifier" />
