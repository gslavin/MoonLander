package com.me.moonlander;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;
//import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
//import com.badlogic.gdx.utils.TimeUtils;

public class MoonLander implements ApplicationListener {

	SpriteBatch batch;
	OrthographicCamera camera;
	Texture spaceshipImage;
	Texture timeBarImage;
	Texture healthBarImage;
	Texture obstacle0Image;
	Texture obstacle1Image;
	Texture flameLeftImage;
	Texture flameRightImage;
	Texture flameDownImage;
	Texture backgroundImage;
	Sound dropSound;
	Music spaceMusic;
	SpaceShip spaceship;
	Rectangle healthBar;
	Rectangle timeBar;
	Rectangle obstacle1;
	Rectangle obstacle2;
	Array<Obstacle> obstacleList;
	Array<Sound> spaceSounds;
	long lastDropTime;
	int xVelo;
	int yVelo;
	int RIGHT_FLAME_OFFSET = 0;
	int LEFT_FLAME_OFFSET = 25;
	int DOWN_FLAME_OFFSET = 25;
	float time;
	float fuel;
	int score;
	Mesh mesh;
	ShapeRenderer shapeRenderer;
	int SCREEN_HEIGHT = 480;
	int SCREEN_WIDTH = 850;
	int BAR_THICKNESS = 5;
	int WALL_BOUNCE_VELO = 50;
	//offsets for the spaceship collisions
	int SPACESHIP_BUFFER_X = 15;
	int SPACESHIP_BUFFER_Y = 10;
	//subtracted from image to make collision rectangle
	int SPACESHIP_BUFFER_WIDTH = 40;
	int SPACESHIP_BUFFER_HEIGHT = 35;
	boolean pause = false;

	@Override
	public void create() {

		//set timer and fuel bars
		time = 400;
		fuel = 500;
		//set score
		score = 500;


		//create the shapeRenderer
		shapeRenderer = new ShapeRenderer();

		//allows textures who sizes are not powers of 2
		Texture.setEnforcePotImages(false);
		// load the images for the droplet and the bucket, 64x64 pixels each
		spaceshipImage = new Texture(Gdx.files.internal("graphics/lander.png"));
		obstacle0Image = new Texture(Gdx.files.internal("graphics/horizontalObject.png"));
		obstacle1Image = new Texture(Gdx.files.internal("graphics/verticalObject.png"));
		flameLeftImage = new Texture(Gdx.files.internal("graphics/leftFlame.png"));
		flameRightImage  = new Texture(Gdx.files.internal("graphics/rightFlame.png"));
		flameDownImage = new Texture(Gdx.files.internal("graphics/downFlame.png"));
		backgroundImage = new Texture(Gdx.files.internal("graphics/background.png"));

		// load the rain background "music"
		spaceMusic = Gdx.audio.newMusic(Gdx.files.internal("raining.mp3"));
		// load all the sound effects
		spaceSounds = new Array<Sound>();
		spaceSounds.add(Gdx.audio.newSound(Gdx.files.internal("ouch.mp3")));
		spaceSounds.add(Gdx.audio.newSound(Gdx.files.internal("noooo.mp3")));
		spaceSounds.add(Gdx.audio.newSound(Gdx.files.internal("ooo.mp3")));
		spaceSounds.add(Gdx.audio.newSound(Gdx.files.internal("theHell.mp3")));
		spaceSounds.add(Gdx.audio.newSound(Gdx.files.internal("youSuck.mp3")));

		// start the playback of the background music immediately
		//spaceMusic.setLooping(true);
		//spaceMusic.play();

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1020, 480);
		batch = new SpriteBatch();

		//create spaceship
		spaceship = new SpaceShip(800/2-spaceshipImage.getWidth()/2,500,0,0,
				spaceshipImage.getWidth() - SPACESHIP_BUFFER_WIDTH, spaceshipImage.getHeight() - SPACESHIP_BUFFER_HEIGHT);

		// create the obstacleList array
		obstacleList = new Array<Obstacle>();
		generateObstacles(6);

		//generate the time and health bars
		if (mesh == null) {
			mesh = new Mesh(true, 4, 4, 
					new VertexAttribute(Usage.Position, 4, "a_position"));          

			mesh.setVertices(new float[] { 0, 0, 0,
					0, 50, 0, 
					400, 50, 0,
					400, 0, 0,
			});   
			mesh.setIndices(new short[] { 0, 1, 2, 3});                         
		}


	}

	/**
	 * generates the list of obstacles.
	 *
	 * @param  count The number of obstacles being created
	 * @return None
	 */
	private void generateObstacles(int count){
		for(int i = 0; i< count;i++){
			int xPos = 100*i+ MathUtils.random(0, 10);
			int yPos = MathUtils.random(50, 500);
			int type = MathUtils.random(0, 1);
			Texture image;
			if (type ==0){

				image = obstacle0Image;
			}
			else{
				image = obstacle1Image;
			}
			Obstacle obstacle = new Obstacle(xPos, yPos, image.getWidth(), image.getHeight(), type, image);
			obstacleList.add(obstacle);
		}
	}

	public char getKeyPress(){
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			spaceship.x = touchPos.x - 64 / 2;
		}
		//keyboard arrow key mappings
		char keyPressed = '0';
		if(Gdx.input.isKeyPressed(Keys.LEFT)){
			keyPressed = 'l';
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT)){
			keyPressed = 'r';
		}
		if(Gdx.input.isKeyPressed(Keys.DOWN)){
			keyPressed = 'd';
		}
		if(Gdx.input.isKeyPressed(Keys.UP)){
			keyPressed = 'u';
		}
		//subtract fuel
		if (keyPressed != '0'){
			fuel -= 1;
		}

		System.out.println("xPos:" + spaceship.x);
		System.out.println("yPos:" + spaceship.y);
		System.out.println("Key Pressed: " + keyPressed);
		System.out.println("xVelo:" + spaceship.xVelo);
		System.out.println("yVelo" + spaceship.yVelo);
		return keyPressed;
	}

	public void checkBounds(){
		if(spaceship.x < 0){
			spaceship.x = 0;
			spaceship.xVelo = WALL_BOUNCE_VELO;
		}
		if(spaceship.x > SCREEN_WIDTH - spaceship.width){
			spaceship.x = SCREEN_WIDTH- spaceship.width;
			spaceship.xVelo = -WALL_BOUNCE_VELO;;
		}
		if(spaceship.y > SCREEN_HEIGHT - spaceship.height){
			spaceship.y = SCREEN_HEIGHT - spaceship.height;
			spaceship.yVelo = -WALL_BOUNCE_VELO;;
		}
		if(spaceship.y < 0){
			spaceship.y = 0;
			spaceship.yVelo = WALL_BOUNCE_VELO;;
		}
	}

	// move the obstacleList, remove any that are beneath the bottom edge of
	// the screen or that hit the spaceship. In the later case we play back
	// a sound effect as well.
	public void updateObstacles(){
		Iterator<Obstacle> iter = obstacleList.iterator();
		while(iter.hasNext()) {
			Obstacle obstacle = iter.next();
			//obstacle.y -= 200 * Gdx.graphics.getDeltaTime();
			if(obstacle.y + 64 < 0) iter.remove();
			if(obstacle.overlaps(spaceship)) {
				spaceship.collide(obstacle);
				System.out.println("collision");
				//spaceSounds.random().play();
				//iter.remove();
			}
		}
	}

	public void updateStatusBars(){
		//update the length of the time bar
		time -= 5*Gdx.graphics.getDeltaTime();
		System.out.println("Time: " + time);
	}

	public void clearScreen(){
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

	public void drawBackground(){
		batch.begin();
		batch.draw(backgroundImage,0,0);
		batch.end();
	}
	
	public void drawStatusBars(){
		//draws the time and fuel bars
		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeType.FilledRectangle);
		//time bar
		shapeRenderer.setColor(1, 1, 0, 1);
		shapeRenderer.filledRect(0, SCREEN_HEIGHT - BAR_THICKNESS, time, BAR_THICKNESS);
		//fuel bar
		shapeRenderer.setColor(0, 1, 0, 1);
		shapeRenderer.filledRect(0, 0, fuel, BAR_THICKNESS);
		shapeRenderer.end();
	}
	
	public void drawSpaceship(char keyPressed){
		// begin a new batch and draw the spaceship and
		// all drops
		batch.begin();

		batch.draw(spaceshipImage, spaceship.x-SPACESHIP_BUFFER_X, spaceship.y - SPACESHIP_BUFFER_Y);
		//draw correct flame effect
		if (keyPressed== 'l'){
			batch.draw(flameLeftImage, (spaceship.x-SPACESHIP_BUFFER_X)-LEFT_FLAME_OFFSET, (spaceship.y-SPACESHIP_BUFFER_Y) +LEFT_FLAME_OFFSET -5);
		}
		else if (keyPressed== 'r'){
			batch.draw(flameRightImage, (spaceship.x-SPACESHIP_BUFFER_X) + spaceship.width+ RIGHT_FLAME_OFFSET, (spaceship.y-SPACESHIP_BUFFER_Y) + 18);
		}
		if (keyPressed== 'u'){
			batch.draw(flameDownImage, (spaceship.x-SPACESHIP_BUFFER_X) + 22, (spaceship.y-SPACESHIP_BUFFER_Y)-DOWN_FLAME_OFFSET);
		}
		System.out.println("Rendering...");
		//update the moving obstacles
		for(Obstacle obstacle: obstacleList) {
			batch.draw(obstacle.getImage(), obstacle.x, obstacle.y);
		}
		batch.end();

	}

	public void drawCollisionBoxes(){
		//collision boxes
		shapeRenderer.begin(ShapeType.Rectangle);
		//spaceship
		shapeRenderer.setColor(1, 1, 0, 1);
		shapeRenderer.rect(spaceship.x, spaceship.y, spaceship.width, spaceship.height);
		//obstacles
		shapeRenderer.setColor(0, 1, 0, 1);
		for(Obstacle obj : obstacleList){
			shapeRenderer.rect(obj.x, obj.y, obj.width, obj.height);
		}
		shapeRenderer.end();

	}

	@Override
	public void render() {
		if (pause == false){
			System.out.println();

			//Intersector.overlaps(Circle c1, Circle c2); 
			//////////////////////////////////////////////////////
			//update

			// process user input
			char keyPressed = '0';
			keyPressed = getKeyPress();

			//update the spaceship
			spaceship.update(keyPressed);

			// make sure the spaceship stays within the screen bounds
			checkBounds();

			updateObstacles();

			//currently only updates time bar
			updateStatusBars();

			/////////////////////////////////////////////////
			//render
			// clear the screen with a dark blue color. The
			// arguments to glClearColor are the red, green
			// blue and alpha component in the range [0,1]
			// of the color to be used to clear the screen.
			clearScreen();
			// tell the camera to update its matrices.
			camera.update();

			drawBackground();

			drawStatusBars();

			// tell the SpriteBatch to render in the
			// coordinate system specified by the camera.
			batch.setProjectionMatrix(camera.combined);


			drawSpaceship(keyPressed);

			drawCollisionBoxes();
		}
		if(Gdx.input.isKeyPressed(Keys.P)){
			System.out.println("pausing");
			if(pause == true){
				pause = false;
			}
			else{
				pause = true;
			}
		}

	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		obstacle0Image.dispose();
		obstacle1Image.dispose();
		spaceshipImage.dispose();
		Iterator<Sound> iter = spaceSounds.iterator();
		while(iter.hasNext()) {
			Sound spaceSound = iter.next();
			spaceSound.dispose();
		}
		spaceMusic.dispose();
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}