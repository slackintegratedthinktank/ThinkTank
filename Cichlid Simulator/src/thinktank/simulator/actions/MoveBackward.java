package thinktank.simulator.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import gameAssets.Player;
import thinktank.simulator.entity.Fish;

public class MoveBackward extends AbstractAction {
public static final String NAME = "move-backward";
	private static MoveBackward instance = null;
	private Player fish;
	private Node obj;
	
	public MoveBackward(Player fish){
		this.fish = fish;
		this.obj = fish.getNode();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Vector3f newLoc = new Vector3f();
		Vector3f curLoc = new Vector3f(obj.getLocalTranslation());
		
		curLoc.addLocal(obj.getLocalRotation().getRotationColumn(0).mult(fish.getSpeed()/500));
		newLoc = curLoc;
		obj.setLocalTranslation(newLoc);
	}

	public static MoveBackward getInstance(Player fish){
		instance = new MoveBackward(fish);
		return instance;
	}
	public static MoveBackward getInstance(){
		if (instance == null){
			System.out.println("Pass fish");
			return null;
		}
		else return instance;
	}
}
