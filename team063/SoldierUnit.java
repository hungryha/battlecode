package team063;

import team063.message.Message;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class SoldierUnit extends BaseUnit {
	private SoldierState state;
	private MapLocation targetLoc = myBaseLoc;
	private int squadId;
	private MapLocation curLoc;
	
	public SoldierUnit(RobotController rc) {
		super(rc);
		state = SoldierState.DEFEND_POSITION;
	}

	@Override
	public void run() throws GameActionException {
		/**
		 * 1. read broadcast 2. switch state or squad if necessary 3. act upon
		 * state
		 */
		// readbroadcast(channelNum)
		int unitMsg = rc.readBroadcast(getUnitChannelNum());
		int squadMsg = rc.readBroadcast(getSquadChannelNum());
		int allUnitMsg = rc.readBroadcast(getAllUnitChannelNum());
		this.curLoc = rc.getLocation();

		switch (state) {

		case BRUTE_MOVE:
			this.goToLocationBrute(this.enemyBaseLoc);
			break;
		case SMART_MOVE:
			break;
		case ATTACK_MOVE:
			break;
		case PATROL:
			break;
		case SCOUT:
			break;
		case CAPTURE_MOVE:
			break;
		case DEFEND_POSITION:
			if (rc.isActive()) {
				defendPosition(targetLoc);
			}
			break;
		case BATTLE:
			break;
		case CHASE_AND_DESTROY:
			break;
		case SEEK_AND_DESTROY_GUERILLA:
			break;
		case SECURE_ENCAMPMENT:
			// assumes robot is close to encampment(targetLoc), go towards
			// encampment
			// capture encampment
			// defend it

			/**
			 * if targetLoc has been captured: 
			 * 	defend by laying mines around encampment and attacking enemies that come by 
			 * else: 
			 * 	if standing on encampment: 
			 * 		capture it 
			 * 	else: 
			 * 		if mine in the way: 
			 * 			defuse it
			 * 		else: 
			 * 			go towards targetLoc
			 */
			if (rc.isActive()) {
				RobotController encampController = (RobotController) rc.senseObjectAtLocation(targetLoc);
				if (encampController.getTeam().equals(this.myTeam)) {

				} else if (encampController.getTeam().equals(Team.NEUTRAL)) {
					if (rc.senseCaptureCost() < rc.getTeamPower()) {
						// defend, so do nothing
					} else {
						if (rc.getLocation().equals(targetLoc)) {
							// TODO switch statement of hq instructions
							rc.captureEncampment(RobotType.SUPPLIER);
						} else {
							this.goToLocationBrute(targetLoc);
						}
					}
				} else {
					// uh oh, opponent has encampment
					// attack encampment?
				}
			}
			else {
				// do some computation and broadcast
			}
			break;
		default:
			// do nothing if no instructions from hq
			break;
		}
	}

	/**
	 * modifies squadId modifies state modifies targetLoc
	 */
	@Override
	public void decodeMsg(int encodedMsg) {
		// TODO Auto-generated method stub

	}
	
	protected void defendPosition(MapLocation defendPoint) throws GameActionException{
		Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class, 25, otherTeam);
		if (nearbyEnemies.length >= 1){
			if (rc.senseNearbyGameObjects(Robot.class,4,myTeam).length <2){
				this.goToLocationBrute(defendPoint);
			}
			else if (curLoc.distanceSquaredTo(defendPoint)<=49) {
//				this.goToLocationBrute(((RobotController) nearbyEnemies[0]).getLocation());
			}
			else {
				this.goToLocationBrute(defendPoint);
			}
		} else {
			MapLocation nearbyMine= this.senseAdjacentMine();
			if (nearbyMine != null) {
				// if nearby neutral or enemy mine is found
				rc.setIndicatorString(0,"mine detected at " + nearbyMine.x +" "+ nearbyMine.y);
				rc.defuseMine(nearbyMine);
				rc.yield();
			}
			else if (rc.senseMine(curLoc)==null && (curLoc.x*2 + curLoc.y)%5 ==1) {
				// standing on patterned empty sq
				rc.setIndicatorString(0,"laying mine");
				rc.layMine();
				rc.yield();
			}
			else if (curLoc.distanceSquaredTo(defendPoint)<=25) {
				// standing on own mine and within defense radius
				rc.setIndicatorString(0,"moving randomly");
				Direction randomDir = Direction.values()[(int) (Math.random()*8)];
				if (rc.canMove(randomDir)) {
					rc.move(randomDir);
				}
				rc.yield();
			}
			else {
				// outside defense radius, so move towards defend point
				rc.setIndicatorString(0,"returning to defend point");
				this.goToLocationBrute(defendPoint);
			}
		}
	}
	
}