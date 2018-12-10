package priceisright;

/*
 * Simple Edge class for the network.
 */
public class Edge
{
public Neuron source,dest;
public double weight;
public double prevweight=0;
public double out=0;

public Edge(Neuron s, Neuron d)
	{
	source=s;dest=d;
	weight=Math.random()*2;
	}

public void fire()
	{
	
	}
}
