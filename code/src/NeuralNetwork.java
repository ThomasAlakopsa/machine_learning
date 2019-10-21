import org.encog.Encog;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.specific.CSVNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import scr.SensorModel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NeuralNetwork implements Serializable {
    private String trainFolder = "train/";
    private String networkFolder = "network/";
    private String trainDataLocation = "train_data/temp.csv";
    private String networkFile = "current";
    private int input;
    private int output;

    private int layer1;
    private int layer2;
    private int layer3;

    BasicNetwork network;


    public NeuralNetwork(int inputs, int layer1, int layer2, int layer3, int outputs) {
        this.input = inputs;
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
        this.output = outputs;
    }

    public NeuralNetwork(int inputs, int outputs) {
        this.input = inputs;
        this.output = outputs;
    }
    public NeuralNetwork(int inputs, int outputs, String networkFile) {
        this.input = inputs;
        this.output = outputs;
        this.networkFile = networkFile;
    }

    private void createNeuralNetwork() {
        //	Give network structure
        network = new BasicNetwork();

        //  Create input layer
        network.addLayer(new BasicLayer(null, true, input));
        //  Create hidden layer(s)
        network.addLayer(new BasicLayer(new ActivationTANH(), true, layer1));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, layer2));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, layer3));

        //  Create output layer
        network.addLayer(new BasicLayer(new ActivationTANH(), false, output));

        network.getStructure().finalizeStructure();
        network.reset();
    }


    public void train(boolean bool) {
        if (bool) {
            loadGenome();
        } else {
            createNeuralNetwork();
        }
        MLDataSet trainingSet = new CSVNeuralDataSet(trainDataLocation, input, output, true);

        //training the network
        Train train = new ResilientPropagation(network, trainingSet);

        if (bool) {
            TrainingContinuation training = loadTraining();
            train.resume(training);
        }
        do {
            train.iteration(200);
            System.out.println("Epoch #" + train.getIteration() + " Error:" + train.getError());
            if (train.getIteration() % 1000 == 0) {
                TrainingContinuation trainStore = train.pause();
                storeTraining(trainStore);
                System.out.println("Training is stored");
                storeGenome(train.getIteration());
                System.out.println("Network is stored");
                train.resume(trainStore);
            }
        } while (train.getError() > 0.0001);
        Encog.getInstance().shutdown();
    }

    public void checkTraining() {
        loadGenome();
        MLDataSet trainingSet;
        float sum = 0;
        try (Stream<Path> walk = Files.walk(Paths.get("check_train_data"))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            for (int i = 0; i < result.size(); i++) {
                trainingSet = new CSVNeuralDataSet(result.get(i), input, output, true);
                sum += network.calculateError(trainingSet);
            }
            System.out.println("Validation, Average error: " + sum / result.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public double[] compute(SensorModel a) {
        double[] inputValues = new double[22];
        inputValues[0] = a.getSpeed();
        inputValues[1] = a.getTrackPosition();
        inputValues[2] = a.getAngleToTrackAxis();
//        System.out.println(a.getTrackPosition());

        double[] trackEdgeSensors = a.getTrackEdgeSensors();
        System.arraycopy(trackEdgeSensors, 0, inputValues, 3, trackEdgeSensors.length);
        MLData data = new BasicMLData(inputValues);
        return network.compute(data).getData();
    }


    //Store the state of this neural network
    public void storeGenome(int iterations) {
        File networkFile = new File(networkFolder + "current_" + iterations + ".network");
        EncogDirectoryPersistence.saveObject(networkFile, network);
    }

    public void storeTraining(TrainingContinuation trainingContinuation) {
        File networkFile = new File(trainFolder + "current.eg");
        EncogDirectoryPersistence.saveObject(networkFile, trainingContinuation);
    }


    public void loadGenome() {
        File file = new File(networkFolder + networkFile + ".network");
        network = (BasicNetwork) (EncogDirectoryPersistence.loadObject(file));
    }

    public TrainingContinuation loadTraining() {
        File networkFile = new File(trainFolder + "current.eg");
        return (TrainingContinuation) EncogDirectoryPersistence.loadObject(networkFile);
    }


}