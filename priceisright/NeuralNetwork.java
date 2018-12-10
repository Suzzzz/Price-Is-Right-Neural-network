package priceisright;

import java.util.ArrayList;

/*
 * This class represents the structure and algorithms of
 * the NN as a whole.
 * @author SchnelT
 */
public class NeuralNetwork
{
ArrayList<Layer> layers=new ArrayList<Layer>();
ArrayList<Layer> backupLayers;
double learningRate=0.1;
int[] layerdata;

/*
 * Creates the structure for the ANN. hiddenlayerdata is a one-dimensional array
 * containing the number of neurons in each hidden layer. For example, if you want the ANN
 * to have layers of size 4 -> 5 -> 4 -> 1, this would be {4,5,4}.
 */
public NeuralNetwork(int[] layerdata)
	{
	this.layerdata=layerdata;
	int nLayers=layerdata.length;
	//output layer only has one neuron
	Neuron out=new Neuron();
	Layer lOut=new Layer(out);
	
	//create blank neurons and assign them to hidden layers
	for (int i=0;i<nLayers;i++)
		{
		ArrayList<Neuron> contents=new ArrayList<Neuron>();
		for (int j=0; j<layerdata[i]; j++)
			{
			Neuron temp=new Neuron();
			contents.add(temp);
			}
		Layer temp=new Layer(contents);
		layers.add(temp);
		}
	layers.add(lOut);
	
	//layers are all in place, now connect all neurons with edges
	for (int i=0;i<layers.size()-1;i++)
		{
		Layer thislayer=layers.get(i);
		//for each neuron in this layer
		for (int j=0;j<thislayer.size();j++)
			{
			//for each neuron in next layer
			Neuron thisNeuron=thislayer.get(j);
			for (int k=0; k<layers.get(i+1).size(); k++)
				{
				Edge e=new Edge(thisNeuron,layers.get(i+1).get(k));
				layers.get(i).get(j).addEdge(e);
				}
			}
		}
	}

/*
 * Sigmoid function.
 */
private double sigmoid(double input)
	{return 1/(1+Math.exp(-input));}

/*
 * Feed forward. This takes an input and runs it through the entire network,
 * which sets all the neurons' outputs for backpropagation.
 * Returns the output of the output layer's first neuron (should be the only neuron)
 */
public double feedForward(double[] in)
{
    double sum=0;
    for(int i=0; i<layers.get(0).size(); i++)
    	{
    	layers.get(0).get(i).value=in[i];
    	}

	// For each layer
	for(int i=1; i<layers.size(); i++)
		{
		// For each neuron in current layer  
		for(int j=0; j<layers.get(i).size(); j++)
			{
			sum=0;
			// For each neuron in preceding layer
			for(int k=0; k<layers.get(i-1).size(); k++)
				{
				// Apply weight to inputs and add to sum
				//sum+= out[i-1][k]*weight[i][j][k];
				double output=layers.get(i-1).get(k).value*layers.get(i-1).get(k).getEdge(j).weight;
				sum+=output;    
				}
			// Apply bias
			//sum+=layers.get(i).get(layers.get(i-1).size()).getEdge(j).weight;                
			// Apply sigmoid function
			layers.get(i).get(j).value=sum;
			}
		}
	return layers.get(layers.size()-1).get(0).value;
}

/*
 * The backpropagation part that looks back and tweaks errors.
 */
public double backprop(double[] in, double target)
	{
	backupNetwork();
	double sum=0;
	int badupdatecount=0;
	System.out.println("======================================================\nBackpropping "+target);
	double output=feedForward(in);
	double errorpct=(output-target)/target;
	double lasterror=errorpct;
	
	//if the network can't learn this input, it causes an infinite loop, so we
	//only allow it to try 100 updates before reverting our changes.
	//I don't remember why the Thread.sleeps are here. Some of the values
	//on the nodes/edges might be cached by the JVM.
	while (Math.abs(errorpct)>0.2 && badupdatecount<100)
		{
		layers.get(layers.size()-1).get(0).error=errorpct;
		try{
			if (errorpct>=lasterror)
				{
				badupdatecount++;
				//System.out.println("Output: "+output+"\nError: "+errorpct+"\nTarget: "+target);
				Thread.sleep(1);
				}
			else {badupdatecount=0;}
			Thread.sleep(1);
		}catch (InterruptedException e){e.printStackTrace();}
		//backpropagate errors
		for (int i=layers.size()-2;i>=0;i--)
			{
			for (int j=0;j<layers.get(i).size();j++)
				{
				sum=0;
				Neuron ij=layers.get(i).get(j);
				for (int k=0;k<layers.get(i+1).size();k++)
					{
					Neuron ik=layers.get(i+1).get(k);
					sum+=ik.error*ij.getEdge(k).weight;
					}
				ij.error=sum;
				}
			}
		//update weights
		for (int i=0;i<layers.size()-1;i++)
			{
			//for each neuron ij in layer i
			for (int j=0;j<layers.get(i).size();j++)
				{
				Neuron ij=layers.get(i).get(j);
				if (ij.error<0.01)
					{
					continue;
					}
				//for each neuron (i+1)k in layer i+1
				for (int k=0;k<layers.get(i+1).size();k++)
				{
					//this was our original equation, but the lack of clamping on
					//the update values sometimes caused rounding errors
					//to push the update amount to infinity, so we wrote the
					//code in the else block instead.
				if (false)//errorpct>=lasterror)
					{
					Neuron ik=layers.get(i+1).get(k);
					double weight=ij.getEdge(k).weight;
					//magnitude of error must be scaled to values between 0 and 2
					System.out.println("\n"+i+" "+j+" Error: "+ij.error);
					System.out.println("Sigmoid value: "+sigmoid(ij.error*learningRate));
					double sigmoid=sigmoid(ij.error*learningRate);
					//sigmoid goes to 0 if error<0, goes to 1 if error>0
					double tgt=(0.5-sigmoid);
					//tgt goes from -0.5 to +0.5
					System.out.println("target value: "+tgt);
					System.out.println("Old edge weight: "+ij.getEdge(k).weight);
					//weight needs to be lowered
					if (tgt<0)ij.getEdge(k).weight=(weight-(weight*Math.abs(tgt)));
					//weight needs to be raised
					if (tgt>0)ij.getEdge(k).weight=(weight+(weight*Math.abs(tgt)));
					System.out.println("New edge weight: "+ij.getEdge(k).weight);
					}
				else
					{
					Neuron ik=layers.get(i+1).get(k);
					double weight=ij.getEdge(k).weight;
					double sigmoid=sigmoid(ij.error*learningRate);
					double tgt=(0.5-sigmoid);
					if (tgt<0)ij.getEdge(k).weight=(weight-(weight*Math.abs(tgt)));
					if (tgt>0)ij.getEdge(k).weight=(weight+(weight*Math.abs(tgt)));
					}
				}
				}
			}
		output=feedForward(in);
		lasterror=errorpct;
		errorpct=(output-target)/target;
		}
	//abort and restore previous configuration
	if (badupdatecount>=100)
		{
		System.out.println(layers.get(0).get(0).getEdge(0).weight);
		layers=backupLayers;
		backupNetwork();
		System.out.println(layers.get(0).get(0).getEdge(0).weight);
		return -1;
		}
	else
		{
		//we converged, and need to backup the network
		System.out.println("Passed, test result = "+output);
		backupNetwork();
		return errorpct;
		}
	}
/*
 * Copies the current network configuration from layers into backupLayers
 * in case anything goes wrong and we need to abort an update
 */
private void backupNetwork()
	{
	NeuralNetwork copy=new NeuralNetwork(layerdata);
	//copy all edge weights
	for (int i=0;i<layers.size()-1;i++)
		{
		for (int j=0;j<layers.get(i).size();j++)
			{
			for (int k=0;k<layers.get(i+1).size();k++)
				{
				copy.layers.get(i).get(j).getEdge(k).weight=layers.get(i).get(j).getEdge(k).weight;
				}
			}
		}
	backupLayers=copy.layers;
	}
}