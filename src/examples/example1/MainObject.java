package examples.example1;

import common.Logger.Logger;
import engine.Game;
import engine.GameSettings;
import engine.Global;
import engine.entity.*;
import engine.input.KeyboardHandler;
import engine.model.*;
import engine.model.loaders.FontLoader;
import engine.model.loaders.ObjLoader;
import engine.model.loaders.TerrainGenerator;
import engine.model.loaders.TextureLoader;
import engine.model.store.TexturedModelStore;
import engine.renderer.StaticRenderer;
import engine.scene.Scene;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import physics.PhysicsEngine;
import physics.PhysicsEntity;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;

public class MainObject extends Game {

    static Entity arrow;
    public static Text text;
    private LightSource sun;
    private PhysicsEntity cube1;
    private PhysicsEntity plane;
    private Text text2;
    private Text text3;
    private TexturedModel cubi;
    private PhysicsEntity cube2;

    public MainObject(GameSettings gameSettings) {
        super(gameSettings);
    }


    @Override
    public void load() {
        this.currentScene = new Scene();

        // Splashscreen tests
        /*
        Texture[] screens = {
                TextureLoader.loadTexture("res/splash/screen0.jpg"),
                TextureLoader.loadTexture("res/splash/screen25.jpg"),
                TextureLoader.loadTexture("res/splash/screen50.jpg"),
                TextureLoader.loadTexture("res/splash/screen75.jpg"),
                TextureLoader.loadTexture("res/splash/screen100.jpg")
        };

        for(int x=0;x<screens.length;x++){
            this.renderer.renderOneImageScreen(screens[x]);
            for (int i = 0; i < 9; i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        }
        */


        Terrain t = TerrainGenerator.generate(
                "res/heightmap.png", 500, 500, 50,
                TextureLoader.loadTexture("res/terrainTexture.png"));
        t.setPosition(new Vector3f(-250, -50, -250));
        this.currentScene.add(t);

        TexturedModel aliceModel = new TexturedModel(
                ObjLoader.loadObjFile("res/aliceV1.obj"),
                TextureLoader.loadTexture("res/aliceV1.png")
        );
        Entity e = new Entity();
        e.setRenderer(StaticRenderer.class);
        e.setModel(aliceModel);
        e.setPosition(new Vector3f(0, 0, 0));
        this.currentScene.add(e);

        WaterTile water = new WaterTile(new Vector3f(0, -10, 0), 300, this.currentScene);
        this.currentScene.add(water);

        /*
        GuiElement guiElementWater = new GuiElement(water.getRefractionFbo().getDepthTexture(),
                new Vector2f(-0.5f, -0.5f),
                new Vector2f(0.25f, 0.25f));
        this.currentScene.add(guiElementWater);
        */

        FontAtlas font = FontLoader.loadFromSys("Monaco");
        text = new Text(font);

        GuiElement guiElement = new GuiElement(text.getFbo().getTexture(), new Vector2f(-0.9f, 0.96f), new Vector2f(0.08f, 0.03f));
        this.currentScene.add(guiElement);

        text2 = new Text(font);
        GuiElement guiElement2 = new GuiElement(text2.getFbo().getTexture(), new Vector2f(-0.7f, 0.90f), new Vector2f(0.15f, 0.03f));
        this.currentScene.add(guiElement2);

        text3 = new Text(font);
        GuiElement guiElement3 = new GuiElement(text3.getFbo().getTexture(), new Vector2f(-0.7f, 0.90f), new Vector2f(0.15f, 0.03f));
        this.currentScene.add(guiElement3);


        sun = new LightSource(new Vector3f(0, 255, 0), new Vector3f(255, 255, 255), 3);
        this.currentScene.add(sun);

        LightSource ls2 = new LightSource(new Vector3f(0, 1, 20), new Vector3f(255, 0, 255), .2f);
        this.currentScene.add(ls2);

        // ----------------------------------------------------------------------
        // ----------------------------------------------------------------------
        // ----------------------------------------------------------------------

        Model cubeModel = ObjLoader.loadObjFile("res/cube.obj");
        Texture cubeTexture = TextureLoader.loadTexture("res/cubeTextur.png");
        TexturedModelStore.getInstance().add("test-cube", new TexturedModel(cubeModel, cubeTexture));

        cube1 = new TestCube(this.physicsEngine);
        this.currentScene.add(cube1);

        cube2 = new TestCube(this.physicsEngine);
        this.currentScene.add(cube2);


        plane = new PhysicsEntity(new Vector3f(-5f, 0.5f, -5f), new Vector3f(10, -5, 10));
        plane.setRenderer(StaticRenderer.class);
        plane.setModel(new TexturedModel(
                PlaneModel.load(),
                TextureLoader.loadTexture("res/cubeTextur.png")
        ));
        plane.setStatic(true);
        plane.setScale(5);
        plane.setElasticity(1);
        plane.setPosition(new Vector3f(20, 10, 0));
        this.physicsEngine.addEntity(plane);
        this.currentScene.add(plane);

    }

    public void tick(double deltaT) throws Exception {
        super.tick(deltaT);
        this.currentScene.getCamera().checkMovementInput(deltaT);

        //Physiscs controlling

        if (KeyboardHandler.isKeyDown(GLFW_KEY_O)) {
            cube1.setPosition(new Vector3f(20, 50, 0));
            cube1.setVelocity(new Vector3f(0));
            cube2.setPosition(new Vector3f(25, 50, 0));
            cube2.setVelocity(new Vector3f(0));
        }
        if (KeyboardHandler.isKeyDown(GLFW_KEY_I)) {
            cube1.setPosition(new Vector3f(20, -50, 0));
            cube1.setVelocity(new Vector3f(0));
            cube2.setPosition(new Vector3f(25, -50, 0));
            cube2.setVelocity(new Vector3f(0));
        }
        if (KeyboardHandler.isKeyDown(GLFW_KEY_P)) {
            cube1.setVelocity(new Vector3f(0, 0.5f, 0));
            cube2.setVelocity(new Vector3f(0, 0.5f, 0));
        }

        text.setText("FPS:" + this.fps);
        text2.setText("PD1: " + Global.data);
        text3.setText("PD2: " + Global.data2);
    }

    class TestCube extends PhysicsEntity {

        public TestCube(PhysicsEngine physicsEngine) {
            super(new Vector3f(-1f, 1f, -1f), new Vector3f(2, -2, 2));
            TexturedModel tm = TexturedModelStore.getInstance().get("test-cube");
            if (tm == null) {
                throw new Error("Textured Model test-cube not loaded!");
            }
            this.setRenderer(StaticRenderer.class);
            this.setModel(tm);
            physicsEngine.addEntity(this);
            this.setPosition(new Vector3f(20, -10, 0));
        }
    }

}
