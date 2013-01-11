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
							// move outwards from hq
							moveOutwardsFromLocation(rc.senseHQLocation());
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
	
	private static void moveOutwardsFromLocation(MapLocation loc) throws GameActionException {
		Direction locDir = rc.getLocation().directionTo(rc.senseHQLocation());
		int[] offsets = { 2, -2, 1, -1, 0, 3, -3 };
		Direction dir = Direction.values()[(locDir.ordinal() + 4) % 8];
		Direction cur = dir;
		for (int d:offsets) {
			cur = Direction.values()[(dir.ordinal() + d + 8) % 8];
			if (rc.canMove(cur)) {
				break;
			}
		}
		rc.move(cur);
		rc.setIndicatorString(0,"Last direction moved: " + cur.toString());
	}
	
	private static void defendBase() {
		// attacks nearby enemies, otherwise, lays mines around base
		
	}
	
}