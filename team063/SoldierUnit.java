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
		state = SoldierState.DEFAULT;
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
		this.goToLocationBrute(this.enemyBaseLoc);
	}

	@Override
	public void decodeMsg(int encodedMsg) {
		// TODO Auto-generated method stub
		
	}
	
}