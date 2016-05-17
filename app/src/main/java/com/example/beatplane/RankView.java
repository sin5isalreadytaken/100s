package com.example.beatplane;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/*游戏开始前的界面*/
public class RankView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private Bitmap background;
    private Canvas canvas;			// 画布资源
    private Paint paint; 			// 画笔
    private SurfaceHolder sfh;
    private Thread thread;			// 绘图线程
    private double button_x2 = 0.055;
    private double button_y = 0.055;
    private double button_y3 = 0.89;
    private float scalex;
    private float scaley;
    private float screen_width;
    private float screen_height;
    private boolean threadFlag;
    private boolean soundsOn;
    private boolean goodsOn;
    private MainActivity mainActivity;
    private GameSoundPool sounds;
    public RankView(Context context,boolean goodsOno,boolean soundsOno) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mainActivity = (MainActivity) context;
        soundsOn = soundsOno;
        goodsOn = goodsOno;
        sounds = new GameSoundPool(mainActivity);
        sounds.setOpen(soundsOn);
        sounds.initSysSound();
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        thread = new Thread(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        screen_width = this.getWidth();
        screen_height = this.getHeight();
        initBitmap(); // 初始化图片资源
        threadFlag = true;
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        release();
        threadFlag = false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN && event.getPointerCount() == 1){
            float x = event.getX();
            float y = event.getY();
            if(x > screen_width/2 - button_x2*screen_width && x < screen_width/2 + button_x2*screen_width
                    && y > button_y3*screen_height && y < (button_y3 + button_y)*screen_height)
            {
                sounds.playSound(1, 0);
                drawSelf();
                mainActivity.toReadyView(goodsOn,soundsOn);
            }
            return true;
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            return true;
        }
        return false;
    }
    // 初始化图片
    public void initBitmap() {
        background = BitmapFactory.decodeResource(getResources(), R.drawable.bg_r);
        scalex = screen_width / background.getWidth();
        scaley = screen_height / background.getHeight();
    }
    // 绘图函数
    public void drawSelf() {
        try {
            canvas = sfh.lockCanvas();
            canvas.drawColor(Color.BLACK); // 绘制背景色
            canvas.save();
            // 计算背景图片与屏幕的比例
            canvas.scale(scalex, scaley, 0, 0);
            canvas.drawBitmap(background, 0, 0, paint);   // 绘制背景图
            canvas.restore();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        finally {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }
    public void release(){
        if(!background.isRecycled()){
            background.recycle();
        }
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (threadFlag) {
            long startTime = System.currentTimeMillis();
            drawSelf();
            long endTime = System.currentTimeMillis();
            try {
                if (endTime - startTime < 500)
                    Thread.sleep(500 - (endTime - startTime));
            } catch (InterruptedException err) {
                err.printStackTrace();
            }
        }
    }
}
