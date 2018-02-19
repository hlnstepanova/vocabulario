package com.example.estepanova.vocablurybooster;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class TopicDict extends Dictionary {

    private HashMap<String, String> topicMap;

    public TopicDict (String dict_source, HashMap<String, String> wordsMap,
                      List<String> unlearned, List<String> to_learn, HashMap<String, Integer> inProcess,
                      List<String> learned, HashMap<String, String> topicMap){

        super(dict_source, wordsMap, unlearned, to_learn, inProcess, learned);
        this.topicMap = topicMap;
    }

    public void importTopics(String selected) {
        //first import the correspondent dictionary
        String filePath = this.dict_source;
        Log.i("import from", filePath);

        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ", 2);
                if (parts.length >= 3) {
                    String key = parts[0];
                    String topic = parts[1];
                    if (topic.equals(selected)){
                        this.topicMap.put(key, topic);
                    }
                } else {
                    Log.i("Import:", "ignoring line: " + line);
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importTopicFile(String selected) {

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
                    String topic = parts[1];
                    String value = parts[2];
                    if (topic.equals(selected)) {
                        this.wordsMap.put(key, value);
                    }
                } else {
                    Log.i("Import:", "ignoring line: " + line);
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
