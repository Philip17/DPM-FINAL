import lejos.nxt.*;

public class RunNxt {

	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odometer = new Odometer(patBot, true);
		int buttonChoice;

		UltrasonicSensor usR = new UltrasonicSensor(SensorPort.S1);
		UltrasonicSensor usL = new UltrasonicSensor(SensorPort.S2);
		LightSensor ls = new LightSensor(SensorPort.S2);
		LightLocalizer lsl = new LightLocalizer(odometer, ls);
		ObstacleNavigation oa = new ObstacleNavigation(odometer);
		
		do {
			// clear the display
			LCD.clear();

			// Ask the user whether the robot drives with or without obstacle
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Away  | Toward ", 0, 2);
			LCD.drawString(" From  |   Wall ", 0, 3);
			LCD.drawString(" Wall  |        ", 0, 4);
			buttonChoice = Button.waitForAnyPress();
		} 
		while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);
		
		LCDInfo lcd = new LCDInfo(odometer, lsl);
		while (Button.waitForAnyPress() != Button.ID_ESCAPE){
		if (buttonChoice == Button.ID_LEFT) {
					
			USLocalizer usl = new USLocalizer(odometer, usR,
					USLocalizer.LocalizationType.FALLING_EDGE);
			// ultrasonic localization
			usl.doLocalization();

			//sensor localization
			lsl.doLocalization();
		}
		
		else if (buttonChoice == Button.ID_RIGHT) {

			USLocalizer usl = new USLocalizer(odometer, usR,
			USLocalizer.LocalizationType.RISING_EDGE);
			
			// perform the ultrasonic localization
			usl.doLocalization();

			// perform the light sensor localization
			lsl.doLocalization();
			
		}
		}
			Button.waitForAnyPress();
	}
	
}

