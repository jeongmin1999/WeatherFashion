package kr.ac.yeonsung.giga.weathernfashion.methods;

import static kr.ac.yeonsung.giga.weathernfashion.methods.API.TO_GRID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.IntSummaryStatistics;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import kr.ac.yeonsung.giga.weathernfashion.VO.TempReply;


public class PostMethods extends Activity {

    public void onBackPresse(Activity activity){
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("뒤로가기");
        alert.setMessage("작성중인 정보가 사라질 수 있습니다");
        alert.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        alert.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
//                        Intent intent = new Intent(activity, HomeFragment.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
//                        startActivity(intent);  //인텐트 이동
//                        finish();   //현재 액티비티 종료
                    }
                });
        alert.show();
    }

    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
//    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private void uploadImageToFirebase(Uri uri) {
        StorageReference fileRef = reference.child("사진아이디"+".jpg");
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // 사진 업로드 성공했을때 액션
                        // 리얼타임 데이터 베이스에 사진경로 저장

                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
            //    Toast.makeText(액티비티명.this,"업로드 완료",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            //    Toast.makeText(액티비티명.this,"업로드 실패",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getAddressApi(float lat_post, float lon_post){
        String address = "";
        try {
            URL url2 = new URL("http://api.vworld.kr/req/address?service=address&request=get" +
                    "Address&key=173F9427-85AF-30BF-808F-DCB8F163058B&point=" +
                    +lon_post + "," + lat_post + "&type=PARCEL&format=json\n");
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

            String str= structure.get("text").toString();
            System.out.println(str);
            if(str.contains("서울")) {
                address = "108";
            }
            else if (str.contains("경기")){address = "108";}else if (str.contains("인천")){address = "112";}
            else if (str.contains("강원")){address = "90";}else if (str.contains("대전")){address = "232";}
            else if (str.contains("부산")){address = "159";}else if (str.contains("대구")){address = "143";}
            else if (str.contains("전주")){address = "146";}else if (str.contains("광주")){address = "156";}
            else if (str.contains("울산")){address = "152";}


        }catch (Exception e){}
        return address;
    }
    public void getWeatherNow(Activity activity, Float lat_post, Float lon_post, String time, TextView temp, TextView mintemp, TextView maxtemp,TextView date){
        ArrayList<Integer> tempList = new ArrayList<Integer>();
        ArrayList<String> tmx = new ArrayList<String>();
        ArrayList<String> tmn = new ArrayList<String>();

        try{
            String timeStr = time;
            timeStr = timeStr.replaceFirst(":", "");
            timeStr = timeStr.replaceFirst(":", "");
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            Calendar cal = Calendar.getInstance();
            Date datePost = df.parse(timeStr.substring(0, 8));
            cal.setTime(datePost);
            String dateStr = df.format(cal.getTime());

            API.LatXLngY latXlngY = convertGRID_GPS(TO_GRID, (double) lat_post, (double) lon_post);
            String latText = String.valueOf(latXlngY.x);
            String lonText = String.valueOf(latXlngY.y);

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
            urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(dateStr, "UTF-8")); /*‘21년 6월 28일발표*/
            urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0200", "UTF-8")); /*05시 발표*/
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
            String dateStr2 = df.format(cal.getTime());

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
                if(jsonEx.get("fcstDate").equals(dateStr2) && jsonEx.get("fcstTime").equals("0000")) {
                    if (jsonEx.get("category").equals("TMP")) {
                        tempList.add(Integer.valueOf(jsonEx.get("fcstValue").toString()));
                    }
                }
                if (jsonEx.get("fcstDate").equals(dateStr)) {
                    if (jsonEx.get("fcstTime").equals("0300") || jsonEx.get("fcstTime").equals("0600") ||
                        jsonEx.get("fcstTime").equals("0900") || jsonEx.get("fcstTime").equals("1200") ||
                        jsonEx.get("fcstTime").equals("1500") || jsonEx.get("fcstTime").equals("1800") ||
                        jsonEx.get("fcstTime").equals("2100")){
                        switch (jsonEx.get("category").toString()){
                            case "TMP":
                                tempList.add(Integer.valueOf(jsonEx.get("fcstValue").toString()));
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
            }
            IntSummaryStatistics statistics = tempList
                    .stream()
                    .mapToInt(num -> num)
                    .summaryStatistics();
            double tempAvg = statistics.getAverage();
            String tempStr = String.valueOf(tempAvg);
            int temp_idx = tempStr.indexOf(".");
            int max_temp_idx = tmx.get(0).indexOf(".");
            int min_temp_idx = tmn.get(0).indexOf(".");

            mintemp.setText(tmn.get(0).substring(0, min_temp_idx));
            mintemp.append("°");
            maxtemp.setText(tmx.get(0).substring(0, max_temp_idx));
            maxtemp.append("°");
            temp.setText(tempStr.substring(0, temp_idx));
            temp.append("°");
            date.setText(time);
            date.setVisibility(View.VISIBLE);
            rd.close();
            conn.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void setPostTempReply(String user_id, String post_id, String content, String user_name, boolean mode){
        String name = user_name;
        LocalDateTime date = LocalDateTime.now();
        String time = date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));
        Long reply_likeCount = 0L;
        TempReply tempReply = new TempReply(content, user_id, time, name, reply_likeCount, mode);
        databaseReference.child("TempReply").child(post_id).push().setValue(tempReply);
        databaseReference.child("TempReply").child(post_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()) {
                    String parentId = snapshot1.getKey();
                    databaseReference.child("TempReply").child(post_id).child(snapshot1.getKey()).child("root").setValue(parentId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setTempReplyComment(String user_id, String post_id, String content, String user_name,
                                 String root_id, String parent_id, boolean mode){
        String name = user_name;
        LocalDateTime date = LocalDateTime.now();
        String time = date.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));
        Long reply_likeCount = 0L;
        TempReply tempReply = new TempReply(content, user_id, time, name, reply_likeCount,
                root_id, parent_id, mode);
        databaseReference.child("TempReply").child(post_id).push().setValue(tempReply);
    }
    public void getWeatherBefore(Activity activity, Float lat_post, Float lon_post, String time, TextView temp, TextView mintemp, TextView maxtemp,TextView date){
        try {

            String timeStr = time;
            timeStr = timeStr.replaceFirst(":", "");
            timeStr = timeStr.replaceFirst(":", "");

            String dateStr = timeStr.substring(0,8);
            String addressCode = getAddressApi(lat_post, lon_post);
            System.out.println(addressCode + " " + dateStr);

            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/AsosDalyInfoService/getWthrDataList"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=Oc76de7i7dgYvE2mefgKF3S3LR1TgmVuSEQFf4LDmbwhYuNLr%2F8%2FijeD%2FOJB6CMY7yDRNyc%2B0lyeb1YEwG%2BqHg%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호 Default : 1*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수 Default : 10*/
            urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default : XML*/
            urlBuilder.append("&" + URLEncoder.encode("dataCd","UTF-8") + "=" + URLEncoder.encode("ASOS", "UTF-8")); /*자료 분류 코드(ASOS)*/
            urlBuilder.append("&" + URLEncoder.encode("dateCd","UTF-8") + "=" + URLEncoder.encode("DAY", "UTF-8")); /*날짜 분류 코드(DAY)*/
            urlBuilder.append("&" + URLEncoder.encode("startDt","UTF-8") + "=" + URLEncoder.encode(dateStr, "UTF-8")); /*조회 기간 시작일(YYYYMMDD)*/
            urlBuilder.append("&" + URLEncoder.encode("endDt","UTF-8") + "=" + URLEncoder.encode(dateStr, "UTF-8")); /*조회 기간 종료일(YYYYMMDD) (전일(D-1)까지 제공)*/
            urlBuilder.append("&" + URLEncoder.encode("stnIds","UTF-8") + "=" + URLEncoder.encode(addressCode, "UTF-8")); /*종관기상관측 지점 번호 (활용가이드 하단 첨부 참조)*/
            URL url3 = new URL(urlBuilder.toString());
            System.out.println(url3);
            HttpURLConnection conn = (HttpURLConnection) url3.openConnection();
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

            JSONObject jsonObj = new JSONObject(rd.readLine());
            JSONObject response1 = (JSONObject) jsonObj.get("response");
            JSONObject body = (JSONObject) response1.get("body");

            JSONObject items = (JSONObject) body.get("items");
            JSONArray item = (JSONArray) items.get("item");
            JSONObject map2 = (JSONObject)item.get(0);
            System.out.println(map2);
            String temp_str;
            String min_temp;
            String max_temp;
//            System.out.println(map);
            if (map2.get("avgTa").toString().contains(".")) {
                int temp_idx = map2.get("avgTa").toString().indexOf(".");
                temp_str = map2.get("avgTa").toString().substring(0,temp_idx);
            } else {
                temp_str = map2.get("avgTa").toString();
            }
            if (map2.get("minTa").toString().contains(".")) {
                int min_temp_idx = map2.get("minTa").toString().indexOf(".");
                min_temp = map2.get("minTa").toString().substring(0,min_temp_idx);  // 소수점 첫째 자리까지만 출력
            } else{
                min_temp = map2.get("minTa").toString();
            }if (map2.get("maxTa").toString().contains(".")) {
                int min_temp_idx = map2.get("maxTa").toString().indexOf(".");
                max_temp = map2.get("maxTa").toString().substring(0,min_temp_idx);  // 소수점 첫째 자리까지만 출력
            } else{
                max_temp = map2.get("minTa").toString();
            }
            mintemp.setText(min_temp);
            mintemp.append("°");
            maxtemp.setText(max_temp);
            maxtemp.append("°");
            temp.setText(temp_str);
            temp.append("°");
            date.setText(time);
            date.setVisibility(View.VISIBLE);

            rd.close();
            conn.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private API.LatXLngY convertGRID_GPS(int mode, double lat_X, double lng_Y )
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
        API.LatXLngY rs = new API.LatXLngY();

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
//
//    private String getFileExtension(Uri uri){
//        ContentResolver cr = getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(cr.getType(uri));
//    }

}
