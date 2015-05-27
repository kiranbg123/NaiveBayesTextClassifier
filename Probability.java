import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class Probability {

	public Probability() {
		// TODO Auto-generated constructor stub
	}

	//The following data structure reperesents the distinct words in the entire document
	public static HashMap <String, Integer> distinctWords = new HashMap<String, Integer>();
	public static HashMap<String, HashMap<String, Integer>> wordCountInClassMap = new HashMap<String,HashMap<String,Integer>>();
	public static HashMap<String,Integer> totalWordCountInClass = new HashMap<String,Integer>();
	public static HashMap<String,HashMap<String,Double>> wordProbability = new HashMap<String,HashMap<String,Double>>();
	public static void updateDistinctWords(HashMap<Integer, HashMap<String, NaiveBayes.wordClass>> vocabulary)
	{
		HashMap<String, NaiveBayes.wordClass> tempWordList;
		String tempWord;
		int tempCount;
 		for(Map.Entry<Integer, HashMap<String, NaiveBayes.wordClass>> entry :vocabulary.entrySet())
		{
		    
			//Count the number of words present in each document
			tempWordList = entry.getValue();
				for(Map.Entry<String, NaiveBayes.wordClass> mapEntry: tempWordList.entrySet())
				{
					//Since we are counting the distinct Words in the entire dataset, increase the wordcount
				
					if(distinctWords.containsKey(mapEntry.getKey()))
						{
							distinctWords.put(mapEntry.getKey(), distinctWords.get(mapEntry.getKey() + 1));
						}
					else
					{
						//Increament the word to the number of words present in that class
							distinctWords.put(mapEntry.getKey(), 1);
					}
					
				}
			
		}
	}
	
	public static int getCountWordsinClass (HashMap<Integer, HashMap<String, NaiveBayes.wordClass>> vocabulary,String word, String className, HashMap<String,ArrayList<Integer>> classDocuments)
	{
		HashMap<String, NaiveBayes.wordClass> tempWordList;
		String tempWord;
		int wordCount =0;
		//System.out.println("Word to be processed is " + word);
		
		//If word Count is already calculated return the word Count
		if(wordCountInClassMap.containsKey(className) && (wordCountInClassMap.get(className).containsKey(word)))  
		{
			
			wordCount = wordCountInClassMap.get(className).get(word);
			//System.out.println("Class Name :"+ wordCountInClassMap.get(className) + "word :" + wordCountInClassMap.get(className).get(word));
			
		}
		
		//Else calculate the word Count in that class and return it
		else
		{
			
			HashMap <String, Integer> tempWordCountMap = new HashMap<String, Integer>();
			ArrayList<Integer> tempDocumentList = new ArrayList<Integer>();
			tempDocumentList = classDocuments.get(className);
			int docId = 0;
			//For each Document find the count of the word in  that Class
		for(int i =0;i<tempDocumentList.size(); i++)
		{
			docId = tempDocumentList.get(i);
			HashMap<String, NaiveBayes.wordClass> tempWordDocMap = vocabulary.get(docId);
			//Get the wordclass for Doc Id
			//System.out.println("Word IS :" + word);
			NaiveBayes.wordClass tempWordclass;
			//Get the Count if the Word is present in the Document
			if(tempWordDocMap.containsKey(word))
			{
				
				tempWordclass = tempWordDocMap.get(word);
			//Count the number of words present in each document
			
						{
							//Increment the word Count to number of words present in that class
							wordCount = wordCount + tempWordclass.count;
						}
						
				
		}
		//System.out.println("Putting word :" + word + " in Class :" + className + " Count: " + wordCount);
		tempWordCountMap.put(word, wordCount);
		wordCountInClassMap.put(className, tempWordCountMap);
		
		}
		
	}
		
		return wordCount;
	}
	
	public static TreeMap<String, Double> findClassProbability(HashMap<String, ArrayList<Integer>> classDocuments, int totalDocuments)
	{
		 ArrayList<Integer> docList = new ArrayList<Integer>();
		 //for each class find the probability
		 //p(class = a) = #no of documents with class = a/ total # of documents
		 
		 TreeMap<String, Double> classProbability = new TreeMap<String,Double>();
		for(Map.Entry<String, ArrayList<Integer>> entry: classDocuments.entrySet())
		 {
		 docList = entry.getValue();
		 
			 //Taking log of the probability to avoid precedng zeros
			 
			 classProbability .put(entry.getKey(),  (double)((double)docList.size()/(double)totalDocuments));
		 }
		 
		 return classProbability;
		
	}
	
	public static int getTotalWordsInClass(HashMap<Integer, HashMap<String, NaiveBayes.wordClass>> vocabulary, String className,HashMap<String,ArrayList<Integer>> classDocuments)
	{
		if(totalWordCountInClass.containsKey(className))
		{
			return totalWordCountInClass.get(className);
		}
		else
		{
			System.out.println("No Class exists with name :" + className);
			return -1;
		}
		
	}
	
	public static void updateTotalWordsInCalss(HashMap<Integer, HashMap<String, NaiveBayes.wordClass>> vocabulary,HashMap<String,ArrayList<Integer>> classDocuments) throws IOException, ClassNotFoundException
	{
		
		//Reference:http://stackoverflow.com/questions/12747946/how-to-write-and-read-a-file-with-a-hashmap
		HashMap<String, NaiveBayes.wordClass> tempWordList;
		String tempWord;
		
		{
		int docId;
		ArrayList<Integer> docList = new ArrayList<Integer>();
		//Find the number of words present in Each Class
		for(Map.Entry<String, ArrayList<Integer>> entry: classDocuments.entrySet())
		{
		int wordCount = 0;	
		docList = entry.getValue();
		
		for(int i =0; i <docList.size();i++)
		{
			docId = docList.get(i);
			tempWordList = vocabulary.get(docId);
			
			for(Map.Entry<String, NaiveBayes.wordClass> wordEntry: tempWordList.entrySet())
			{
				wordCount = wordCount + wordEntry.getValue().count;
			}
		}
		
		//Update the map
		totalWordCountInClass.put(entry.getKey(), wordCount);
 		
		}
		
		}
	}
	
	public static int getDistinctWords()
	{
		//This function returns the distinct words present in the entire document

		return distinctWords.size();
	}
	
	public static HashMap<String, HashMap<String,Double>> updateProbabilities(HashMap<Integer, HashMap<String, NaiveBayes.wordClass>> vocabulary,HashMap<String,ArrayList<Integer>> classDocuments)
	{
		
		ArrayList<Integer> tempDocumentList = new ArrayList<>();
		int distinctWords = getDistinctWords(); 
		//System.out.println("Entering word probability Building");
		//Calculate the word probability for each word in the class
		for(Map.Entry<String, ArrayList<Integer>> entry : classDocuments.entrySet())
		{
			int totalWordsInClass = 0;
			String className = entry.getKey();
			tempDocumentList =entry.getValue();
			totalWordsInClass = Probability.getTotalWordsInClass(vocabulary,className,classDocuments );
			HashMap <String, Double> tempWordProbabilityMap = new HashMap<String, Double>();
			
		for(int i =0;i<tempDocumentList.size(); i++)
		{
			int docId = tempDocumentList.get(i);
			
			HashMap<String,NaiveBayes.wordClass> document = vocabulary.get(docId);
			
			for(Map.Entry<String, NaiveBayes.wordClass> wordEntry : document.entrySet())
			{
				
				String word = wordEntry.getKey();
				//If probability is not already calculated then only calculate it
				if(wordProbability.containsKey(className) && wordProbability.get(className).containsKey(word))
				{
					
					continue;
				}
				int specificWordsInClass = 0;
				Double tempWordProbability;
				specificWordsInClass = Probability.getCountWordsinClass(vocabulary, word, className, classDocuments);
				//System.out.println("word :" + word + " count is :"+ specificWordsInClass);
				//Put the class Name and word count in the hashMap
				
				//System.out.println("COunt of word " + word + " :" +countOfWordsInTestDoc);
				
				
				//Need to calculate the probability
				
				//p^(w\c) = (count(w,c) + 1)/(count(c) + |v|)
				
				//Keeping the log of the probability to avoid preceeeding zeros
				//System.out.println("Word " + word + "class :" + className +"specific Words in Class:" + specificWordsInClass +" distinctWords in class: " + distinctWordsInDocument );
				tempWordProbability = Math.log((double)(specificWordsInClass + 1)/(double)(totalWordsInClass + distinctWords));
				//tempWordProbability.probability = ((double)(specificWordsInClass + 1)/(double)(totalWordsInClass + distinctWordsInDocument));
			//	System.out.println("Probability is " + tempWordProbability.probability);
				tempWordProbabilityMap.put(word, tempWordProbability);
				//Calculate word Probability
				
			}
			
			
		}
		wordProbability.put(className, tempWordProbabilityMap);
	}

		return wordProbability;
}
	
	public static double getWordProbability(String word, String className, HashMap<String, HashMap<String,Double>> wordProbabilities)
	{
		double probability = Double.NEGATIVE_INFINITY;
		
		if(wordProbabilities.containsKey(className) && wordProbabilities.get(className).containsKey(word))
		{
			probability = wordProbabilities.get(className).get(word);
		}
		return probability;
	}
}
