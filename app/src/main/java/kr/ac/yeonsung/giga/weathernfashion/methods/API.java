package kr.ac.yeonsung.giga.weathernfashion.methods;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.Timestamp;
import java.text.*;
import java.util.*;

import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.Weather;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class API {
    DatabaseReference mDatabase ;
    String date2;
    Double lon;
    Double lat;
    String dateNow;
    DateFormat df = new SimpleDateFormat("yyyyMMdd");
    public static int TO_GRID = 0;
    public static int TO_GPS = 1;
    ArrayList<String> thunder = new ArrayList<>(Arrays.asList("200","201","202"
            ,"210","211","212","221","230","231","232"));
    ArrayList<String> drizzle = new ArrayList<>(Arrays.asList("300","301","302"
            ,"310","311","312","313","314","321"));
    ArrayList<String> rain = new ArrayList<>(Arrays.asList("500","501","502"
            ,"503","504","511","520","521","522"));
    ArrayList<String> snow = new ArrayList<>(Arrays.asList("600","601","602"
            ,"611","612","613","615","616","620","621","622"));
    ArrayList<String> Atmosphere = new ArrayList<>(Arrays.asList("701","711","721"
            ,"731","741","751","761","762","771","781"));
    String [] times = {"오전 00시","오전 1시","오전 2시","오전 3시","오전 4시","오전 5시","오전 6시","오전 7시","오전 8시",
            "오전 9시","오전 10시","오전 11시","오후 12시","오후 1시","오후 2시","오후 3시","오후 4시","오후 5시",
            "오후 6시","오후 7시","오후 8시","오후 9시","오후 10시","오후 11시"};
    ArrayList<String> morning = new ArrayList<>(Arrays.asList("07","08","09","10","11","12","13","14","15","16","17","18"));
    ArrayList<String> night = new ArrayList<>(Arrays.asList("00","01","02","03","04","05","06","19","20","21","22","23"));
    ImageView imageView;

    String [] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION
    };

    //위치 권한 설정 체크 및 좌표 가져오기
    public void getGpsLocation(Activity activity) {
        int MY_PERMISSIONS_REQUEST_LOCATION = 10;
        ActivityCompat.requestPermissions(activity,PERMISSIONS, MY_PERMISSIONS_REQUEST_LOCATION);
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "위치 권한 세팅중...", Toast.LENGTH_LONG).show();
            return;
        } else {

            String provider = LocationManager.NETWORK_PROVIDER;
            Location location = lm.getLastKnownLocation(provider);
//
            lon = location.getLongitude();
            lat = location.getLatitude();

        }
        if (ActivityCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_MEDIA_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "위치 권한 세팅중...", Toast.LENGTH_LONG).show();
            return;
        }


    }


    public void getWeatherNow(Activity activity, ImageView imageView, TextView nowTemp, TextView nowWeather, TextView si, TextView gu, TextView dong,
                              TextView feeltemp, TextView humidity, TextView wind_speed, TextView cloud, TextView weatherCode){
        try {
            //서울시청의 위도와 경도이지만 핸드폰 위경도로 받을 수 있게 수정해야해요!
            getGpsLocation(activity);
//            lon=126.842892677;
//            lat=37.653102738;
            //OpenAPI call하는 URL
//            https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon
                    +"&lang=kr&appid=623ffab3e9d338b9916bd0b0e33d5d87&units=metric");
            BufferedReader bf;
            String line;
            String result="";

            //날씨 정보를 받아온다.
            bf = new BufferedReader(new InputStreamReader(url.openStream()));

            //버퍼에 있는 정보를 문자열로 변환.
            while((line=bf.readLine())!=null){
                result=result.concat(line+"\n");
            }
            System.out.println(result);
            //문자열을 JSON으로 파싱
            JSONObject jsonObj = new JSONObject(result);
            JSONObject main = jsonObj.getJSONObject("main");
            JSONArray weather = jsonObj.getJSONArray("weather");
            JSONObject wind = jsonObj.getJSONObject("wind");
            JSONObject clouds = jsonObj.getJSONObject("clouds");
            JSONObject weatherdesc = weather.getJSONObject(0);

            String temp, min_temp, max_temp, feel_temp,wind_speed_str,humidity_str,cloud_str, mylocation;

            if (main.getString("temp").contains(".")) {
                int temp_idx = main.getString("temp").indexOf(".");
                temp = main.getString("temp").substring(0,temp_idx);  // 소수점 첫째 자리까지만 출력
            } else{
                temp = main.getString("temp");
            }

            if (main.getString("feels_like").contains(".")) {
                int feel_temp_idx = main.getString("feels_like").indexOf(".");
                feel_temp = main.getString("feels_like").substring(0,feel_temp_idx);  // 소수점 첫째 자리까지만 출력
            } else{
                feel_temp = main.getString("feels_like");
            }

            if (wind.getString("speed").contains(".")) {
                int wind_speed_idx = wind.getString("speed").indexOf(".");
                wind_speed_str = wind.getString("speed").substring(0,wind_speed_idx+2);  // 소수점 첫째 자리까지만 출력
            } else{
                wind_speed_str = wind.getString("speed");
            }

            humidity_str = main.getString("humidity");
            cloud_str = clouds.getString("all");
            mylocation = jsonObj.getString("name");

            //스레드 뭐시기 오류가 나더라고요 그래서 mainactivity랑 여기에 문장 따로 추가했습니다
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        nowTemp.setText(temp+"°");
                        feeltemp.setText(feel_temp+"°");
                        humidity.setText(humidity_str+"%");
                        cloud.setText(cloud_str+"%");
                        wind_speed.setText(wind_speed_str+"㎧");
                        weatherCode.setText(weatherdesc.getString("id"));
                        String wcode=weatherdesc.getString("id");
                        //날씨 코드별 이미지 가져오기
                        getWeatherIcon(activity, imageView, weatherdesc.getString("id"));
                        // 지역
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void getWeatherList(Activity activity, String adLevel1, String adLevel2, TextView mintemp, TextView maxtemp, String mint, String maxt){
        ArrayList<Weather> timeDataList= new ArrayList<Weather>();
        Weather timeData = null;
        ArrayList<Integer> temp = new ArrayList<Integer>();
        ArrayList<String> tmx = new ArrayList<String>();
        ArrayList<String> tmn = new ArrayList<String>();
        ArrayList<String> sky = new ArrayList<String>();
        ArrayList<String> pty = new ArrayList<String>();
//        ArrayList<String> time = null;

        try {
            int idx = adLevel2.indexOf("시");
            adLevel2 = adLevel2.substring(0,idx+1);
            if(lon<0) {
                lon *= -1;
            } else {
                lon *= 1;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            date2 = df.format(cal.getTime()) ;
            System.out.println("current: " + df.format(cal.getTime()));
            cal.add(Calendar.DATE, -1);
            System.out.println("after: " + df.format(cal.getTime()));
            String date = df.format(cal.getTime()).toString();

            LatXLngY tmp = convertGRID_GPS(TO_GRID, lat, lon);

            String latText = String.valueOf(tmp.x);
            String lonText = String.valueOf(tmp.y);

            int latindex = latText.indexOf(".");
            int lonindex = lonText.indexOf(".");

            latText = latText.substring(0,latindex);
            lonText = lonText.substring(0,lonindex);

            System.out.println("lat : "+ latText);
            System.out.println("lon : "+ lonText);

            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=jMWjbP9YhgZtze0FB7Z53iwbcJQe%2FhlgQeZ%2FgG1bJIjulGTrATjO4xDFruA9Pql8MzR41ldgbX6gHD4dr2Gmww%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
            urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); /*‘21년 6월 28일발표*/
            urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("2300", "UTF-8")); /*05시 발표*/
            urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(latText, "UTF-8")); /*예보지점의 X 좌표값*/
            urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(lonText, "UTF-8")); /*예보지점의 Y 좌표값*/
            System.out.println(urlBuilder.toString());
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            cal.add(Calendar.DATE, 1);
            date = df.format(cal.getTime()).toString();

            JSONObject jsonObj = new JSONObject(sb.toString());
            JSONObject response = (JSONObject) jsonObj.get("response");
            JSONObject body = (JSONObject) response.get("body");

            JSONObject items = (JSONObject) body.get("items");
            JSONArray item = (JSONArray) items.get("item");
            System.out.println(item);
            JSONObject jsonEx;
            //필요한 데이터만 가져오기 위해 먼저 sky, pty, tmep 등을 ArrayList로 선언하고
            //반복을 돌려서 우리가 원하는 조건에 맞다면 ArrayList에 add()
            //시간까지 확인하고 싶지만 그럴 경우 반복문만 16000번 돌아가서 그게 별로인 것 같음
            //800번만 반복을 돌리는 대신 어차피 json에 저장되어 있는 시간 정보 순으로 반복이 돌아가서
            //그것을 믿기로 했다
                for(int i = 0; i < item.length(); i++) {
                    jsonEx = (JSONObject) item.get(i);
                    if (jsonEx.get("fcstDate").equals(date)) {
                        switch (jsonEx.get("category").toString()){
                            case "TMP":
                                temp.add(Integer.parseInt(jsonEx.get("fcstValue").toString()));
                                break;
                            case "SKY":
                                sky.add(jsonEx.get("fcstValue").toString());
                                break;
                            case "PTY":
                                pty.add(jsonEx.get("fcstValue").toString());
                                break;
                            case "TMN":
                                if (jsonEx.get("fcstTime").equals("0600")){
                                    tmn.add(jsonEx.get("fcstValue").toString());
                                }

                                break;
                            case "TMX":
                                if(jsonEx.get("fcstTime").equals("1500")){
                                    tmx.add(jsonEx.get("fcstValue").toString());
                                }

                                break;
                        }
                    }
                }
            System.out.println(Collections.max(temp));
            System.out.println(Collections.min(temp));
            System.out.println(temp);
            mint = Collections.min(temp).toString();
            maxt = Collections.max(temp).toString();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mintemp.setText(Collections.min(temp)+"°");
                    maxtemp.setText(Collections.max(temp)+"°");
                }
            });
            mDatabase = FirebaseDatabase.getInstance().getReference().child("weather").child(adLevel1+" "+adLevel2);
            //데이터를 ArrayList에 다 저장했으니 시간별 날씨 정보를 인자로 TimeDate 객체를 생성하고
            //그 객체를 다시 TimeDate형으로 캐스트한 ArrayList에 add() 후 return
            for (int y = 0; y < times.length; y++){
                System.out.println("온도 " + temp.get(y) + " 강수 " + pty.get(y) + " 하늘 " + sky.get(y) +" 시간 " + times[y]);
                Weather weather = new Weather(times[y], sky.get(y), pty.get(y), temp.get(y).toString());
                mDatabase.child(date2).child(String.valueOf(y)).setValue(weather);
            }


            rd.close();
            conn.disconnect();

        }catch(Exception e) {}
        finally {

        }
    }


    public void getToast(Activity activity, String str){
        Toast.makeText(activity,str,Toast.LENGTH_SHORT).show();
    }

    public void getWeatherIcon(Activity activity, ImageView imageView, String weatherCode){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat df_now = new SimpleDateFormat("HH");
        dateNow = df_now.format(cal.getTime()) ;
        System.out.println("실행 순서 : 3");
        System.out.println(weatherCode);
        if(thunder.contains(weatherCode)) {
            setWeatherIcon(activity, R.raw.thunder, imageView);
        }else if (drizzle.contains(weatherCode)) {
            setWeatherIcon(activity, R.raw.less_rain, imageView);
        }else if (rain.contains(weatherCode)) {
            setWeatherIcon(activity, R.raw.middle_rain, imageView);
        }else if (snow.contains(weatherCode)) {
            setWeatherIcon(activity, R.raw.snow, imageView);
        }else if (Atmosphere.contains(weatherCode)) {

            setWeatherIcon(activity, R.raw.mist, imageView);
        }if(morning.contains(dateNow)){
            if (weatherCode.equals("800")) {
            setWeatherIcon(activity, R.raw.clear, imageView);
        }else if (weatherCode.equals("801")) {
            setWeatherIcon(activity, R.raw.cloud, imageView);
        }else if (weatherCode.equals("802")) {
            setWeatherIcon(activity, R.raw.cloud, imageView);
        }else if (weatherCode.equals("803")) {
            setWeatherIcon(activity, R.raw.cloud, imageView);
        }else if (weatherCode.equals("804")) {
            setWeatherIcon(activity, R.raw.cloud, imageView);
        }
        }else{
            if (weatherCode.equals("800")) {
                setWeatherIcon(activity, R.raw.moon, imageView);
            }else if (weatherCode.equals("801")) {
                setWeatherIcon(activity, R.raw.cloud, imageView);
            }else if (weatherCode.equals("802")) {
                setWeatherIcon(activity, R.raw.cloud, imageView);
            }else if (weatherCode.equals("803")) {
                setWeatherIcon(activity, R.raw.cloud, imageView);
            }else if (weatherCode.equals("804")) {
                setWeatherIcon(activity, R.raw.cloud, imageView);
            }
        }
    }
    public void setWeatherIcon(Activity activity,int resource, ImageView imageView){
        Glide.with(activity)
                .load(resource)
                .override(250,250)
                .into(imageView);
    }

    private LatXLngY convertGRID_GPS(int mode, double lat_X, double lng_Y )
    {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //


        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        if (mode == TO_GRID) {
            rs.lat = lat_X;
            rs.lng = lng_Y;
            double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lng_Y * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        }
        else {
            rs.x = lat_X;
            rs.y = lng_Y;
            double xn = lat_X - XO;
            double yn = ro - lng_Y + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            }
            else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                }
                else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
            rs.lat = alat * RADDEG;
            rs.lng = alon * RADDEG;
        }
        return rs;
    }

    public void getMyAddress(TextView si, TextView gu, TextView dong) {
        try {
            URL url2 = new URL("http://api.vworld.kr/req/address?service=address&request=get" +
                    "Address&key=173F9427-85AF-30BF-808F-DCB8F163058B&point=" +
                    +lon + "," + lat + "&type=PARCEL&format=json\n");
            BufferedReader bf2;
            String line2;
            String result2 = "";

            //날씨 정보를 받아온다.
            bf2 = new BufferedReader(new InputStreamReader(url2.openStream()));

            //버퍼에 있는 정보를 문자열로 변환.
            while ((line2 = bf2.readLine()) != null) {
                result2 = result2.concat(line2 + "\n");
            }

            System.out.println(result2);


            JSONObject jsonObj = new JSONObject(result2);
            JSONObject response = jsonObj.getJSONObject("response");
            JSONArray result3 = response.getJSONArray("result");
            JSONObject structure = result3.getJSONObject(0);

            JSONObject structure2 = structure.getJSONObject("structure");

            String si_str = structure2.getString("level1");
            String gu_str = structure2.getString("level2");
            String dong_str = structure2.getString("level4L");

            si.setText(si_str);
            gu.setText(gu_str);
            dong.setText(dong_str);

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    static class LatXLngY
    {
        public double lat;
        public double lng;

        public double x;
        public double y;

    }


}

