package com.alex.alexchat.activity;
import github.common.utils.DiskUtil;
import github.common.utils.ImageUtil;
import github.common.utils.TimeUtils;
import github.common.utils.ToastUtil;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.alex.alexchat.R;
import com.alex.alexchat.config.Cache;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapScreenShotListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.jakewharton.disklrucache.DiskLruCache;
import com.lidroid.xutils.util.LogUtils;
public class LocationActivity extends BaseActivity 
{
	private MapView mapView;
	private AMap aMap;
	private LocationManagerProxy locationManagerProxy;
	private MyAMapLocationListener aMapLocationListener;
	private AMapLocation aMapLocation;
	/**当前经纬度*/
	private LatLonPoint currLatLon;
	protected float zoomXXXLarge = 19f;
	protected float zoomXXLarge = 15f;
	protected float zoomXLarge = 12f;
	/**位置变化通知距离，单位为米。*/
	private static final float minDistance = 100;
	/**位置变化的通知时间，单位为毫秒。如果为-1，定位只定位一次。*/
	//1000 * 10
	private static final long minTime = 1000 * 10;
	/**已经获取高德地图的 截图*/
	private boolean hasScreenShot;
	/**高德地图 的截图*/
	private Bitmap bitmapScreenShot;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		hasScreenShot = false;
		initView(savedInstanceState);
		initAMap();
		startLocation();
	}
	private void initView(Bundle savedInstanceState) {
		mapView = (MapView) findViewById(R.id.mv);
		/*必须要写*/
		mapView.onCreate(savedInstanceState);
		findViewById(R.id.tv_confirm).setOnClickListener(new MyOnClickListener());
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			if(R.id.tv_confirm == v.getId()){
				aMap.getMapScreenShot(new MyOnMapScreenShotListener());
				if(!hasScreenShot){
					ToastUtil.shortAtTop(context, "等待截图");
					return;
				}
				
			}else if(R.id.tv_back == v.getId()){
				finish();
			}
		}
	}
	/**AMap配置信息
	 * @time 2015-03-03    16:15*/
	private void initAMap()
	{
		aMap = mapView.getMap();
		aMap.setMapType(AMap.MAP_TYPE_NORMAL);
		aMap.getUiSettings().setMyLocationButtonEnabled(true);
		aMap.setMyLocationEnabled(true);
		aMap.getUiSettings().setMyLocationButtonEnabled(true);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomXLarge));
		aMap.setLocationSource(new MyLocationSource());
		
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location));
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));
		aMap.setMyLocationStyle(myLocationStyle);
	}
	private final class MyLocationSource implements LocationSource
	{
		@Override
		public void activate(OnLocationChangedListener listener) {
			startLocation();
		}
		@Override
		public void deactivate() {
			stopLocation();
		}
	}
	private final class MyOnMapScreenShotListener implements OnMapScreenShotListener
	{
		@Override
		public void onMapScreenShot(Bitmap bitmap) {
			if(bitmap!=null && bitmap.getHeight()>0 && bitmap.getWidth()>0){
				bitmapScreenShot = bitmap;
				hasScreenShot  = true;
				/*将截图信息 反馈给上个 页面*/
				Intent data = new Intent();
				DiskLruCache diskLruCache = DiskUtil.open(context, Cache.imageDiskCacheDir);
				String imageKey = TimeUtils.getCurrentTimeInLongString();
				DiskUtil.write(diskLruCache, imageKey , ImageUtil.bitMap2Byte(bitmapScreenShot));
				data.putExtra("imageKey", imageKey);
				data.putExtra("latitude", currLatLon.getLatitude());
				data.putExtra("longitude", currLatLon.getLongitude());
				data.putExtra("address", aMapLocation.getAddress()+"");
				setResult(Activity.RESULT_OK, data);
				finish();
			}
		}
	}
	/**AMap定位的回调接口
	 * @time 2015-03-04    12:31*/
	private final class MyAMapLocationListener implements AMapLocationListener
	{
		@Override
		public void onLocationChanged(Location location){	
			aMapLocationListener.onLocationChanged(location);
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			LogUtils.e("onStatusChanged");
		}
		@Override
		public void onProviderEnabled(String provider)
		{
			LogUtils.e("onProviderEnabled");
		}
		@Override
		public void onProviderDisabled(String provider)
		{
			LogUtils.e("onProviderDisabled");
		}
		@Override
		public void onLocationChanged(AMapLocation aMapLocation)
		{
			if(aMapLocation==null || aMapLocation.getAMapException().getErrorCode()!=0){
				LogUtils.e("网络异常");
				ToastUtil.shortAtTop(context, "网络异常");
				return;
			}
			LocationActivity.this.aMapLocation = aMapLocation;
			currLatLon = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
			MarkerOptions options = new MarkerOptions();
			options.position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
			BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_location));
			options.icon(descriptor );
			aMap.addMarker(options);
			aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(currLatLon.getLatitude(), currLatLon.getLongitude())));
			aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomXXLarge));
			LogUtils.e("定位结束:  Latitude = "+currLatLon.getLatitude()+"  Longitude"+currLatLon.getLongitude());
			
			dialogLoading.dismiss();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	/**方法必须重写*/
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		stopLocation();
	}
	/**开始AMap定位
	 * @time 2015-03-04    13:51*/
	private void startLocation() {
		locationManagerProxy = (locationManagerProxy==null) ? LocationManagerProxy.getInstance(context) : locationManagerProxy;
		aMapLocationListener = (aMapLocationListener==null) ? new MyAMapLocationListener() : aMapLocationListener;
		locationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, minTime, minDistance, aMapLocationListener);
		locationManagerProxy.setGpsEnable(true);
		dialogLoading.show();
	}
	/**停止AMap定位
	 * @time 2015-03-04    13:51*/
	private void stopLocation()
	{
		if(locationManagerProxy != null)
		{
			locationManagerProxy.removeUpdates(aMapLocationListener);
			locationManagerProxy.destroy();
		}
		locationManagerProxy = null;
		aMapLocationListener = null;
	}
	@Override
	public void initView()
	{
		
	}
}
