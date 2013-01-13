package team063;

import battlecode.common.RobotController;

public class EncampmentUnit extends BaseUnit {
	public EncampmentUnit(RobotController rc) {
		super(rc);
	}

	@Override
	public void run() {
		switch(rc.getType()) {
		case MEDBAY:
			break;
		case SHIELDS:
			break;
		case ARTILLERY:
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
}