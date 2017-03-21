package com.wzs.bottomtab.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 标签页2
 * @author wu_zhongshan@163.com
 *
 */
public class mainTab2Fragment extends Fragment {
	private static WebView webview;
	private Context context=getActivity();
	public View view_fragment ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view_fragment = inflater.inflate(R.layout.main_tab2_fragment, container,
				false);
		return view_fragment;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context=getActivity();
		webview = (WebView) view_fragment.findViewById(R.id.webView);
		//设置WebView属性，能够执行Javascript脚本
		webview.getSettings().setJavaScriptEnabled(true);
		//加载需要显示的网页
		webview.loadUrl("http://www.99cyi.com/m/");
		//设置Web视图
		webview.setWebViewClient(new HelloWebViewClient ());
	}


	//Web视图
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	public static boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack(); //goBack()表示返回WebView的上一页面
			return true;
		}
		return false;
	}
	public static void goBackWeb() {
		if (webview.canGoBack()) {
			webview.goBack(); //goBack()表示返回WebView的上一页面
		}
	}

}
