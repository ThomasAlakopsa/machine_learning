import cicontest.torcs.controller.extras.IExtra;
import scr.Action;
import scr.SensorModel;

public class AdjustBreaks implements IExtra {

    int stuck = 0;
    @Override
    public void process(Action action, SensorModel sensors) {

        if (sensors.getSpeed() <= 50){
            stuck++;
            if (stuck <= 100){
                action.brake = 0;
                return;
            }
        }
        if (stuck > 100){
            action.brake = 0;
            if(sensors.getSpeed() > 60){
                stuck = 0;
            }

        }

        return;
    }
    @Override
    public void reset() {

    }
}

