package com.example.beatplane;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/*游戏界面的类*/
public class MainView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
	private Bitmap background; 		// 背景图片
	private Bitmap background1; 	// 背景图片
	private Bitmap background2;
	private Bitmap background3;
	private Bitmap background4;
	private Bitmap playButton; 		// 按钮图片
	private Bitmap missile_bt;		//导弹按钮图标
	private Canvas canvas;			// 画布资源
	private Paint paint; 			// 画笔
	private SurfaceHolder sfh;
	private Thread thread;			// 绘图线程
	private MyPlane myPlane;
//	private int scoreSum;			//总积分
//	private int middleSum;			//中型敌机积分
//	private int bigSum;				//大型敌机积分
//	private int bossSum;			//BOSS敌机的积分
//	private int missileSum;			//导弹的积分
	private int smallCount;
	private int middleCount;
	private int bigCount;
	private int shieldCount;
	private int ephemeralCount;
	private int speedTime;
	private float bg_y;
	private float bg_y2;
	private float play_bt_w;
	private float play_bt_h;
	private float scalex;
	private float scaley;
	//private float missile_bt_y;
	private float screen_width;		 // 屏幕的宽度
	private float screen_height;	 // 屏幕的高度
	private boolean threadFlag;
	private boolean isPlay;
	private boolean isTouch;
	private boolean goodsOn;
	private boolean soundsOn;
	//private boolean shielded;  //shield
	//private GameGoods missileGoods;	 //导弹物品
	//private GameGoods bulletGoods;	 //子弹物品
	private BossPlane bossPlane;	 //BOSS飞机对象
	private List<GameObject> planes;
	private GameGoods shields;
	private GameGoods ephemeral;
	private GameSoundPool sounds;
	private MainActivity mainActivity;
	private Handler myHandler;
	private  int time ;
	private  int ephemeralTime;
	Timer timer;
	public MainView(Context context,boolean goodsOno,boolean soundsOno) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mainActivity = (MainActivity)context;
		goodsOn = goodsOno;
		soundsOn = soundsOno;
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		sounds = new GameSoundPool(mainActivity);
		sounds.setOpen(soundsOn);
		sounds.initSound();
		planes = new ArrayList<GameObject>();
		myPlane = new MyPlane(this,getResources());
		bossPlane = new BossPlane(getResources(),mainActivity);
		planes.add(bossPlane);
		shields= new GameGoods(getResources(),1);
		ephemeral = new GameGoods(getResources(),2);
		for(int i = 0;i < 21;i++){
			BigPlane bigPlane = new BigPlane(getResources());
			planes.add(bigPlane);
		}
		for(int i = 0;i <16;i++){
			MiddlePlane middlePlane = new MiddlePlane(getResources());
			planes.add(middlePlane);
		}
		for(int i = 0;i < 30;i++){
			SmallPlane smallPlane = new SmallPlane(getResources());
			planes.add(smallPlane);
		}
		//missileGoods = new GameGoods(getResources(),1);
		//bulletGoods = new GameGoods(getResources(),2);
		smallCount = 0;
		middleCount = 0;
		bigCount = 0;
		shieldCount = 0;
		ephemeralCount = 0;
		speedTime = 1;
		thread = new Thread(this);
		isPlay = true;
		ephemeralTime = 0;
		myHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(msg.what == 1){
					mainActivity.toEndView(time,goodsOn,soundsOn);

				}
			}
		};
		time=0;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				if(isPlay){
					time++;
				}
			}
		},0,1000);

	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		screen_width = this.getWidth();
		screen_height = this.getHeight();
		initBitmap(); // 初始化图片资源
		for(GameObject obj:planes){
			obj.setScreenWH(screen_width, screen_height);
		}
		myPlane.setScreenWH(screen_width, screen_height);
		//missileGoods.setScreenWH(screen_width, screen_height);
		shields.setScreenWH(screen_width,screen_height);
		ephemeral.setScreenWH(screen_width,screen_height);
		//bulletGoods.setScreenWH(screen_width, screen_height);
		myPlane.setAlive(true);
		threadFlag = true;
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		threadFlag = false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			isTouch = false;
		}
		else if(event.getAction() == MotionEvent.ACTION_DOWN){
			float x = event.getX();
			float y = event.getY();
			if(x > 10 && x < 10 + play_bt_w && y > 10 && y < 10 + play_bt_h){
				if(isPlay){
					isPlay = false;
					//暂停图标
				}
				else{
					isPlay = true;
					synchronized(thread){
						thread.notify();
					}
				}
				return true;
			}
			//判断玩家飞机是否被按下
			else if(x > myPlane.getObject_x() && x < myPlane.getObject_x() + myPlane.getObject_width()
					&& y > myPlane.getObject_y() && y < myPlane.getObject_y() + myPlane.getObject_height()){
				if(isPlay){
					isTouch = true;
				}
				return true;
			}

		}
		//玩家飞机是否移动
		else if(event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 1){
			if(isTouch){
				float x = event.getX();
				float y = event.getY();
				if(x > myPlane.getMiddle_x() + 20){
					if(myPlane.getMiddle_x() + myPlane.getSpeed() <= screen_width){
						myPlane.setMiddle_x(myPlane.getMiddle_x() + myPlane.getSpeed());
					}
				}
				else if(x < myPlane.getMiddle_x() - 20){
					if(myPlane.getMiddle_x() - myPlane.getSpeed() >= 0){
						myPlane.setMiddle_x(myPlane.getMiddle_x() - myPlane.getSpeed());
					}
				}
				if(y > myPlane.getMiddle_y() + 20){
					if(myPlane.getMiddle_y() + myPlane.getSpeed() <= screen_height){
						myPlane.setMiddle_y(myPlane.getMiddle_y() + myPlane.getSpeed());
					}
				}
				else if(y < myPlane.getMiddle_y() - 20){
					if(myPlane.getMiddle_y() - myPlane.getSpeed() >= 0){
						myPlane.setMiddle_y(myPlane.getMiddle_y() - myPlane.getSpeed());
					}
				}
			}
		}
		return false;
	}

	// 初始化图片
	public void initBitmap() {
		playButton = BitmapFactory.decodeResource(getResources(),R.drawable.play);
		background = BitmapFactory.decodeResource(getResources(), R.drawable.bg_02);
		background1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_02);
		background2 = BitmapFactory.decodeResource(getResources(),R.drawable.bg_03);
		background3 = BitmapFactory.decodeResource(getResources(),R.drawable.bg_04);
		background4 = BitmapFactory.decodeResource(getResources(),R.drawable.bg_05);
		missile_bt = BitmapFactory.decodeResource(getResources(), R.drawable.missile_bt);
		scalex = screen_width / background.getWidth();
		scaley = screen_height / background.getHeight();
		play_bt_w = playButton.getWidth();
		play_bt_h = playButton.getHeight()/2;
		bg_y = 0;
		bg_y2 = bg_y - screen_height;
		//missile_bt_y = screen_height - 10 - missile_bt.getHeight();
	}
	//初始化对象
	public void initObject() {
		//初始化敌机对象
		for (GameObject obj : planes) {
			//初始化小型敌机
			if (obj instanceof SmallPlane) {
				if (time >= 0 && time < 25) {
					if (!obj.isAlive()) {
						obj.initial(smallCount, 0, 0, speedTime);
						smallCount++;
						if (smallCount >= 5) {
							smallCount = 0;
						}
						break;
					}
				}
			}
			//初始化中型敌机
			else if (obj instanceof MiddlePlane) {
				if (time >= 25 && time < 50) {
					if (!obj.isAlive()) {
						obj.initial(middleCount, 0, 0, speedTime);
						middleCount++;
						if (middleCount >= 17
								) {
							middleCount = 0;
							//middleSum = 0;
						}
						break;
					}
				}
			}
			//初始化大型敌机
			else if (obj instanceof BigPlane) {
				if (time >= 50 && time < 100) {
					if (!obj.isAlive()) {
						obj.initial(bigCount, 0, 0, speedTime);
						bigCount++;
						if (bigCount >= 32) {
							bigCount = 0;
							//bigSum = 0;
						}
						break;
					}
				}
			}
			//初始化BOSS敌机
			else {
				if (time >= 75 && time <= 100) {
					if (!obj.isAlive()) {
						obj.initial(0, 0, 0, 0);
						bossPlane.setPlane(myPlane);
						//bossSum = 0;
						break;
					}
				}
			}
		}
		//初始化子弹
		if (bossPlane.isAlive())
			bossPlane.initButtle();
		if (time >= 25 && goodsOn) {
			if (!shields.isAlive()) {
				shields.initial((int)screen_height*shieldCount*3, 0, 0, speedTime);
				shieldCount++;
			}
		}
		if (time >= 75 && goodsOn) {
			if (!ephemeral.isAlive()) {
				ephemeral.initial((int)screen_height*ephemeralCount, 0, 0, speedTime);
				ephemeralCount++;
			}
		}

	}
	// 绘图函数
	public void drawSelf() {
		try {
			canvas = sfh.lockCanvas();
			canvas.drawColor(Color.BLACK); // 绘制背景色
			canvas.save();
			// 计算背景图片与屏幕的比例
			canvas.scale(scalex, scaley, 0, 0);
			if (time >=0 && time < 25){
				canvas.drawBitmap(background, 0, bg_y, paint);   // 绘制背景图
				canvas.drawBitmap(background1, 0, bg_y2, paint); // 绘制背景图
				canvas.restore();
				//背景移动的逻辑
				if(bg_y > bg_y2){
					bg_y += 10;
					bg_y2 = bg_y - background.getHeight();
				}
				else{
					bg_y2 += 10;
					bg_y = bg_y2 - background.getHeight();
				}
				if(bg_y >= background.getHeight()){
					bg_y = bg_y2 - background.getHeight();
				}
				else if(bg_y2 >= background.getHeight()){
					bg_y2 = bg_y - background.getHeight();
				}
			}
			else if(time>=25 && time < 50){
				canvas.drawBitmap(background2, 0, 0, paint);
				canvas.restore();
			}
			else if(time>=50 && time < 75){
				canvas.drawBitmap(background3, 0, 0, paint);
				canvas.restore();
			}
			else if (time >= 75 && time <100){
				canvas.drawBitmap(background4, 0, 0, paint);
				canvas.restore();
			}
			else{
				canvas.restore();
				time = 99;
				threadFlag = false;
				return;
			}
			if(shields.isAlive()){
				if(shields.isCollide(myPlane)){
					shields.setAlive(false);
					myPlane.setShielded(1);
					sounds.playSound(6, 0);
				}
				else
					shields.drawSelf(canvas);
			}
			if(ephemeral.isAlive()){
				if(ephemeral.isCollide(myPlane)){
					ephemeral.setAlive(false);
					ephemeralTime = time;
					myPlane.setPhenomenal(true);
					sounds.playSound(6, 0);
				}
				else
					ephemeral.drawSelf(canvas);
			}
			//绘制敌机
			for(GameObject obj:planes){
				if(obj.isAlive()){
					obj.drawSelf(canvas);
					if(!obj.isExplosion() && myPlane.isAlive()){
						if(obj.isCollide(myPlane)){			//检测敌机是否与玩家的飞机碰撞
							if(myPlane.getPhenomenal()){
								//
							}
							else if(myPlane.getShielded()>0){
								myPlane.setShielded(myPlane.getShielded() - 1);
								sounds.playSound(1, 0);
							}
							else{
								timer.cancel();
								myPlane.setAlive(false);
							}
						}
					}
				}
			}
			if(!myPlane.isAlive()){
				sounds.playSound(2, 0);			//飞机炸毁的音效
				threadFlag = false;
			}
			//绘制玩家的飞机
			if(time > ephemeralTime + 5){
				myPlane.setPhenomenal(false);
			}
			myPlane.drawSelf(canvas);


			//绘制按钮
			canvas.save();
			canvas.clipRect(10, 10, 10 + play_bt_w,10 + play_bt_h);
			if(isPlay){
				canvas.drawBitmap(playButton, 10, 10, paint);
			}
			else{
				canvas.drawBitmap(playButton, 10, 10 - play_bt_h, paint);
			}
			canvas.restore();
			//绘制积分文字

			paint.setTextSize(30);
			paint.setColor(Color.rgb(255, 255, 0));
			canvas.drawText("时间:"+String.valueOf(time), screen_width - 150, 40, paint);//绘制文字
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas);
		}
	}
	public void release(){
		for(GameObject obj:planes){
			obj.release();
		}
		if(!playButton.isRecycled()){
			playButton.recycle();
		}
		if(!background.isRecycled()){
			background.recycle();
		}
		if(!background2.isRecycled()){
			background2.recycle();
		}
		if(!background3.isRecycled()){
			background3.recycle();
		}
		if(!background4.isRecycled()){
			background4.recycle();
		}
		if(!background1.isRecycled()){
			background1.recycle();
		}
		if(!missile_bt.isRecycled()){
			missile_bt.recycle();
		}
		//missileGoods.release();
		shields.release();
		ephemeral.release();
		myPlane.release();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (threadFlag) {
			long startTime = System.currentTimeMillis();
			initObject();
			drawSelf();
			long endTime = System.currentTimeMillis();
			if(!isPlay){
				synchronized (thread) {
					try {
						thread.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				if (endTime - startTime < 100)
					Thread.sleep(100 - (endTime - startTime));
			} catch (InterruptedException err) {
				err.printStackTrace();
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myHandler.sendEmptyMessage(1);
	}

//	public void addMiddleSum(int score) {
//		middleSum += score;
//	}
//
//	public void addBigSum(int score) {
//		bigSum += score;
//	}
//
//	public void addScoreSum(int score) {
//		scoreSum += score;
//	}
//
//	public void addMissileSum(int score) {
//		missileSum += score;
//	}
//
//	public void addBossSum(int score) {
//		bossSum += score;
//	}

	public GameSoundPool getSounds() {
		return sounds;
	}
}
