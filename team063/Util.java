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
	
	public static final int X_COORD_SHIFT = 0;
	public static final int Y_COORD_SHIFT = 7;
	public static final int SOLDIER_STATE_SHIFT = 14;
	public static final int ENCAMPMENT_TYPE_SHIFT = 18;
	public static final int CHECK_SUM_SHIFT = 29;
	
	
	
	public static final int UNIT_CHANNEL_INDEX = 16;
	public static final int SQUAD_CHANNEL_INDEX = 10;
	public static final int SEED = 3221;
	
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
		return encodeChannelNum(1);
	}
	
	public static int getAllUnitExceptScoutChannelNum() {
		return encodeChannelNum(2);
	}
	
	public static int encodeChannelNum(int channelNum) {
		return (channelNum * 151 + 983*Clock.getRoundNum() + Util.SEED) % 65521;
	}
	
	
	/**
	 * bits 0-6: x coord
	 * bits 7-13: y coord
	 * bits 14-17: soldier state
	 * bits 18-28: extra info??
	 * 	ex) type of encampment for capturing
	 * bits 29-31: checksum
	 */
	public static int encodeMsg(MapLocation loc, SoldierState state, RobotType encampmentType, int otherInfo) {
		return (loc.x << X_COORD_SHIFT) | 
				(loc.y << Y_COORD_SHIFT) | 
				(state.ordinal() << SOLDIER_STATE_SHIFT) |
				(encampmentType.ordinal() << ENCAMPMENT_TYPE_SHIFT);
	}
	
	public static MapLocation getMapLocationFromMsg(int encodedMsg) {
		int xcoord = (encodedMsg & (X_COORD_MASK << X_COORD_SHIFT)) >> X_COORD_SHIFT;
		int ycoord = (encodedMsg & (Y_COORD_MASK << Y_COORD_SHIFT)) >> Y_COORD_SHIFT;
		MapLocation loc = new MapLocation(xcoord, ycoord);
		return loc;
	}
	
	public static SoldierState getSoldierStateFromMsg(int encodedMsg) {
		int index = (encodedMsg & (SOLDIER_STATE_MASK << SOLDIER_STATE_SHIFT)) >> SOLDIER_STATE_SHIFT;
		return SoldierState.values()[index];
	}
	
	public static RobotType getEncampmentTypeFromMsg(int encodedMsg) {
		int index = (encodedMsg & (ENCAMPMENT_TYPE_MASK << ENCAMPMENT_TYPE_SHIFT)) >> ENCAMPMENT_TYPE_SHIFT;
		return RobotType.values()[index];
	}
	
	public static int getOtherInfoFromMsg(int encodedMsg) {
		//TODO implement
		return 0;
	}
}
