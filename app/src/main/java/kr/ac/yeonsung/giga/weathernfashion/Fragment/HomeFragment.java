package kr.ac.yeonsung.giga.weathernfashion.Fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import kr.ac.yeonsung.giga.weathernfashion.Dialog.LoadingDialog;
import kr.ac.yeonsung.giga.weathernfashion.Activities.LoginActivity;
import kr.ac.yeonsung.giga.weathernfashion.Adapter.PostRankAdapter;
import kr.ac.yeonsung.giga.weathernfashion.Adapter.DailyWeatherAdapter;
import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.PostRank;
import kr.ac.yeonsung.giga.weathernfashion.VO.Weather;
import kr.ac.yeonsung.giga.weathernfashion.methods.API;
import kr.ac.yeonsung.giga.weathernfashion.methods.UserMethods;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment{
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    UserMethods userMethods = new UserMethods();
    ArrayList<String> morning = new ArrayList<>(Arrays.asList("07","08","09","10","11","12","13","14","15","16","17","18"));
    ArrayList<String> night = new ArrayList<>(Arrays.asList("00","01","02","03","04","05","06","19","20","21","22","23"));
    String mint_str, maxt_str;
    API api = new API();
    RecyclerView.Adapter adapter;
    ImageView weather_icon;
    RecyclerView recyclerView;
    ScrollView MainLayout;
    LinearLayout SubLayout;
    RecyclerView.Adapter rank_adapter;
    RecyclerView rank_recyclerView;
    String dateNow;
    Long mint, maxt;
    DateFormat df = new SimpleDateFormat("yyyyMMdd");
    TextView nowTemp; //현재 온도
    TextView nowWeather; //현재 날씨
    TextView si; //현재 지역
    TextView gu; //현재 지역
    TextView dong; //현재 지역
    public TextView min_temp; //최저
    public TextView max_temp; //최고
    TextView feel_temp; //체감
    TextView humidity;//습도
    TextView wind_speed;//풍속
    TextView cloud; // 구름
    TextView weatherCode;
    String weatherCodeStr;
    ArrayList<Weather> list = new ArrayList();
    ArrayList<PostRank> rank_list = new ArrayList();
    String date3;
    String date2;
    DatabaseReference mDatabase;
    Calendar cal = Calendar.getInstance();

    DateFormat df_now = new SimpleDateFormat("HH");
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    LoadingDialog loadingDialog;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        Button logout = view.findViewById(R.id.logout_btn);
        cal.setTime(new Date());
        date2 = df.format(cal.getTime());
        cal.setTime(new Date());
        date3 = df.format(cal.getTime());
        dateNow = df_now.format(cal.getTime()) ;
        System.out.println("현재시간 " +dateNow);
        weather_icon = view.findViewById(R.id.weather_icon);
        weatherCode = view.findViewById(R.id.weather_code);
        MainLayout = view.findViewById(R.id.MainLayout);
        SubLayout = view.findViewById(R.id.SubLayout);
        nowTemp = view.findViewById(R.id.now_temp);
        si = view.findViewById(R.id.si);
        gu = view.findViewById(R.id.gu);
        dong = view.findViewById(R.id.dong);
        min_temp = view.findViewById(R.id.min_temp);
        max_temp = view.findViewById(R.id.max_temp);
        feel_temp = view.findViewById(R.id.temp_feel);
        humidity = view.findViewById(R.id.now_humidity);
        wind_speed = view.findViewById(R.id.wind_speed);
        cloud = view.findViewById(R.id.now_cloud);

        recyclerView = view.findViewById(R.id.recyclerView) ;
        rank_recyclerView = view.findViewById(R.id.rank_recyclerView) ;
        recyclerView.setHasFixedSize(true);
        rank_recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false)) ;
        rank_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false)) ;

        getBackgroundColor();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        logout.setOnClickListener(btnListener);
        weather_icon.setOnClickListener(btnListener);


        new Thread(){
            @Override
            public void run() {
                try {
                    api.getWeatherNow(getActivity(), weather_icon,nowTemp, nowWeather, si, gu, dong, feel_temp, humidity, wind_speed, cloud, weatherCode);


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            api.getMyAddress(si,gu,dong);
                            api.getWeatherList(getActivity(),si.getText().toString(), gu.getText().toString(), min_temp,max_temp, mint_str, maxt_str);
                            getDailyWeather();
                            // 이 위치가 아니면 메소드가 실행이 안됩니다 list2에 값은 저장되는데 이 스레드 밖으로 나가면 사라져요 이걸 해결해야할 것 같습니다
                            mint_str = min_temp.getText().toString().substring(0,min_temp.getText().toString().lastIndexOf("°"));
                            maxt_str = max_temp.getText().toString().substring(0,max_temp.getText().toString().lastIndexOf("°"));
                            loadingDialog.dismiss();
                            getPostRank();

                        }
                    });



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


        setCode();
        api.getWeatherIcon(getActivity(), weather_icon, weatherCodeStr); // 날씨아이콘
       return view;
    }

    public void setCode(){
        weatherCodeStr = weatherCode.getText().toString();

    }

    // 버튼 리스너 (로그아웃, 위치 날씨 설정)
    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.logout_btn:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case R.id.weather_icon:
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                api.getWeatherNow(getActivity(), weather_icon,nowTemp, nowWeather, si, gu, dong, feel_temp, humidity, wind_speed, cloud, weatherCode);
                                api.getMyAddress(si,gu,dong);
                                api.getWeatherList(getActivity(), si.getText().toString(), gu.getText().toString(), min_temp, max_temp, mint_str, maxt_str);
                                // 이 위치가 아니면 메소드가 실행이 안됩니다 list2에 값은 저장되는데 이 스레드 밖으로 나가면 사라져요 이걸 해결해야할 것 같습니다
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    api.getToast(getActivity(),"새로고침 중...");
                    break;
            }
        }
    };
    public void getPostRank(){

        mDatabase.child("post").orderByChild("post_likeCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rank_list.clear();
                for (DataSnapshot document : snapshot.getChildren()) {
                    System.out.println(document.getValue());

                    System.out.println("민 : " + mint_str);
                    System.out.println("맥스 : " + maxt_str);

                    long postmin = Long.parseLong(document.child("post_min_temp").getValue().toString());
                    long postmax = Long.parseLong(document.child("post_max_temp").getValue().toString());
                    long mint = Long.parseLong(mint_str);
                    long maxt = Long.parseLong(maxt_str);
                    if ((mint == postmin - 1 || mint == postmin || mint == postmin + 1) && (maxt == postmax - 1 || maxt == postmax || maxt == postmax + 1)) {
                        String postImage = document.child("post_image").getValue().toString();
                        String postlike = document.child("post_likeCount").getValue().toString();
                        String postMax = document.child("post_max_temp").getValue().toString();
                        String postMin = document.child("post_min_temp").getValue().toString();
                        String post_id = document.getKey();
                        PostRank postRank = new PostRank(postImage, postMax, postMin, postlike, post_id);
                        rank_list.add(postRank);
                    }

                Collections.reverse(rank_list);
                }
            rank_adapter =new PostRankAdapter(getContext(),rank_list);
             rank_adapter.notifyDataSetChanged();
            rank_recyclerView.setAdapter(rank_adapter);
        }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getDailyWeather(){
        int idx = gu.getText().toString().indexOf("시");
        String adress = si.getText().toString() + " " +
                gu.getText().toString().substring(0, idx+1);
        System.out.println(adress);
        mDatabase.child("weather").child(adress).child(date2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot document : snapshot.getChildren()){
                        String temp = document.child("now_Temp").getValue().toString();
                        String sky = document.child("sky").getValue().toString();
                        String pty = document.child("pty").getValue().toString();
                        String time = document.child("time").getValue().toString();
                        Weather weather = new Weather(time,sky,pty,temp);
                        list.add(weather);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        adapter = new DailyWeatherAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter);
    }

    public void getBackgroundColor(){
        if (morning.contains(dateNow)){
            MainLayout.setBackgroundResource(R.drawable.bg_gradient_morning);
            SubLayout.setBackgroundResource(R.drawable.bg_morning);
            recyclerView.setBackgroundResource(R.drawable.bg_morning);
            rank_recyclerView.setBackgroundResource(R.drawable.bg_morning);
        }else if (night.contains(dateNow)){
            MainLayout.setBackgroundResource(R.drawable.bg_gradient_night);
            SubLayout.setBackgroundResource(R.drawable.bg_night);
            recyclerView.setBackgroundResource(R.drawable.bg_night);
            rank_recyclerView.setBackgroundResource(R.drawable.bg_night);
        }
    }
}