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

public class ProductCLAdapter extends RecyclerView.Adapter<ProductCLAdapter.Product2ViewHolder>{
    private ArrayList<ProducItem> productItemList;
    private Context context;

    public ProductCLAdapter(ArrayList<ProducItem> productItemList, Context context) {
        this.productItemList = productItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public Product2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_product_order_cancle, parent, false);
        return new ProductCLAdapter.Product2ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Product2ViewHolder holder, int position) {
        ProducItem productItem = productItemList.get(position);
        Product product = productItem.getProductId();
        Size size = productItem.getSizeId();

        if (product != null) {
            holder.tvProductName_cl.setText(product.getProduct_name());
            holder.tvProductPrice_cl.setText("$"+String.valueOf(product.getPrice()));


            if (size != null) {
                holder.tvProductSizeQty_cl.setText("Size: " + size.getName() + " | Qty: " + productItem.getQuantity());
            } else {
                holder.tvProductSizeQty_cl.setText("Size: N/A | Qty: " + productItem.getQuantity());
            }
            // Kiểm tra xem có ảnh không trước khi load
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(context).load(product.getImage().get(0)).into(holder.ivProductImage_cl);
            } else {
                Glide.with(context).load(R.drawable.ic_launcher_background).into(holder.ivProductImage_cl); // placeholder nếu không có ảnh
            }
        }
    }

    @Override
    public int getItemCount() {
        return productItemList != null ? productItemList.size() : 0;
    }

    public static class Product2ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName_cl, tvProductPrice_cl, tvProductSizeQty_cl;
        ImageView ivProductImage_cl;

        public Product2ViewHolder(View itemView) {
            super(itemView);
            tvProductName_cl = itemView.findViewById(R.id.tvProductName_cl);
            tvProductPrice_cl = itemView.findViewById(R.id.tvProductPrice_cl);
            tvProductSizeQty_cl = itemView.findViewById(R.id.tvProductSizeQty_cl);
            ivProductImage_cl = itemView.findViewById(R.id.ivProductImage_cl);
        }
    }
}
