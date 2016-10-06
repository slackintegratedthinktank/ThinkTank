package gameAssets;
import javax.swing.AbstractAction;

/*****************************************************************************************
 * Class: Player
 * Purpose: Create a player instance of a Cichlid
 * Author: Think Tank
 * Revisions:
 * 3/11/16 - JC - Added Class Header
 * 
 * 
 * 
 * 
 * 
 ****************************************************************************************/
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;

import Game.Main;
import Game.Main.CAM_MODE;
import thinktank.simulator.Starter;
import thinktank.simulator.environment.Tank;

public class Player extends Cichlid
{
	private static final long serialVersionUID = 4038460719382327559L;
	static private Player player;  //singleton
	private static Node node = null;
	private CameraNode cam;
	private BetterCharacterControl cc;
	private GhostControl ghost;

    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private boolean left = false, right = false, up = false, down = false,
    		forward = false, backward = false, ascend = false, descend = false,
    		upLock = false, downLock = false, rightLock = false, leftLock = false,
    		backwardLock = false, forwardLock = false;
    private Vector3f walkDirection = new Vector3f(0,0,0);
    private Vector3f viewDirection = new Vector3f(0,0,0);
    private boolean collision = false;
    private float deg = (float) (Math.PI/2);
    private float pitch;
    private Node collidables;
	
	private Player(float size, float speed, String sex)
	{
		super(size, speed, sex);
		node.attachChild(super.getNode());
		node.rotate(0, (float) (Math.PI/2), 0);
		super.setName("Player");
		//getGhost().setPhysicsRotation(node.getLocalRotation());
		//rotate object 180 degrees to correct orientation
	}
	
	static public Player getPlayer()
	{
		if(player == null){
			node = new Node();
			player = new Player(1, 1, "male");
			//player.getNode().rotate(0, (float) (Math.PI/2), 0);
			player.getObj().rotate(0, (float) (Math.PI/2), 0);
			//player.getGhost().setPhysicsRotation(player.getObj().getWorldRotation());
			//player.getPhysicsControl().setKinematic(true);
		}
			
		return player;
	}
	
	/*
	 * attach camera from main to player to be used by cichlid controller
	 * offset the spatial in the z direction
	 * attaching the player node to camNode creates the broken rotation
	 */
	
	public void attachCam(CameraNode camera){
		
	}
	
	public Node getNode(){
		if (node == null){
			System.out.println("There is no player fish");
			return null;
		}
		else return node;
	}
	
	public void update(float tpf){
		collidables = new Node();
		Vector3f old = player.getNode().getWorldTranslation();
		Vector3f reset = new Vector3f(0, .25f, 0);
		Vector3f move = player.getNode().getWorldTranslation();
		Vector3f movement = new Vector3f();
		Vector3f test = new Vector3f();

    	test = player.getNode().localToWorld(getNextLoc(tpf),getNextLoc(tpf));
    	collidables = testCollision(test);
		if (!collision){
			movement = getNextLoc(tpf);
		}
		else if (collision){
			//TODO collision stuff
			for (Spatial s : collidables.getChildren()){
				if (s.getName().contains("plant")){
					System.out.println("Plant");
					movement = avoidCollision(tpf);
				}
				else if (s.getName().contains("pot")){
					System.out.println("Pot");
					movement = avoidCollision(tpf);
				}
				else if (s.getName().contains("cichlid")){
					System.out.println("Cichlid");
					
				}
			}
		}
		move = player.getNode().localToWorld(movement,movement);
        
		if (upLock) { move.setY(old.y - 0.00015f); }
		if (downLock) { move.setY(old.y + 0.00015f); }
		if (leftLock) { move.setX(old.x + 0.00015f); }
		if (rightLock) { move.setX(old.x - 0.00015f); }
		if (forwardLock) { move.setZ(old.z + 0.00015f); }
		if (backwardLock) { move.setZ(old.z - 0.00015f); }

		rotateObj(tpf);
		player.getNode().setLocalTranslation(move);
	    player.getGhost().setPhysicsRotation(player.getObj().getWorldRotation());
		//player.getPhysicsControl().setPhysicsLocation(player.getObj().getWorldTranslation());
	    
		left = false;
        right = false;
        up = false;
        down = false;
        
		upLock = false;
		downLock = false;
		leftLock = false;
		rightLock = false;
		forwardLock = false;
		backwardLock = false;
		
	}
	
	private Vector3f avoidCollision(float tpf) {
		Vector3f movement = new Vector3f();
        if (forward) {
        	//base forward movement, should use fish speed. 
        	movement = new Vector3f(0,0,tpf*.25f);
        }
        else if (backward) {
    		movement = new Vector3f(0,0,-tpf*.1f);
        }
        if(player.isSprinting()){
    		//double movement speed
    		movement.setZ(movement.getZ()*2);
    	}
        movement.setZ(movement.getZ()/2);
        return movement;
	}

	/**
	 * Used to test collisions with player's ghost
	 * @param loc, location of player
	 * @return boolean 
	 */
	private Node testCollision(Vector3f loc){
		Node col = new Node();
		Spatial testObj = player.getNode().clone();
		testObj.setLocalTranslation(loc);
		Node test = Starter.getClient().getWorkingScenario().getEntityNode();
		for (Spatial s : test.getChildren()){
			if (s instanceof Node){
				Node t = (Node) s;
				for (Spatial p : t.getChildren()){
					if (testObj.getWorldBound().intersects(p.getWorldBound())){
						Spatial x = p.clone();
						col.attachChild(x);
					}
				}
			}
			else if (testObj.getWorldBound().intersects(s.getWorldBound())){
				Spatial x = s.clone();
				col.attachChild(x);
			}
		}
		if (!col.getChildren().isEmpty()){
			System.out.println("COLLISION WITH ");
			collision = true;
		}
		else collision = false;
		return col;
		
	}//end of testCollision method
	
	
	/**
	 * Used to get player's next location to test before moving
	 * @param tpf
	 * @return player's next location
	 */
	private Vector3f getNextLoc(float tpf){
		Vector3f movement = new Vector3f();
        if (forward) {
        	//base forward movement, should use fish speed. 
        	movement = new Vector3f(0,0,tpf*.25f);
        }
        else if (backward) {
    		movement = new Vector3f(0,0,-tpf*.1f);
        }
        if(player.isSprinting()){
    		//double movement speed
    		movement.setZ(movement.getZ()*2);
    	}
        return movement;
	}//end of getNextLoc method
	

	/**
	 * Rotates player, uses tpf to calculate rotation
	 * @param tpf
	 */
	private void rotateObj(float tpf){
		//TODO Need to add variance into turning, test if for value of left/right/up/down
		if (left) {
            deg -= 250f * tpf;
        }
        if (right) {
            deg += 250f * tpf;
        }
        if (up){
        	if (pitch < 45f){
        		pitch += 100f * tpf;
        	}
        }
        if (down){
        	if (pitch > -45f){
        		pitch -= 100f * tpf;
        	}
        }
        Vector3f point = getPoint(deg, pitch, .15f);
        player.getCam().setLocalTranslation(point);
        player.getCam().lookAt(player.getObj().getWorldTranslation(), Vector3f.UNIT_Y);
        
		if (cam.isEnabled()){
			player.getNode().setLocalRotation(player.getCam().getWorldRotation());
			//player.getObj().rotate(0, (float) (Math.PI/2), 0);
			//player.getPhysicsControl().setPhysicsRotation(player.getNode().getLocalRotation());
		}
	}//end of rotateObj method
	
	/**
	 * getPoint() returns position of camera based on a circle around
	 * player using float deg and float radius
	 * where deg = angle of camera from player and radius = distance from player
	 * @param degrees
	 * @param radius
	 * @return Vector3f position of camera
	 */
    private Vector3f getPoint(float degrees, float pitch, float radius){
    	Vector3f pos = new Vector3f();

        double rads = Math.toRadians(degrees - 90); // 0 becomes the top
        double r = Math.toRadians(pitch - 90); // 0 becomes the top
        
        float x = player.getObj().getWorldTranslation().getX();
        float y = player.getObj().getWorldTranslation().getY();
        float z = player.getObj().getWorldTranslation().getZ();
        
        pos.setX((float) (x + Math.cos(rads) * radius));
        pos.setY((float) (y + Math.cos(r) * radius));
        pos.setZ((float) (z + Math.sin(rads) * radius));

        return pos;
    }//end of getPoint method
	public void setCam(CameraNode fishCam) {
		cam = fishCam;
	}

	public CameraNode getCam() {
		return cam;
	}

	public void setCollision(boolean b) {
		collision = true;		
	}

	public void setLeft(boolean b) {
		left = b;
	}
	public void setRight(boolean b) {
		right = b;
	}
	public void setForward(boolean b) {
		forward = b;
	}
	public void setBackward(boolean b) {
		backward = b;
	}
	public void setUp(boolean b) {
		up = b;
	}
	public void setDown(boolean b) {
		down = b;
	}
	public void setAscend(boolean b) {
		ascend = b;
	}
	public void setDescend(boolean b) {
		descend = b;
	}
	public void setUpLock(boolean b) {
		upLock = b;
	}
	public void setDownLock(boolean b) {
		downLock = b;
	}
	public void setLeftLock(boolean b) {
		leftLock = b;
	}
	public void setRightLock(boolean b) {
		rightLock = b;
	}
	public void setForwardLock(boolean b) {
		forwardLock = b;
	}
	public void setBackwardLock(boolean b) {
		backwardLock = b;
	}
}