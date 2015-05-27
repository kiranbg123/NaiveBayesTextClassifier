import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.sql.rowset.Predicate;


public class NaiveBayes {
private static BufferedReader buffer;
	
	private static HashMap<Integer, HashMap<String, wordClass>> vocabulary = new HashMap<Integer, HashMap<String, wordClass>>();
	private static HashMap<String, ArrayList<Integer>> classDocuments = new HashMap<String, ArrayList<Integer>>();
	private static TreeMap<String, Double> classProbability = new TreeMap<String, Double>();
	private static HashMap<String,Integer> classNames = new HashMap<String,Integer>();
	private static HashMap<String,Integer> classifications = new HashMap<String,Integer>();
	private static HashMap<String,HashMap<String,Double>> wordProbabilities = new HashMap<String,HashMap<String,Double>>();
	private static HashMap<String,HashMap<String,Double>> newWordProbability = new HashMap<String,HashMap<String,Double>>();
	private static Set<String> stopwordList = new HashSet<String>();
	private static HashMap<String, Integer> classList = new HashMap<String, Integer>();
	//private static ArrayList<Map.Entry<String, Integer>> list;
	
	private static int docId = 0;
	private static int totalDocuments = 0;
	private static int classNumber = 0;
	//private static Stemmer myStemmer = new Stemmer();


	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		if(args.length < 1)
		{
			System.out.println("You need to provide test Directory Path and PredictionOutputFile Path");
			System.exit(1);
		}
		String Train = "train";
		String stopwordsPath ="stopwords";
		String TestDataPath = args[0];
		String predictionOutputFilePath = args[1];
		System.out.println(Train);
		//Function to read Train directory into string
		String TrainContent = new String();
		File testFile = new File(TestDataPath);
		File stopWordsFile = new File(stopwordsPath);
		File TrainFiles = new File(Train);
		File predictionOutputFile = new File(predictionOutputFilePath);
		//Programm time starts
		System.out.println("Path to Train files : " + Train);
		String className = null;
		//If we already have vocabulary built, do not re-read the Train directory
		String tempWord;
		String vocabularyMapPath = "vocabulary.map";
		String classDocumentsPath ="classDocuments.map";
		String wordProbabilitiesPath = "wordProbabilies.map";
				
		File vocabularyMap = new File(vocabularyMapPath);
		File classDocumentsMap = new File(classDocumentsPath);
		File wordProbabilitiesMap = new File(wordProbabilitiesPath);
		stopwordList = parseStopWords(stopwordsPath);
		if(vocabularyMap.exists() && classDocumentsMap.exists() && wordProbabilitiesMap.exists())
		{
			//Read Vocabulary
			FileInputStream fis=new FileInputStream(vocabularyMap);
	        ObjectInputStream ois=new ObjectInputStream(fis);
	        vocabulary =(HashMap<Integer,HashMap<String, wordClass>>)ois.readObject();
	        ois.close();
	        fis.close();
	        
	      
	        
	        //Read classDocuments
	        fis=new FileInputStream(classDocumentsMap);
	        ois=new ObjectInputStream(fis);
	        classDocuments =(HashMap<String, ArrayList<Integer>>)ois.readObject();
	        ois.close();
	        fis.close();
	        
	        System.out.println("Loading Training Model into the Main memory....");
	        //Read WordProbabilities
	        fis=new FileInputStream(wordProbabilitiesMap);
	        ois=new ObjectInputStream(fis);
	        wordProbabilities =(HashMap<String, HashMap<String, Double>>)ois.readObject();
	        ois.close();
	        fis.close();
	        
	        Probability.updateDistinctWords(vocabulary);
			//This is the distinct words present in Entire training Set
			//Update totalWords in Each Class
			Probability.updateTotalWordsInCalss(vocabulary, classDocuments);
			classProbability = Probability.findClassProbability(classDocuments, vocabulary.size());
			int value = 0;
			for(Map.Entry<String, Double> entry: classProbability.entrySet()){
				classNames.put(entry.getKey(), value++);
			
				System.out.println( entry.getKey() + ": " +(value -1) );
			}
			
			
	        
		}
		else{
		ReadFile(TrainFiles, className,null);
		System.out.println("Building Training Model..");
		Probability.updateDistinctWords(vocabulary);
		//This is the distinct words present in Entire training Set
		//Update totalWords in Each Class
		Probability.updateTotalWordsInCalss(vocabulary, classDocuments);
		wordProbabilities = Probability.updateProbabilities(vocabulary, classDocuments);
		classProbability = Probability.findClassProbability(classDocuments, vocabulary.size());

		//Serialize the Map
	    	FileOutputStream fos=new FileOutputStream(vocabularyMap);
	        ObjectOutputStream oos=new ObjectOutputStream(fos);

	        oos.writeObject(vocabulary);
	        oos.flush();
	        oos.close();
	        fos.close();
	        
	        fos=new FileOutputStream(classDocumentsMap);
	        oos=new ObjectOutputStream(fos);

	        oos.writeObject(classDocuments);
	        oos.flush();
	        oos.close();
	        fos.close();
	        
	        fos=new FileOutputStream(wordProbabilitiesMap);
	        oos=new ObjectOutputStream(fos);

	        oos.writeObject(wordProbabilities);
	        oos.flush();
	        oos.close();
	        fos.close();
		}
		
		totalDocuments = vocabulary.size();
		System.out.println("Total number of documents: " + vocabulary.size());
		 	
		System.out.println("Vocabulary built");
		 System.out.println("Total Documents = " + totalDocuments);
		
	
		/*
		for(Map.Entry<String,Double> entry:classProbability.entrySet())
		{
			System.out.println("Class Name: " + entry.getKey() + "Probability" + entry.getValue());
		}*/
		readTestFile(testFile,predictionOutputFile);
		//Testing the files
		//String devLabelPath = "dev_labelTemp.txt";
		//File devLabel = new File(devLabelPath);
		//double accuracy = TestUtility.findAccuracy(devLabel, classifications);
	//	System.out.println("ACCURACY = " + accuracy*100 +"%");
		HashMap<Integer, ArrayList<String>> tempPrint = new HashMap<Integer,ArrayList<String>>();
		
		 
	}
	
	//The document Ids are used to find out the no of documents for each class
			static int prevDocId = 0;
			static int currentDocId = 0;
			
	public static void ReadFile(File TrainFiles, String className, ArrayList<Integer> docIds) throws IOException{
		//String TrainContent = new String();
		
		//System.out.println(docIds.size());
		
		for (File file: TrainFiles.listFiles())
		{
			
			//read files recursively if path contains folder
			if(file.isDirectory())
			{
				
				className = file.getName();
				//Assign class Number to each class.
				classNames.put(className, classNumber++);
				ArrayList<Integer> docList = new ArrayList<Integer>();
				//Update from first directory onwards
				
				prevDocId =docId; 
				ReadFile(file, className,docList);
				
				
				
				
			}
			
			else
			{
				//Keep track of document Ids for each class
				docId++;
				//classDocuments.put(className, docIds);
				classList.put(className, docId - prevDocId);
					//Add the new document Id
					docIds.add(docId);
				//Update the classDocument
				classDocuments.put(className, docIds);
				try
				{
				buffer = new BufferedReader(new FileReader(file));
				}
				catch (FileNotFoundException e)
				{
					System.out.println("File not Found");
					
				}
				//System.out.println("DocID is :" + docId + "Class Name is : " + className);
				//find words in the Training data
				//Update the total number of documents
				totalDocuments = docId;
				vocabulary = TokenHandler.handleTokens(file, vocabulary, className, docId,stopwordList);
				
			}
		}
		
		
	}
	
	public static void readTestFile(File testFile, File predictionOutputFile) throws IOException
	{
		PrintWriter printWriterFile= new PrintWriter (predictionOutputFile);
		
		for (File file: testFile.listFiles())
		{
			ArrayList<String> tempWords = new ArrayList<String>();
			//read files recursively if path contains folder
			if(file.isDirectory())
			{
				readTestFile(file,predictionOutputFile);
			}
		tempWords = TestUtility.testData(vocabulary, file);
		String [] words = tempWords.toArray(new String[tempWords.size()]);
			String finalClass = evaluatePosterior(words);
			System.out.println(file.getName() + " : " + finalClass);
			//Putting the file classification into hashMao for testing purpose
			classifications.put(file.getName(),classNames.get(finalClass));
			String line = file.getName() + " " + classNames.get(finalClass);
			//Write the output to textfile
			
			
			printWriterFile.write(line);
			  printWriterFile.println();
	}
		printWriterFile.close();
	}
	
	public static String evaluatePosterior(String[] words)
	{
		HashMap<String, Integer> wordMap =new HashMap<String, Integer>();
		HashMap<String, HashMap<String, wordProbability>> wordCountClassMap =new HashMap<String, HashMap<String, wordProbability>>();
		HashMap<String , Double> docInClassProbabilityMap = new HashMap<String, Double>();
		int countOfWordsInTestDoc = 0;
		int distinctWordsInDocument = 0;
	
		distinctWordsInDocument = Probability.getDistinctWords();
		countOfWordsInTestDoc = 0;
		//Stem the words
		wordMap = stemming(words);
		//System.out.println("wordMap size " +wordMap.size());
		//For each distinct word in document, find probability
		for(Map.Entry<String, Integer> wordEntry : wordMap.entrySet())
		{
			
		String	word = wordEntry.getKey();
			countOfWordsInTestDoc = wordEntry.getValue();
			HashMap<String, wordProbability> tempWordProbabilityMap = new HashMap<String,wordProbability>();
			
			//System.out.println("ClassProbability Size"+ classProbability.size());
			for(Map.Entry<String, Double> entry : classProbability.entrySet())
			{
				String className = entry.getKey();
				wordProbability tempWordProbability = new wordProbability();
				tempWordProbability.wordCount = countOfWordsInTestDoc;
				tempWordProbability.probability = Probability.getWordProbability(word,className,wordProbabilities);
				
				if(tempWordProbability.probability == Double.NEGATIVE_INFINITY)
				{
				
				//If already occured in any of the previous documents, the do not recalculate
					if(newWordProbability.containsKey(className) && newWordProbability.get(className).containsKey(word))
					{
						tempWordProbability.probability = newWordProbability.get(className).get(word);
					}
					else
					{
				int specificWordsInClass = 0;
				int totalWordsInClass = 0;
				HashMap<String, Double> tempNewWordProbability = new HashMap<String,Double>();
				//specificWordsInClass = Probability.getCountWordsinClass(vocabulary, word, className, classDocuments);
			//	System.out.println("word Not Found in Map :" + word);
				//Put the class Name and word count in the hashMap
				tempWordProbability.wordCount = countOfWordsInTestDoc;
				//System.out.println("COunt of word " + word + " :" +countOfWordsInTestDoc);
				
				totalWordsInClass = Probability.getTotalWordsInClass(vocabulary,className,classDocuments );
				//Need to calculate the probability
				
				//p^(w\c) = (count(w,c) + 1)/(count(c) + |v|)
				
				//Keeping the log of the probability to avoid preceeeding zeros
				tempWordProbability.probability = Math.log((double)(specificWordsInClass + 1)/(double)(totalWordsInClass + distinctWordsInDocument));
				//tempWordProbability.probability = ((double)(specificWordsInClass + 1)/(double)(totalWordsInClass + distinctWordsInDocument));
			//	System.out.println("Probability is " + tempWordProbability.probability);
				tempNewWordProbability.put(word,tempWordProbability.probability);
				newWordProbability.put(className,tempNewWordProbability);
					}
				}
				tempWordProbabilityMap.put(entry.getKey(), tempWordProbability);
				//Calculate word Probability
				
			}
		
			//update  the probability and count for each class of the word
			//so it is (word, (class, wordProbability))
			wordCountClassMap.put(word, tempWordProbabilityMap);
			
		}
		//Evaluate the probability of a document for each class
		docInClassProbabilityMap = evaluateProbability(wordCountClassMap);
		
		//Finally decide which class does the document belong to based on probabilities for each class
		String finalClass = new String();
		finalClass = evaluateDoc(docInClassProbabilityMap);
		return finalClass;
	}
	
	public static String evaluateDoc(HashMap<String,Double> docInClassProbabilities)
	{
		String className = null;
		//Not sure of this yet
		Double maxProbability = Double.NEGATIVE_INFINITY;
		
		//Find the class which highest Probability
	//	System.out.println("docInClassProbability size :" + docInClassProbabilities.size());
		for(Map.Entry<String,Double> entry: docInClassProbabilities.entrySet())
		{
		//System.out.println("Class: " + entry.getKey() + ": " + entry.getValue());
			if((maxProbability) <= entry.getValue())
			{
				maxProbability = entry.getValue();
				className = entry.getKey();
			//	System.out.println("probability" + maxProbability);
						
			}
			
		}
		return className;
		
		
	}
	public static HashMap<String, Double> evaluateProbability(HashMap<String, HashMap<String, wordProbability>> wordCountClassMap)
	{
		//This datastructure defines the probability of a document in each class
		HashMap<String,Double> classProbabilities = new HashMap<String, Double>();
		Double probabilityOfDocInClass = 1.00;
		//Sum up the probability of all words in a document for each class
		//System.out.println("Size of wordSet is" + wordCountClassMap.size());
		//System.out.println("Word Count Class Probability " + wordCountClassMap.size());
		for(Map.Entry<String, HashMap<String, wordProbability>> classEntry : wordCountClassMap.entrySet())
		{
			HashMap<String,wordProbability> classProbablitySet = classEntry.getValue();
			//Now sum up the log of each word Probability
			//i.e P(W|class) = p(w1|class) + p(w2|class)..... +p(wn|class)
		//	System.out.println("wordEntry size :" + classProbablitySet.size());
			for(Map.Entry<String,wordProbability> wordEntry: classProbablitySet.entrySet())
			{
				//System.out.println("Word Entry probability:" + wordEntry.getValue().probability );
				String className = new String();
				Double probability = 0.00;
				/*
				System.out.println("word Entry :" + wordEntry.getKey());
				wordProbability tempWordProbability = wordEntry.getValue();
				
				//p(w) + p(w) = 2 p(w)
				//i.e probability of word * number of times word occurs
				probabilityOfDocInClass = tempWordProbability.probability*tempWordProbability.wordCount;*/
				className = wordEntry.getKey();
				double tempProbability = wordEntry.getValue().probability;
			//	System.out.println("probability : " + tempProbability + " wordCount is " + wordEntry.getValue().wordCount);
				//tempProbability = Math.pow(tempProbability, wordEntry.getValue().wordCount);
				tempProbability = tempProbability * wordEntry.getValue().wordCount;
				//See if the class is present in the classProbabilities
				if(classProbabilities.containsKey(className))
				{
					//Update the probability
					probability = classProbabilities.get(className);
					
					//Not sure of this??
					//probability = probability + (wordEntry.getValue().probability * wordEntry.getValue().wordCount);
					//verifying 
					
					probability = probability + tempProbability;
					//System.out.println("Probability as of now is" + probability);
					
				}
				else
				{
					//calculate probability 
					//For each word probability of that word belonging to class is word count times the probability of word in class
				//	System.out.println("probability :" + wordEntry.getValue().probability + "wordCount" + wordEntry.getValue().wordCount);
					//Need to verify this??
					//probability = wordEntry.getValue().probability * wordEntry.getValue().wordCount;
					//verifying
					probability = probability  + tempProbability;
					//System.out.println("Probability as of now is" + probability);
				}
				//Update the map
				classProbabilities.put(className, probability);
						
			}
	
				
			
		}
		Double tempProbability = 0.00;
		for(Map.Entry<String,Double> probabilityEntry: classProbabilities.entrySet())
		{
		//Probability of doc = probability of each words in class * probability of class
		tempProbability = probabilityEntry.getValue();
		//System.out.println("probability before multiplying with class Probability :" + tempProbability + " * " + classProbability.get(probabilityEntry.getKey()));
		tempProbability = tempProbability + classProbability.get(probabilityEntry.getKey());
		
		//Update the hashMap of classProbabilities
		classProbabilities.put(probabilityEntry.getKey(), tempProbability);
		}
		
		
	return classProbabilities;
	}
	
	public static class wordClass implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			return "wordClass [className=" + className + ", count=" + count
					+ "]";
		}

		//word class presents wordcount and its class name only.
		String className;
		int count;
		
		public wordClass(String className, int wordCount)
		{
			this.className = className;
			this.count = wordCount;
		}
		
	}
	public static class wordProbability implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int wordCount; //number of times word occurs in testDoc
		double probability;
	}
	
	public static HashMap<String, Integer> stemming(String[] words)
	{
		HashMap<String, Integer> wordMap = new HashMap<String, Integer>();
		for(String word:words)
		{
			//Ignore stopwords
			if(stopwordList.contains(word))
			{
				continue;
			}
			String stemmedWord = null;
			String temp = word;
			  Stemmer myStemmer = new Stemmer();
			  //add the word to the stemmer
			  myStemmer.add(temp.toCharArray(), temp.length());
			  myStemmer.stem();
			  stemmedWord = myStemmer.toString();
			
			//find the count of the word in each class
			if(wordMap.containsKey(word))
			{
				wordMap.put(word, wordMap.get(word)+ 1);
			}
			else
			{
				wordMap.put(word, 1);
			}
		}
		
		return wordMap;
	}
	
	public static Set<String> parseStopWords(String filename) throws FileNotFoundException {
		Set<String> stopWords = new HashSet<>(); 
		Scanner scanner = new Scanner(new File(filename));
		while(scanner.hasNext()){
			stopWords.add(scanner.next());
		}
		scanner.close();
		return stopWords;
	}
}
