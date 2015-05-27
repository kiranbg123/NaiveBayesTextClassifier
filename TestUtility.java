import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class TestUtility {

	public TestUtility() {
		// TODO Auto-generated constructor stub
	}
	
	public static ArrayList<String> testData(HashMap<Integer, HashMap<String, NaiveBayes.wordClass>> vocabulary,File file) throws IOException
	{
		String line;
		String words[] = null;
		String tempLine[] = null;
		ArrayList<String> lines = new ArrayList<String>();
		HashMap<String, NaiveBayes.wordClass> wordList = new HashMap<String, NaiveBayes.wordClass>();
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		HashMap<Integer, HashMap<String,NaiveBayes.wordClass>> tempMap = new HashMap<Integer, HashMap<String,NaiveBayes.wordClass>>();
		 while((line = buffer.readLine()) != null)
	        {
	        	 //Replace everything that is not an alphabet with a blank space.
	        	String s1 = line.replaceAll("[^a-zA-Z.]+"," ");
	        	//Replace words with . (eg U.S) as 1 word
	            String s2 = s1.replaceAll("[.]", "");
	            String s3 = s2.replaceAll("\\s+", " ");
	            s3 = s3.toLowerCase();
	        	tempLine = s3.split(" ");
	        	//Add all words in the line
	        	for(int i =0; i <tempLine.length; i++)
	        	{
	        		lines.add(tempLine[i]);
	        	}
	        }
		 words = lines.toArray(new String[lines.size()]);
		 		 
		 buffer.close();
	            return lines;
	            
	}
	
	public static double findAccuracy(File devLabel, HashMap<String,Integer> classifications) throws IOException
	{
		Double accuracy = 0.00;
		String line;
		String[] words;
		int totalNumberOfFiles = 0;
		int correctlyClassifiedFiles = 0;
		HashMap<String,Integer>actualClassifications = new HashMap<String,Integer>();
		
		//Read the content of the devLabel line by line and put it in a hashmap
		FileReader inputFile = new FileReader(devLabel);

        BufferedReader bufferReader = new BufferedReader(inputFile);
        
        while ((line = bufferReader.readLine()) != null)   {
        	String fileName = new String();
        	int classLabel;
        	
        	words = line.split(" ");
        	fileName = words[0];
        	classLabel = Integer.parseInt(words[1]);
        	actualClassifications.put(fileName,classLabel);
        	
        }
        
        //Close the buffer reader
        bufferReader.close();
        totalNumberOfFiles = classifications.size();
        Integer actualClassLabel;
        String fileName;
        int count = 0;
        for(Map.Entry<String, Integer> entry : actualClassifications.entrySet())
        {
        	count++;
        	fileName = entry.getKey();
        	actualClassLabel = entry.getValue();
        	//Increament the count if the file if correctly classified
        	if(actualClassLabel == classifications.get(fileName))
        	{
        		
        		correctlyClassifiedFiles++;
        	}
        	else
        	{
        		System.out.println("File :" + count + "Correctly not classified");
        	}
        	
        }
        
        accuracy = (double) ((double)(correctlyClassifiedFiles)/(double)(totalNumberOfFiles));
        
        
		return accuracy;
	}

}
