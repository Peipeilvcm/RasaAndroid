package com.example.administrator.myapplication.asr_partial_json;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/18.
 */

public class Result {
    private ArrayList<String> uncertain_word;
    private ArrayList<String> word;

    public ArrayList<String> getUncertain_word() {
        return uncertain_word;
    }

    public ArrayList<String> getWord() {
        return word;
    }
}
