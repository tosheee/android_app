package com.example.todorchakarov.myapplication;

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.todorchakarov.myapplication.Model.Item;
import com.example.todorchakarov.myapplication.ViewHolder.ItemViewHolder;
import com.example.todorchakarov.myapplication.interfaces.ItemClickListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Item> items = new ArrayList();
    FirebaseRecyclerAdapter<Item, ItemViewHolder> adapter;

    SparseBooleanArray expandState = new SparseBooleanArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init View
        recyclerView = (RecyclerView) findViewById(R.id.list_item);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // retrieve data
        retriveData();

        //set data
        setData();
    }


    private void setData() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Items");
        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(options) {

            @Override
            public int getItemViewType(int position) {
                // return super.getItemViewType(position);
                if(items.get(position).isExpandable())
                    return 1;
                else
                    return 0;
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, final int position, @NonNull final Item model) {

                switch(holder.getItemViewType())
                {
                    case 0: //without item
                    {
                        ItemViewHolder viewHolder = (ItemViewHolder)holder;
                        viewHolder.setIsRecyclable(false);
                        viewHolder.txt_item_text.setText(model.getText());

                        //event
                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Toast.makeText(MainActivity.this, "" + items.get(position).getText(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                    case 1: //with item
                    {
                        final ItemViewHolder viewHolder = (ItemViewHolder)holder;
                        viewHolder.setIsRecyclable(false);
                        viewHolder.txt_item_text.setText(model.getText());

                        //Because we use Recycler View so we need use Expandable Linear Layout
                        viewHolder.expandableLinearLayout.setInRecyclerView(true);
                        viewHolder.expandableLinearLayout.setExpanded(expandState.get(position));

                        viewHolder.expandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter() {

                            @Override
                            public void onPreOpen(){
                                changeRotate(viewHolder.button, 0f, 180f);
                                expandState.put(position, true);
                            }

                            @Override
                            public void onPreClose(){
                                changeRotate(viewHolder.button, 180f, 0f);
                                expandState.put(position, false);
                            }

                        });

                        viewHolder.button.setRotation(expandState.get(position)?180f:0f);
                        viewHolder.button.setOnClickListener(new View.OnClickListener(){


                            @Override
                            public void onClick(View v) {
                                viewHolder.expandableLinearLayout.toggle();
                            }
                        });

                        viewHolder.txt_child_item_text.setText(model.getText());
                        viewHolder.txt_child_item_text.setOnClickListener(new View.OnClickListener(){

                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "" + viewHolder.txt_child_item_text.getText(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        //set event
                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Toast.makeText(MainActivity.this, ""+model.getText(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                    default:
                        break;
                }
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                if(viewType == 0)  // Without item
                {
                    View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_without_child, viewGroup, false);
                    return new ItemViewHolder(itemView, viewType == 1); // false
                }
                else // with item
                {
                    View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_with_child, viewGroup, false);
                    return new ItemViewHolder(itemView, viewType == 1); // true
                }
            }
        };

        // Init SparseArray, all view is no expandable

        expandState.clear();
        for(int i = 0; i < items.size(); i++)
            expandState.append(i, false);

        recyclerView.setAdapter(adapter);
    }

    private Object changeRotate(RelativeLayout button, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(button, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    private void retriveData(){
        items.clear();

        DatabaseReference db = FirebaseDatabase.getInstance()
        .getReference()
        .child("Items");

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                {
                    Item item = itemSnapShot.getValue(Item.class);
                    items.add(item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", "" + databaseError.getMessage());
            }
        });
    }

    // ctrl+o

    @Override
    protected void onStart() {
        if(adapter != null)
            adapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }
}
