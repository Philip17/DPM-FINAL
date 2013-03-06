import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;
	private final int FILTER_OUT = 5;
	private int filterControl, distance;	
	boolean filter;

	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	private Navigation navigation;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		this.navigation = odo.getNavigation();
		
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		
		if (locType == LocalizationType.FALLING_EDGE) {
		
			
			robot.setRotationSpeed(ROTATION_SPEED);
			
			// rotate the robot until it sees no wall			
			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() < 36) {}
			while(getFilteredData() > 32) {}
			Sound.beep();
			
			odo.getPosition(pos);
			angleA = pos[2];
			
			robot.setRotationSpeed(-ROTATION_SPEED);
			
			//try { Thread.sleep(1000); } catch (InterruptedException e) {}
			
			// switch direction and wait until it sees no wall			
			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() < 36) {}
			while(getFilteredData() > 32) {}
			Sound.playTone(1200,150);
			
			odo.getPosition(pos);
			angleB = pos[2];
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
									
			if(angleA < angleB) {
				//LCD.drawInt((int)((angleA + angleB)/2) + 135, 0, 6);	
				navigation.turnTo( ((angleA + angleB)/2) + 135);
				
				try { Thread.sleep(1000); } catch (InterruptedException e) {}
				
				odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {false, false, true});
				
				
			}
			else {
				//LCD.drawInt((int)((angleA + angleB)/2) - 45, 0, 6);
				navigation.turnTo(  ((angleA + angleB)/2) - 45);
				
				try { Thread.sleep(1000); } catch (InterruptedException e) {}
				
				odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {false, false, true});
								
			}			
						
					
			// update the odometer position (example to follow:)
			//odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
			
			
			
		} 
		
		else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			robot.setRotationSpeed(ROTATION_SPEED);
			
			// rotate the robot until it sees no wall			
			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() > 32) {}
			while(getFilteredData() < 36) {}
			Sound.playTone(1200,150);
			
			odo.getPosition(pos);
			angleA = pos[2];
			
			robot.setRotationSpeed(-ROTATION_SPEED);
			
					
			// switch direction and wait until it sees no wall			
			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() > 32) {}
			while(getFilteredData() < 36) {}
			Sound.beep();
			
			odo.getPosition(pos);
			angleB = pos[2];
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
									
			if(angleA < angleB) {
				//LCD.drawInt((int)((angleA + angleB)/2) - 45, 0, 6);	
				navigation.turnTo( ((angleA + angleB)/2) - 45);
				
				try { Thread.sleep(1000); } catch (InterruptedException e) {}
				
				odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {false, false, true});
								
			}
			else {
				//LCD.drawInt((int)((angleA + angleB)/2) + 135, 0, 6);
				navigation.turnTo(  ((angleA + angleB)/2) + 135);
				try { Thread.sleep(1000); } catch (InterruptedException e) {}
				
				odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {false, false, true});
								
			}			
						
			// update the odometer position (example to follow:)
			//odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		} 
	}
	
	
	private int getFilteredData() {
		
	filter = true;
	filterControl = 0;
		
		while(filter) {
		
			// do a ping
			us.ping();
			// wait for the ping to complete
			try { Thread.sleep(50); } catch (InterruptedException e) {}
		
			distance = us.getDistance();
		
			if(distance == 255 && (filterControl < FILTER_OUT) ) {
			filterControl++;
			}
		
			else {
				filter = false;
			}
		
		}	
	
	return distance;
	}
}	
	