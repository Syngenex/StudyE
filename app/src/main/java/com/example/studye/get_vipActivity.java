package com.example.studye;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class get_vipActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_getvip;
    private TextView tv_useread;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getvip);

        tv_useread=findViewById(R.id.getvip_allow_usertxt);
        btn_getvip=findViewById(R.id.btn_getvpi_get);
        tv_useread.setMovementMethod(LinkMovementMethod.getInstance());
        if (adapter.youngjiu_vip_flag){
            btn_getvip.setText("你已经解锁高级版");
        }else {
            btn_getvip.setOnClickListener(this);
        }

    }

    //按钮事件
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_getvpi_get){
            Intent intent1 = new Intent(getApplicationContext(), Activity_WebView.class);
            startActivity(intent1);
        }
    }
}
