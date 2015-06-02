package alonedroid.com.nanitabe.scene.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import alonedroid.com.nanitabe.R;


/**
 * 処理中画面を表示します。
 */
public class NtWaitSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    /**
     * View本体
     */
    private SurfaceHolder mSurfaceHolder;
    /**
     * メインスレッド
     */
    private Thread mThread;
    /**
     * 処理中画像
     */
    private Bitmap mBmp;
    /**
     * 処理中画像の表示位置
     */
    int mWidth, mHeight;

    /**
     * コンストラクタ
     */
    public NtWaitSurfaceView(Context context) {
        super(context);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread = null;
    }

    @Override
    public void run() {
        // init
        Canvas canvas = null;
        int i = 0;
        int[] image = new int[]{R.drawable.wait_1, R.drawable.wait_2, R.drawable.wait_3, R.drawable.wait_4};
        while (mThread != null) {
            // animation
            try {
                // canvas lock
                canvas = mSurfaceHolder.lockCanvas();

                // image change
                mBmp = BitmapFactory.decodeResource(getResources(), image[i % 4]);
                float m_posX = (getWidth() - mBmp.getWidth()) * 0.5f;
                float m_posY = (getHeight() - mBmp.getHeight()) * 0.5f;
                canvas.drawBitmap(mBmp, m_posX, m_posY, null);

                // canvas unlock
                mSurfaceHolder.unlockCanvasAndPost(canvas);

                Thread.sleep(500);

                i++;
                if (mBmp != null) {
                    mBmp.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mBmp = null;
    }
}