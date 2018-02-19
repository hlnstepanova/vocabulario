package com.example.estepanova.vocablurybooster;

import android.content.Context;
import android.util.Log;
import android.widget.TableLayout;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Dictionary implements Serializable{

    protected HashMap<String, String> wordsMap = new HashMap<String, String>();
    protected HashMap<String, Integer> inProcessMap = new HashMap<String, Integer>();
    protected List<String> learned = new ArrayList<String>();
    protected List<String> unlearned = new ArrayList<String>(wordsMap.keySet());
    protected List<String> to_learn = new ArrayList<String>();
    protected String dict_source;

    Random random = new Random();

    public Dictionary(String dict_source, HashMap<String, String> wordsMap,
                      List<String> unlearned, List<String> to_learn, HashMap<String, Integer> inProcess,
                      List<String> learned) {

        this.dict_source = dict_source;
        this.wordsMap = wordsMap;
        this.unlearned = unlearned;
        this.to_learn = to_learn;
        this.inProcessMap = inProcess;
        this.learned = learned;

    }


    public void importFile() {

        //first import the correspondent dictionary
        String filePath = dict_source;
        Log.i("import from", filePath);

        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ", 2);
                if (parts.length >= 3) {
                    String key = parts[0];
                    String value = parts[2];
                    this.wordsMap.put(key, value);
                } else {
                    Log.i("Import:", "ignoring line: " + line);
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


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

        int wordPos = random.nextInt(to_learn.size());
        return to_learn.get(wordPos);

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
    }


    public HashMap getWordsMap(){
        return wordsMap;
    }

    public List getUnlearned(){
        return this.unlearned;
    }

    public List getToLearn(){
        return this.to_learn;
    }

    public void setUnlearned(List unlearned) {
        this.unlearned = unlearned;
    }

    public void setToLearn(List to_learn){
        this.to_learn = to_learn;
    }
}
