package priceisright;

import java.util.ArrayList;

public class Neuron
{
//list of edges from this neuron to the next layer
ArrayList<Edge> edges=new ArrayList<Edge>();

//current buildup of the neuron. This ends up being the neuron's output before weights.
double value=0;
double output=0;

//the delta for the neuron. Used to update weights.
double error=0;

//how much buildup required to fire the neuron's output
//We probably won't use this. We'll just make the neuron fire when all previous
//neurons have given it input.
double threshold=0;

//Constructor takes no arguments and creates a "blank" neuron
public Neuron()
{}

public void addEdge(Edge e)
	{
	edges.add(e);
	}

/*
 * Gets the edge connecting this neuron to neuron n in the next layer.
 */
public Edge getEdge(int n)
	{
	return edges.get(n);
	}

/*
 * Give this neuron input
 */
public void input(double in)
	{
	value=in;
	}

/*
 * Preps this neuron for firing. This is used in the feed forward loop
 * to propagate each neuron's output so the next layer can use it.
 * 
 * This sends data to the edges, NOT to the next layer. The propagation algorithm
 * is responsible for moving the data forward.
 */
public void fire()
	{
	for (int i=0;i<edges.size();i++)
		{
		edges.get(i).out=value*edges.get(i).weight;
		}
	}
}
