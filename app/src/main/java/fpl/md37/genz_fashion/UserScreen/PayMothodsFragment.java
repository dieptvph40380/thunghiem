package fpl.md37.genz_fashion.UserScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.genz_fashion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PayMothodsFragment extends AppCompatActivity {

    private ImageView btnBack_payment;

    private TextView tvZalo,tvMomo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pay_mothods);

        btnBack_payment = findViewById(R.id.btnBack_payment);
        tvZalo=findViewById(R.id.tv_zalopay);
        tvMomo=findViewById(R.id.tv_momo);

        btnBack_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showBottomNav();
                // Hiển thị lại BottomNavigationView
                // Quay lại CheckOutActivity
                Intent intent = new Intent(PayMothodsFragment.this, CheckOutActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Đảm bảo không lưu stack
                startActivity(intent);
                finish();
            }
        });

        tvZalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayMothods(view, "ZaloPay");
            }
        });
        tvMomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayMothods(view, "MoMo");
            }
        });


    }

    private void showBottomNav() {
        BottomNavigationView bottomNavigationView =findViewById(R.id.bottom_nav);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    public void PayMothods(View view, String method) {

        // Tạo Intent trả kết quả về
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_method", method);  // Gửi phương thức thanh toán
        setResult(Activity.RESULT_OK, resultIntent);  // Trả kết quả về Activity gọi
        finish();

    }


}