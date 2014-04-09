/*
 * Adventure - This class is the main program class for the Adventure game.
 * 
 * File: Adventure.java
 * @author Micah Stairs, William Fiset
 * @version 1.3
 *
 * -------------------------------------------------------
 * 
 * Special Features:
 *
 *	 -MTA Storyline: We wrote a storyline of our own for the game. We recommend that
 *		you try it out (otherwise all of the work we put into writing it was in vain)!
 * 
 *   -Hint System: We included an additional keyword 'HINT' that displays all the 
 *		possible directions the user can move. There are a limited number of hints
 *		the user is able to use each time they play an Adventure.
 *
 *	 -'SYNONYMS' Command: User is able to print out a list of the synonyms that the
 *	  current Adventure game uses.
 *
 *	 -'TAKE ALL' command was added, which allows the user to take all available
 *	  objects from the room and put it in their inventory.
 *
 *	 -'DROP ALL' command was added, which allows the user to drop all of their
 *		objects back into the current room.
 *
 *	 -Save Game Feature: We incorporated an interesting feature which allows the
 *	  user to save their current progress in an adventure to be played later. It
 *	  creates the following files:
 *		- List of rooms (with the current room listed first, enabling the user to
 *		  be in the correct room when the game is loaded)
 *		- List of synonyms (identical to original)
 *		- List of objects (which have new initial locations, depending on where the
 *		  object was when the game was saved)
 *		- List of which rooms that hints have been used in
 *		- List of inventory objects (after all of the rooms and objects have been
 *		  loaded, the specified objects are removed from the inital room and placed
 *		  into the user's inventory)
 *
 */

import acm.io.*;
import acm.program.*;
import java.awt.*;
import java.util.*;
import java.io.File;

public class Adventure extends ConsoleProgram {

	/* Protected instance variables */
	protected static ArrayList<AdvRoom> rooms;
	protected static ArrayList<AdvObject> objects;
	protected static ArrayList<AdvObject> inventory;
	protected static HashMap <String, String> synonyms;
	protected static int currentRoomNumber;
	protected static int numberHints;
	
	/* Private instance variables */
	private boolean gameActive = true;
	private IOConsole console;

/** The method that is called when the Java Applet is initiated **/
	public void run() {
		
		/** Set up the console **/
		console = this.getConsole();
		pimpConsole();
		
		// Generate list of Adventure files that are available to load 
		ArrayList<String> adventureFiles = LoadAdventure.getAdventuresInFolder(new File(System.getProperty("user.dir")));
		
		/** Display error if no Adventures exist, terminating the program **/
		if(adventureFiles.size() == 0){
			console.print("Welcome to Adventure! There were no Adventures located in your current directory. ");
			console.print("The program has been terminated.");
			return;
		}
		
		/** Display starting message, with a list of all available Adventures **/
		else{
			
			String str = "Welcome to Adventure! Please select which Adventure you wish to play: \n";
			
			for (String adventureName : adventureFiles )
				str += " - " + adventureName + "\n";
			
			console.print(str.substring(0, str.length() - 1));
		
		}

		/** Load Adventure **/
		LoadAdventure.load(this, null);
		if(!forceUser(false))
			print("\n> ");

		/** MAIN LOOP **/
		while(gameActive){
			
			// Get input from the user
			String userInput = readLine();

			// Process and respond to input
			respondToInput(userInput);

		}
		
	}

/** Takes a String, which is the user's input, and responds to it properly **/
	protected void respondToInput(String userInput){
		
		/** Check to see if the user is in room 0, which means that the game is over **/
		if(currentRoomNumber == 0){
			gameActive = false;
			return;
		}

		/** Parse Input **/
		
		String[] userInputArray = userInput.toUpperCase().split(" ");

		/** Check for Command Synonyms **/
		
		for (String key : synonyms.keySet()){
			
			// Check each string in the user input array
			for(int i = 0; i < userInputArray.length; i++)
				if (userInputArray[i].equals(key))
					userInputArray[i] = synonyms.get(key);
		}

		String command = userInputArray[0];

		/** Execute Corresponding Action **/
		
		if(command.equals("INVENTORY"))
			inventoryCommand();
		
		else if(command.equals("LOOK")){
			lookCommand(userInputArray);
			return;
		}

		else if(command.equals("HELP"))
			helpCommand();

		else if(command.equals("HINT"))
			hintCommand();

		else if(command.equals("DROP"))
			dropCommand(userInputArray);

		else if(command.equals("TAKE"))
			takeCommand(userInputArray);
		
		else if(command.equals("QUIT"))
			quitCommand();
		
		else if(command.equals("SYNONYMS"))
			synonymsCommand();
		
		else if(command.equals("SAVE"))
			SaveAdventure.save(this);
		
		else if(command.equals("LOAD")){
			console.print("Please type the name of the game you'd like to load.");
			LoadAdventure.load(this, concatenateStrings(userInputArray, 1));
		}

		else{
			directionCommand(command);
			return;
		}
		
		/** Force User into Another Room (If Applicable) **/
		if(forceUser(false))
			return;

		/** Prepare Console for User's Next Set of Input **/
		if(gameActive)
			print("\n> ");
	}

/** Forces the user into another room if a possible room exists, returning true if they were forced **/
	private boolean forceUser(boolean emptyDescription){
		
		AdvRoom room = getRoom(currentRoomNumber);
		AdvMotionTableEntry[] motionTable = room.getAdvMotionTableEntries();

		boolean accessGranted = false;

		// Iterate through each entry in the room's motion table, looking for 'FORCED'
		for(AdvMotionTableEntry entry : motionTable)
			if(entry.getDirection().equals("FORCED")){
				
				// Access is granted if you do not need a key
				if (entry.getKeyName() == null)
					accessGranted = true;
				// Access is granted if a key is needed, and it's in the inventory
				else 
					for(AdvObject inventoryItem : inventory)
						if(inventoryItem.getName().equals(entry.getKeyName())) {
							accessGranted = true;
							break;
						}
				
				// Force user to that room if access was granted
				if(accessGranted){
					currentRoomNumber = entry.getDestinationRoom();
					if(!emptyDescription)
						print("\n");
					respondToInput("LOOK INVOKED");
					return true;
				}
			}
		
		return false;
		
	}

/** Prints out the user's inventory of objects **/
	private void inventoryCommand(){

		// Print if the inventory is empty
		if(inventory.size() == 0)
			print("You are empty-handed.");

		// Print bulleted list of inventory objects
		else{

			String str = "";

			// Iterate through inventory objects, putting the info in a bulleted format
			for(AdvObject obj : inventory)
				str += " - " + obj.getName() + ": " + obj.getDescription() + "\n";
			
			// Used to ensure that an empty line isn't generated on the console 
			print(str.substring(0, str.length() - 1));
			
		}
		
	}

/** Prints the description of the current room **/
	private void lookCommand(String[] userInputArray){

		AdvRoom room = getRoom(currentRoomNumber);

		boolean emptyDescription = false;

		/** Print the short description if the user has been here before, and the long one if they haven't **/
		if(userInputArray.length > 1 && userInputArray[1].equals("INVOKED"))
			if(room.hasBeenVisited())
				print(room.getName());
			else
				if(room.getDescription().length != 0)
					printDescription();
				else
					emptyDescription = true;
		
		/** Print full description since the user typed 'LOOK' **/
		else
			if(room.getDescription().length != 0)
				printDescription();
			else
				emptyDescription = true;
		
		/** Force User into Another Room (If Applicable) **/
		if(forceUser(emptyDescription))
			return;
		
		print("\n> ");

	}

/** Prints the help information **/
	private void helpCommand(){

		println("Welcome to Adventure!\n");
		print("To move, try words like IN, OUT, EAST, WEST, NORTH, SOUTH, UP, or DOWN. Looking at the full ");
		print("description of the current room can help you figure out which direction commands might be ");
		print("available. You can use a hint to reveal all of the direction commands that exist for that room. ");
		print(" You currently have " + formatHintString() + " left. To get a list of shortcuts that you ");
		print("are able to use in this Adventure game, you can take a look at the synonyms. You are also ");
		print("able to save your progress at any time by saving the game, or you may load a different ");
		print("Adventure.\n\nThe standard commands that you can use are 'QUIT', 'HELP', 'INVENTORY', 'DROP', ");
		print("'DROP ALL', 'TAKE', 'TAKE ALL', 'LOOK', 'HINT', 'SYNONYMS', 'SAVE' and 'LOAD'.");

	}

/** Prints a hint about the room (giving the user information about the direction commands that they can use) **/
	private void hintCommand(){

		AdvRoom room = getRoom(currentRoomNumber);

		/** Prints the hint information, but does not use a hint up
		 * (since the user has already used a hint in this room before)**/
		if(room.hintHasBeenUsed()){
			printHints();
			print("(You've already used a hint in this room before, so you still have " + formatHintString() + " left.)");
		}

		/** Uses a hint, and prints the hint information **/
		else if(numberHints > 0){
			numberHints--;
			room.setHint(true);
			printHints();
			print("(You have " + formatHintString()  + " remaining.)");
		}

		/** Notifies user that they do not have any hints remaining **/
		else
			print("Sorry, you do not have any hints remaining.");
	}

/** Drops the specified object (or your entire inventory) into the current room **/
	private void dropCommand(String[] userInputArray){
		
		/** Print error message saying that there are no objects to drop **/
		if(inventory.size() == 0)
			print("You do not have any objects in your inventory.");
		
		/** Print error message saying that an object was not specified **/
		else if(userInputArray.length < 2)
			print("You need to specify an object to drop.");
		
		/** Drop all objects into the room **/
		else if(userInputArray[1].equals("ALL")){
			
			AdvRoom tempRoom = getRoom(currentRoomNumber);
			
			// Move all objects from inventory into the room
			for(AdvObject obj : inventory)
				tempRoom.addObject(obj);	
			
			// Clear the inventory list 
			inventory.clear();
			
			print("You dropped your entire inventory.");
		}
		
		/** Drop specified object into the room **/
		else{

			boolean objectFound = false;

			for(AdvObject obj : inventory)
				if(obj.getName().equals(userInputArray[1])){

					// Remove object from inventory
					inventory.remove(obj);
					objectFound = true;

					// Place the dropped object into the room
					AdvRoom tempRoom = getRoom(currentRoomNumber);
					tempRoom.addObject(obj);

					print("You dropped " + obj.getName() + ".");
					break;
				}

			if(!objectFound)
				print("Sorry, you do not have that object in your inventory.");

		}
		
	}
	
/** Takes the requested object from the room and puts it into the user's inventory **/
	private void takeCommand(String[] userInputArray){

		AdvRoom room = getRoom(currentRoomNumber);

		/** Prints error message if no object was specified **/
		if(userInputArray.length < 2)
			print("You need to specify an object to take.");
				
		/** Prints error message if there are no objects available to take **/
		else if(room.getObjectCount() == 0)
			print("Sorry, there are no objects in this room that you can take.");
		
		/** Take all objects from room and put them into the user's inventory **/
		else if(userInputArray[1].equals("ALL")){
			
			AdvRoom tempRoom = getRoom(currentRoomNumber);
			
			// Move all objects from from into the inventory
			for(int i = 0; i < tempRoom.getObjectCount(); i++){
				AdvObject obj = tempRoom.getObject(i);	
				inventory.add(obj);
			}
			
			// Clear the room's list of objects 
			tempRoom.clearObjectList();
			
			print("You picked up all of the objects in the room.");

		}
		
		/** Attempts to take the specified object **/
		else{

			boolean objectFound = false;

			// Iterates through each object in the room
			for(int i = 0; i < room.getObjectCount(); i++){
				AdvObject obj = room.getObject(i);
				
				// Takes the object specified by the user, if it's name matches
				if(obj.getName().equals(userInputArray[1])){
					inventory.add(obj);
					room.removeObject(obj);
					objectFound = true;
					print("You took " + obj.getName() + ".");
					break;
				}
			}

			/** Print error message if the specified object was not in the room **/
			if(!objectFound)
				print("Sorry, that object does not exist in this room.");
		}
		
	}

/** Confirms that the user wants to quit the game, and stops the main loop if they are done playing the adventure **/
	private void quitCommand(){

		print("Are you sure you want to quit this fantastic Adventure? (Y/N)\n> ");

		// Continue asking them if they want to quit the game until they respond with a valid answer
		while(true){

			// Read input
			String userInput = readLine().toUpperCase();

			// Quit the game if they said 'YES'
			if(userInput.equals("Y") || userInput.equals("YES")){
				print("I'm sorry to see you leave, but I suppose I don't have much say.");
				gameActive = false;
				break;
			}

			// Continue the adventure if they said 'NO'
			else if(userInput.equals("N") || userInput.equals("NO")){
				print("Excellent! Let us continue the Adventure then!");
				break;
			}

			// Ask them to answer the question again if the input was invalid
			else
				print("I did not understand what you said. Please respond with a 'YES' or a 'NO'.\n> ");
		
		}
		
	}
	
/** Prints a list of all of the synonyms **/
	private void synonymsCommand(){
		
		// Store set of keys, and initialize counter
		Set<String> keys = synonyms.keySet();
		int counter = keys.size();
		
		/** Print a message if there are not synonyms **/
		if(counter == 0)
			print("Sorry, there are no synonyms that can be used during this Adventure.");

		/** Otherwise, iterate through each entry in the HashMap, printing out the synonyms **/
		else
			for(String key : keys){
				
				// Print key and value out
				print(key + " = " + synonyms.get(key));
				counter--;
				
				// Does not terminate the line if we are on the last entry
				if(counter > 0)
					print("\n");
			}
	}

/** Takes the direction command from the user and attempts to direct it to the requested room **/
	private void directionCommand(String command){

		AdvRoom room = getRoom(currentRoomNumber);
		AdvMotionTableEntry[] motionTable = room.getAdvMotionTableEntries();
		boolean accessGranted = false;
		boolean validCommand = false;

		// Iterates through the entries in the motion table
		for(AdvMotionTableEntry entry : motionTable)
			if(entry.getDirection().equals(command)){
				
				// The command matches a direction in the motion map, therefore it's a valid direction
				validCommand = true;
				
				// Access is granted if no key is required
				if (entry.getKeyName() == null)
					accessGranted = true;

				// Access is granted if the required key is present in the inventory
				else 
					for (AdvObject inventoryItem : inventory)
						if (inventoryItem.getName().equals(entry.getKeyName())) {
							accessGranted = true;
							break;
						}

				// Switch to the new room if access was granted
				if(accessGranted){
					
					// Set visited of previous room
					room.setVisited(true);
					currentRoomNumber = entry.getDestinationRoom();
					respondToInput("LOOK INVOKED");
					
					// Set visited of new room
					room.setVisited(true);
					
					return;
				}
			
			}
		
		// Prints if the user typed a valid direction but did not have the required key
		if(validCommand)
			print("Sorry you do not have access to that room.\n> ");
		// Prints if the user typed something that did not match any of the standard commands or an available direction command
		else
			print("That command is complete gibberish.\n> ");
	
	}
	
/** Prints the available directions the user can move **/
	private void printHints(){

		String directions = "";

		// Gets all possible directions (and required keys, if applicable) for the current room
		for (AdvMotionTableEntry entry : getRoom(currentRoomNumber).getAdvMotionTableEntries())
			if(entry.getKeyName() == null)
				directions += "\n - " + entry.getDirection();
			else
				directions += "\n - " + entry.getDirection() + " (Requires " + entry.getKeyName() + ")";

		// Print the directions
		println("Possible direction commands in this room: " + directions);

	}

/** Properly formats the hint String to take pluralization into account **/
	private String formatHintString(){
		
		// Nifty opportunity to make use of the ternary operator 
		return numberHints == 1 ? "1 hint" : numberHints + " hints";
	
	}
	
/** Returns a room of the specified index, returning null if it does not exist **/
	protected AdvRoom getRoom(int roomNumber){
	
		// Iterate through each room, checking to see if it's index matches the specified room number
		for(int i = 0; i < rooms.size(); i++)
			if(rooms.get(i).getNumber() == roomNumber)
				return rooms.get(i);
	
		return null;
	
	}

/** Prints full description of the current room and its objects **/
	private void printDescription(){
	
		AdvRoom room = getRoom(currentRoomNumber);
	
		// Print room description
		for(String string : room.getDescription())
			print(string + " ");
		
		// Moves to the line if we have objects to print
		if(room.getObjectCount() > 0)
			println();
	
		// Prints all objects that are in the current room
		for(int i = 0; i < room.getObjectCount(); i++ ) {
			AdvObject obj = room.getObject(i);
	
			// Print object's name and description
			print(" - " + obj.getName() + ": " + obj.getDescription() );
	
			// Used to ensure that an empty line isn't generate on the console 
			if(i < room.getObjectCount() - 1)
				println();
		}
		
		// Prints hints if a hint has already been used in this room
		if(room.hintHasBeenUsed()){
			println();
			printHints();
			print("(You've previously used a hint in this room.)");
		}
		
	}

/** Stylized the console background and text **/
	private void pimpConsole(){
	
		setTitle("Adventure Game - Micah & William");
	
		console.setBackground(Color.BLACK);
		console.setForeground(new Color(45, 227, 114));
		console.setFont(new Font("Will_Micah_Font", Font.BOLD, 15));
		console.setInputColor(Color.WHITE);
		console.setInputStyle(Font.BOLD);
	
	}
	
/** Concatenate all Strings in String[], starting at a given index **/
	private String concatenateStrings(String[] arr, int index){

		String str = "";

		for(int i = index; i < arr.length; i++)
			str += arr[i] + " ";

		// Trim whitespace
		str = str.trim();

		// Returns null if the str is empty, otherwise it returns the str
		return str.equals("") ? null : str;
	}
	
}