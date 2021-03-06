package thinktank.simulator.entity;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Ring;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.material.Material;

import thinktank.simulator.Starter;
import thinktank.simulator.environment.Environment;
import thinktank.simulator.main.Main;
import thinktank.simulator.scenario.Scenario;
import thinktank.simulator.util.CichlidRelationships;

/**
 * Class representing a specific type of <code>Fish</code> object, which is a
 * type of entity.
 * 
 * @author Bob Thompson, Vasher Lor, Jonathan Coffman
 * @version %I%, %G%
 */
public class Cichlid extends Fish implements IMoving{
	/**
	 * List of possible colors and the associated texture 
	 * for the Cichlid class
	 */
	public enum POSSIBLE_COLORS{
		
		BLACK("Black",Color.BLACK,"Cichlid/CichlidTextDark.jpg"),
		BLUE("Blue",Color.BLUE, "Cichlid/CichlidTextBlue.jpg"),
		DEFAULT("Default",Color.WHITE, "Cichlid/CichlidText.jpg"),
		BLUEFIN("Blue Fins",Color.BLUE, "Cichlid/BlueFin.jpg"),
		BRIGHTWHITES("Bright White",Color.WHITE, "Cichlid/BrightWhitesDarkBlacks.jpg"),
		VIBRANTBLUE("Vibrant Blue",Color.BLUE, "Cichlid/VibrantBlue.jpg");
		/**
		 * Name of Cichlid color
		 */
		public final String NAME;
		/**
		 * Java.awt.Color value for the Cichlid
		 */
		public final Color COLOR;
		/**
		 * JPG file for the texture of Cichlid
		 */
		public final String TEXTURE;
		
		private POSSIBLE_COLORS(String name, Color color, String texture){
			this.NAME = name;
			this.COLOR = color;
			this.TEXTURE = texture;
		}//end of enum constructor
		
	}//end of POSSIBLE_COLORS enum
	
	/**
	 * List of possible sizes of Cichlid 
	 */
	public enum POSSIBLE_SIZES{
		
		SMALL("Small (2in)",2.0f),
		MEDIUM("Medium (2.51969in)",2.51969f),
		LARGE("Large (3in)",3.0f);
		
		/**
		 * Name of the size value.
		 */
		public final String NAME;
		/**
		 * Length value of the size in inches.
		 */
		public final float LENGTH_INCHES;
		
		private POSSIBLE_SIZES(String name, float lengthInches){
			this.NAME = name;
			this.LENGTH_INCHES = lengthInches;
		}//end of enum constructor
		
	}//end of POSSIBLE_SIZES enum
	
	//---------------------static constants----------------------------
	private static final long serialVersionUID = 8763564513637299079L;
	/**
	 * The value for the size of the cichlid model on the Z-axis.
	 */
	private static final float MODEL_DEPTH = 2f;//z-axis
	/**
	 * 
	 */
	private static final float OBJECT_DISTANCE = 30;

	//---------------------static variables----------------------------
	/**
	 * Determines the aggression threshold requirement
	 */
	private static  double AGGRESSION_THRESHOLD = 1.998;
	/**
	 * Provides weight based on the distance and what
	 * impact it has on interacting with other fish
	 */
	private static double DISTANCE_WEIGHT = 1.005;
	/**
	 * Provides weight based on the size and what
	 * impact it has on interacting with other fish
	 */
	private static double SIZE_WEIGHT = 1.002;
	/**
	 * Provides weight based on the speed and what
	 * impact it has on interacting with other fish
	 */
	private static float SPEED_WEIGHT = 1;
	
	//---------------------instance constants--------------------------
	//---------------------instance variables--------------------------
	/**
	 * The <code>POSSIBLE_SIZES</code> value representing the size 
	 * of the cichlid.
	 */
	private POSSIBLE_SIZES pSize;
	/**
	 * The <code>POSSIBLE_COLORS</code> value representing the color 
	 * of the cichlid.
	 */
	private POSSIBLE_COLORS pColor;
	/**
	 * A reference to the grid values for the scenario.
	 */
	private Vector3f[][][] gridXYZ;
	/**
	 * Stores the most recently calculated relationships of this 
	 * cichlid to all other objects in the scenario, allowing them 
	 * to be referenced within the same frame without recalculating.
	 */
	private HashMap<Long,CichlidRelationships> currentRelationships;
	/**
	 * The animation channel for the cichlid.
	 */
	private AnimChannel channel;
	/**
	 * The animation control for the cichlid.
	 */
	private AnimControl control;
	/**
	 * The ghost attached to this cichlid.
	 */
	private FishGhost ghost;
	/**
	 * The vector representing the destination the cichlid is 
	 * traveling to.
	 */
	private Vector3f destination;
	/**
	 * The vector representing the movement location of the cichlid.
	 */
	private Vector3f loc;
	/**
	 * A reference to the material for the cichlid's model.
	 */
	private Material mat;
	/**
	 * The environment object the cichlid is trying to hide behind.
	 */
	private EnvironmentObject shelterObject;
	/**
	 * The glow color for the cichlid.
	 */
	private ColorRGBA glowColor;
	/**
	 * Flag for whether or not the cichlid is at its desired location.
	 */
	private boolean atLoc;
	/**
	 * Flag for whether or not a collision was detected.
	 */
	private boolean collisionDetected;
	/**
	 * Flag for whether or not the cichlid has a destination.
	 */
	private boolean hasDestination;
	/**
	 * The X-coordinate on the grid for the cichlid.
	 */
	private int gridX;
	/**
	 * The Y-coordinate on the grid for the cichlid.
	 */
	private int gridY;
	/**
	 * The Z-coordinate on the grid for the cichlid.
	 */
	private int gridZ;
	/**
	 * The time for the cichlid to remain idle.
	 */
	private float idleTimer;
	/**
	 * Value for calculating the cichlid's idle movement.
	 */
	private float idleSine;
	/**
	 * Time since the cichlid's last action.
	 */
	private float elapsed;
	/**
	 * The original speed of the cichlid.
	 */
	private float originalSpeed;
	/**
	 * A weighting value for the cichlid's shelter mechanism.
	 */
	private double shelterWeight;

	//fish is 10cm long, 4.5cm tall, 2.5cm wide in blender
	//the orge file seems to have scaled the model to 2 world units.

	//---------------------constructors--------------------------------
	/**
	 * Constructor for creating a cichlid object with default values.
	 */
	public Cichlid(){
		init();
		pSize = POSSIBLE_SIZES.SMALL;
		pColor = POSSIBLE_COLORS.BLACK;
		setSize(pSize.LENGTH_INCHES);
	}//end of default constructor

	/**
	 * Constructor for creating a cichlid object with the specified values 
	 * for size, speed, and sex.
	 * 
	 * @param size the size value for this cichlid.
	 * @param speed the speed value for this cichlid.
	 * @param sex the set value for this cichlid.
	 */
	public Cichlid(POSSIBLE_SIZES size, float speed, String sex){
		init();
		pSize = size;
		pColor = POSSIBLE_COLORS.BLACK;
		setSize(pSize.LENGTH_INCHES);
		setSpeed(speed);
		setSex(sex);
	}//end of (float,float,String) constructor

	/**
	 * Constructor for creating a cichlid object with the specified values 
	 * for size, speed, sex, and name.
	 * 
	 * @param size the size value for this cichlid.
	 * @param speed the speed value for this cichlid.
	 * @param sex the sex value for this cichlid.
	 * @param name the name for this cichlid
	 */
	public Cichlid(POSSIBLE_SIZES size, float speed, String sex, String name){
		init();
		pSize = size;
		pColor = POSSIBLE_COLORS.BLACK;
		setSize(pSize.LENGTH_INCHES);
		setSpeed(speed);
		setSex(sex);
		setName(name);
	}//end of (float,float,String,String) constructor

	//---------------------instance methods----------------------------
	//GETTERS
	/**
	 * Returns a reference to the ghost attached to the cichlid.
	 * 
	 * @return the ghost for the cichlid.
	 */
	public FishGhost getGhost(){
		return ghost;
	}//end of getGhost method
	
	/**
	 * Returns the <code>POSSIBLE_SIZES</code> value representing the 
	 * cichlid's size.
	 * 
	 * @return the size for the cichlid.
	 */
	public POSSIBLE_SIZES getPSize(){
		return pSize;
	}//end of getPSize method

	/**
	 * Returns the <code>POSSIBLE_COLORS</code> value representing the 
	 * cichlid's color.
	 * 
	 * @return the color for the cichlid.
	 */
	public POSSIBLE_COLORS getPColor(){
		return pColor;
	}//end of getPColor method

	//SETTERS
	/**
	 * Sets the cichlid's size to the specified <code>POSSIBLE_SIZES</code> value.
	 * 
	 * @param size the size value to be set.
	 */
	public void setSize(POSSIBLE_SIZES size){
		if(size != null){
			pSize = size;
			setSize(pSize.LENGTH_INCHES);
			setDimensions();
		}
	}//end of setSize(POSSIBLE_SIZES) method

	/**
	 * Sets the cichlid's color to the specified <code>POSSIBLE_COLORS</code> value.
	 * 
	 * @param size the color value to be set.
	 */
	public void setColor(POSSIBLE_COLORS color){
		if(color != null){
			pColor = color;
			setColor(pColor.COLOR);
			Material cichlidMat = new Material(Main.asset_manager, "Common/MatDefs/Misc/Unshaded.j3md");
			cichlidMat.setTexture("ColorMap", Main.asset_manager.loadTexture(new TextureKey(color.TEXTURE, false)));
			getObj().setMaterial(cichlidMat);
		}
	}//end of setColor(POSSIBLE_COLORS) method

	/**
	 * Sets the cichlid to glow or not, as specified.
	 * 
	 * @param the state to which the cichlid's glow is to be set.
	 */
	public void setGlow(boolean glow){
		if(glow){
			mat.setColor("GlowColor", glowColor);
		}
		else{
			mat.setColor("GlowColor", ColorRGBA.Black);
		}
	}//end of setGlow method

	/**
	 * Sets the color that cichlid will glow when its glow is 
	 * next activated.
	 * 
	 * @param color the glow color.
	 */
	public void setGlowColor(ColorRGBA color){
		glowColor = color;
	}//end of setGlowColor method

	//OPERATIONS
	/**
	 * Initializes the values of this <code>Cichilid</code> object and 
	 * prepares it to be displayed in the environment.
	 */
	private void init(){
		atLoc = false;
		collisionDetected = false;
		hasDestination = false;
		idleSine = 90;
		
		currentRelationships = new HashMap<Long,CichlidRelationships>();
		setSpeed(1.5f + 2 * Main.RNG.nextFloat());
		originalSpeed = this.getSpeed();
		setSize(pSize);
		idleTimer = Main.RNG.nextFloat();
		
		setObj(Main.asset_manager.loadModel("Cichlid/Cube.mesh.xml"));
		Material cichlidMat = new Material(Main.asset_manager, "Common/MatDefs/Misc/Unshaded.j3md");
		cichlidMat.setTexture("ColorMap", Main.asset_manager.loadTexture(new TextureKey("Cichlid/CichlidTextDark.jpg", false)));
		getObj().setMaterial(cichlidMat);
		mat = cichlidMat;
		glowColor = ColorRGBA.Yellow;
		
		setDimensions();
		attachGhost();//collision radius
		this.setTimeControl(Main.RNG.nextInt(10));//this sets the starting random time interval for behavior decision
		
		//animation stuff
		control = getObj().getControl(AnimControl.class);
		control.addListener(this);
		channel = control.createChannel();
		channel.setAnim("Float", 2f);
		channel.setLoopMode(LoopMode.Loop);

		gridX = Main.RNG.nextInt(10);
		gridY = Main.RNG.nextInt(10);
		gridZ = Main.RNG.nextInt(10);
		gridXYZ = Main.getGrid().getGrid();
		destination = gridXYZ[gridX][gridY][gridZ];
		loc = destination;
	}//end of init method

	/**
	 * Calculates and sets the values for the dimensions of this 
	 * cichlid in the environment.
	 */
	private void setDimensions(){
		worldUnitDepth = Environment.inchesToWorldUnits(this.getSize());
		float sizeFactor = worldUnitDepth / MODEL_DEPTH;
		getObj().setLocalScale(1.0f);
		getObj().scale(sizeFactor);
	}//end of setDimensions method
	
	/**
	 * This creates a mesh for the object and places it on top of the model.
	 * TODO Potenially remove - VASH
	 */
	private void attachGhost(){
		CollisionShape ghostShape = CollisionShapeFactory.createDynamicMeshShape(getObj());
		ghost = new FishGhost(ghostShape, this);
		getObj().addControl(ghost);
		Starter.getClient().getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(ghost); //TODO convert to 
	}//end of attachGhost method

	/**
	 * Moves the fish based on time per frame
	 */
	@Override
	public void move(float tpf){
		if (atLoc){
			getDestination();
			if (idleTimer > 0){
				idleTimer -= tpf;
				hover(tpf);
			}
			else if (idleTimer <= 0){
				atLoc = false;
			}
		}
		else {
			hasDestination = false;
			if (getGhost().getOverlappingCount() > 0){
				this.behavioralMovement(tpf);
				avoid(tpf);
			}
			else moveToLoc(tpf, loc);
		}
	}//end of move method
	
	/**
	 * Uses a sine wave and tpf to calculate hovering motion
	 * @param tpf
	 */
	private void hover(float tpf){
		idleSine += tpf * 2;
		float sineWave = (float)Math.sin(idleSine) / 10000;
		Vector3f yPos = getObj().getLocalTranslation();
		yPos.setY(yPos.getY() + sineWave);
		getObj().setLocalTranslation(yPos);
	}//end of hover method

	/**
	 * Determines a new destination for the cichlid's movement.
	 */
	private void getDestination(){
		if(!hasDestination){
			idleTimer = Main.RNG.nextFloat();
			gridX = getNextPoint(gridX);
			gridY = getNextPoint(gridY);
			gridZ = getNextPoint(gridZ);
			loc = gridXYZ[gridX][gridY][gridZ];
			hasDestination = true;
		}
	}//end of getDestination method
	
	/**
	 * Movement by spherical linear interpolation.
	 * 
	 * @param tpf time per frame.
	 */
	@SuppressWarnings("unused")
	private void slerpIt(float tpf){
		Quaternion result = getObj().getLocalRotation();
		Quaternion look = Quaternion.IDENTITY;
		look.lookAt(loc, Vector3f.UNIT_Y);
		Quaternion test = new Quaternion().slerp(result, look, tpf * 5);
		getObj().setLocalRotation(test);
	}//end of slerpIt method
	
	/**
	 * Avoidance algorithm. Uses ghost to gather potential collision objects.
	 * Ray casts towards destination to detect possible collisions to avoid.
	 * 
	 * @param tpf time per frame
	 */
	private void avoid(float tpf){
		Vector3f collisionPos = new Vector3f();
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(getNextLoc(tpf), loc);
		CollisionResult closest = new CollisionResult();
		Node collision = new Node();
		for(int i=1; i<=getGhost().getOverlappingCount(); i++){ //Determines if anything is near by
			PhysicsCollisionObject colObj = getGhost().getOverlappingObjects().get(i - 1);
			if(colObj instanceof FishGhost){
				FishGhost fishGhost = (FishGhost)getGhost().getOverlappingObjects().get(i - 1);
				if(fishGhost.getOwner() instanceof Player){//if colliding with player
					moveAround(tpf, fishGhost.getOwner().getObj().getWorldTranslation());
				}
				else{//if colliding with other fish
					Spatial spatial = fishGhost.getOwner().getObj();
					collision.attachChild(spatial);
				}
			}
		}
		collision.collideWith(ray, results);
		if(results.size() > 0){
			// The closest collision point is what was truly hit:
			closest = results.getClosestCollision();
			collisionPos = closest.getGeometry().getWorldTranslation();
			collisionDetected = true;
		}
		else{
			collisionDetected = false;
		}

		//Need to add all spatials back to entity node to be rendered
		for(Spatial spatial : collision.getChildren()){
			Starter.getClient().getWorkingScenario().getEntityNode().attachChild(spatial);
		}
		if(collisionDetected){
			this.setGlow(true);
			moveAround(tpf, collisionPos);
		}
		else{
			this.setGlow(false);
			moveToLoc(tpf, loc);
		}
	}//end of avoid method
	
	/**
	 * This is the handler for behavioral movement. It receives the scenario object and handles iteration through all the objects
	 * in the tank and calls independent interaction methods for each item.
	 * 
	 * @param tpf time per frame
	 */
	private void behavioralMovement(float tpf){
		//this decides what action to take for the next random amount of time.
		this.decision();
		this.fishFinder();
		this.shelterFinder();
		if(collisionDetected){
			this.setBehavior(BEHAVIOR.RUN);
		}
		
		//handles the attack behavior. this will measure the aggression and find a target
		//it then lets the target fish know it is chasing it
		if(this.getBehavior() == BEHAVIOR.ATTACK){
			if(this.getTargetAggression() > AGGRESSION_THRESHOLD){
				if(this.getTargetAggression() > getTargetFish().getTargetAggression()){
					this.getTargetFish().setRun();
					this.getTargetFish().setSpeed(this.getSpeed() * (Main.RNG.nextFloat()));
					this.getTargetFish().setTargetFish(this);
					this.attack(tpf);
				}
			}
		}
		else if(this.getBehavior() == BEHAVIOR.HIDE){//Handles the decision to hide
			if(shelterWeight > 0){
				this.hide(shelterObject, tpf);
			}	
		}
		else if(this.getBehavior() == BEHAVIOR.RUN){//handles running
			this.run(tpf);
		}
		else if(this.getBehavior() == BEHAVIOR.LOITER){//handles loitering
			this.loiter(tpf);
		}
		else if(this.getBehavior() == BEHAVIOR.DART){//handles darting behavior
			this.dart(tpf);
		}
	}//end of behavioralMovement method

	/**
	 * This is where the Cichlid determines what his course of action will be. also this will be overridden if
	 * another fish attempts to attack him. it uses a random time interval between 0 and 10 seconds to 
	 * decide between Fish.BEHAVIOR options. if it is before the time limit is up, then the fish does not make a new 
	 * decision.
	 * 
	 * @param tpf time per frame
	 */
	private void decision(){
		//here we enter the loop based on a random amount of time and the fish decides what to do.
		if(Main.getTime() >= this.elapsed + this.getTimeControl()){
			if(this.getSpeed() != originalSpeed){
				this.setSpeed(originalSpeed);
			}
			if(this.getSpeed() == 0){
				this.setSpeed(1);
			}
			//reset the variables used for movement as well as the aggression level.
			this.elapsed = Main.getTime();//to cover the unlikely scenario of time change during the loop
			this.setTimeControl(Main.RNG.nextInt(4));
			setTargetAggression(0);
			setTargetFish(this);
			this.nextMove();
			System.out.println("Change Behavior: " + this.getBehavior());
		}	
	}//end of decision method

	/**
	 * Determines the cichlid's next move.
	 */
	private void nextMove(){
		int decision = Main.RNG.nextInt(4);
		if(decision == 0){
			this.setSpeed(this.getSpeed()*(Main.RNG.nextFloat()+1));
			this.setTimeControl(Main.RNG.nextInt(15)); //set a time that is long enough to allow the attack to happen
			this.setBehavior(BEHAVIOR.ATTACK);
		}
		else if(decision == 1 && !(this.getBehavior().equals(BEHAVIOR.HIDE))){
			this.setSpeed(this.getSpeed()*(Main.RNG.nextFloat()+1));
			this.setBehavior(BEHAVIOR.HIDE);
		}
		else if(decision == 2 && !(this.getBehavior().equals(BEHAVIOR.DART))){
			this.setSpeed(this.getSpeed()*(Main.RNG.nextFloat()+1));
			this.setBehavior(BEHAVIOR.DART);
		}
		else if(decision == 3 && !(this.getBehavior().equals(BEHAVIOR.LOITER))){
			this.setSpeed(this.getSpeed()*(Main.RNG.nextFloat()+1));
			this.setBehavior(BEHAVIOR.LOITER);
		}
		else{
			this.nextMove();
		}
	}//end of nextMove method

	/**
	 * This controls the fish action of darting.
	 * 
	 * @param tpf time per frame.
	 */
	private void dart(float tpf){
		this.run(tpf);
	}//end of dart method

	/**
	 * Simple Loiter behavior that stops the fish.
	 * 
	 * @param tpf time per frame.
	 */
	private void loiter(float tpf){
		hover(tpf);
		atLoc = true;
	}//end of loiter method
	
	private void fishFinder(){
		//Iterate through the fish and determine the aggression level for each fish
		Iterator<Fish> itrF = Starter.getClient().getWorkingScenario().getFish();
		if(itrF.hasNext()){
			//Here determine which fish is a target fish. If none targetAggression will remain 0
			while(itrF.hasNext()){
				Fish nextFish =itrF.next();
				if(this.getID() != nextFish.getID()){
					double nextAggression = fishInteract(nextFish);
					if(nextAggression > this.getTargetAggression() && nextAggression > AGGRESSION_THRESHOLD){
						this.setTargetAggression(nextAggression);
						this.setTargetFish(nextFish);
					}
				}
			}
		}
	}//end of fishFinder method
	
	/**
	 * Finds a suitable environment object for the cichlid to 
	 * hide behind.
	 */
	private void shelterFinder(){
		double shelterWeight = 0;
		Iterator<EnvironmentObject> itrO = Starter.getClient().getWorkingScenario().getEnvironmentObjects();
		//This has to be here so that it fish interaction occurs first and takes into account 
		while(itrO.hasNext()){
			EnvironmentObject nextObject = itrO.next();
			shelterWeight = objectInteract(nextObject);
			if(shelterWeight > 0){
				shelterObject = nextObject;
			}
		}
	}//end of shelterFinder method
	
	/**
	 * This Cichlid will hide near EnvironmentObjects.
	 * 
	 * @param shelterObject the object to hide behind.
	 * @param tpf time per frame.
	 */
	private void hide(EnvironmentObject shelterObject, float tpf){
		float xPos = this.getObj().getWorldTranslation().getX();
		float yPos = this.getObj().getWorldTranslation().getY();
		float zPos = this.getObj().getWorldTranslation().getZ();
		float xAvoid = this.getTargetFish().getObj().getWorldTranslation().getX();
		float yAvoid = this.getTargetFish().getObj().getWorldTranslation().getY();
		float zAvoid = this.getTargetFish().getObj().getWorldTranslation().getZ();
		float xShelter = shelterObject.getObj().getWorldTranslation().getX();
		float yShelter = shelterObject.getObj().getWorldTranslation().getY();
		float zShelter = shelterObject.getObj().getWorldTranslation().getZ();

		//here we are setting this fish as the origin and comparing two vectors to the other objects for manuever info
		float diffXAvoid = xAvoid - xPos;
		float diffYAvoid = yAvoid - yPos;
		float diffZAvoid = zAvoid - zPos;
		float diffXShelter = xShelter - xPos;
		float diffYShelter = yShelter - yPos;
		float diffZShelter = zShelter - zPos;
		Vector3f toAvoid = new Vector3f(diffXAvoid, diffYAvoid, diffZAvoid);
		Vector3f toShelter = new Vector3f(diffXShelter, diffYShelter, diffZShelter);
		float angle = toAvoid.angleBetween(toShelter);

		//Here the fish will look to see if the shelter object and the opponent fish are 
		//within a close proximity from its line of sight. It then checks to see if the pot is 
		//closer than the opponent fish and if it is it attempts to hide behind it. If it is not, 
		//it fails to attempt to hide behind the object.
		if(angle < Math.PI / 4){
			if(toShelter.length() < toAvoid.length()){
				if(shelterObject instanceof Pot){
					loc = gridXYZ[(int)xShelter][(int)yShelter][(int)zShelter];
					moveToLoc(tpf, loc); 
				}
				else{
					int newPositionX = getHidePosition(xShelter, xAvoid, OBJECT_DISTANCE);
					int newPositionY = getHidePosition(yShelter, yAvoid, OBJECT_DISTANCE);
					int newPositionZ = getHidePosition(zShelter, zAvoid, OBJECT_DISTANCE);
					//here we increase the speed a little bit to encourage a more realistic scenario.
					loc = gridXYZ[newPositionX][newPositionY][newPositionZ];
					moveToLoc(tpf, loc); 
				}
			}
		}
	}//end of hide method

	/**
	 * Determines the new position for destination based on the two points passed to it
	 * with the distance from the object added to the total.
	 * NOTE: this is only in D1, must call 3 times for each XYZ coordinate
	 * 
	 * @param shelter float
	 * @param avoid float
	 * @param distance float
	 * @return the point in D1 desired
	 */
	private int getHidePosition(float shelter, float avoid, float distance){
		int returnValue = (int)(shelter - distance);
		float newPosition = shelter - avoid;
		if(newPosition < shelter){
			returnValue = (int)(shelter + distance);
		}
		return returnValue;
	}//end of getHidePosition method

	/**
	 * Used by Cichlid to chase target. This method uses getChasingPoint to 
	 * determine where to move in order to chase target.
	 * 
	 * @param tpf time per frame.
	 */
	private void attack(float tpf){
		float xPos = this.getObj().getWorldTranslation().getX();
		float yPos = this.getObj().getWorldTranslation().getY();
		float zPos = this.getObj().getWorldTranslation().getZ();
		float xTarget = this.getTargetFish().getObj().getWorldTranslation().getX();
		float yTarget = this.getTargetFish().getObj().getWorldTranslation().getY();
		float zTarget = this.getTargetFish().getObj().getWorldTranslation().getZ();
		gridX = getChasingPoint(xPos, xTarget, gridX);
		gridY = getChasingPoint(yPos, yTarget, gridY);
		gridZ = getChasingPoint(zPos, zTarget, gridZ);
		//here we increase the speed a little bit to encourage a more realistic scenario.
		loc = gridXYZ[gridX][gridY][gridZ];//Using loc overwrites the old destination
		moveToLoc(tpf, loc);
	}//end of attack method

	/**
	 * Used by Cichlid to run away from target. This method uses getAvoidingPoint
	 * to determine the next location to run towards.
	 * 
	 * @param tpf time per frame.
	 */
	private void run(float tpf){
		float xPos = this.getObj().getWorldTranslation().getX();
		float yPos = this.getObj().getWorldTranslation().getY();
		float zPos = this.getObj().getWorldTranslation().getZ();
		float xAvoid = this.getTargetFish().getObj().getWorldTranslation().getX();
		float yAvoid = this.getTargetFish().getObj().getWorldTranslation().getY();
		float zAvoid = this.getTargetFish().getObj().getWorldTranslation().getZ();
		gridX = getAvoidingPoint(xPos, xAvoid, gridX);
		gridY = getAvoidingPoint(yPos, yAvoid, gridY);
		gridZ = getAvoidingPoint(zPos, zAvoid, gridZ);
		loc = gridXYZ[gridX][gridY][gridZ];//Using loc overwrites the old destination
		moveToLoc(tpf, loc);
	}//end of run method
	
	/**
	 * DO NOT CALL DIRECTLY: Use behavioralMovement() Handles the interactions with other 
	 * fish via range with a weight, size with a weight, and speed with a weight
	 * 
	 * @param opponent the other fish.
	 * TODO add tank temp to this calculation
	 */
	private double fishInteract(Fish opponent){
		double aggression = 0;
		aggression = (1 / calculateRelationships(opponent).getRange() * DISTANCE_WEIGHT);
		aggression = aggression + (this.getSize() / opponent.getSize() * SIZE_WEIGHT);
		aggression = aggression + (this.getSpeed() / opponent.getSpeed() * SPEED_WEIGHT);
		if(!this.getSex().matches(opponent.getSex())){
			aggression = aggression * 2; //This will accoint for different sex's with an attemot to mate
		}
		aggression = aggression * calculateRelationships(opponent).getVisibility(); //here we account for visibility 0 is blocked, 100 is visible
		aggression = 2 - (1 / aggression);
		return aggression;
	}//end of fishInteract method
	
	/**
	 * NOT YET IMPLEMENTED
	 * 
	 * DO NOT CALL DIRECTLY: Use behavioralMovement() 
	 * Handles the interaction with the cichlid object and the fish.  
	 * 
	 * @param EmvironmentObject next
	 * @return Double shelterWeight
	 */
	private double objectInteract(EnvironmentObject next){
		//TODO for additional control
		return 1;
	}//end of objectInteract method


	/**
	 * Used by Cichlid to run away from target. This method uses getAvoidingPoint
	 * to determine the next location to run towards.
	 * 
	 * @param tpf time per frame.
	 * @param p point to avoid.
	 */
	private void moveAround(float tpf, Vector3f p){
		float xPos = this.getObj().getWorldTranslation().getX();
		float yPos = this.getObj().getWorldTranslation().getY();
		float zPos = this.getObj().getWorldTranslation().getZ();
		gridX = getAvoidingPoint(xPos, p.x, gridX);
		gridY = getAvoidingPoint(yPos, p.y, gridY);
		gridZ = getAvoidingPoint(zPos, p.z, gridZ);
		loc = gridXYZ[gridX][gridY][gridZ];//Using loc overwrites the old destination
		moveToLoc(tpf, loc);
	}//end of moveAround method
	
	/**
	 * Used by avoidance algorithm to determine relative positions of colliding fishes. 
	 * This method compares the Cichlid's object to the target's position and 
	 * moves away from the target. This methods needs to be called for each axis.
	 * 
	 * @param pos this objects own coordinate position.
	 * @param avoid colliding fish's coordinate position.
	 * @param gridPos this objects position on the grid.
	 * @return new position to move to, on the grid.
	 */
	private int getAvoidingPoint(float pos, float avoid, int gridPos){
		int size = Main.getGrid().getSize();
		if(pos > avoid){
			gridPos++;
			if(gridPos >= size){
				gridPos--;
			}
		}
		else{
			gridPos--;
			if(gridPos < 0){
				gridPos++;
			}
		}
		return gridPos;
	}//end of getAvoidingPoint method
	
	/**
	 * Used by attack algorithm to find next movement on an axis. Must be called for each axis.
	 * This method compares this Cichlid's own position on an axis to 
	 * target's position. If the target's position is less than the Cichlid's position, 
	 * the Cichlid moves towards one position less than it's current location.
	 * 
	 * @param pos this objects own coordinate position.
	 * @param target Desired Targets coordinate position.
	 * @param gridPos this objects position on the grid.
	 * @return new position to move to, on the grid.
	 */
	private int getChasingPoint(float pos, float target, int gridPos){
		int size = Main.getGrid().getSize();
		if(target > pos){
			gridPos++;
			if(gridPos >= size){
				gridPos--;
			}
		}
		else{
			gridPos--;
			if(gridPos < 0){
				gridPos++;
			}
		}
		return gridPos;
	}//end of getDesiredPoint method

	/**
	 * Used to find next destination on 3d grid. Must be called for each axis. 
	 * The value returned is limited by arbitrary value <code>int</code> limit.
	 * 
	 * @param x 
	 * @return the next point.
	 */
	private int getNextPoint(int x){
		boolean add = Main.RNG.nextBoolean();
		int size = Main.getGrid().getSize();
		int limit = 5;
		if(add){
			if(x >= size - limit){
				x -= (Main.RNG.nextInt(limit) + 1);
			}
			else{
				x += (Main.RNG.nextInt(limit) + 1);
			}
		}
		else{
			if(x <= limit){
				x =+ (Main.RNG.nextInt(limit) + 1);
			}
			else{
				x = x - (Main.RNG.nextInt(limit) + 1);
			}
		}
		return x;
	}//end of getNextPoint method

	/**
	 * Rotate Cichlid towards its destination and moves towards it. 
	 * 
	 * @param tpf time per frame.
	 * @param location the location to move to.
	 */
	private void moveToLoc(float tpf, Vector3f location){
		Quaternion rot = new Quaternion();
		rot.lookAt(location, Vector3f.UNIT_Y);
		getObj().lookAt(location, Vector3f.UNIT_Y);
		getObj().setLocalTranslation(getNextLoc(tpf));
		ghost.setPhysicsLocation(getObj().getWorldTranslation());

		float testX = getObj().getWorldTranslation().getX();
		float testY = getObj().getWorldTranslation().getY();
		float testZ = getObj().getWorldTranslation().getZ();
		float deltX = Math.abs(testX - location.x);
		float deltY = Math.abs(testY - location.y);
		float deltZ = Math.abs(testZ - location.z);

		if(deltX < .01 && deltY < 0.01 && deltZ < 0.01){
			atLoc = true;
		}
		getObj().rotate(0, (float)(Math.PI / 2), 0);
		ghost.setPhysicsRotation(getObj().getWorldRotation());
	}//end of moveToLoc method

	/**
	 * Calculates an returns the relationships between the cichlid and 
	 * the specified entity.
	 * 
	 * @param entity the other entity to compare with the cichlid.
	 * @return the calculated relationships.
	 */
	public CichlidRelationships calculateRelationships(Entity entity){
		CichlidRelationships returnValue = null;
		if(currentRelationships.containsKey(entity.getID())){
			returnValue = currentRelationships.get(entity.getID());
		}
		else{
			returnValue = new CichlidRelationships(this,entity);
			returnValue.setVisibility(visibilityFactor(entity));
			returnValue.setRange(range(entity));
		}
		return returnValue;
	}//end of calculateRelationships method

	/**
	 * Returns the range between this instance of a cichlid and the specified 
	 * entity. Used by <code>calculateRelationships()</code>.
	 * 
	 * @param entity the other entity.
	 * @return the range value.
	 */
	private double range(Entity entity){
		double returnValue = 0;
		Vector3f loc = getObj().getLocalTranslation();
		Vector3f tar = entity.getObj().getLocalTranslation();
		returnValue = loc.distance(tar);
		return returnValue;
	}//end of range method

	/**
	 * Calculates a value (0-100) that represents the visibility between the 
	 * cichlid and the specified entity. A value of 0 is fully obstructed, while 
	 * a value of 100 is fully clear.
	 * 
	 * @param entity the other entity.
	 * @return the visibility factor value.
	 */
	private int visibilityFactor(Entity entity){
		int returnValue = 0;
		Vector3f loc = getObj().getLocalTranslation();
		Vector3f tar = entity.getObj().getLocalTranslation();
		Vector3f viewVec = tar.subtract(loc);
		Ring ring = new Ring(loc,viewVec,0,0.001f);
		ArrayList<Ray> rayList = new ArrayList<Ray>();
		rayList.add(new Ray(loc,viewVec));
		for(int i=0; i<50; i++){
			Vector3f origin = ring.random();
			rayList.add(new Ray(origin,viewVec));
		}
		for(Ray ray : rayList){
			Scenario scenario = Starter.getClient().getWorkingScenario();
			CollisionResults results = new CollisionResults();
			Node entityNode = scenario.getEntityNode();
			entityNode.collideWith(ray, results);
			if(results.size() > 0){
				CollisionResult closest = results.getClosestCollision();
				String closestName = closest.getGeometry().getName();
				Entity closestEntity = scenario.getEntity(closestName);
				if(closestEntity != null){
					if(closestEntity.equals(entity)){
						returnValue++;
					}
					else if(closestEntity.equals(this)){
						Iterator<CollisionResult> it = results.iterator();
						Entity nextClosest = null;
						float nextClosestDist = Float.POSITIVE_INFINITY;
						while(it.hasNext()){
							CollisionResult collision = it.next();
							Entity colEntity = scenario.getEntity(collision.getGeometry().getName());
							if(!colEntity.equals(this)){
								if(nextClosestDist > collision.getDistance()){
									nextClosestDist = collision.getDistance();
									nextClosest = colEntity;
								}
							}
						}
						if(nextClosest == null || nextClosest.equals(entity)){
							returnValue++;
						}
					}
				}
			}
			else{
				returnValue++;
			}
		}
		returnValue *= 2;
		return returnValue;
	}//end of visibilityFactor method

	/**
	 * Used to get next location to test before moving.
	 * 
	 * @param tpf time per frame.
	 * @return next location.
	 */
	private Vector3f getNextLoc(float tpf){
		Vector3f movement = new Vector3f();
		movement = new Vector3f(0, 0, tpf * getSpeed()); //TODO add multiplier for fast forward
		Vector3f move = getObj().localToWorld(movement, movement);
		return move;
	}//end of getNextLoc method

	/**
	 * Removes the ghost from the cichlid.
	 */
	public void removeGhost(){
		getObj().removeControl(ghost);	
		Starter.getClient().getStateManager().getState(BulletAppState.class).getPhysicsSpace().remove(ghost);
	}//end of removeGhost method

	/**
	 * Clears all the calculated relationships for the cichlid.
	 * 
	 * Note: should only be called in the final phase of update in Main.
	 */
	public void clearRelationships(){
		currentRelationships.clear();
	}//end of clearRelationships method
	
	/**
	 * NOT YET IMPLEMENTED
	 */
	@Override
	public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2){

	}//end of onAnimChange method

	/**
	 * NOT YET IMPLEMENTED
	 */
	@Override
	public void onAnimCycleDone(AnimControl control, AnimChannel channel, String anim){

	}//end of onAnimCycleDone method

	/**
	 * Reads in the data for this serializable object.
	 * 
	 * @param stream the input data to be read
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException{
		init();
		//values for Spatial local rotation
		float rotX = stream.readFloat();
		float rotY = stream.readFloat();
		float rotZ = stream.readFloat();
		float rotW = stream.readFloat();
		Quaternion rot = new Quaternion(rotX, rotY, rotZ, rotW);
		//values for Spatial local scale
		float scaleX = stream.readFloat();
		float scaleY = stream.readFloat();
		float scaleZ = stream.readFloat();
		Vector3f scale = new Vector3f(scaleX, scaleY, scaleZ);
		//values for Spatial local translate
		float transX = stream.readFloat();
		float transY = stream.readFloat();
		float transZ = stream.readFloat();
		Vector3f trans = new Vector3f(transX, transY, transZ);
		//set Spatial transform
		Transform xform = new Transform(trans, rot, scale);
		getObj().setLocalTransform(xform);
		POSSIBLE_SIZES size = (POSSIBLE_SIZES)stream.readObject();
		this.setSize(size);
		POSSIBLE_COLORS color = (POSSIBLE_COLORS)stream.readObject();
		this.setColor(color);
	}//end of readObject method
	

	/**
	 * Writes the data from this object to an output stream.
	 * 
	 * @param stream the output to be written to
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException{
		//values for Spatial local rotation
		stream.writeFloat(getObj().getLocalRotation().getX());
		stream.writeFloat(getObj().getLocalRotation().getY());
		stream.writeFloat(getObj().getLocalRotation().getZ());
		stream.writeFloat(getObj().getLocalRotation().getW());
		//values for Spatial local scale
		stream.writeFloat(getObj().getLocalScale().getX());
		stream.writeFloat(getObj().getLocalScale().getY());
		stream.writeFloat(getObj().getLocalScale().getZ());
		//values for Spatial local translate
		stream.writeFloat(getObj().getLocalTranslation().getX());
		stream.writeFloat(getObj().getLocalTranslation().getY());
		stream.writeFloat(getObj().getLocalTranslation().getZ());
		stream.writeObject(pSize);
		stream.writeObject(pColor);
	}//end of writeObject method

	/**
	 * Unused placeholder for object serialization.
	 * @throws ObjectStreamException
	 */
	@SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException{}//end of readObjectNoData method

	//---------------------static main---------------------------------
	//---------------------static methods------------------------------
}//end of Cichlid class