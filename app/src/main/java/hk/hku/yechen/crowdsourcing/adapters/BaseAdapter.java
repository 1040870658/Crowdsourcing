package hk.hku.yechen.crowdsourcing.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import hk.hku.yechen.crowdsourcing.util.LevelLog;


/**
 * Created by yechen on 2017/11/23.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter.GeneralViewHolder> {
    protected List<T> datas;

    public BaseAdapter(){datas = new ArrayList<>();}

    public BaseAdapter(List<T> datas){
        this.datas = datas;
    }
    @Override
    public GeneralViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return GeneralViewHolder.getInstance(parent,getLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(GeneralViewHolder holder, int position) {
        convert(datas.get(position),holder,position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public abstract int getLayoutId(int viewType);
    public abstract void convert(T data,GeneralViewHolder viewHolder,int position);
    public static class GeneralViewHolder extends RecyclerView.ViewHolder{
        private View contentView;
        private SparseArray<View> sparseViews;

        private GeneralViewHolder(View itemView) {
            super(itemView);
            this.contentView = itemView;
            sparseViews = new SparseArray<>();
        }

        public void setListener(View.OnClickListener listener){
            contentView.setOnClickListener(listener);
        }
        public void setChildListener(View.OnClickListener listener,int id){
            View view = getView(id);
            if(view != null)
                view.setOnClickListener(listener);
        }
        public static GeneralViewHolder getInstance(ViewGroup parent,int id){
            View view = LayoutInflater.from(parent.getContext()).inflate(id,parent,false);
            return new GeneralViewHolder(view);
        }

        public <T extends View> T getView(int id){
            View view = sparseViews.get(id);
            if(view == null){
                view = contentView.findViewById(id);
                sparseViews.put(id,view);
            }
            return (T)view;
        }
        public void setTextView(int id,String value){
            TextView textView = getView(id);
            textView.setText(value);
        }
        public void setImageView(int id, Drawable drawable){
            ImageView imageView = getView(id);
            imageView.setImageDrawable(drawable);
        }
        public void setImageView(int id,int imgID){
            ImageView imageView = getView(id);
            imageView.setImageResource(imgID);
        }
        public void setImageView(Context context,int id, int imgID){
            ImageView imageView = getView(id);
            Glide.with(context).load(imgID).into(imageView);
        }
        public void setImageButton(Context context,int id,int imgID){
            ImageButton imageButton = getView(id);
            //Glide.with(context).load(imgID).into(imageButton);
            imageButton.setImageResource(imgID);
        }
        public void setImageView(Context context,int id,String imgURL){
            ImageView imageView = getView(id);
            Glide.with(context).load(imgURL).into(imageView);
        }
        public String getText(int id){
            TextView textView = getView(id);
            if(textView == null)
                return "";
            return textView.getText().toString();
        }
    }
}
