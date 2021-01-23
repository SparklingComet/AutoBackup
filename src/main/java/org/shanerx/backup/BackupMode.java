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
