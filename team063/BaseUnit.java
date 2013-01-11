package team063;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public abstract class BaseUnit {
	private RobotController rc;
	public BaseUnit(RobotController rc) {
		this.rc = rc;
	}
	
	public void loop() {
		while (true) {
			try {
				this.run();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	abstract public void run();
	
	
	protected void goToLocationBrute(MapLocation whereToGo) throws GameActionException {
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
	
	protected void followWaypointPath(MapLocation[] waypointArray, int startIndex) throws GameActionException {
		MapLocation currentLoc = rc.getLocation();
		int waypointCounter = startIndex;
		if (!currentLoc.equals(waypointArray[waypointCounter])){
			goToLocation(waypointArray[waypointCounter]);
		}
		waypointCounter+=1;
	}
}