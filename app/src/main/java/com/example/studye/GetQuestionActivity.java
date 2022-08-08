package com.example.studye;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private OkHttpClient okHttpClient;
    private Button btn_get;
    private EditText tv_testKey;
    private TextView helloworld,tv_use,tv_outlogin;
    public static Boolean flag=false;
    private SharedPreferences sharedPreferences;

    //初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        adapter.question_key=null;

        sharedPreferences=getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        okHttpClient=new OkHttpClient();
        tv_use=findViewById(R.id.question_about);
        tv_outlogin=findViewById(R.id.question_outlogin);
        btn_get=findViewById(R.id.question_getKey);
        tv_testKey=findViewById(R.id.question_testKey);
        helloworld=findViewById(R.id.question_helloWord);
        tv_use.setMovementMethod(LinkMovementMethod.getInstance());
        Log.e("tag",adapter.vip_flag.toString());

        tv_use.setOnClickListener(this);
        btn_get.setOnClickListener(this);
        tv_outlogin.setOnClickListener(this);

        new Thread(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(2000);
                        if (adapter.youngjiu_vip_flag==true){
                            youngjiu_vip_helloWord();
                        }else if (adapter.vip_flag==true){
                            vip_helloWord();
                        }else{
                            helloWord();
                            go_get_vip();
                        }
                    }
                });
            }
        }.start();

    }


    //按钮事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.question_getKey:
                if (tv_testKey.getText().toString().trim()==""){
                    Toast.makeText(getApplicationContext(),"请输入testKey",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"解析中,请勿操作",Toast.LENGTH_SHORT).show();
                    get_qu(tv_testKey.getText().toString().trim());
                    SystemClock.sleep(3000);
                    if (flag==true){
                        Toast.makeText(getApplicationContext(),"解析成功",Toast.LENGTH_SHORT).show();
                        Intent intentMain =new Intent(getApplicationContext(),GetAnswerActivity.class);
                        startActivity(intentMain);
                    }else {
                        Toast.makeText(getApplicationContext(),"解析失败，请检查testKey",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.question_outlogin:
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent intentMain =new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intentMain);
                break;
            case R.id.question_about:
                Intent intentMain1 =new Intent(getApplicationContext(), about_activity.class);
                startActivity(intentMain1);
                break;
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

                        String question="";
                        Pattern p = Pattern.compile("(questionKey\":\").*?(\",\"smallQuestionKey\":)");
                        Matcher m = p.matcher(str);
                        while (m.find()){
                            question=question+m.group();
                        }
                        String question2=question.replace("questionKey\":\"","");
                        Log.e("all_question",question2);
                        adapter.question_key =question2.split("\",\"smallQuestionKey\":");

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
    //普通用户
    public void helloWord(){
        new Thread(){
            @Override
            public void run() {
                helloworld.setText(adapter.name+",欢迎回来");
            }
        }.start();
    }
    //体验用户
    public void vip_helloWord(){
        new Thread(){
            @Override
            public void run() {
                helloworld.setText("体验用户："+adapter.name+",欢迎回来\n截止时间："+adapter.vip_time);
            }
        }.start();
    }
    //永久会员
    public void youngjiu_vip_helloWord(){
        new Thread(){
            @Override
            public void run() {
                helloworld.setText("永久用户："+adapter.name+",欢迎回来");
            }
        }.start();
    }

    //获取VIP对话框
    public void go_get_vip() {
        //实例化AlertDialog使用其子类来实现
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置对话框的图标
        //builder.setIcon(R.drawable.ic_launcher_background);
        //设置标题
        builder.setTitle("您还未解锁高级版");
        //设置信息
        builder.setMessage("普通用户仅可使用基础功能，你可以选择升级为高级版");
        //积极的按钮——确定/ok/finish
        //@param 按钮显示的文本
        //@param 单击事件
        builder.setPositiveButton("带我去看看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent =new Intent(getApplicationContext(),get_vipActivity.class);
                startActivity(intent);
            }
        });

        //消极的按钮——取消
//        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getApplicationContext(),"单击cancel",Toast.LENGTH_LONG).show();
//            }
//        });
        //中立的按钮——忽略/跳过
        builder.setNeutralButton("我再想一想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(),"作者难过的留下了一滴眼泪",Toast.LENGTH_SHORT).show();
            }
        });
        //对话框的显示
        builder.show();
    }

    //软件更新提示对话框
    public void updata_msg(String title, String msg) {
        //实例化AlertDialog使用其子类来实现
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置标题
        builder.setTitle(title);
        //设置信息
        builder.setMessage(msg);
        //积极的按钮——确定/ok/finish
        //@param 按钮显示的文本
        //@param 单击事件
        builder.setPositiveButton("带我去下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Uri uri=Uri.parse("https://wwb.lanzouw.com/b030orisb");
                Toast.makeText(getApplicationContext(),"密码：abcd",Toast.LENGTH_LONG).show();
                Intent intent =new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        //对话框的显示
        builder.show();
    }

}
