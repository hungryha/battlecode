package derpbotv3;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Robot;
import battlecode.common.GameConstants;

public class EncampmentUnit extends BaseUnit {
	public EncampmentUnit(RobotController rc) {
		super(rc);
	}

	MapLocation enemyLoc;
	@Override
	public void run() throws GameActionException {
		switch(rc.getType()) {
		case MEDBAY:
			break;
		case SHIELDS:
			break;
		case ARTILLERY:
			if (Clock.getRoundNum() > 500 && Clock.getRoundNum() <= 1000) {
				if (rc.getLocation().distanceSquaredTo(enemyBaseLoc) > 2*63) {
					rc.suicide();
					rc.yield();
				}
			}
			Robot[] nearbyEnemies=rc.senseNearbyGameObjects(Robot.class, 63, otherTeam);
			if (nearbyEnemies.length >= 1){
				for (int i=0; i<nearbyEnemies.length; i++){
					enemyLoc = rc.senseRobotInfo(nearbyEnemies[i]).location;
					if (rc.senseNearbyGameObjects(Robot.class, enemyLoc, 2, myTeam).length <1){
						if (rc.isActive()) {
							rc.attackSquare(enemyLoc);
						}
					}
				}
			}
			break;
		case GENERATOR:
			break;
		case SUPPLIER:
			break;
		}
	}

	@Override
	public void decodeMsg(int encodedMsg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runTest() throws GameActionException {
		// TODO Auto-generated method stub
		
	}
}