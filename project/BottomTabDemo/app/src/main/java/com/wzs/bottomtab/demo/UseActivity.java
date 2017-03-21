package com.wzs.bottomtab.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
/**
 * 设置界面
 * @author wu_zhongshan@163.com
 *
 */
public class UseActivity extends Activity {
	public static BlueTooth blueTooth;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.use);
		RelativeLayout resetPwd = (RelativeLayout) findViewById(R.id.rePwd);
		RelativeLayout resetName = (RelativeLayout) findViewById(R.id.reName);
		RelativeLayout aboutBtn = (RelativeLayout) findViewById(R.id.aboutBtn);

		aboutBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent aboutIntent = new Intent(UseActivity.this,
						AboutActivity.class); // 跳转关于界面
				startActivity(aboutIntent);
			}
		});

		resetPwd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				//Toast.makeText(UseActivity.this, "ok", Toast.LENGTH_SHORT).show();
				final EditText editText = new EditText(UseActivity.this);
				new AlertDialog.Builder(UseActivity.this)
						.setTitle("请输入四位密码")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(editText)
						.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								String pwd=replaceBlank(editText.getText().toString());
								if( isNumber4(pwd) )
								{
									blueTooth.sendString("PIN"+pwd,UseActivity.this);
								}
								else {
									new  AlertDialog.Builder(UseActivity.this)
											.setTitle("提示" )
											.setMessage("密码只能是4位数字" )
											.setPositiveButton("确定" ,  null )
											.show();
								}
							}
						})
						.setNegativeButton("取消", null)
						.show();
			}
		});
		resetName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final EditText editText = new EditText(UseActivity.this);
				new AlertDialog.Builder(UseActivity.this)
						.setTitle("请输入四位数字或字母组合")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(editText)
						.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								String name=replaceBlank(editText.getText().toString());
								if( isNumLetter4(name) )
								{
									blueTooth.sendString("NAME"+name,UseActivity.this);
								}
								else {
									new  AlertDialog.Builder(UseActivity.this)
											.setTitle("提示" )
											.setMessage("名称只能是4位数字或字母组合" )
											.setPositiveButton("确定" ,  null )
											.show();
								}
							}
						})
						.setNegativeButton("取消", null)
						.show();
			}
		});

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

	}
	public String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	public boolean isNumber4(String str) {
		Pattern p = Pattern.compile("^\\d{4}$");
		Matcher m = p.matcher(str);
		return m.find();
	}
	public boolean isNumLetter4(String str) {
		Pattern p = Pattern.compile("^[0-9a-zA-Z]{4}$");
		Matcher m = p.matcher(str);
		return m.find();
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

