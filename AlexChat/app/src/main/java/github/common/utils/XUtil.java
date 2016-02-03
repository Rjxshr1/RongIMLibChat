package github.common.utils;
import java.io.File;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DaoConfig;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.util.LogUtils;
public class XUtil
{
	/**UTF-8*/
	public static String charset = "UTF-8";
	/**上次已经下载完成*/
	public static String alreadyFinish = "maybe the file has downloaded completely";
	private static HttpUtils httpUtils = null;
	private static DbUtils dbUtils = null;
	public static HttpUtils getHttpUtilInstance()
	{
		if(httpUtils == null){
			synchronized (XUtil.class){
				httpUtils = (httpUtils==null) ? new HttpUtils():httpUtils;
			}
		}
		//设置当前请求的缓存时间
		httpUtils.configCurrentHttpCacheExpiry(5*1000);
		//设置默认请求的缓存时间
		httpUtils.configDefaultHttpCacheExpiry(0);
		httpUtils.configRequestRetryCount(3);
		httpUtils.configTimeout(1000 * 10);
		httpUtils.configSoTimeout(1000 * 10);
		return httpUtils;
	}
	public static DbUtils getDbUtilsInstance(Context context,String dbDir, String dbName, int dbVersion){
		/*创建数据库*/
		DaoConfig daoConfig = new DaoConfig(context);
		if(context==null){
			LogUtils.e("context 为空！！！");
		}
		/*@params dbDir db文件所在的文件夹，在存储卡的全路径*/
		File path1 = new File(dbDir);
		if (!path1.exists()) {
			//若不存在，创建目录，可以在应用启动的时候创建
			@SuppressWarnings("unused")
			boolean mkdirs = path1.mkdir();
			//LogUtils.e("创建  "+path1.getAbsolutePath()+" 成功 = "+mkdirs);
		}
		daoConfig.setDbDir(dbDir);
		/*@params dbName db文件夹的名字*/
		daoConfig.setDbName(dbName);
		/*设置 DB的版本号*/
		daoConfig.setDbVersion(dbVersion);
		if(dbUtils == null){
			synchronized (XUtil.class){
				dbUtils = (dbUtils==null) ? DbUtils.create(daoConfig):dbUtils;
			}
		}
		return dbUtils;
	}
	public static void closeDB(DbUtils dbUtils){
		if(dbUtils!=null){
			dbUtils.close();
		}
	}
	/**增加数据
	 * @param obj 实例对象 不需要序列化
	 * @return true 插入成功 */
	public static boolean save(DbUtils dbUtils, Object obj){
		try {
			dbUtils.save(obj);
			return true;
		} catch (DbException e) {
			LogUtils.e("有异常："+getDBErrorInfo(e));
			return false;
		}
	}
	/**增加数据
	 * @param list 实例对象集合 不需要序列化
	 * @return true 插入成功 */
	public static boolean saveAll(DbUtils dbUtils, List<?> list){
		try {
			dbUtils.saveAll(list);
			return true;
		} catch (DbException e) {
			LogUtils.e("有异常："+getDBErrorInfo(e));
			return false;
		}
	}
	/** 根据id查询 单个
	 * @param entityType 如StuBean.class 
	 * @param idValue id id的值
	 * */
	public static <T> T findById(DbUtils dbUtils, Class<T> entityType, Object idValue){
		try {
			return dbUtils.findById(entityType, idValue);
		} catch (DbException e) {
			LogUtils.e("有异常："+getDBErrorInfo(e));
		}
		return null;
	}
	/**
	 * @param entityType 如StuBean.class 
	 * @param orderByKey 根据 某一个key 进行排序  key可以为null 
	 * @param asc false 降序 ; true 升序*/
	public static <T> List<T> findAll(DbUtils dbUtils, Class<T> entityType, String orderByKey, boolean asc){
		try {
			if(TextUtils.isEmpty(orderByKey)){
				return dbUtils.findAll(entityType);
			}
			Selector selector = Selector.from(entityType);
			selector.orderBy(orderByKey, asc);
			return dbUtils.findAll(selector);
		} catch (DbException e) {
			LogUtils.e("有异常："+getDBErrorInfo(e));
		}
		return null;
	}
	/**
	 * @param entityType 如StuBean.class */
	public static <T> List<T> findAll(DbUtils dbUtils, Class<T> entityType){
		try {
			return dbUtils.findAll(entityType);
		} catch (DbException e) {
			LogUtils.e("有异常："+getDBErrorInfo(e));
		}
		return null;
	}
	/**删除数据 根据id删除
	 * @param entityType 如StuBean.class 
	 * @param idValue id的值
	 * @return true 删除成功
	 * */
	public static boolean deleteById(DbUtils dbUtils, Class<?> entityType, Object idValue){
		try {
			boolean isExist = dbUtils.tableIsExist(entityType);
			if(!isExist){
				return false;
			}
			dbUtils.deleteById(entityType, idValue);
			return true;
		} catch (DbException e) {
			LogUtils.e("有异常："+getDBErrorInfo(e));
		}
		return false;
	}
	/**查询 xx表 共有多少条数据
	 * @param entityType 如StuBean.class 
	 * @return long 数据量
	 * */
	public static long count(DbUtils dbUtils, Class<?> entityType){
		try
		{
			boolean isExist = dbUtils.tableIsExist(entityType);
			if(!isExist){
				return 0;
			}
			return dbUtils.count(entityType);
		} catch (DbException e){
			LogUtils.e("有异常："+getDBErrorInfo(e));
		}
		return 0;
	}
	/**删除数据 根据id删除
	 * @param entityType 如StuBean.class 
	 * @return true 删除成功
	 * */
	public static boolean deleteAll(DbUtils dbUtils, Class<?> entityType){
		try {
			boolean isExist = dbUtils.tableIsExist(entityType);
			if(!isExist){
				return false;
			}
			dbUtils.deleteAll(entityType);
			return true;
		} catch (DbException e) {
			LogUtils.e("有异常："+getDBErrorInfo(e));
		}
		return false;
	}
	/**将状态码，转成普通话！*/
	public static String getErrorInfo(HttpException error)
	{
		int errorCode = error.getExceptionCode();
		if(errorCode == 0){
			return "0 没有网络！";
		}else if(errorCode == 202){
			return errorCode+" 服务器已接受，尚未处理！";
		}else if(errorCode == 204){
			return errorCode+" 服务器已接受，无对应内容！";
		}else if(errorCode == 301){
			return errorCode+" 被请求的资源，已经永久移动到新位置了！";
		}else if(errorCode == 400){
			return errorCode+" 服务器不理解请求语法！";
		}else if(errorCode == 403){
			return errorCode+" 服务器拒绝您的请求！";
		}else if(errorCode == 404){
			return errorCode+" 找不到服务器！";
		}else if(errorCode == 405){
			return errorCode+" 请求资源被禁止！";
		}else if(errorCode == 406){
			return errorCode+" 服务器，无法接受请求！";
		}else if(errorCode == 408){
			return errorCode+" 请求超时！";
		}else if(errorCode == 410){
			return errorCode+" 被请求的资源，已经永久移动到未知位置！";
		}else if(errorCode == 500){
			return errorCode+" 内部服务器异常！";
		}else if(errorCode == 501){
			return errorCode+" 服务器不具备，被请求功能！";
		}else if(errorCode == 502){
			return errorCode+" 网关错误！";
		}else if(errorCode == 503){
			return errorCode+" 服务器，暂停服务！";
		}
		return errorCode+"  其他异常";
	}
	/**将DB 异常信息转换成 普通话*/
	public static String getDBErrorInfo(DbException e){
		String dbError = e.toString();
		if("com.lidroid.xutils.exception.DbException: android.database.sqlite.SQLiteConstraintException: column id is not unique (code 19)".equalsIgnoreCase(dbError)){
			return "id 要保证唯一性！";
		}else{
			return dbError;
		}
	}

}