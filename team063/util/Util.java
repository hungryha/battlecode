package team063.util;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Util {
	
	public static MapLocation senseAdjacentMine(RobotController rc) {
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
	
	private static void goToLocation(MapLocation whereToGo, RobotController rc) throws GameActionException {
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
}