package org.shanerx.backup;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

public class Backup {

     private BackupMode mode;
     private LocalDateTime date;
     private long time;
     private File zip;

     AutoBackup plugin;

     public Backup(BackupMode mode, long time) {
         this.mode = mode;
         this.time = time;
         this.date = LocalDateTime.ofEpochSecond(
                 time / 1000,
                 (int) time % 1000 * 1000000,
                 ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now())
         );

         plugin = AutoBackup.getInstance();
         this.zip = new File(plugin.getBackupsDir(), mode.buildZipName(date));
     }

    public BackupMode getMode() {
        return mode;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }

    public boolean deleteDisk(String logEntity) {
        boolean deleted = zip.delete();
        if (deleted) {
            plugin.logToFile(BackupAction.DELETE_SUCCESS, null, logEntity, mode.buildZipName(date));
            return true;
        }
        plugin.logToFile(BackupAction.DELETE_FAIL, "unknown", logEntity, mode.buildZipName(date));
        return false;
    }

}
