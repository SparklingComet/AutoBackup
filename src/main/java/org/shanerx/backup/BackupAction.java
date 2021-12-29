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

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public enum BackupAction {
    SUCCESS,
    FAIL,
    DELETE_SUCCESS,
    DELETE_FAIL;

    public void logToFile(String failReason, String logEntity, String zipName) {
        AutoBackup ab = AutoBackup.getInstance();
        if (!ab.getConfig().getBoolean("backup-log.enable")) {
            return;
        }

        try {
            if (!ab.getLogFile().exists()) {
                if (!ab.getLogFile().createNewFile()) {
                    ab.getLogger().log(Level.SEVERE, Message.LOG_FILE_CREATION_FAIL.toString());
                }
            }

            FileWriter writer = new FileWriter(ab.getLogFile(), true);

            if (this == BackupAction.SUCCESS && ab.getConfig().getBoolean("backup-log.log-success")) {
                if (logEntity != null)
                    writer.append(String.format("%s    (by %s)\n", zipName, logEntity));
                else
                    writer.append(zipName + "\n");

            }
            else if (this == BackupAction.FAIL && ab.getConfig().getBoolean("backup-log.log-failure")) {
                if (logEntity != null)
                    writer.append(String.format("%s    (by %s)    FAIL: %s\n", zipName, logEntity, failReason));
                else
                    writer.append(zipName + "    FAIL\n");

            }
            else if (this == BackupAction.DELETE_SUCCESS) {
                writer.append(String.format("%s    (by %s)    DELETED\n", zipName, logEntity));
            }
            else if (this == BackupAction.DELETE_FAIL) {
                writer.append(String.format("%s    (by %s)    DELETION FAILED: %s\n", zipName, logEntity, failReason));
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            ab.getLogger().log(Level.SEVERE, Message.LOG_FAIL.toString());
            e.printStackTrace();
        }
    }

}
