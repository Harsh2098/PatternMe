package com.hmproductions.patternme;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Harsh Mahajan on 19/7/2017.
 *
 * GridRecyclerAdapter is used to provide adapter for GridLayoutManager
 */

class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.GridViewHolder> {

    private boolean[][] mGrid;
    private Context mContext;
    private int mGridEdge;

    private OnGridCellClickListener mGridCellClickListener;

    interface OnGridCellClickListener {
        void onGridCellClick(int gridCellPosition);
    }

    GridRecyclerAdapter(Context context, boolean[][] grid, int edgeLength, OnGridCellClickListener mClickListener) {
        mGrid = new boolean[mGridEdge][mGridEdge];

        mGrid = grid;
        mContext = context;
        mGridEdge = edgeLength;
        mGridCellClickListener = mClickListener;
    }

    @Override
    public GridRecyclerAdapter.GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View myView;

        switch (mGridEdge) {
            case 3:
            default:
                myView = LayoutInflater.from(mContext).inflate(R.layout.easy_grid_item, parent, false);
                break;

            case 5:
                myView = LayoutInflater.from(mContext).inflate(R.layout.medium_grid_item, parent, false);
                break;

            case 7:
                myView = LayoutInflater.from(mContext).inflate(R.layout.hard_grid_item, parent, false);
                break;
        }
        return new GridViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(GridRecyclerAdapter.GridViewHolder holder, int position) {

        int x = position / mGridEdge;
        int y = position % mGridEdge;

        if(mGrid[x][y])
            holder.view.setBackgroundColor(Color.parseColor("#FF8800"));
        else
            holder.view.setBackgroundColor(Color.parseColor("#000000"));
    }

    @Override
    public int getItemCount() {
        return mGridEdge * mGridEdge;
    }

    void swapData(boolean[][] data)
    {
        mGrid = data;
        notifyDataSetChanged();
    }

    class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View view;

        GridViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mGridCellClickListener.onGridCellClick(getAdapterPosition());
        }
    }


}
