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
					if (rc.isActive()) {
						if (Clock.getRoundNum() < 130) {
							if (!rc.hasUpgrade(Upgrade.PICKAXE)) {
								rc.researchUpgrade(Upgrade.PICKAXE);
							}
							// spawn robots
							if (rc.canMove(enemyBaseDir)) {				
								if (rc.isActive()) {
									rc.spawn(enemyBaseDir);
								}
							}
						} else {
							rc.researchUpgrade(Upgrade.NUKE);
						}
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					// soldier lays mines around hq
					if (rc.isActive()) {
						defendPosition(rc.senseHQLocation());
					}

				} else {
					System.out.println("not hq or soldier");
				}

			} catch (Exception e) {
//				e.printStackTrace();
			}
			// End turn
			rc.yield();
		}
	}


	
	protected static void defendPosition(MapLocation defendPoint)
			throws GameActionException { // 50 - 800 bytecode
		Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class, 25,
				otherTeam);
		if (nearbyEnemies.length >= 1) {
			if (rc.senseNearbyGameObjects(Robot.class, 4, myTeam).length < 2) {
				rc.setIndicatorString(0, "not enough neraby allies to fight!");
				goToLocationBrute(defendPoint);
			} else if (rc.getLocation().distanceSquaredTo(defendPoint) <= 49) {

				rc.setIndicatorString(0, "attacking nearby enemy!");
				goToLocationBrute(rc.senseRobotInfo(nearbyEnemies[0]).location);
			} else {
				rc.setIndicatorString(0, "enemy is too far away to chase!");
				goToLocationBrute(defendPoint);
			}
		} else {
			MapLocation nearbyMine = senseAdjacentMine();
			if (nearbyMine != null) {
				// if nearby neutral or enemy mine is found
				rc.setIndicatorString(0, "mine detected at " + nearbyMine.x
						+ " " + nearbyMine.y);
				rc.defuseMine(nearbyMine);
				rc.yield();
			} else if (rc.senseMine(rc.getLocation()) == null
					&& (rc.getLocation().x * 2 + rc.getLocation().y) % 5 == 1) {
				// standing on patterned empty sq
				rc.setIndicatorString(0, "laying mine");
				rc.layMine();
				rc.yield();
			} else if (rc.getLocation().distanceSquaredTo(defendPoint) <= 25) {
				// standing on own mine and within defense radius
				rc.setIndicatorString(0, "moving randomly");
				Direction randomDir = Direction.values()[(int) (Math.random() * 8)];
				if (rc.canMove(randomDir)) {
					rc.move(randomDir);
				}
				rc.yield();
			} else {
				// outside defense radius, so move towards defend point
				rc.setIndicatorString(0, "returning to defend point");
				goToLocationBrute(defendPoint);
			}
		}
	}
	
	protected static void goToLocationBrute(MapLocation whereToGo) //340 bytecode
			throws GameActionException {
		MapLocation curLoc = rc.getLocation();
		int dist = curLoc.distanceSquaredTo(whereToGo);
		if (dist > 0 && rc.isActive()) {
			Direction dir = curLoc.directionTo(whereToGo);
			int[] directionOffsets = { 0, 1, -1, 2, -2 };
			Direction lookingAtCurrently = dir;
			for (int d : directionOffsets) {
				lookingAtCurrently = Direction.values()[(dir.ordinal() + d + 8) % 8];
				if (rc.canMove(lookingAtCurrently)) {
					if ((rc.senseMine(curLoc.add(lookingAtCurrently)) == null) || (rc
								.senseMine(curLoc.add(lookingAtCurrently)) == myTeam)) {
						rc.move(lookingAtCurrently);
					}
					else {
						rc.defuseMine(curLoc.add(lookingAtCurrently));
					}
					break;
				}
			}
		}
	}
	
	// returns location of adjacent mine, or null if no adjacentneutral or enemy mine nearby
	protected static MapLocation senseAdjacentMine() {
		Direction dir = Direction.NORTH;
		int[] directionOffsets = { 0, 1, -1, 2, -2, 3, -3, 4 };
		MapLocation curLoc = rc.getLocation();
		MapLocation lookingAtCurrently = curLoc;
		for (int d : directionOffsets) {
			lookingAtCurrently = rc.getLocation().add(Direction.values()[(dir.ordinal() + d + 8) % 8]); //TODO optimize this so it just checks the 8 spaces rather than calling the "add" method to check them
			Team mineTeam = rc.senseMine(lookingAtCurrently);
			if (mineTeam != null && !(mineTeam.equals(myTeam))) {
				return lookingAtCurrently;
			}
		}
		
		Team curLocMineTeam = rc.senseMine(curLoc);
		if (curLocMineTeam != null && !(curLocMineTeam.equals(myTeam))) {
			return curLoc;
		}
		return null;
	}
}