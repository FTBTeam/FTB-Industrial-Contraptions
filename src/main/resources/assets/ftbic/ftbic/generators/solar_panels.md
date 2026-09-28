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

<Energy config="machines.lv_solar_panel_output" rate="true" /> during the day. Buffer: <Energy config="machines.lv_solar_panel_capacity" times="60" />. Entry-tier solar.

<RecipeFor id="lv_solar_panel" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="mv_solar_panel" />
  ### <Color id="aqua">MV Solar Panel</Color>
</Row>

<Energy config="machines.mv_solar_panel_output" rate="true" /> during the day. Buffer: <Energy config="machines.mv_solar_panel_capacity" times="60" />.

<RecipeFor id="mv_solar_panel" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="hv_solar_panel" />
  ### <Color id="aqua">HV Solar Panel</Color>
</Row>

<Energy config="machines.hv_solar_panel_output" rate="true" /> during the day. Buffer: <Energy config="machines.hv_solar_panel_capacity" times="60" />.

<RecipeFor id="hv_solar_panel" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="ev_solar_panel" />
  ### <Color id="aqua">EV Solar Panel</Color>
</Row>

<Energy config="machines.ev_solar_panel_output" rate="true" /> during the day. Buffer: <Energy config="machines.ev_solar_panel_capacity" times="60" />. The largest solar output in the mod.

<RecipeFor id="ev_solar_panel" />
