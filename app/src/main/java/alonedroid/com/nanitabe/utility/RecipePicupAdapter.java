package alonedroid.com.nanitabe.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import alonedroid.com.nanitabe.R;


public class RecipePicupAdapter extends ArrayAdapter<String> {

    NtDataManager dataManager;

    private LayoutInflater mInflater;

    private SparseArray<String> mSelectedItem = new SparseArray<String>();

    private HashMap<String, Bitmap> mGetedImage = new HashMap<String, Bitmap>();

    public RecipePicupAdapter(Context context, int layout, List<String> items) {
        super(context, layout, items);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dataManager = NtDataManager_.getInstance_(context);
    }

    public void clickItem(View view, int pos) {
        if (((PhotoSelectViewHolder) view.getTag()).iv_recipe_check.isEnabled()) {
            mSelectedItem.remove(pos);
            ((PhotoSelectViewHolder) view.getTag()).iv_recipe_check.setEnabled(false);
        } else {
            mSelectedItem.put(pos, ((PhotoSelectViewHolder) view.getTag()).str_key_url);
            ((PhotoSelectViewHolder) view.getTag()).iv_recipe_check.setEnabled(true);
        }
    }

    public String[] getSelectedItems() {
        int size = mSelectedItem.size();
        String[] recipes = new String[size];

        for (int i = 0; i < size; i++) {
            recipes[i] = mSelectedItem.valueAt(i);
        }

        return recipes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 生成済みのViewは選択済みかどうか判定する
        View view = convertView;
        if (view == null) {
            PhotoSelectViewHolder holder = new PhotoSelectViewHolder();
            view = mInflater.inflate(R.layout.layout_recipe_list_row, null);
            ((ImageView) view.findViewById(R.id.recipe_list_image)).setImageBitmap(null);
            holder.iv_recipe_image = (ImageView) view.findViewById(R.id.recipe_list_image);
            holder.tv_recipe_title = (TextView) view.findViewById(R.id.recipe_list_title);
            holder.iv_recipe_check = (ImageView) view.findViewById(R.id.recipe_list_check);
            holder.iv_recipe_check.setEnabled(false);
            view.setTag(holder);
        } else {
            // 取得済みの行に対して何かしらの処理を施す
            if (0 <= mSelectedItem.indexOfKey(position)) {
                ((PhotoSelectViewHolder) view.getTag()).iv_recipe_check.setEnabled(true);
            } else {
                ((PhotoSelectViewHolder) view.getTag()).iv_recipe_check.setEnabled(false);
            }
            Log.d("itinoue", mSelectedItem.indexOfKey(position) + "");
        }

        String key = getItem(position);

        View title = new View(getContext());
        title.setTag(this.dataManager.getTitle(key));

        ((PhotoSelectViewHolder) view.getTag()).tv_recipe_title.setText(this.dataManager.getTitle(key));
        ((PhotoSelectViewHolder) view.getTag()).str_key_url = key;
        if (TextUtils.isEmpty(this.dataManager.getImage(key))) {
            ((PhotoSelectViewHolder) view.getTag()).iv_recipe_image.setImageResource(R.drawable.no_image);
        } else {
            final String strUrl = this.dataManager.getImage(key);
            if (mGetedImage.containsKey(strUrl) == false) {
                ((PhotoSelectViewHolder) view.getTag()).iv_recipe_image.setImageResource(R.drawable.no_image);
                new AsyncTask<View, Integer, String>() {
                    private View v;
                    private String title;

                    @Override
                    protected String doInBackground(View... params) {
                        v = params[0];
                        title = params[1].getTag().toString();
                        try {
                            InputStream is = new URL(strUrl).openStream();
                            mGetedImage.put(strUrl, BitmapFactory.decodeStream(is));
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if (title.equals(((PhotoSelectViewHolder) v.getTag()).tv_recipe_title.getText().toString())) {
                            ((PhotoSelectViewHolder) v.getTag()).iv_recipe_image.setImageBitmap(mGetedImage.get(strUrl));
                        }
                        super.onPostExecute(result);
                    }
                }.execute(view, title);
            } else {
                ((PhotoSelectViewHolder) view.getTag()).iv_recipe_image.setImageBitmap(mGetedImage.get(strUrl));
            }
        }

        return view;
    }

    class PhotoSelectViewHolder {
        public ImageView iv_recipe_image;
        public TextView tv_recipe_title;
        public ImageView iv_recipe_check;
        public String str_key_url;
    }
}