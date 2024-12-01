package fpl.md37.genz_fashion.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;

import java.util.ArrayList;

import fpl.md37.genz_fashion.models.ProducItem;
import fpl.md37.genz_fashion.models.Product;
import fpl.md37.genz_fashion.models.Size;

public class ProductCPAdapter extends RecyclerView.Adapter<ProductCPAdapter.ProductCPViewHolder>{
    private ArrayList<ProducItem> productItemList;
    private Context context;

    public ProductCPAdapter(ArrayList<ProducItem> productItemList, Context context) {
        this.productItemList = productItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductCPViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_product_order_compeleted, parent, false);
        return new ProductCPAdapter.ProductCPViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCPViewHolder holder, int position) {
        ProducItem productItem = productItemList.get(position);
        Product product = productItem.getProductId();
        Size size = productItem.getSizeId();

        if (product != null) {
            holder.tvProductName_cp.setText(product.getProduct_name());
            holder.tvProductPrice_cp.setText("$"+String.valueOf(product.getPrice()));


            if (size != null) {
                holder.tvProductSizeQty_cp.setText("Size: " + size.getName() + " | Qty: " + productItem.getQuantity());
            } else {
                holder.tvProductSizeQty_cp.setText("Size: N/A | Qty: " + productItem.getQuantity());
            }
            // Kiểm tra xem có ảnh không trước khi load
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(context).load(product.getImage().get(0)).into(holder.ivProductImage_cp);
            } else {
                Glide.with(context).load(R.drawable.ic_launcher_background).into(holder.ivProductImage_cp); // placeholder nếu không có ảnh
            }
        }
    }

    @Override
    public int getItemCount() {
        return productItemList != null ? productItemList.size() : 0;
    }


    public static class ProductCPViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName_cp, tvProductPrice_cp, tvProductSizeQty_cp;
        ImageView ivProductImage_cp;

        public ProductCPViewHolder(View itemView) {
            super(itemView);
            tvProductName_cp = itemView.findViewById(R.id.tvProductName_cp);
            tvProductPrice_cp = itemView.findViewById(R.id.tvProductPrice_cp);
            tvProductSizeQty_cp = itemView.findViewById(R.id.tvProductSizeQty_cp);
            ivProductImage_cp = itemView.findViewById(R.id.ivProductImage_cp);
        }
    }
}
