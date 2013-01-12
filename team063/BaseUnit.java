package team063;

import java.util.Comparator;

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
	protected MapLocation enemyBaseLoc;
	protected MapLocation myBaseLoc;
	protected int mapHeight;
	protected int mapWidth;
	
	public BaseUnit(RobotController rc) {
		this.rc = rc;
		this.myTeam = rc.getTeam();
		this.otherTeam = myTeam.opponent();
		this.id = rc.getRobot().getID();
		this.enemyBaseDir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		this.enemyBaseLoc = rc.senseEnemyHQLocation();
		this.myBaseLoc = rc.senseHQLocation();
		this.mapHeight = rc.getMapHeight();
		this.mapWidth = rc.getMapWidth();
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

	abstract public void run() throws GameActionException;

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
	
	public int getAllUnitChannelNum() {
		//TODO make better
		return 0;
	}
	/**
	 * bits 0-6: x coord
	 * bits 7-13: y coord
	 * bits 14-17: soldier state
	 * bits 18-29: extra info??
	 * 	ex) type of encampment for capturing
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
	protected void goToLocationBrute(MapLocation whereToGo) //340 bytecode
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