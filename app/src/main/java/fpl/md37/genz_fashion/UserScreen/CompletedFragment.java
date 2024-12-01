package fpl.md37.genz_fashion.UserScreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.genz_fashion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import fpl.md37.genz_fashion.adapter.AdapterOderCanclled;
import fpl.md37.genz_fashion.adapter.AdapterOderCompeleted;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.models.Order;
import fpl.md37.genz_fashion.models.OrderResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletedFragment extends Fragment {
    private HttpRequest httpRequest;
    private RecyclerView recyclerView;
    private AdapterOderCompeleted adapter;
    private ArrayList<Order> orderList = new ArrayList<>();
    private String userId;
    private TextView tvEmptyMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvEmptyMessage = view.findViewById(R.id.tvNoOrders);
        recyclerView = view.findViewById(R.id.rvProductList_cp);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterOderCompeleted(getContext(), orderList);
        recyclerView.setAdapter(adapter);

        // Đảm bảo đã khởi tạo httpRequest và FirebaseAuth
        if (httpRequest == null) {
            httpRequest = new HttpRequest();
        }

        getOrdersData();
    }
    private void getOrdersData() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Nếu người dùng chưa đăng nhập, thông báo cho họ và kết thúc hàm
            Log.d("OrderDetails", "User not logged in");
            return;
        }

        userId = currentUser.getUid();
        int state = 1;

        // Gọi API sử dụng Retrofit
        httpRequest.callApi().getOrders(userId, state).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    OrderResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getOrders() != null) {
                        orderList.clear();
                        orderList.addAll(apiResponse.getOrders());
                    } else {
                        orderList.clear(); // Không có đơn hàng
                    }
                } else {
                    Log.d("OrderDetails", "Error: " + response.message());
                    orderList.clear(); // Trong trường hợp lỗi, coi như không có đơn hàng
                }

                adapter.notifyDataSetChanged();
                updateUIBasedOnOrders(); // Cập nhật giao diện sau khi tải dữ liệu
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.d("OrderDetails", "Error: " + t.getMessage());
            }
        });
    }
    private void updateUIBasedOnOrders() {
        if (orderList.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
