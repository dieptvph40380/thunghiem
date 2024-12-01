package fpl.md37.genz_fashion.UserScreen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genz_fashion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fpl.md37.genz_fashion.adapter.AdapterOderActive;
import fpl.md37.genz_fashion.adapter.AdapterOderCanclled;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.handel.Item_Handel_checkOrder;
import fpl.md37.genz_fashion.models.OrderResponse;
import fpl.md37.genz_fashion.models.Order;
import fpl.md37.genz_fashion.models.OrderUpdateRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveFragment extends Fragment implements Item_Handel_checkOrder {
    private HttpRequest httpRequest;
    private RecyclerView recyclerView;
    private AdapterOderActive adapter;
    private ArrayList<Order> orderList = new ArrayList<>();
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvProductList_ac);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterOderActive(orderList, getContext(),this);
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
        int state = 0; // Hoặc bạn có thể thay đổi `state` tùy vào trạng thái bạn muốn

        // Gọi API sử dụng Retrofit
        httpRequest.callApi().getOrders(userId, state).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    OrderResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getOrders() != null && !apiResponse.getOrders().isEmpty()) {
                        ArrayList<Order> orders = apiResponse.getOrders();
                        orderList.clear();
                        orderList.addAll(orders);
                        adapter.notifyDataSetChanged();
                    } else {
                        orderList.clear();
                    }
                    updateUIBasedOnOrders();
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

    @Override
    public void onTrackOrderClick(Order order) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_cancel_order, null);
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();
        Button btnYes = dialogView.findViewById(R.id.btnYes);
        Button btnNo = dialogView.findViewById(R.id.btnNo);
        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            cancelOrder(order);
        });

        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }
    private void cancelOrder(Order order) {
        // Lấy thời gian hiện tại
        String cancelOrderTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(new Date());

        // Tạo đối tượng yêu cầu cập nhật
        OrderUpdateRequest updateRequest = new OrderUpdateRequest(2, cancelOrderTime);
        Log.d("CancelOrder", "Order ID to cancel: " + order.getId());
        Log.d("CancelOrder", "Update Request: State=" + updateRequest.getState() + ", CancelTime=" + cancelOrderTime);
        AlertDialog progressDialog = new AlertDialog.Builder(requireContext())
                .setView(R.layout.layout_progress_dialog)
                .setCancelable(false)
                .create();
        progressDialog.show();

        // Gọi API để cập nhật trạng thái
        httpRequest.callApi().updateOrder(order.getId(), updateRequest).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                   getOrdersData();

                } else {
                    Toast.makeText(requireContext(), "Failed to cancel the order.", Toast.LENGTH_SHORT).show();
                    Log.e("CancelOrder", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error. Try again.", Toast.LENGTH_SHORT).show();
                Log.e("CancelOrder", "Error: " + t.getMessage());
            }
        });
    }
    private void updateUIBasedOnOrders() {
        TextView tvEmptyMessage = getView().findViewById(R.id.tvEmptyMessage);
        RecyclerView rvProductList = getView().findViewById(R.id.rvProductList_ac);

        if (orderList.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            rvProductList.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            rvProductList.setVisibility(View.VISIBLE);
        }
    }

}
