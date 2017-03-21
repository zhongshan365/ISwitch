package com.wzs.bottomtab.demo;

import java.io.IOException;
import java.io.OutputStream;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

/**
 * 蓝牙服务类  可用于多个activity之间提供蓝牙服务
 * @author wu_zhongshan@163.com
 *
 */
public class BlueTooth {

	boolean hex=false;
	public BluetoothDevice _device = null; // 蓝牙设备
	public BluetoothSocket _socket = null; // 蓝牙通信socket
	public BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter(); // 获取本地蓝牙适配器，即蓝牙设备

	public boolean sendString(String str,Context context) {
		if (_socket == null) {
			Toast.makeText(context.getApplicationContext(), "未连接蓝牙", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (str == null) {
			Toast.makeText(context.getApplicationContext(), "发送内容为空", Toast.LENGTH_SHORT).show();
			return false;
		}

		try {

			OutputStream os = _socket.getOutputStream(); // 蓝牙连接输出流
			if(hex)
			{

				byte[] bos_hex = hexStringToBytes(str); // 十六进制
				os.write(bos_hex);
			}
			else
			{
				byte[] bos = str.getBytes("GB2312");	//native的Socket发送字节流默认是GB2312的，所以在Java方面需要指定GB2312
				os.write(bos);
			}

		} catch (IOException e) {
		}
		return true;
	}
	public static byte[] hexStringToBytes(String hexString) {
		hexString = hexString.replaceAll(" ", ""); // 去空格
		if ((hexString == null) || (hexString.equals(""))) {
			return null;
		}
		hexString = hexString.toUpperCase(); // 字符串中的所有字母都被转化为大写字母
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; ++i) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[(pos + 1)]));
		}
		return d;
	}
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	public void disconnect()
	{
		try {
			_socket.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		_socket = null;
	}

}
