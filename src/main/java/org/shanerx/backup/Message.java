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
    BACKUP_DELETED("&7Successfully auto-purged the following backup:  &c"),
    INVALID_USAGE("&4Invalid usage. Try &7/autobackup help&4 for more information."),
    HELP("&7Running AutoBackup v" + AutoBackup.getInstance().getDescription().getVersion() +
            "\n&c/autobackup&7: View plugin information.\n" +
            "&c/autobackup help&7: Display this message.\n" +
            "&c/autobackup list&7: Display all available backup modes.\n" +
            "&c/autobackup default&7: Take default backups.\n" +
            "&c/autobackup take <mode>&7: Take chosen backup mode.\n" +
            "&c/autobackup purge&7: Purge all past backups."),
    RELOAD("&7Plugin configuration reloaded successfully."),
   // INVALID_CONFIG("&4Invalid configuration, check the server log for more details. No backups could be loaded."),
    MANUAL_BACKUP_LOG("&7Entity &c%NAME% &7manually started backup mode &c%MODE%&7."),
    SCHEDULED_BACKUP_LOG("&7Entity &c%NAME% &7started scheduled backup mode &c%MODE%&7."),
    INVALID_MODE("&7The specified backup mode does not exist."),
    NO_DEFAULT("&4No default backup modes are configured at this time."),
    DIR_NOT_CREATED("&4The backup directory could not be created! Solve this problem ASAP"),
    LOG_FAIL("Failed logging to file. Logging stacktrace below:"),
    LOG_FILE_CREATION_FAIL("Failed creating log file. Logging stacktrace below:"),
    PURGE_PERFORMING("&7Purging all backups"),
    PURGE_SUCCESSFUL("&7Successfully purged all backups (&c%NUMBER%&7)"),
    PURGE_LOG("&7Entity &c%NAME% &7manually started purge of all backups."),
    PURGE_LOG_FAILED("&7Entity &c%NAME% &7manual purge failed."),
    CONFIG_WARNING_AUTOPURGE("&7Could not read config value for option `purge-after`. Defaulting to 0 (Skipping auto-purge). (&cBackup-Mode: %NAME%&7)");

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
