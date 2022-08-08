package com.example.studye;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetAnswerActivity extends AppCompatActivity implements View.OnClickListener {

    private OkHttpClient okHttpClient;
    private Button btn_up,btn_down,btn_print;
    private TextView tv_question,tv_answer,tv_showNum,tv_item_show;
    private ArrayList<String> questionadapter=new ArrayList<>();
    private ArrayList<String> answeradapter=new ArrayList<>();
    private int question_num=0;
    private GridView mGrideView;
    private adapter madapter;

    ArrayList<Map<String,Object>> mData=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        init();
        System.out.println(adapter.question_key.length);
        grideview_show();

        //adapter.vip_flag=true;
        if (adapter.vip_flag==false){
            get_10_answer();
        }else {
            get_all_answer();
        }
        SystemClock.sleep(1000);
        shou_answer(0);
    }

    //初始化
    private void init() {
        okHttpClient=new OkHttpClient();
        btn_up=findViewById(R.id.answer_up);
        btn_down=findViewById(R.id.answer_down);
        tv_question=findViewById(R.id.answer_question);
        tv_answer=findViewById(R.id.answer_answer);
        tv_showNum=findViewById(R.id.answer_shouNum);
        tv_item_show=findViewById(R.id.item_shownum);
        btn_print=findViewById(R.id.answer_print);

        btn_up.setOnClickListener(this);
        btn_down.setOnClickListener(this);
        btn_print.setOnClickListener(this);
        tv_question.setMovementMethod(ScrollingMovementMethod.getInstance());

        GetQuestionActivity.flag=false;

    }

    //按钮事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.answer_print:
                String str_print="";
                if (questionadapter.size()<adapter.question_key.length){
                    Toast.makeText(getApplicationContext(),"获取题目中，稍后重试", Toast.LENGTH_SHORT).show();
                }else {
                    //Toast.makeText(getApplicationContext(),"加载成功", Toast.LENGTH_SHORT).show();
                    for (int i=0;i<adapter.question_key.length;i++){
                        Spanned spanned = null;
                        spanned = Html.fromHtml(questionadapter.get(i), Html.FROM_HTML_MODE_COMPACT);//解析HTML格式文件

                        str_print=str_print+"\n"+Integer.toString(i+1)+"."+spanned+"\n答案："+answeradapter.get(i)+"\n";
                    }

                    try {
                        //获取剪贴板管理器
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", str_print);
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(getApplicationContext(),"已将全部题目复制到粘贴板", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"失败:"+e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.answer_up:
                if (question_num==0){
                    Toast.makeText(getApplicationContext(),"已经是第一题了", Toast.LENGTH_SHORT).show();
                }else {
                    question_num--;
                    shou_answer(question_num);
                }
                break;
            case R.id.answer_down:
                if (adapter.vip_flag==false){
                    if ((question_num+1)==10){
                        go_get_vip2();
                        //Toast.makeText(getApplicationContext(),"普通用户仅可查看前10题", Toast.LENGTH_SHORT).show();
                    }else {
                        question_num++;
                        shou_answer(question_num);
                    }
                }else {
                    if ((question_num+1)==adapter.question_key.length){
                        Toast.makeText(getApplicationContext(),"已经是最后一题了", Toast.LENGTH_SHORT).show();
                    }else {
                        question_num++;
                        shou_answer(question_num);
                    }
                }
                break;
        }
    }

    //遍历所有答案存入数组
    private void get_all_answer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<=adapter.question_key.length-1;i++){
                    get_an(adapter.question_key[i]);
                }
            }
        }).start();
    }

    //遍历前十答案存入数组
    private void get_10_answer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<=9;i++){
                    get_an(adapter.question_key[i]);
                }
            }
        }).start();
    }

    //获取答案
    public void get_an(String key){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody formBody =new FormBody.Builder()
                        .add("key",key).build();
                Request request =new Request.Builder()
                        .url("http://103.239.153.192:8081/suite/appTestPaper/getQuestion.do")
                        .post(formBody)
                        .addHeader("Cookie",adapter.cookie.get(0))
                        .build();
                Call call=okHttpClient.newCall(request);
                try {
                    Response response=call.execute();
                    String str =response.body().string();
                    String question,answer;

                    //Log.e("reponse",str);

                    //处理数据
                    if(str.indexOf("\"type\":\"1\"")>=0||str.indexOf("\"type\":\"2\"")>=0){
                        //单选题/多选题
                        //获取题目
                        question ="选择题："+str.substring(str.indexOf("\"description\":\"")+15,str.indexOf("\",\"answerInfos\""));
                        //获取答案
                        answer =str.substring(str.indexOf("\"correctAnswer\":\"")+17,str.indexOf("\",\"lines\""));
                    }else if (str.indexOf("\"type\":\"5\"")>=0){
                        //判断
                        //获取题目
                        question ="判断题："+str.substring(str.indexOf("\"description\":\"")+15,str.indexOf("\",\"answerInfos\""));
                        //获取答案
                        answer =str.substring(str.indexOf("\"correctAnswer\":\"")+17,str.indexOf("\",\"lines\""));
                    }else if (str.indexOf("\"type\":\"6\"")>=0){
                        //大题
                        question ="解答题："+str.substring(str.indexOf("\"description\":\"")+15,str.indexOf("\"},\"msg\":"));
                        answer="解答题目前无法帮你完成哦";
                    }else{
                        question="填空题:"+str.substring(str.indexOf("\"description\":\"")+15,str.indexOf("\",\"answerInfos\""));
                        answer =str.substring(str.indexOf("\"correctAnswer\":\"")+17,str.indexOf("\",\"lines\""));
                    }
                    //Log.e("answer",answer);

                    questionadapter.add(question);
                    answeradapter.add(answer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //显示答案
    private void shou_answer(int i){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Spanned spanned = null;
                        spanned = Html.fromHtml(questionadapter.get(i), Html.FROM_HTML_MODE_COMPACT);//解析HTML格式文件
                        tv_showNum.setText((question_num+1)+"/"+adapter.question_key.length+"题：");//显示题号以及题目
                        tv_question.setText(spanned);
                        tv_answer.setText("答案："+answeradapter.get(i));
                    }
                });
            }
        }).start();
    }

    //gridview
    private void grideview_show() {
        mGrideView=findViewById(R.id.answer_Gridview);

        for (int i=0;i<adapter.question_key.length;i++){
            Map<String,Object> map =new HashMap<>();
            map.put("txt",i+1);
            mData.add(map);
        }
        madapter=new adapter(getApplicationContext(),mData);
        mGrideView.setAdapter(madapter);

        mGrideView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (adapter.vip_flag==false){
                    go_get_vip();
                }else {
                    question_num=position;
                    shou_answer(question_num);
                }
            }
        });

    }

    //获取VIP对话框
    public void go_get_vip() {
        //实例化AlertDialog使用其子类来实现
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置对话框的图标
        //builder.setIcon(R.drawable.ic_launcher_background);
        //设置标题
        builder.setTitle("❤您还未解锁高级版");
        //设置信息
        builder.setMessage("普通用户无法进行题目跳转，你可以选择升级为高级版");
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
    public void go_get_vip2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("❤您还未解锁高级版");
        builder.setMessage("普通用户仅可查看前10道题，你可以选择升级为高级版");

        builder.setPositiveButton("带我去看看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent =new Intent(getApplicationContext(),get_vipActivity.class);
                startActivity(intent);
            }
        });

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

}
