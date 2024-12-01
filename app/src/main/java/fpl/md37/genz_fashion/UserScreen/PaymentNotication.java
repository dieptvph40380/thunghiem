package fpl.md37.genz_fashion.UserScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.genz_fashion.R;

public class PaymentNotication extends AppCompatActivity {
    TextView txtNotication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_notication);
        String test;
        Intent intent = getIntent();
        txtNotication = findViewById(R.id.tv_Notification);
        String result = intent.getStringExtra("result");

        Intent itenIntent = getIntent();
        txtNotication.setText(itenIntent.getStringExtra("result"));
        // Sử dụng Handler để chuyển màn hình sau 2 giây
        new Handler().postDelayed(() -> {
            Intent nextIntent;
            if ("Thanh toán thành công".equals(result)) {
                nextIntent = new Intent(PaymentNotication.this, MyOrderActivity.class); // Màn hình sau khi thanh toán thành công
            } else if ("Hủy thanh toán".equals(result)) {
                nextIntent = new Intent(PaymentNotication.this, CheckOutActivity.class); // Màn hình sau khi hủy thanh toán
            } else {
                nextIntent = new Intent(PaymentNotication.this, MainActivity.class); // Màn hình mặc định
            }
            startActivity(nextIntent);
            finish(); // Đóng PaymentNotication sau khi chuyển màn hình
        }, 2000); // 2 giây
    }
}