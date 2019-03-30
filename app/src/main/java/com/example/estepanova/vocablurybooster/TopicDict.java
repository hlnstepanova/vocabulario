package com.example.estepanova.vocablurybooster;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TopicDict extends Dictionary {

    private HashMap<String, String> topicMap;

    public TopicDict (String dict_source, HashMap<String, String> wordsMap,
                      List<String> unlearned, List<String> to_learn, HashMap<String, Integer> inProcessMap,
                      List<String> learned, String topic, HashMap<String, String> topicMap, int progress){

        super(dict_source, wordsMap, unlearned, to_learn, inProcessMap, learned, topic, progress);
        this.topicMap = topicMap;
    }

    public void importTopics(String selected) {
        //first import the correspondent dictionary
        String filePath = this.dict_source;

        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ", 3);
                if (parts.length >= 3) {
                    String key = parts[0];
                    String topic = parts[1];
                    //create a hashmap with topic only with words from selected topic
                    if (topic.equals(selected)){
                        this.topicMap.put(key, topic);
                    }
                } else {
                    //ignoring line
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        unlearned = new ArrayList<String>(wordsMap.keySet());
    }

}
