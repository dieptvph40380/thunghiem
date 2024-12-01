package fpl.md37.genz_fashion.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genz_fashion.R;

import java.util.ArrayList;

import fpl.md37.genz_fashion.models.Order;
import fpl.md37.genz_fashion.models.ProducItem;

public class AdapterOderCanclled extends RecyclerView.Adapter<AdapterOderCanclled.OrderCanclleViewHolder>{
     Context context;
    private ArrayList<Order> orderList;

    public AdapterOderCanclled(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderCanclleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_cancel, parent, false);
        return new AdapterOderCanclled.OrderCanclleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderCanclleViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.timecancle_cl.setText(order.getTimeCancleOrder());

        ArrayList<ProducItem> productList = new ArrayList<>(order.getProducts());
        int totalQuantity = 0;
        for (ProducItem productItem : productList) {
            totalQuantity += productItem.getQuantity();
        }

        holder.total_cl.setText(""+ totalQuantity+" items: "+ order.getTotalAmount());



        if (!productList.isEmpty()) {
            ProductCLAdapter productAdapter = new ProductCLAdapter(productList, context);
            holder.rvProductList_cl.setAdapter(productAdapter);
        } else {
            Log.d("AdapterOderActive", "Product list is empty.");
        }
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    public static class OrderCanclleViewHolder extends RecyclerView.ViewHolder {
        TextView timecancle_cl,total_cl;
        RecyclerView rvProductList_cl;

        public OrderCanclleViewHolder(@NonNull View itemView) {
            super(itemView);
            rvProductList_cl = itemView.findViewById(R.id.rvProductList_order_cl);
            total_cl = itemView.findViewById(R.id.total_order_cl);
            timecancle_cl = itemView.findViewById(R.id.timeorder_cl);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            layoutManager.setReverseLayout(true);
            rvProductList_cl.setLayoutManager(layoutManager);

        }
    }
}
