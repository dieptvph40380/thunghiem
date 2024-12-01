package fpl.md37.genz_fashion.UserScreen;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContentProviderCompat.requireContext;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpl.md37.genz_fashion.adapter.ImageSliderAdapter;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.models.CartData;
import fpl.md37.genz_fashion.models.CartResponseBody;
import fpl.md37.genz_fashion.models.FavouriteResponseBody;
import fpl.md37.genz_fashion.models.ProducItem;
import fpl.md37.genz_fashion.models.Product;
import fpl.md37.genz_fashion.models.RemoveFavouriteRequest;
import fpl.md37.genz_fashion.models.Response;
import fpl.md37.genz_fashion.models.ResponseCart;
import fpl.md37.genz_fashion.models.Size;
import fpl.md37.genz_fashion.models.SizeQuantity;
import fpl.md37.genz_fashion.models.TypeProduct;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DetailUser extends AppCompatActivity {

    private ImageSliderAdapter imageSliderAdapter;
    private ImageView backArrow,heart;
    private ViewPager2 productImagePlaceholder;
    private TextView productName, productPrice, productDescription;
    private Product product;
    private HttpRequest httpRequest = new HttpRequest();
    private Map<String, String> sizeIdMap = new HashMap<>();  // Lưu trữ id của các size
    private boolean isFavorite; // Biến này sẽ lưu trạng thái yêu thích
    private SharedPreferences sharedPreferences; // SharedPreferences để lưu trạng thái yêu thích
    private ImageView imgcart;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            httpRequest.callApi().getCart(userId).enqueue(getCartID);
        }
        imgcart=findViewById(R.id.cartIcon);
        imgcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartFragment cartFragment = new CartFragment();

                // Lấy FragmentTransaction để thay thế Fragment trong Activity
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Thay thế Fragment hiện tại trong container (frame_layout) với CartFragment
                transaction.replace(R.id.fragment_container, cartFragment);

                // Nếu muốn giữ lại trạng thái Fragment khi quay lại, thêm vào back stack
                transaction.addToBackStack(null);

                // Commit transaction
                transaction.commit();
            }
        });
        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);

        // Ánh xạ các view trong layout
        backArrow = findViewById(R.id.backArrow);
        productImagePlaceholder = findViewById(R.id.productImagePlaceholder);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);

        heart=findViewById(R.id.ImgHeart);

        heart.setOnClickListener(view -> {


            if (currentUser != null) {
                String userId = currentUser.getUid();

                if (isFavorite) {
                    // Nếu đang yêu thích, xóa khỏi danh sách
                    removeFromFavorite(userId, product);
                    heart.setImageResource(R.drawable.heart1); // Đổi icon thành outline
                    isFavorite = false; // Cập nhật trạng thái yêu thích
                } else {
                    // Nếu chưa yêu thích, thêm vào danh sách
                    addToFavorite(userId, product);
                    heart.setImageResource(R.drawable.heart); // Đổi icon thành filled
                    isFavorite = true; // Cập nhật trạng thái yêu thích
                }
                // Lưu trạng thái yêu thích vào SharedPreferences
                saveFavoriteState(isFavorite);
            } else {
                Toast.makeText(DetailUser.this, "Vui lòng đăng nhập để thêm yêu thích!", Toast.LENGTH_SHORT).show();
            }
        });



        // Nút thêm vào giỏ hàng và mua ngay
        LinearLayout addToCartButton = findViewById(R.id.addToCart);
//        LinearLayout buyNowButton = findViewById(R.id.addBuyNow);

        addToCartButton.setOnClickListener(v -> showBottomSheet("Add to Cart"));
//        buyNowButton.setOnClickListener(v -> showBottomSheet("Buy Now"));

        // Nhận dữ liệu sản phẩm từ Intent
        product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            updateProductDetails(product);
            backArrow.setOnClickListener(v -> onBackPressed());
        } else {
            Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT).show();
        }
    }


    Callback<ResponseCart> getCartID = new Callback<ResponseCart>() {
        @Override
        public void onResponse(Call<ResponseCart> call, retrofit2.Response<ResponseCart> response) {
            if (response.isSuccessful()) {
                CartData cartData = response.body().getData();
                List<ProducItem> products = cartData.getProducts();
                updateCartItemCount(products.size());
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    httpRequest.callApi().getCart(userId).enqueue(getCartID);
                }
            }

            else {

            }
        }

        @Override
        public void onFailure(Call<ResponseCart> call, Throwable t) {
            Log.e("zzzzz Failure", "Network error: " + t.getMessage());
        }
    };
    private void updateCartItemCount(int itemCount) {
        TextView cartItemCountTextView = findViewById(R.id.cartItemCount);
        // Cập nhật số lượng sản phẩm
        cartItemCountTextView.setText(String.valueOf(itemCount));

        // Nếu số lượng = 0, ẩn số lượng
        if (itemCount > 0) {
            cartItemCountTextView.setVisibility(View.VISIBLE);
        } else {
            cartItemCountTextView.setVisibility(View.GONE);
        }
    }
    private void updateProductDetails(Product product) {
        if (product != null) {
            List<String> imageUrls = product.getImage();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                imageSliderAdapter = new ImageSliderAdapter(this, imageUrls);
                productImagePlaceholder.setAdapter(imageSliderAdapter);
            }

            productName.setText(TextUtils.isEmpty(product.getProduct_name()) ? "Unknown Product" : product.getProduct_name());
            productPrice.setText(TextUtils.isEmpty(product.getPrice()) ? "Price Not Available" : product.getPrice());
            productDescription.setText(TextUtils.isEmpty(product.getDescription()) ? "No description available." : product.getDescription());

            // Đọc trạng thái yêu thích từ SharedPreferences
            boolean isFavorite = sharedPreferences.getBoolean("isFavorite_" + product.getId(), false);  // Mặc định là false nếu không tìm thấy
            if (isFavorite) {
                heart.setImageResource(R.drawable.heart); // Đổi icon thành filled
            } else {
                heart.setImageResource(R.drawable.heart1); // Đổi icon thành outline
            }
            // Lấy và cập nhật danh sách kích thước từ API
            loadSizesFromApi();
        }
    }

    private void loadSizesFromApi() {
        httpRequest.callApi().getTypeProductById(product.getTypeProductId()).enqueue(new Callback<Response<TypeProduct>>() {

            @Override
            public void onResponse(Call<Response<TypeProduct>> call, retrofit2.Response<Response<TypeProduct>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    TypeProduct typeProduct = response.body().getData();
                    if (typeProduct != null && typeProduct.getSizes() != null) {
                        // Cập nhật sizeIdMap từ danh sách sizes
                        for (Size size : typeProduct.getSizes()) {
                            sizeIdMap.put(size.getName(), size.getId());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<TypeProduct>> call, Throwable t) {
                Log.e("API Failure", "Error: " + t.getMessage());
            }
        });
    }

    private void showBottomSheet(String action) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Inflate layout cho BottomSheet
        LinearLayout sheetLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_selected, null);

        // Gắn dữ liệu vào các thành phần trong BottomSheet
        ImageView imgProduct = sheetLayout.findViewById(R.id.imgProduct);
        TextView tvProductPrice = sheetLayout.findViewById(R.id.tvProductPrice);
        TextView tvProductStock = sheetLayout.findViewById(R.id.tvProductStock);
        ChipGroup sizeOptions = sheetLayout.findViewById(R.id.chipGroupSizes);
        TextView tvQuantity = sheetLayout.findViewById(R.id.tvQuantity);
        TextView btnDecrease = sheetLayout.findViewById(R.id.btnDecreaseQuantity); // Là TextView
        TextView btnIncrease = sheetLayout.findViewById(R.id.btnIncreaseQuantity); // Là TextView

        // Biến lưu trữ số lượng hiện tại và tối đa
        final int[] currentQuantity = {1};
        final int[] maxQuantity = {0};

        // Lấy userId
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user != null ? user.getUid() : null;

        if (product != null) {
            String imageUrl = product.getImage() != null && !product.getImage().isEmpty() ? product.getImage().get(0) : "";
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(imgProduct);
            } else {
                imgProduct.setImageResource(R.drawable.shark); // Placeholder image
            }

            tvProductPrice.setText(TextUtils.isEmpty(product.getPrice()) ? "Price Not Available" : product.getPrice());

            // Cập nhật các size vào ChipGroup
            sizeOptions.removeAllViews();
            for (String sizeName : sizeIdMap.keySet()) {
                Chip chip = new Chip(this);
                chip.setText(sizeName);
                chip.setCheckable(true); // Cho phép chọn size
                chip.setOnClickListener(v -> {
                    // Bỏ chọn tất cả các chip khác
                    for (int i = 0; i < sizeOptions.getChildCount(); i++) {
                        Chip otherChip = (Chip) sizeOptions.getChildAt(i);
                        if (otherChip != chip) {
                            otherChip.setChecked(false);
                        }
                    }

                    // Cập nhật số lượng tối đa và hiện tại
                    String selectedSize = chip.getText().toString();
                    maxQuantity[0] = getAvailableQuantity(selectedSize, product.getSizeQuantities());
                    tvProductStock.setText("Still: " + maxQuantity[0]);
                    currentQuantity[0] = 1; // Reset số lượng về 1 khi chọn size mới
                    tvQuantity.setText(String.valueOf(currentQuantity[0]));
                });

                sizeOptions.addView(chip);
            }
        }

        // Xử lý nút tăng/giảm số lượng
        btnIncrease.setOnClickListener(v -> {
            if (currentQuantity[0] < maxQuantity[0]) {
                currentQuantity[0]++;
                tvQuantity.setText(String.valueOf(currentQuantity[0]));
            } else {
                Toast.makeText(this, "Đã đạt số lượng tối đa!", Toast.LENGTH_SHORT).show();
            }
        });

        btnDecrease.setOnClickListener(v -> {
            if (currentQuantity[0] > 1) {
                currentQuantity[0]--;
                tvQuantity.setText(String.valueOf(currentQuantity[0]));
            } else {
                Toast.makeText(this, "Số lượng tối thiểu là 1!", Toast.LENGTH_SHORT).show();
            }
        });

        // Gắn layout vào BottomSheetDialog và hiển thị
        bottomSheetDialog.setContentView(sheetLayout);
        bottomSheetDialog.show();

        // Thêm sản phẩm vào giỏ hàng khi nhấn "Add to Cart"
        sheetLayout.findViewById(R.id.btnBuyNow).setOnClickListener(v -> {
            if (userId != null) {
                // Gọi API thêm vào giỏ hàng với thông tin sản phẩm và userId
                addToCart(userId, product, sizeOptions, currentQuantity[0]);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart(String userId, Product product, ChipGroup sizeOptions, int quantity) {
        String selectedSize = null;
        for (int i = 0; i < sizeOptions.getChildCount(); i++) {
            Chip chip = (Chip) sizeOptions.getChildAt(i);
            if (chip.isChecked()) {
                selectedSize = chip.getText().toString();
                break;
            }
        }

        if (selectedSize != null) {
            String sizeId = sizeIdMap.get(selectedSize);
            if (sizeId != null) {
                // Tạo đối tượng CartResponseBody với thông tin giỏ hàng
                CartResponseBody cartResponseBody = new CartResponseBody(userId, product.getId(), sizeId, quantity);

                // Gọi API thêm vào giỏ hàng với đối tượng CartResponseBody
                httpRequest.callApi().addToCart(cartResponseBody).enqueue(new Callback<ResponseBody>() {


                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            // Xử lý khi thêm vào giỏ hàng thành công
                            Toast.makeText(DetailUser.this, "Added to cart successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                // Xử lý khi thêm vào giỏ hàng không thành công
                                String errorBody = response.errorBody().string(); // Get error message from error body
                                Log.e("API Error", "Error message: " + errorBody);
                                Toast.makeText(DetailUser.this, "Failed to add to cart: " + errorBody, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Log.e("API Error", "IOException: " + e.getMessage());
                                Toast.makeText(DetailUser.this, "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Xử lý khi gọi API thất bại (ví dụ: lỗi mạng)
                        Log.d(".....", "onFailure: " + t.getMessage());
                        Toast.makeText(DetailUser.this, "Added to no cart successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Vui lòng chọn size!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Vui lòng chọn size!", Toast.LENGTH_SHORT).show();
        }
    }



    private int getAvailableQuantity(String selectedSize, List<SizeQuantity> sizeQuantities) {
        if (sizeQuantities == null || sizeIdMap.get(selectedSize) == null) {
            Log.d("SizeQuantity", "SizeQuantities hoặc SizeIdMap không hợp lệ.");
            return 0;
        }

        String sizeId = sizeIdMap.get(selectedSize);
        for (SizeQuantity sq : sizeQuantities) {
            if (sizeId.equals(sq.getSizeId())) {
                try {
                    return Integer.parseInt(sq.getQuantity());
                } catch (NumberFormatException e) {
                    Log.e("SizeQuantity", "Số lượng không hợp lệ: " + sq.getQuantity());
                }
            }
        }
        return 0;
    }

    private void addToFavorite(String userId, Product product) {
        FavouriteResponseBody requestBody = new FavouriteResponseBody(userId, product.getId());

        httpRequest.callApi().addToFavourite(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetailUser.this, "Đã thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailUser.this, "Không thể thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DetailUser.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromFavorite(String userId, Product product) {
        RemoveFavouriteRequest requestBody = new RemoveFavouriteRequest(userId, product.getId());

        httpRequest.callApi().removeFavourite(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetailUser.this, "Đã xóa khỏi yêu thích!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailUser.this, "Không thể xóa khỏi yêu thích!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DetailUser.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveFavoriteState(boolean isFavorite) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFavorite_" + product.getId(), isFavorite); // Lưu trạng thái yêu thích của sản phẩm theo id
        editor.apply();
    }






}
