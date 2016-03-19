package Game;
import java.security.SecureRandom;
import java.util.ArrayList;

/*****************************************************************************************
 * Class: Main
 * Purpose: Inititates the game entities and environment, also contains the update method
 * Author: Think Tank
 * Revisions:
 * 3/11/16 - JC - Added Class Header
 * 
 * 
 * 
 * 
 * 
 ****************************************************************************************/
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.sun.xml.internal.stream.Entity;

import gameAssets.*;
import thinktank.simulator.actions.AddFishAction;
import thinktank.simulator.actions.AddPlantAction;
import thinktank.simulator.actions.AddPotAction;
import thinktank.simulator.actions.SpinControlTEST;
import thinktank.simulator.entity.EntityFactory;
import thinktank.simulator.entity.Fish;
import thinktank.simulator.entity.Plant;
import thinktank.simulator.entity.Pot;
import thinktank.simulator.entity.collection.Iterator;
import thinktank.simulator.entity.collection.SimulatorCollection;
import thinktank.simulator.environment.Environment;
import thinktank.simulator.environment.TANK_TYPE;
import thinktank.simulator.environment.Tank;
import thinktank.simulator.scenario.Scenario;


public class Main extends SimpleApplication {
	public static final Vector3f WORLD_UP_AXIS = new Vector3f(0, 1, 0);
	public static final SecureRandom RNG = new SecureRandom();
	
	public static AssetManager am;
	/**
	 * @deprecated
	 */
	private static Player player;
	/**
	 * @deprecated
	 */
	private static Spatial table;
	/**
	 * @deprecated
	 */
	private static Tank tank;
	private static SimulatorCollection simCollection;
	private static Node environ_node;
	/**
	 * @deprecated
	 */
	private static Environment environment;
	
	/**
	 * @deprecated
	 */
	private Pot pot;
	/**
	 * @deprecated
	 */
	private Plant plant;
	
	private ArrayList<Scenario> scenarios;
	private int activeScenarioIndex;
	private Scenario workingScenario;
	
	public Main(){
		scenarios = new ArrayList<Scenario>();
		activeScenarioIndex = -1;
	}//end of default constructor
	
	public void attachToRootNode(Spatial obj){
		if(obj != null){
			rootNode.attachChild(obj);
		}
	}//end of attachToRootNode method
	
	public void removeFromRootNode(Spatial obj){
		if(obj != null){
			rootNode.detachChild(obj);
		}
	}//end of removeFromRootNode method
	
	public Scenario getWorkingScenario(){
		return workingScenario;
	}//end of getWorkingScenario method
	
	@Override
	public void simpleInitApp(){
		am = this.assetManager;
		simCollection = new SimulatorCollection();
		//TODO load saved scenarios
		workingScenario = new Scenario();
		
		//DEBUG
		showAxes();
		//END DEBUG
		
		//Add nodes to root
		rootNode.attachChild(workingScenario.getEnvironment().getEnvirionmentNode());
		rootNode.attachChild(workingScenario.getEnvironment().getTank().getNode());
		rootNode.attachChild(workingScenario.getEntityNode());
		
		//world elements
		makeSun();
		rootNode.attachChild(SkyFactory.createSky(
	            assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
		
		//setup inputs
		initInputs();
		
		//set initial camera position
		this.cam.setLocation(new Vector3f(-2, 0.1f, 0));//temp: for easier testing
		this.cam.lookAt(workingScenario.getEnvironment().getTank().getSpatial().getWorldBound().getCenter(), WORLD_UP_AXIS);
		//set (fovY, ratio, near, far)
		this.cam.setFrustumPerspective(60f, (float) cam.getWidth() / cam.getHeight(), 0.05f, 100f);
		flyCam.setMoveSpeed(1.2f);
	}//end of simpleInitApp method
	
	private void initInputs(){
		InputListener.getInstance();
	    inputManager.addMapping(AddPotAction.NAME,  new KeyTrigger(KeyInput.KEY_P));
	    inputManager.addMapping(AddPlantAction.NAME,   new KeyTrigger(KeyInput.KEY_L));
	    inputManager.addMapping(AddFishAction.NAME,  new KeyTrigger(KeyInput.KEY_K));
	    // Add the names to the action listener.
	    inputManager.addListener(InputListener.getInstance(), AddPotAction.NAME);
	    inputManager.addListener(InputListener.getInstance(), AddPlantAction.NAME);
	    inputManager.addListener(InputListener.getInstance(), AddFishAction.NAME);
		
	}//end of initInputs method

	/**
	 * @deprecated
	 */
	private void makePlayer(){
		player = Player.getPlayer();
		SpinControlTEST cont = new SpinControlTEST();
		player.addControl(cont);
		rootNode.attachChild(player.getObj());
		player.getObj().setLocalTranslation(0, 6f, 0);
	}

	/**
	 * @deprecated
	 */
	private void makeEnvironment(TANK_TYPE type){
		makeTable();
		environment = new Environment();
		tank = Tank.createTank(type);
		environment.setTank(tank);
		//move on top of table
		tank.getNode().setLocalTranslation(0, 4.675f, 0);
		environ_node = new Node();
		environ_node.attachChild(table);
		environ_node.attachChild(tank.getNode());
		rootNode.attachChild(environ_node);
	}
	/**
	 * @deprecated
	 */
	private void makeTable() {
		table = assetManager.loadModel("Table.obj");
		table.scale(1.5f);
	}
	/**
	 * @deprecated
	 */
	private void makePot() {
		pot = EntityFactory.createPot();
		simCollection.add(pot);
		rootNode.attachChild(pot.getObj());
	}
	/**
	 * @deprecated
	 */
	private void makePlant() {
		plant = EntityFactory.createPlant();
		simCollection.add(plant);
		rootNode.attachChild(plant.getObj());
	}
	private void makeSun() {
		DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-2f,-2f,-2f).normalizeLocal());
        sun.setColor(new ColorRGBA(2,2,2,0));
        rootNode.addLight(sun);
        
        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection(new Vector3f(2f,2f,2f).normalizeLocal());
        sun2.setColor(new ColorRGBA(2,2,2,0));
        rootNode.addLight(sun2);
	}

	/**
	 * Shows the X, Y, and Z axes for debug purposes.
	 */
	private void showAxes(){
		Line x = new Line(new Vector3f(0, 0, 0), new Vector3f(100, 0, 0));
		Line y = new Line(new Vector3f(0, 0, 0), new Vector3f(0, 100, 0));
		Line z = new Line(new Vector3f(0, 0, 0), new Vector3f(0, 0, 100));
		x.setLineWidth(1);
		y.setLineWidth(1);
		z.setLineWidth(1);
		Geometry geometryX = new Geometry("x", x);
		Geometry geometryY = new Geometry("y", y);
		Geometry geometryZ = new Geometry("z", z);
		Material green = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material red = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material blue = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		green.setColor("Color", ColorRGBA.Green);
		red.setColor("Color", ColorRGBA.Red);
		blue.setColor("Color", ColorRGBA.Blue);
		geometryX.setMaterial(green);            
		geometryY.setMaterial(red);            
		geometryZ.setMaterial(blue);                  
		rootNode.attachChild(geometryX);
		rootNode.attachChild(geometryY);
		rootNode.attachChild(geometryZ);
		//DEBUG 2
//		Line z1 = new Line(new Vector3f(0, 0, 1), new Vector3f(0, 100, 1));
//		Line z5 = new Line(new Vector3f(0, 0, 5), new Vector3f(0, 100, 5));
//		Line z10 = new Line(new Vector3f(0, 0, 10), new Vector3f(0, 100, 10));
//		z1.setLineWidth(1);
//		z5.setLineWidth(1);
//		z10.setLineWidth(1);
//		Geometry geometryZ1 = new Geometry("z1", z1);
//		Geometry geometryZ5 = new Geometry("z5", z5);
//		Geometry geometryZ10 = new Geometry("z10", z10);
//		geometryZ1.setMaterial(red);            
//		geometryZ5.setMaterial(red);            
//		geometryZ10.setMaterial(red);                  
//		rootNode.attachChild(geometryZ1);
//		rootNode.attachChild(geometryZ5);
//		rootNode.attachChild(geometryZ10);
		//END DEBUG 2
	}//end of showAxes method
	
	@Override
	public void simpleUpdate(float tpf){
		//tpf = time per frame
		
		super.simpleUpdate(tpf);
	}//end of simpleUpdate method

}//end of Main class
