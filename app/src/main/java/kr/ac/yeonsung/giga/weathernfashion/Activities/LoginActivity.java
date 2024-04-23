package kr.ac.yeonsung.giga.weathernfashion.Activities;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.methods.API;
import kr.ac.yeonsung.giga.weathernfashion.methods.UserMethods;

public class LoginActivity extends AppCompatActivity {
    UserMethods userMethods = new UserMethods();
    API api = new API();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText user_email;
    private EditText user_pw;
    private String email;
    private String pw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        TextView text_weather = findViewById(R.id.text_weather);
        TextView text_fashion = findViewById(R.id.text_fashion);

        user_email = findViewById(R.id.user_email);
        user_pw = findViewById(R.id.user_pw);



        TextView join_user = findViewById(R.id.join_user);
        Button login_btn = findViewById(R.id.login_btn);

        join_user.setOnClickListener(btnListner);
        login_btn.setOnClickListener(btnListner);


        //텍스트뷰 텍스트 -> String 변환
        String weather = text_weather.getText().toString();
        String fashion = text_fashion.getText().toString();

        // 각각 텍스트를 담은 객체 생성
        SpannableString spannableWeather = new SpannableString(weather);
        SpannableString spannableFashion = new SpannableString(fashion);

        spannableWeather.setSpan(new RelativeSizeSpan(1.6f),0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text_weather.setText(spannableWeather);
        spannableFashion.setSpan(new RelativeSizeSpan(1.6f),0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text_fashion.setText(spannableFashion);
    }

    View.OnClickListener btnListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.join_user:
                    mystartActivity(JoinActivity.class);
                    break;
                case R.id.login_btn:
                    email = user_email.getText().toString();
                    pw = user_pw.getText().toString();
                    System.out.println("이메일 : " + email + " 비번 : "+pw);
                    user_login(email,pw,LoginActivity.this);
                    break;
            }
        }
    };

    public void user_login(String email, String passwd, Activity activity){
        mAuth = FirebaseAuth.getInstance();
        List<String>hash = new ArrayList<>();
        hash.clear();
        if(email.length() > 0 && passwd.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, passwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();
                                api.getToast(activity, "로그인성공");
                                mystartActivity(MainActivity.class);
                            } else {
                                api.getToast(activity, "비밀번호가 틀렸습니다.");
                            }
                        }
                    });
        } else {
            api.getToast(activity,"이메일 또는 비밀번호를 입력하세요.");
        }
    }

    public void mystartActivity(Class c){
        Intent intent = new Intent(LoginActivity.this,c);
        startActivity(intent);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
    }
}