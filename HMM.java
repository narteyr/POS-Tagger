/**
 * Implementation code for a Hidden Markov Model Viterbi Algorithm
 * Includes console-driven testing
 * @author Richmond Nartey Tettey, 03/27/2025
 * @author Samuel Jin, 03/27/2025
 *
 * */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class HMM {
    Map<String, Map<String, Double>> transition_probabilities = new HashMap<>();
    Map<String, Map<String, Double>> observation_probabilities = new HashMap<>();
    public HMM() {}

    /**
     * Load the text and input files and store the data in the instance variables
     * @param text_file
     * @param label_file
     * @throws Exception
     */
    public void read_files(String text_file, String label_file) throws Exception
    {
        BufferedReader r1 = new BufferedReader(new FileReader(text_file));
        BufferedReader r2 = new BufferedReader(new FileReader(label_file));
        String line1;
        String line2;
        while ((line1 = r1.readLine()) != null && (line2 = r2.readLine()) != null)
        {
            String[] words = line1.split(" ");
            String[] labels = line2.split(" ");
            //add the initial state transition from # to first state
            simplified_add(transition_probabilities, "#" ,labels[0]);
            //keep looping through words and update the transition counts in the instance variable maps
            for (int i = 0; i < labels.length -1; i++)
            {
                //keep updating state-state transition probability table
                simplified_add(transition_probabilities, labels[i], labels[i+1]);
                //keep updating state-word observation probability table
                simplified_add(observation_probabilities, labels[i], words[i]);
            }
            //update the state-word transition probability table
            simplified_add(observation_probabilities, labels[labels.length -1], words[words.length -1]);
        }
        r1.close();
        r2.close();
        //convert next state count to state transition probabilities in log e
        for (String state: transition_probabilities.keySet())
        {
            double total_num = 0;
            for (String inner_state: transition_probabilities.get(state).keySet())
            {
                total_num += transition_probabilities.get(state).get(inner_state);
            }
            for (String inner_state: transition_probabilities.get(state).keySet())
            {
                Double current_count = transition_probabilities.get(state).get(inner_state);
                transition_probabilities.get(state).put(inner_state, Math.log(current_count/total_num));
            }
        }
        //convert observation count to observation probabilities in log e
        for (String state: observation_probabilities.keySet())
        {
            double total_num = 0;
            for (String inner_word: observation_probabilities.get(state).keySet())
            {
                total_num += observation_probabilities.get(state).get(inner_word);
            }
            for (String inner_word: observation_probabilities.get(state).keySet())
            {
                Double current_count = observation_probabilities.get(state).get(inner_word);
                observation_probabilities.get(state).put(inner_word, Math.log(current_count/total_num));
            }
        }
    }

    public List<String> viterbi(String sentence){

        Double unseen = -144.0;

        /**
         * to calculate score
         * score = observation score(next state) + transition score + current score
         * */
        //stores the  best tag for each word
        //this is for back tracking to top
        ArrayList<String> bestStatePaths = new ArrayList<>(); // track path from bottom to top
        List<Map<String,String>> backTrackToTop = new ArrayList<>(); //key -> next state and value -> previous state

        String[] words = sentence.toLowerCase().split(" "); // splits sentence into words / observations
        Map<String,Double> currScores = new HashMap<>(); //create new map to store current states and their scores
        Set<String> currStates = new HashSet<>(); // create new sets that stores all current states

        //add the start state to currState and current score of 0.0
        currScores.put("#", 0.0);
        currStates.add("#");


        //iterate each word/observations
        for(int i = 0; i < words.length; i++){

            //create new set to store next states for each current states
            Set<String> nextStates = new HashSet<>();

            // key -> nextStates and value -> score of next state
            Map<String, Double> nextScores = new HashMap<>();

            //create new map. key -> next states and value -> previous state/ current state
            //best previous state
            Map<String, String> backTrack = new HashMap<>();

            //the best score for the last word to back track
            Double highestScore = -1000.0;
            String bestState = "";

            //iterate every state in current states sets
            for(String curr: currStates){
                if(curr.equals(".")) continue;
                //iterate every next state for each current state
                for(String next: transition_probabilities.get(curr).keySet()){
                    //add next state to nextStates sets
                    nextStates.add(next);
                    //check if nextState has observation score for the word else, use unseen
                    if(!observation_probabilities.get(next).containsKey(words[i])){
                        //compute next score
                        Double nextScore = transition_probabilities.get(curr).get(next) + currScores.get(curr) + unseen;

                        //if next score not found in nextScore or nextScore is greater than score for nextState
                        //update next score for next states in nextScore map
                        if(!nextScores.containsKey(next) || nextScores.get(next) < nextScore){
                            //add new score for next state
                            nextScores.put(next,nextScore);

                            //add next State and Current State to back track maps
                            backTrack.put(next,curr);
                        }
                    }
                    else{
                        //else if observation is found in next state, get the observation score
                        //compute nextScore
                        Double nextScore = transition_probabilities.get(curr).get(next)
                                + observation_probabilities.get(next).get(words[i]) + currScores.get(curr);

                        //if score not found in nextScore set or nextScore is greater than what is in
                        if(!nextScores.containsKey(next) || nextScore > nextScores.get(next)){
                            //add new score to nextScores set
                            nextScores.put(next,nextScore);
                            //add new back track nextState and previous state/current state
                            backTrack.put(next,curr);
                        }
                    }
                }
            }

            /**
             * after finding the best score for each next state
             * change current score to next scores
             * This will be used for the next observation
             * */
            currScores = nextScores;
            currStates = nextStates;
            backTrackToTop.add(backTrack);
        }

        //loop through the last set of scores to get the best score
        double lastStateBestScore = -2000;
        String lastStateWithBestScore = "";

        for(String state: currScores.keySet()){
            if(currScores.get(state) > lastStateBestScore){
                lastStateBestScore = currScores.get(state);
                lastStateWithBestScore = state;
            }
        }


        while(!backTrackToTop.isEmpty()){

            Map<String,String> curr = backTrackToTop.remove(backTrackToTop.size() - 1);

            bestStatePaths.add(lastStateWithBestScore);


            lastStateWithBestScore = curr.get(lastStateWithBestScore);
        }

        List<String> tags = new ArrayList<>();
        for(int i = bestStatePaths.size() - 1; i >= 0; i--){
//            System.out.println(words[bestStatePaths.size() - i - 1] + ": " + bestStatePaths.get(i));
            tags.add(bestStatePaths.get(i));
        }
        return tags;
    }


    /**
     * Simplify adding items to nested map
     * @param m map we are modifying
     * @param outer_state the first (outer) key, which is a state
     * @param inner the second (inner) key, which is either the next state or the word
     */
    public void simplified_add(Map<String, Map<String, Double>> m, String outer_state, String inner)
    {
        if (!m.containsKey(outer_state))
        {
            Map<String, Double> map = new HashMap<>();
            map.put(inner, 1.0);
            m.put(outer_state, map);
        }
        else if (!m.get(outer_state).containsKey(inner))
        {
            Map<String, Double> inner_map = m.get(outer_state);
            inner_map.put(inner, 1.0);
            m.put(outer_state, inner_map);
        }
        else {
            Map<String, Double> inner_map = m.get(outer_state);
            inner_map.put(inner, inner_map.get(inner) + 1);
            m.put(outer_state, inner_map);
        }
    }

    public static void main(String[] args) {
        String thing = new File("").getAbsolutePath();
        String relative_path = "/texts/";
        String simple_train_sentences = "simple-train-sentences.txt";
        String simple_train_tags = "simple-train-tags.txt";
        String simple_test_sentences = "simple-test-sentences.txt";
        String simple_test_tags = "simple-test-tags.txt";
        String brown_train_sentences = "brown-train-sentences.txt";
        String brown_train_tags = "brown-train-tags.txt";
        String brown_test_sentences = "brown-test-sentences.txt";
        String brown_test_tags = "brown-test-tags.txt";

        String train_text_file = thing + relative_path + brown_train_sentences;
        String train_tags_file = thing + relative_path + brown_train_tags;

        HMM test = new HMM();
        try {
            test.read_files(train_text_file, train_tags_file);
        }
        catch (Exception e){
            System.out.println("Exception while training: " + e);
        }

        System.out.println("----Test cases------");

        /**
         * test case 1
         * expect: we/PRO should/MOD watch/V the/DET dog/N in/P a/DET cave/N
         * */
        String test1 = "we should watch the dog work in a cave .";
        System.out.println(test1 + ": " + test.viterbi(test1));

        /**
         * test case 2
         * expect: Will/NP eats/V the/DET fish/N
         * */
        String test2 = "Will eats the fish";
        System.out.println(test2 + ": " + test.viterbi(test2));


        /**
         * test case 3
         * expect: Will/MOD you/PRO cook/V the/DET fish/N
         * */
        String test3 = "Will you cook the fish";
        System.out.println(test3 + ": " + test.viterbi(test3));


        /**
         * test case 4
         * */
        String test4 = "The mine has many fish .";
        System.out.println(test4 + ": " + test.viterbi(test4));


        /***
         * test case 5
         * to check sentence that may not work for the model (other languages
         */
        String test5 = "I am about to write a word in espanol .";
        System.out.println(test5 + test.viterbi(test5));

        Scanner user_in = new Scanner(System.in);
        String input_line = "";
        while (input_line != null)
        {
            System.out.print("Enter a sentence: ");
            input_line = user_in.nextLine();
            List<String> labels = test.viterbi(input_line);
            String[] words = input_line.split(" ");
            for (int i = 0; i < labels.size(); i++)
            {
                System.out.print(words[i] + "/" + labels.get(i) + " ");
            }
            System.out.println();
        }
    }

}