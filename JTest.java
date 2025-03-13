import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @author Richmond Nartey Tettey, 03/27/2025
 * @author Samuel Jin, 03/27/2025
 *
 * */

public class JTest {
    private HMM hmm;
    public int numIncorrect;
    public int numCorrect;


    /**
     * constructor method
     * @param textName is the file path that contains sentences
     * @param tagsName is the file path to the POS/ tags
     * */
    public JTest(String textName, String tagsName){
        hmm = new HMM();
        try {
            hmm.read_files(textName, tagsName);
        }catch(Exception e){
            System.err.println("Test Failed!" + e);
        }

        numIncorrect = 0;
        numCorrect = 0;
    }


    /**
     * test tags with answer tags
     * @param testText is the file path to the test file for texts
     * @param testTags is the file path to the test file for the tags/answer keys
     * */
    public void accuracyTest(String testText, String testTags){

        //read buffered Readers
        BufferedReader textTestFile = null;
        BufferedReader tagsTestFile = null;

        try{
            textTestFile = new BufferedReader(new FileReader(testText));
            tagsTestFile = new BufferedReader(new FileReader(testTags));
        }catch(IOException e){
            System.out.println("file not found:" + e);
        }

        //if buffered readers exists continue
        if(textTestFile != null && tagsTestFile != null){
            try{

                //initialize new tag line and text line
                String tagLine;
                String textLine;

                while((tagLine = tagsTestFile.readLine()) != null && (textLine = textTestFile.readLine()) != null){

                    //add each word in sentence to string array
                    String[] actualTags = tagLine.split(" ");

                    //call the viterbi method and get the tags to list
                    List<String> response = hmm.viterbi(textLine);
                    if (actualTags.length != response.size()){
                        System.out.println("Different sizes!");
                    }

                    //iterate each tag/answer keys and compare it to response
                    for(int i = 0; i < actualTags.length; i++){
                        if(actualTags[i].equals(response.get(i))){
                            //if they are the same increase numCorrect by 1
                            numCorrect++;
                        } else {
                            //if they are not the same increase numIncorrect by 1
                            numIncorrect++;
                        }
                    }
                }

                //output the incorrect and  correct values
                System.out.println("incorrect: " + numIncorrect + " correct: " + numCorrect);

                //calculate the accuracy in percentage
                double percentage = (numCorrect / ((double) numCorrect + numIncorrect) * 100);

                System.out.println("accuracy: " + String.format("%.3g",percentage) + "%");

            }catch(Exception e){
                System.err.println("testing file failed!" + e);
            }
        }
    }


    public static void main(String args[]){
        String relative_path = "/texts/";

        String root = new File("").getAbsolutePath();

        //train and test brown file datasets
        String trainBrownTags = root + relative_path + "brown-train-tags.txt";
        String trainBrownSentences = root + relative_path + "brown-train-sentences.txt";

        String testBrownTags = root + relative_path + "brown-test-tags.txt";
        String testBrownSentences = root + relative_path +  "brown-test-sentences.txt";

        //train and test on simple test datasets
        String trainSimpleTags = root + relative_path + "simple-train-tags.txt";
        String trainSimpleSentence = root + relative_path + "simple-train-sentences.txt";

        String testSimpleTags = root + relative_path + "simple-test-tags.txt";
        String testSimpleSentence = root + relative_path + "simple-test-sentences.txt";


        //Brown Test
        System.out.println("Brown Test");
        JTest unitTest = new JTest(trainBrownSentences, trainBrownTags);

        unitTest.accuracyTest(testBrownSentences, testBrownTags);

        //Simple sentence Test
        System.out.println("Simple Test");
        JTest unitTest1 = new JTest(trainSimpleSentence, trainSimpleTags);

        unitTest1.accuracyTest(testSimpleSentence, testSimpleTags);

    }
}
