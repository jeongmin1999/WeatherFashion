package kr.ac.yeonsung.giga.weathernfashion.Activities;

import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.SurfaceControl;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import kr.ac.yeonsung.giga.weathernfashion.Fragment.PostFragment;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.HomeFragment;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.MyInfoFragment;
import kr.ac.yeonsung.giga.weathernfashion.R;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 0;
    private NotificationManager mNotificationManager;
    LinearLayout main_ly;
    BottomNavigationView bottom_nav;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final String CHANNEL_ID = user.getUid();
    String str;
    String name;
    String uname = null;
    Boolean state = false;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); //객체 정의
        SettingListener(); //리스너 등록

        //맨 처음 시작할 탭 설정
        bottom_nav.setSelectedItemId(R.id.tab_home);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if (android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O) {
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Test Notification", mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
            NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID);

            mDatabase.child("chatrooms").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
                    if (snapshot.child("alert").getValue() != null) {
                        System.out.println("알림 : "+snapshot.child("alert").getValue());
                        hashMap = (HashMap<String, Boolean>) snapshot.child("alert").getValue();
                        if (hashMap.containsKey(user.getUid()) && hashMap.get(user.getUid())) {
                            mDatabase.child("chatrooms").child(snapshot.getKey()).child("comments").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    for (DataSnapshot data : snapshot1.getChildren()
                                    ) {
                                        System.out.println("메세지 : "+data.child("message").getValue().toString());
                                        str = data.child("message").getValue().toString();
                                        name = data.child("uid").getValue().toString();
                                        notifyBuilder.setContentTitle(message_user(name))// 이 부분에 누가 보낸건지 추가 예정
                                                .setContentText(str)
                                                .setSmallIcon(R.drawable.ic_baseline_chat_24);
                                        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }
                    }
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
    private void init() {
        main_ly = findViewById(R.id.main_ly);
        bottom_nav = findViewById(R.id.bottom_nav);
    }

    private void SettingListener() {
        //선택 리스너 등록
        bottom_nav.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.tab_home: {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.main_ly, new HomeFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_board: {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .addToBackStack(null)
                            .replace(R.id.main_ly, new PostFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_myinfo: {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .addToBackStack(null)
                            .replace(R.id.main_ly, new MyInfoFragment())
                            .commit();
                    return true;
                }
            }

            return false;
        }
    }

    public String message_user(String uid){


        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uname = snapshot.child("user_name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return uname;
    }

}