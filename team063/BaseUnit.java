package team063;

import battlecode.common.RobotController;

public abstract class BaseUnit {
	private RobotController rc;
	public BaseUnit(RobotController rc) {
		this.rc = rc;
	}
	
	public void loop() {
		while (true) {
			try {
				this.run();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	abstract public void run();
}