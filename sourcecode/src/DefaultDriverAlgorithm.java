import java.io.File;
import java.util.Scanner;

import cicontest.algorithm.abstracts.AbstractAlgorithm;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import race.TorcsConfiguration;

public class DefaultDriverAlgorithm extends AbstractAlgorithm {

    private static final long serialVersionUID = 654963126362653L;

    DefaultDriverGenome[] drivers = new DefaultDriverGenome[1];
    int[] results = new int[1];

    public Class<? extends Driver> getDriverClass() {
        return DefaultDriver.class;
    }

    public void run(boolean continue_from_checkpoint) {
        if (!continue_from_checkpoint) {
            //init NN
            DefaultDriverGenome genome = new DefaultDriverGenome();
            drivers[0] = genome;

            //Start a race
            DefaultRace race = new DefaultRace();
            race.setTrack("aalborg", "road");
            race.laps = 1;

            //for speedup set withGUI to false
            results = race.runRace(drivers, true);

            // Save genome
            DriversUtils.storeGenome(drivers[0]);
        }
        // create a checkpoint this allows you to continue this run later
        DriversUtils.createCheckpoint(this);
        //DriversUtils.clearCheckpoint();
    }

    public static void main(String[] args) {

        //Set path to torcs.properties
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));

        DefaultDriverAlgorithm algorithm = new DefaultDriverAlgorithm();
        DriversUtils.registerMemory(algorithm.getDriverClass());
        if (args.length > 0 && args[0].equals("-resumeTraining")) {
            //if a training for the network exist it can be resumed
            NeuralNetwork net = new NeuralNetwork(22,3);
            net.train(true);
        } else if (args.length > 0 && args[0].equals("-createTraining")) {
            // define the size of the inputs, outputs and layers of the network and create a new network
            NeuralNetwork net = new NeuralNetwork(22, 45, 35, 25, 3);
            net.train(false);
        } else if (args.length > 0 && args[0].equals("-run")) {
            //start a race with a given network
            if (DriversUtils.hasCheckpoint()) {
                DriversUtils.loadCheckpoint().run(true);
            } else {
                algorithm.run();
            }
        } else if(args.length > 0 && args[0].equals("-checkTraining")){
            // check the training of a network
            Scanner inputScanner = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Enter network file name or press enter to use current.network");
            String networkFileName = inputScanner.nextLine();  // Read user input
            NeuralNetwork net = new NeuralNetwork(22, 3, networkFileName);
            net.checkTraining();
        }else {
            algorithm.run();
        }
    }

}