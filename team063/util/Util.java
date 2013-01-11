package team063.util;

import battlecode.common.*;

public class Util{
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
	
	private static void goToLocationBrute(MapLocation whereToGo) throws GameActionException {
		int dist = rc.getLocation().distanceSquaredTo(whereToGo);
		if (dist>0&&rc.isActive()){
			Direction dir = rc.getLocation().directionTo(whereToGo);
			int[] directionOffsets = {0,1,-1,2,-2};
			Direction lookingAtCurrently = dir;
			lookAround: for (int d:directionOffsets){
				lookingAtCurrently = Direction.values()[(dir.ordinal()+d+8)%8];
				if(rc.canMove(lookingAtCurrently) && ((rc.senseMine(lookingAtCurrently) == null) || (rc.senseMine(lookingAtCurrently) == myTeam))){
					rc.move(lookingAtCurrently);
					break lookAround;
				} else if (rc.canMove(lookingAtCurrently)){
					rc.defuseMine(lookingAtCurrently);
					break lookAround;
				}
			}
		}
	}
	
	private static void followWaypointPath(MapLocation[] waypointArray, int startIndex) throws GameActionException {
		MapLocation currentLoc = rc.getLocation();
		int waypointCounter = startIndex;
		if (!currentLoc.equals(waypointArray[waypointCounter])){
			goToLocation(waypointArray[waypointCounter]);
		}
		waypointCounter+=1;
	}
}