package top.lyfzn.music.douyinquick;


import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Douyin {
    private String user_name,video_id;
    private String real_url;
    private String music_url;
    private DYtask dYtask;
    private int user_count=0;

    public int getUser_count() {
        return user_count;
    }

    public String getMusic_url() {
        return music_url;
    }

    public String getUserName() {
        return userName;
    }

    public String getShort_id() {
        return short_id;
    }

    private String userName,short_id;

    public boolean isHas_long() {
        return has_long;
    }

    private boolean has_long=false;

    public String getQuantity_name() {
        return quantity_name;
    }

    private String quantity_name="";

    public String getLong_video() {
        return long_video;
    }

    private void setLong_video(String long_video,String quantity_name) {
        this.long_video = long_video;
        if(!this.long_video.equals("")){
            this.has_long=true;
            this.quantity_name=quantity_name;
        }
    }

    private String long_video;
    private DYCallBack callBack;

    public Douyin(String share_url, DYCallBack callBack){
        this.callBack=callBack;
        DYtask task=new DYtask();
        dYtask=task;
        task.execute(share_url);

    }
    public void cancle(){
        if(dYtask!=null){
            dYtask.cancel(true);
        }
    }
    public String getUser_name() {
        return user_name;
    }

    public String getVideo_id() {
        return video_id;
    }

    public String getReal_url() {
        return real_url;
    }


    class DYtask extends AsyncTask<String,Void,String>{
    private String[] quantities={"normal_720","normal_540","normal_360"};
    private String[] quantities_name={"L720","L540","L360"};
        @Override
        protected String doInBackground(String... strings) {
            return new HttpUtil().HttpRequest(strings[0]);
        }

        @Override
        @Override
protected void onPostExecute(String s) {
    try{
        JSONObject object= JSON.parseObject(s);
        // 尝试解析不同API返回格式
        String src_no="";
        String murl="";
        String user="";
        String id="";
        
        // 格式1: 直接返回数据
        if(object.containsKey("urls")){
            src_no=object.getJSONArray("urls").getString(0);
        }
        if(object.containsKey("music_urls")){
            murl=object.getJSONArray("music_urls").getString(0);
        }
        if(object.containsKey("nickname")){
            user=object.getString("nickname");
        }
        if(object.containsKey("awemeId")){
            id=object.getString("awemeId");
        }
        
        // 格式2: 数据在data字段中
        if(object.containsKey("data")){
            JSONObject data=object.getJSONObject("data");
            if(data.containsKey("urls")){
                src_no=data.getJSONArray("urls").getString(0);
            }
            if(data.containsKey("music_urls")){
                murl=data.getJSONArray("music_urls").getString(0);
            }
            if(data.containsKey("nickname")){
                user=data.getString("nickname");
            }
            if(data.containsKey("awemeId")){
                id=data.getString("awemeId");
            }
        }
        
        // 格式3: 简化格式
        if(object.containsKey("video_url")){
            src_no=object.getString("video_url");
        }
        if(object.containsKey("music_url")){
            murl=object.getString("music_url");
        }
        if(object.containsKey("author")){
            user=object.getString("author");
        }
        if(object.containsKey("video_id")){
            id=object.getString("video_id");
        }
        
        // 格式4: 其他常见格式
        if(object.containsKey("play_addr")){
            JSONObject playAddr=object.getJSONObject("play_addr");
            if(playAddr.containsKey("url_list")){
                src_no=playAddr.getJSONArray("url_list").getString(0);
            }
        }
        
        // 确保必要字段存在
        if(src_no.isEmpty() || user.isEmpty()){
            throw new Exception("必要字段缺失");
        }
        
        // 尝试解析长视频（如果有）
        if(object.containsKey("long_video")){
            JSONArray arr_l=object.getJSONArray("long_video");
            for (int i=0;i<arr_l.size();i++){
                JSONObject lv=arr_l.getJSONObject(i);
                for(int j=0;j<3;j++){
                    if(lv.containsKey("gear_name") && lv.getString("gear_name").equals(quantities[j])){
                        if(lv.containsKey("play_addr")){
                            JSONObject playAddr=lv.getJSONObject("play_addr");
                            if(playAddr.containsKey("url_list")){
                                setLong_video(playAddr.getJSONArray("url_list").getString(0),quantities_name[j]);
                                break;
                            }
                        }
                    }
                }
                if(has_long){
                    break;
                }
            }
        }

        user_name=user;
        video_id=id;
        real_url=src_no;
        music_url=murl;
    }catch (Exception e){
        callBack.HttpSuccessDo(Douyin.this,true);
        return;
    }
    callBack.HttpSuccessDo(Douyin.this,false);
}



    public interface DYCallBack {
        void HttpSuccessDo(Douyin douyin, boolean error);//获取抖音页面内容后做的事
    }
}

