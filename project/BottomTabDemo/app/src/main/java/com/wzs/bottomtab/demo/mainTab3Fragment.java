package com.wzs.bottomtab.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 标签页3
 * @author wu_zhongshan@163.com
 *
 */
public class mainTab3Fragment extends Fragment {
	public static BlueTooth blueTooth;
	public View view_fragment ;
	private Context context=getActivity();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view_fragment = inflater.inflate(R.layout.main_tab3_fragment, container,
				false);
		return view_fragment;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context=getActivity();
		init_all();
	}
	public void init_all(){
		//初始化图标
		Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
		TextView rePwdImg= (TextView) view_fragment.findViewById(R.id.rePwdImg);
		TextView reNameImg= (TextView) view_fragment.findViewById(R.id.reNameImg);
		TextView aboutBtnImg= (TextView) view_fragment.findViewById(R.id.aboutBtnImg);
		rePwdImg.setTypeface(iconFont);
		reNameImg.setTypeface(iconFont);
		aboutBtnImg.setTypeface(iconFont);

		RelativeLayout resetPwd = (RelativeLayout) view_fragment.findViewById(R.id.rePwd);
		RelativeLayout resetName = (RelativeLayout) view_fragment.findViewById(R.id.reName);
		RelativeLayout aboutBtn = (RelativeLayout) view_fragment.findViewById(R.id.aboutBtn);
		aboutBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent aboutIntent = new Intent(context,
						AboutActivity.class); // 跳转关于界面
				startActivity(aboutIntent);
			}
		});

		resetPwd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText editText = new EditText(context);
				new AlertDialog.Builder(context)
						.setTitle("请输入四位密码")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(editText)
						.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								String pwd=replaceBlank(editText.getText().toString());
								if( isNumber4(pwd) )
								{
									blueTooth.sendString("PIN"+pwd,context);
								}
								else {
									new  AlertDialog.Builder(context)
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

				final EditText editText = new EditText(context);
				new AlertDialog.Builder(context)
						.setTitle("请输入四位数字或字母组合")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(editText)
						.setPositiveButton("确定",  new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								String name=replaceBlank(editText.getText().toString());
								if( isNumLetter4(name) )
								{
									blueTooth.sendString("NAME"+name,context);
								}
								else {
									new  AlertDialog.Builder(context)
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

}
