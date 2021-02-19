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

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class AutoBackup extends JavaPlugin {

    private static AutoBackup plugin;
    private Set<BackupMode> backupModes, defaultModes;
    private File root, backupsDir, logFile;
    private Set<BukkitTask> tasks;

    @Override
    public void onEnable() {
        plugin = this;
        backupModes = new HashSet<>();
        defaultModes = new HashSet<>();

        loadSettings();

        getCommand("autobackup").setExecutor(new BackupCommand(this));

        if (!getConfig().getBoolean("disable-updater")) {
            new Updater(getDescription()).checkCurrentVersion();
        }

        // METRICS
        registerMetrics();
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    public void loadSettings() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        root = getConfig().getBoolean("relative-paths") ? new File(new File(getDataFolder().getAbsoluteFile().getParent()).getParent()) : null;
        backupsDir = new File(root, getConfig().getString("backup-dir"));
        if (getConfig().getBoolean("backup-log.enable")) {
            logFile = new File(backupsDir, "backup-log.txt");
        }

        loadBackupModes();
        scheduleAll();
    }

    public void scheduleAll() {
        if (tasks == null) {
            tasks = new HashSet<>();
        }
        else {
            tasks.forEach(BukkitTask::cancel);
            tasks.clear();
        }

        int period;
        boolean log = getConfig().getBoolean("log-to-console");
        for (BackupMode mode : getBackupModes()) {
            if (mode.getSchedule() > 0) {
                period = mode.getSchedule() * 60 * 20; // convert mins to ticks

                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (log) getServer().getConsoleSender().sendMessage(Message.SCHEDULED_BACKUP_LOG.toConsoleString()
                                .replaceAll("%NAME%", getServer().getConsoleSender().getName()).replaceAll("%MODE%", mode.getName()));
                        performBackup(mode, true, getConfig().getBoolean("backup-log.enable") ? "CONSOLE" : null);
                    }
                }.runTaskTimer(this, getConfig().getBoolean("immediate-backup") ? 0 : period, period);
                tasks.add(task);
            }
        }
    }

    public Set<BackupMode> getBackupModes() {
        return backupModes;
    }

    public Set<BackupMode> getDefaultBackups() {
        return defaultModes;
    }

    public boolean performBackup(BackupMode mode, boolean async, String logEntity) {
        if (!backupsDir.isDirectory()) {
            if (!backupsDir.mkdirs()) {
                getLogger().log(Level.SEVERE, Message.DIR_NOT_CREATED.toConsoleString());
                return false;
            }
        }

        String zipName = mode.buildZipName(LocalDateTime.now());

        final boolean[] success = {true};
        boolean log = getConfig().getBoolean("log-to-console"),
                rec = mode.isRecursive();
        final String[] failReason = new String[1];

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {

                try {
                    File zipFile = new File(backupsDir, zipName);
                    String path = mode.getDir().getAbsoluteFile().toPath().relativize(backupsDir
                            .getAbsoluteFile().toPath()).toString();

                    if (getConfig().getBoolean("exclude-backup")) {
                        ZipUtil.pack(mode.getDir(), zipFile, s -> {

                            if (s.startsWith(path) || !rec && s.split("/").length > 1) {
                                return null;
                            }
                            return s;
                            }, mode.getCompressionLevel());
                    }
                    else {
                        ZipUtil.pack(mode.getDir(), zipFile, s -> {

                            if (!rec && s.split("/").length > 1) {
                                return null;
                            }
                            return s;

                            },  mode.getCompressionLevel());
                    }

                    if (log) getServer().getConsoleSender().sendMessage(Message.BACKUP_SUCCESSFUL.toConsoleString()
                            + mode.getName());
                } catch(Exception e){
                    e.printStackTrace();
                    if (log) getServer().getConsoleSender().sendMessage(Message.BACKUP_FAILED.toConsoleString()
                            + mode.getName());
                    success[0] = false;
                    failReason[0] = e.getMessage();
                }
            }
        };

        if (async) runnable.runTaskAsynchronously(this);
        else runnable.runTask(this);

        logToFile(success[0] ? BackupAction.SUCCESS : BackupAction.FAIL, failReason[0], logEntity, zipName);
        return success[0];
    }

    public boolean purgeBackups(String logEntity) {
        final boolean[] success = new boolean[1];
        final int[] counter = {0};
        boolean log = getConfig().getBoolean("log-to-console");

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
                        logToFile(BackupAction.DELETE_FAIL, "unknown", logEntity, path);
                    }
                    else {
                        ++counter;
                        logToFile(BackupAction.DELETE_SUCCESS, null, logEntity, path);
                    }
                }

                if (log)
                    getServer().getConsoleSender().sendMessage(Message.PURGE_SUCCESSFUL.toConsoleString()
                            .replace("%NUMBER%", String.valueOf(counter)));
            }
        }.runTaskAsynchronously(this);

        return success[0];
    }

    public void logToFile(BackupAction action, String failReason, String logEntity, String zipName) {
        if (!getConfig().getBoolean("backup-log.enable")) {
            return;
        }

        try {
            if (!logFile.exists()) {
                if (!logFile.createNewFile()) {
                    getLogger().log(Level.SEVERE, Message.LOG_FILE_CREATION_FAIL.toString());
                }
            }

            FileWriter writer = new FileWriter(plugin.getLogFile(), true);

            if (action == BackupAction.SUCCESS && getConfig().getBoolean("backup-log.log-success")) {
                if (logEntity != null)
                    writer.append(String.format("%s    (by %s)\n", zipName, logEntity));
                else
                    writer.append(zipName + "\n");

            }
            else if (action == BackupAction.FAIL && getConfig().getBoolean("backup-log.log-failure")) {
                if (logEntity != null)
                    writer.append(String.format("%s    (by %s)    FAIL: %s\n", zipName, logEntity, failReason));
                else
                    writer.append(zipName + "    FAIL\n");

            }
            else if (action == BackupAction.DELETE_SUCCESS) {
                writer.append(String.format("%s    (by %s)    DELETED\n", zipName, logEntity));
            }
            else if (action == BackupAction.DELETE_FAIL) {
                writer.append(String.format("%s    (by %s)    DELETION FAILED: %s\n", zipName, logEntity, failReason));
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            getLogger().log(Level.SEVERE, Message.LOG_FAIL.toString());
            e.printStackTrace();
        }
    }

    public void loadBackupModes() {
        backupModes.clear();
        defaultModes.clear();

        Map<?,?> map;
        for (Object obj : getConfig().getList("backup-modes")) {
            map = (LinkedHashMap<?,?>) obj;
            backupModes.add(new BackupMode(
                    (String) map.get("name"),
                    new File(root, (String) map.get("dir")),
                    (Boolean) map.get("allow-manual"),
                    (Integer) map.get("schedule"),
                    (Integer) map.get("compression"),
                    (Boolean) map.get("recursive")
            ));
        }

        for (BackupMode mode : backupModes) {
            if (getConfig().getStringList("default-modes").contains(mode.getName())) {
                defaultModes.add(mode);
            }
        }
    }

    public Set<Backup> getPastBackups() {
        Set<Backup> backups = new HashSet<>();

        for (File f : backupsDir.listFiles()) {
            String path = backupsDir.toPath().relativize(f.toPath()).toString();

            // assumption: #listFiles() never null since backupsDir is a valid dir
            if (f.isDirectory() || path.equals(backupsDir.toPath().relativize(logFile.toPath()))) {
                // not a backup zip
                continue;
            }

            if (!path.endsWith(".zip") || !path.startsWith("backup__")) {
                continue;
            }

            String modeName = path.split("__")[2].split("\\.")[0];
            for (BackupMode mode : getBackupModes()) {
                if (mode.getName().equalsIgnoreCase(modeName)) {
                    backups.add(new Backup(mode, null)); // TODO FIX
                    break;
                }
            }
        }

        return backups;
    }

    public void registerMetrics() {
        int pluginId = 10087;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("num_modes", () -> String.valueOf(backupModes.size())));
        metrics.addCustomChart(new Metrics.SimplePie("num_def_modes", () -> String.valueOf(defaultModes.size())));
    }

    public static AutoBackup getInstance() {
        return plugin;
    }

    public File getLogFile() {
        return logFile;
    }

    public File getRoot() {
        return root;
    }

    public File getBackupsDir() {
        return backupsDir;
    }
}
