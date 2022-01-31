package com.example.studye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private OkHttpClient okHttpClient;
    private EditText num,psd;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        okHttpClient =new OkHttpClient();
        num=findViewById(R.id.login_student_num);
        psd=findViewById(R.id.login_student_psd);
        btn=findViewById(R.id.login_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number=num.getText().toString();
                String password=psd.getText().toString();
                post_login(number,password);
                Toast.makeText(getApplicationContext(),"Loading...",Toast.LENGTH_SHORT).show();
                SystemClock.sleep(2000);
                if(adapter.name!=null){
                    Intent intentMain =new Intent(getApplicationContext(), GetQuestionActivity.class);
                    startActivity(intentMain);
                    adapter.sendStudentNum(number,adapter.name,password);
                    Toast.makeText(getApplicationContext(),adapter.name+"登录成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"密码错误，也有可能是网络延时，等待一会再试",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //登录
    public void post_login(String num,String psd){
        new Thread(){
            @Override
            public void run() {
                FormBody formBody =new FormBody.Builder()
                        .add("loginName",num)
                        .add("password",psd).build();
                Request request =new Request.Builder()
                        .url("http://103.239.153.192:8081/suite/appLogin/login.do")
                        .post(formBody)
                        //.addHeader("Cookie",cookie)
                        .build();
                Call call=okHttpClient.newCall(request);

                try {
                    Response response=call.execute();
                    String str =response.body().string();
                    //判断是否登陆成功
                    if (str.indexOf("true")>=0){//登录成功
                        Log.e("login",str);
                        //获取用户名
                        adapter.name =str.substring(str.indexOf("userName\":\"")+11,str.indexOf("\",\"userKey\""));
                        //adapter.flag=true;
                    }else{//登录失败
                        //adapter.flag=false;
                    }
                    //获取cookie
                    if (response.isSuccessful()){
                        Headers headers=response.headers();
                        adapter.cookie=headers.values("Set-Cookie");
                        Log.e("cookie",adapter.cookie.get(0));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("tag","未知错误");
                }
            }
        }.start();
    }


}