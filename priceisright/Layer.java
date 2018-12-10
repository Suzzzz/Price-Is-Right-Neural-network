package priceisright;

import java.util.ArrayList;

/*
 * This class is basically a wrapper of ArrayList.
 */
public class Layer
{
ArrayList<Neuron> contents=new ArrayList<Neuron>();


public Layer(Neuron n)
	{
	contents=new ArrayList<Neuron>();
	contents.add(n);
	}

public Layer(ArrayList<Neuron> data)
	{contents=data;}

public Neuron get(int i)
	{return contents.get(i);}

public int size()
	{return contents.size();}
}
