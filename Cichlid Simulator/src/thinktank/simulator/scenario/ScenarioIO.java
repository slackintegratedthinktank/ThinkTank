package thinktank.simulator.scenario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 
 * @author Bob Thompson
 * @version %I%, %G%
 */
public class ScenarioIO{
	//---------------------static constants----------------------------
	public static final String SCENARIO_FILE_EXTENSION = ".cichlid";
	
	//---------------------static variables----------------------------
	//---------------------instance constants--------------------------
	//---------------------instance variables--------------------------
	//---------------------constructors--------------------------------
	//---------------------instance methods----------------------------
	//---------------------static main---------------------------------
	//---------------------static methods------------------------------
	public static boolean saveScenario(Scenario scenario, File file){
		boolean returnValue = false;
		if(file == null){
			return false;
		}
		else{
			String filePath = file.getPath();
			if(!filePath.toLowerCase().endsWith(SCENARIO_FILE_EXTENSION)){
			    file = new File(filePath + SCENARIO_FILE_EXTENSION);
			}
		}
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			if(!saveScenario(scenario, oos)){
				//error
			}
			else{//success
				returnValue = true;
			}
		}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		finally{
			if(oos != null){
				try{
					oos.close();
				}
				catch(IOException ex){
					ex.printStackTrace();
				}
			}
			if(fos != null){
				try{
					fos.close();
				}
				catch(IOException ex){
					ex.printStackTrace();
				}
			}
		}
		
		return returnValue;
	}//end of saveScenario method
	
	private static boolean saveScenario(Scenario scenario, ObjectOutputStream out){
		boolean returnValue = false;
		try{
			out.writeObject(scenario);
			returnValue = true;
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		return returnValue;
	}//end of saveScenario method
	
	public static Scenario loadScenario(File file){
		Scenario returnValue = null;

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			returnValue = loadScenario(ois);
		}
		catch(FileNotFoundException ex){
			ex.printStackTrace();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		finally{
			if(ois != null){
				try{
					ois.close();
				}
				catch(IOException ex){
					ex.printStackTrace();
				}
			}
			if(fis != null){
				try{
					fis.close();
				}
				catch(IOException ex){
					ex.printStackTrace();
				}
			}
		}
		return returnValue;
	}//end of loadScenario method
	
	private static Scenario loadScenario(ObjectInputStream in){
		Scenario returnValue = null;
		try{
			returnValue = (Scenario)(in.readObject());
		}
		catch(IOException ex){
			returnValue = null;
			ex.printStackTrace();
		}
		catch(ClassNotFoundException ex){
			returnValue = null;
			ex.printStackTrace();
		}
		return returnValue;
	}//end of loadScenario method
	
}//end of ScenarioIO class