package kr.ac.yeonsung.giga.weathernfashion.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.methods.API;
import kr.ac.yeonsung.giga.weathernfashion.methods.UserMethods;

public class JoinActivity extends Activity {
    private EditText user_email;
    private EditText user_pw;
    private EditText user_name;
    private EditText user_pwcheck;
    private EditText user_phone;
    Boolean result = false;
    UserMethods userMethods = new UserMethods();
    API api = new API();
    private Button check_btn;

    private String email;
    private String pw;
    private String pwcheck;
    private String name;
    private String phone;
    private String gender;
    private Intent intent;


    private Spinner gender_spinner;
    private Button btn_other; //기타
    private Button btn_casual; //캐주얼
    private Button btn_ame; //아메카지
    private Button btn_minimal; //미니얼
    private Button btn_street; //스트릿

    private List<String> styles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        TextView text_weather = findViewById(R.id.text_weather);
        TextView text_fashion = findViewById(R.id.text_fashion);

        check_btn = findViewById(R.id.check_btn);

        user_email = findViewById(R.id.user_email);
        user_pw = findViewById(R.id.user_pw);
        user_pwcheck = findViewById(R.id.user_pwcheck);
        user_name = findViewById(R.id.user_name);
        user_phone = findViewById(R.id.user_phone);

        Button join_btn = findViewById(R.id.join_btn); // 가입 버튼
        btn_other = findViewById(R.id.btn_other); //기타
        btn_casual = findViewById(R.id.btn_casual); //캐주얼
        btn_ame =findViewById(R.id.btn_ame); //아메카지
        btn_minimal = findViewById(R.id.btn_minimal); //미니얼
        btn_street =findViewById(R.id.btn_street); //스트릿

        gender_spinner = findViewById(R.id.spinner_gender);

        btn_other.setOnClickListener(btnListner);
        btn_casual.setOnClickListener(btnListner);
        btn_ame.setOnClickListener(btnListner);
        btn_minimal.setOnClickListener(btnListner);
        btn_street.setOnClickListener(btnListner);
        join_btn.setOnClickListener(join_btn_listener);

        check_btn.setOnClickListener(checkListener);

        List<String> gender_list = new ArrayList<>(Arrays.asList("남자","여자"));
        ArrayAdapter<String> gender_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,gender_list);
        gender_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(gender_adapter);

//////////////////////////////////////////////////////////////////////////////////////////////
        //텍스트뷰 텍스트 -> String 변환
        String weather = text_weather.getText().toString();
        String fashion = text_fashion.getText().toString();

        // 각각 텍스트를 담은 객체 생성
        SpannableString spannableWeather = new SpannableString(weather);
        SpannableString spannableFashion = new SpannableString(fashion);

        spannableWeather.setSpan(new RelativeSizeSpan(1.6f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text_weather.setText(spannableWeather);
        spannableFashion.setSpan(new RelativeSizeSpan(1.6f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text_fashion.setText(spannableFashion);
//////////////////////////////////////////////////////////////////////////////////////////////
        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                gender = adapterView.getItemAtPosition(i).toString();
                System.out.println("ㅎㅎ : " + gender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


View.OnClickListener checkListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        boolean result;
        email = user_email.getText().toString();
        result = userMethods.email_check(email);
        if (email.length()>6){
            if(result == false){
                check_btn.setText("v");
                check_btn.setTextColor(Color.CYAN);
                check_btn.setBackgroundResource(R.drawable.radius_btn);
                api.getToast(JoinActivity.this,"사용가능한 이메일입니다.");
            }else {
                check_btn.setText("");
                check_btn.setBackgroundResource(R.drawable.radius_btn_empty);
                api.getToast(JoinActivity.this,"이미 존재하는 이메일입니다.");
            }
        } else{
            check_btn.setText("");
            check_btn.setBackgroundResource(R.drawable.radius_btn_empty);
            api.getToast(JoinActivity.this,"이메일을 정확히 입력해주세요.");
        }
    }
};





View.OnClickListener join_btn_listener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        email = user_email.getText().toString();
        pw = user_pw.getText().toString();
        pwcheck = user_pwcheck.getText().toString();
        name = user_name.getText().toString();
        phone = user_phone.getText().toString();
        System.out.println("email : " + email);
        System.out.println("pw : " + pw +" pwcheck : " + pwcheck);
        System.out.println("name : " + name);
        System.out.println("phone : " + phone);
        if (email.length()>0 && pw.length()>0 && name.length()>0 && phone.length() > 0) {
            if (result == false) { // 중복체크 완료 후
                if (email.length() >= 6 && pw.length() >= 6) {
                    if (pw.equals(pwcheck)) {
                        userMethods.join_fb(email,pw,name,phone,styles,gender,JoinActivity.this);
                    } else {
                        api.getToast(JoinActivity.this, "비밀번호가 일치하지 않습니다.");
                    }
                } else {
                    api.getToast(JoinActivity.this, "이메일, 비밀번호는 8자 이상입니다.");
                }
            } else if(result == true) {
                api.getToast(JoinActivity.this, "중복 확인 체크.");
            }
        } else{
            api.getToast(JoinActivity.this,"입력이 안된 항목이 있습니다.");
        }
    }
};








/// 스타일 리스트 세팅
    View.OnClickListener btnListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btn_other:
                        if(styles.size() < 3) {
                            if (!styles.contains(btn_other.getText())) {
                                styles.add((String) btn_other.getText());
                                System.out.println(styles);
                                btn_other.setBackgroundResource(R.drawable.radius_btn);
                                btn_other.setTextColor(Color.WHITE);
                            } else if (styles.contains(btn_other.getText())) {
                                styles.remove((String) btn_other.getText());
                                System.out.println(styles);
                                btn_other.setTextColor(Color.BLACK);
                                btn_other.setBackgroundResource(R.drawable.radius_btn_empty);
                            }

                        }else if (styles.size() >= 3 && styles.contains(btn_other.getText())){
                            styles.remove((String)btn_other.getText());
                            System.out.println(styles);
                            btn_other.setTextColor(Color.BLACK);
                            btn_other.setBackgroundResource(R.drawable.radius_btn_empty);
                        } else {
                            Toast.makeText(JoinActivity.this, "최대 3개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.btn_ame:
                        if(styles.size() < 3) {
                            if (!styles.contains(btn_ame.getText())) {
                                styles.add((String) btn_ame.getText());
                                System.out.println(styles);
                                btn_ame.setBackgroundResource(R.drawable.radius_btn);
                                btn_ame.setTextColor(Color.WHITE);
                            } else if (styles.contains(btn_ame.getText())) {
                                styles.remove((String) btn_ame.getText());
                                System.out.println(styles);
                                btn_ame.setTextColor(Color.BLACK);
                                btn_ame.setBackgroundResource(R.drawable.radius_btn_empty);
                            }

                        }else if (styles.size() >= 3 && styles.contains(btn_ame.getText())){
                            styles.remove((String) btn_ame.getText());
                            System.out.println(styles);
                            btn_ame.setTextColor(Color.BLACK);
                            btn_ame.setBackgroundResource(R.drawable.radius_btn_empty);
                        } else {
                            Toast.makeText(JoinActivity.this, "최대 3개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.btn_casual:
                        if(styles.size() < 3) {
                            if (!styles.contains(btn_casual.getText())) {
                                styles.add((String) btn_casual.getText());
                                System.out.println(styles);
                                btn_casual.setBackgroundResource(R.drawable.radius_btn);
                                btn_casual.setTextColor(Color.WHITE);
                            } else if (styles.contains(btn_casual.getText())) {
                                styles.remove((String) btn_casual.getText());
                                System.out.println(styles);
                                btn_casual.setTextColor(Color.BLACK);
                                btn_casual.setBackgroundResource(R.drawable.radius_btn_empty);
                            }

                        }else if (styles.size() >= 3 && styles.contains(btn_casual.getText())){
                            styles.remove((String) btn_casual.getText());
                            System.out.println(styles);
                            btn_casual.setTextColor(Color.BLACK);
                            btn_casual.setBackgroundResource(R.drawable.radius_btn_empty);
                        } else {
                            Toast.makeText(JoinActivity.this, "최대 3개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.btn_minimal:
                        if(styles.size() < 3) {
                            if (!styles.contains(btn_minimal.getText())) {
                                styles.add((String) btn_minimal.getText());
                                System.out.println(styles);
                                btn_minimal.setBackgroundResource(R.drawable.radius_btn);
                                btn_minimal.setTextColor(Color.WHITE);
                            } else if (styles.contains(btn_minimal.getText())) {
                                styles.remove((String) btn_minimal.getText());
                                System.out.println(styles);
                                btn_minimal.setTextColor(Color.BLACK);
                                btn_minimal.setBackgroundResource(R.drawable.radius_btn_empty);
                            }

                        }else if (styles.size() >= 3 && styles.contains(btn_minimal.getText())){
                            styles.remove((String) btn_minimal.getText());
                            System.out.println(styles);
                            btn_minimal.setTextColor(Color.BLACK);
                            btn_minimal.setBackgroundResource(R.drawable.radius_btn_empty);
                        } else {
                            Toast.makeText(JoinActivity.this, "최대 3개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.btn_street:
                        if(styles.size() < 3) {
                            if (!styles.contains(btn_street.getText())) {
                                styles.add((String) btn_street.getText());
                                System.out.println(styles);
                                btn_street.setBackgroundResource(R.drawable.radius_btn);
                                btn_street.setTextColor(Color.WHITE);
                            } else if (styles.contains(btn_street.getText())) {
                                styles.remove((String) btn_street.getText());
                                System.out.println(styles);
                                btn_street.setTextColor(Color.BLACK);
                                btn_street.setBackgroundResource(R.drawable.radius_btn_empty);
                            }

                        }else if (styles.size() == 3 && styles.contains(btn_street.getText())){
                            styles.remove((String) btn_street.getText());
                            System.out.println(styles);
                            btn_street.setTextColor(Color.BLACK);
                            btn_street.setBackgroundResource(R.drawable.radius_btn_empty);
                        } else {
                            Toast.makeText(JoinActivity.this, "최대 3개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;
            }
        }
    };
}