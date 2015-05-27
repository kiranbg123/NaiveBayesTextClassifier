import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TokenHandler {

private static BufferedReader buffer;
	
	
	private static HashMap<String, Integer> stemmedWordList = new HashMap<String, Integer>();
	private static ArrayList<HashMap <String,Integer>> fileMap = new ArrayList<HashMap<String,Integer>>();
	private static ArrayList<HashMap <String,Integer>> stemmedFileMap = new ArrayList<HashMap<String,Integer>>();
	private static Set<String> tagNames = new HashSet<String>();
	//private static ArrayList<Map.Entry<String, Integer>> list;
	
	private static int documentsCount = 0;
	private static int totalTokens = 0;
	private static int uniqueWords = 0;
	private static int tagCount = 0;
	private static int singleOccureneWords = 0;
	private static int averageTokens;
	//private static int documentsCount = 0;
	private static int stemmedTotalTokens = 0;
	private static int stemmedUniqueWords = 0;
	private static int stemmedTagCount = 0;
	private static int stemmedSingleOccureneWords = 0;
	private static int stemmedAverageTokens;
	private static ArrayList<Map.Entry<String, Integer>> sortedList;
	
	public static HashMap<Integer, HashMap<String, NaiveBayes.wordClass>>  handleTokens(File file, HashMap<Integer, HashMap<String, NaiveBayes.wordClass>> vocabulary, String className, int docId, Set<String> stopWords ) throws IOException
	{
		String line;
		String words[];
		HashMap<String, NaiveBayes.wordClass> wordList = new HashMap<String, NaiveBayes.wordClass>();
		buffer = new BufferedReader(new FileReader(file));
		HashMap<Integer, HashMap<String,NaiveBayes.wordClass>> tempMap = new HashMap<Integer, HashMap<String,NaiveBayes.wordClass>>();
		//Need one more map if stemming is implimented in future
		//HashMap<String, Integer> stemmedTempMap = new HashMap<String, Integer>();
        
        while((line = buffer.readLine()) != null)
        {
        	 //Replace everything that is not an alphabet with a blank space.
        	String s1 = line.replaceAll("[^a-zA-Z.]+"," ");
        	//Replace words with . (eg U.S) as 1 word
            String s2 = s1.replaceAll("[.]", "");
        	words = s2.split(" ");
           
        	
            for(String word : words)
            {
           //Ignore stopwords
            	if(stopWords.contains(word))
            	{
            		continue;
            	}
            		word = word.toLowerCase(); // Converts all words to lower case.
            		
            		//Stem the word
            		String stemmedWord = null;
        			String temp = word;
        			  Stemmer myStemmer = new Stemmer();
        			  //add the word to the stemmer
        			  myStemmer.add(temp.toCharArray(), temp.length());
        			  myStemmer.stem();
        			  stemmedWord = myStemmer.toString();
            		
            		//add word if it isn't added already
            		if(!wordList.containsKey(stemmedWord))
            		{   
            			 //Need temporary wordclass to store in the hashMap
            			NaiveBayes.wordClass tempWordClass = new NaiveBayes.wordClass(className, 1);
            			//first occurance of this word
            			wordList.put(word,tempWordClass ); 
            			
            		}
            		else
            		{
            			//Increament the count of that wordClass
            			NaiveBayes.wordClass existingWordClass;
            			existingWordClass = wordList.get(stemmedWord);
            			existingWordClass.count = existingWordClass.count + 1;
            			wordList.put(stemmedWord, existingWordClass);
            		}
            		
            	
            	
            }
        }
        
        //Add the hashMap of word to the main vocabulary
        if(vocabulary.containsKey(docId))
        {
        	vocabulary.put(docId, wordList);
        	System.out.println("Something is Wrong. Duplicate Document Ids");
        }
        else
        {
        	vocabulary.put(docId, wordList);
        }
       // stemmedFileMap.add(stemmedTempMap);
        
        return vocabulary;
	}
	
	
}
