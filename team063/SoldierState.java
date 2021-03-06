package team063;

public enum SoldierState {
	RUSH_ENEMY_HQ,
	BRUTE_MOVE,
	SMART_MOVE,
	ATTACK_MOVE, // smart formation movement, don't chase
	PATROL, // moving between locations and fight
	SCOUT, // moving to location and avoid enemies
	LAYING_MINES,
	DEFEND_POSITION, // lay mines and attacking enemies who come
	BATTLE, // formation and fight, no moving
	CHASE_AND_DESTROY,
	SEEK_AND_DESTROY_GUERILLA,
	SECURE_ENCAMPMENT, // protecting encampment
	DEFAULT,
}