package com.example.studye;

import static com.example.studye.adapter.updata_msg;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class about_activity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_about,tv_getvip,tv_user,tv_into,tv_getupdate;
    private OkHttpClient okHttpClient;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
        tv_getupdate.setText("检查更新 "+"V"+adapter.now_version2);

    }

    //初始化
    private void init() {
        tv_about=findViewById(R.id.about_about);
        tv_getvip=findViewById(R.id.about_getvip);
        tv_user=findViewById(R.id.about_user);
        tv_into=findViewById(R.id.about_into);
        tv_getupdate=findViewById(R.id.about_getupdata);
        tv_getupdate.setOnClickListener(this);
        tv_into.setOnClickListener(this);
        tv_about.setOnClickListener(this);
        tv_getvip.setOnClickListener(this);
        tv_user.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_about:
                Uri uri = Uri.parse("https://support.qq.com/products/379381/blog/547458");    //设置跳转的网站
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.about_getvip:
                Intent intent1 = new Intent(getApplicationContext(), get_vipActivity.class);
                startActivity(intent1);
                break;
            case R.id.about_user:
                Uri uri2 = Uri.parse("https://support.qq.com/products/379381/blog/547451");    //设置跳转的网站
                Intent intent2 = new Intent(Intent.ACTION_VIEW, uri2);
                startActivity(intent2);
                break;
            case R.id.about_into:
                Intent intent3 = new Intent();
                intent3.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + "ZwOxyHWryCJaKJ_Ho8BInZrw1C0I6WBf"));
                // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    startActivity(intent3);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"请检查是否安装QQ",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.about_getupdata:
                Toast.makeText(getApplicationContext(),"暂无更新",Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),"弃坑中，有缘再见,接盘请联系3283065209",Toast.LENGTH_LONG).show();
                /*
                if (Integer.parseInt(adapter.updata_version)>adapter.now_version){
                    updata_msg(adapter.updata_msg);
                }else{
                }
                */
                break;
        }

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
                Toast.makeText(getApplicationContext(),"密码：abcd",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        //对话框的显示
        builder.show();
    }
}