# GlobalWarming - ![](https://travis-ci.org/nsporillo/GlobalWarming.svg?branch=master) - Supports Minecraft 1.13+
Minecraft Server Java Edition (Spigot) plugin which adds game changing climate change mechanics.

## Contributing
- [Developer Setup](https://github.com/nsporillo/GlobalWarming/wiki/Developer-Setup-and-Installation)
- There is a Kanban board on Github where I'll be prioritizing work, feel free to help work on any of the tasks.
- Pull requests welcome! This is a very new project and I appreciate contributions.

## Overview
+ Adds the concept of greenhouse gases (CO2) in the worlds atmosphere 
+ Furnaces emit CO2 when players smelt items
+ Trees (instantly) absorb CO2 when they grow from a sapling
+ As CO2 levels rise, global temperature rises because of the [Greenhouse Effect](http://hyperphysics.phy-astr.gsu.edu/hbase/thermo/grnhse.html)

## Purpose
+ Ever had the hankering to turn a game meant for fun into a emulator of one of the modern worlds toughest challenges?
+ Observe the [Prisoner's Dilemma](https://en.wikipedia.org/wiki/Prisoner%27s_dilemma#In_environmental_studies) first hand! 
+ Players are best off when they co-operate and agree to reduce their emissions
+ However, each individual player typically believes they are better off for themselves to emit as much as they wish. 
+ Avoiding a [Tragedy of the Commons](https://en.wikipedia.org/wiki/Tragedy_of_the_commons) might be fun! If the players dont play nice and end up dealing with the consequences, they might decide to make an agreement to fix the planet. 

## Mechanics 
+ Every furnace burn causes a "Contribution" to emissions with an associated numerical value (based on the model)
+ Every tree growth causes a "Reduction" from emissions with an associated numerical value (based on the model)
+ The global temperature is a function of the net global carbon score. 
+ As the global temperature rises, the frequency and severity of negative climate damages increases.
+ Players can purchase "carbon offsets" which creates a tree-planting bounty for other players to fulfill. 

## Challenges
+ Designing a default model that doesn't quickly destroy worlds
+ Efficiently applying in-game mechanics changes

## (Planned) Features
+ Scoreboard Integration - Players can compete for carbon neutrality! The worst polluters can be shamed.
+ Economy Integration - Carbon Offsetting using your in-game currency! Set up tree-planting bounties 
+ Carbon Scorecard - Each player can see their latest carbon footprint trends via command line.
+ Multi-world - You can experiment with this plugin on one of your worlds to test it out!
+ Custom Models - The inner numerical workings are as configurable as possible. Set thresholds, probabilities, and distributions.
+ Database storage - Load data on startup, queue DB changes to be done async and at intervals (instead of as they happen, that'd kill performance), and empty queue on shutdown. 
+ Highly configurable - Almost everything will have some degree of configuration to suit your server's needs.
+ Efficient - Despite major mechanics changes and an extensive event listening setup, I plan to optimize this plugin to be suitable for up to medium to large servers. 


## Negative Climate Damages (Sample Model)
Higher temps inherit the damages from the lower temps

| Global Temp | Effect 1 | Effect 2 | Effect 3 | Effect 4 |
| ------ | ------------ | ------- | --------|-----------|
| 14.0 C | None | | | |
| 15.0 C | Some mobs spawn less | some mobs spawn more | | |
| 16.0 C | +1 Sea level rise | Some fish die | Ice/Snow stops forming | |
| 17.0 C | +1 Sea level rise | Flora species growth stunted | Aquatic life stops spawning | Ocean flora/fauna die |
| 18.0 C | +1 Sea level rise | Area Potion Effect Clouds | Farm yields lower | Snow/Ice melts |
| 19.0 C | +1 Sea level rise | Forest Fires | Slower Health Regen | Frequent T-Storms | |
| 20.0 C | +1 Sea level rise | Severe Forest Fires | Violent T-Storms | Permanent Slowness Effect |

These are just a sample of the possible effects, these will be configurable and implement randomness and probabilities.


## Roadmap
+ Education Edition support
+ Incorporate Methane, since CO2 is not the only greenhouse gas that matters.

## Related Science of Climate Change
### Atmosphere warms with more CO2 because of Greenhouse Effect
![Greenhouse Effect](https://i.imgur.com/XsWJGz9.png)
### Atmospheric Energy Budget
+ ![Earth's Global Energy Budget](https://i.imgur.com/aHdJxXc.png)
### Impact on Oceans
![Oceans](https://i.imgur.com/dJPkYAo.png)
### Ecological Niche
![Niche](https://i.imgur.com/e6pwXlI.png)
- Species have a temperature range in which they can survive
- Plants also have niches (Sunlight, Temperature, Water, Nutrients, CO2, Soil, etc)

## Suggestions 
Feel free to create issues on this github project, or email me at nsporillo@gmail.com
