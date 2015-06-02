package alonedroid.com.nanitabe.utility;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import alonedroid.com.nanitabe.R;


/**
 * 選択用のレシピ一覧を返す
 */
public class RecipeChoiceAdapter extends PagerAdapter {
    /**
     * メイン画像
     */
    private ImageView mImageView;
    /**
     * 実行元アクティビティ
     */
    private Activity mActivity;
    /**
     * 生成画像リスト
     */
    private HashMap<String, Bitmap> mList;
    /**
     * レシピ名リスト
     */
    public HashMap<String, String> mTitle;
    /**
     * レシピ総数
     */
    int mAllRecipeCount = 0;
    /**
     * 表示するレシピId一覧
     */
    String[] mRecipeIds;

    /**
     * コンストラクタ
     */
    public RecipeChoiceAdapter(Activity activity) {
        mActivity = activity;
        mList = new HashMap<String, Bitmap>();
        mTitle = new HashMap<String, String>();
    }

    /**
     * パラメータの画像URLからBITMAP一覧を生成する
     */
    public void get_image(String[] urls) {
        mAllRecipeCount = urls.length;
        mRecipeIds = urls;
        Resources res = mActivity.getResources();
        for (String p : urls) {
            mList.put(p, BitmapFactory.decodeResource(res, R.drawable.no_image));
            mTitle.put(p, "取得中・・・");
        }

        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {
                try {
                    Resources res = mActivity.getResources();
                    for (String p : params) {
                        Document doc = Jsoup.parse(downloadHtml("http://cookpad.com/recipe/" + p));

                        String param = doc.select(".main_photo").first().getElementsByTag("img").attr("src");
                        mTitle.put(p, doc.select(".recipe_title").first().text());
                        if (param.length() == 0) {
                            //→画像なしならNO IMAGEをセット
                            mList.put(p, BitmapFactory.decodeResource(res, R.drawable.no_image));
                        } else {
                            //→画像のある料理はそれを取得
                            URL url = new URL(param);
                            InputStream is = url.openStream();
                            mList.put(p, BitmapFactory.decodeStream(is));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            /* (非 Javadoc)
             * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
             */
            @Override
            protected void onPostExecute(String result) {
                notifyDataSetChanged();
//                mActivity.setNowPage();
                super.onPostExecute(result);
            }


        }.execute(urls);
    }

    @Override
    public Object instantiateItem(View container, int position) {
        try {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            FrameLayout f = (FrameLayout) inflater.inflate(R.layout.layout_menu, null);

            // メイン画像のセット
            mImageView = (ImageView) f.findViewById(R.id.menu_photo);
            if (mList.containsKey(mRecipeIds[position])) {
                mImageView.setImageBitmap(mList.get(mRecipeIds[position]));
            } else {
                mImageView.setImageResource(R.drawable.no_image);
            }

            // コンテナに追加
            ((ViewGroup) container).addView(f);

            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewGroup) container).removeView((View) object);
    }

    @Override
    public int getCount() {
        return mAllRecipeCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // Object 内に View が存在するか判定する
        return view == (FrameLayout) object;
    }

    @Override
    public int getItemPosition(Object object) {
        // 画像のキャッシュを保持しない（ちょっと重くなるかも？）
        return POSITION_NONE;
    }

    private static int mTrial = 0;

    public static String downloadHtml(String path) {
        InputStream is = null;
        try {
            StringBuilder result = new StringBuilder();
            String line;

            URL url = new URL(path);
            is = url.openStream();  // throws an IOException
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            mTrial = 0;
            return result.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            mTrial++;
            if (mTrial < 3) {
                downloadHtml(path);
            } else {
                mTrial = 0;
                return "";
            }
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
        return "";
    }
}