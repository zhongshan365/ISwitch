package com.wzs.bottomtab.demo;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;

/**
 * 标签页1
 * @author wu_zhongshan@163.com
 *
 */
public class mainTab1Fragment extends Fragment {
	public View view_fragment ;
	private Context context=getActivity();
	public Bundle myBudle;


	public final static int REQUEST_CONNECT_DEVICE = 1; // 宏定义查询设备句柄
	public final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; // SPP服务UUID号
	public static final int REQUEST_ENABLE = 0;

	public InputStream is; // 输入流，用来接收蓝牙数据
	public String smsg = ""; // 显示用数据缓存
	public String tmpString="";

	public boolean bRun = true;
	public boolean bThread = false;

	//private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter(); // 获取本地蓝牙适配器，即蓝牙设备
	public ArrayAdapter<String> mPairedDevicesArrayAdapter;

	//以下摇一摇开关 by zhongshan
	public Button sharkSwitch;
	public SensorManager sensorManager;
	public Vibrator vibrator;
	public int delayShake = 0;
	public boolean isShakeOpen = false;
	public boolean isShake = true;


	//蓝牙类
	public BlueTooth blueTooth=new BlueTooth();

	//主页面方块按钮
	public Button setButton;//打开设置界面
	public ToggleButton switch_tb;//开关按钮

	public Button btnadd;//连接-断开 按钮

	//以下为定时  by zhongshan
	public Button time_open_tb;
	public Button time_close_tb;

	//定义显示时间控件
	public Calendar calendar; //通过Calendar获取系统时间
	public int mHour;
	public int mMinute;
	public AlertDialog.Builder builder ;
	public TimePicker timePicker;

	public boolean isOpen = false;
	public boolean firstCreat = true;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view_fragment = inflater.inflate(R.layout.main_tab1_fragment, container,
				false);
		myBudle=savedInstanceState;

		//init_layout();
		return view_fragment;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context=getActivity();
		init_all();
		if(firstCreat)//防止多次弹出
		{
			firstCreat=false;
			auto_connect();
		}
	}

	public void init_all()
	{
		SharedPreferences preferences = context.getSharedPreferences("count", 0);
		int count = preferences.getInt("count", 0);
		if (count == 0) {
			SharedPreferences.Editor sharedata = context.getSharedPreferences("Add", 0).edit();
			sharedata.clear();
			sharedata.commit();
		}
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("count", ++count);
		editor.commit();

		calendar = Calendar.getInstance();
		setButton=(Button) view_fragment.findViewById(R.id.setButton);
		setButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				((MainActivity)context).changeView(2);
				((MainActivity)context).clickTabLayout_3();

			}
		});
		switch_tb=(ToggleButton) view_fragment.findViewById(R.id.open_closeButton);
		switch_tb.setChecked(false); // 显示原状态
		switch_tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			// @Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {
				if (isChecked)
				{
					blueTooth.sendString("K",context);
					switch_tb.setTextOn("已开启");
				}
				else{
					blueTooth.sendString("G",context);
					switch_tb.setTextOff("已关闭");
				}
			}
		});
		//定时模块
		time_open_tb=(Button) view_fragment.findViewById(R.id.time_open_bt);
		time_open_tb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//自定义控件
				builder = new AlertDialog.Builder(context);
				View view =  getLayoutInflater(myBudle).inflate(R.layout.time_dialog, null);//(LinearLayout)
				timePicker = (TimePicker) view.findViewById(R.id.time_picker);
				//初始化时间
				calendar.setTimeInMillis(System.currentTimeMillis());
				timePicker.setIs24HourView(true);
				timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
				timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
				//设置time布局
				builder.setView(view);
				builder.setTitle("设置定时开时间");
				builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mHour = timePicker.getCurrentHour();
						mMinute = timePicker.getCurrentMinute();
						//时间小于10的数字 前面补0 如01:12:00
						//timeEdit.setText(new StringBuilder().append(mHour < 10 ? "0" + mHour : mHour).append(":")
						//.append(mMinute < 10 ? "0" + mMinute : mMinute).append(":00") );
						calendar.setTimeInMillis(System.currentTimeMillis());
						int hour=calendar.get(Calendar.HOUR_OF_DAY);
						int min=calendar.get(Calendar.MINUTE);
						int period=(mHour*60+mMinute) -( hour*60+min);
						if(period<0)
						{
							Toast.makeText(context, "时间不能小于当前时间",Toast.LENGTH_SHORT).show();
							time_open_tb.setText("设置定时开时间");
							return ;
						}
						StringBuffer period_str=new StringBuffer();
						if(mHour<10)
						{
							period_str.append("0"+mHour);
						}
						else {
							period_str.append(mHour);
						}
						if(mMinute<10)
						{
							period_str.append("0"+mMinute);
						}
						else {
							period_str.append(mMinute);
						}
						boolean isSuccess=blueTooth.sendString("BB"+period_str.toString(),context);//约定好要发送的定时格式
						if(isSuccess)
							time_open_tb.setText("将于 "+mHour+"时"+mMinute+"分 开启");
						dialog.cancel();
					}
				});
				builder.setNegativeButton("取  消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.cancel();
					}
				});
				builder.create().show();
			}
		});
		time_close_tb=(Button) view_fragment.findViewById(R.id.time_close_bt);
		time_close_tb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//自定义控件
				builder = new AlertDialog.Builder(context);
				View view =  getLayoutInflater(myBudle).inflate(R.layout.time_dialog, null);//(LinearLayout)
				timePicker = (TimePicker) view.findViewById(R.id.time_picker);
				//初始化时间
				calendar.setTimeInMillis(System.currentTimeMillis());
				timePicker.setIs24HourView(true);
				timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
				timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
				//设置time布局
				builder.setView(view);
				builder.setTitle("设置定时关时间");
				builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mHour = timePicker.getCurrentHour();
						mMinute = timePicker.getCurrentMinute();
						calendar.setTimeInMillis(System.currentTimeMillis());
						int hour=calendar.get(Calendar.HOUR_OF_DAY);
						int min=calendar.get(Calendar.MINUTE);
						int period=(mHour*60+mMinute) -( hour*60+min);
						if(period<0)
						{
							Toast.makeText(context, "时间不能小于当前时间",Toast.LENGTH_SHORT).show();
							time_open_tb.setText("设置定时开时间");
							return ;
						}
						StringBuffer period_str=new StringBuffer();
						if(mHour<10)
						{
							period_str.append("0"+mHour);
						}
						else {
							period_str.append(mHour);
						}
						if(mMinute<10)
						{
							period_str.append("0"+mMinute);
						}
						else {
							period_str.append(mMinute);
						}
						boolean isSuccess=blueTooth.sendString("CB"+period_str.toString(),context);
						if(isSuccess)
							time_close_tb.setText("将于 "+mHour+"时"+mMinute+"分 关闭");
						dialog.cancel();
					}
				});
				builder.setNegativeButton("取  消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_SHORT).show();
						dialog.cancel();
					}
				});
				builder.create().show();
			}
		});

		sharkSwitch=(Button) view_fragment.findViewById(R.id.sharkSwitch);
		sharkSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				if (!isShakeOpen)
				{
					isShakeOpen=true;
					sharkSwitch.setText("摇一摇\n已开启");
					sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
					vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
					sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
					Toast.makeText(context, "请摇晃手机~", Toast.LENGTH_SHORT).show();
				}
				else
				{
					isShakeOpen=false;
					sharkSwitch.setText("摇一摇\n已关闭");
					if (sensorManager != null) {// 取消监听器
						sensorManager.unregisterListener(sensorEventListener);
					}
					vibrator =null;
					sensorManager =null;
					Toast.makeText(context, "摇一摇功能已关闭", Toast.LENGTH_SHORT).show();
				}
			}
		});
		btnadd = (Button) view_fragment.findViewById(R.id.addButton);
		btnadd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				connect();
			}
		});

		mainTab3Fragment.blueTooth=blueTooth;//静态对象传递

		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(context, R.layout.device_name);
	}
	public  void auto_connect(){
		// 如果打开本地蓝牙设备不成功，提示信息，结束程序
		if (blueTooth._bluetooth == null) {
			Toast.makeText(context, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_LONG)
					.show();
			getActivity().finish();
			return;
		}
		if (blueTooth._bluetooth.isEnabled() == false) { // 如果蓝牙服务不可用则提示
			Toast.makeText(context, " 打开蓝牙中...",
					Toast.LENGTH_SHORT).show();

			new Thread() {
				public void run() {
					if (blueTooth._bluetooth.isEnabled() == false) {
//						_bluetooth.enable();//强制打开蓝牙，不提示
						Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enabler, REQUEST_ENABLE);
					}
				}
			}.start();
		}
		if (blueTooth._bluetooth.isEnabled() == false)
		{
			Toast.makeText(context, "等待蓝牙打开，5秒后，尝试连接！", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable(){   //延迟执行
				@Override
				public void run(){
					if (blueTooth._bluetooth.isEnabled() == false)
					{
						Toast.makeText(context, "自动打开蓝牙失败，请手动打开蓝牙！", Toast.LENGTH_SHORT).show();
						//询问打开蓝牙
						Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enabler, REQUEST_ENABLE);
					}
					else
						connect(); //自动进入连接

				}
			}, 5000);

		}
		else
		{
			connect(); //自动进入连接
		}

		if(blueTooth._socket != null)
		{
			//此处进行时间同步
			Calendar c=Calendar.getInstance();
			StringBuffer str=new StringBuffer();
			if((c.get(Calendar.MONTH)+1)<10)
			{
				str.append("0"+(c.get(Calendar.MONTH)+1));
			}
			else {
				str.append((c.get(Calendar.MONTH)+1));
			}
			str.append(",");
			if(c.get(Calendar.DAY_OF_MONTH)<10)
			{
				str.append("0"+c.get(Calendar.DAY_OF_MONTH));
			}
			else {
				str.append(c.get(Calendar.DAY_OF_MONTH));
			}
			str.append(",");
			if(c.get(Calendar.HOUR_OF_DAY)<10)
			{
				str.append("0"+c.get(Calendar.HOUR_OF_DAY));
			}
			else {
				str.append(c.get(Calendar.HOUR_OF_DAY));
			}
			str.append(",");
			if(c.get(Calendar.MINUTE)<10)
			{
				str.append("0"+c.get(Calendar.MINUTE));
			}
			else {
				str.append(c.get(Calendar.MINUTE));
			}
			str.append(",");
			if(c.get(Calendar.SECOND)<10)
			{
				str.append("0"+c.get(Calendar.SECOND));
			}
			else {
				str.append(c.get(Calendar.SECOND));
			}
			//Log.d("testmy", ""+(c.get(Calendar.DAY_OF_WEEK)-1));
			blueTooth.sendString("A"+(c.get(Calendar.DAY_OF_WEEK)-1)+","+c.get(Calendar.YEAR)+","+str.toString(),context);
		}
	}
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

				disconnect();
			}
		}
	};
	public void disconnect()
	{
		//取消注册异常断开接收器
		context.unregisterReceiver(mReceiver);

		Toast.makeText(context, "线路已断开，请重新连接！", Toast.LENGTH_SHORT).show();
		// 关闭连接socket
		try {
			bRun = false; // 一定要放在前面
			is.close();
			blueTooth.disconnect();
			//bRun = false;
			btnadd.setText("已断开");
		} catch (IOException e) {
		}
	}
	public void connect()
	{
		if (blueTooth._bluetooth.isEnabled() == false) { // 如果蓝牙服务不可用则提示
			//询问打开蓝牙
			Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enabler, REQUEST_ENABLE);
			return;
		}

		// 如未连接设备则打开DeviceListActivity进行设备搜索
		if (blueTooth._socket == null) {
			//mPairedDevicesArrayAdapter.clear();
			Intent serverIntent = new Intent(context,
					DeviceListActivity.class); // 跳转程序设置
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); // 设置返回宏定义
		} else {
			disconnect();
		}
		return;

	}

	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
			// Log.i(TAG, "x轴方向的重力加速度" + x +  "；y轴方向的重力加速度" + y +  "；z轴方向的重力加速度" + z);
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
			//float medumValue = (float) 19.2;
			if (isShake && (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue)) {
				vibrator.vibrate(new long[]{300,300,300,300}, -1);//第一个｛｝里面是节奏数组， 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
				//vibrator.vibrate(1000);
				//Toast.makeText(MainActivity.this, "shark", Toast.LENGTH_SHORT).show();
				isShake=false;
				if (!isOpen)//本来关的状态，现在要打开
				{
					//open_or_close=0;
					blueTooth.sendString("K",context);
				}
				else
				{
					//open_or_close=1;
					blueTooth.sendString("G",context);
				}

			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	// 关闭程序掉用处理部分
	public void onDestroy() {
		super.onDestroy();
		if (blueTooth._socket != null) // 关闭连接socket
			blueTooth.disconnect();

		//_bluetooth.disable(); //关闭蓝牙服务

		android.os.Process.killProcess(android.os.Process.myPid()); // 终止线程
	}

	// 接收活动结果，响应startActivityForResult()
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE: // 连接结果，由DeviceListActivity设置返回
				// 响应返回结果
				if (resultCode == Activity.RESULT_OK) { // 连接成功，由DeviceListActivity设置返回
					// MAC地址，由DeviceListActivity设置返回
					String address = data.getExtras().getString(
							DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					// 得到蓝牙设备句柄
					blueTooth._device = blueTooth._bluetooth.getRemoteDevice(address);
					// 用服务号得到socket
					try {
						blueTooth._socket = blueTooth._device.createRfcommSocketToServiceRecord(UUID
								.fromString(MY_UUID));
					} catch (IOException e) {

						Toast.makeText(context, "连接失败,无法得到Socket！"+e, Toast.LENGTH_SHORT).show();

					}


					// 连接socket
					try {
						blueTooth._socket.connect();

						Toast.makeText(context, "连接" + blueTooth._device.getName() + "成功！",
								Toast.LENGTH_SHORT).show();
						//mPairedDevicesArrayAdapter.add(blueTooth._device.getName() + "\n"
						//		+ blueTooth._device.getAddress());
						SharedPreferences.Editor sharedata = context.getSharedPreferences("Add", 0).edit();
						sharedata.putString(String.valueOf(0),blueTooth._device.getName());
						sharedata.putString(String.valueOf(1),blueTooth._device.getAddress());
						sharedata.commit();

						btnadd.setText("已连接");

						//注册异常断开接收器  等连接成功后注册
						IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
						context.registerReceiver(mReceiver, filter);

					} catch (IOException e) {
						btnadd.setText(getResources().getString(R.string.add));

						Toast.makeText(context, "连接失败！"+e, Toast.LENGTH_SHORT)
								.show();
						blueTooth.disconnect();
						//清空自动连接缓存
						SharedPreferences.Editor sharedata = context.getSharedPreferences("Add", 0).edit();
						sharedata.clear();
						sharedata.commit();

						return;
					}

					// 打开接收线程
					try {
						is = blueTooth._socket.getInputStream(); // 得到蓝牙数据输入流
					} catch (IOException e) {
						Toast.makeText(context, "异常：打开接收线程！"+e, Toast.LENGTH_SHORT).show();
						btnadd.setText(getResources().getString(R.string.add));
						return;
					}
					if (bThread == false) {// 如果没有启动线程，就启动线程;否则开始接收
						{
							ReadThread.start();
							bThread = true;

						}
					} else {
						bRun = true;
					}

					//此处进行时间同步
					Calendar c=Calendar.getInstance();
					StringBuffer str=new StringBuffer();
					if((c.get(Calendar.MONTH)+1)<10)
					{
						str.append("0"+(c.get(Calendar.MONTH)+1));
					}
					else {
						str.append((c.get(Calendar.MONTH)+1));
					}
					str.append(",");
					if(c.get(Calendar.DAY_OF_MONTH)<10)
					{
						str.append("0"+c.get(Calendar.DAY_OF_MONTH));
					}
					else {
						str.append(c.get(Calendar.DAY_OF_MONTH));
					}
					str.append(",");
					if(c.get(Calendar.HOUR_OF_DAY)<10)
					{
						str.append("0"+c.get(Calendar.HOUR_OF_DAY));
					}
					else {
						str.append(c.get(Calendar.HOUR_OF_DAY));
					}
					str.append(",");
					if(c.get(Calendar.MINUTE)<10)
					{
						str.append("0"+c.get(Calendar.MINUTE));
					}
					else {
						str.append(c.get(Calendar.MINUTE));
					}
					str.append(",");
					if(c.get(Calendar.SECOND)<10)
					{
						str.append("0"+c.get(Calendar.SECOND));
					}
					else {
						str.append(c.get(Calendar.SECOND));
					}
					//Log.d("testmy", ""+(c.get(Calendar.DAY_OF_WEEK)-1));
					blueTooth.sendString("A"+(c.get(Calendar.DAY_OF_WEEK)-1)+","+c.get(Calendar.YEAR)+","+str.toString(),context);//约定好要发送的定时格式
					// Log.d("testmy", "A"+(c.get(Calendar.DAY_OF_WEEK)-1)+","+c.get(Calendar.YEAR)+","+str.toString());
				}
				break;
			default:
				break;
		}
	}
	// 接收数据线程
	Thread ReadThread = new Thread() {

		public void run() {
			int num = 0;
			byte[] buffer = new byte[1024];
			byte[] buffer_new = new byte[1024];
			bRun = true;
			// 接收线程
			while (true) {
				try {
					while (is.available() == 0) {        //无接收数据
						while (bRun == false) {   //线程阻塞
						}
					}

					while (true) {

						if (1 == is.read(buffer, 0, 1)) // 一个一个地接收，把需要的数据放在buffer_new中
						{
							//if(hex)
							if(false)
							{
								smsg+=String.format("%02X ",buffer[0]);//转为十六进制格式 所有接收
							}
							else
							{
								if(127<(buffer[0]&0xff)) //解决汉字被截断
								{
									buffer_new[num++]=buffer[0];
									if(num==2)
									{
										smsg+=new String(buffer_new, 0, 2, "GB2312");  //+String.format("(%02X %02X )",buffer_new[0],buffer_new[1]);  //GB2312   GBK  UTF-8
										num=0;
									}
								}
								else
									smsg+=new String(buffer, 0, 1, "GB2312");  //GB2312   GBK  UTF-8
								tmpString += new String(buffer, 0, 1, "GB2312");
							}

						}

						if (is.available() == 0)
							break; // 短时间没有数据才跳出进行显示
					}

					// 发送显示消息，进行显示刷新
					handler.sendMessage(handler.obtainMessage());
				} catch (IOException e) {
				}
			}
		}
	};
	// 消息处理队列,解决子线程无法更新ui控件问题
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (tmpString.contains("BA") )
			{
				Toast.makeText(context, "定时开成功", Toast.LENGTH_SHORT).show();
			}
			if (tmpString.contains("CA") )
			{
				Toast.makeText(context, "定时关成功", Toast.LENGTH_SHORT).show();
			}

			if (tmpString.contains("PIN") )
			{
				Toast.makeText(context, "重置密码成功", Toast.LENGTH_SHORT).show();
			}
			if (tmpString.contains("NAME") )
			{
				Toast.makeText(context, "重置名称成功", Toast.LENGTH_SHORT).show();
			}
			if (tmpString.contains("KS") )//当前状态是开
			{
				isOpen=true;
				switch_tb.setChecked(true);
			}
			if (tmpString.contains("GS") )//当前状态是关
			{
				isOpen=false;
				switch_tb.setChecked(false);
			}
			if (tmpString.contains("\n"))
			{
				tmpString="";
			}
			if(!isShake)
			{
				++delayShake;
				if(delayShake>15)
				{
					isShake=true;
					delayShake=0;
				}
			}
		}
	};
	@Override
	public void onResume() {
		super.onResume();
		if (sensorManager != null) {// 注册监听器
			sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
			// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

}
