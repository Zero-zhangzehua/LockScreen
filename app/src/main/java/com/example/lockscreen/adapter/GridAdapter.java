package com.example.lockscreen.adapter;

import android.content.Context;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.Cancellable;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.example.lockscreen.R;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView适配器
 */


public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mList;

    public MyItemClickListener mItemClickListener;

    public GridAdapter(Context mContext, List<String> list) {
        mList = list;
        this.mContext = mContext;
    }

    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_staggere_grid_item, parent, false);
        ViewHolder holder = new GridAdapter.ViewHolder(view, mItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GridAdapter.ViewHolder holder, final int position) {
        //holder.mImageView.setAnimation(mList.get(position));
        Cancellable compositionLoader = LottieComposition.Factory.fromAssetFileName(mContext, mList.get(position), new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition composition) {
                //holder.mImageView.setAnimation(mList.get(position));
                holder.mImageView.setComposition(composition);
                holder.mImageView.playAnimation();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MyItemClickListener mListener;
        private LottieAnimationView mImageView;

        public ViewHolder(View itemView, MyItemClickListener myItemClickListener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv);
            //将全局的监听赋值给接口
            this.mListener = myItemClickListener;
            mImageView.setOnClickListener(this);
        }

        /**
         * 实现OnClickListener接口重写的方法
         */
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getLayoutPosition());
            }
        }
    }

    /**
     * 添加点击事件监听
     */
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 将点击事件监听传递过来,并赋值给全局的监听
     */
    public void setItemClickListener(MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }

}