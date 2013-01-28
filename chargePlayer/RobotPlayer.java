package chargePlayer;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class RobotPlayer {
	static Direction enemyBaseDir;
	static RobotController rc;

	public static void run(RobotController myRC) {
		rc = myRC;
		enemyBaseDir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		while (true) {
			try {
				if (myRC.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						if (rc.canMove(enemyBaseDir)) {
							rc.spawn(enemyBaseDir);
						}
					}
				}
				else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						MapLocation curLoc = rc.getLocation();
						Team mineTeam = rc.senseMine(curLoc);
						MapLocation mineLoc = senseAdjacentMine();
						if (mineLoc != null && !rc.senseMine(mineLoc).equals(rc.getTeam())) {
							rc.defuseMine(mineLoc);
						}
						else {
							// move outwards from hq
							goToLocation(rc.senseEnemyHQLocation());
						}
					}
				}
			} catch (Exception e) {
//				e.printStackTrace();
			}
			rc.yield();
		}
	}
	private static void goToLocation(MapLocation whereToGo) throws GameActionException {
		int dist = rc.getLocation().distanceSquaredTo(whereToGo);
		if (dist>0&&rc.isActive()){
			Direction dir = rc.getLocation().directionTo(whereToGo);
			int[] directionOffsets = {0,1,-1,2,-2};
			Direction lookingAtCurrently = dir;
			lookAround: for (int d:directionOffsets){
				lookingAtCurrently = Direction.values()[(dir.ordinal()+d+8)%8];
				if(rc.canMove(lookingAtCurrently)){
					break lookAround;
				}
			}
			rc.move(lookingAtCurrently);
		}
	}
	private static MapLocation senseAdjacentMine() {
		Direction dir = Direction.EAST;
		int[] directionOffsets = { 0, 1, -1, 2, -2, 3, -3, 4};
		MapLocation lookingAtCurrently = rc.getLocation();
		for (int d : directionOffsets) {
			lookingAtCurrently = rc.getLocation().add(Direction.values()[(dir.ordinal() + d + 8) % 8]); //TODO optimize this so it just checks the 8 spaces rather than calling the "add" method to check them
			if (rc.senseMine(lookingAtCurrently) != null) {
				return lookingAtCurrently;
			}
		}
		return null;
	}
}