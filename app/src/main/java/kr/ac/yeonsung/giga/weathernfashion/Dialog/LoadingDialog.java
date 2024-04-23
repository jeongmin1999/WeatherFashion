package kr.ac.yeonsung.giga.weathernfashion.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import kr.ac.yeonsung.giga.weathernfashion.R;

public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context){

        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);


    }
}
