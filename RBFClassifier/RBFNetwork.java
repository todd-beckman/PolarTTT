package RBFClassifier;

import java.util.Arrays;

import Logic.Main;

public class RBFNetwork {
	protected GaussianNode[] gnodes;
	public OutputNode[] onodes;
	protected float learning_rate;
	
	
	/**
	 * Constructs an actual Radial-Basis Function network
	 * @param num_inputs The number of input layer nodes
	 * @param num_gaussian The number of hidden layer nodes
	 * @param num_outputs The number of output layer nodes (classifications)
	 */
	public RBFNetwork(int num_inputs, int num_gaussian, int num_outputs, float learning_rate, float gaussian_width) {
		this.learning_rate = learning_rate;
		GaussianNode.C = gaussian_width;
		
		//	Save the kmeans data to initialize the gnodes
		gnodes = new GaussianNode[num_gaussian];
		for (int i = 0; i < num_gaussian; i++) {
			//	Inputs = input space
			gnodes[i] = new GaussianNode();
		}
		
		//	Initialize the output nodes (random weights will be updated)
		onodes = new OutputNode[num_outputs];
		for (int i = 0; i < num_outputs; i++) {
			
			//	Inputs = number of hidden nodes
			onodes[i] = new OutputNode(num_gaussian);
		}
	}
	
	/**
	 * Gets the trained outputs of the network given input
	 * @param input
	 * @return The outputs
	 */
	public float[] get_output(float[] input, int num_input) {

		//	Feed input layer into gaussian layer
		float gaussian[] = new float[gnodes.length];
		for (int i = 0; i < gaussian.length; i++) {
			gaussian[i] = gnodes[i].output(input, num_input);
		}
		
		//	Feed gaussian layer into output layer
		float output[] = new float[onodes.length];
		for (int i = 0; i < output.length; i++) {
			output[i] = onodes[i].output(gaussian);
		}
		
		return output;
	}
	
	/**
	 * Updates the weights of the hidden layer (the heights of the
	 * Gaussian functions) in order to allow the network to learn.
	 * @param expected What was thought to be the outcome
	 * @param outcome The actual outcome
	 */
	public void back_propogate(float[] expected, float[] outcome) {
		
		//	Calculate the error for each node of the output layer
		for (int i = 0; i < onodes.length; i++) {
			
			//	Get the error of this classification
			float error = expected[i] - outcome[i];
			
			//	Update the weights
			for (int j = 0; j < onodes[i].weights.length; j++){
				float new_weight = onodes[i].weights[j] +
						learning_rate *
						error *
						onodes[i].last_input[j];// * onodes[i].weights[j];
				//	The weight update function
				onodes[i].weights[j] = (new_weight < 0.000000001) ? 0 : new_weight;
			}
		}
	}
}

class GaussianNode {
	float[] centers;
	protected static float C;
	
	public GaussianNode() { }
	
	public float output(float[] raw, int num_input) {
		try {
			//	Gaussify the distance between input layer and the gauss centers
			return gaussian_function(
				RBFClassifier.euclidean_distance(raw, centers, num_input)
			);
		}
		catch (Exception e) {
			Main.sout("Problem line",Arrays.toString(raw));
			Main.sout("Centers",Arrays.toString(centers));
			e.printStackTrace();
			return 0;
		}
	}
	
	public void set_centers(float[] centers) {
		this.centers = new float[centers.length];
		System.arraycopy(centers, 0, this.centers, 0, centers.length);
	}
	
	private static float gaussian_function(double in) {
		return (float)Math.exp(- (in * in) / (2 * C * C));
	}
}

class OutputNode {
	protected float[] weights;
	protected float[] last_input;
	
	public OutputNode(int num_inputs) {
		weights = new float[num_inputs];
		for (int i = 0; i < num_inputs; i++) {
			
			//	Keep the weights between -.5 and .5
			weights[i] = (float)(Math.random() - .5);
		}
	}
	
	public float output(float[] goutput) {
		last_input = goutput;
		
		float sum = 0f;
		
		//	Dot product weights*goutput
		for (int i = 0; i < goutput.length; i++) {
			sum += weights[i] * goutput[i];
		}
		
		return sum;
	}
}

