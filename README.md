# GlobalWarming
Minecraft Server Java Edition (Spigot) plugin which adds game changing climate change mechanics.

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
+ Players can purchase "carbon offsets" which creates a tree-planting bounty for other players to furfill. 

## Challenges
+ Currently, a tree growth will instantly reduce CO2 levels so players can commercially farm trees while still reducing emissions. This is not ideal. Keeping track of all planted trees is an expensive operation, so some ingenuity is needed here.

## (Planned) Features
+ Scoreboard Integration - Players can compete for carbon neutrality! The worst polluters can be shamed.
+ Economy Integration - Carbon Offsetting using your in-game currency! Set up tree-planting bountys 
+ Carbon Scorecard - Each player can see their latest carbon footprint trends via command line.
+ Multi-world - You can experiment with this plugin on one of your worlds to test it out!
+ Custom Models - The inner numerical workings are configurable as possible. Set thresholds, probabilities, and distributions.
+ Database storage - Load data on startup, queue DB changes to be done async and at intervals (instead of as they happen, that'd kill performance), and empty queue on shutdown. 
+ Highly configurable - Almost everything will have some degree of configuration to suit your servers needs.
+ Efficient - Despite major mechanics changes and an extensive event listening setup, I plan to optimize this plugin to be suitable for up to medium to large servers. 

## Roadmap
+ Education Edition support
+ Incorporate Methane, since CO2 is not the only greenhouse gas that matters.

## Science and Economics of Climate Change
+ Atmosphere warms with more CO2 because of Greenhouse Effect
![Greenhouse Effect](https://i.imgur.com/XsWJGz9.png)
+ ![Earth's Global Energy Budget](https://i.imgur.com/aHdJxXc.png)
+ [Acid Rain](https://en.wikipedia.org/wiki/Acid_rain)
+ Oceans rise b/c water expands when heated and glaciers are melting
![Oceans](https://i.imgur.com/dJPkYAo.png)

## Negative Climate Damages (Sample Model) - 
- Higher temps inherit the damages from the lower temps
+ 14.0 C - No effects [Baseline]
+ 14.5 C - | Minor changes | 
+ 15.0 C - | Localized Acid Rain | Some mobs spawn less | Some mobs spawn more |
+ 16.0 C - | +1 Sea Level Rise | Tropical fish die |
+ 17.0 C - |Global Acid Rain | Some trees no longer grow | +1 Sea Level Rise |
+ 18.0 C - |Noxious Area Potion Effect Clouds | Farm yields decrease | +2 Sea Level Rise | All Snow/Ice melts |
+ 19.0 C - | All fish die |
+ 20.0 C - "Devastation".. Highly polluted chunks get permanent severe area potion effects, forest fires, etc

## Suggestions 
Feel free to create issues on this github project, or email me at nsporillo@gmail.com

I'm still working on establishing a stable default model that doesnt implode servers a day after they install. The negative effects need to be burdensome but not devastating right off the bat, and it's difficult to project what will happpen. Please let me know if you have any thoughts

## Contributing 
Always welcome to fork this repository and submit pull requests!

I will need at least a medium size server to test this plugin on once I've completed features and testing! Performance is important to me, and I can only identify the problematic code sections with the help of server owners. 
