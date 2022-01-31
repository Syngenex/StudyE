package com.example.studye;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetAnswerActivity extends AppCompatActivity implements View.OnClickListener {

    private OkHttpClient okHttpClient;
    private Button btn_up,btn_down;
    private TextView tv_question,tv_answer,tv_showNum,tv_item_show;
    private String question,answer;
    private int question_num=0;
    private GridView mGrideView;
    private adapter madapter;

    ArrayList<Map<String,Object>> mData=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        okHttpClient=new OkHttpClient();
        btn_up=findViewById(R.id.answer_up);
        btn_down=findViewById(R.id.answer_down);
        tv_question=findViewById(R.id.main_question);
        tv_answer=findViewById(R.id.main_answer);
        tv_showNum=findViewById(R.id.answer_shouNum);
        tv_item_show=findViewById(R.id.item_shownum);

        btn_up.setOnClickListener(this);
        btn_down.setOnClickListener(this);

        GetQuestionActivity.flag=false;
        grideview_show();
    }

    //按钮事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.answer_up:
                if (question_num==0){
                    Toast.makeText(getApplicationContext(),"已经是第一题了", Toast.LENGTH_SHORT).show();
                }else {
                    tv_question.setText(question);
                    tv_answer.setText("答  案："+answer);
                    tv_showNum.setText("第"+(question_num+1)+"题"+adapter.question_key[question_num]);
                    get_an(adapter.question_key[question_num]);
                    question_num--;
                }
                break;
            case R.id.answer_down:
                if ((question_num+1)==adapter.question_key.length){
                    Toast.makeText(getApplicationContext(),"已经是最后一题了", Toast.LENGTH_SHORT).show();
                }else {
                    tv_question.setText(question);
                    tv_answer.setText("答  案："+answer);
                    tv_showNum.setText("第"+(question_num+1)+"题"+adapter.question_key[question_num]);
                    get_an(adapter.question_key[question_num]);
                    question_num++;
                }
                break;
        }
    }

    //获取答案
    public void get_an(String key){
        new Thread(){
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

                    //处理数据
                    question ="选择题："+str.substring(str.indexOf("\"title\":\"")+9,str.indexOf("\",\"type\""));
                    answer =str.substring(str.indexOf("\"correctAnswer\":\"")+17,str.indexOf("\",\"lines\""));

//                    if(str.indexOf("\"type\":\"1\"")>=0||str.indexOf("\"type\":\"2\"")>=0){
//                        //单选题/多选题
//                        //获取题目
//                        question ="选择题："+str.substring(str.indexOf("\"title\":\"")+9,str.indexOf("\",\"type\""));
//                        //获取答案
//                        answer =str.substring(str.indexOf("\"correctAnswer\":\"")+17,str.indexOf("\",\"lines\""));
//                    }else if (str.indexOf("\"type\":\"5\"")>=0){
//                        //判断
//                        //获取题目
//                        question ="判断题："+str.substring(str.indexOf("\"title\":\"")+9,str.indexOf("\",\"type\""));
//                        //获取答案
//                        answer =str.substring(str.indexOf("\"correctAnswer\":\"")+17,str.indexOf("\",\"lines\""));
//                    }else if (str.indexOf("\"type\":\"6\"")>=0){
//                        //大题
//                        question ="解答题："+str.substring(str.indexOf("\"title\":\"")+9,str.indexOf("\",\"type\""));
//                        answer="略";
//                    }else{
//                        question="未知题型";
//                        answer =str.substring(str.indexOf("\"correctAnswer\":\"")+17,str.indexOf("\",\"lines\""));
//                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("tag","未知错误");
                }
            }
        }.start();

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

                //tv_item_show.setBackgroundResource(R.drawable.button_circle_shape);
                Toast.makeText(getApplicationContext(),"尚未实现，等待后续更新",Toast.LENGTH_SHORT).show();
//                get_an(Integer.toString(position));
//                SystemClock.sleep(2000);
//                tv_question.setText(question);
//                tv_answer.setText("答  案："+answer);
//                tv_showNum.setText("第"+(question_num+1)+"题"+adapter.question_key[question_num]);
//                get_an(adapter.question_key[question_num]);
            }
        });

    }


}
