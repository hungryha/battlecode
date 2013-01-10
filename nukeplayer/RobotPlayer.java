package nukeplayer;

import battlecode.common.*;

public class RobotPlayer {
	private static RobotController rc;
	private static Team myTeam;
	private static Team otherTeam;

	public static void run(RobotController myRC) {
		rc = myRC;
		myTeam = rc.getTeam();
		otherTeam = myTeam.opponent();
		Direction enemyBaseDir = rc.getLocation().directionTo(
				rc.senseEnemyHQLocation());
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					System.out.println("hq robot type");
					if (rc.isActive()) {
						if (Clock.getRoundNum() < 200) {
							// spawn robots
							if (rc.canMove(enemyBaseDir)) {				//TODO make it spawn in other positions if this position isn't open. check for a freespace
								rc.spawn(enemyBaseDir);
							}
						} else {
							rc.researchUpgrade(Upgrade.NUKE);
						}
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					// soldier lays mines around hq
					if (rc.isActive()) {
						MapLocation curLoc = rc.getLocation();
						Team mineTeam = rc.senseMine(curLoc);
						MapLocation mineLoc = senseAdjacentMine();
						if (mineTeam == null) {
							rc.layMine();
						} else if (mineLoc != null && !rc.senseMine(mineLoc).equals(myTeam)) {
							rc.defuseMine(mineLoc);
						}
						else {
							// Choose a random direction, and move that way if
							// possible
							Direction dir = Direction.values()[(int) (Math
									.random() * 8)];
							if (rc.canMove(dir)) {
								rc.move(dir);
								rc.setIndicatorString(
										0,
										"Last direction moved: "
												+ dir.toString());
							}
						}
					}

				} else {
					System.out.println("not hq or soldier");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// End turn
			rc.yield();
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
	
	private static void goToLocation(MapLocation whereToGo)
			throws GameActionException {
		int dist = rc.getLocation().distanceSquaredTo(whereToGo);
		if (dist > 0 && rc.isActive()) {
			Direction dir = rc.getLocation().directionTo(whereToGo);
			int[] directionOffsets = { 0, 1, -1, 2, -2 };
			Direction lookingAtCurrently = dir;
			lookAround: for (int d : directionOffsets) {
				lookingAtCurrently = Direction.values()[(dir.ordinal() + d + 8) % 8];
				if (rc.canMove(lookingAtCurrently)) {
					break lookAround;
				}
			}
			rc.move(lookingAtCurrently);
		}
	}
	
	private static void followWaypointPath(MapLocation[] waypoints){
		
	}
}