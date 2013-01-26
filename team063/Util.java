package team063;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class Util {
	
	//masks for encoding
	public static final int X_COORD_MASK = 127; //0b1111111
	public static final int Y_COORD_MASK = 127;
	public static final int SOLDIER_STATE_MASK = 15;
	public static final int ENCAMPMENT_TYPE_MASK = 7;
	public static final int CHECK_SUM_MASK = 7;
	public static final int UNIT_SQUAD_ASSIGNMENT_MASK = 1;
	public static final int SQUAD_ASSIGNMENT_MASK = 15;
	
	public static final int X_COORD_SHIFT = 0;
	public static final int Y_COORD_SHIFT = 7;
	public static final int SOLDIER_STATE_SHIFT = 14;
	public static final int ENCAMPMENT_TYPE_SHIFT = 18;
	public static final int CHECK_SUM_SHIFT = 29;
	public static final int UNIT_SQUAD_ASSIGNMENT_SHIFT = 28;
	public static final int SQUAD_ASSIGNMENT_SHIFT = 24;
	
	
	public static final int UNIT_CHANNEL_INDEX = 16;
	public static final int SQUAD_CHANNEL_INDEX = 10;
	public static final int SEED = 3221;
	
	// channels
	// squad consts
	public static final int SQUAD_ASSIGNMENT_CHANNEL = 6011;
	public static final int UNIT_ASSIGNMENT_CHANNEL = 11489;
	public static final int HQ_LISTEN_FOR_SCOUT_CHANNEL = 1753;
	/**
	 * Message/broadcast methods
	 */

	public static int getUnitChannelNum(int unitID) {
		return encodeChannelNum(unitID + Util.UNIT_CHANNEL_INDEX);
	}
	
	public static int getSquadChannelNum(int squadID) {
		return encodeChannelNum(squadID + Util.SQUAD_CHANNEL_INDEX);
	}
	
	public static int getAllUnitChannelNum() {
		return encodeChannelNum(0);
	}
	
	public static int getInitialSquadNumChannelNum() {
		return encodeChannelNum(Util.SQUAD_ASSIGNMENT_CHANNEL);
	}
	
	public static int getAllUnitExceptScoutChannelNum() {
		return encodeChannelNum(2);
	}
	
	public static int getInitialUnitNumChannelNum() {
		return encodeChannelNum(1);
	}
	
	public static int encodeChannelNum(int channelNum) {
		return (channelNum * 151 + 983*Clock.getRoundNum() + Util.SEED) % 65521;
	}
	
	
	/**
	 * bits 0-6: x coord
	 * bits 7-13: y coord
	 * bits 14-17: soldier state
	 * bits 18-28: extra info??
	 * 	ex) type of encampment for capturing (18-20)
	 *  bits 24-27: which squad to switch to
	 *  bit 28: switch/assign squad
	 *  
	 * bits 29-31: checksum
	 */
	public static int encodeMsg(MapLocation loc, SoldierState state, RobotType encampmentType, int otherInfo) {
		int checksum = (loc.x * 13 + loc.y * 41 + state.ordinal() * 3 + encampmentType.ordinal() * 31 + 511) % 8;
		return (loc.x << X_COORD_SHIFT) | 
				(loc.y << Y_COORD_SHIFT) | 
				(state.ordinal() << SOLDIER_STATE_SHIFT) |
				(encampmentType.ordinal() << ENCAMPMENT_TYPE_SHIFT) |
				(checksum << CHECK_SUM_SHIFT);
	}
	
	public static int encodeUnitSquadAssignmentChangeMsg(int squadNum) {
		return (1 << UNIT_SQUAD_ASSIGNMENT_SHIFT) | (squadNum << SQUAD_ASSIGNMENT_SHIFT);
	}
	
	public static int encodeSquadToHQMsg(MapLocation location, int numEnemies, int numEncamps, int numMines) {
		return 0;
	}
	
	public static int[] decode(int msg) {
		
		int xcoord = (msg & (X_COORD_MASK << X_COORD_SHIFT)) >> X_COORD_SHIFT;
		int ycoord = (msg & (Y_COORD_MASK << Y_COORD_SHIFT)) >> Y_COORD_SHIFT;
		
		int soldierStateIndex = (msg & (SOLDIER_STATE_MASK << SOLDIER_STATE_SHIFT)) >>> SOLDIER_STATE_SHIFT;
		
		int encampmentTypeIndex = (msg & (ENCAMPMENT_TYPE_MASK << ENCAMPMENT_TYPE_SHIFT)) >>> ENCAMPMENT_TYPE_SHIFT;
		
		int checksum = (msg & (CHECK_SUM_MASK << CHECK_SUM_SHIFT)) >>> CHECK_SUM_SHIFT;
		
		if ((xcoord * 13 + ycoord * 41 + soldierStateIndex * 3 + encampmentTypeIndex * 31 + 511) % 8 == checksum) {
			return new int[] {xcoord, ycoord, soldierStateIndex, encampmentTypeIndex};
		} else {
			return null;
		}
	}
	
	public static boolean getChangeSquadBool(int unitMsg) {
		int change = (unitMsg & (UNIT_SQUAD_ASSIGNMENT_MASK << UNIT_SQUAD_ASSIGNMENT_SHIFT)) >> UNIT_SQUAD_ASSIGNMENT_SHIFT;
		return (change == 1);
	}
	
	public static int getSquadAssignment(int unitMsg) {
		return (unitMsg & (SQUAD_ASSIGNMENT_MASK << SQUAD_ASSIGNMENT_SHIFT)) >> SQUAD_ASSIGNMENT_SHIFT;
	}
	public static MapLocation getMapLocationFromMsg(int encodedMsg) {
		int xcoord = (encodedMsg & (X_COORD_MASK << X_COORD_SHIFT)) >> X_COORD_SHIFT;
		int ycoord = (encodedMsg & (Y_COORD_MASK << Y_COORD_SHIFT)) >> Y_COORD_SHIFT;
		MapLocation loc = new MapLocation(xcoord, ycoord);
		return loc;
	}
	
	public static SoldierState getSoldierStateFromMsg(int encodedMsg) {
		
		int index = (encodedMsg & (SOLDIER_STATE_MASK << SOLDIER_STATE_SHIFT)) >> SOLDIER_STATE_SHIFT;
		if (index < 12) {
			return SoldierState.values()[index];
		}
		else {
			return SoldierState.DEFAULT;
		}
	}
	
	public static RobotType getEncampmentTypeFromMsg(int encodedMsg) {
		int index = (encodedMsg & (ENCAMPMENT_TYPE_MASK << ENCAMPMENT_TYPE_SHIFT)) >> ENCAMPMENT_TYPE_SHIFT;
		if (index < 7) {
			return RobotType.values()[index];
		}
		else {
			return RobotType.ARTILLERY;
		}
	}
	
	public static int getOtherInfoFromMsg(int encodedMsg) {
		//TODO implement
		return 0;
	}
	
	public enum ScoutMessages {
		
	}
}
