package fpl.md37.genz_fashion.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fpl.md37.genz_fashion.ManagerScreen.ProfileCustomerFragment;
import fpl.md37.genz_fashion.UserScreen.CartFragment;
import fpl.md37.genz_fashion.handel.Item_Handel_selected_voucher;
import fpl.md37.genz_fashion.models.Voucher;

public class AdapterSelectedVoucher extends RecyclerView.Adapter<AdapterSelectedVoucher.ViewHolder> {
    private Context context;
    private ArrayList<Voucher> voucherList;
    private Item_Handel_selected_voucher items;
    private Handler handler = new Handler();

    public AdapterSelectedVoucher(Context context, ArrayList<Voucher> voucherList, Item_Handel_selected_voucher items) {
        this.context = context;
        this.voucherList = voucherList;
        this.items = items;
    }

    @NonNull
    @Override
    public AdapterSelectedVoucher.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_selected_voucher, parent, false);
        return new AdapterSelectedVoucher.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSelectedVoucher.ViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        String imageUrl = voucher.getImage();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http://localhost")) {
                imageUrl = imageUrl.replace("http://localhost", "http://10.0.2.2");
            }
            Log.d("ImageURL", "Image URL: " + imageUrl);
            Glide.with(holder.imageView.getContext())
                    .load(imageUrl)
                    .into(holder.imageView);
        }

        String validFrom = voucher.getValidFrom();
        String validUntil = voucher.getValidUntil();


        long daysDifference = calculateDaysDifference(validFrom, validUntil);
        if (daysDifference <= 0) {
            holder.date.setText("Expired");
        } else {
            holder.date.setText("Expires in " + daysDifference + " days");
        }

        if ("percent".equals(voucher.getDiscountType())) {
            holder.discountValue.setText("Discount voucher " + voucher.getDiscountValue() +"%");
        } else if ("fixed".equals(voucher.getDiscountType())) {
            holder.discountValue.setText("Discount voucher " + voucher.getDiscountValue() +"$");
        } else {
            holder.discountValue.setText("Discount voucher " + voucher.getDiscountValue());
        }

        holder.minimumOrderValue.setText("Applies to orders " + voucher.getMinimumOrderValue());
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        String idvoucher = voucher.getId();
                        if (idvoucher != null) {
                            items.selected_voucher(userId, idvoucher);
                            if (items != null) {
                                items.onVoucherSelected(voucher);
                            }
                        }
                    }
                }
            });
            holder.apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        items.unVoucherDeselected(userId);

                    }
                }
            });
    }



    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    private long calculateDaysDifference(String validFrom, String validUntil) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date startDate = sdf.parse(validFrom);
            Date endDate = sdf.parse(validUntil);
            Date currentDate = new Date();


            if (currentDate.after(endDate)) {
                return 0;
            }

            Date effectiveStartDate = currentDate.before(startDate) ? startDate : currentDate;


            long differenceInMillis = endDate.getTime() - effectiveStartDate.getTime();
            return differenceInMillis / (1000 * 60 * 60 * 24);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void scheduleDailyUpdate() {
        handler.postDelayed(() -> {
            notifyDataSetChanged();
            scheduleDailyUpdate();
        }, 24 * 60 * 60 * 1000);
    }

    public void startDailyUpdate() {
        scheduleDailyUpdate();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        ImageView imageView;
        TextView discountValue, minimumOrderValue, date;
        MaterialButton apply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout=itemView.findViewById(R.id.select_voucher);
            imageView = itemView.findViewById(R.id.ivVoucherImage);
            discountValue = itemView.findViewById(R.id.tvVoucherTitle);
            minimumOrderValue = itemView.findViewById(R.id.tvVoucherMin);
            date = itemView.findViewById(R.id.tvVoucherDate);
            apply = itemView.findViewById(R.id.btn_apply_voucher);
        }
    }
}
