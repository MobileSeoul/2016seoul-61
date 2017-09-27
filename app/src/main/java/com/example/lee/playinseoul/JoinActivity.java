package com.example.lee.playinseoul;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity {
    boolean ok_userid, ok_password, ok_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        // 모든 검사가 완료되어야 회원가입이 가능합니다.
        ok_userid = ok_password = ok_name = false;

        View button_join = findViewById(R.id.join_join);
        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ok_userid && ok_password && ok_name)
                    Join();
                else {
                    if(!ok_userid) findViewById(R.id.join_userid).requestFocus();
                    else if(!ok_password) findViewById(R.id.join_password2).requestFocus();
                    else if(!ok_name) findViewById(R.id.join_name).requestFocus();
                }
            }
        });

        // 아이디의 유효성을 검사하는 리스너들을 추가합니다.
        EditText edit_userid = (EditText)findViewById(R.id.join_userid);
        // 아이디 입력 완료 후에 중복검사를 해줍니다.
        edit_userid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if(focus) return;
                EditText edt = (EditText)view;
                String userid = edt.getText().toString();
                if(userid.length() < 4) {
                    ok_userid = false;
                    edt.setError("아이디는 4자리 이상입니다.");
                } else {
                    Check_ID_Overlap(userid);
                }
            }
        });
        // 아이디를 수정할 경우 중복 검사를 다시 해야하기 때문에 회원가입 방지를 합니다.
        edit_userid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ok_userid = false;
            }
        });

        // 비밀번호의 유효성을 검사하기 위한 리스너들을 추가합니다.
        EditText edit_password1 = (EditText)findViewById(R.id.join_password1);
        // 첫번째 비밀번호 칸은 자리 수 체크만 합니다.
        edit_password1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) return;
                EditText edt = (EditText)view;
                String password = edt.getText().toString();
                if(password.length() < 8) {
                    edt.setError("비밀번호는 8자리 이상입니다.");
                    ok_password = false;
                }
            }
        });
        // 첫번째 비밀번호 칸을 수정하는 경우 두번째 비밀번호 칸을 비워주고 비밀번호 일치 검사를 다시 하도록 합니다.
        edit_password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ok_password = false;
                EditText edt = (EditText)findViewById(R.id.join_password2);
                edt.setText("");
            }
        });

        // 비밀번호 확인의 유효성을 검사하기 위한 리스너를 추가합니다.
        EditText edit_password2 = (EditText)findViewById(R.id.join_password2);
        // 첫번째 비밀번호와 두번째 비밀번호가 일치하는지 검사합니다.
        edit_password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) return;
                EditText pass1 = (EditText)view;
                EditText pass2 = (EditText)findViewById(R.id.join_password1);
                if(pass1.getText().toString().equals(pass2.getText().toString())) {
                    ok_password = true;
                } else {
                    pass1.setError("비밀번호가 일치하지 않습니다.");
                    ok_password = false;
                }
            }
        });

        // 별칭의 유효성을 검사하기 위한 리스너를 추가합니다.
        EditText edit_name = (EditText)findViewById(R.id.join_name);
        // 사용자가 별칭을 모두 입력한 후에 별칭의 중복 검사를 실시합니다.
        edit_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) return;
            }
        });
        // 별칭을 수정하는 경우 다시 중복 검사를 하도록 합니다.
        edit_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ok_name = false;
                String name = editable.toString();
                Check_Name_Overlap(name);
            }
        });
    }

    private void Join() {
        final ProgressDialog progressDialog = ProgressDialog.show(JoinActivity.this, "", "회원가입 중입니다.");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 아래 코드는 서버와 연동하는 코드입니다.
                    // 서버의 특정 페이지로 아이디를 POST 방식으로 넘겨준 다음
                    // 결과 값을 입력 받아서 중복 여부를 얻습니다.
                    URL url = new URL("http://52.78.92.129:8080/PlayInSeoul/join.jsp");
                    HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                    huc.setRequestMethod("POST");
                    huc.setDoInput(true);
                    huc.setDoOutput(true);
                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    EditText edit_userid = (EditText)findViewById(R.id.join_userid);
                    EditText edit_password = (EditText)findViewById(R.id.join_password1);
                    EditText edit_name = (EditText)findViewById(R.id.join_name);
                    String userid = edit_userid.getText().toString();
                    String passowrd = edit_password.getText().toString();
                    String name = edit_name.getText().toString();

                    OutputStream os = huc.getOutputStream();
                    String body = "userid="+userid+"&password="+passowrd+"&name="+name;
                    os.write(body.getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader( new InputStreamReader( huc.getInputStream(), "UTF-8" ), huc.getContentLength() );
                    String buf , ret = "";
                    while((buf=br.readLine())!=null) {
                        if(!buf.equals("")) ret = buf;
                    }
                    // 회원가입 성공
                    if(ret.equals("100"))
                        handler_join.sendEmptyMessage(0);
                    // 회원가입 실패
                    else
                        handler_join.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });
        thread.start();
    }

    Handler handler_join = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 회원가입 성공
            if(msg.what == 0) {
                finish();
            }
            // 회원가입 실패
            else {
                Toast.makeText(JoinActivity.this, "알수 없는 오류로 회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void Check_ID_Overlap(final String userid) {
        final ProgressDialog progressDialog = ProgressDialog.show(JoinActivity.this, "", "중복 검사 중입니다.");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 아래 코드는 서버와 연동하는 코드입니다.
                    // 서버의 특정 페이지로 아이디를 POST 방식으로 넘겨준 다음
                    // 결과 값을 입력 받아서 중복 여부를 얻습니다.
                    URL url = new URL("http://52.78.92.129:8080/PlayInSeoul/id_overlap.jsp");
                    HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                    huc.setRequestMethod("POST");
                    huc.setDoInput(true);
                    huc.setDoOutput(true);
                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    OutputStream os = huc.getOutputStream();
                    String body = "userid="+userid;
                    os.write(body.getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader( new InputStreamReader( huc.getInputStream(), "UTF-8" ), huc.getContentLength() );
                    String buf , ret = "";
                    while((buf=br.readLine())!=null) {
                        if(!buf.equals("")) ret = buf;
                    }
                    // 중복이 없음
                    if(ret.equals("100"))
                        handler_id_overlap.sendEmptyMessage(0);
                        // 중복이 존재함
                    else
                        handler_id_overlap.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });
        thread.start();
    }

    Handler handler_id_overlap = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 중복인 경우
            if(msg.what == 0) {
                ok_userid = true;
            }
            // 중복이 아닌 경우
            else {
                ok_userid = false;
                EditText edit = (EditText)findViewById(R.id.join_userid);
                edit.setError("중복되는 아이디가 존재합니다.");
            }
        }
    };

    private void Check_Name_Overlap(final String name) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 아래 코드는 서버와 연동하는 코드입니다.
                    // 서버의 특정 페이지로 닉네임을 POST 방식으로 넘겨준 다음
                    // 결과 값을 입력 받아서 중복 여부를 얻습니다.
                    URL url = new URL("http://52.78.92.129:8080/PlayInSeoul/name_overlap.jsp");
                    HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                    huc.setRequestMethod("POST");
                    huc.setDoInput(true);
                    huc.setDoOutput(true);
                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    OutputStream os = huc.getOutputStream();
                    String body = "name="+name;
                    os.write(body.getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader( new InputStreamReader( huc.getInputStream(), "UTF-8" ), huc.getContentLength() );
                    String buf , ret = "";
                    while((buf=br.readLine())!=null) {
                        if(!buf.equals("")) ret = buf;
                    }
                    // 중복이 없음
                    if(ret.equals("100"))
                        handler_name_overlap.sendEmptyMessage(0);
                        // 중복이 존재함
                    else
                        handler_name_overlap.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    Handler handler_name_overlap = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 중복인 경우
            if(msg.what == 0) {
                ok_name = true;
            }
            // 중복이 아닌 경우
            else {
                ok_name = false;
                EditText edit = (EditText)findViewById(R.id.join_name);
                edit.setError("중복되는 별칭이 존재합니다.");
            }
        }
    };
}
