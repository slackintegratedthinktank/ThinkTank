package thinktank.simulator.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import thinktank.simulator.Starter;
import thinktank.simulator.entity.EntityFactory;
import thinktank.simulator.entity.Pot;

/**
 * 
 * @author Bob Thompson
 * @version %I%, %G%
 *
 */
public class AddPotAction extends AbstractAction{
	//---------------------static constants----------------------------
	private static final long serialVersionUID = -8335086092259791814L;
	public static final String NAME = "add-pot";
	
	//---------------------static variables----------------------------
	private static AddPotAction instance = null;
	
	//---------------------instance constants--------------------------
	//---------------------instance variables--------------------------
	//---------------------constructors--------------------------------
	private AddPotAction(){
		
	}//end of default constructor
	
	//---------------------instance methods----------------------------
	@Override
	public void actionPerformed(ActionEvent evt){
		Pot pot = EntityFactory.createPot();
		Starter.getClient().getWorkingScenario().addEnvironmentObject(pot);
		Starter.getClient().attachToRootNode(pot.getObj());
	}//end of actionPerformed method
	
	//---------------------static main---------------------------------
	//---------------------static methods------------------------------
	public static AddPotAction getInstance(){
		if(instance == null){
			instance = new AddPotAction();
		}
		return instance;
	}//end of getInstance method
	
}//end of AddPotAction class