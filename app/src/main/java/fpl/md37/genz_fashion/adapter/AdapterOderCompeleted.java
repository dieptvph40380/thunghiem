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

public class AdapterOderCompeleted extends RecyclerView.Adapter<AdapterOderCompeleted.OrderCompleledViewHolder>{
    Context context;
    private ArrayList<Order> orderList;

    public AdapterOderCompeleted(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderCompleledViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_compeleted, parent, false);
        return new AdapterOderCompeleted.OrderCompleledViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderCompleledViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.timecancle_cp.setText(order.getTimeCancleOrder());

        ArrayList<ProducItem> productList = new ArrayList<>(order.getProducts());
        int totalQuantity = 0;
        for (ProducItem productItem : productList) {
            totalQuantity += productItem.getQuantity();
        }

        holder.total_cp.setText(""+ totalQuantity+" items: "+ order.getTotalAmount());



        if (!productList.isEmpty()) {
            ProductCPAdapter productAdapter = new ProductCPAdapter(productList, context);
            holder.rvProductList_cp.setAdapter(productAdapter);
        } else {
            Log.d("AdapterOderActive", "Product list is empty.");
        }
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }


    public static class OrderCompleledViewHolder extends RecyclerView.ViewHolder {
        TextView timecancle_cp,total_cp;
        RecyclerView rvProductList_cp;

        public OrderCompleledViewHolder(@NonNull View itemView) {
            super(itemView);
            rvProductList_cp = itemView.findViewById(R.id.rvProductList_order_cp);
            total_cp = itemView.findViewById(R.id.total_order_cp);
            timecancle_cp = itemView.findViewById(R.id.timeorder_cp);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            layoutManager.setReverseLayout(true);
            rvProductList_cp.setLayoutManager(layoutManager);

        }
    }
}
