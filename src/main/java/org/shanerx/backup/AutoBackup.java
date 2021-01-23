package org.shanerx.backup;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class AutoBackup extends JavaPlugin {

    private static AutoBackup plugin;
    private Set<BackupMode> backupModes, defaultModes;
    private File root, backupsDir;

    @Override
    public void onEnable() {
        plugin = this;
        backupModes = new HashSet<>();
        defaultModes = new HashSet<>();

        loadSettings();

        getCommand("autobackup").setExecutor(new BackupCommand(this));

        // METRICS
        int pluginId = 10087;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("num_modes", () -> String.valueOf(backupModes.size())));
        metrics.addCustomChart(new Metrics.SimplePie("num_def_modes", () -> String.valueOf(defaultModes.size())));
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    public void loadSettings() {
        saveDefaultConfig();

        root = getConfig().getBoolean("relative-paths") ? new File(new File(getDataFolder().getAbsoluteFile().getParent()).getParent()) : null;
        backupsDir = new File(root, getConfig().getString("backup-dir"));

        loadBackups();
        scheduleAll();
    }

    public void scheduleAll() {
        int period;
        boolean log = getConfig().getBoolean("log-to-console");
        for (BackupMode mode : getBackups()) {
            if (mode.getSchedule() > 0) {
                period = mode.getSchedule() * 60 * 20; // convert mins to ticks

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (log) getServer().getConsoleSender().sendMessage(Message.SCHEDULED_BACKUP_LOG.toConsoleString()
                                .replaceAll("%NAME%", getServer().getConsoleSender().getName()).replaceAll("%MODE%", mode.getName()));
                        performBackup(mode, true);
                    }
                }.runTaskTimer(this, getConfig().getBoolean("immediate-backup") ? 0 : period, period);
            }
        }
    }

    public Set<BackupMode> getBackups() {
        return backupModes;
    }

    public Set<BackupMode> getDefaultBackups() {
        return defaultModes;
    }

    public boolean performBackup(BackupMode mode, boolean async) {
        if (!backupsDir.isDirectory()) {
            if (!backupsDir.mkdirs()) {
                getLogger().log(Level.SEVERE, Message.DIR_NOT_CREATED.toConsoleString());
                return false;
            }
        }

        LocalDateTime now = LocalDateTime.now();
        String zipName = String.format("backup__%04d-%02d-%02d_%02d:%02d:%02d__%s.zip",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond(),
                mode.getName());

        final boolean[] success = {true};
        boolean log = getConfig().getBoolean("log-to-console");

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {

                try {
                    File zipFile = new File(backupsDir, zipName);
                    if (getConfig().getBoolean("exclude-backup")) {
                        ZipUtil.pack(mode.getDir(), zipFile, s -> {
                            String path = mode.getDir().getAbsoluteFile().toPath().relativize(backupsDir.getAbsoluteFile().toPath()).toString();
                            if (s.startsWith(path)) {
                            //    System.out.println(s);
                                return null;
                            }
                            return s;
                        });
                    }
                    else {
                        ZipUtil.pack(mode.getDir(), zipFile);
                    }

                    if (log) getServer().getConsoleSender().sendMessage(Message.BACKUP_SUCCESSFUL.toConsoleString() + mode.getName());
                } catch(Exception e){
                    e.printStackTrace();
                    if (log) getServer().getConsoleSender().sendMessage(Message.BACKUP_FAILED.toConsoleString() + mode.getName());
                    success[0] = false;
                }
            }
        };

        if (async) runnable.runTaskAsynchronously(this);
        else runnable.runTask(this);
        return success[0];
    }

    public boolean loadBackups() {
        backupModes.clear();
        defaultModes.clear();

        Map<?,?> map;
        for (Object obj : getConfig().getList("backup-modes")) {
            map = (LinkedHashMap<?,?>) obj;
            backupModes.add(new BackupMode(
                    (String) map.get("name"),
                    new File(root, (String) map.get("dir")),
                    (Boolean) map.get("allow-manual"),
                    (Integer) map.get("schedule")));
        }

        for (BackupMode mode : backupModes) {
            if (getConfig().getStringList("default-modes").contains(mode.getName())) {
                defaultModes.add(mode);
            }
        }
        return true;
    }

    public static AutoBackup getInstance() {
        return plugin;
    }
}
