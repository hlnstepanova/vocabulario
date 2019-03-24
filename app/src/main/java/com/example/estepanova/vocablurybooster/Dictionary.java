package com.example.estepanova.vocablurybooster;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static java.lang.String.valueOf;

public class Dictionary implements Serializable{

    protected HashMap<String, String> wordsMap = new HashMap<String, String>();
    protected HashMap<String, Integer> inProcessMap = new HashMap<String, Integer>();
    protected List<String> learned = new ArrayList<String>();
    protected List<String> unlearned = new ArrayList<String>();
    protected List<String> to_learn = new ArrayList<String>();
    protected String dict_source;
    protected String topic;
    protected int progress;

    public String encoding = "UTF-8";

    Random random = new Random();

    public Dictionary(String dict_source, HashMap<String, String> wordsMap,
                      List<String> unlearned, List<String> to_learn, HashMap<String, Integer> inProcessMap,
                      List<String> learned, String topic, int progress) {

        this.dict_source = dict_source;
        this.wordsMap = wordsMap;
        this.unlearned = unlearned;
        this.to_learn = to_learn;
        this.inProcessMap = inProcessMap;
        this.learned = learned;
        this.topic = topic;
        this.progress=progress;

    }


    public String showNewWord(){
        if (unlearned.size() > 0) {
            int wordPos = random.nextInt(unlearned.size()); //get random number(position)
            String randomKey = unlearned.get(wordPos); // get one unlearned word at that position
            this.unlearned.remove(wordPos); // remove the word from unlearned
            this.to_learn.add(randomKey); // add the word to "to learn"
            return randomKey;
        } else {
            return "no_unlearned_words";
        }
    }

    public String showTranslation(String new_word){

            String translation = wordsMap.get(new_word);
            this.inProcessMap.put(new_word, 0);
            return translation;

    }

    public String testWord(){

        if (to_learn.size()==0){
            int wordPos = random.nextInt(learned.size());
            return learned.get(wordPos);
        } else {
            int wordPos = random.nextInt(to_learn.size());
            return to_learn.get(wordPos);
        }

    }

    public String reverseWord(String word){
            //get a new word and show it's translation in a different language
            return wordsMap.get(word);
    }

    public void correctAnswer(String word){
        Integer new_count = this.inProcessMap.get(word) + 1;
        if (new_count > 4) {
            this.inProcessMap.remove(word);
            this.to_learn.remove(word);
            this.learned.add(word);

        }else{
            this.inProcessMap.put(word, new_count);
        }

        Log.i("new count", Integer.toString(new_count));
        Log.i("inProcessMap:", inProcessMap.toString());
        Log.i("To learn:", to_learn.toString());
        Log.i("Learned:", learned.toString());
    }

    public boolean checkEmpty(){
        if (to_learn.size()==0 && unlearned.size()==0) {
            return true;
        }
        return false;
    }

    public double calculateProgress(){
        double result = (double) learned.size()/(wordsMap.size()) * 100.0;
        Log.i("Progress:", (valueOf(result)));
        Log.i("Learned size:", (valueOf(learned.size())));
        Log.i("Total size:", valueOf((wordsMap.size())));
        return result;

    }

    public HashMap getWordsMap(){
        return wordsMap;
    }

    public String getSource(){
        return dict_source;
    }

    public String getTopic(){
        return topic;
    }

    public void setTopic(String topic){
        this.topic = topic;
    }

    public int getProgress(){
        return progress;
    }

    public void setProgress(int progress){
        this.progress = progress;
    }

    public int getUnlearnedLength(){
        return this.unlearned.size();
    }

    public int getToLearnLength(){
        return this.to_learn.size();
    }

    public List getLearned(){
        return this.learned;
    }

    public List getToLearn(){
        return this.to_learn;
    }

    public void setUnlearned(List unlearned) {
        this.unlearned = unlearned;
    }

    public List setLearned(List<String> learned){
        return this.learned = learned;
    }

    public void setToLearn(List to_learn){
        this.to_learn = to_learn;
    }
}
