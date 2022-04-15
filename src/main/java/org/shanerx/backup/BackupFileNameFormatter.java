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

public class BackupFileNameFormatter {
    private String fileNameFormatString;
    public String getFileNameFormatString() {
        return this.fileNameFormatString;
    }

    private String fileNameRegex;
    public String getFileNameRegex() {
        return this.fileNameRegex;
    }

    public int getYearIndex() { return 1; }
    public int getMonthIndex() { return 2; }
    public int getDayIndex() { return 3; }
    public int getHourIndex() { return 4; }
    public int getMinuteIndex() { return 5; }
    public int getSecondIndex() { return 6; }
    public int getModeIndex() { return 7; }

    public BackupFileNameFormatter() {
        this.fileNameFormatString = "backup__%04d-%02d-%02d_%02d-%02d-%02d__%s.zip";
        this.fileNameRegex = "backup__([\\d]{4,4})-([\\d]{2,2})-([\\d]{2,2})_([\\d]{2,2})-([\\d]{2,2})-([\\d]{2,2})__([^\\.]*)\\.zip";
    }
}