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

public class NeuralNetwork implements Serializable {
    private String trainFolder = "train/";
    private String networkFolder = "network/";
    private String trainDataLocation = "train_data/output.csv";
    private int input;
    private int output;

    private int layer1;
    private int layer2;

    BasicNetwork network;


    public NeuralNetwork(int inputs, int layer1, int layer2, int outputs) {
        this.input = inputs;
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.output = outputs;
    }

    public NeuralNetwork() {
    }

    private void createNeuralNetwork() {
        //22 100 40 3
        //	Give network structure
        network = new BasicNetwork();

        //  Create input layer
        network.addLayer(new BasicLayer(null, true, input));

        //  Create hidden layer(s)
        network.addLayer(new BasicLayer(new ActivationTANH(), true, layer1));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, layer2));

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
        MLDataSet trainingSet = new CSVNeuralDataSet(trainDataLocation, input, output, false);

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
                storeGenome();
                System.out.println("Network is stored");
                train.resume(trainStore);
            }
        } while (train.getError() > 0.01);
        Encog.getInstance().shutdown();
    }


    public double[] compute(SensorModel a) {
        double[] inputValues = new double[22];
        inputValues[0] = a.getSpeed();
        inputValues[1] = a.getTrackPosition();
        inputValues[2] = a.getAngleToTrackAxis();
        double[] trackEdgeSensors = a.getTrackEdgeSensors();
        System.arraycopy(trackEdgeSensors, 0, inputValues, 3, trackEdgeSensors.length);
        MLData data = new BasicMLData(inputValues);
        return network.compute(data).getData();
    }


    //Store the state of this neural network
    public void storeGenome() {
        File networkFile = new File(networkFolder + "current.network");
        EncogDirectoryPersistence.saveObject(networkFile, network);
    }

    public void storeTraining(TrainingContinuation trainingContinuation) {
        File networkFile = new File(trainFolder + "current.eg");
        EncogDirectoryPersistence.saveObject(networkFile, trainingContinuation);
    }

    public void loadGenome() {
        File networkFile = new File(networkFolder + "current.network");
        network = (BasicNetwork) (EncogDirectoryPersistence.loadObject(networkFile));
    }

    public TrainingContinuation loadTraining() {
        File networkFile = new File(trainFolder + "current.eg");
        return (TrainingContinuation) EncogDirectoryPersistence.loadObject(networkFile);
    }


}