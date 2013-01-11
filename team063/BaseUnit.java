package team063;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public abstract class BaseUnit {
	private RobotController rc;
	private Team myTeam;
	private Team otherTeam;
	Direction enemyBaseDir = rc.getLocation().directionTo(
			rc.senseEnemyHQLocation());
	public BaseUnit(RobotController rc) {
		this.rc = rc;
		this.myTeam=rc.getTeam();
		this.otherTeam=myTeam.opponent();
		
	}
	
	public void loop() {
		while (true) {
			try {
				run();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			rc.yield();
		}		
	}
	
	abstract public void run();
	
	
	protected void goToLocationBrute(MapLocation whereToGo) throws GameActionException {
		MapLocation curLoc= rc.getLocation();
		int dist = curLoc.distanceSquaredTo(whereToGo);
		if (dist>0&&rc.isActive()){
			Direction dir = curLoc.directionTo(whereToGo);
			int[] directionOffsets = {0,1,-1,2,-2};
			Direction lookingAtCurrently = dir;
			lookAround: for (int d:directionOffsets){
				lookingAtCurrently = Direction.values()[(dir.ordinal()+d+8)%8];
				if(rc.canMove(lookingAtCurrently) && ((rc.senseMine(curLoc.add(lookingAtCurrently)) == null) || (rc.senseMine(curLoc.add(lookingAtCurrently)) == myTeam))){
					rc.move(lookingAtCurrently);
					break lookAround;
				} else if (rc.canMove(lookingAtCurrently)){
					rc.defuseMine(curLoc.add(lookingAtCurrently));
					break lookAround;
				}
			}
		}
	}
	
	protected void followWaypointPath(MapLocation[] waypointArray, int startIndex) throws GameActionException {
		MapLocation currentLoc = rc.getLocation();
		int waypointCounter = startIndex;
		if (!currentLoc.equals(waypointArray[waypointCounter])){
			goToLocationBrute(waypointArray[waypointCounter]);
		}
		waypointCounter+=1;
	}
}