package team063;


import java.util.Arrays;
import java.util.Comparator;

import team063.message.Message;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

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
	protected int patienceCounter=0;
	protected MapLocation[] prevLocs = new MapLocation[5];
	protected int prevLocsCounter = 0;
	protected boolean haveSeenEnemy = false;
	
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
					Team teamOfMine = rc.senseMine(curLoc.add(lookingAtCurrently));
					if ((teamOfMine == null) || (teamOfMine == myTeam)) {
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
	

	// buggy
	protected void goToLocationCareful(MapLocation dest) throws GameActionException {
		MapLocation curLoc = rc.getLocation();
		
		if (!haveSeenEnemy && rc.senseNearbyGameObjects(
					Robot.class, 16, otherTeam).length > 0) {
			haveSeenEnemy = true;
		}
		
		if (!haveSeenEnemy) {
			for (int i = 0; i < prevLocs.length; i++) {
				if (prevLocs[i] != null && curLoc.equals(prevLocs[i])) {
					goToLocationBrute(curLoc.add(curLoc.directionTo(dest)));
					break;
				}
			}
			prevLocs[prevLocsCounter] = curLoc;
			prevLocsCounter = (prevLocsCounter + 1) % prevLocs.length;
		}
		int dist = curLoc.distanceSquaredTo(dest);
		if (dist > 0 && rc.isActive()) {
			Direction dir = curLoc.directionTo(dest);
			int[] directionOffsets = { 0, 1, -1, 2, -2 };
			Direction lookingAtCurrently = dir;
			//MapLocation[] possibleMovementLocs=new MapLocation[8];
			//int index=0;
			//int minHuer=-1;
			if (patienceCounter<=2){
				for (int d : directionOffsets) {
					lookingAtCurrently = Direction.values()[(dir.ordinal() + d + 8) % 8];
					if (rc.canMove(lookingAtCurrently) && (rc.senseMine(curLoc.add(lookingAtCurrently)) == null || rc
							.senseMine(curLoc.add(lookingAtCurrently)) == myTeam)) {
						//possibleMovementLocs[index]=curLoc.add(lookingAtCurrently);
						//rc.move(lookingAtCurrently);
						patienceCounter-=1;
						if (rc.isActive()) {
							rc.move(lookingAtCurrently);
						}
					}
				}
//				for (int ind=0; ind<8; ind++){
//					if (possibleMovementLocs[ind]!=null){
//						if (minHuer>=0){
//							minHuer=Math.min(possibleMovementLocs[ind].distanceSquaredTo(dest), minHuer);
//						} else {
//							minHuer=ind;
//						}
//					}
//				}
//				if (minHuer>=0){
//					rc.setIndicatorString(1, "possibleMovementLocs: "+ Arrays.toString(possibleMovementLocs) +"best location: ("+possibleMovementLocs[minHuer].x+","+possibleMovementLocs[minHuer].y+")");
//					rc.move(curLoc.directionTo(possibleMovementLocs[minHuer]));
//				}
				patienceCounter+=1;
			} else {
				for (int j: directionOffsets){
					lookingAtCurrently = Direction.values()[(dir.ordinal() + j + 8) % 8];
					if (rc.canMove(lookingAtCurrently) && (rc.senseMine(curLoc.add(lookingAtCurrently)) == null || rc
							.senseMine(curLoc.add(lookingAtCurrently)) == myTeam)) {
						if (rc.isActive()) {
							rc.move(lookingAtCurrently);
						}
						break;
				 	} else if (rc.canMove(lookingAtCurrently) && rc.isActive()){
				 		rc.defuseMine(curLoc.add(lookingAtCurrently));
				 	}	
				}
				patienceCounter=0;
			}
			
		}
	}
	
//	protected void updateMineMap() {
//		int start = Clock.getBytecodeNum();
//		MapLocation[] mines = rc.senseNonAlliedMineLocations(new MapLocation(mapWidth/2, mapHeight/2), 2500);
//		for (int i=0; i < mines.length; i++) {
//			mineMap[mines[i].x][mines[i].y] = 1;
//		}
//		System.out.println("mine map used: " + (Clock.getBytecodeNum() - start));
//	}
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