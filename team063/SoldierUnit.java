package team063;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class SoldierUnit extends BaseUnit {
	private SoldierState state;
	
	private MapLocation targetLoc = myBaseLoc;
	private int squadId;
	private MapLocation curLoc;
	private RobotType encampmentSecureType;
	private int lastSquadMsg;
	private int lastUnitMsg;
	private int lastAllMsg;
	private int lastAllExceptScoutMsg;

	public SoldierUnit(RobotController rc) {
		super(rc);
		squadId = HQUnit.NO_SQUAD;
		state = SoldierState.DEFAULT;
		lastSquadMsg = 0;
		lastUnitMsg = 0;
		lastAllMsg = 0;
		lastAllExceptScoutMsg = 0;
	}
	
	@Override
	public void run() throws GameActionException {
		/**
		 * 1. read broadcasts 2. switch state or squad if necessary 3. act upon
		 * state
		 */
		if (rc.getTeamPower() >= 5 * GameConstants.BROADCAST_READ_COST) {

			if (squadId == HQUnit.NO_SQUAD) {
				squadId = rc.readBroadcast(Util.getInitialSquadNumChannelNum());
			}
			

			
			int squadMsg = rc.readBroadcast(Util.getSquadChannelNum(squadId));

			if (squadMsg != lastSquadMsg && squadMsg != 0) {
				targetLoc = Util.getMapLocationFromMsg(squadMsg);
				state = Util.getSoldierStateFromMsg(squadMsg);
				encampmentSecureType = Util.getEncampmentTypeFromMsg(squadMsg);
				lastSquadMsg = squadMsg;
			}
		
			// read message sent to all squads except scout
			if (squadId != HQUnit.SCOUT_SQUAD) {
				int curMsg = rc.readBroadcast(Util.getAllUnitExceptScoutChannelNum());
				if (curMsg != lastAllExceptScoutMsg && curMsg != 0) {
					targetLoc = Util.getMapLocationFromMsg(curMsg);
					state = Util.getSoldierStateFromMsg(curMsg);
					encampmentSecureType = Util.getEncampmentTypeFromMsg(curMsg);
					lastAllExceptScoutMsg = curMsg;
				}
			}
			// read message sent to everyone
			int msg = rc.readBroadcast(Util.getAllUnitChannelNum());
			if (msg != lastAllMsg && msg != 0) {
				rc.setIndicatorString(0, "round: " + Clock.getRoundNum() + "all unit channel: " + Util.getAllUnitChannelNum() + " msg: " + msg);
				targetLoc = Util.getMapLocationFromMsg(msg);
				state = Util.getSoldierStateFromMsg(msg);
				encampmentSecureType = Util.getEncampmentTypeFromMsg(msg);
				lastAllMsg = msg;
			}

		}
		else {
			state = SoldierState.DEFAULT;
		}

		this.curLoc = rc.getLocation();
		rc.setIndicatorString(2, "cur state: " + state + " cur target: " + targetLoc + " squad: " + squadId);

		switch (state) {

		case BRUTE_MOVE:
			this.goToLocationBrute(this.targetLoc);
			break;
		case SMART_MOVE:
			break;
		case ATTACK_MOVE:
			/*robot will move to a location in loose formation attacking what it runs into and avoiding mines.
			 * it should defuse mines if there are no enemies around
			 */
			
			Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class, 16, otherTeam);
			Robot[] nearbyAllies = rc.senseNearbyGameObjects(Robot.class,9,myTeam);
			Robot[] farAllies;
			if (mapHeight <=30 && mapWidth<=30){
				farAllies = rc.senseNearbyGameObjects(Robot.class,20,myTeam);
			} else if (!(mapHeight >=60) && !(mapWidth>=60)){
				farAllies = rc.senseNearbyGameObjects(Robot.class,36,myTeam);
			} else {
				farAllies = rc.senseNearbyGameObjects(Robot.class,49,myTeam);
			}
			curLoc=rc.getLocation();
			MapLocation[] farMines= {};
			if (rc.hasUpgrade(Upgrade.DEFUSION)){
				farMines= rc.senseNonAlliedMineLocations(curLoc, 14);
			}
			if (rc.isActive()) {
				if (nearbyEnemies.length < 1 && farMines.length >0){
					rc.setIndicatorString(0,"defusing mine");
					rc.defuseMine(farMines[0]);
				} else if (nearbyAllies.length >= 4){
					rc.setIndicatorString(0, "attacking!");
					this.goToLocationBrute(targetLoc);
//					this.goToLocationSmart(targetLoc);

				} else if (farAllies.length >= 4){
					rc.setIndicatorString(0, "regrouping to " + rc.senseRobotInfo(farAllies[0]).location);

					
					this.goToLocationBrute(rc.senseRobotInfo(farAllies[0]).location);
//					this.goToLocationSmart(rc.senseRobotInfo(farAllies[0]).location);

				} else {
					rc.setIndicatorString(0,"no one nearby! retreating home!");
					this.goToLocationBrute(myBaseLoc);
//					this.goToLocationSmart(myBaseLoc);
				}
			}
			break;
		case PATROL:
			break;
		case SCOUT:
			/*robot will move towards a location
			 * it will avoid enemies it encounters and send messages based on what it sees
			 * 
			 * basic implementation for sprint: seeing high numbers of enemy units near their HQ with mines assumes they are nuke rushing
			 * seeing low numbers of enemies near their HQ -AND- high resistance at encampments assumes they are spread out
			 * seeing low numbers of enemies near their HQ -AND- few encounters with enemies elsewhere assumes rush
			 */
			
			Robot[] nearbyEnemies_scouting = rc.senseNearbyGameObjects(
					Robot.class, 16, otherTeam);
			if (nearbyEnemies_scouting.length >= 2) {
				if ((rc.senseNearbyGameObjects(Robot.class, 49, otherTeam)).length >= 8) {
					if (curLoc.distanceSquaredTo(this.enemyBaseLoc) <= 81) {
						// broadcast high enemy presence near their HQ
					}
				} else {
					// broadcast high enemy presence near this robot's current
					// location
				}
				if (rc.isActive()) {
					this.goToLocationBrute(curLoc.subtract(curLoc.directionTo(rc
						.senseRobotInfo(nearbyEnemies_scouting[0]).location)));
				}
				rc.yield();
			} else {
				if (rc.isActive()) {
					this.goToLocationBrute(targetLoc);
				}
				rc.yield();
			}
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
		case SECURE_ENCAMPMENT: // 21 - 900 bytecode
			// assumes robot is close to encampment(targetLoc), go towards
			// encampment
			// capture encampment
			// defend it

			/**
			 * if targetLoc has been captured: defend by laying mines around
			 * encampment and attacking enemies that come by else: if standing
			 * on encampment: capture it else: if mine in the way: defuse it
			 * else: go towards targetLoc
			 */
			
			if (rc.isActive()) {
				if (rc.getLocation().equals(targetLoc)) {
					if (rc.senseCaptureCost() < rc.getTeamPower()) {
						rc.setIndicatorString(1, "capturing encampment");

						rc.captureEncampment(encampmentSecureType);
					} else {
						rc.setIndicatorString(1,
								"not enough power, waiting till next turn to capture");
						rc.yield();
					}
				} else if (rc.canSenseSquare(targetLoc)) {
					GameObject ec = rc.senseObjectAtLocation(targetLoc);
					Robot[] objs = rc.senseNearbyGameObjects(Robot.class, targetLoc, 1, myTeam);
					if (ec == null) {
						rc.setIndicatorString(1,
								"near neutral encampment, moving towards it");
						this.goToLocationBrute(targetLoc);
					}
					else if (ec.getTeam().equals(myTeam) && (rc.senseRobotInfo(objs[0]).type.equals(RobotType.SOLDIER))) {
						rc.setIndicatorString(1, "encampment currently being captured, move towards it");
						this.goToLocationBrute(targetLoc);
					}
					else if (ec.getTeam().equals(myTeam)) {
						rc.setIndicatorString(1,
								"encampment captured, defend it");
						this.defendPosition(targetLoc);
					} else {
						// uh oh
						rc.setIndicatorString(1, "near enemy encampment");
					}
				} else {
					rc.setIndicatorString(1,
							"target out of range, move towards it");
					this.goToLocationBrute(targetLoc);
				}
			} else {
				rc.setIndicatorString(0, "currently not active");
				// do some computation and broadcast
			}

			break;
		case DEFAULT:
			
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

	protected void defendPosition(MapLocation defendPoint)
			throws GameActionException { // 50 - 800 bytecode
		if (rc.isActive()) {
			Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class, 20,
					otherTeam);
			if (nearbyEnemies.length >= 1) {
				if (rc.senseNearbyGameObjects(Robot.class, 4, myTeam).length < 2) {
					rc.setIndicatorString(0, "not enough neraby allies to fight!");
					this.goToLocationBrute(defendPoint);
				} else if (curLoc.distanceSquaredTo(defendPoint) <= 49) {
	
					rc.setIndicatorString(0, "attacking nearby enemy!");
					this.goToLocationBrute(rc.senseRobotInfo(nearbyEnemies[0]).location);
				} else {
					rc.setIndicatorString(0, "enemy is too far away to chase!");
					this.goToLocationBrute(defendPoint);
				}
			} else {
				MapLocation nearbyMine = this.senseAdjacentMine();
				if (nearbyMine != null) {
					// if nearby neutral or enemy mine is found
					rc.setIndicatorString(0, "mine detected at " + nearbyMine.x
							+ " " + nearbyMine.y);
					rc.defuseMine(nearbyMine);
					rc.yield();
				} else if (rc.senseMine(curLoc) == null
						&& (curLoc.x * 2 + curLoc.y) % 5 == 1 && rc.hasUpgrade(Upgrade.PICKAXE)) {
					// standing on patterned empty sq
					rc.setIndicatorString(0, "laying mine");
					rc.layMine();
					rc.yield();
				} else if (rc.senseMine(curLoc) == null
						&& (curLoc.x + curLoc.y) % 2 == 1){
					rc.setIndicatorString(0,"laying mine");
					rc.layMine();
					rc.yield();
				} else if (curLoc.distanceSquaredTo(defendPoint) <= 20) {
					// standing on own mine and within defense radius
					rc.setIndicatorString(0, "moving to defensive formation");
					MapLocation topLeft=new MapLocation(defendPoint.x-2, defendPoint.y-2);
					MapLocation topRight=new MapLocation(defendPoint.x+2,defendPoint.y-2);
					MapLocation bottomLeft=new MapLocation(defendPoint.x-2,defendPoint.y+2);
					MapLocation bottomRight = new MapLocation(defendPoint.x+2,defendPoint.y+2);
					MapLocation[] locationArray={topLeft,topRight,bottomLeft,bottomRight, new MapLocation(defendPoint.x,defendPoint.y+1), new MapLocation(defendPoint.x,defendPoint.y-1), new MapLocation(defendPoint.x-1,defendPoint.y), new MapLocation(defendPoint.x+1,defendPoint.y)};
					Direction randomDir = Direction.values()[(int) (Math.random() * 8)];
					
					for (int index=0;index<=3;index++){
						if (rc.senseObjectAtLocation(locationArray[index])==null && rc.senseMine(locationArray[index])==null){
							this.goToLocationBrute(locationArray[index]);
						} else if (curLoc == locationArray[index]){
							rc.yield();
						} else {
							if (rc.canMove(randomDir) && rc.isActive()){
								rc.move(randomDir);
							}
						}
					}
					
					rc.yield();
				} else {
					// outside defense radius, so move towards defend point
					rc.setIndicatorString(0, "returning to defend point");
					this.goToLocationBrute(defendPoint);
				}
			}
		}
	}

}