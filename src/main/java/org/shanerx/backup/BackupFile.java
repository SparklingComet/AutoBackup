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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackupFile {
    private String fileName;
    public String getFileName() {
      return fileName;
    }

    private LocalDateTime backupDate = null;
    public LocalDateTime getBackupDate() {
        return backupDate;
    }

    private boolean isValid = false;
    public boolean getIsValid() {
      return isValid;
    }

    private String mode;
    public String getMode() {
      return mode;
    }

    public BackupFile(String fileName) {
      this.fileName = fileName;
    }

    public BackupFile initialize() {
      BackupFileNameFormatter formatter = new BackupFileNameFormatter();
      String zipNameRegex = formatter.getFileNameRegex();
      final Pattern zipNamePattern = Pattern.compile(zipNameRegex, Pattern.MULTILINE);

      Matcher matcher = zipNamePattern.matcher(fileName);
      this.isValid = matcher
          .find();

      if (!this.isValid) {
        return this;
      }

      String dateString = String.format("%s-%s-%s %s:%s:%s",
        matcher.group(formatter.getYearIndex()),
        matcher.group(formatter.getMonthIndex()),
        matcher.group(formatter.getDayIndex()),
        matcher.group(formatter.getHourIndex()),
        matcher.group(formatter.getMinuteIndex()),
        matcher.group(formatter.getSecondIndex()));

      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      backupDate = LocalDateTime.parse(dateString, dateFormatter);

      mode = matcher.group(formatter.getModeIndex());

      return this;
    }


}