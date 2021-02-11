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

import java.io.File;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.zip.Deflater;

public class BackupMode {
    private String name;
    private File dir;
    private boolean allowManual;
    private int schedule;
    private int compressionLevel;
    private boolean recursive;

    public BackupMode(String name, File dir, boolean allowManual, int schedule, int compressionLevel, boolean recursive) {
        this.name = name;
        this.dir = dir;
        this.allowManual = allowManual;
        this.schedule = Math.max(schedule, 0); // if <= 0 set to 0, otherwise to predefined value
        this.recursive = recursive;

        switch (compressionLevel) {
            case -1:
                this.compressionLevel = Deflater.NO_COMPRESSION;
                break;
            case 0:
                this.compressionLevel = Deflater.DEFAULT_COMPRESSION;
                break;
            case 1:
                this.compressionLevel = Deflater.BEST_SPEED;
                break;
            case 2:
                this.compressionLevel = Deflater.BEST_COMPRESSION;
                break;
            default:
                AutoBackup.getInstance().getLogger().log(Level.WARNING,
                    String.format("(Backup mode: %s) Invalid compression value: %d", name, compressionLevel));
                this.compressionLevel = Deflater.DEFAULT_COMPRESSION;
                break;
        }
    }

    public String getName() {
        return name;
    }

    public File getDir() {
        return dir;
    }

    public int getSchedule() {
        return schedule;
    }

    public boolean isAllowedManually() {
        return allowManual;
    }

    public int getCompressionLevel() {
        return compressionLevel;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public String buildZipName(LocalDateTime now) {
        return String.format("backup__%04d-%02d-%02d_%02d:%02d:%02d__%s.zip",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond(),
                this.getName());
    }
}
