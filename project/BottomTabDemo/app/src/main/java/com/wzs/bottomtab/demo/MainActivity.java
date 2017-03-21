package com.wzs.bottomtab.demo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 主activity
 * @author wu_zhongshan@163.com
 *
 */
public class MainActivity extends FragmentActivity implements OnClickListener {

	// 三个tab布局
	private RelativeLayout tab1Layout, tab2Layout, tab3Layout;

	// 底部标签切换的Fragment
	private Fragment tab1Fragment, tab2Fragment, tab3Fragment,
			currentFragment;
	// 底部标签图片
	private TextView tab1Img, tab2Img, tab3Img,backImg;
	private LinearLayout backLayout;
	// 底部标签的文本
	private TextView tab1Tv, tab2Tv, tab3Tv;


	private ViewPager mViewPager;
	private ArrayList<Fragment> fragmentList;

	private int currIndex;//当前页卡编号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUI();

		Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
		tab1Img.setTypeface(iconFont);
		tab2Img.setTypeface(iconFont);
		tab3Img.setTypeface(iconFont);
		backImg.setTypeface(iconFont);
		mViewPager.setCurrentItem(0);

		hide_backIcon();
	}
	private void show_backIcon() {
		backImg.setVisibility(View.VISIBLE);
	}
	private void hide_backIcon() {
		backImg.setVisibility(View.INVISIBLE);
	}
	/**
	 * 初始化UI
	 */
	private void initUI() {
		tab1Layout = (RelativeLayout) findViewById(R.id.rl_tab1);
		tab2Layout = (RelativeLayout) findViewById(R.id.rl_tab2);
		tab3Layout = (RelativeLayout) findViewById(R.id.rl_tab3);
		tab1Layout.setOnClickListener(this);
		tab2Layout.setOnClickListener(this);
		tab3Layout.setOnClickListener(this);

		backLayout= (LinearLayout) findViewById(R.id.back);
		backLayout.setOnClickListener(this);

		tab1Img = (TextView) findViewById(R.id.iv_tab1);
		tab2Img = (TextView) findViewById(R.id.iv_tab2);
		tab3Img = (TextView) findViewById(R.id.iv_tab3);
		backImg= (TextView) findViewById(R.id.backImg);
		//backImg.setOnClickListener(this);
		tab1Tv = (TextView) findViewById(R.id.tv_tab1);
		tab2Tv = (TextView) findViewById(R.id.tv_tab2);
		tab3Tv = (TextView) findViewById(R.id.tv_tab3);


		mViewPager = (ViewPager) findViewById(R.id.content_layout);
		fragmentList = new ArrayList<Fragment>();
		tab1Fragment = new mainTab1Fragment();
		tab2Fragment = new mainTab2Fragment();
		tab3Fragment = new mainTab3Fragment();

		fragmentList.add(tab1Fragment);
		fragmentList.add(tab2Fragment);
		fragmentList.add(tab3Fragment);

		mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
		mViewPager.setCurrentItem(0);//设置当前显示标签页为第一页
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
		ViewPagerScroller scroller = new ViewPagerScroller(this);//自定义滑动速度
		scroller.setScrollDuration(1000);
		scroller.initViewPagerScroll(mViewPager);

		clickTabLayout_1();

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.rl_tab1: // 知道
				changeView(0);
				clickTabLayout_1();
				break;
			case R.id.rl_tab2: // 我想知道
				changeView(1);
				clickTabLayout_2();
				break;
			case R.id.rl_tab3: // 我的
				changeView(2);
				clickTabLayout_3();
				break;
			case R.id.back:
				mainTab2Fragment.goBackWeb();
				break;
			default:
				break;
		}
	}

	/**
	 * 点击第一个tab
	 */
	public void clickTabLayout_1() {
		// 设置底部tab变化
		tab1Img.setTextColor(getResources().getColor(R.color.bottomtab_press));
		tab1Tv.setTextColor(getResources().getColor(R.color.bottomtab_press));
		tab2Img.setTextColor(getResources().getColor(
				R.color.bottomtab_normal));
		tab2Tv.setTextColor(getResources().getColor(
				R.color.bottomtab_normal));
		tab3Img.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		tab3Tv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		hide_backIcon();
	}

	/**
	 * 点击第二个tab
	 */
	public void clickTabLayout_2() {
		tab1Img.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		tab1Tv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		tab2Img.setTextColor(getResources().getColor(
				R.color.bottomtab_press));
		tab2Tv.setTextColor(getResources().getColor(
				R.color.bottomtab_press));
		tab3Img.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		tab3Tv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		show_backIcon();
	}

	/**
	 * 点击第三个tab
	 */
	public void clickTabLayout_3() {
		tab1Img.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		tab1Tv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		tab2Img.setTextColor(getResources().getColor(
				R.color.bottomtab_normal));
		tab2Tv.setTextColor(getResources().getColor(
				R.color.bottomtab_normal));
		tab3Img.setTextColor(getResources().getColor(R.color.bottomtab_press));
		tab3Tv.setTextColor(getResources().getColor(R.color.bottomtab_press));
		hide_backIcon();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = false;
		if (currIndex == 1) {
			flag = mainTab2Fragment.onKeyDown(keyCode, event);//用类的静态方法解决 fragment没有onKeyDown方法
		}
		if (flag)
			return true;//事件不往上传递，后续不再处理
		else
			return super.onKeyDown(keyCode, event);//父类继续处理

	}

	//手动设置ViewPager要显示的视图
	public void changeView(int desTab) {
		mViewPager.setCurrentItem(desTab, true);
	}



	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageSelected(int arg0) {
			currIndex = arg0;
			int i = currIndex + 1;
			switch (i) {
				case 1:
					clickTabLayout_1();
					break;
				case 2:
					clickTabLayout_2();
					break;
				case 3:
					clickTabLayout_3();
					break;
				default:
					break;
			}
		}
	}

}
