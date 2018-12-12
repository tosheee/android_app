package com.example.todorchakarov.myapplication.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.todorchakarov.myapplication.R;
import com.example.todorchakarov.myapplication.interfaces.ItemClickListener;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

public class ItemViewHolder extends RecyclerView.ViewHolder{

    public TextView txt_item_text, txt_child_item_text;
    public RelativeLayout button;
    public ExpandableLinearLayout expandableLinearLayout;

    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public ItemViewHolder(@NonNull View itemView, boolean isExpandable){
        super(itemView);

        if(isExpandable){
            txt_item_text = (TextView)itemView.findViewById(R.id.txt_item_text);
            txt_child_item_text = (TextView)itemView.findViewById(R.id.txt_child_item_text);
            button = (RelativeLayout)itemView.findViewById(R.id.button);
            expandableLinearLayout = (ExpandableLinearLayout)itemView.findViewById(R.id.expandableLayout);

        }else{
            txt_item_text = (TextView)itemView.findViewById(R.id.txt_item_text);
        }


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onClick(view, getAdapterPosition());
            }
        });
    }
}
