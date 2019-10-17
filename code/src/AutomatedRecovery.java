import cicontest.torcs.controller.extras.IExtra;
import cicontest.algorithm.abstracts.DriversUtils;

import scr.Action;
import scr.SensorModel;

public class AutomatedRecovery implements IExtra {
    private boolean stuck = false;
    private boolean start = true;
    private boolean forward = false;
    private boolean backonTrack = false;

    @Override
    public void process(Action action, SensorModel sensors) {
        if (start) {
            if (sensors.getSpeed() > 10) {
                start = false;
            }
            return;
        }


        if (sensors.getSpeed() < 3 && stuck == false)
            stuck = true;

        if (stuck) {
            if (!backonTrack) {
                action.steering = 0;
                action.gear = -1;
                action.accelerate = 1.0D;
                action.brake = 0.0D;
                if (sensors.getSpeed() < -10 ) {
                    action.accelerate = 0.0D;
                    action.brake = 0.0D;
                }
                System.out.println(sensors.getTrackPosition());
                if (sensors.getTrackPosition() > -0.05 && sensors.getTrackPosition() < 0.05)
                    backonTrack = true;
            } else {
                if (!forward) {
                    action.steering = Math.round(-DriversUtils.alignToTrackAxis(sensors, 0.5));
                    action.gear = -1;
                    action.accelerate = 1.0D;
                    action.brake = 0.0D;
                    if (sensors.getSpeed() < -20) {
                        action.accelerate = 0.0D;
                        action.brake = 1.0D;
                        action.gear = 1;
                        forward = true;
                    }
                } else {
                    action.steering = DriversUtils.alignToTrackAxis(sensors, 0.5);
                    action.gear = 1;
                    action.accelerate = 0.5D;
                    action.brake = 0.0D;
                    if (sensors.getSpeed() > 50)
                        reset();
                }


            }
            System.out.println("Steering: " + action.steering);
            System.out.println("Acceleration: " + action.accelerate);
            System.out.println("Brake: " + action.brake);
            System.out.println("-----------------------------------------------");
            return;
        }
    }


    @Override
    public void reset() {
        stuck = false;
        start = true;
        forward = false;
        forward = false;
    }
}

