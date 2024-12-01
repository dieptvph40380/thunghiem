package fpl.md37.genz_fashion.UserScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import fpl.md37.genz_fashion.ManagerScreen.SignInActivity;
import fpl.md37.genz_fashion.ManagerScreen.SignUpActivity;
import fpl.md37.genz_fashion.adapter.ImagePagerAdapter;
import com.example.genz_fashion.R;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ImagePagerAdapter adapter;
    private Handler handler;
    private Runnable runnable;
    private int currentPage = 0;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_activity);

        // Tìm View trong layout
        viewPager = findViewById(R.id.viewPager);
        btnStart = findViewById(R.id.startButton_Wcl);

        // Danh sách các ảnh
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.a1c); // Thay bằng ảnh trong drawable
        imageList.add(R.drawable.a2c);
        imageList.add(R.drawable.a3c);
        imageList.add(R.drawable.a4c);
        imageList.add(R.drawable.a5c);
        imageList.add(R.drawable.a6c);

        adapter = new ImagePagerAdapter(this, imageList);
        viewPager.setAdapter(adapter);

        autoScrollViewPager(imageList.size());

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void autoScrollViewPager(int totalPages) {
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                currentPage = (currentPage + 1) % totalPages;
                viewPager.setCurrentItem(currentPage, true);
                handler.postDelayed(this, 2000); // Lặp lại
            }
        };
        handler.postDelayed(runnable, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable); // Ngừng Handler khi Activity bị hủy
        }
    }
}
