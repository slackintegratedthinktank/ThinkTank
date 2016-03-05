package Game;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

import gameAssets.*;
import thinktank.simulator.environment.Tank;


public class Main extends SimpleApplication {
	private static Player player;
	private static Spatial fish;
	private static Tank tank;
	
	public Main(){
		
	}//end of default constructor
	
	@Override
	public void simpleInitApp() {
		//create player
		player = Player.getPlayer();
		//load model of cichlid
		makeSun();
		makeCichlid(fish);
		flyCam.setMoveSpeed(5);
		
		//printouts to test that player exists and has attributes
		System.out.println(player.getSize());
		System.out.println(player.getSpeed());
		System.out.println(player.getSex());
		
		//initialize terrain, objects, other cichlids camera, and display.
		makeTank(tank);
		rootNode.attachChild(SkyFactory.createSky(
	            assetManager, "horizon.jpg", false));
	}
	
	private void makeTank(Tank tanks) {
		tank = Tank.getTank(this.assetManager);
		rootNode.attachChild(tank.getSpatial());
		
	}

	private void makeSun() {
		DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-2f,-2f,-2f).normalizeLocal());
        sun.setColor(new ColorRGBA(2,2,2,0));
        rootNode.addLight(sun);
	}

	private void makeCichlid(Spatial fish) {
		fish = assetManager.loadModel("Cichlid_v5.obj");
		fish.scale(.25f, .25f, .25f);
		fish.rotate(0, 45f, 0);
		rootNode.attachChild(fish);
	}

	public void simpleUpdate(float tpf){
		//tpf = time per frame
		super.simpleUpdate(tpf);
	}

}
