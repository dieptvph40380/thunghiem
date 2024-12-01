package fpl.md37.genz_fashion.UserScreen;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.genz_fashion.R;
import com.example.genz_fashion.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fpl.md37.genz_fashion.adapter.AdapterProductUser;
import fpl.md37.genz_fashion.adapter.AdapterTypeProductUser;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.handel.Item_Handel_click;
import fpl.md37.genz_fashion.handel.Item_Handle_MyWishlist;
import fpl.md37.genz_fashion.models.CartData;
import fpl.md37.genz_fashion.models.FavouriteResponseBody;
import fpl.md37.genz_fashion.models.ProducItem;
import fpl.md37.genz_fashion.models.Product;
import fpl.md37.genz_fashion.models.Response;
import fpl.md37.genz_fashion.models.ResponseCart;
import fpl.md37.genz_fashion.models.TypeProduct;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment implements Item_Handel_click, Item_Handle_MyWishlist {

    private ArrayList<TypeProduct> typeProducts;
    private FragmentHomeBinding binding;
    private CountDownTimer countDownTimer;
    private HttpRequest httpRequest;
    private ArrayList<Product> productList = new ArrayList<>();
    private RecyclerView rcv, rcv2;
    private AdapterTypeProductUser adapter;
    private ImageView imgcart;
    private boolean isCartLoaded = false;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcv2 = binding.rvTypes;
        rcv = binding.rvItems;
        httpRequest = new HttpRequest();

        fetchProducts();
        fetchTypeProducts();
        loadCart();
        imgcart=view.findViewById(R.id.cartIcon);
        imgcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
                if (bottomNavigationView != null) {
                    bottomNavigationView.setVisibility(View.GONE);
                }
                CartFragment cartFragment = new CartFragment();

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction(); // Dùng trong Fragment
                // Thay thế Fragment hiện tại trong container (frame_layout) với CartFragment
                transaction.replace(R.id.fragment_container, cartFragment);

                // Nếu muốn giữ lại trạng thái Fragment khi quay lại, thêm vào back stack
                transaction.addToBackStack(null);

                // Commit transaction
                transaction.commit();
            }
        });
        // Search functionality
        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                filterProductsByName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {

                    setupRecyclerView(productList);
                } else {

                    filterProductsByName(newText);
                }
                return true;
            }
        });

        // Initialize image slider
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner3, ScaleTypes.FIT));
        binding.slide.setImageList(slideModels, ScaleTypes.FIT);

        startCountdownTimer(2 * 60 * 60 * 1000); // 2 hours
    }
    Callback<ResponseCart> getCartID = new Callback<ResponseCart>() {
        @Override
        public void onResponse(Call<ResponseCart> call, retrofit2.Response<ResponseCart> response) {
            if (response.isSuccessful()) {
                CartData cartData = response.body().getData();
                List<ProducItem> products = cartData.getProducts();
                updateCartItemCount(products.size());
                isCartLoaded=true;
            }

            else {

            }
        }

        @Override
        public void onFailure(Call<ResponseCart> call, Throwable t) {
            Log.e("zzzzz Failure", "Network error: " + t.getMessage());
        }
    };
    private void loadCart() {
        if (!isCartLoaded) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                httpRequest.callApi().getCart(userId).enqueue(getCartID);
            }
        }
    }
    public void updateCartItemCount(int count) {
        View view = getView(); // Lấy View của Fragment
        if (view != null) {
            TextView cartItemCount = view.findViewById(R.id.cartItemCount);
            if (cartItemCount != null) {
                cartItemCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                cartItemCount.setText(String.valueOf(count));
            }
        }
    }

    private void setupRecyclerView(ArrayList<Product> products) {
        if (products == null) {
            products = new ArrayList<>();
        }
        AdapterProductUser adapter = new AdapterProductUser(getContext(), products, this);
        rcv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rcv.setAdapter(adapter);
    }

    private void setupRecyclerView2(ArrayList<TypeProduct> ds) {
        adapter = new AdapterTypeProductUser(getContext(), ds, this);
        rcv2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcv2.setAdapter(adapter);
    }

    private void filterProductsByName(String query) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        if (productList != null && !query.isEmpty()) {
            for (Product product : productList) {
                if (product.getProduct_name().toLowerCase().contains(query.toLowerCase())) {
                    filteredProducts.add(product);
                }
            }
        }

        if (filteredProducts.isEmpty() && isAdded()) {
            Toast.makeText(requireContext(), "No products found", Toast.LENGTH_SHORT).show();
        }

        setupRecyclerView(filteredProducts);
    }
    private void filterProductsByType(String typeId) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        if (productList != null) { // Kiểm tra productList không null
            for (Product product : productList) {
               // Đảm bảo sử dụng đúng getter
                    if (Objects.equals(product.getTypeProductId(), typeId)) { // Đảm bảo sử dụng đúng getter
                        filteredProducts.add(product);
                    }
                }

            if (filteredProducts.isEmpty()) {
                Toast.makeText(getContext(), "No Product ", Toast.LENGTH_SHORT).show();
            }
            setupRecyclerView(filteredProducts);
        }
    }


    private void fetchTypeProducts() {
        httpRequest.callApi().getAlltypeproduct().enqueue(new Callback<Response<ArrayList<TypeProduct>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<TypeProduct>>> call, retrofit2.Response<Response<ArrayList<TypeProduct>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    typeProducts = response.body().getData();
                    // Thêm mục "All" vào đầu danh sách
                    TypeProduct allTypeProduct = new TypeProduct("all", "All");
                    allTypeProduct.setId("all");
                    allTypeProduct.setName("All");
                    typeProducts.add(0, allTypeProduct);

                    setupRecyclerView2(typeProducts);
                } else {
                    Log.e("FetchTypeProducts", "Failed response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<TypeProduct>>> call, Throwable t) {
                Log.e("FetchTypeProducts", "Error: " + t.getMessage());
            }
        });
    }


    private void fetchProducts() {
        httpRequest.callApi().getAllProducts().enqueue(new Callback<Response<ArrayList<Product>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Product>>> call, retrofit2.Response<Response<ArrayList<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body().getData();
                    setupRecyclerView(productList);
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Error fetching products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Product>>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startCountdownTimer(long duration) {
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int hours = (int) (millisUntilFinished / (1000 * 60 * 60));
                int minutes = (int) (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                int seconds = (int) ((millisUntilFinished % (1000 * 60)) / 1000);

                binding.tvHours.setText(String.format("%02d", hours));
                binding.tvMinutes.setText(String.format("%02d", minutes));
                binding.tvSeconds.setText(String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                binding.tvHours.setText("00");
                binding.tvMinutes.setText("00");
                binding.tvSeconds.setText("00");
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }

    @Override
    public void onTypeProductClick(String typeId) {
        if ("all".equals(typeId)) {
            // Hiển thị tất cả sản phẩm
            setupRecyclerView(productList);
        } else {
            // Lọc sản phẩm theo loại
            filterProductsByType(typeId);
        }
    }


    @Override
    public void addToFavourite(String userId, Product product) {
        FavouriteResponseBody request = new FavouriteResponseBody(userId, product.getId());
        httpRequest.callApi().addToFavourite(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Add to favourite successfully", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(getContext(), "Failed to add to Favourite: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e("API Error", "IOException: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to add to Favourite: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("AddToFavourite", "onFailure: " + t.getMessage());
            }
        });
    }
}