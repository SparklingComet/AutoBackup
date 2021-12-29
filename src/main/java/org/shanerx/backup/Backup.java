/*
   Copyright 2021 SparklingComet/ShanerX @ www.shanerx.org

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package org.shanerx.backup;

import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.LocalDateTime;

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
            BackupAction.DELETE_SUCCESS.logToFile(null, logEntity, mode.buildZipName(date));
            return true;
        }
        BackupAction.DELETE_FAIL.logToFile("unknown", logEntity, mode.buildZipName(date));
        return false;
    }

    public static boolean purgeBackups(String logEntity) {
        File backupsDir = AutoBackup.getInstance().getBackupsDir();
        File logFile = AutoBackup.getInstance().getLogFile();

        final boolean[] success = new boolean[1];
        final int[] counter = {0};
        boolean log = AutoBackup.getInstance().getConfig().getBoolean("log-to-console");

        new BukkitRunnable() {
            @Override
            public void run() {
                String path;
                int counter = 0;

                for (File f : backupsDir.listFiles()) {
                    path = backupsDir.toPath().relativize(f.toPath()).toString();

                    // assumption: #listFiles() never null since backupsDir is a valid dir
                    if (f.isDirectory() || path.equals(backupsDir.toPath().relativize(logFile.toPath()))) {
                        // not a backup zip
                        continue;
                    }

                    if (!path.endsWith(".zip") || !path.startsWith("backup__")) {
                        continue;
                    }

                    if(!f.delete()) {
                        success[0] = false;
                        BackupAction.DELETE_FAIL.logToFile("unknown", logEntity, path);
                    }
                    else {
                        ++counter;
                        BackupAction.DELETE_SUCCESS.logToFile(null, logEntity, path);
                    }
                }

                if (log)
                    AutoBackup.getInstance().getServer().getConsoleSender().sendMessage(Message.PURGE_SUCCESSFUL.toConsoleString()
                            .replace("%NUMBER%", String.valueOf(counter)));
            }
        }.runTaskAsynchronously(AutoBackup.getInstance());

        return success[0];
    }

}
