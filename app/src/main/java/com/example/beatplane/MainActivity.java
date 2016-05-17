package com.example.beatplane;

import com.example.beatplane.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private MainView mainView;
	private ReadyView readyView;
	private EndView endView;
	private OptionView optionView;
	private TipView tipView;
	private RankView rankView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		readyView = new ReadyView(this,true,true);
		setContentView(readyView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	//显示游戏界面
	public void toMainView(boolean goodsOn,boolean soundsOn){
		mainView = new MainView(this,goodsOn,soundsOn);
		setContentView(mainView);
		readyView = null;
	}
	//显示结束界面
	public void toEndView(int scoreSum,boolean goodsOn,boolean soundsOn){
		System.gc();
		endView = new EndView(this,scoreSum,goodsOn,soundsOn);
		setContentView(endView);
		mainView = null;
	}

	public void toReadyView(boolean goodsOn,boolean soundsOn){
		readyView = new ReadyView(this,goodsOn,soundsOn);
		setContentView(readyView);
		mainView = null;
	}

	public void toOptionView(boolean goodsOn,boolean soundsOn){
		optionView = new OptionView(this,goodsOn,soundsOn);
		setContentView(optionView);
		mainView = null;
	}

	public void toTipView(boolean goodsOn,boolean soundsOn){
		tipView = new TipView(this,goodsOn,soundsOn);
		setContentView(tipView);
		mainView = null;
	}

	public void toRankView(boolean goodsOn,boolean soundsOn){
		rankView = new RankView(this,goodsOn,soundsOn);
		setContentView(rankView);
		mainView = null;
	}
}
