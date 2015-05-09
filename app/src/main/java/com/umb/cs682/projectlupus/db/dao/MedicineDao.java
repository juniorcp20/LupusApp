package com.umb.cs682.projectlupus.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.umb.cs682.projectlupus.domain.MedicineBO;
import com.umb.cs682.projectlupus.db.helpers.DaoSession;

public class MedicineDao extends AbstractDao<MedicineBO, Long> {

    public static final String TABLENAME = "MEDICINE";

    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property MedName = new Property(1, String.class, "medName", false, "MED_NAME");
        public final static Property Dosage = new Property(2, int.class, "dosage", false, "DOSAGE");
        public final static Property Interval = new Property(3, String.class, "interval", false, "INTERVAL");
        public final static Property Notes = new Property(4, String.class, "notes", false, "NOTES");
        public final static Property MedReminderCount = new Property(5, Integer.class, "medReminderCount", false, "MED_REMINDER_COUNT");
        public final static Property MedTakenCount = new Property(6, Integer.class, "medTakenCount", false, "MED_TAKEN_COUNT");
    };

    private DaoSession daoSession;


    public MedicineDao(DaoConfig config) {
        super(config);
    }

    public MedicineDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MEDICINE' (" +
                "'_id' INTEGER PRIMARY KEY ," +
                "'MED_NAME' TEXT NOT NULL ," +
                "'DOSAGE' INTEGER NOT NULL ," +
                "'INTERVAL' TEXT NOT NULL ," +
                "'NOTES' TEXT," +
                "'MED_REMINDER_COUNT' INTEGER," +
                "'MED_TAKEN_COUNT' INTEGER);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MEDICINE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MedicineBO entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getMedName());
        stmt.bindLong(3, entity.getDosage());
        stmt.bindString(4, entity.getInterval());

        String notes = entity.getNotes();
        if (notes != null) {
            stmt.bindString(5, notes);
        }

        Integer medReminderCount = entity.getMedReminderCount();
        if (medReminderCount != null) {
            stmt.bindLong(6, medReminderCount);
        }

        Integer medTakenCount = entity.getMedTakenCount();
        if (medTakenCount != null) {
            stmt.bindLong(7, medTakenCount);
        }
    }

    @Override
    protected void attachEntity(MedicineBO entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public MedicineBO readEntity(Cursor cursor, int offset) {
        MedicineBO entity = new MedicineBO( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                cursor.getString(offset + 1),
                cursor.getInt(offset + 2),
                cursor.getString(offset + 3),
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4),
                cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5),
                cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6)
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MedicineBO entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMedName(cursor.getString(offset + 1));
        entity.setDosage(cursor.getInt(offset + 2));
        entity.setInterval(cursor.getString(offset + 3));
        entity.setNotes(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMedReminderCount(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setMedTakenCount(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MedicineBO entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(MedicineBO entity) {
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

