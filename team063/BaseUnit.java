package team063;

import java.util.Comparator;

import team063.message.Message;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import battlecode.engine.instrumenter.lang.System;

public abstract class BaseUnit {
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
/*
	protected MapLocation senseAdjacentMine() {
		MapLocation curLoc = rc.getLocation();
		MapLocation[] nearbyLocations = {new MapLocation(curLoc.x-1,curLoc.y-1),new MapLocation(curLoc.x,curLoc.y-1), new MapLocation(curLoc.x+1,curLoc.y-1), new MapLocation(curLoc.x-1,curLoc.y),											
				new MapLocation(curLoc.x+1,curLoc.y), new MapLocation(curLoc.x-1,curLoc.y+1), new MapLocation(curLoc.x,curLoc.y+1), new MapLocation(curLoc.x+1,curLoc.y+1)};
		for (MapLocation lookingAt : nearbyLocations) {
			if (rc.senseMine(lookingAt)!= null){
				return lookingAt;
			}
		}
		if (rc.senseMine(curLoc) != null){
			return curLoc;
		}
		return null;
	}
*/
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