package com.example.administrator.myapplication.asr_partial_json;

/**
 * Created by Administrator on 2018/8/18.
 */

public class OriginResult {
    private String corpus_no;
    private String err_no;
    private String sn;
    private Result result;

    public Result getResult() {
        return result;
    }

    public String getCorpus_no() {
        return corpus_no;
    }

    public String getErr_no() {
        return err_no;
    }

    public String getSn() {
        return sn;
    }
}
