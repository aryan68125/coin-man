package com.aditya.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;

//import java.awt.Rectangle; is not the import that we want
//we want the rectangle from the gdx
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	//Texture is way to import an image assets in to our game
	Texture background;

	//for our character
	//whenever we want to use different images of a character in a game to give the illusion that our character is running
	//we will use a texture array
	Texture[] man;

	//int manState; will handle the animation of our character based on the Texture array position of our man
	int manState;
	//pausing our app fora short amount of time because the animation is way to fast and its gonna drain hell out of the device's battery energy way too fast
	int pause = 0;

	//**now importing adreno openCL physX engine**
	//the float gravity will decide at what speed our character is gonna fall
	float gravity = 0.2f;
	float velocity = 0;
	//int manY = man's y position
	int manY = 0;
	//int manX = man's X position
	int manX = 0;
	//setting up a geometric shape for our man character
	Rectangle manRectangle;

	//it will hold a bunch of coin objects that are gonna be appearing on the screen
	//creating an arrayList that will hold the coordinates of our coin
	ArrayList<Integer> coinX = new ArrayList<>();
	ArrayList<Integer> coinY = new ArrayList<>();
	//ArrayList of type Rectangle allows us add a geometry to our objects on the screen
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	Texture coin;
	//coinCount  =  proper spacing between the coin
	//it will allow us to show a coin after every 100 loop of screen render
	int coinCount;
	Random random; //we will use this random object to randomizing the positions at which our coins will appear on the screen

	//it will hold a bunch of bomb objects that are gonna be appearing on the screen
	//creating an arrayList that will hold the coordinates of our bomb
	ArrayList<Integer> bombX = new ArrayList<>();
	ArrayList<Integer> bombY = new ArrayList<>();
	//ArrayList of type Rectangle allows us add a shape to our objects on the screen
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Texture bomb;
	//bombCount  =  proper spacing between the bomb
	//it will allow us to show a bomb after every 100 loop of screen render
	int bombCount;
	Random random2;

	//int score will keep track of our score
	int score =0;
	//now to display our score onto our screen
	BitmapFont Font;

	//setting up a bitmap for play button
	BitmapFont Play;
	ArrayList <Integer> playY = new ArrayList<>();
	ArrayList <Integer> playX = new ArrayList<>();

	//gameState is responsible for ending the game when we collide with the bomb
	//its also responsible for keeping track of our state of the game
	int gameState = 0;

	//it will hold the high score in the game made by the user
	int HighScore = 0;
	//setting up the BitmapFont for highScore
	BitmapFont highScore;
	ArrayList <Integer> highScoreY = new ArrayList<>();
	ArrayList <Integer> highScoreX = new ArrayList<>();
	private static Preferences prefs;//its responsible to store the high score in the game
	String HS;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");

		//setting up our character in our game
		man = new Texture[4];
		//filling up the Texture array will hold our character's images
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		//for our character
		//setting up our manY
		manY = Gdx.graphics.getHeight()/2;

		//setting up our coin
		coin = new Texture("coin.png");
        //setting up our random object for our coins position
		random = new Random();

		//setting up our bombs
		bomb= new Texture("bomb.png");
		random2 = new Random();

		//setting our BitmapFont so that we can show the score onto our screen
		Font = new BitmapFont();
		//setting up the color on our BitmapFont
		Font.setColor(Color.WHITE);
		//setting up the size for our Bitmap
		//xy = 10
		Font.getData().setScale(10);

		//setting our BitmapFont so that we can show the play button onto our screen
		Play = new BitmapFont();
		//setting up the color on our BitmapFont
		Play.setColor(Color.GREEN);
		//setting up the size for our Bitmap
		//xy = 10
		Play.getData().setScale(7);

		//setting up high score bitmap Font on our screen
		highScore = new BitmapFont();
		highScore.setColor(Color.WHITE);
		highScore.getData().setScale(4);
	}

	//method that deals with showing multiple coins on our screen randomely
	public void makeCoin(){
		//float height = height at which the coin will appear on the screen (randomly)
		//random.nextFloat() = it will give us randome numbers between 0 and 1
		//Gdx.graphics.getHeight() = will give us the height of the screen
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		//now updating the coordinates of the coins in the ArrayList that holds the coin Coordinates on the screen
		coinY.add((int)height);
		//make sure that your coin appears off of the screen and the X coordinate of the coins will all be the same
		coinX.add(Gdx.graphics.getWidth());
	}

	//method that deals with showing multiple bomb on our screen randomely
	public void makebomb(){
		//float height = height at which the bomb will appear on the screen (randomly)
		//random.nextFloat() = it will give us randome numbers between 0 and 1
		//Gdx.graphics.getHeight() = will give us the height of the screen
		float height = random2.nextFloat() * Gdx.graphics.getHeight();
		//now updating the coordinates of the bomb in the ArrayList that holds the coin Coordinates on the screen
		bombY.add((int)height);
		//make sure that your bomb appears off of the screen and the X coordinate of the coins will all be the same
		bombX.add(Gdx.graphics.getWidth());
	}

	//this function handles how the assets show up on the screen
	//this function gets called constantly in a loop
	@Override
	public void render () {
		//calculation of man's X position on the screen
		manX = Gdx.graphics.getWidth()/2 - man[manState].getHeight()/2;

		//inorder to start things we need to say batch.begin();
		batch.begin();

		//to draw something on the game screen
		/*
		batch.draw(Image that you want to draw, starting position of the image in x and y coordinates,height and width of the image);
        Gdx.graphics.getWidth() = tells the image to match the width of the devices screen
        Gdx.graphics.getHeight() = tells the image to match the height of the devices screen
		 */
		//setting up our game background
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		//checking the state in which our game is
		//if our gameState is equal to 1 then the game will behave normally
		//if our gameState =0 then we are at the starting position
		//if our gameState = 2 then game over
		if(gameState == 1){
			//game is live and will run normally
			//setting up our bombs
			if(bombCount<250){
				bombCount++;
			}
			else{
				bombCount = 0;
				//making a coin after every 100 frames rendered
				makebomb();
			}

			bombRectangles.clear();
			//using a for loop to draw our bombs on the screen
			for(int i=0;i<bombX.size();i++)
			{
				batch.draw(bomb,bombX.get(i),bombY.get(i));
				//inorder to move the coin in the x directrion from right to left we need to update the X coordinate of the bomb
				//updating the x coordinate of our bomb
				//bombX.set(i, bombX.get(i)-4);
				//bombX.set(); ->updates the x coordinates of a bomb
				//current position = i
				//bombX.get(i)-4 = moves bombs from right to left the speed can be controlled by changing the number four
				//higher the number faster the bomb moves on the screen
				bombX.set(i, bombX.get(i)-10);

				//coinRectangles.add(new Rectangle(coinX.get(i), coinY.get(i), coin.getWidth(),coin.getHeight())); = now adding the coin physX onto our coins
				//new Rectangle(coordinates of the screen, size of the coin on the screen) = it will set the Rectangle on to our coins
				//coinX.get(i), coinY.get(i) = telling the rectangle to appear where the coin appear on the screen
				//coin.getWidth(),coin.getHeight() = the size of the rectangle should be equal to the size of the coin
				bombRectangles.add(new Rectangle(bombX.get(i), bombY.get(i), bomb.getWidth(),bomb.getHeight()));
			}

			//setting up our coins before our character so that our character appears above it
			if(coinCount<100){
				coinCount++;
			}
			else{
				coinCount = 0;
				//making a coin after every 100 frames rendered
				makeCoin();
			}

			coinRectangles.clear();
			//using a for loop to draw our coins on the screen
			for(int i=0;i<coinX.size();i++)
			{
				batch.draw(coin,coinX.get(i),coinY.get(i));
				//inorder to move the coin in the x directrion from right to left we need to update the X coordinate of the coin
				//updating the x coordinate of our coin
				//coinX.set(i, coinX.get(i)-4);
				//coinX.set(); ->updates the x coordinates of a coin
				//current position = i
				//coinX.get(i)-4 = moves coins from right to left
				coinX.set(i, coinX.get(i)-4);

				//coinRectangles.add(new Rectangle(coinX.get(i), coinY.get(i), coin.getWidth(),coin.getHeight())); = now adding the coin physX onto our coins
				//new Rectangle(coordinates of the screen, size of the coin on the screen) = it will set the Rectangle on to our coins
				//coinX.get(i), coinY.get(i) = telling the rectangle to appear at the position of the coin where the coin appear on the screen
				//coin.getWidth(),coin.getHeight() = the size of the rectangle should be equal to the size of the coin
				coinRectangles.add(new Rectangle(coinX.get(i), coinY.get(i), coin.getWidth(),coin.getHeight()));

			}

			//setting up the code so that our character can jump when the user touches him
			//Gdx.input.justTouched() = it will handle all the touch input from the user in our game
			if(Gdx.input.justTouched()){
				//velocity = -10; -> will make our character jump up
				velocity = -10;
			}

			//here we want to keep track of the character on what position our character man is in the game screen
			//setting up the pause function in our app so that we can slow our games animation to a manageble speed
			if(pause<5){
				pause++;
			}
			else {
				//here we will update our character's running animation
				pause = 0;
				//we have to have a way to draw our character so that we can show our character is running
				if (manState < 3) {
					//updating our manState
					manState++;
				} else {
					//as soon as it becomes 3 we will set manState to be zero again to keep the running animation of our character in a loop
					manState = 0;
				}
			}

			//calculating our character's velocity
			//velocity of our character in falling state is = current velocity + gravity;
			velocity = velocity + gravity;
			manY -= velocity;
			//we will set our character to remain on the ground and stop his decent once he has reached the ground
			if(manY <= 30){
				manY = 30;
			}

			//**so in order to do that we are going to apply graphical mathematical model of adreno gpu using open cl graphics library**
			//we are going to write the code related to our character after we've drawn the main background because we want to draw our character on top of the main background and not under it
        /*
        if we want our man to show in the centre of the screen then we have to do the math like the below:-
        1. figure out the man's x and y positions
        2. take the height/2 and width/2
        4. it will look something like this:-position on the screen of our character-> x: Gdx.graphics.getWidth()/2, y: Gdx.graphics.getHeight()/2
        5. subtracting the half of  width of the man from the x: Gdx.graphics.getWidth()/2 (man's position) - man[0].getHeight()/2 (width of the man's image itself)
         */
			batch.draw(man[manState],manX, manY);
			//setting up a geometric shape for our character
			manRectangle = new Rectangle(manX, manY,man[manState].getWidth(),man[manState].getHeight());

			//now setting up the collision mechanism for coins after everything is drawn
			//using an if statement to check whether our character is colliding with the coin or not
			//for lop to get to all of our coins
			for(int i=0;i<coinRectangles.size();i++)
			{
				//Intersector.overlaps() it checks whether the two images that have the designated polygons assigned to them have collided or not
				if(Intersector.overlaps(manRectangle, coinRectangles.get(i))){
					//so lets go ahead and log some information here
					Gdx.app.log("collisionCoin","Collision with coin");
					//increment the score by 1 if we hit a coin
					score++;
					if(score>HighScore) {
						HighScore = score; //updating high score if the score is greater than high core
					}
					Gdx.app.log("HighScore", Integer.toString(HighScore));
					//getting rid of the coin after the collision with the coin of our character
					coinRectangles.remove(i);
					coinX.remove(i);
					coinY.remove(i);
					//updating our gameState
					gameState = 1;
					break;
				}
			}

			//after we've updated our score now we have to draw the score on our screen
			//drawing a Bitmap on the screen is a little bit different than drawing your game assets on the screen
			Font.draw(batch,String.valueOf(score),100,2100);

			//now setting up the collision mechanism for bombs after everything is drawn
			//using an if statement to check whether our character is colliding with the bomb or not
			//for lop to get to all of our bomb
			for(int i=0;i<bombRectangles.size();i++)
			{
				//Intersector.overlaps() it checks whether the two images that have the designated polygons assigned to them have collided or not
				if(Intersector.overlaps(manRectangle, bombRectangles.get(i))){
					//so lets go ahead and log some information here
					Gdx.app.log("Bomb","Collision with bomb");

					//getting rid of the bomb after the collision with the bomb of our character
					bombRectangles.remove(i);
					bombX.remove(i);
					bombY.remove(i);

					//updating our gameState
					gameState = 2;
					break;
				}
			}
		}
		else if(gameState == 0)
		{
			//waiting to start
			score = 0;
			//getting rid of the coin after the collision with the coin of our character
			for(int i=0;i<coinRectangles.size();i++) {
				coinRectangles.remove(i);
				coinX.remove(i);
				coinY.remove(i);
			}
			//getting rid of the bombs
			for(int i=0;i<bombRectangles.size();i++)
			{
				//getting rid of the bomb after the collision with the bomb of our character
				bombRectangles.remove(i);
				bombX.remove(i);
				bombY.remove(i);
			}
			//drawing a Bitmap on the screen is a little bit different than drawing your game assets on the screen
			String PLAY = "START THE GAME";
			playY.clear();
			playX.clear();
			//now updating the coordinates of the START THE GAME in the ArrayList that holds the coin Coordinates on the screen
			playY.add(Gdx.graphics.getHeight()/2);
			playX.add(100);
			Play.draw(batch,String.valueOf(PLAY),playX.get(0),playY.get(0));
			savedHighscore();
			if(Gdx.input.justTouched()){
				gameState = 1;
				//removing the BitmapFont from the screen when user touches the screen
				playX.remove(0);
				playY.remove(0);

				//removing highscore from the screen
				highScoreX.remove(0);
				highScoreY.remove(0);
				score = 0;
			}
		}
		else if(gameState == 2){
			//game Over
			//drawing a Bitmap on the screen is a little bit different than drawing your game assets on the screen
			playX.clear();
			playY.clear();
			String PLAY = "Game Over";
			Play.setColor(Color.RED);
			//now updating the coordinates of the coins in the ArrayList that holds the coin Coordinates on the screen
			playY.add(Gdx.graphics.getHeight()/2);
			playX.add(250);
			Play.draw(batch,String.valueOf(PLAY),playX.get(0),playY.get(0));

			//now showing highScore in the game
			savedHighscore();
			if(Gdx.input.justTouched()){
				gameState = 1;
				//removing the BitmapFont from the screen when user touches the screen
				playX.remove(0);
				playY.remove(0);

				//removing highscore from the screen
				highScoreX.remove(0);
				highScoreY.remove(0);
				score = 0;
			}
		}
		//after everything that we wanna put on the screen say batch.end();
		batch.end();
	}

	//saving the high score inside the Preferences permanent memory block
	public void savedHighscore(){

		prefs = Gdx.app.getPreferences("highScoreGame");// We store the value 10 with the key of "highScore"
		prefs.putInteger("HSC", HighScore);
		prefs.flush(); // This saves the preferences file.

		highScoreX.clear();
		highScoreY.clear();
		highScoreX.add(290);
		highScoreY.add(100);
		HS = "HIGH SCORE = "+ prefs.getInteger("HSC");
		highScore.draw(batch,String.valueOf(HS),highScoreX.get(0),highScoreY.get(0));
		Gdx.app.log("saved",Integer.toString(prefs.getInteger("HSC")));
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
