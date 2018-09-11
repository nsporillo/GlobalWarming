package net.porillo.effect.api;

public enum ClimateEffectType {

	// Returned by ClimateEffect in case @ClimateData is nonexistent
	NONE,

	// Climate Damages
	SEA_LEVEL_RISE,

	ICE_MELT,
	SNOW_MELT,
	ICE_FORMATION,
	SNOW_FORMATION,

	AREA_POTION_CLOUD,

	FARM_YIELD,
	MOB_SPAWN_RATE,

	PERMANENT_SLOWNESS,

	// TODO: Add more
}
