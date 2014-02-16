package com.jan.rm.dao;

import com.jan.rm.dao.ds.ActionPair;
import com.jan.rm.logger.RLog;
import com.jan.rm.sns.entity.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RmDatabase extends SQLiteOpenHelper {
	
	private final static String DATABASE_NAME = "rm.db";
	private final static int DATABASE_VERSION = 3;

	private static RmDatabase instance;
	
	private static class SnsUser{
		public static final String TABLE_NAME = "sns_user";
		
		public static final String AKEY = "akey";
		public static final String PLATFORM = "platform";
		public static final String USER_ID = "user_id";
		public static final String USER_NAME = "user_name";
		public static final String AVATAR_IMAGE_PATH = "avatar_image_path";
		public static final String GENDER = "gender";
		public static final String DESCRIPTION = "description";
		public static final String STATUSES_COUNT = "statuses_count";
		public static final String FOLLOWER_COUNT = "follower_count";
		public static final String IDOL_COUNT = "idol_count";
	}
	
	private static class RmRecentSituation{
		public static final String TABLE_NAME = "recent_situation";
		
		public static final String AKEY = "akey";
		public static final String CONTENT = "content";
		public static final String LEVEL = "level";
		public static final String ID = "id";
		public static final String COUNT = "sent_count";
	}
	
	public RmDatabase(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static RmDatabase getInstance(Context context){
		if(instance == null){
			instance = new RmDatabase(context);
		}
		
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder snsUserSQL = new StringBuilder();
		snsUserSQL.append("CREATE TABLE ").append(SnsUser.TABLE_NAME).append(" (")
		          .append(SnsUser.AKEY).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
		          .append(SnsUser.PLATFORM).append(" TEXT,")
		          .append(SnsUser.USER_ID).append(" TEXT,")
		          .append(SnsUser.USER_NAME).append(" TEXT,")
		          .append(SnsUser.AVATAR_IMAGE_PATH).append(" TEXT,")
		          .append(SnsUser.GENDER).append(" TEXT,")
		          .append(SnsUser.DESCRIPTION).append(" TEXT,")
		          .append(SnsUser.STATUSES_COUNT).append(" INTEGER,")
		          .append(SnsUser.FOLLOWER_COUNT).append(" INTEGER,")
		          .append(SnsUser.IDOL_COUNT).append(" INTEGER);");
		
		StringBuilder rmRecentSituationSQL = new StringBuilder();
		rmRecentSituationSQL.append("CREATE TABLE ").append(RmRecentSituation.TABLE_NAME).append(" (")
		                    .append(RmRecentSituation.AKEY).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
		                    .append(RmRecentSituation.CONTENT).append(" TEXT,")
		                    .append(RmRecentSituation.COUNT).append(" INTEGER,")
		                    .append(RmRecentSituation.ID).append(" INTEGER,")
		                    .append(RmRecentSituation.LEVEL).append(" INTEGER);");
		
		db.execSQL(snsUserSQL.toString());
		db.execSQL(rmRecentSituationSQL.toString());
				   
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + SnsUser.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + RmRecentSituation.TABLE_NAME);
		
		onCreate(db);
	}
	
	/*
	public void insertUser(User user, String platform){
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(SnsUser.TABLE_NAME).append(" (")
		   .append(SnsUser.PLATFORM).append(",")
		   .append(SnsUser.USER_ID).append(",")
		   .append(SnsUser.USER_NAME).append(",")
		   .append(SnsUser.AVATAR_IMAGE_PATH).append(",")
		   .append(SnsUser.GENDER).append(",")
		   .append(SnsUser.DESCRIPTION).append(",")
		   .append(SnsUser.STATUSES_COUNT).append(",")
		   .append(SnsUser.FOLLOWER_COUNT).append(",")
		   .append(SnsUser.IDOL_COUNT).append(") VALUES (")
		   .append(platform).append(",")
		   .append(user.getUserId()).append(",")
		   .append(user.getUserName()).append(",")
		   .append(user.getAvatarImagePath()).append(",")
		   .append(user.getGender()).append(",")
		   .append(user.getDescription()).append(",")
		   .append(user.getStatusesCount()).append(",")
		   .append(user.getFollowerCount()).append(",")
		   .append(user.getIdolCount()).append(");");
		
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql.toString());
		
	}
	 */
	
	public void insertRecentSituation(ActionPair actionPair){
		int count = getRecentSituationSentCount(actionPair.getTitle());
		
		if(isRecentSituationExisted(actionPair.getTitle())){
			deleteRecentSituation(actionPair.getTitle());
		}else{
			if(getRecentSituationCount() == 10){
				String content = getBearlyUseRecentSituationContent();
				if(content != null) deleteRecentSituation(content);
			}
		}
		
		ContentValues cv = new ContentValues();
		cv.put(RmRecentSituation.CONTENT, actionPair.getTitle());
		cv.put(RmRecentSituation.LEVEL, actionPair.getLevel());
		cv.put(RmRecentSituation.COUNT, ++count);
		
		SQLiteDatabase db = getWritableDatabase();
		db.insert(RmRecentSituation.TABLE_NAME, null, cv);
		
	}
	
	public String getBearlyUseRecentSituationContent(){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ").append(RmRecentSituation.TABLE_NAME).append(" ORDER BY ").append(RmRecentSituation.COUNT).append(" ASC;");
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql.toString(), null);
		if(cursor.moveToFirst()){
			return cursor.getString(cursor.getColumnIndex(RmRecentSituation.CONTENT));
		}
		
		return null;
	}
	
	public void deleteRecentSituation(String content){
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ").append(RmRecentSituation.TABLE_NAME).append(" WHERE ").append(RmRecentSituation.CONTENT).append(" = '").append(content).append("';");
		
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql.toString());
	}
	
	public void updateRecentSituationLevel(String content, int count){
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(RmRecentSituation.TABLE_NAME).append(" SET ").append(RmRecentSituation.COUNT).append(" = ").append(count).append(" WHERE ").append(RmRecentSituation.CONTENT).append(" = '").append(content).append("';");
	}
	
	public int getRecentSituationSentCount(String content){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append(RmRecentSituation.COUNT).append(" FROM ").append(RmRecentSituation.TABLE_NAME).append(" WHERE ").append(RmRecentSituation.CONTENT).append(" = '").append(content).append("';");
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql.toString(), null);
		if(cursor.moveToFirst()){
			return cursor.getInt(0);
		}
		
		return 0;
	}
	
	public int getRecentSituationCount(){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM ").append(RmRecentSituation.TABLE_NAME).append(";");
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql.toString(), null);
		if(cursor.moveToFirst()){
			return cursor.getInt(0);
		}
		
		return 0;
	}
	
	public boolean isRecentSituationExisted(String content){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM ").append(RmRecentSituation.TABLE_NAME).append(" WHERE ").append(RmRecentSituation.CONTENT).append(" = '").append(content).append("';");
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql.toString(), null);
		if(cursor.moveToFirst() && cursor.getInt(0) > 0){
			return true;
		}
		
		return false;
	}
	
	public ActionPair[] getRecentSituation(){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ").append(RmRecentSituation.TABLE_NAME).append(" ORDER BY ").append(RmRecentSituation.COUNT).append(" DESC").append(";");
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql.toString(), null);
		if(cursor.moveToFirst()){
			ActionPair[] situations = new ActionPair[cursor.getCount()];
			
			do{
				int i = cursor.getPosition();
				situations[i] = new ActionPair(null, 0);
				situations[i].setTitle(cursor.getString(cursor.getColumnIndex(RmRecentSituation.CONTENT)));
				situations[i].setLevel(cursor.getInt(cursor.getColumnIndex(RmRecentSituation.LEVEL)));
				situations[i].setCount(cursor.getInt(cursor.getColumnIndex(RmRecentSituation.COUNT)));
			}while(cursor.moveToNext());
			
			return situations;
		}
		
		return null;
	}
	
	public void insertUser(User user, String platform){
		ContentValues cv = new ContentValues();
		cv.put(SnsUser.PLATFORM, platform);
		cv.put(SnsUser.USER_ID, user.getUserId());
		cv.put(SnsUser.USER_NAME, user.getUserName());
		cv.put(SnsUser.AVATAR_IMAGE_PATH, user.getAvatarImagePath());
		cv.put(SnsUser.GENDER, user.getGender());
		cv.put(SnsUser.DESCRIPTION, user.getDescription());
		cv.put(SnsUser.STATUSES_COUNT, user.getStatusesCount());
		cv.put(SnsUser.FOLLOWER_COUNT, user.getFollowerCount());
		cv.put(SnsUser.IDOL_COUNT, user.getIdolCount());
		
		SQLiteDatabase db = getWritableDatabase();
		db.insert(SnsUser.TABLE_NAME, null, cv);
	}
	
	public void deleteUser(String platform){
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ").append(SnsUser.TABLE_NAME).append(" WHERE ").append(SnsUser.PLATFORM).append(" = '").append(platform).append("';");
		
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql.toString());
	}
	
	public void updateUser(User user, String platform){
		deleteUser(platform);
		insertUser(user, platform);
	}
	
	public boolean isPlatformStored(String platform){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ").append(SnsUser.TABLE_NAME).append(";");
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql.toString(), null);
		if(!cursor.moveToFirst()) return false;
		
		do{
			if(cursor.getString(cursor.getColumnIndex(SnsUser.PLATFORM)).equals(platform)){
				return true;
			}
		}while(cursor.moveToNext());
		
		if(!cursor.isClosed()) cursor.close();
		
		return false;
	}
	
	public void printPlatformForSnsUser(){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ").append(SnsUser.TABLE_NAME).append(";");
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql.toString(), null);
		cursor.moveToFirst();
		
		do{
			RLog.d("db_platform", cursor.getString(cursor.getColumnIndex(SnsUser.PLATFORM)));
		}while(cursor.moveToNext());
	}
	
	public User getUser(String platform){
		User user = new User();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ").append(SnsUser.TABLE_NAME).append(" WHERE ").append(SnsUser.PLATFORM).append(" = '").append(platform).append("';");
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql.toString(), null);
		cursor.moveToFirst();
		
		user.setUserId(cursor.getString(cursor.getColumnIndex(SnsUser.USER_ID)));
		user.setUserName(cursor.getString(cursor.getColumnIndex(SnsUser.USER_NAME)));
		user.setAvatarImagePath(cursor.getString(cursor.getColumnIndex(SnsUser.AVATAR_IMAGE_PATH)));
		user.setGender(cursor.getString(cursor.getColumnIndex(SnsUser.GENDER)));
		user.setDescription(cursor.getString(cursor.getColumnIndex(SnsUser.DESCRIPTION)));
		user.setStatusesCount(cursor.getInt(cursor.getColumnIndex(SnsUser.STATUSES_COUNT)));
		user.setFollowerCount(cursor.getInt(cursor.getColumnIndex(SnsUser.FOLLOWER_COUNT)));
		user.setIdolCount(cursor.getInt(cursor.getColumnIndex(SnsUser.IDOL_COUNT)));
		user.setPlatform(cursor.getString(cursor.getColumnIndex(SnsUser.PLATFORM)));
		
		if(!cursor.isClosed()) cursor.close();
		
		return user;
	}
	
}
