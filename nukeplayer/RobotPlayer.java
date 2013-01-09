package nukeplayer;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Upgrade;

public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					System.out.println("hq robot type");
					if (rc.isActive()) {
						rc.researchUpgrade(Upgrade.NUKE);
					}
				}
				else if (rc.getType() == RobotType.SOLDIER) {
					System.out.println("soldier");
				}
				else {
					System.out.println("not hq or soldier");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// End turn
			rc.yield();
		}
	}
}