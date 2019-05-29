package std.neomind.brainmanager.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.RecyclerView;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

import std.neomind.brainmanager.KeywordActivity;
import std.neomind.brainmanager.MainActivity;
import std.neomind.brainmanager.R;
import std.neomind.brainmanager.data.Keyword;

public class RelationListRecyclerAdapter extends RecyclerView.Adapter<RelationListRecyclerAdapter.KeywordViewHolder> {

    private static final String TAG = "RelationListRecyclerAdapter";

    private Activity mActivity;
    private Keyword mSelectK;
    private ArrayList<Keyword> mKeywords;

    public RelationListRecyclerAdapter(Activity activity, Keyword targetK, ArrayList<Keyword> keywords) {
        mActivity = activity;
        mSelectK = targetK;
        mKeywords = keywords;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_relation_keyword_item, viewGroup, false);
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
            imageView = itemView.findViewById(R.id.reviewRelation_item_circularImage_keyword);
            textView = itemView.findViewById(R.id.reviewRelation_item_textView_keyword);
        }

        public void build(int i) {
            Keyword currentItemKey = mKeywords.get(i);
            currentItemKey.setCardView((CardView)itemView);

            if(!currentItemKey.name.equals("")) {
                boolean ck_rel = false;
                //기존 관계성 체크
                for(Integer id : mSelectK.getRelationIds()){
                    if(currentItemKey.id == id) ck_rel = true;
                }
                if (ck_rel) {
                    currentItemKey.setSelected(true);
                    itemView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.review_relation_blue_edge));
                } else {
                    itemView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.review_relation_edge));
                }
                itemView.setOnClickListener(new ItemClickListener(i));
            }
            else{
                itemView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.review_relation_gray_edge));
            }


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
            BrainDBHandler dbHandler = new BrainDBHandler(mActivity);
            if (mKeywords.get(mPosition).isSelected()) {
                mKeywords.get(mPosition).setSelected(false);
                dbHandler.removeRelation(mSelectK.id, mKeywords.get(mPosition).id);
                v.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.review_relation_edge));
            } else {
                mKeywords.get(mPosition).setSelected(true);
                try {
                    dbHandler.addRelation(mSelectK.id, mKeywords.get(mPosition).id);
                }
                catch(BrainDBHandler.DataDuplicationException e){
                    Toast.makeText(mActivity, "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                v.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.review_relation_blue_edge));
            }
        }
    }
}
