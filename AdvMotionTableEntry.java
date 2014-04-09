/*
 * AdvMotionTableEntry - This class is one entry in a motion table, and allows you to retrieve
 * 											 the direction command, the destination room index and the key required.
 * 
 * File: AdvMotionTableEntry.java
 * @author Micah Stairs, William Fiset
 * 
 */

public class AdvMotionTableEntry {
	
	/* Private instance variables */
  private String direction, keyName;
	private int destinationRoom;
	
/**
 * @param dir: The string specifying the direction of motion
 * @param room: The number of the destination room
 * @param key: The name of the key object, or null if none
 */
   public AdvMotionTableEntry(String dir, int room, String key) {
      direction = dir.toUpperCase();
      destinationRoom = room;
      keyName = (key == null) ? null : key.toUpperCase();
   }

/** Returns the direction name from a motion table entry **/
   public String getDirection() {
      return direction;
   }

/** Returns the room index number that the associated direction command takes you to **/
   public int getDestinationRoom() {
      return destinationRoom;
   }

/** Returns the name of the object required to proceed, or null if the passage is open **/
   public String getKeyName() {
      return keyName;
   }

}