package com.example.lee.playinseoul;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyInformActivity extends AppCompatActivity {
    String userid;
    boolean ok_password, ok_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_inform);

        userid = getIntent().getStringExtra("userid");
        TextView text_userid = (TextView)findViewById(R.id.my_inform_userid);
        text_userid.setText(userid);

        getUserName(userid);

        ok_password = ok_name = false;

        findViewById(R.id.my_inform_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edit_password1 = (EditText) findViewById(R.id.my_inform_now_password);
                String password = edit_password1.getText().toString();
                if(password.equals("")) {
                    edit_password1.setError("비밀번호를 입력해주세요.");
                } else {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(MyInformActivity.this);
                    dialog.setTitle("주의");
                    dialog.setMessage("정말로 계정을 삭제하시겠습니까?");
                    dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            EditText edit_password1 = (EditText) findViewById(R.id.my_inform_now_password);
                            String password = edit_password1.getText().toString();
                            DelUser(MyInformActivity.this.userid, password);
                        }
                    });
                    dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });

        findViewById(R.id.my_inform_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edit_password = (EditText)findViewById(R.id.my_inform_password1);
                EditText edit_confirm = (EditText)findViewById(R.id.my_inform_password2);
                EditText edit_name = (EditText)findViewById(R.id.my_inform_name);
                String password = edit_password.getText().toString();
                String password2 = edit_confirm.getText().toString();
                String name = edit_name.getText().toString();

                // 비밀번호를 변경하는 경우
                if(!password.equals("") && name.equals("") && ok_password) {
                    if(!password.equals("") && password2.equals("")) {
                        edit_confirm.setError("비밀번호가 일치하지 않습니다.");
                        return;
                    }
                    EditInformation(userid, password, "");
                }
                // 닉네임을 변경하는 경우
                else if (password.equals("") && !name.equals("") && ok_name) {
                    EditInformation(userid, "", name);
                }
                // 둘다 변경하는 경우
                else if (!password.equals("") && !name.equals("") && ok_name && ok_password) {
                    EditInformation(userid, password, name);
                }
            }
        });

        View button_logout = findViewById(R.id.my_inform_logout);
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });
        // 비밀번호의 유효성을 검사하기 위한 리스너들을 추가합니다.
        EditText edit_password1 = (EditText)findViewById(R.id.my_inform_password1);
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
                EditText edt = (EditText)findViewById(R.id.my_inform_password2);
                edt.setText("");
            }
        });

        // 비밀번호 확인의 유효성을 검사하기 위한 리스너를 추가합니다.
        EditText edit_password2 = (EditText)findViewById(R.id.my_inform_password2);
        // 첫번째 비밀번호와 두번째 비밀번호가 일치하는지 검사합니다.
        edit_password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                EditText pass1 = (EditText)findViewById(R.id.my_inform_password1);
                if(pass1.getText().toString().equals(s.toString())) {
                    ok_password = true;
                } else {
                    ok_password = false;
                }
            }
        });

        // 별칭의 유효성을 검사하기 위한 리스너를 추가합니다.
        EditText edit_name = (EditText)findViewById(R.id.my_inform_name);
        // 사용자가 별칭을 모두 입력한 후에 별칭의 중복 검사를 실시합니다.
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

    private void EditInformation(final String userid, final String password, final String name) {
        final ProgressDialog progressDialog = ProgressDialog.show(MyInformActivity.this, "", "회원 정보를 수정합니다.");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int type = 0;
                    if(!password.equals("")) type += 1;
                    if(!name.equals("")) type += 2;
                    // 아래 코드는 서버와 연동하는 코드입니다.
                    // 서버의 특정 페이지로 닉네임을 POST 방식으로 넘겨준 다음
                    // 결과 값을 입력 받아서 중복 여부를 얻습니다.
                    URL url = new URL("http://52.78.92.129:8080/PlayInSeoul/edit_user.jsp");
                    HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                    huc.setRequestMethod("POST");
                    huc.setDoInput(true);
                    huc.setDoOutput(true);
                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    OutputStream os = huc.getOutputStream();
                    String body = "userid="+userid+"&password="+password+"&name="+name+"&type="+type;
                    Log.v("edit", body);
                    os.write(body.getBytes("utf-8"));
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader( new InputStreamReader( huc.getInputStream(), "utf-8" ), huc.getContentLength() );
                    String buf , ret = "";
                    while((buf=br.readLine())!=null) {
                        if(!buf.equals("")) ret = buf;
                    }
                    // 회원 수정에 성공함
                    if(ret.equals("100")) {
                        handler_edit_user.sendEmptyMessage(0);
                    }
                    // 오류 발생
                    else
                        handler_edit_user.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });
        thread.start();
    }

    private void DelUser(final String userid, final String password) {
        final ProgressDialog progressDialog = ProgressDialog.show(MyInformActivity.this, "", "회원 탈퇴를 진행중입니다..");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 아래 코드는 서버와 연동하는 코드입니다.
                    // 서버의 특정 페이지로 닉네임을 POST 방식으로 넘겨준 다음
                    // 결과 값을 입력 받아서 중복 여부를 얻습니다.
                    URL url = new URL("http://52.78.92.129:8080/PlayInSeoul/del_user.jsp");
                    HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                    huc.setRequestMethod("POST");
                    huc.setDoInput(true);
                    huc.setDoOutput(true);
                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    OutputStream os = huc.getOutputStream();
                    String body = "userid="+userid+"&password="+password;
                    os.write(body.getBytes("utf-8"));
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader( new InputStreamReader( huc.getInputStream(), "utf-8" ), huc.getContentLength() );
                    String buf , ret = "";
                    while((buf=br.readLine())!=null) {
                        if(!buf.equals("")) ret = buf;
                    }
                    // 계정 탈퇴에 성공함
                    if(ret.equals("100"))
                        handler_del_user.sendEmptyMessage(0);
                    // 비밀번호가 틀림
                    else
                        handler_del_user.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });
        thread.start();
    }

    private void Logout() {
        SharedPreferences pref = getSharedPreferences("PlayInSeoul", 0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("auto_login", false);
        edit.putString("login_userid", "");
        edit.putString("login_password", "");
        edit.commit(); // 실행 (저장)
        Intent intent = new Intent(MyInformActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

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
                    os.write(body.getBytes("utf-8"));
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
                EditText edit = (EditText)findViewById(R.id.my_inform_name);
                edit.setError("중복되는 별칭이 존재합니다.");
            }
        }
    };

    Handler handler_del_user = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 회원 탈퇴에 성공한 경우
            if(msg.what == 0) {
                Logout();
            }
            // 비밀번호가 틀린 경우
            else {
                EditText edit_password1 = (EditText)findViewById(R.id.my_inform_now_password);
                edit_password1.setText("");
                edit_password1.requestFocus();
                ok_password = false;
            }
        }
    };

    Handler handler_edit_user = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyInformActivity.this);
                builder.setMessage("회원 정보를 수정하였습니다.");
                builder.setPositiveButton("확인", null);
                Dialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyInformActivity.this);
                builder.setMessage("오류가 발생하였습니다.");
                builder.setPositiveButton("확인", null);
                Dialog dialog = builder.create();
                dialog.show();
            }
        }
    };

    private void getUserName(final String userid) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 아래 코드는 서버와 연동하는 코드입니다.
                    // 서버의 특정 페이지로 닉네임을 POST 방식으로 넘겨준 다음
                    // 결과 값을 입력 받아서 중복 여부를 얻습니다.
                    URL url = new URL("http://52.78.92.129:8080/PlayInSeoul/getName.jsp");
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
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("name", ret);
                    msg.setData(data);
                    handlerGetName.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    Handler handlerGetName = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String name = msg.getData().getString("name");
            EditText editName = (EditText)findViewById(R.id.my_inform_name);
            editName.setHint(name);
        }
    };
}
