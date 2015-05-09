package com.umb.cs682.projectlupus.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.umb.cs682.projectlupus.domain.ProfileBO;
import com.umb.cs682.projectlupus.db.helpers.DaoSession;

public class ProfileDao extends AbstractDao<ProfileBO, Long> {

    public static final String TABLENAME = "PROFILE";


    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserName = new Property(1, String.class, "userName", false, "USER_NAME");
        public final static Property Age = new Property(2, String.class, "age", false, "AGE"); //NK
        public final static Property Gender = new Property(3, String.class, "gender", false, "GENDER");
        public final static Property Ethnicity = new Property(4, String.class, "ethnicity", false, "ETHNICITY");
    };


    public ProfileDao(DaoConfig config) {
        super(config);
    }
    
    public ProfileDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PROFILE' (" +
                "'_id' INTEGER PRIMARY KEY ," +
                "'USER_NAME' TEXT NOT NULL ," +
                "'AGE' TEXT," +
                "'GENDER' TEXT," +
                "'ETHNICITY' TEXT);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PROFILE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ProfileBO entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getUserName());
 
        String age = entity.getAge();
        if (age != null) {
            stmt.bindString(3, age);
        }
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(4, gender);
        }
 
        String ethnicity = entity.getEthnicity();
        if (ethnicity != null) {
            stmt.bindString(5, ethnicity);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ProfileBO readEntity(Cursor cursor, int offset) {
        ProfileBO entity = new ProfileBO(
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
            cursor.getString(offset + 1),
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2),
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3),
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4)
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ProfileBO entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserName(cursor.getString(offset + 1));
        entity.setAge(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGender(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setEthnicity(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ProfileBO entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ProfileBO entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
