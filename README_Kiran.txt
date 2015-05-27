Name: Kiran Bhat Gopalakrishna
NetId: kxb140230

*************************************************************************************************
To Compile:

javac NaiveBayes.java

*************************************************************************************************

To Run:

java NaiveBayes <path to TestFiles directory <output.txt>

Assumption:
The Programm assumes that the following files/directories are present at the same folder from where the programm is being run

1)Train Folder
2)stopwords file
3)dev_label.txt

(All the necesserary files and folders are provided along with the source code)
*************************************************************************************************
Output:

Programm creates the Map and serializes it to Secondory drive so that the training model need not be rebuilt every time.
Sample outputKiran.txt is tested and run on the Dev Directory.Classification is also displayed on the screen output.


Programm takes around 5-7 minutes to build the training model for the First time.

Testing:
Seperate Test module is developed to test the accuracy of the the model.

*************************************************************************************************
To Compile:

javac TestClasssifier.java

*************************************************************************************************

To Run:

java TestClassifier <path to actualoutput.txt> <path to classifiedoutput.txt>
actualoutput.txt and classifiedoutput.txt should be in a similar format like dev_label.txt

