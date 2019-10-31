import cicontest.torcs.controller.extras.IExtra;
import scr.Action;
import scr.SensorModel;

public class AdjustBreaks implements IExtra {

    int stuck = 0;
    @Override
    public void process(Action action, SensorModel sensors) {
        //Probleem dat Neurale Netwerk moeilijk start vanaf het begin van de race
        if (sensors.getSpeed() <= 50){
            stuck++;
            //Controle of de auto stil staat
            if (stuck <= 100){
                //Auto heeft moeite met wegrijden, override de brake van het neurale netwerk
                action.brake = 0;
                return;
            }
        }
        if (stuck > 100){
            action.brake = 0;
            //Auto rijdt weer, brake van neurale netwerk gebruiken
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


