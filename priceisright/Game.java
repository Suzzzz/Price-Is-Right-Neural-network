package priceisright;

import java.io.*;
import java.net.URL;
import java.util.*;

public class Game
{
public static void main(String[] args)
	{
	final String delimiter=",";
	final int ITERATIONS=1;
	int[] layerdata={5,6,5};
	int success=0;
	NeuralNetwork net=new NeuralNetwork(layerdata);
	
	//load inputs from file
	try{
	File in=new File(Game.class.getClassLoader().getResource("Inputs.txt").toURI());
	Scanner scan=new Scanner(in);
	String sLine="";
	int lines=0;
	
	//first, count the number of lines in the file
	while (scan.hasNextLine())
		{
		scan.nextLine();
		lines++;
		}
	
	double[][] trainingData=new double[lines][6];
	scan=new Scanner(in);
	lines=0;
	//load training data
	while (scan.hasNextLine())
		{
		sLine=scan.nextLine();
		StringTokenizer st=new StringTokenizer(sLine,",");
		trainingData[lines][0]=Double.parseDouble(st.nextToken());
		trainingData[lines][1]=Double.parseDouble(st.nextToken());
		trainingData[lines][2]=Double.parseDouble(st.nextToken());
		trainingData[lines][3]=Double.parseDouble(st.nextToken());
		trainingData[lines][4]=Double.parseDouble(st.nextToken());
		trainingData[lines][5]=Double.parseDouble(st.nextToken());
		lines++;
		}
	
	//train the NN
	for (int i=0;i<trainingData.length;i++)
		{
		double[] temp={trainingData[i][0],trainingData[i][1],trainingData[i][2],trainingData[i][3],trainingData[i][4]};
		System.out.println("Learning input "+i);
		for (int j=0;j<ITERATIONS;j++)
			{
			double passed=net.backprop(temp, trainingData[i][5]);
			if (passed!=-1)
				{
				success++;
				System.out.println("Passed with "+passed+" max error.");
				}
			else
				{
				System.out.println("Backprop failed.");
				}
			}
		}
	System.out.println(success+" inputs successfully trained.");
	
	//start the guessing game
	boolean game=true;
	scan=new Scanner(System.in);
	while (game)
		{
		System.out.println("\nEnter laptop specs (Format: )");
		String parseme=scan.nextLine();
		try{
		String[] parsed=parseme.split(delimiter);
		double[] data={0,0,0,0,0};
		for (int i=0;i<5;i++)
			{
			data[i]=Double.parseDouble(parsed[i]);
			}
		double result=net.feedForward(data);
		System.out.println("(FFWD result = "+result+")");
		System.out.println("Enter your guess:");
		double punyHumanGuess=Double.parseDouble(scan.nextLine());
		double guess=0;
		if (punyHumanGuess<result)guess=punyHumanGuess+0.01;
		else guess=result*0.85;
		System.out.println("Your guess is "+punyHumanGuess+"\nMy guess is "+guess);
		System.out.println("(FFWD result = "+result+")");
		scan.nextLine();
		}catch (Exception e){e.printStackTrace();}//System.out.println("Invalid format.");}
		}
	}catch (Exception e){e.printStackTrace();}
	}
}
