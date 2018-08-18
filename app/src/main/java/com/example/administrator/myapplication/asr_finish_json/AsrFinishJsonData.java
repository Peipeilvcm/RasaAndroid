package com.example.administrator.myapplication.asr_finish_json;

/**
 * Created by Administrator on 2018/8/18.
 */

public class AsrFinishJsonData {
    private String error;
    private OriginResult origin_result;
    private String desc;
    private String sub_error;

    public String getError() {
        return error;
    }

    public String getSub_error() {
        return sub_error;
    }

    public String getDesc() {
        return desc;
    }

    public OriginResult getOrigin_result() {
        return origin_result;
    }

}
