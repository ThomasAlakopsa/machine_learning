import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.extras.IExtra;
import cicontest.algorithm.abstracts.DriversUtils;

import scr.Action;
import scr.SensorModel;

public class AutomatedRecovery implements IExtra {
    private int stuck = 0;
    private double UNSTUCK_TIME_LIMIT = 2.0;
    private double MAX_UNSTUCK_ANGLE = 30 / (180 * Math.PI);
    private double MAX_UNSTUCK_SPEED = 5.0;
    private double MIN_UNSTUCK_DIST = 0.9;
    private double MAX_UNSTUCK_DIST = 0.2;

    private double RCM_MAX_DT_ROBOTS = 1;
    private int MAX_UNSTUCK_COUNT = (int) (UNSTUCK_TIME_LIMIT / RCM_MAX_DT_ROBOTS);

    @Override
    public void process(Action action, SensorModel sensors) {
        if (isStuck(sensors)) {
            System.out.println("Ik ben stuck");
            action.steering = -sensors.getAngleToTrackAxis() /(0.366519 * 1.5);
//            action.steering = -sensors.getAngleToTrackAxis()/; //STEERLOCK?
            action.gear = -1; // reverse gear
            action.accelerate = 0.5D; // 50% accelerator pedal
            action.brake = 0.0D; // no brakes
        }
        return;
    }

    public boolean isStuck(SensorModel sensors) {
        if (Math.abs(sensors.getAngleToTrackAxis()) > MAX_UNSTUCK_ANGLE &&
                sensors.getSpeed() < MAX_UNSTUCK_SPEED &&
                Math.abs(sensors.getTrackPosition()) > MAX_UNSTUCK_DIST) {
            if (stuck > MAX_UNSTUCK_COUNT && sensors.getTrackPosition() * sensors.getAngleToTrackAxis() < 0.0) {
                return true;
            } else {
                stuck++;
                return false;
            }
        } else {
            stuck = 0;
            return false;
        }
    }


    @Override
    public void reset() {
        stuck = 0;

    }
}

