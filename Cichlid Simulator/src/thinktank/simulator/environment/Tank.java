package thinktank.simulator.environment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

import thinktank.simulator.main.Main;

/**
 * Represents the tank within which the fish and environment are contained.
 * 
 * @author Bob Thompson
 * @version %I%, %G%
 */
public class Tank{
	//---------------------static constants----------------------------
	/**
	 * Constant value for the default tank depth (on the x-axis).
	 */
	private static final float MODEL_DEPTH = 1;
	/**
	 * Constant value for the default tank height (on the y-axis).
	 */
	private static final float MODEL_HEIGHT = 1;
	/**
	 * Constant value for the default tank width (on the z-axis).
	 */
	private static final float MODEL_WIDTH = 1;
	//tank is 1m x 1m x 1m by default
	
	//---------------------static variables----------------------------
	//---------------------instance constants--------------------------
	//---------------------instance variables--------------------------
	/**
	 * The <code>Spacial</code> object representing the visual model of the 
	 * "front" wall of the tank.
	 */
	private Spatial wallFront;
	/**
	 * The <code>Spacial</code> object representing the visual model of the 
	 * "back" wall of the tank.
	 */
	private Spatial wallBack;
	/**
	 * The <code>Spacial</code> object representing the visual model of the 
	 * "left" wall of the tank.
	 */
	private Spatial wallLeft;
	/**
	 * The <code>Spacial</code> object representing the visual model of the 
	 * "right" wall of the tank.
	 */
	private Spatial wallRight;
	/**
	 * The visual terrain at the bottom of the tank.
	 */
	private TerrainQuad terrain;
	/**
	 * <code>Node</code> object for the tank. The other tank elements are attached 
	 * to this node.
	 */
	private Node tankNode;
	/**
	 * <code>Node</code> object for the terrain. The terrain is attached to this 
	 * node.
	 */
	private Node terrainNode;
	/**
	 * <code>RigidBodyControl</code> for the tank allowing for collisions between 
	 * fish and the tank.
	 */
	@SuppressWarnings("unused")
	private RigidBodyControl tankControl;
	/**
	 * The specific preset type of tank this tank represents.
	 */
	private TANK_TYPE type;
	/**
	 * Value for the tank depth (on the x-axis).
	 */
	private float worldUnitDepth;
	/**
	 * Value for the tank height (on the y-axis).
	 */
	private float worldUnitHeight;
	/**
	 * Value for the tank width (on the z-axis).
	 */
	private float worldUnitWidth;
	/**
	 * Value by which the depth of the tank model is related 
	 * to the depth of the tank type.
	 */
	private float depthFactor;
	/**
	 * Value by which the height of the tank model is related 
	 * to the depth of the tank type.
	 */
	private float heightFactor;
	/**
	 * Value by which the width of the tank model is related 
	 * to the depth of the tank type.
	 */
	private float widthFactor;
	/**
	 * Value (x) for the location of the tank in the game world.
	 */
	private float x;
	/**
	 * Value (y) for the location of the tank in the game world.
	 */
	private float y;
	/**
	 * Value (z) for the location of the tank in the game world.
	 */
	private float z;
	
	//---------------------constructors--------------------------------
	/**
	 * Constructs a basic, default tank.
	 */
	private Tank(){
		tankNode = new Node();
		wallFront = Main.asset_manager.loadModel("Tank/Wall.obj");
		wallFront.setName("wallFront");
		wallBack = Main.asset_manager.loadModel("Tank/Wall.obj");
		wallBack.setName("wallBack");
		wallLeft = Main.asset_manager.loadModel("Tank/Wall.obj");
		wallLeft.setName("wallLeft");
		wallRight = Main.asset_manager.loadModel("Tank/Wall.obj");
		wallRight.setName("wallRight");
		makeMap();
		tankNode.attachChild(wallFront);
		tankNode.attachChild(wallBack);
		tankNode.attachChild(wallLeft);
		tankNode.attachChild(wallRight);
		tankNode.attachChild(terrainNode);
		setType(TANK_TYPE.FIFTY_GAL);
		Vector3f loc = tankNode.getWorldTranslation();
		x = loc.x + depthFactor/2;
		y = loc.y + heightFactor;
		z = loc.z + widthFactor/2;
	}//end of default constructor

	/**
	 * Constructs a tank of the specified type.
	 * 
	 * @param type the type for the tank.
	 */
	private Tank(TANK_TYPE type){
		tankNode = new Node();
		wallFront = Main.asset_manager.loadModel("Tank/Wall.obj");
		wallFront.setName("wallFront");
		wallBack = Main.asset_manager.loadModel("Tank/Wall.obj");
		wallBack.setName("wallBack");
		wallLeft = Main.asset_manager.loadModel("Tank/Wall.obj");
		wallLeft.setName("wallLeft");
		wallRight = Main.asset_manager.loadModel("Tank/Wall.obj");
		wallRight.setName("wallRight");
		makeMap();
		tankNode.attachChild(wallFront);
		tankNode.attachChild(wallBack);
		tankNode.attachChild(wallLeft);
		tankNode.attachChild(wallRight);
		tankNode.attachChild(terrainNode);
		setType(type);
		Vector3f loc = tankNode.getWorldTranslation();
		x = loc.x + depthFactor/2;
		y = loc.y + heightFactor;
		z = loc.z + widthFactor/2;
	}//end of (TANK_TYPE) constructor
	
	//---------------------instance methods----------------------------
	//GETTERS
	/**
	 * Gets the model for this tank.
	 * 
	 * @return the model for this tank.
	 */
	public Spatial getSpatial(){
		return tankNode;
	}//end of getSpatial method
	
	/**
	 * Gets the tank type for this tank.
	 * 
	 * @return the tank type for this tank.
	 */
	public TANK_TYPE getType(){
		return type;
	}//end of getType method
	
	/**
	 * Gets the terrain for this tank.
	 * 
	 * @return the terrain for this tank.
	 */
	public TerrainQuad getTerrain(){
		return terrain;
	}//end of getTerrain method
	
	/**
	 * Gets the root node for this tank.
	 * 
	 * @return the root node for this tank.
	 */
	public Node getNode(){
		return tankNode;
	}//end of getNode method

	/**
	 * Gets the depth of this tank in world units.
	 * 
	 * @return the depth of this tank in world units.
	 */
	public float getWorldUnitDepth(){
		return worldUnitDepth;
	}//end of getWorldUnitDepth method

	/**
	 * Gets the height of this tank in world units.
	 * 
	 * @return the height of this tank in world units.
	 */
	public float getWolrdUnitHeight(){
		return worldUnitHeight;
	}//end of getWorldUnitHeight method

	/**
	 * Gets the width of this tank in world units.
	 * 
	 * @return the width of this tank in world units.
	 */
	public float getWorldUnitWidth(){
		return worldUnitWidth;
	}//end of getWorldUnitWidth method

	/**
	 * Gets the x coordinate of this tank.
	 * 
	 * @return the x coordinate of this tank.
	 */
	public float getX(){
		return x;
	}//end of getX method

	/**
	 * Gets the y coordinate of this tank.
	 * 
	 * @return the y coordinate of this tank.
	 */
	public float getY(){
		return y;
	}//end of getY method

	/**
	 * Gets the z coordinate of this tank.
	 * 
	 * @return the z coordinate of this tank.
	 */
	public float getZ(){
		return z;
	}//end of getZ method
	
	//SETTERS
	/**
	 * Sets the type for this tank to the specified type.
	 *  
	 * @param type the type to which this tank's type is to be set.
	 */
	public void setType(TANK_TYPE type){
		this.type = type;
		setDimensions();
	}//end of setTYpe method

	/**
	 * Sets the terrain for this tank to the specified 
	 * <code>TerrainQuad</code> object.
	 *  
	 * @param terrain the <code>TerrainQuad</code> object to which this 
	 * tank terrain is to be set.
	 */
	public void setTerrain(TerrainQuad terrain){
		this.terrain = terrain;
	}//end of setTerrain method
	
	//OPERATIONS
	/**
	 * Creates sand terrain using a 64bit heightMap.
	 */
	private void makeMap(){
		terrainNode = new Node();
		Material terrainMat = new Material(Main.asset_manager, "Common/MatDefs/Terrain/Terrain.j3md");
		terrainMat.setTexture("Alpha", Main.asset_manager.loadTexture("Terrain/Sand.jpg"));
		AbstractHeightMap heightmap = null;
		Texture heightmapImage = Main.asset_manager.loadTexture("Terrain/terrain3.bmp");
		heightmap = new ImageBasedHeightMap(heightmapImage.getImage());
		heightmap.load();
		terrain = new TerrainQuad("tankBase", 65, 513, heightmap.getHeightMap());
		terrain.setMaterial(terrainMat);
		terrain.rotate(0, 3.14159f, 0);
		terrain.setLocalScale(0.0019f, 0.000125f, 0.001925f);
		terrain.setName("terrain");
		terrainNode.attachChild(terrain);
		terrainNode.setName("terrainNode");
	}//end of makeMap method

	/**
	 * Scales the model based on the TANK_TYPE and the base model dimensions 
	 * defined in the constants <code>MODEL_WIDTH</code>, <code>MODEL_HEIGHT</code>, 
	 * and <code>MODEL_DEPTH</code>.
	 */
	private void setDimensions(){
		wallFront.scale(1, 1, .25f);
		wallBack.scale(1, 1, .25f);
		wallLeft.scale(1, 1, .25f);
		wallRight.scale(1, 1, .25f);
		
		worldUnitDepth = Environment.inchesToWorldUnits(type.DEPTH);
		worldUnitHeight = Environment.inchesToWorldUnits(type.HEIGHT);
		worldUnitWidth = Environment.inchesToWorldUnits(type.WIDTH);
		
		depthFactor = worldUnitDepth / MODEL_DEPTH;
		heightFactor = worldUnitHeight / MODEL_HEIGHT;
		widthFactor = worldUnitWidth / MODEL_WIDTH;
		wallLeft.scale(1, heightFactor, depthFactor);
		wallRight.scale(1, heightFactor, depthFactor);
		wallFront.scale(1, heightFactor, widthFactor);
		wallBack.scale(1, heightFactor, widthFactor);
		wallFront.setLocalTranslation(0, 0, .5f);
		wallBack.setLocalTranslation(0, 0, -.5f);
		wallLeft.setLocalTranslation(.5f, 0, 0);
		wallRight.setLocalTranslation(-.5f, 0, 0);
		
		wallLeft.rotate(0, (float) (Math.PI/2), 0);
		wallRight.rotate(0, (float) (Math.PI/2), 0);
		wallLeft.scale(widthFactor/depthFactor, 1, 1);
		wallRight.scale(widthFactor/depthFactor, 1, 1);
		tankNode.setLocalScale(depthFactor, 1, widthFactor);
	}//end of setDimensions method

	/**
	 * The readObject method is responsible for reading from the stream and restoring 
	 * the fields of the class.
	 * 
	 * @param stream the input stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException{
		setType((TANK_TYPE)(stream.readObject()));
	}//end of readObject method

	/**
	 * The writeObject method is responsible for writing the state of the object 
	 * so that the corresponding readObject method can restore it.
	 * 
	 * @param stream the output stream.
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream stream) throws IOException{
		stream.writeObject(type);
	}//end of writeObj}ect method
	
	@SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException{}//end of readObjectNoData method
	
	//---------------------static main---------------------------------
	//---------------------static methods------------------------------
	/**
	 * Creates a default <code>Tank</code> object.
	 * 
	 * @return the created tank.
	 */
	public static Tank createTank(){
		return new Tank();
	}//end of createTank method
	
	/**
	 * Creates a <code>Tank</code> object of the specified type.
	 * 
	 * @param type the type of tank to create.
	 * @return the created tank.
	 */
	public static Tank createTank(TANK_TYPE type){
		return new Tank(type);
	}//end of createTank(TANK_TYPE) method
	
}//end of Tank class