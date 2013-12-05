package com.me.moonlander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Obstacle extends Rectangle{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int type;
	Texture image;
	
	public Obstacle(int xPos, int yPos, int width, int height, int type, Texture image){
		  this.x = xPos;
	      this.y = yPos; // bottom left corner of the spaceShip is 20 pixels above the bottom screen edge
	      this.width = width;
	      this.height = height;
	      this.type = type;
	      this.image = image;
	}
	
	public void update(char keyPressed){
		//update he position of the obstacle
		
		//update the position of the spaceShip
		x = 20*Gdx.graphics.getDeltaTime()+x;
		y = 20*Gdx.graphics.getDeltaTime()+y;
		
	}
	
	public Texture getImage(){
		return image;
	}

}
