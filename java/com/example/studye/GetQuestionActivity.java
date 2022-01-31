package com.example.studye;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private OkHttpClient okHttpClient;
    private Button btn_get;
    private EditText tv_testKey;
    private TextView helloworld;
    public static Boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        adapter.flag=false;//关闭登录验证

        okHttpClient=new OkHttpClient();
        btn_get=findViewById(R.id.question_getKey);
        tv_testKey=findViewById(R.id.question_testKey);
        helloworld=findViewById(R.id.question_helloWord);
        helloWord();
        btn_get.setOnClickListener(this);
    }


    //按钮事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.question_getKey:
                if (tv_testKey.getText().toString().trim()==""){
                    Toast.makeText(getApplicationContext(),"请输入testKey",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"解析中...",Toast.LENGTH_SHORT).show();
                    get_qu(tv_testKey.getText().toString().trim());
                    SystemClock.sleep(2000);
                    if (flag==true){
                        Toast.makeText(getApplicationContext(),"解析成功",Toast.LENGTH_SHORT).show();
                        Intent intentMain =new Intent(getApplicationContext(), GetAnswerActivity.class);
                        startActivity(intentMain);
                    }else {
                        Toast.makeText(getApplicationContext(),"解析失败，请检查testKey",Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    //获取题目
    public void get_qu(String key){
        new Thread(){
            @Override
            public void run() {
                FormBody formBody =new FormBody.Builder()
                        .add("testKey",key).build();
                Request request =new Request.Builder()
                        .url("http://103.239.153.192:8081/suite/appAssessment/getAssessment.do?withAnswers=true")
                        .post(formBody)
                        .addHeader("Cookie",adapter.cookie.get(0))
                        .build();
                Call call=okHttpClient.newCall(request);
                try {
                    Response response=call.execute();
                    String str =response.body().string();
                    //处理数据
                    if (str.indexOf("\"success\":true")>=0){
                        flag=true;
                        String question=str.substring(str.indexOf("\"questionKeys\":[{\"key\":\"")+24,str.indexOf("\"}]},\"msg\":\"\",\"success\":true}"))
                                .replace("\"},{\"key\":","");
                        Log.e("key",question);
                        adapter.question_key =question.split("\"");
                    }else {
                        flag=false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("tag","未知错误");
                }
            }
        }.start();

    }

    //欢迎登陆
    public void helloWord(){
        new Thread(){
            @Override
            public void run() {
                helloworld.setText(adapter.name+",欢迎回来");
            }
        }.start();
    }

}
