package std.neomind.brainmanager.utils;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.RecyclerView;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

import std.neomind.brainmanager.KeywordActivity;
import std.neomind.brainmanager.MainActivity;
import std.neomind.brainmanager.R;
import std.neomind.brainmanager.ReviewActivity;
import std.neomind.brainmanager.data.Keyword;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.KeywordViewHolder> {

    private static final String TAG = "MainRecyclerAdapter";

    private MainActivity mActivity;
    private ArrayList<Keyword> mKeywords;

    public MainRecyclerAdapter(MainActivity activity, ArrayList<Keyword> keywords) {
        mActivity = activity;
        mKeywords = keywords;
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_keyword_item, viewGroup, false);
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
            imageView = itemView.findViewById(R.id.main_item_circularImage_keyword);
            textView = itemView.findViewById(R.id.main_item_textView_keyword);
        }

        public void build(int i) {
            mKeywords.get(i).setCardView((CardView)itemView);

            itemView.setOnClickListener(new ItemClickListener(i));
            itemView.setOnLongClickListener(new ItemLongClickListener(i, this));

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
            Intent intent = new Intent(mActivity, KeywordActivity.class);
            intent.putExtra(KeywordActivity.EXTRAS_INTENT_MODE, KeywordActivity.INTENT_MODE_VIEW);
            intent.putExtra(KeywordActivity.EXTRAS_KEYWORD, mKeywords.get(mPosition).id);
            mActivity.startActivity(intent);
        }
    }
    
    private class ItemLongClickListener implements View.OnLongClickListener {
        private int mPosition;
        private KeywordViewHolder mKeywordViewHolder;

        private ItemLongClickListener(int position, KeywordViewHolder keywordViewHolder) {
            mPosition = position;
            mKeywordViewHolder = keywordViewHolder;
        }

        @Override
        public boolean onLongClick(View v) {
            PopupMenu popupMenu = new PopupMenu(mActivity, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            Menu menu = popupMenu.getMenu();
            inflater.inflate(R.menu.main_context_keyword, menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {

                    case R.id.item_edit:
                        Intent intent = new Intent(mActivity, KeywordActivity.class);
                        intent.putExtra(KeywordActivity.EXTRAS_INTENT_MODE, KeywordActivity.INTENT_MODE_UPDATE);
                        intent.putExtra(KeywordActivity.EXTRAS_KEYWORD, mKeywords.get(mPosition).id);
                        mActivity.startActivity(intent);
                        break;

                    case R.id.item_remove:
                        try {
                            BrainDBHandler dbHandler = new BrainDBHandler(mActivity.getBaseContext());
                            dbHandler.removeKeyword(mKeywords.get(mPosition).id);       // db 삭제
                            mKeywordViewHolder.removeItem();                            // 기프티콘 배열에서 삭제 및 뷰홀더 리로드

                            ArrayList<Integer> reviewList = new ArrayList<>();
                            ArrayList<Long> reviewDateList = new ArrayList<>();

                            //TODO 사실 이런 부분들은 전부 메서드로 만들어야 깔끔하지만 시간 없어서 걍 복붙함
                            try {
                                FileInputStream fileStream = new FileInputStream(new File(mActivity.getFilesDir() ,"BrainAlarm.data"));
                                try {
                                    ObjectInputStream os = new ObjectInputStream(fileStream);

                                    reviewList = (ArrayList<Integer>)os.readObject();
                                    reviewDateList = (ArrayList<Long>) os.readObject();
                                    //y
                                    os.close();
                                }
                                catch (Exception ioe){
                                    ioe.printStackTrace();
                                }
                                finally {
                                    try {
                                        fileStream.close();
                                        try{
                                            FileOutputStream fileStream2 = new FileOutputStream(new File(mActivity.getFilesDir(),"BrainAlarm.data"));
                                            try {
                                                ObjectOutputStream os2 = new ObjectOutputStream(fileStream2);
                                                reviewDateList.remove(reviewList.indexOf(mKeywords.get(mPosition).id));
                                                reviewList.remove(mKeywords.get(mPosition).id);

                                                os2.writeObject(reviewList);
                                                os2.writeObject(reviewDateList);
                                                os2.close();
                                            }
                                            catch (Exception ioe){
                                                ioe.printStackTrace();
                                            }
                                            finally {
                                                try {
                                                    fileStream2.close();
                                                }
                                                catch (IOException ioee){
                                                    ioee.printStackTrace();
                                                }
                                            }
                                        }
                                        catch(FileNotFoundException e2){
                                            e2.printStackTrace();
                                        }
                                    }
                                    catch (IOException ioee){
                                        ioee.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                }
                return true;
            });
            popupMenu.show();
            return true;
        }
    }
}
