package thinktank.simulator.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import thinktank.simulator.Starter;
import thinktank.simulator.entity.Fish;
import thinktank.simulator.entity.Player;
import thinktank.simulator.main.Main;

/**
 * 
 * @author Vasher Lor
 * @version %I%, %G%
 */
public class MoveBackward extends AbstractAction{
	//---------------------static constants----------------------------
	private static final long serialVersionUID = 2404049837412531037L;
	public static final String NAME = "move-backward";

	//---------------------static variables----------------------------
	/**
	 * Singleton instance for the action.
	 */
	private static MoveBackward instance = null;

	//---------------------instance constants--------------------------
	//---------------------instance variables--------------------------
	private Player fish;
	private Node obj;

	//---------------------constructors--------------------------------
	/**
	 * Constructs a basic, default <code>MoveBackward</code> action 
	 * with the specified <code>Player</code>.
	 */
	public MoveBackward(Player fish){
		this.fish = fish;
		if(fish != null){
			this.obj = fish.getNode();
		}
		else{
			this.obj = null;
		}
	}//end of (Player) constructor

	//---------------------instance methods----------------------------
	//OPERATIONS
	@Override
	public void actionPerformed(ActionEvent evt){
		Main client = Starter.getClient();
		if(fish != null && obj != null && !client.isInMenus() && client.getWorkingScenario() != null && client.getWorkingScenario().isEditingMode()){
			Vector3f newLoc = new Vector3f();
			Vector3f curLoc = new Vector3f(obj.getLocalTranslation());
		
			curLoc.addLocal(obj.getLocalRotation().getRotationColumn(0).mult(fish.getSpeed()/500));
			newLoc = curLoc;
			obj.setLocalTranslation(newLoc);
		}
	}//end of actionPerformed method

	//---------------------static main---------------------------------
	//---------------------static methods------------------------------
	public static MoveBackward getInstance(Player fish){
		instance = new MoveBackward(fish);
		return instance;
	}//end of getInstance(Player) method
	
	public static MoveBackward getInstance(){
		if (instance == null){
			System.out.println("Pass fish");
			return null;
		}
		else return instance;
	}//end of getInstance method
	
}//end of MoveBackward class