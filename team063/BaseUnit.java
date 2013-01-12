package team063;

import team063.message.Message;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public abstract class BaseUnit {
	protected RobotController rc;
	protected Team myTeam;
	protected Team otherTeam;
	protected int id;
	protected int squadId;
	protected Direction enemyBaseDir;

	public BaseUnit(RobotController rc) {
		this.rc = rc;
		this.myTeam = rc.getTeam();
		this.otherTeam = myTeam.opponent();
		this.id = rc.getRobot().getID();
		this.enemyBaseDir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
	}

	public void loop() {
		while (true) {
			try {
				run();
			} catch (Exception e) {
				e.printStackTrace();
			}
			rc.yield();
		}
	}

	abstract public void run();

	/**
	 * Message/broadcast methods
	 */
	public int getUnitChannelNum() {
		//TODO make better
		return id;
	}
	
	public int getSquadChannelNum() {
		//TODO make better
		return squadId;
	}
	
	/**
	 * bits 0-7: x coord
	 * bits 8-14: y coord
	 * bits 15-18: soldier state
	 * bits 19-29: extra info??
	 * bits 30-32: checksum
	 */
	public int encodeMsg(MapLocation loc, SoldierState state, int squadId, int unitId) {
		return 0;
	}
	
	abstract public void decodeMsg(int encodedMsg);
	
	/**
	 * head straight towards target, defuse mines in the way
	 * @param whereToGo
	 * @throws GameActionException
	 */
	protected void goToLocationBrute(MapLocation whereToGo)
			throws GameActionException {
		MapLocation curLoc = rc.getLocation();
		int dist = curLoc.distanceSquaredTo(whereToGo);
		if (dist > 0 && rc.isActive()) {
			Direction dir = curLoc.directionTo(whereToGo);
			int[] directionOffsets = { 0, 1, -1, 2, -2 };
			Direction lookingAtCurrently = dir;
			lookAround: for (int d : directionOffsets) {
				lookingAtCurrently = Direction.values()[(dir.ordinal() + d + 8) % 8];
				if (rc.canMove(lookingAtCurrently)
						&& ((rc.senseMine(curLoc.add(lookingAtCurrently)) == null) || (rc
								.senseMine(curLoc.add(lookingAtCurrently)) == myTeam))) {
					rc.move(lookingAtCurrently);
					break lookAround;
				} else if (rc.canMove(lookingAtCurrently)) {
					rc.defuseMine(curLoc.add(lookingAtCurrently));
					break lookAround;
				}
			}
		}
	}

	protected void followWaypointPath(MapLocation[] waypointArray,
			int startIndex) throws GameActionException {
		MapLocation currentLoc = rc.getLocation();
		int waypointCounter = startIndex;
		if (!currentLoc.equals(waypointArray[waypointCounter])) {
			goToLocationBrute(waypointArray[waypointCounter]);
		}
		waypointCounter += 1;
	}
}