package raycaster;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 * An LWJGL / Java port of a very nice ray-caster tutorial<br>
 * ================================<br>
 * http://lodev.org/cgtutor/raycasting.html<br>
 * ================================<br>
 * @author Lode Vandevenne
 *
 */
public class RayCasterMain {
	private static int width = 1024;
	private static int height = 576;
	private static boolean SHADING = false;
	
	final static int[][] MAP = {
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,2,2,2,2,2,0,0,0,0,3,0,3,0,3,0,0,0,1},
			{1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,3,0,0,0,3,0,0,0,1},
			{1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,2,2,0,2,2,0,0,0,0,3,0,3,0,3,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,4,4,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,4,0,4,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,4,0,0,0,0,5,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,4,0,4,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,4,0,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,4,4,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
	};
	
	// Player Postions
	private static double posX = 22, posY = 12;
	// Initial Direction Vector
	private static double dirX = -1, dirY = 0;
	// 2D RayCaster camera plane
	private static double planeX = 0, planeY = 0.66;
	//  FPS counter
	private static double time = 0;
	private static double oldTime = 0;
	
	public static void main(String[] args) {
		initLWJGL();
		time = System.currentTimeMillis();
		while (!Display.isCloseRequested()) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			
			render();
			double frameTime = calcFps();
			checkKeys(frameTime);
			
			Display.update();
			Display.sync(60);
		}
		Display.destroy();
		System.exit(0);
	}

	private static void checkKeys(double frameTime) {
	    double moveSpeed = frameTime * 50.0;
	    double rotSpeed = frameTime * 30.0;
	    
		while (Keyboard.next()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				if(MAP[(int)(posX + dirX * frameTime)][(int)posY] == 0) posX += dirX * moveSpeed;
			    if(MAP[(int)posX][(int)(posY + dirY * moveSpeed)] == 0) posY += dirY * moveSpeed;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				if(MAP[(int) (posX - dirX * frameTime)][(int) posY] == 0) posX -= dirX * moveSpeed;
			    if(MAP[(int) posX][(int) (posY - dirY * moveSpeed)] == 0) posY -= dirY * moveSpeed;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				double oldDirX = dirX;
			    dirX = dirX * Math.cos(rotSpeed) - dirY * Math.sin(rotSpeed);
			    dirY = oldDirX * Math.sin(rotSpeed) + dirY * Math.cos(rotSpeed);
			    double oldPlaneX = planeX;
			    planeX = planeX * Math.cos(rotSpeed) - planeY * Math.sin(rotSpeed);
			    planeY = oldPlaneX * Math.sin(rotSpeed) + planeY * Math.cos(rotSpeed);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				double oldDirX = dirX;
			    dirX = dirX * Math.cos(-rotSpeed) - dirY * Math.sin(-rotSpeed);
			    dirY = oldDirX * Math.sin(-rotSpeed) + dirY * Math.cos(-rotSpeed);
			    double oldPlaneX = planeX;
			    planeX = planeX * Math.cos(-rotSpeed) - planeY * Math.sin(-rotSpeed);
			    planeY = oldPlaneX * Math.sin(-rotSpeed) + planeY * Math.cos(-rotSpeed);
			}
		}
	}

	private static void initLWJGL() {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle("RayCasting Demo");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			Display.destroy();
			System.exit(-1);
		}
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	/**
	 * Ray Caster DDA Algorithm
	 */
	private static void render() {
		for (int x = 0; x < width; x++) {
			double cameraX = 2 * x / (double) width - 1;
		    double rayPosX = posX;
		    double rayPosY = posY;
		    double rayDirX = dirX + planeX * cameraX;
		    double rayDirY = dirY + planeY * cameraX;
		    // Current Map Box
		    int mapX = (int) rayPosX;
		    int mapY = (int) rayPosY;
		    // Length between ray position and next X / Y side
		    double sideDistX = 0;
		    double sideDistY = 0;
		    // Actual length between X sides or Y sides
		    double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
		    double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
		    double perpWallDist = 0;
		    // What direction to step in
		    int stepX = 0;
		    int stepY = 0;
		    // Has it it?  What side was hit?
		    boolean hit = false;
		    int side = 0;
		    
		    if (rayDirX < 0) {
		    	stepX = -1;
		    	sideDistX = (rayPosX - mapX) * deltaDistX;
		    } else {
		    	stepX = 1;
		    	sideDistX = (rayPosX + 1.0 - mapX) * deltaDistX;
		    }
		    if (rayDirY < 0) {
		    	stepY = -1;
		    	sideDistY = (rayPosY - mapY) * deltaDistY;
		    } else {
		    	stepY = 1;
		    	sideDistY = (rayPosY + 1.0 - mapY) * deltaDistY;
		    }
		    
		    while (!hit) {
		    	/**
		    	 * Actual DDA Algorithm
		    	 */
		    	if (sideDistX < sideDistY) {
		    		sideDistX += deltaDistX;
		    		mapX += stepX;
		    		side = 0;
		    	} else {
		    		sideDistY += deltaDistY;
		    		mapY += stepY;
		    		side = 1;
		    	}
		    	if (MAP[mapX][mapY] > 0) hit = true;
		    }
		    if (side == 0) {
		    	perpWallDist = Math.abs((mapX - rayPosX + (1 - stepX) / 2) / rayDirX);
		    } else {
		    	perpWallDist = Math.abs((mapY - rayPosY + (1 - stepY) / 2) / rayDirY);
		    }
		    int lineHeight = Math.abs((int) (height / perpWallDist));
		    int lineBegin = (-lineHeight / 2) + (height / 2);
		    if (lineBegin < 0) lineBegin = 0;
		    int lineEnd = (lineHeight / 2) + (height / 2);
		    if (lineEnd > height) lineEnd = height - 1;
		    
		    float[] color = new float[3];
		    switch(MAP[mapX][mapY]) {
		    	case 1:  color = new float[] {1f, 0f, 0f};  break; //red
		        case 2:  color = new float[] {0f, 1f, 0f};  break; //green
		        case 3:  color = new float[] {0f, 0f, 1f};   break; //blue
		        case 4:  color = new float[] {1f, 1f, 1f};  break; //white
		        default: color = new float[] {1f, 1f, 0f}; break; //yellow
		    }
		    if (side == 1 && SHADING) color = new float[] {color[0] / 2, color[1] / 2, color[2] / 2};
		    
		    renderLine(x, lineBegin, lineEnd, color);
		}
	}
	
	private static void renderLine(int x, int lineBegin, int lineEnd, float[] color) {
		GL11.glColor3f(color[0], color[1], color[2]);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		{
			GL11.glVertex2i(x, lineBegin);
			GL11.glVertex2i(x, lineEnd);
		}
		GL11.glEnd();
	}
	
	private static double calcFps() {
		oldTime = time;
		time = System.currentTimeMillis();
		double frameTime = (time - oldTime) / 1000;
		Display.setTitle("RayCasting Demo :: FPS: " + (int) (1.0 / frameTime));
		return frameTime;
	}
}
