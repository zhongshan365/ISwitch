package com.wzs.bottomtab.demo;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * 关于界面
 * @author wu_zhongshan@163.com
 *
 */
public class AboutActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);	
		
		TextView promptTextView = (TextView)findViewById(R.id.about);  
		promptTextView.setMovementMethod(LinkMovementMethod.getInstance());
		
		ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
	}
	@Override  
	 public boolean onOptionsItemSelected(MenuItem item) {  
	     switch (item.getItemId()) {  
	     case android.R.id.home: 
	    	finish(); 
	     } 
	     return true;  
	 }
	
}
