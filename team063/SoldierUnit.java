package team063;

import team063.message.Message;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

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
		 * 1. read broadcast
		 * 2. switch state of necessary
		 * 3. act upon state
		 */
		//readbroadcast(channelNum)
		int unitMsg = rc.readBroadcast(getUnitChannelNum());
		int squadMsg = rc.readBroadcast(getSquadChannelNum());
		int allUnitMsg = rc.readBroadcast(getAllUnitChannelNum());
		switch(state) {
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
			break;
		default:
			//do nothing if no instructions from hq
			break;
		}
	}

	@Override
	public void decodeMsg(int encodedMsg) {
		// TODO Auto-generated method stub
		
	}
	
}