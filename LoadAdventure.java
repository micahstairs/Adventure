
/*
 * LoadAdventure - This class lets the user select an Adventure and then it loads the rooms, 
 * 				   			 objects and synonyms into lists to be accessed in the main program.
 * 
 * File: LoadAdventure.java
 * @author Micah Stairs, William Fiset
 * 
 */

import acm.program.*;
import java.io.*;
import java.util.*;

public abstract class LoadAdventure{
	
	/* Private instance variables */
	private static BufferedReader bufferedReaderRooms = null, bufferedReaderObjects = null;
	
/**
 * @param console: The ConsoleProgram of the Adventure game, allowing the program to interact with the user
 */
	static void load(ConsoleProgram console, String line){

		// Reset lists		
		Adventure.rooms = new ArrayList<AdvRoom>();
		Adventure.objects = new ArrayList<AdvObject>();
		Adventure.inventory = new ArrayList<AdvObject>();
		Adventure.synonyms = new HashMap <String, String>();
		
		boolean adventureSelected = false;

		/** Test to see if the user already typed a valid Adventure game **/
		boolean validAdventureAlreadyChosen = false;
		if(line != null){

			File fileRooms = new File(line + "Rooms.txt");

			if(fileRooms.exists())
				validAdventureAlreadyChosen = true;
		}

		/** Allows user to select an Adventure **/

		if(!validAdventureAlreadyChosen)
			console.print("\n> ");	

		while(!adventureSelected){

			try{

				if(!validAdventureAlreadyChosen)
					line = console.readLine();
				
				adventureSelected = true;
				
				File fileRooms = new File(line + "Rooms.txt");
				bufferedReaderRooms = new BufferedReader(new FileReader(fileRooms));
			
			}
			catch(IOException e){
				console.print("'" + line + "Rooms.txt' was unable to be located. Please try again: \n> ");
				adventureSelected = false;	
			}
			
		}
		
		/** Import and Store Rooms from File **/
		AdvRoom room = null;

		// Read in rooms one at a time
		while(!false){

			room = AdvRoom.readRoom(bufferedReaderRooms);

			if(room != null)
		 		Adventure.rooms.add(room);
		 	else
		 		break;
		}
		
		// Find starting room's index (usually 1, but it can be different if it's loading a saved game)
		Adventure.currentRoomNumber = Adventure.rooms.get(0).getNumber();
		
		/** Import and Store Objects from File (If It Exists) **/
		try{
			
			// Set up BufferedReader
			File fileObjects = new File(line + "Objects.txt");
			bufferedReaderObjects = new BufferedReader(new FileReader(fileObjects));
			
			AdvObject tempObject = null;
			
			// Read in and instantiate AdvObjects, one at a time
			while(bufferedReaderObjects != null){

				tempObject = AdvObject.readObject(bufferedReaderObjects);
				
				// Add object to the list
				if(tempObject != null)
			 		Adventure.objects.add(tempObject);
			 	else
			 		break;
				
			}
				
		}
		catch(IOException e){}
		
		/** Import and Store Synonyms from File (If It Exists) **/
		File fileSynonyms = new File(line + "Synonyms.txt");
		readSynonyms(fileSynonyms);
		
		/** Import and Store Inventory from File (If It Exists) **/
		File fileInventory = new File(line + "Inventory.txt");
		readInventory(fileInventory);

		/** Close Rooms and Objects files **/
		
		try{
			bufferedReaderRooms.close();
			bufferedReaderObjects.close();
		}
		catch(IOException e){}
		catch(NullPointerException e){}
		
		/** Import and Store Hint Information from File (If It Exists) **/
		
		// Calculate default amount of hints
		Adventure.numberHints = Math.max(3, Adventure.rooms.size()/10);
		
		// Set the hint booleans in rooms, and calculate the remaining number of hints
		try {

			Scanner sc = new Scanner(new File(line + "Hints.txt"));
			
			// Read in the indexes of the rooms (that have had hints used) from the file
			while (sc.hasNextInt()){
				getRoom(sc.nextInt()).setHint(true);
				Adventure.numberHints--;
			}	
			
			sc.close();
		
		}
		catch(IOException e){}
		
		/** Final setup: Place objects in the proper room, and print the description of starting room **/
		
		// Adds each object to its corresponding room
		for (AdvObject obj : Adventure.objects){
			room = getRoom(obj.getInitialLocation());
			room.addObject(obj);
		}

		// Print the description of the starting room
		printDescription(console);

	}

/** Reads synonyms from file and stores into the HashMap **/
	private static void readSynonyms(File file){
	
		try {

			Scanner sc = new Scanner(file);
			
			// Read all of the synonyms from the file
			while (sc.hasNext()){
				String line = sc.nextLine();
				
				// Find index of where the String should be split
				int index = line.indexOf("=");
				
				// Separate the key and the value, and then put it into the HashMap
				String key = line.substring(0, index);
				String value = line.substring(index + 1, line.length());
				Adventure.synonyms.put(key.trim(), value.trim());
				
			}	
			
			sc.close();
			
		}
		catch(IOException e){}
		
	}
	
	/** Reads inventory file, removing specified objects from the current room and placing them in the inventory **/
	private static void readInventory(File file){
	
		try {

			Scanner sc = new Scanner(file);
			String name = null, description = null;
			
			// Read all of the inventory items from the file
			while (sc.hasNext()){
				
				/** Retrieve object information from file **/
				
				String line = sc.nextLine();
				
				 // Get name, skipping a line if a empty line is found between objects
        if(line == null || line.equals(""))
            name = sc.nextLine();
        else
            name = line;
        
        // Get description
        description = sc.nextLine();
				
        /** Look for an identical object in the objects list, and move it into the inventory if found **/
        
        AdvObject objMatch = null;
        // Check each object in the current room for a match
        for(AdvObject obj : Adventure.objects){
        	
        	// Break out of loop once the object is located
        	if(name.equals(obj.getName()) && description.equals(obj.getDescription())){
        		objMatch = obj;	
        		break;
        	}
        }
        
        // Add object to inventory and remove from the current room
				Adventure.inventory.add(objMatch);
				Adventure.objects.remove(objMatch);
				
			}
			
			sc.close();
			
		}
		catch(IOException e){}

	}
	
/** Returns a room of the specified index, returning null if it does not exist **/
	private static AdvRoom getRoom(int roomNumber){
	
		// Iterate through each room, checking to see if it's index matches the specified room number
		for(int i = 0; i < Adventure.rooms.size(); i++)
			if(Adventure.rooms.get(i).getNumber() == roomNumber)
				return Adventure.rooms.get(i);
	
		return null;
	
	}
	
/** Prints the description of the starting room (a static version of the method found in the Adventure class) **/
	private static void printDescription(ConsoleProgram console){
		
		AdvRoom room = getRoom(Adventure.currentRoomNumber);
		
		// Set this room as being visited
		room.setVisited(true);
		
		// Print room description
		for(String string : room.getDescription())
			console.print(string + " ");
		
		// Terminates the line if we have objects to print
		if(room.getObjectCount() > 0)
			console.println();
	
		// Prints all objects that are in the current room
		for(int i = 0; i < room.getObjectCount(); i++ ) {
			AdvObject obj = room.getObject(i);
	
			// Print object's name and description
			console.print(" - " + obj.getName() + ": " + obj.getDescription() );
	
			// Used to ensure that an empty line isn't generate on the console 
			if(i < room.getObjectCount() - 1)
				console.println();
		}
		
		// Prints hints if a hint has already been used in this room
		if(room.hintHasBeenUsed()){
			console.println(formatHintsString());
			console.print("(You've previously used a hint in this room.)");
		}
		
	}
	
/** Prints the available directions the user can move (a static version of the method found in the Adventure class) **/
	private static String formatHintsString(){

		String directions = "";

		// Gets all possible directions (and required keys, if applicable) for the current room
		for (AdvMotionTableEntry entry : getRoom(Adventure.currentRoomNumber).getAdvMotionTableEntries())
			if(entry.getKeyName() == null)
				directions += "\n - " + entry.getDirection();
			else
				directions += "\n - " + entry.getDirection() + " (Requires " + entry.getKeyName() + ")";

		return "Possible direction commands in this room: " + directions;

	}

   /** 
    * Searches the entire directory for files ending in "Rooms.txt"
    * @return The list of Adventure games contained within directory
	**/

	public static ArrayList<String> getAdventuresInFolder(File directory) {
	    
		ArrayList<String> availableAdventures = new ArrayList<String> ();

			// Iterates through each file in the current directory 
	    for (File fileEntry : directory.listFiles()) {

	        String completePathName = fileEntry.toString();
	        int length = completePathName.length();
	        
	        String fileNameEnding = completePathName.substring(length - 9, length);

	        // Tests to see if the current file is a room file
	        if (fileNameEnding.equals("Rooms.txt") ) {
	        	
	        	// Find the last index of a slash in the full path name (the slashes vary in direction depending on the OS)
	        	int indexOfLastSlash = Math.max(completePathName.lastIndexOf("/"), completePathName.lastIndexOf("\\"));

	        	// Get the exact name of the file excluding the .txt ending
	        	String fileName = completePathName.substring(indexOfLastSlash + 1, length - 9);
	        	availableAdventures.add(fileName);

	        }
	    }

	    return availableAdventures;

	}

}