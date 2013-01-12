package team063;

import team063.message.Message;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class SoldierUnit extends BaseUnit {
	private SoldierState state;
	private MapLocation targetLoc;
	private int squadId;

	public SoldierUnit(RobotController rc) {
		super(rc);
		state = SoldierState.BRUTE_MOVE;
		targetLoc = null;
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

	public void defendTarget(MapLocation target) {

	}

}