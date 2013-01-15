package team063;

import java.util.Comparator;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Upgrade;

//singleton
public class HQUnit extends BaseUnit {
	// squad consts
	public static int SQUAD_ASSIGNMENT_CHANNEL = 7907;

	public static int NO_SQUAD = 0;
	public static int SCOUT_SQUAD = 1;
	public static int ENCAMPMENT_SQUAD_1 = 2;
	public static int ENCAMPMENT_SQUAD_2 = 3;
	public static int ENCAMPMENT_SQUAD_3 = 4;
	public static int DEFEND_BASE_SQUAD = 5;
	public static int ATTACK_SQUAD = 6;

	public int unitsCount = 0;

	protected int[] unitsMap;
	protected int[] squads;
	protected MapLocation[] initialTargetEncampments;
	private int encampCounter;

	public HQUnit(RobotController rc) {
		super(rc);
		this.unitsMap = new int[2000];
		this.encampCounter = 0;
		int start = Clock.getBytecodeNum();
		this.initialTargetEncampments = getTargetEncampments();
		System.out.println("getTargetEncampments bytecode usage: "
				+ (Clock.getBytecodeNum() - start));
	}

	@Override
	public void run() throws GameActionException {

		// if (mapHeight > 65 && mapWidth > 65) {
		// // big map, nuke strategy
		// int msg = this.encodeMsg(
		// myBaseLoc,
		// SoldierState.DEFEND_POSITION, RobotType.HQ, 0);
		// rc.broadcast(this.getAllUnitChannelNum(), msg);
		// if (rc.isActive()) {
		// if (Clock.getRoundNum() < 200) {
		// // spawn robots
		// this.spawnInAvailable();
		// } else {
		// rc.researchUpgrade(Upgrade.NUKE);
		//
		// rc.broadcast(this.getAllUnitChannelNum(), this.encodeMsg(
		// myBaseLoc,
		// SoldierState.DEFEND_POSITION, RobotType.HQ, 0));
		// }
		// }
		// }
		// if (mapHeight <= 30 && mapWidth <= 30) {
		// // small map, rush strategy
		// // build a shield encamp, rally there until 130, then rush
		// if (Clock.getRoundNum() < 100) {
		// rc.broadcast(Util.getAllUnitChannelNum(),
		// Util.encodeMsg(initialTargetEncampments[0],
		// SoldierState.SECURE_ENCAMPMENT, RobotType.ARTILLERY, 0));
		// }
		// else {
		// rc.broadcast(Util.getAllUnitChannelNum(),
		// Util.encodeMsg(enemyBaseLoc, SoldierState.ATTACK_MOVE, RobotType.HQ,
		// 0));
		// }
		// if (rc.isActive()) {
		// this.spawnInAvailable();
		// }
		// }
		// else {
		{
			if (rc.getTeamPower() >= GameConstants.BROADCAST_SEND_COST) {
				rc.broadcast(Util.getInitialSquadNumChannelNum(),
						getCurrentSquadAssignment());

				if (Clock.getRoundNum() < 70) {
					// broadcast
					GameObject[] myUnits = rc.senseNearbyGameObjects(Robot.class,
							1000, myTeam);
					RobotType encamp = RobotType.SUPPLIER;

					if (rc.getTeamPower() > 5) {
						if (myUnits.length <= 3) {
							encampCounter = 0;
							encamp = RobotType.SUPPLIER;
							rc.broadcast(Util.getSquadChannelNum(ENCAMPMENT_SQUAD_1), Util.encodeMsg(
									initialTargetEncampments[encampCounter],
									SoldierState.SECURE_ENCAMPMENT, encamp, 0));

						} else if (myUnits.length <= 5) {
							encampCounter = 1;
							encamp = RobotType.SUPPLIER;
							rc.broadcast(Util.getSquadChannelNum(ENCAMPMENT_SQUAD_2), Util.encodeMsg(
									initialTargetEncampments[encampCounter],
									SoldierState.SECURE_ENCAMPMENT, encamp, 0));
						} else if (myUnits.length <= 7){
							encampCounter = 2;
							encamp = RobotType.SHIELDS;
							encamp = RobotType.SUPPLIER;
							rc.broadcast(Util.getSquadChannelNum(ENCAMPMENT_SQUAD_3), Util.encodeMsg(
									initialTargetEncampments[encampCounter],
									SoldierState.SECURE_ENCAMPMENT, encamp, 0));
						}
						else {
							rc.broadcast(Util.getSquadChannelNum(DEFEND_BASE_SQUAD), 
										Util.encodeMsg(myBaseLoc, SoldierState.DEFEND_POSITION, RobotType.HQ, 0));
						}
//						int msg = Util.encodeMsg(
//								initialTargetEncampments[encampCounter],
//								SoldierState.SECURE_ENCAMPMENT, encamp, 0);

//						rc.broadcast(Util.getAllUnitChannelNum(), msg);
						rc.broadcast(Util.getSquadChannelNum(SCOUT_SQUAD), Util
								.encodeMsg(enemyBaseLoc, SoldierState.SCOUT,
										RobotType.HQ, 0));
						if (rc.isActive()) {
							this.spawnInAvailable();
						}
					}
				}
				
				// if (Clock.getRoundNum() > 70
				// && !rc.hasUpgrade(Upgrade.DEFUSION)) {
				// rc.setIndicatorString(0, "researching DEFUSION");
				// rc.researchUpgrade(Upgrade.DEFUSION);
				// } else

				else if (Clock.getRoundNum() >= 70 && Clock.getRoundNum() <= 200) {
					rc.setIndicatorString(
							1,
							"round: "
									+ Clock.getRoundNum()
									+ " rally at shields, writing to channel: "
									+ Util.getAllUnitChannelNum()
									+ " msg: "
									+ Util.encodeMsg(
											initialTargetEncampments[2],
											SoldierState.BRUTE_MOVE,
											RobotType.SHIELDS, 0));

					rc.broadcast(Util.getAllUnitChannelNum(), Util.encodeMsg(
							initialTargetEncampments[2],
							SoldierState.BRUTE_MOVE, RobotType.SHIELDS, 0));
					if (!rc.hasUpgrade(Upgrade.FUSION)) {

						rc.setIndicatorString(0,
								"researching FUSION, telling units to rally at shields");
						rc.researchUpgrade(Upgrade.FUSION);
					} else {
						if (rc.isActive()) {
							this.spawnInAvailable();
						}
					}

				}
				/*
				 * else if (Clock.getRoundNum() <= 200) {
				 * rc.setIndicatorString(1, "rally at shields");
				 * rc.broadcast(Util.getAllUnitChannelNum(), Util
				 * .encodeMsg(initialTargetEncampments[2],
				 * SoldierState.BRUTE_MOVE, RobotType.SHIELDS, 0)); if
				 * (rc.isActive()) { this.spawnInAvailable(); } }
				 */
				else if (Clock.getRoundNum() > 200
						&& Clock.getRoundNum() <= 1000) {

					rc.setIndicatorString(0,
							"sending attack move msg and spawning");
					rc.broadcast(Util.getAllUnitChannelNum(), Util.encodeMsg(
							enemyBaseLoc, SoldierState.ATTACK_MOVE,
							RobotType.HQ, 0));
					if (rc.isActive()) {
						this.spawnInAvailable();
					}

				} else if (Clock.getRoundNum() > 1000) {

					rc.setIndicatorString(0,
							"researching nuke, sending defend base msg");
					rc.researchUpgrade(Upgrade.NUKE);
					rc.broadcast(Util.getAllUnitChannelNum(), Util.encodeMsg(
							myBaseLoc, SoldierState.DEFEND_POSITION,
							RobotType.HQ, 0));

				} else {
					rc.setIndicatorString(0, "spawning in available space");
					if (rc.isActive()) {
						this.spawnInAvailable();
					}
				}
			}


		}
	}

	public int getCurrentSquadAssignment() {
		if (unitsCount < 1) {
			return SCOUT_SQUAD;
		} else if (unitsCount <= 3) {
			return ENCAMPMENT_SQUAD_1;
		} else if (unitsCount <= 5) {
			return ENCAMPMENT_SQUAD_2;
		} else if (unitsCount <= 7) {
			return ENCAMPMENT_SQUAD_3;
		}
		
		else {
			return DEFEND_BASE_SQUAD;
		}
	}

	// checks all available spaces around hq for spawning
	public boolean spawnInAvailable() throws GameActionException {
		Direction dir = myBaseLoc.directionTo(enemyBaseLoc);
		Direction dirOrig = Direction.values()[dir.ordinal()];
		if (rc.canMove(dir) && rc.senseMine(myBaseLoc.add(dir)) == null) {
			rc.setIndicatorString(0, "spawning robot at: " + myBaseLoc.add(dir));
			rc.spawn(dir);
			unitsCount++;
			return true;
		} else {
			dir = dir.rotateLeft();
			while (!rc.canMove(dir) || rc.senseMine(myBaseLoc.add(dir)) != null
					|| dir.equals(dirOrig)) {
				dir = dir.rotateLeft();
			}

			if (dir.equals(dirOrig)) {
				// looped all the way around
				rc.setIndicatorString(0, "all dirs occupied");
				return false;
			} else {
				rc.setIndicatorString(0,
						"spawning robot at: " + myBaseLoc.add(dir));
				rc.spawn(dir);
				unitsCount++;
				return true;
			}
		}
	}

	@Override
	public void decodeMsg(int encodedMsg) {
		// TODO Auto-generated method stub

	}

	public MapLocation[] getTargetEncampments() {
		MapLocation[] encampments = this.rc.senseAllEncampmentSquares();

		// compute distances of encampments to hq location
		// sort by distance
		// take top k
		// TODO use median of medians?
		// Arrays.sort(encampments, new MapLocationComparator()); // 6300
		// bytecodes

		// int targetRange = Math.max(rc.getMapHeight(), rc.getMapWidth())/2;
		// int targetRangeSquared = targetRange * targetRange;
		MapLocation[] targetEncampments = new MapLocation[3];

		/*
		 * int j = 0; if (myBaseLoc.x <= rc.getMapWidth()/2 || myBaseLoc.y <=
		 * rc.getMapHeight()/2) { for (int i=0; i < encampments.length; i++) {
		 * if (encampments[i].distanceSquaredTo(myBaseLoc) < targetRangeSquared)
		 * { targetEncampments[j] = encampments[i]; j++; if (j ==
		 * targetEncampments.length) { break; } } } } else { for (int
		 * i=encampments.length-1; i>=0; i--) { if
		 * (encampments[i].distanceSquaredTo(myBaseLoc) < targetRangeSquared) {
		 * targetEncampments[j] = encampments[i]; j++; if (j ==
		 * targetEncampments.length) { break; } } } }
		 */
		int[] targetDists = { 1000, 1000, 1000 };
		for (int i = 0; i < encampments.length; i++) {
			int dist = myBaseLoc.distanceSquaredTo(encampments[i]);
			int largestIndex = 0;
			for (int k = 1; k < 3; k++) {
				if (targetDists[k] > targetDists[largestIndex]) {
					largestIndex = k;
				}
			}
			if (targetEncampments[largestIndex] == null
					|| targetDists[largestIndex] == 0
					|| dist < targetDists[largestIndex]) {
				targetDists[largestIndex] = dist;
				targetEncampments[largestIndex] = encampments[i];
			}
		}
		/*
		 * [java] [server] basicplayer (A) wins
		 * System.out.println("start sorted encampments");
		 * System.out.println("myBaseLoc x: " + myBaseLoc.x + " y: " +
		 * myBaseLoc.y); for (int i=0; i < targetEncampments.length; i++) {
		 * System.out.println("x: " + targetEncampments[i].x + " y: " +
		 * targetEncampments[i].y); }
		 */
		return targetEncampments;
	}

	public class MapLocationComparator implements Comparator {

		@Override
		public int compare(Object loc0, Object loc1) {
			// TODO Auto-generated method stub
			return ((MapLocation) loc0).distanceSquaredTo(myBaseLoc)
					- ((MapLocation) loc1).distanceSquaredTo(myBaseLoc);
		}

	}

}