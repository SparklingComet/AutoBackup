package org.shanerx.backup;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Backup {

     private BackupMode mode;
     private LocalDateTime date;
     private File zip;

     AutoBackup plugin;

     public Backup(BackupMode mode, LocalDateTime date) {
         this.mode = mode;
         this.date = date;

         plugin = AutoBackup.getInstance();
         this.zip = new File(plugin.getBackupsDir(), mode.buildZipName(date));
     }

    public BackupMode getMode() {
        return mode;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public boolean deleteDisk(String logEntity) {
        System.out.println("DEBUG: " + mode.buildZipName(date));

        boolean deleted = zip.delete();
        if (deleted) {
            plugin.logToFile(BackupAction.DELETE_SUCCESS, null, logEntity, mode.buildZipName(date));
            return true;
        }
        plugin.logToFile(BackupAction.DELETE_FAIL, "unknown", logEntity, mode.buildZipName(date));
        return false;
    }

}
