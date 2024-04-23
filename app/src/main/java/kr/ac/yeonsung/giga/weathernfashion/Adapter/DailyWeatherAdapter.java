package kr.ac.yeonsung.giga.weathernfashion.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.Weather;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> {

    private ArrayList<Weather> mData = null ;
    private Context context;

    public DailyWeatherAdapter(Context context, ArrayList<Weather> mData) {
        this.mData = mData;
        this.context = context;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time ;
        ImageView icon;
        TextView temp;

        public ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            time = itemView.findViewById(R.id.time);
            icon = itemView.findViewById(R.id.icon);
            temp = itemView.findViewById(R.id.temp);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public DailyWeatherAdapter(ArrayList<Weather> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public DailyWeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_weather_recycler_view_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(DailyWeatherAdapter.ViewHolder holder, int position) {
        String temp = mData.get(position).getNow_Temp() + "°";
        ArrayList<String> morning = new ArrayList<>(Arrays.asList("오전 7시","오전 8시", "오전 9시","오전 10시","오전 11시","오후 12시","오후 1시",
                "오후 2시","오후 3시","오후 4시","오후 5시", "오후 6시"));
        ArrayList<String> night = new ArrayList<>(Arrays.asList("오전 00시","오전 1시","오전 2시","오전 3시","오전 4시","오전 5시","오전 6시",
                "오후 7시","오후 8시","오후 9시","오후 10시","오후 11시"));
        String sky = mData.get(position).getSky();
        String pty = mData.get(position).getPty();
        String time = mData.get(position).getTime();
        holder.time.setText(time);
        holder.temp.setText(temp);



        if(morning.contains(time)){
            if(sky.equals("1")){
                switch (pty) {
                    case "0":
                        holder.icon.setImageResource(R.drawable.sun_icon);
                        break;
                    case "1":
                        holder.icon.setImageResource(R.drawable.rain_icon);
                        break;
                    case "2":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "3":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "4":
                        holder.icon.setImageResource(R.drawable.snow_icon);
                        break;
                }
            }else if (sky.equals("2")) {
                switch (pty) {
                    case "0":
                        holder.icon.setImageResource(R.drawable.sun_cloud_icon);
                        break;
                    case "1":
                        holder.icon.setImageResource(R.drawable.rain_icon);
                        break;
                    case "2":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "3":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "4":
                        holder.icon.setImageResource(R.drawable.snow_icon);
                        break;
                }
            }else if (sky.equals("3")) {
                switch (pty) {
                    case "0":
                        holder.icon.setImageResource(R.drawable.sun_cloud_icon);
                        break;
                    case "1":
                        holder.icon.setImageResource(R.drawable.rain_icon);
                        break;
                    case "2":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "3":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "4":
                        holder.icon.setImageResource(R.drawable.snow_icon);
                        break;
                }
            }else if (sky.equals("4")) {
                switch (pty) {
                    case "0":
                        holder.icon.setImageResource(R.drawable.morning_cloud_icon);
                        break;
                    case "1":
                        holder.icon.setImageResource(R.drawable.rain_icon);
                        break;
                    case "2":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "3":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "4":
                        holder.icon.setImageResource(R.drawable.snow_icon);
                        break;
                }
            }
        }else{
            if(sky.equals("1")){
                switch (pty) {
                    case "0":
                        holder.icon.setImageResource(R.drawable.moon_icon);
                        break;
                    case "1":
                        holder.icon.setImageResource(R.drawable.rain_icon);
                        break;
                    case "2":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "3":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "4":
                        holder.icon.setImageResource(R.drawable.snow_icon);
                        break;
                }
            }else if (sky.equals("2")) {
                switch (pty) {
                    case "0":
                        holder.icon.setImageResource(R.drawable.moon_cloud_icon);
                        break;
                    case "1":
                        holder.icon.setImageResource(R.drawable.rain_icon);
                        break;
                    case "2":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "3":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "4":
                        holder.icon.setImageResource(R.drawable.snow_icon);
                        break;
                }
            }else if (sky.equals("3")) {
                switch (pty) {
                    case "0":
                        holder.icon.setImageResource(R.drawable.moon_cloud_icon);
                        break;
                    case "1":
                        holder.icon.setImageResource(R.drawable.rain_icon);
                        break;
                    case "2":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "3":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "4":
                        holder.icon.setImageResource(R.drawable.snow_icon);
                        break;
                }
            }else if (sky.equals("4")) {
                switch (pty) {
                    case "0":
                        holder.icon.setImageResource(R.drawable.night_cloud_icon);
                        break;
                    case "1":
                        holder.icon.setImageResource(R.drawable.rain_icon);
                        break;
                    case "2":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "3":
                        holder.icon.setImageResource(R.drawable.rain_snow_icon);
                        break;
                    case "4":
                        holder.icon.setImageResource(R.drawable.snow_icon);
                        break;
                }
            }
        }
    }
    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}