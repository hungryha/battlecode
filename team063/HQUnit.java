package team063;

import java.util.Comparator;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Upgrade;

//singleton
public class HQUnit extends BaseUnit {
	public int curSoldiers=0;
	protected int[] unitsMap;
	protected MapLocation[] initialTargetEncampments;
	private int encampCounter;
	public HQUnit(RobotController rc) {
		super(rc);
		this.unitsMap = new int[2000];
		this.encampCounter = 0;
		int start = Clock.getBytecodeNum();
		this.initialTargetEncampments = getTargetEncampments();
		System.out.println("getTargetEncampments bytecode usage: " + (Clock.getBytecodeNum() - start));
	}

	@Override
	public void run() throws GameActionException {
		if (this.rc.isActive()) {
			if (this.rc.canMove(this.myBaseLoc.directionTo(enemyBaseLoc))) {
				rc.spawn(this.myBaseLoc.directionTo(enemyBaseLoc));
			}
		}
		
		if (Clock.getRoundNum() < 70) {
			// broadcast
			GameObject[] myUnits = rc.senseNearbyGameObjects(Robot.class, 1000, myTeam);
			RobotType encamp = RobotType.SUPPLIER;

			if (rc.getTeamPower() > 5) {
				if (myUnits.length <= 2) {
					encampCounter = 0;
					encamp = RobotType.SUPPLIER;
				}
				else if (myUnits.length <= 4) {
					encampCounter = 1;
					encamp = RobotType.SUPPLIER;
				}
				else {
					encampCounter = 2;
					encamp = RobotType.GENERATOR;
				}
				int msg = this.encodeMsg(initialTargetEncampments[encampCounter], SoldierState.SECURE_ENCAMPMENT, encamp, 0);

				rc.broadcast(1, msg);
				rc.broadcast(2, msg);
				rc.broadcast(3, msg);
			}
		}

	}

	@Override
	public void decodeMsg(int encodedMsg) {
		// TODO Auto-generated method stub
		
	}
	

	public MapLocation[] getTargetEncampments() {
		MapLocation[] encampments = this.rc.senseAllEncampmentSquares();

		// compute distances of encampments to hq location
		// sort by distance
		// take top k
		// TODO use median of medians?
//		Arrays.sort(encampments, new MapLocationComparator()); // 6300 bytecodes
	
//		int targetRange = Math.max(rc.getMapHeight(), rc.getMapWidth())/2;
//		int targetRangeSquared = targetRange * targetRange;
		MapLocation[] targetEncampments = new MapLocation[3];

/*		
		int j = 0;
		if (myBaseLoc.x <= rc.getMapWidth()/2 || myBaseLoc.y <= rc.getMapHeight()/2) {
			for (int i=0; i < encampments.length; i++) {
				if (encampments[i].distanceSquaredTo(myBaseLoc) < targetRangeSquared) {
					targetEncampments[j] = encampments[i];
					j++;
					if (j == targetEncampments.length) {
						break;
					}
				}
			}
		}
		else {
			for (int i=encampments.length-1; i>=0; i--) {
				if (encampments[i].distanceSquaredTo(myBaseLoc) < targetRangeSquared) {
					targetEncampments[j] = encampments[i];
					j++;
					if (j == targetEncampments.length) {
						break;
					}
				}
			}
		}
*/		
		int[] targetDists = {1000, 1000, 1000};
		for (int i=0; i < encampments.length; i++) {
			int dist = myBaseLoc.distanceSquaredTo(encampments[i]);
			int largestIndex = 0;
			for (int k = 1; k < 3; k++) {
				if (targetDists[k] > targetDists[largestIndex]) {
					largestIndex = k;
				}
			}
			if (targetEncampments[largestIndex] == null || targetDists[largestIndex] == 0 || dist < targetDists[largestIndex]) {
				targetDists[largestIndex] = dist;
				targetEncampments[largestIndex] = encampments[i];
			}
		}
/*
		System.out.println("start sorted encampments");
		System.out.println("myBaseLoc x: " + myBaseLoc.x + " y: " + myBaseLoc.y);
		for (int i=0; i < targetEncampments.length; i++) {
			System.out.println("x: " + targetEncampments[i].x + " y: " + targetEncampments[i].y);
		}
*/
		return targetEncampments;
	}
	
	public class MapLocationComparator implements Comparator {

		@Override
		public int compare(Object loc0, Object loc1) {
			// TODO Auto-generated method stub
			return ((MapLocation) loc0).distanceSquaredTo(myBaseLoc) - ((MapLocation) loc1).distanceSquaredTo(myBaseLoc);
		}

		
	}
	
}