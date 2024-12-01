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

import com.example.genz_fashion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import fpl.md37.genz_fashion.adapter.AdapterOderActive;
import fpl.md37.genz_fashion.adapter.AdapterOderCanclled;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.models.Order;
import fpl.md37.genz_fashion.models.OrderResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelledFragment extends Fragment {
    private HttpRequest httpRequest;
    private RecyclerView recyclerView;
    private AdapterOderCanclled adapter;
    private ArrayList<Order> orderList = new ArrayList<>();
    private String userId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cancelled, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvProductList_cl);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterOderCanclled(getContext(), orderList);
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
        int state = 2; // Hoặc bạn có thể thay đổi `state` tùy vào trạng thái bạn muốn

        // Gọi API sử dụng Retrofit
        httpRequest.callApi().getOrders(userId, state).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    OrderResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getOrders() != null && !apiResponse.getOrders().isEmpty()) {
                        ArrayList<Order> orders = apiResponse.getOrders();
                        Log.d("OrderDetails", "Received orders: " + orders.size());
                        Log.d("OrderDetails", "API Response: " + apiResponse.toString());
                        Log.d("OrderDetails", "Received orders: " + orders.size());
                        for (Order order : orders) {
                            Log.d("OrderDetails", "Order ID: " + order.getId() + " | Total Amount: " + order.getTotalAmount());
                        }
                        orderList.clear();
                        orderList.addAll(orders);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("OrderDetails", "No orders found.");
                    }
                } else {
                    Log.d("OrderDetails", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.d("OrderDetails", "Error: " + t.getMessage());
            }
        });
    }
}
