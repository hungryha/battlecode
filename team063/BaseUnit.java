package team063;

import java.util.Comparator;

import team063.message.Message;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public abstract class BaseUnit {
	//masks for encoding
	public static final int X_COORD_MASK = 127; //0b1111111
	public static final int Y_COORD_MASK = 127;
	public static final int SOLDIER_STATE_MASK = 15;
	public static final int ENCAMPMENT_TYPE_MASK = 7;
	public static final int CHECK_SUM_MASK = 7;
	
	public static final int X_COORD_SHIFT = 0;
	public static final int Y_COORD_SHIFT = 7;
	public static final int SOLDIER_STATE_SHIFT = 14;
	public static final int ENCAMPMENT_TYPE_SHIFT = 18;
	public static final int CHECK_SUM_SHIFT = 29;
	
	
	protected RobotController rc;
	protected Team myTeam;
	protected Team otherTeam;
	protected int id;
	protected int squadId;
	protected MapLocation enemyBaseLoc;
	protected MapLocation myBaseLoc;
	protected int mapHeight;
	protected int mapWidth;
	
	public BaseUnit(RobotController rc) {
		this.rc = rc;
		this.myTeam = rc.getTeam();
		this.otherTeam = myTeam.opponent();
		this.id = rc.getRobot().getID();
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
		int id = Clock.getRoundNum()*151 % 65081;
		return id;
	}
	
	public int getSquadChannelNum() {
		int squadId=Clock.getRoundNum()*599 % 65291;
		return squadId;
	}
	
	public int getAllUnitChannelNum() {
		return 53401;
	}
	/**
	 * bits 0-6: x coord
	 * bits 7-13: y coord
	 * bits 14-17: soldier state
	 * bits 18-28: extra info??
	 * 	ex) type of encampment for capturing
	 * bits 29-31: checksum
	 */
	public int encodeMsg(MapLocation loc, SoldierState state, RobotType encampmentType, int otherInfo) {
		return (loc.x << X_COORD_SHIFT) | 
				(loc.y << Y_COORD_SHIFT) | 
				(state.ordinal() << SOLDIER_STATE_SHIFT) |
				(encampmentType.ordinal() << ENCAMPMENT_TYPE_SHIFT);
	}
	
	public MapLocation getMapLocationFromMsg(int encodedMsg) {
		int xcoord = (encodedMsg & (X_COORD_MASK << X_COORD_SHIFT)) >> X_COORD_SHIFT;
		int ycoord = (encodedMsg & (Y_COORD_MASK << Y_COORD_SHIFT)) >> Y_COORD_SHIFT;
		MapLocation loc = new MapLocation(xcoord, ycoord);
		return loc;
	}
	
	public SoldierState getSoldierStateFromMsg(int encodedMsg) {
		int index = (encodedMsg & (SOLDIER_STATE_MASK << SOLDIER_STATE_SHIFT)) >> SOLDIER_STATE_SHIFT;
		return SoldierState.values()[index];
	}
	
	public RobotType getEncampmentTypeFromMsg(int encodedMsg) {
		int index = (encodedMsg & (ENCAMPMENT_TYPE_MASK << ENCAMPMENT_TYPE_SHIFT)) >> ENCAMPMENT_TYPE_SHIFT;
		return RobotType.values()[index];
	}
	
	public int getOtherInfoFromMsg(int encodedMsg) {
		//TODO implement
		return 0;
	}
	abstract public void decodeMsg(int encodedMsg);

	// returns location of adjacent mine, or null if no adjacentneutral or enemy mine nearby
	protected MapLocation senseAdjacentMine() {
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