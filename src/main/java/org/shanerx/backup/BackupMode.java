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

public class BackupMode {
    private String name;
    private File dir;
    private boolean allowManual;
    private int schedule;

    public BackupMode(String name, File dir, boolean allowManual, int schedule) {
        this.name = name;
        this.dir = dir;
        this.allowManual = allowManual;
        this.schedule = Math.max(schedule, 0); // if <= 0 set to 0, otherwise to predefined value
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
}
