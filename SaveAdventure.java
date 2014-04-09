/*
 * SaveAdventure - This class lets the user save an ongoing Adventure to be loaded later. Five
 * 				   files are created; rooms, objects, synonyms, hints, and inventory.
 *
 * File: SaveAdventure.java
 * @author Micah Stairs, William Fiset
 * 
 */

import acm.program.*;
import java.io.*;
import java.util.*;

public abstract class SaveAdventure{

	static void save(ConsoleProgram console){
		
		console.print("Please type a name for your saved game.\n> ");
		String name = console.readLine();
		
		try{
			
			/** Save all of the rooms to file **/
			
			PrintWriter writerRooms = new PrintWriter(name + "Rooms.txt", "UTF-8");
			
			// Save current room into the file first
			writeRoom(writerRooms, getRoom(Adventure.currentRoomNumber));
			
			// Save all of the other rooms
			for(AdvRoom room : Adventure.rooms){
				
				// Save the room as long as it's not the current room
				if(room.getNumber() != Adventure.currentRoomNumber)
					writeRoom(writerRooms, room);
				
			}
			
			writerRooms.close();
			
			/** Save all of the objects to file **/
			
			PrintWriter writerObjects = new PrintWriter(name + "Objects.txt", "UTF-8");

			// Iterate through each room, searching for objects to store
			for(AdvRoom room : Adventure.rooms){
				
				// Iterate through this room's list of objects, writing them to file
				for(int i = 0; i < room.getObjectCount(); i++)
					 writeObject(writerObjects, room.getObject(i), room.getNumber());
				
			}
			
			// Iterate through the inventory, storing each object
			for(AdvObject obj : Adventure.inventory)
				writeObject(writerObjects, obj, Adventure.currentRoomNumber);
			
			writerObjects.close();
			
			/** Save all of the synonyms to file **/
			
			PrintWriter writerSynonyms = new PrintWriter(name + "Synonyms.txt", "UTF-8");
			
			Set<String> keys = Adventure.synonyms.keySet();
			
			// Iterate through each key in the synonyms HashMap, storing the key and its associated value
			for(String key : keys)
				writerSynonyms.println(key + "=" + Adventure.synonyms.get(key));
			
			writerSynonyms.close();
			
			/** Save all of the inventory objects to file **/
			
			PrintWriter writerInventory = new PrintWriter(name + "Inventory.txt", "UTF-8");
			
			// Iterate through object in the inventory, storing it's name and description
			for(AdvObject obj : Adventure.inventory){
				writerInventory.println(obj.getName());
				writerInventory.println(obj.getDescription());
				writerInventory.println();
			}
			
			writerInventory.close();
			
			/** Save which rooms have had a hint used in **/
			
			PrintWriter writerHints = new PrintWriter(name + "Hints.txt", "UTF-8");
			
			// Iterate through each room, storing the indexes of those rooms which a hint has been used
			for(AdvRoom room : Adventure.rooms)
				if(room.hintHasBeenUsed())
					writerHints.println(room.getNumber());
			
			writerHints.close();
			
		}
		catch(IOException e){}
		
		console.print("The game was saved as '" + name + "' . You may continue playing your adventure.");
		
	}
	
	private static void writeRoom(PrintWriter writerRooms, AdvRoom room){
		
		// Write room index number
		writerRooms.println(room.getNumber());
		
		// Write room name
		writerRooms.println(room.getName());
		
		// Write room description
		for(String line : room.getDescription())
			writerRooms.println(line);
		
		// Write separator
		writerRooms.println("-----");
		
		// Write motion map, one entry at a time
		String line = null;
		for(AdvMotionTableEntry entry : room.getAdvMotionTableEntries()){
			line = entry.getDirection() + " " + entry.getDestinationRoom();
			
			// Don't write the key if it's null
			if(entry.getKeyName() != null)
					line += "/" + entry.getKeyName();
			
			writerRooms.println(line);
		}
		
		// Write empty line
		writerRooms.println();
		
	}
	
	private static void writeObject(PrintWriter writerObjects, AdvObject obj, int roomNumber){
		
		 // Write object's name
		 writerObjects.println(obj.getName());
		 
		 // Write object's description
		 writerObjects.println(obj.getDescription());
		 
		 // Write the object's room number (not necessarily its initial location)
		 writerObjects.println(roomNumber);
		 
		 // Write empty line
		 writerObjects.println();
		
	}
	
/** Returns a room of the specified index, returning null if it does not exist **/
	private static AdvRoom getRoom(int roomNumber){
	
		// Iterate through each room, checking to see if it's index matches the specified room number
		for(int i = 0; i < Adventure.rooms.size(); i++)
			if(Adventure.rooms.get(i).getNumber() == roomNumber)
				return Adventure.rooms.get(i);
	
		return null;
	
	}
	
}