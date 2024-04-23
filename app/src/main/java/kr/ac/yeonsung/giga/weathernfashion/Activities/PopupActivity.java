package kr.ac.yeonsung.giga.weathernfashion.Activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.Categories;


public class PopupActivity extends Activity {
    CheckBox checkBoxCasual,checkBoxMinimal,checkBoxAmerican,checkBoxStreet,checkBoxEtc ;
    Button btnSubit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        Categories categories = new Categories();

        checkBoxCasual = findViewById(R.id.checkbox_casual);
        checkBoxMinimal = findViewById(R.id.checkbox_minimal);
        checkBoxAmerican = findViewById(R.id.checkbox_American);
        checkBoxStreet = findViewById(R.id.checkbox_street);
        checkBoxEtc = findViewById(R.id.checkbox_etc);
        btnSubit = findViewById(R.id.btn_categoris_submit);

        btnSubit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PostActivity.class);

                if(checkBoxCasual.isChecked() ==true){
                    intent.putExtra("casual",checkBoxCasual.getText().toString());
                }
                if(checkBoxMinimal.isChecked() ==true){
                    intent.putExtra("minimal",checkBoxMinimal.getText().toString());
                }
                if(checkBoxAmerican.isChecked() ==true){
                    intent.putExtra("american",checkBoxAmerican.getText().toString());
                }
                if(checkBoxStreet.isChecked() ==true){
                    intent.putExtra("street",checkBoxStreet.getText().toString());
                }
                if(checkBoxEtc.isChecked() ==true){
                    intent.putExtra("etc",checkBoxEtc.getText().toString());
                }
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}
