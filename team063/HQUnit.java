package team063;

import java.util.Comparator;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.Upgrade;

//singleton
public class HQUnit extends BaseUnit {
	public int curSoldiers=0;
	protected int[] unitsMap;
	
	public HQUnit(RobotController rc) {
		super(rc);
		unitsMap = new int[2000];
	}

	@Override
	public void run() throws GameActionException {
		if (Clock.getRoundNum() == 1) {
			int start = Clock.getBytecodeNum();
			System.out.println("start: " + start);
			getTargetEncampments();
			System.out.println("end: " + Clock.getBytecodeNum());
			System.out.println("getSortedEncampments bytecode usage: " + (Clock.getBytecodeNum() - start));
		}
		if (this.rc.isActive()) {

//			if (this.rc.canMove(this.myBaseLoc.directionTo(enemyBaseLoc)) && curSoldiers <=10) {
//				this.rc.spawn(this.myBaseLoc.directionTo(enemyBaseLoc));
//				curSoldiers+=1;
//			}
			
			if (this.rc.canMove(this.myBaseLoc.directionTo(enemyBaseLoc))) {
				rc.spawn(this.myBaseLoc.directionTo(enemyBaseLoc));
			}
		}
	}

	@Override
	public void decodeMsg(int encodedMsg) {
		// TODO Auto-generated method stub
		
	}
	
	public MapLocation[] getTargetEncampments() { // 6300 bytecodes =(
		MapLocation[] encampments = this.rc.senseAllEncampmentSquares();

		// compute distances of encampments to hq location
		// sort by distance
		// take top k
		// TODO use medians of medians?
//		Arrays.sort(encampments, new MapLocationComparator());
	
		int targetRange = Math.min(rc.getMapHeight(), rc.getMapWidth())/2;
		int targetRangeSquared = targetRange * targetRange;
		MapLocation[] targetEncampments = new MapLocation[3];
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
		
			
//		System.out.println("start sorted encampments");
//		System.out.println("myBaseLoc x: " + myBaseLoc.x + " y: " + myBaseLoc.y);
//		for (int i=0; i < targetEncampments.length; i++) {
//			System.out.println("x: " + targetEncampments[i].x + " y: " + targetEncampments[i].y);
//		}
			
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