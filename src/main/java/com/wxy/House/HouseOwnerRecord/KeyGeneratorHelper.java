package com.wxy.House.HouseOwnerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cooper on 20/11/2016.
 */
public class KeyGeneratorHelper {

    private List<String> keys;

    public KeyGeneratorHelper() {
        keys = new ArrayList<String>();
    }

    public KeyGeneratorHelper(String key) {
        this.keys = new ArrayList<String>();
        if (key != null) {

            Pattern pattern = Pattern.compile("\\[([\\s\\S]+)\\]");
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
                keys.add(matcher.group(1));
            }
        }
    }

    private String genSearchWord(String word){
        if (word != null) {
            return "[" + word + "]";
        }else {
            return "";
        }
    }

    public boolean addWord(String word){
        if (!keys.contains(word)){
            keys.add(word);
            return true;
        }
        return false;
    }

    public void addWords(KeyGeneratorHelper keyGeneratorHelper){
        for(String key : keyGeneratorHelper.getKeys()){
            addWord(key);
        }
    }

    public List<String> getKeys() {
        return keys;
    }

    public String getKey() {
        String result = "";
        for(String key: keys){
            result += genSearchWord(key);
        }
        return result;
    }
}
