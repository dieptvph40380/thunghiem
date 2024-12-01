package fpl.md37.genz_fashion.UserScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.genz_fashion.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import fpl.md37.genz_fashion.adapter.AdapterProduct;
import fpl.md37.genz_fashion.adapter.AdapterSelectedVoucher;
import fpl.md37.genz_fashion.adapter.AdapterVoucher;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.handel.Item_Handel_selected_voucher;
import fpl.md37.genz_fashion.models.CartData;
import fpl.md37.genz_fashion.models.ProducItem;
import fpl.md37.genz_fashion.models.RemoveFavouriteRequest;
import fpl.md37.genz_fashion.models.Response;
import fpl.md37.genz_fashion.models.ResponseCart;
import fpl.md37.genz_fashion.models.Voucher;
import fpl.md37.genz_fashion.models.VoucherRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class SelectedVoucherFragment extends Fragment implements Item_Handel_selected_voucher {
ImageView btnback;
AdapterSelectedVoucher adapter;
RecyclerView recyclerView;
HttpRequest httpRequest;
AdapterProduct adapterProduct;
 ArrayList<Voucher> listvoucher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_voucher, container, false);
        btnback=view.findViewById(R.id.btnBack_voucher);
        recyclerView=view.findViewById(R.id.rvVoucherList);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new CartFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.frameLayout_voucher, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        httpRequest=new HttpRequest();
        fetchVoucher();
        return view;
    }
    private void fetchVoucher() {
        httpRequest.callApi().getAllVoucher().enqueue(new Callback<Response<ArrayList<Voucher>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Voucher>>> call, retrofit2.Response<Response<ArrayList<Voucher>>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        listvoucher = response.body().getData();
                        setupRecyclerView(listvoucher);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Voucher>>> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
    private void setupRecyclerView(ArrayList<Voucher> ds) {
        adapter = new AdapterSelectedVoucher(getContext(), ds,this);
        adapter.startDailyUpdate();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void selected_voucher(String userId, String voucherId) {
        VoucherRequest request = new VoucherRequest(userId, voucherId);
        httpRequest.callApi().selectVoucher(request).enqueue(new Callback<ResponseCart>() {
            @Override
            public void onResponse(Call<ResponseCart> call, retrofit2.Response<ResponseCart> response) {
                if (response.isSuccessful()) {

                } else {
                    Log.e("MyCart", "Error removing product: " + response.code());
                    Toast.makeText(getContext(), "Failed to remove product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseCart> call, Throwable t) {
                Log.e("MyCart", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Selected Voucher successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
        public void onVoucherSelected(Voucher voucher) {
            String voucherName = voucher.getName();
            Log.d("SelectedVoucher", "Voucher Name: " + voucherName);

            // Lưu voucherName vào SharedPreferences
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("VoucherPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("voucher_name", voucherName);  // Lưu tên voucher với key là "voucher_name"
            editor.apply();  // Lưu dữ liệu

            // Tiến hành chuyển Fragment
            CartFragment cartFragment = new CartFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", voucherName); // Truyền tên voucher vào Bundle
            cartFragment.setArguments(bundle);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout_voucher, cartFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }


    @Override
    public void unselected_voucher(String userId) {
        VoucherRequest request = new VoucherRequest(userId);
        httpRequest.callApi().unselectVoucher(request).enqueue(new Callback<ResponseCart>() {
            @Override
            public void onResponse(Call<ResponseCart> call, retrofit2.Response<ResponseCart> response) {
                if (response.isSuccessful()) {

                } else {
                    Log.e("MyCart", "Error removing product: " + response.code());
                    Toast.makeText(getContext(), "Failed to remove product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseCart> call, Throwable t) {
                Log.e("MyCart", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Unselected Voucher ", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void unVoucherDeselected(String userId) {
        // Ghi log thông báo bỏ chọn voucher
        Log.d("DeselectedVoucher", "Voucher deselected");

        // Xóa tên voucher trong SharedPreferences (cập nhật lại trạng thái)
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("VoucherPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("voucher_name", "Select Vouchers");
        editor.apply();


        unselected_voucher(userId);


        CartFragment cartFragment = new CartFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", "No voucher selected");
        cartFragment.setArguments(bundle);

        // Thực hiện giao diện lại fragment sau khi bỏ chọn voucher
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_voucher, cartFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



}