package derpbotv3;

import battlecode.common.RobotController;


public class RobotPlayer {
	public static void run(RobotController myRC) {
		BaseUnit unit;
		switch(myRC.getType()) {
		case HQ:
			unit = new HQUnit(myRC);
			break;
		case SOLDIER:
			unit = new SoldierUnit(myRC);
			break;
		default:
			unit = new EncampmentUnit(myRC);
			break;
		}
		unit.loop();
	}
}