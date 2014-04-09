/*
 * AdvObject -  This class defines an object in the Adventure game.  An object is
 * 							characterized by a name, description, and an initial location.
 * 
 * File: AdvObject.java
 * @author William Fiset, Micah Stairs
 * 
 */

import java.io.*;

public class AdvObject {
	
	/* Private instance variables */
	private String objectName;
	private String objectDescription;
	private int objectInitialLocation;
	 
 /**
  * @param objectName: Its name, which is the noun used to refer to the object
  * @param objectDescription: Its description, which is a string giving a short description
  * @param objectInitialLocation: The room number in which the object initially lives
  */

  public AdvObject(String objectName, String objectDescription, int initialLocation){

    this.objectName = objectName;
    this.objectDescription = objectDescription;
    this.objectInitialLocation = initialLocation;

  }

/** Returns the object name, which is the word used to refer to it **/
    public String getName() {
       return this.objectName;
	}

/** Returns the one-line description of the object **/
    public String getDescription() {
       return this.objectDescription;
    }

/** Returns the initial room index of the object **/
    public int getInitialLocation() {
       return this.objectInitialLocation;
    }

/**
 * Creates and returns a new object by reading its data from the specified reader.
 * If no data is left in the reader, this method returns null instead.
 *
 * @param rd The BufferedReader from which the object data is read 
 */
    public static AdvObject readObject(BufferedReader rd) {

        String name = null, description = null, initialLocationString = null;
        
        // Try to read in and store the object properties
        try{
            String line = rd.readLine();
            
            // Skips a line if a line is empty between objects, reading in the name
            if(line == null || line.equals(""))
                name = rd.readLine();
            else
                name = line;
            
            // Read in the description and initial location
            description = rd.readLine(); 
            initialLocationString = rd.readLine(); 

        }
        catch(IOException e){}

        // Return null to indicate the end of the object file
        if (name == null || description == null || initialLocationString == null)
            return null; 
        
        // Return instantiated AdvObject with the properties that were just read in from file
        int initialLocation = Integer.valueOf(initialLocationString.trim());
        return new AdvObject(name, description, initialLocation);

   }

}