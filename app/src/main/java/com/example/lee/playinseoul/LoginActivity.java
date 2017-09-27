package com.example.lee.playinseoul;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    boolean auto_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 간단한 정보 불러올 때
        SharedPreferences pref = getSharedPreferences("PlayInSeoul", 0); // 불러올 Preferences 이름과 모드
        auto_login = pref.getBoolean("auto_login", false); // setting_enable 이라는 이름으로부터 값을 가져오는데 없는 경우 우 default값(false) 지정
        if(auto_login) {
            String userid = pref.getString("login_userid", "");
            String password = pref.getString("login_password", "");
            ImageView chk_auto = (ImageView)findViewById(R.id.login_auto);
            chk_auto.setImageResource(R.drawable.icon_checkbox_checked);
            EditText edt_userid = (EditText)findViewById(R.id.login_userid);
            EditText edt_password = (EditText)findViewById(R.id.login_password);
            edt_userid.setText(userid);
            edt_password.setText(password);
            Login(userid, password);
        }

        View button_login = findViewById(R.id.login_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edt_userid = (EditText)findViewById(R.id.login_userid);
                EditText edt_password = (EditText)findViewById(R.id.login_password);
                String userid = edt_userid.getText().toString();
                String password = edt_password.getText().toString();
                Login(userid, password);
            }
        });
        View button_join = findViewById(R.id.login_join);
        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });
        ImageView checkbox = (ImageView)findViewById(R.id.login_auto);
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_login = !auto_login;
                if(auto_login) ((ImageView)v).setImageResource(R.drawable.icon_checkbox_checked);
                else ((ImageView)v).setImageResource(R.drawable.icon_checkbox);
            }
        });
    }

    private void Login(final String userid, final String password) {
        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "", "로그인을 시도하는 중입니다.");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // 아래 코드는 서버와 연동하는 코드입니다.
                    // 서버의 특정 페이지로 아이디와 비밀번호를 POST 방식으로 넘겨준 다음
                    // 결과 값을 입력 받아서 로그인 성공 여부를 결정합니다.
                    URL url = new URL("http://52.78.92.129:8080/PlayInSeoul/login.jsp");
                    HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                    huc.setRequestMethod("POST");
                    huc.setDoInput(true);
                    huc.setDoOutput(true);
                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    OutputStream os = huc.getOutputStream();
                    String body = "userid="+userid+"&password="+password;
                    os.write(body.getBytes("euc-kr"));
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader( new InputStreamReader( huc.getInputStream(), "EUC-KR" ), huc.getContentLength() );
                    String buf , ret = "";
                    while((buf=br.readLine())!=null) {
                        if(!buf.equals("")) ret = buf;
                    }
                    if(ret.equals("100")) {
                        SaveLoginInformation(userid, password, auto_login);
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("userid", userid);
                        message.setData(bundle);
                        message.what = 0;
                        handler_login.sendMessage(message);
                    }
                    else
                        handler_login.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });
        thread.start();
    }

    // 로그인 결과 핸들러입니다.
    Handler handler_login = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 메세지의 what 값에 따라서 처리합니다. (sendEmptyMessage함수 매개인자)
            if(msg.what==0) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userid", msg.getData().getString("userid", ""));
                startActivity(intent);
                finish();
            } else {
                EditText edt_password = (EditText)findViewById(R.id.login_password);
                edt_password.setError("아이디 또는 비밀번호가 일치하지 않습니다.");
            }
        }
    };

    private void SaveLoginInformation(String userid, String password, boolean auto) {
        SharedPreferences pref = getSharedPreferences("PlayInSeoul", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("auto_login", auto);
        edit.putString("login_userid", userid);
        edit.putString("login_password", password);
        edit.commit(); // 실행 (저장)
    }
    
}
