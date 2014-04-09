/*
 * AdvRoom - This class defines a single room in the Adventure game. A room is characterized
 * 					 by a number, name, description, list of objects, a motion table (specifying its
 * 					 connections with other rooms), a flag indicating whether its been visited or not,
 * 					 and a flag indicating whether ot not a hint has been used there.
 * 
 * File: AdvRoom.java
 * @author William Fiset, Micah Stairs
 * 
 */

import java.io.*;
import java.util.*;

public class AdvRoom {
	
	/* Private instance variables */
	private int roomNumber;
	private String roomName;
	private String[] roomDescription;
	private AdvMotionTableEntry[] motionTable;
	private boolean hasBeenVisited = false;
	private boolean hintHasBeenUsed = false;
	private ArrayList<AdvObject> roomObjects = new ArrayList<AdvObject>();

/**
* @param roomNumber: A room number, which must be greater than zero
* @param roomName: Its name, which is a one-line string identifying the room
* @param roomDescription: Its description, which is a line array describing the room
* @param motionTable: A motion table specifying the exits and where they lead
*/
	
	public AdvRoom(int roomNumber, String roomName, String[] roomDescription, AdvMotionTableEntry[] motionTable){

		this.roomNumber = roomNumber;
		this.roomName = roomName;
		this.roomDescription = roomDescription;
		this.motionTable = motionTable;

	}

/** Return whether or not the room has had a hint used on it before **/
	public boolean hintHasBeenUsed(){
		return this.hintHasBeenUsed;
	}

/** Sets if a hint has been used on the room **/
	public void setHint(boolean hintHasBeenUsed){
		this.hintHasBeenUsed = hintHasBeenUsed;
	}

/** Return whether or not the room has been visited before **/
	public boolean hasBeenVisited(){
		return this.hasBeenVisited;
	}

/** Sets if the room has been visited before or not **/
	public void setVisited(boolean hasBeenVisited){
		this.hasBeenVisited = hasBeenVisited;
	}

/** Return the index of this room **/
	public int getNumber (){
		return this.roomNumber;
	}

/** Gets the room name, which is its one-line description **/
	public String getName(){
		return this.roomName;
	}

/** Gets the full room description **/
	public String[] getDescription(){
		return this.roomDescription;
	}

/** Adds an AdvObject to the list of objects in this room **/
	public void addObject(AdvObject obj) {
		this.roomObjects.add(obj);
	}

/** Removes an AdvObject to the list of objects in this room **/
	public void removeObject(AdvObject obj) {
		roomObjects.remove(obj);		
	}

/** Checks whether the specified AdvObject is in the room **/
	public boolean containsObject(AdvObject obj) {
		 return roomObjects.contains(obj);
	}

/** Clears list of objects **/
	public void clearObjectList(){
		roomObjects.clear();
	}

/** Returns the number of AdvObjects in this room **/
	public int getObjectCount() {
		return this.roomObjects.size();
	}

/** Returns the specified element from the list of AdvObjects in the room **/
	public AdvObject getObject(int index) {
		return roomObjects.get(index);
	}

/** Returns the motion table */
	public AdvMotionTableEntry[] getAdvMotionTableEntries(){
		return this.motionTable;
	}

/**
 * Creates a new room by reading its data from the specified reader.
 * If no data is left in the reader, this method returns null instead of an AdvRoom object.
 *
 * @param rd The BufferedReader from which the room data is read 
 */
	public static AdvRoom readRoom(BufferedReader rd) {
		
		int tempNumber = 0;
		String tempName = null;
		String[] tempDescription  = {};
    ArrayList<AdvMotionTableEntry> tempListMotionTable = new ArrayList<AdvMotionTableEntry>();

		try{

			/** Read-in room number, stops trying to read, and returns null if there are no more rooms to read **/
			
			String tempString = rd.readLine();

			if(tempString == null)
				return null;

			tempNumber = Integer.parseInt(tempString);

			/** Read-in room name **/
			
			tempName = rd.readLine();

			/** Read-in room description line by line until the whole thing has been read and stored**/
			
			ArrayList<String> tempArrayDescription = new ArrayList<String>();
			String line  = rd.readLine();
			
			while(!line.equals("-----")){
				tempArrayDescription.add(line);
				line = rd.readLine();
			}

			tempDescription = tempArrayDescription.toArray(new String[0]);

			/** Read-in motion table, which specifies the exits and where they lead **/
			
			while(!false){
				
				// Stop trying to read in motion table if we are already done
				line = rd.readLine();
				if (line == null || line.equals(""))
					break;
				
				// Find the index up to the end of the word
				int directionIndex = line.indexOf(' ');
				
				// Checks to see if its separated by tabs instead of spaces
				if(directionIndex == - 1)
					directionIndex = line.indexOf(9);
				
				// Find the index up to the end of the number
				int numberIndex = line.indexOf('/');
				if(numberIndex == -1)
					numberIndex = line.length();
				
				// Split line into direction command and destination room index
				String direction = line.substring(0, directionIndex);
				String number = line.substring(directionIndex, numberIndex).trim();
				int intNumber = Integer.parseInt(number.trim()); 
				
				// Store the key into a String, leaving it null if there is no required key
				String key = line.substring(numberIndex, line.length());
				if (key.equals(""))
					key = null;
				else
					key = key.substring(1, key.length());
				
				// Add entry to the list
				tempListMotionTable.add(new AdvMotionTableEntry(direction, intNumber, key));

			}
			
		}
    catch(IOException e){
    	return null;
    }

		// Return instantiated AdvRoom with the properties that were just read in from file
    AdvMotionTableEntry[] tempMotionTable = tempListMotionTable.toArray(new AdvMotionTableEntry[0]);
		return new AdvRoom(tempNumber, tempName, tempDescription, tempMotionTable);

	}

}