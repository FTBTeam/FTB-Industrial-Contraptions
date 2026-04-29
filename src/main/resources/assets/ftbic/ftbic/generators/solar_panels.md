---
navigation:
  title: Solar Panels
  icon: ftbic:hv_solar_panel
  parent: generators/index.md
  position: 3
item_ids:
  - ftbic:lv_solar_panel
  - ftbic:mv_solar_panel
  - ftbic:hv_solar_panel
  - ftbic:ev_solar_panel
---

# <Color id="gold">Solar Panels</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="hv_solar_panel" scale="2" />

  Solar panels need **direct sky access** and **daylight** to generate. They produce nothing at night or when covered.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="lv_solar_panel" />
  ### <Color id="aqua">LV Solar Panel</Color>
</Row>

1 zap/t during the day. Buffer: 60 zaps. Entry-tier solar.

<RecipeFor id="lv_solar_panel" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="mv_solar_panel" />
  ### <Color id="aqua">MV Solar Panel</Color>
</Row>

8 zap/t during the day. Buffer: 480 zaps.

<RecipeFor id="mv_solar_panel" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="hv_solar_panel" />
  ### <Color id="aqua">HV Solar Panel</Color>
</Row>

64 zap/t during the day. Buffer: 3,840 zaps.

<RecipeFor id="hv_solar_panel" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="ev_solar_panel" />
  ### <Color id="aqua">EV Solar Panel</Color>
</Row>

512 zap/t during the day. Buffer: 30,720 zaps. The largest solar output in the mod.

<RecipeFor id="ev_solar_panel" />
