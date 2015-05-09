package com.umb.cs682.projectlupus.db.dao;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.umb.cs682.projectlupus.domain.MoodLevelBO;
import com.umb.cs682.projectlupus.db.helpers.DaoSession;

public class MoodLevelDao extends AbstractDao<MoodLevelBO, Long> {

    public static final String TABLENAME = "MOOD_LEVEL";

    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ReminderId = new Property(1, long.class, "reminderId", false, "REMINDER_ID");
        public final static Property Date = new Property(2, java.util.Date.class, "date", false, "DATE");
        public final static Property MoodLevel = new Property(3, int.class, "moodLevel", false, "MOOD_LEVEL");
    };

    private Query<MoodLevelBO> reminder_MoodRemindersQuery;

    public MoodLevelDao(DaoConfig config) {
        super(config);
    }
    
    public MoodLevelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MOOD_LEVEL' (" +
                "'_id' INTEGER PRIMARY KEY ," +
                "'REMINDER_ID' INTEGER NOT NULL ," +
                "'DATE' INTEGER NOT NULL ," +
                "'MOOD_LEVEL' INTEGER NOT NULL );");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MOOD_LEVEL'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MoodLevelBO entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getReminderId());
        stmt.bindLong(3, entity.getDate().getTime());
        stmt.bindLong(4, entity.getMoodLevel());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public MoodLevelBO readEntity(Cursor cursor, int offset) {
        MoodLevelBO entity = new MoodLevelBO(
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
            cursor.getLong(offset + 1),
            new java.util.Date(cursor.getLong(offset + 2)),
            cursor.getInt(offset + 3)
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MoodLevelBO entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setReminderId(cursor.getLong(offset + 1));
        entity.setDate(new java.util.Date(cursor.getLong(offset + 2)));
        entity.setMoodLevel(cursor.getInt(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MoodLevelBO entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MoodLevelBO entity) {
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
    
    /** Internal query to resolve the "moodReminders" to-many relationship of Reminder. */
    public List<MoodLevelBO> _queryReminder_MoodReminders(long reminderId) {
        synchronized (this) {
            if (reminder_MoodRemindersQuery == null) {
                QueryBuilder<MoodLevelBO> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ReminderId.eq(null));
                reminder_MoodRemindersQuery = queryBuilder.build();
            }
        }
        Query<MoodLevelBO> query = reminder_MoodRemindersQuery.forCurrentThread();
        query.setParameter(0, reminderId);
        return query.list();
    }

}
