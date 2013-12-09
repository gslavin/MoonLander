package com.me.moonlander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public class SpaceShip extends Rectangle{

	private static final long serialVersionUID = 1L;
	int xVelo;
	int yVelo;
	static int GRAVITY = -200;
	static int THRUST = 350;
	
	/**
	 * The spaceship controlled by the player
	 *
	 * @param  xPos x position
	 * @param  yPos y position
	 * @param  xVelo x velocity
	 * @param  yVelo y velocity
	 * @param  width width of spaceship hitbox
	 * @param  height height of spaceship hitbox
	 */
	public SpaceShip(int xPos, int yPos,int xVelo, int yVelo, int width, int height){
		  this.x = xPos;
	      this.y = yPos; // bottom left corner of the spaceShip is 20 pixels above the bottom screen edge
	      this.xVelo = xVelo;
	      this.yVelo = yVelo;
	      this.width = width;
	      this.height = height;
	}
	
	/**
	 * updates the spaceship based on the current game state
	 *
	 * The position of the spaceship is updated.
	 * If a thrust key was pressed, the spaceship velocity is updated
	 *
	 * @param  keyPressed The last key the player pressed
	 */
	public void update(char keyPressed){
		//update the velocity of the spaceShip
		if (keyPressed == 'l'){
			xVelo = (int) (THRUST*Gdx.graphics.getDeltaTime() + xVelo);
		}
		else if (keyPressed == 'r'){
			xVelo = (int) (-THRUST*Gdx.graphics.getDeltaTime() + xVelo);
		}
		else if (keyPressed == 'u'){
			yVelo = (int) ((GRAVITY+THRUST)*Gdx.graphics.getDeltaTime() + yVelo);
		}
		else{
			yVelo = (int) (GRAVITY*Gdx.graphics.getDeltaTime() + yVelo);
		}
		
		//update the position of the spaceShip
		x = xVelo*Gdx.graphics.getDeltaTime()+x;
		y = yVelo*Gdx.graphics.getDeltaTime()+y;
		
	}
	
	
	/**
	 * When a collision is detected, updates the
	 * position and velocity of the spaceship
	 *
	 * @param  obstacle The obstacle that the spaceship collided with
	 */
	//fix collisions because they currently suck
	public void collide(Obstacle obstacle) {
		if (Math.abs(obstacle.x-x) < width){
			yVelo = -yVelo;
			if(obstacle.y < y){
				y += 5;
			}
			else{
				y -= 5;
			}
				
		}
		else{
			xVelo = -xVelo;
			if(obstacle.x < x){
				x += 5;
			}
			else{
				x -= 5;
			}
		}
	}


}
