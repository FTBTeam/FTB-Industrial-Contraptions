---
navigation:
  title: Generators
  icon: ftbic:basic_generator
  position: 20
---

# <Color id="gold">Generators</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="basic_generator" scale="2" />

  Four renewable generators, one combustion generator, plus the nuclear reactor cover every stage of the mod's power curve.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Output Comparison</Color>
</Column>

| Generator | Tier | Output | Conditions |
|-----------|------|--------|-----------|
| <ItemLink id="basic_generator" /> | LV | <Energy config="machines.basic_generator_output" rate="true" /> | While fuel burns |
| <ItemLink id="geothermal_generator" /> | LV | <Energy config="machines.geothermal_generator_output" rate="true" /> | Needs lava in tank |
| <ItemLink id="wind_mill" /> | LV | <ZapMode>0.3 to 6.5 z/t</ZapMode><FEMode><Energy config="machines.wind_mill_min_output" rate="true" /> to <Energy config="machines.wind_mill_max_output" rate="true" /></FEMode> | Height-dependent |
| <ItemLink id="lv_solar_panel" /> | LV | <Energy config="machines.lv_solar_panel_output" rate="true" /> | Daylight, sky access |
| <ItemLink id="mv_solar_panel" /> | MV | <Energy config="machines.mv_solar_panel_output" rate="true" /> | Daylight, sky access |
| <ItemLink id="hv_solar_panel" /> | HV | <Energy config="machines.hv_solar_panel_output" rate="true" /> | Daylight, sky access |
| <ItemLink id="ev_solar_panel" /> | EV | <Energy config="machines.ev_solar_panel_output" rate="true" /> | Daylight, sky access |
| <ItemLink id="nuclear_reactor" /> | up to IV | <Energy zaps="1000" rate="true" /> or more | Depends on layout |

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Subsections</Color>
</Column>

<SubPages icons={true} />
