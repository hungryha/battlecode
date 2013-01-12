package team063;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

//singleton
public class HQUnit extends BaseUnit {
	
	public HQUnit(RobotController rc) {
		super(rc);
	}

	@Override
	public void run() throws GameActionException {
		if (this.rc.isActive()) {
			if (this.rc.canMove(this.enemyBaseDir)) {
				this.rc.spawn(this.enemyBaseDir);
			}
		}
	}

	@Override
	public void decodeMsg(int encodedMsg) {
		// TODO Auto-generated method stub
		
	}
	
}