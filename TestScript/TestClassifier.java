import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class TestClassifier {

	public TestClassifier() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if(args.length < 2)
		{
			System.out.println("You need to provide exactly 2 arguments: actual classification and predicted Classification Files");
		}

		String devLabelPath = args[0];
		String predictedOutputFilePath = args[1];
		File devLabel = new File(devLabelPath);
		File predictedOutputFile = new File(predictedOutputFilePath);
		double accuracy = 0;
		accuracy = findAccuracy(devLabel, predictedOutputFile);
		System.out.println("ACCURACY = " +accuracy*100 +"%");
	}
	
	public static double findAccuracy(File devLabel, File predictedOutputFile) throws IOException
	{
		Double accuracy = 0.00;
		String line;
		String[] words;
		int totalNumberOfFiles = 0;
		int correctlyClassifiedFiles = 0;
		HashMap<String,Integer>actualClassifications = new HashMap<String,Integer>();
		HashMap<String,Integer>predictedClassifications = new HashMap<String,Integer>();
		
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
        inputFile.close();
        
        //Read prectided Output File
        
         inputFile = new FileReader(predictedOutputFile);

        bufferReader = new BufferedReader(inputFile);
        
        words = null;
        int classLabel;
        while ((line = bufferReader.readLine()) != null)   {
        	String fileName = new String();
        	
        	
        	words = line.split(" ");
        	fileName = words[0];
        	classLabel = Integer.parseInt(words[1]);
        	predictedClassifications.put(fileName,classLabel);
        	
        }
        
        //Close the buffer reader
        bufferReader.close();
        inputFile.close();
        totalNumberOfFiles = actualClassifications.size();
        Integer actualClassLabel;
        String fileName;
        for(Map.Entry<String, Integer> entry : actualClassifications.entrySet())
        {
        	fileName = entry.getKey();
        	actualClassLabel = entry.getValue();
        	//Increament the count if the file is correctly classified
        	if(actualClassifications.get(fileName) == predictedClassifications.get(fileName))
        	{
        		correctlyClassifiedFiles++;
        	}
        	
        }
        
        accuracy = (double) ((double)(correctlyClassifiedFiles)/(double)(totalNumberOfFiles));
        
        
		return accuracy;
	}

}
