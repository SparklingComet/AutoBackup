package org.shanerx.backup;

import org.bukkit.ChatColor;

public enum Message {

    PREFIX("&f[&cAutoBackup&f]&r "),
    NO_PERMISSION("&4Sorry, but you cannot perform this operation."),
    INFO("&7Running AutoBackup v" + AutoBackup.getInstance().getDescription().getVersion()),
    NO_BACKUPS("&4No backup modes are configured at this time."),
    MODES_ACCESS("&7You have access to the following backup modes:"),
    BACKUP_FAILED("&4Could not take the following backup:  &c"),
    BACKUP_PERFORMING("&7Taking the following backups:  &c"),
    BACKUP_SUCCESSFUL("&7Successfully taken the following backups:  &c"),
    INVALID_USAGE("&4Invalid usage. Try &7/autobackup help&4 for more information."),
    HELP("&c/autobackup&7: View plugin information.\n" +
            "&c/autobackup help&7: Display this message.\n" +
            "&c/autobackup list&7: Display all available backup modes.\n" +
            "&c/autobackup default&7: Take default backups.\n" +
            "&c/autobackup take <mode>&7: Take chosen backup mode."),
    RELOAD("&7Plugin configuration reloaded successfully."),
   // INVALID_CONFIG("&4Invalid configuration, check the server log for more details. No backups could be loaded."),
    MANUAL_BACKUP_LOG("&7Entity &c%NAME% &7manually started backup mode &c%MODE%&7."),
    SCHEDULED_BACKUP_LOG("&7Entity &c%NAME% &7started scheduled backup mode &c%MODE%&7."),
    INVALID_MODE("&7The specified backup mode does not exist."),
    NO_DEFAULT("&4No default backup modes are configured at this time."),
    DIR_NOT_CREATED("&4The backup directory could not be created! Solve this problem ASAP");

    String msg;
    Message(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        if (this == PREFIX) {
            return ChatColor.translateAlternateColorCodes('&', PREFIX.msg);
        }
        return ChatColor.translateAlternateColorCodes('&', PREFIX.msg + msg);
    }

    public String toConsoleString() {
        return toString();
    }
}
