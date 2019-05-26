package std.neomind.brainmanager.utils;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;
import std.neomind.brainmanager.R;
import std.neomind.brainmanager.data.Keyword;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.KeywordViewHolder> {

    private static final String TAG = "CategoryRecyclerAdapter";

    private Activity mActivity;
    private ArrayList<Keyword> mKeywords;

    public CategoryRecyclerAdapter(Activity activity, ArrayList<Keyword> keywords) {
        mActivity = activity;
        mKeywords = keywords;
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_keyword_item, viewGroup, false);
        KeywordViewHolder keywordViewHolder = new KeywordViewHolder(view);
        return keywordViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordViewHolder keywordViewHolder, int i) {
        keywordViewHolder.build(i);
    }

    @Override
    public int getItemCount() {
        try {
            return mKeywords.size();
        } catch (Exception e) {
            return 0;
        }
    }

    class KeywordViewHolder extends RecyclerView.ViewHolder implements AnimateViewHolder {
        /**
         * @param itemView is convertView
         */

        CircularImageView imageView;
        TextView textView;

        KeywordViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "KeywordViewHolder: itemView : " + itemView);
            imageView = itemView.findViewById(R.id.category_item_circularImage_keyword);
            textView = itemView.findViewById(R.id.category_item_textView_keyword);
        }

        public void build(int i) {
            mKeywords.get(i).setCardView((CardView)itemView);

            itemView.setOnClickListener(new ItemClickListener(i));

            Glide.with(mActivity.getBaseContext())
                    .load(mKeywords.get(i).imagePath)
                    .into(imageView);
            textView.setText(mKeywords.get(i).name);
        }

        public void removeItem() {
            mKeywords.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }

        public void removeItem(int i) {
            mKeywords.remove(i);
            notifyItemRemoved(i);
        }

        @Override
        public void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
            ViewCompat.setTranslationY(itemView, -itemView.getHeight() * 0.3f);
            ViewCompat.setAlpha(itemView, 0f);
        }

        @Override
        public void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {

        }

        @Override
        public void animateAddImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {
            ViewCompat.animate(itemView).
                    translationY(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(listener)
                    .start();
        }

        @Override
        public void animateRemoveImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {
            ViewCompat.animate(itemView).
                    translationY(-itemView.getHeight() * 0.3f)
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(listener)
                    .start();
        }
    }

    private class ItemClickListener implements View.OnClickListener {
        private int mPosition;

        private ItemClickListener(int position) {
            this.mPosition = position;
        }

        @Override
        public void onClick(View v) {
            Keyword keyword = mKeywords.get(mPosition);
            keyword.setSelected(!keyword.isSelected());

            keyword.getCardView().findViewById(R.id.imageViewSelectedMask).
                    setVisibility(keyword.isSelected() ? View.VISIBLE : View.GONE);
        }
    }
}
