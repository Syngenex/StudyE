package com.example.studye;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    private TextView tv_allow,tv_ann;
    private CheckBox ck_user,ck_autologin;
    private Boolean all_get_over=true,rember_flag = false;
    private String isay,ann;
    //private String all_vip_num;
    private SharedPreferences sharedPreferences;
    private String number,password;
    Connection conn;     //一个成员变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        okHttpClient =new OkHttpClient();
        num=findViewById(R.id.login_student_num);
        psd=findViewById(R.id.login_student_psd);
        btn=findViewById(R.id.login_login);
        tv_allow=findViewById(R.id.login_allow_usertxt);
        ck_user=findViewById(R.id.login_allow_user);
        ck_autologin=findViewById(R.id.login_autologin);


        //get_vip_num();

        //自动登录的实现
        sharedPreferences=getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("autologin_flag",false)){
            number=sharedPreferences.getString("num","");
            adapter.num=number;
            password=sharedPreferences.getString("psd","");
            while(true){
                if (all_get_over==true){
                    //are_you_vip(number);
                    post_login(number,password);
                    Toast.makeText(getApplicationContext(),"登录中...",Toast.LENGTH_SHORT).show();
                    SystemClock.sleep(2000);
                    //艺爪付费
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                EzrevenueClient.ezrevenueClient(number,adapter.name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    Intent intentMain =new Intent(getApplicationContext(), GetQuestionActivity.class);
                    startActivity(intentMain);
                    adapter.sendStudentNum(number,adapter.name,password);
                    Toast.makeText(getApplicationContext(),adapter.name+"登录成功",Toast.LENGTH_SHORT).show();
                    finish();
                    break;}
            }
        }else{
            //对话框的子线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"初始化，请稍后",Toast.LENGTH_SHORT).show();
                            while(true){
                                if (all_get_over==true){
                                    Toast.makeText(getApplicationContext(),"初始化成功",Toast.LENGTH_SHORT).show();
                                    break;}
                            }
                            //adapter.updata_version =all_vip_num.substring(all_vip_num.indexOf("<")+1,all_vip_num.indexOf(">"));
                            //adapter.updata_msg =all_vip_num.substring(all_vip_num.indexOf("[")+1,all_vip_num.indexOf("]"));
                            //isay =all_vip_num.substring(all_vip_num.indexOf("(")+1,all_vip_num.indexOf(")"));
                            //ann=all_vip_num.substring(all_vip_num.indexOf("{")+1,all_vip_num.indexOf("}"));
                            //Log.e("tag",ann);

                            //如果有更新的话先更新，不更新就检测用没用要说的话
                            if (Integer.parseInt(adapter.updata_version)>adapter.now_version){
                                updata_msg(adapter.updata_msg);
                            }else{
                                if (isay.length()!=0){
                                    show_say(isay);
                                }
                            }
                            if(ann.length()!=0){
                                tv_ann.setText("公告"+ann);
                            }
                        }
                    });

                }
            });//start();
        }


        //登录按钮事件的监听
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.cookie=null;
                adapter.name=null;
                if (!ck_user.isChecked()){
                    Toast.makeText(getApplicationContext(),"请先同意《用户协议》",Toast.LENGTH_SHORT).show();
                }else {
                    number=num.getText().toString();
                    adapter.num=number;
                    //密码base64解密
                    password = Base64.encodeToString(psd.getText().toString().getBytes(), Base64.DEFAULT);
                    post_login(number,password);

                    Toast.makeText(getApplicationContext(),"登录中...",Toast.LENGTH_SHORT).show();
                    SystemClock.sleep(2000);
                    //艺爪付费
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                EzrevenueClient.ezrevenueClient(number,adapter.name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    if(adapter.name!=null){
                        //are_you_vip(number);
                        Intent intentMain =new Intent(getApplicationContext(), GetQuestionActivity.class);
                        startActivity(intentMain);
                        adapter.sendStudentNum(number,adapter.name,password);
                        //记住密码的实现
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        if (ck_autologin.isChecked()){
                            editor.putString("num",number);
                            editor.putString("psd",password);
                            editor.putBoolean("user_flag",true);
                            editor.putBoolean("autologin_flag",true);
                        }else{editor.clear();}
                        editor.commit();

                        Toast.makeText(getApplicationContext(),adapter.name+"登录成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"密码错误\n也可能网络太差",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        tv_allow.setMovementMethod(LinkMovementMethod.getInstance());

    }

    //登录post查询
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

    //数据库数据的获取和获取VIP_num
    /*public void get_vip_num(){

        //加载数据库驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.d("MainActivity", "加载JDBC驱动成功！");
        } catch (ClassNotFoundException e) {
            Log.d("MainActivity", "加载JDBC驱动失败！");
            return;
        }
        //连接数据库（开辟一个新线程）
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 反复尝试连接，直到连接成功后退出循环
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(100);  // 每隔0.1秒尝试连接
                    } catch (InterruptedException e) {
                        Log.e("MainActivity", e.toString());
                    }

                    // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                    String ip = "154.209.71.68";                  //IP
                    int port = 3306;                              //mysql默认端口
                    String dbName = "studye";             //自己的数据库名
                    String url = "jdbc:mysql://" + ip + ":" + port
                            + "/" + dbName; // 构建连接mysql的字符串
                    String user = "StudyE";                //自己的用户名
                    String password ="11062732HhY";           //自己的密码

                    // 3.连接JDBC
                    try {
                        conn = DriverManager.getConnection(url, user, password);
                        Log.d("MainActivity", "连接数据库成功!");
                        //查询学生表
                        String sql = "SELECT * FROM StudentCode";
                        try {
                            // 创建用来执行sql语句的对象
                            java.sql.Statement statement = conn.createStatement();
                            // 执行sql查询语句并获取查询信息
                            ResultSet rSet = statement.executeQuery(sql);
                            // 迭代打印出查询信息
                            while (rSet.next()) {
                                all_vip_num+=rSet.getString("StudentNum")+"*";
                            }
                            adapter.updata_version =all_vip_num.substring(all_vip_num.indexOf("<")+1,all_vip_num.indexOf(">"));
                            adapter.updata_msg =all_vip_num.substring(all_vip_num.indexOf("[")+1,all_vip_num.indexOf("]"));
                            isay =all_vip_num.substring(all_vip_num.indexOf("(")+1,all_vip_num.indexOf(")"));
                            all_get_over=true;

                        } catch (SQLException e) {
                            Log.e("MainActivity", "查询错误");
                        }
                        //关闭数据库
                        try {
                            conn.close();
                            Log.d("MainActivity", "关闭连接成功。");
                        } catch (SQLException e) {
                            Log.d("MainActivity", "关闭连接失败。");
                        }
                        return;
                    } catch (SQLException e) {
                        Log.d("MainActivity", "连接数据库失败!");
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, "初始化失败，请重启应用", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }
        });
        thread.start();
    }

    //判断是否为会员
    /*public void are_you_vip(String num){
        if (all_vip_num.indexOf(num)>=0){
            adapter.vip_flag=true;
        }else {
            adapter.vip_flag=false;
        }
    }*/

    //显示对话框
    public void show_say(String str) {
        //实例化AlertDialog使用其子类来实现
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置信息
        builder.setMessage(str);
        //积极的按钮——确定/ok/finish
        //@param 按钮显示的文本
        //@param 单击事件
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        //对话框的显示
        builder.show();
    }

    //软件更新提示对话框
    public void updata_msg(String str) {
        //实例化AlertDialog使用其子类来实现
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置标题
        builder.setTitle("您有新版本更新：");
        //设置信息
        builder.setMessage(str);
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