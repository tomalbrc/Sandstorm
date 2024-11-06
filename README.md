# Sandstorm

Bedrock/Snowstorm compatible particle engine but for fabric, java - using polymer for packet-based display entities

Use the webapp [snowstorm.app](https://snowstorm.app) (or VSCode extension or app from Microsoft Store) to create particle effects.

WIP™

## Component implementation

🟢 = Fully implemented\
🟠 = Partially implemented\
🔴 = Not implemented

### Emitter:
 
- 🟢 `minecraft:emitter_initialization`
- 🟢 `minecraft:emitter_lifetime_looping`
- 🟢 `minecraft:emitter_lifetime_once`
- 🟢 `minecraft:emitter_shape_disc`
- 🟢 `minecraft:emitter_shape_box`
- 🟢 `minecraft:emitter_shape_point`
- 🟢 `minecraft:emitter_shape_sphere`
- 🟢 `minecraft:emitter_rate_instant`
- 🟢 `minecraft:emitter_rate_steady`
- 🟢 `minecraft:emitter_rate_manual`
- 🟢 `minecraft:emitter_shape_custom`
- 🟢 `minecraft:emitter_lifetime_expression`
- 🟢 `minecraft:emitter_lifetime_events`
- 🔴 `minecraft:emitter_shape_entity_aabb`
- 🔴 `minecraft:emitter_local_space`

### Particle:

- 🟢 `minecraft:particle_initial_speed`
- 🟢 `minecraft:particle_initial_spin`
- 🟢 `minecraft:particle_lifetime_expression`
- 🟢 `minecraft:particle_motion_collision`
- 🟢 `minecraft:particle_motion_dynamic`
- 🟢 `minecraft:particle_motion_parametric`
- 🟠 `minecraft:particle_appearance_billboard`
- 🟢 `minecraft:particle_appearance_lighting`
- 🟢 `minecraft:particle_appearance_tinting`
- 🟢 `minecraft:particle_expire_if_in_blocks`
- 🟢 `minecraft:particle_expire_if_not_in_blocks`
- 🟢 `minecraft:particle_lifetime_events`
- 🔴 `minecraft:particle_kill_plane`

## Issues

- Very "long" numbers can't be parsed by the molang compiler (Ex: 0.4398741874364278346234372)
- Transparency is not supported yet (switch to text display entities?)
- Molang expression support for UV mappings is limited due to resource-pack limitations in minecraft java edition 
- No support for `bezier_curve`-type curves
