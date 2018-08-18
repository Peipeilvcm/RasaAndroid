package com.example.administrator.myapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.example.administrator.myapplication.asr_finish_json.AsrFinishJsonData;
import com.example.administrator.myapplication.asr_partial_json.AsrPartialJsonData;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/18.
 */

public class VoiceRecognizer implements EventListener{

    private static final String Tag = "VoiceReconginzer";
    private EventManager asr;
    //private boolean logTime = true;
    protected boolean enableOffline = true;
    private String final_result;

    private Handler handler;

    public VoiceRecognizer(Context context,Handler handler){
        this.handler = handler;
        asr = EventManagerFactory.create(context,"asr");
        asr.registerListener(this);

        if(enableOffline){
            loadOfflineEngine();// 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }

    }

    public void start(){
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = null;
        event = SpeechConstant.ASR_START;

        params.put(SpeechConstant.PID, 1536); // 默认1536
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);// 语音活动检测
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 2000); // 不开启长语音。开启VAD尾点检测，即静音判断的毫秒数。建议设置800ms-3000ms
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);// 是否需要语音音量数据回调
        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, false);// 是否需要语音音频数据回调

        String json = null; // 可以替换成自己的json
        json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        //(new AutoCheck())
    }

    private void stop(){
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }


    @Override       //   EventListener  回调方法
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String resultTemp = "name" + name;

        if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)){
            //引擎准备就绪
        }
        else if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)){
            //用户已经开始说话
        }
        else if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)){
            //用户停止说话
        }
        else if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)){
            //部分结果,可能还没说完
            if(params.contains("\"nlu_result\"")){
                if(length > 0 && data.length > 0){
                    resultTemp += ",语义解析结果" + new String(data, offset, length);
                }
            }
            parseAsrPartialJsonData(params);
        }
        else if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)){
            //识别结束
            parseAsrFinishJsonData(params);
            stop();
            //让按钮enable=true
        }
        else if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)){
            //识别引擎结束,空闲
        }
        resultTemp += ";params:" + params;
        Log.d(Tag, "onEvent: " + resultTemp);
    }

    //加载离线引擎
    private void loadOfflineEngine(){
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(SpeechConstant.DECODER, 2);
        params.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets://baidu_speech_grammar.bsg");
        asr.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, new JSONObject(params).toString(), null, 0, 0);
    }
    //解绑离线引擎
    private void unloadOfflineEngine() {
        asr.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0);
    }

    //处理中间json格式结果
    private void parseAsrPartialJsonData(String params){
        Gson gson = new Gson();
        AsrPartialJsonData jsonData = gson.fromJson(params, AsrPartialJsonData.class);
        String resultType = jsonData.getResult_type();
        if(resultType != null && resultType.equals("final_result")){
            final_result = jsonData.getBest_result();
        }
    }
    //处理结束json格式结果
    private void parseAsrFinishJsonData(String params){
        Gson gson = new Gson();
        AsrFinishJsonData jsonData = gson.fromJson(params, AsrFinishJsonData.class);
        String desc = jsonData.getDesc();
        if(desc != null && desc.equals("Speech Recognize success.")){
            //识别成功,更新UI
            printToUI(final_result);
        }else {
            String errCode = ",错误码: " + jsonData.getError();
            printToUI("ERROR!"+ desc +errCode);
        }
    }

    //利用handler,以显示在主界面
    private void printToUI(String text){
        Message msg = handler.obtainMessage();
        msg.what = UdpClientActivity.Voice_to_String;
        msg.obj = text;
        handler.sendMessage(msg);
    }

    public void destory(){
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        if (enableOffline) {
            unloadOfflineEngine(); // 测试离线命令词请开启, 测试 ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH 参数时开启
        }
        // 必须与registerListener成对出现，否则可能造成内存泄露
        asr.unregisterListener(this);
    }
}
