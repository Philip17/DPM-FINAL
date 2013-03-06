import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.Sound;

public class LightLocalizer {
	
	private final double LS_TO_CENTER = 9.0; // distance from light sensor to center of robot
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightSensor ls;
	private Navigation navigation;
	private int currentLSValue, count;
	private double initialTheta, currentTheta;
	private double angleX, angleY, lengthX, lengthY, deltaTheta;
	private boolean rotate;
	
	public LightLocalizer(Odometer odo, LightSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		this.navigation = odo.getNavigation();
		
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
		double[] pos = new double[3];
		double[] gridlines = new double[4];		
		
		navigation.travelTo(8,8);
		navigation.turnTo(0);
		
		Motor.A.stop();
		Motor.B.stop();
		try { Thread.sleep(1000); } catch (InterruptedException e) {}
		
		odo.getPosition(pos);
		initialTheta = currentTheta = pos[2];
		count = 0;
		
		robot.setRotationSpeed(-25);
		rotate = true;
		
		while(rotate) {
			
			currentLSValue = ls.readValue();
			
			
			if (currentLSValue < 43 && count < 4) {
				
				odo.getPosition(pos);
				Sound.playTone(800, 150);
				gridlines[count] = pos[2];				
				//LCD.drawString("" + gridlines[count], 0, count+1);
				count++;
				try { Thread.sleep(600); } catch (InterruptedException e) {}				
			}
			
			else if(count > 4) {
				Sound.buzz();
				count = 0;
				rotate = false;
			}
			
			odo.getPosition(pos);
			currentTheta = pos[2];
						
			if(count==0) {}
			else {
				 rotate = (currentTheta - initialTheta >= 9);
			}
			
		} // end while loop
		
		Motor.A.stop();
		Motor.B.stop();
		//odo.getPosition(pos);
				
		// calculate x, y, and theta
		angleX = gridlines[1] - gridlines[3];
		angleY = gridlines[0] - gridlines[2];
		lengthX = Math.abs(LS_TO_CENTER * Math.cos(angleY/2));
		lengthY = Math.abs(LS_TO_CENTER * Math.cos(angleX/2));		
		deltaTheta = (angleY/2) - gridlines[0] + 270;
		
		odo.setPosition(new double[] {-lengthX, -lengthY, deltaTheta}, new boolean[] {true, true, true});
				
//		LCD.drawString("" + -lengthX, 0, 1);
//		LCD.drawString("" + -lengthY, 0, 2);
//		LCD.drawString("" + pos[2]+deltaTheta, 0, 3);
			
		// go to the origin and face 0 degrees		
		navigation.travelTo(0.0, 0.0);		
		navigation.turnTo(0.0);
		
		odo.getPosition(pos);
//		LCD.drawString("" + pos[0], 0, 4);
//		LCD.drawString("" + pos[1], 0, 5);
//		LCD.drawString("" + pos[2], 0, 6);
		
		
	}

}
