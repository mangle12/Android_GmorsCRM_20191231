package tw.com.masterhand.gmorscrm.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import tw.com.masterhand.gmorscrm.room.local.DownloadLog;
import tw.com.masterhand.gmorscrm.room.local.ReadLog;
import tw.com.masterhand.gmorscrm.room.local.SyncLog;

@Dao
public interface LocalDao {
    @Query("SELECT * from ReadLog where parent_id IN (:parentIdList)")
    public List<ReadLog> getReadLog(List<String> parentIdList);

    @Query("SELECT * from SyncLog where user_id = :userId")
    public List<SyncLog> getSyncLog(String userId);

    @Query("SELECT * from SyncLog where sync_status = 1 and is_file = :isFile and user_id = " +
            ":userId and deleted_at IS NULL order by " +
            "created_at desc LIMIT 1")
    public SyncLog getLastSyncLog(int isFile, String userId);

    @Query("SELECT * from SyncLog where sync_status = 0 and is_file = :isFile and user_id = " +
            ":userId and deleted_at IS " +
            "NULL order by " +
            "created_at desc LIMIT 1")
    public SyncLog getLastUncompletedSyncLog(int isFile, String userId);

    @Query("SELECT * from DownloadLog where parent_id = :parent_id order by " +
            "created_at desc LIMIT 1")
    public DownloadLog getDownloadLog(String parent_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveSyncLog(SyncLog log);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveDownloadLog(DownloadLog log);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveReadLog(List<ReadLog> log);

    /*-------------------------------刪除-----------------------------------*/
    @Delete
    public void deleteSyncLog(List<SyncLog> log);
}
