package std.neomind.brainmanager.data;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;
import std.neomind.brainmanager.MainActivity;
import std.neomind.brainmanager.R;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder> {

    private static final String TAG = "KeywordAdapter";

    private MainActivity mActivity;
    private ArrayList<Keyword> mKeywords;

    public KeywordAdapter(MainActivity activity, ArrayList<Keyword> keywords) {
        mActivity = activity;
        mKeywords = keywords;
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.keyword_item, viewGroup, false);
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

        public CircularImageView imageView;
        public TextView textView;

        public KeywordViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.keyword_circle_image);
            textView = itemView.findViewById(R.id.keyword_text_view);
        }

        public void build(int i) {
            itemView.setOnClickListener(new ItemClickLister(i));

            Glide.with(mActivity.getBaseContext())
                    .load(mKeywords.get(i).imagePath)
                    .into(imageView);
            textView.setText(mKeywords.get(i).name);
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

    private class ItemClickLister implements View.OnClickListener {
        private int mPosition;

        private ItemClickLister(int position) {
            this.mPosition = position;
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick position: " + mPosition);

            mActivity.pickImage(mKeywords.get(mPosition));
        }
    }
}
