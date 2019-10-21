import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;
import cicontest.torcs.genome.IGenome;
import scr.Action;
import scr.SensorModel;

import java.net.Inet4Address;
import java.util.stream.DoubleStream;

public class DefaultDriver extends AbstractDriver {

    private NeuralNetwork neuralNetwork;


    public DefaultDriver() {
        initialize();
        neuralNetwork = new NeuralNetwork(22, 3);
        neuralNetwork.loadGenome();
    }

    private void initialize() {
        this.enableExtras(new AutomatedClutch());
        this.enableExtras(new AutomatedGearbox());
        this.enableExtras(new AutomatedRecovery());
        this.enableExtras(new ABS());
    }

    @Override
    public void loadGenome(IGenome genome) {
        if (genome instanceof DefaultDriverGenome) {
            DefaultDriverGenome myGenome = (DefaultDriverGenome) genome;
        } else {
            System.err.println("Invalid Genome assigned");
        }
    }

    @Override
    public double getAcceleration(SensorModel sensors) {
        return neuralNetwork.compute(sensors)[1];
    }

    @Override
    public double getSteering(SensorModel sensors) {

        return neuralNetwork.compute(sensors)[0];
    }

    @Override
    public String getDriverName() {
        return "UberDriver";
    }

    @Override
    public Action controlWarmUp(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action controlQualification(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action controlRace(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action defaultControl(Action action, SensorModel sensors) {
        double[] output = neuralNetwork.compute(sensors);
        action.accelerate = output[0];

        if (sensors.getSpeed() > 30)
            action.brake = output[1];
        else
            action.brake = 0;
        action.steering = output[2];
        return action;
    }


}