package com.example.smartreader;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SearchingWord {

    String[] strArr;
    int count = 0;
    List<Integer> position = new ArrayList<>();

    public int afterSearch(String totalText, String searchedWord) {

        totalText = totalText.toLowerCase().trim();
        searchedWord = searchedWord.toLowerCase().trim();

        strArr = totalText.split(" ");

        for (int i = 0; i < strArr.length; i++) {
            Log.i("info", strArr[i]);
        }

        int i;
        for (i = 0; i < strArr.length; i++) {
            if (strArr[i].equals(searchedWord)) {
                count += 1;
                position.add(i);
            }
        }
        return count;
    }

    public void setItNull() {
        count = 0;
        position.clear();
    }


    public String getSelectedString(int currentPos) {

        String result = "";

        for (int i = position.get(currentPos - 1); i < strArr.length; i++) {
            result += strArr[i] + " ";
        }
        return result;
    }
}
