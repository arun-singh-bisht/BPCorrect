/**
 * 
 */
package com.protechgene.android.bpconnect.deviceManager.Transtek;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lifesense.ble.bean.LsDeviceInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author CaiChiXiang
 *
 */
public class AsyncTaskRunner
{
	private static final String TAG= AsyncTaskRunner.class.getSimpleName();

	private LsDeviceInfo saveDeviceInfo;
	private Context mAppContext;

	
	public static void savePairedDeviceInfo(Context mAppContext,final LsDeviceInfo lsDeviceInfo)
	{
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				if(lsDeviceInfo!=null)
				{
					//save paired device info to file
					String key=PairedDeviceInfo.class.getName();
					savePairedDeviceInfoToFile(mAppContext, key,lsDeviceInfo);
				}
			}
		});
	}

	private static void savePairedDeviceInfoToFile(Context appContext,String key,LsDeviceInfo pairedDevice)
	{
		if(pairedDevice==null)
		{
			return ;
		}
		String deviceKey=pairedDevice.getDeviceId();
		if(!TextUtils.isEmpty(pairedDevice.getBroadcastID()))
		{
			deviceKey=pairedDevice.getBroadcastID();
		}
		HashMap<String,LsDeviceInfo> pairedDeviceHashMap=null;
		PairedDeviceInfo savePairedDeviceInfo=PairedDeviceInfo.readPairedDeviceInfoFromFile(appContext);
		if(false && savePairedDeviceInfo!=null)
		{
			pairedDeviceHashMap=savePairedDeviceInfo.getPairedDeviceMap();
			if(pairedDeviceHashMap!=null && pairedDeviceHashMap.size()>0)
			{
				String tempBroadcastID=null;
				tempBroadcastID=isPairedDeviceExist(pairedDeviceHashMap, deviceKey);

				//本地存在已配对过的设备对象信息
				if(tempBroadcastID!=null)
				{
					//该设备已保存在本地文件，则先删除旧对象信息，后添加新的对象信息
					pairedDeviceHashMap.remove(tempBroadcastID);
					pairedDeviceHashMap.put(pairedDevice.getBroadcastID(), pairedDevice);
				}
				else
				{
					//该设备未保存在本地文件
					pairedDeviceHashMap.put(pairedDevice.getBroadcastID(), pairedDevice);
				}
				//放入已配对的设备信息对象属性
				savePairedDeviceInfo.setPairedDeviceMap(pairedDeviceHashMap);
			}
			else
			{
				//初次保存已配对的设备对象信息
				//生成Broadcast ID与设备信息的Map对照表
				HashMap<String,com.lifesense.ble.bean.LsDeviceInfo> deviceInfoMap=new HashMap<String, com.lifesense.ble.bean.LsDeviceInfo>();

				//以Broadcast ID作为Key,已配对的设备信息对象作为Value存入Map对照表
				deviceInfoMap.put(pairedDevice.getBroadcastID(), pairedDevice);

				//放入已配对的设备信息对象属性
				savePairedDeviceInfo.setPairedDeviceMap(deviceInfoMap);
			}
		}
		else
		{
			savePairedDeviceInfo=new PairedDeviceInfo();

			//初次保存已配对的设备对象信息
			//生成Broadcast ID与设备信息的Map对照表
			HashMap<String,com.lifesense.ble.bean.LsDeviceInfo> deviceInfoMap=new HashMap<String, com.lifesense.ble.bean.LsDeviceInfo>();

			//以Broadcast ID作为Key,已配对的设备信息对象作为Value存入Map对照表
			deviceInfoMap.put(pairedDevice.getBroadcastID(), pairedDevice);

			//放入已配对的设备信息对象属性
			savePairedDeviceInfo.setPairedDeviceMap(deviceInfoMap);
		}

		Gson gson = new Gson();
		String deviceInfo=gson.toJson(savePairedDeviceInfo);
		Log.e("将内容写入文件",deviceInfo);
		//key=PairedDeviceInfo.class.getName();
		saveToSharedPreferences(appContext, key, deviceInfo);

	}

	private static String isPairedDeviceExist(Map<String,LsDeviceInfo> hashMap, String deviceId)
	{
		if(deviceId!=null && hashMap!=null && !hashMap.isEmpty())
		{
			Iterator<Map.Entry<String, LsDeviceInfo>> it = hashMap.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<String, LsDeviceInfo> entry = it.next();
				LsDeviceInfo lsDevice=entry.getValue();
				if(lsDevice!=null && deviceId.equals(lsDevice.getDeviceId()))
				{
					return lsDevice.getBroadcastID();
				}
			}
			return null;
		}
		else return null;

	}


	private static void saveToSharedPreferences(Context appContext,String key,String value)
	{
		if(appContext!=null && key!=null && key.length()>0 && value!=null)
		{
			SharedPreferences savePrefs=appContext.getSharedPreferences(
					appContext.getApplicationInfo().name, Context.MODE_PRIVATE);
			SharedPreferences.Editor ed=savePrefs.edit();
			ed.putString(key, value);
			ed.commit();
			Log.d(TAG, "Saved Device Details: "+value);
		}
		else
		{
			Log.e(TAG, "Failed to save content to share preferences,is null....");
		}
	}

}
