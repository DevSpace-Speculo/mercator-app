package com.speculo.mercator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.speculo.mercator.R;
import com.speculo.mercator.models.ItemsModel;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<ItemsModel> itemsModelList;

    public CartAdapter(List<ItemsModel> itemsModelList) {
        this.itemsModelList = itemsModelList;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        holder.name.setText(itemsModelList.get(position).getProduct_name());
        holder.desc.setText(itemsModelList.get(position).getProduct_description());
        holder.price.setText("₹ " + itemsModelList.get(position).getProduct_price());

        Glide.with(holder.itemView.getContext())
                .load(itemsModelList.get(position).getProduct_image())
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemsModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, price, desc;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            desc = itemView.findViewById(R.id.product_desc);
            imageView = itemView.findViewById(R.id.product_image);
        }
    }
}
