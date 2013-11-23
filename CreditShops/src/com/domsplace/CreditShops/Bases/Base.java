/*
 * Copyright 2013 Dominic.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domsplace.CreditShops.Bases;

import com.domsplace.CreditShops.DataManagers.ConfigManager;
import com.domsplace.CreditShops.CreditShopsPlugin;
import com.domsplace.CreditShops.Hooks.VaultHook;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * @author      Dominic
 * @since       11/10/2013
 */
public class Base extends RawBase {
    public static final String TAB = "    ";
    
    public static boolean DebugMode = false;
    public static CreditShopsPlugin plugin;
    
    public static String ChatDefault = ChatColor.GRAY.toString();
    public static String ChatImportant = ChatColor.BLUE.toString();
    public static String ChatError = ChatColor.RED.toString();
    
    private static String permissionMessage = "&4You don't have permission to do this!";
    
    public static final Pattern SEPERATOR_REGEX = Pattern.compile(",\\s*(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    public static final String ATTRIBUTE_SEPERATOR_REGEX = "\\{(\\s*)(\\w+)\\:(\\s*)(\".*?(?<!\\\\)(\"))(\\s*)\\}";
    
    public static final String getBukkitPage() {
        return "http://dev.bukkit.org/bukkit-plugins/creditshops/";
    }
    
    //HOOKING OPTIONS

    //String Utils
    public static String getDebugPrefix() {
        return ChatColor.LIGHT_PURPLE + "DEBUG: " + ChatColor.AQUA;
    }
    
    public static String colorise(Object o) {
        String msg = o.toString();
        
        String[] andCodes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", 
            "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f", "&l", "&o", "&n", 
            "&m", "&k", "&r"};
        
        String[] altCodes = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", 
            "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§l", "§o", "§n", 
            "§m", "§k", "§r"};
        
        for(int x = 0; x < andCodes.length; x++) {
            msg = msg.replaceAll(andCodes[x], altCodes[x]);
        }
        
        return msg;
    }
    
    public static String decolorise(Object o) {
        String msg = o.toString();
        
        String[] andCodes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", 
            "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f", "&l", "&o", "&n", 
            "&m", "&k", "&r"};
        
        String[] altCodes = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", 
            "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§l", "§o", "§n", 
            "§m", "§k", "§r"};
        
        for(int x = 0; x < andCodes.length; x++) {
            msg = msg.replaceAll(altCodes[x], andCodes[x]);
        }
        
        return msg;
    }
    
    public static String coloriseByPermission(Object message, CommandSender player, String permissionPrefix) {
        String msg = message.toString();
        
        String[] andCodes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", 
            "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f", "&l", "&o", "&n", 
            "&m", "&k", "&r"};
        
        String[] altCodes = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", 
            "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§l", "§o", "§n", 
            "§m", "§k", "§r"};
        
        String[] perms = {"black", "darkblue", "darkgreen", "cyan", "darkred", 
            "purple", "gold", "gray", "darkgray", "blue", "green", "lightblue", 
            "red", "pink", "yellow", "white", "magic", "bold", "strike", 
            "underline", "italics", "reset"};
        
        for(int i = 0; i < perms.length; i++) {
            if(!player.hasPermission(permissionPrefix + perms[i]) && isPlayer(player)) continue;
            msg = msg.replaceAll(andCodes[i], altCodes[i]);
        }
        
        if(player.hasPermission(permissionPrefix + "emoji")) msg = emoji(msg);
        
        return msg;
    }
    
    public static String removeColors(Object o) {
        String msg = o.toString();
        
        String[] andCodes = {"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", 
            "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f", "&l", "&o", "&n", 
            "&m", "&k", "&r"};
        
        String[] altCodes = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", 
            "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f", "§l", "§o", "§n", 
            "§m", "§k", "§r"};
        for(int i = 0; i < andCodes.length; i++) {
            msg = msg.replaceAll(andCodes[i], "");
            msg = msg.replaceAll(altCodes[i], "");
        }
        
        return msg;
    }
    
    public static String getPermissionMessage() {
        return Base.permissionMessage;
    }
    
    public static void setPermissionMessage(String msg) {
        Base.permissionMessage = msg;
    }

    public static String[] listToArray(List<String> c) {
        String[] s = new String[c.size()];
        for(int i = 0; i < c.size(); i++) {
            s[i] = c.get(i);
        }
        
        return s;
    }
    
    public static String capitalizeFirstLetter(String s) {
        if(s.length() < 2) return s.toUpperCase();
        String end = s.substring(1, s.length());
        return s.substring(0, 1).toUpperCase() + end;
    }

    public static String capitalizeEachWord(String toLowerCase) {
        String[] words = toLowerCase.split(" ");
        String w = "";
        for(int i = 0; i < words.length; i++) {
            w += capitalizeFirstLetter(words[i]);
            if(i < (words.length-1)) {
                w += " ";
            }
        }
        return w;
    }
    
    public static String arrayToString(Object[] array) {
        return Base.arrayToString(array, " ");
    }
    
    public static String arrayToString(Object[] array, String seperator) {
        String m = "";
        for(int i = 0; i < array.length; i++) {
            m += array[i].toString();
            if(i < (array.length - 1)) {
                m += seperator;
            }
        }
        
        return m;
    }
    
    public static String trim(String s, int length) {
        if(s.length() < length) return s;
        return s.substring(0, length);
    }
    
    public static String emoji(String s) {
        s = s.replaceAll("<3", "❤");
        s = s.replaceAll("(?i)\\(PERSONF\\)", "유");
        s = s.replaceAll("(?i)\\(PERSONM\\)", "웃");
        s = s.replaceAll("(?i)\\(PERSON\\)", "유");
        s = s.replaceAll("(?i)\\(PENCIL\\)", "✎");
        s = s.replaceAll("(?i)\\(PLANE\\)", "✈");
        s = s.replaceAll("(?i)\\(NOTE\\)", "♫");
        s = s.replaceAll("(?i)\\(YINGYANG\\)", "☯");
        return s;
    }
    
    //Messaging Utils
    public static void sendMessage(CommandSender sender, String msg) {
        if(msg.replaceAll(" ", "").equalsIgnoreCase("")) return;
        msg = msg.replaceAll("\\t", TAB);
        msg = msg.replaceAll("\\\\t", TAB);
        msg = msg.replaceAll("\t", TAB);
        sender.sendMessage(ChatDefault + msg);
    }

    public static void sendMessage(CommandSender sender, String msg, Object... objs) {
        String s = msg;
        for(int i = 0; i < objs.length; i++) {
            s = s.replaceAll("\\{" + i + "\\}", Matcher.quoteReplacement(objs[i].toString()));
        }
        sendMessage(sender, s);
    }
    
    public static void sendMessage(CommandSender sender, Object[] msg) {
        for(Object o : msg) {
            sendMessage(sender, o);
        }
    }

    public static void sendMessage(CommandSender sender, List<?> msg) {
        sendMessage(sender, msg.toArray());
    }

    public static void sendMessage(CommandSender sender, Object msg) {
        if(msg == null) return;
        if(msg instanceof String) {
            sendMessage(sender, (String) msg);
            return;
        }
        
        if(msg instanceof Object[]) {
            sendMessage(sender, (Object[]) msg);
            return;
        }
        
        if(msg instanceof List<?>) {
            sendMessage(sender, (List<?>) msg);
            return;
        }
        sendMessage(sender, msg.toString());
    }

    public static void sendMessage(Player sender, Object... msg) {
        sendMessage((CommandSender) sender, msg);
    }

    public static void sendMessage(OfflinePlayer sender, Object... msg) {
        if(sender == null) return;
        if(!sender.isOnline()) return;
        sendMessage(sender.getPlayer(), msg);
    }

    public static void sendMessage(Entity sender, Object... msg) {
        if(!(sender instanceof CommandSender)) return;
        sendMessage(sender, msg);
    }
    
    public static void sendMessage(Object o) {
        sendMessage(Bukkit.getConsoleSender(), o);
    }
    
    public static void sendAll(List<Player> players, Object o) {
        for(Player p : players) {
            sendMessage(p, o);
        }
    }
    
    public static void sendAll(Player[] players, Object o) {
        for(Player p : players) {
            sendMessage(p, o);
        }
    }
    
    public static void sendAll(Object o) {
        sendAll(Bukkit.getOnlinePlayers(), o);
    }
    
    public static void sendAll(String permission, Object o) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!hasPermission((CommandSender) p, permission)) continue;
            sendMessage(p, o);
        }
    }
    
    public static void broadcast(Object o) {
        sendMessage(o);
        sendAll(o);
    }
    
    public static void broadcast(String permission, Object o) {
        sendMessage(o);
        sendAll(permission, o);
    }
    
    public static void debug(Object o) {
        if(!DebugMode) return;
        broadcast(getDebugPrefix() + o.toString());
    }
    
    public static void error(String message, boolean postfix) {
        String msg = ChatError + "Error: " + ChatColor.DARK_RED + message;
        if(postfix && DebugMode) msg += ChatColor.YELLOW + " Caused by: ";
        if(postfix && !DebugMode) msg += ChatColor.YELLOW + " Turn debug mode on to view whole error.";
        sendMessage(msg);
    }
    
    public static void error(String message) {
        error(message, false);
    }
    
    public static void error(String message, Exception e) {
        error(message, true);
        debug("ERROR: " + message);
        if(!DebugMode) return;
        error("CAUSE: " + e.getMessage());
        String lines = "\n" + e.getClass().getName() + ":  " +  e.getMessage();
        for(StackTraceElement ste : e.getStackTrace()) {
            
            lines += "\t" + ChatColor.GRAY + "at " + ste.getClassName() + "." 
                    + ste.getMethodName() + "(" + ste.getFileName() + ":" + 
                    ste.getLineNumber() + ")\n";
        }
        
        sendMessage(lines);
    }
    
    public static void log(Object o) {
        getPlugin().getLogger().info(o.toString());
    }
    
    //Conversion Utils
    public static boolean isPlayer(Object o) {
        return o instanceof Player;
    }
    
    public static Player getPlayer(Object o) {
        return (Player) o;
    }
    
    public static Player getPlayer(CommandSender sender, String argument) {
        Player p = null;
        for(Player plyr : Bukkit.getOnlinePlayers()) {
            if(!canSee(sender, plyr)) continue;
            if(plyr.getName().toLowerCase().contains(argument.toLowerCase())) {
                p = plyr;
                break;
            }
        }
        
        if(p == null) {
            for(Player plyr : Bukkit.getOnlinePlayers()) {
                if(!canSee(sender, plyr)) continue;
                if(plyr.getDisplayName().toLowerCase().contains(argument.toLowerCase())) {
                    p = plyr;
                    break;
                }
            }
        }
        return p;
    }
    
    public static OfflinePlayer getOfflinePlayer(Player player) {
        return Bukkit.getOfflinePlayer(player.getName());
    }
    
    public static OfflinePlayer getOfflinePlayer(String player) {
        return Bukkit.getOfflinePlayer(player);
    }
    
    public static OfflinePlayer getOfflinePlayer(CommandSender relative, String player) {
        if(player.length() < 3) return null;
        if(player.length() > 16) return null;
        OfflinePlayer p = Base.getPlayer(relative, player);
        if(p == null || !p.isOnline()) {
            p = Bukkit.getOfflinePlayer(player);
        }
        return p;
    }
    
    public static String getDisplayName(OfflinePlayer player) {
        if(!player.isOnline()) return player.getName();
        return getPlayer(player).getDisplayName();
    }
    
    public static boolean isInt(Object o) {
        try {
            Integer.parseInt(o.toString());
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public static int getInt(Object o) {
        return Integer.parseInt(o.toString());
    }
    
    public static boolean isDouble(Object o) {
        try {
            Double.parseDouble(o.toString());
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public static double getDouble(Object o) {
        return Double.parseDouble(o.toString());
    }
    
    public static boolean isShort(Object o) {
        try {
            Short.parseShort(o.toString());
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public static short getShort(Object o) {
        return Short.parseShort(o.toString());
    }
    
    public static boolean isByte(Object o) {
        try {
            Byte.parseByte(o.toString());
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public static byte getByte(Object o) {
        return Byte.parseByte(o.toString());
    }
    
    public static boolean isLong(Object o) {
        try {
            Long.parseLong(o.toString());
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public static long getLong(Object o) {
        return Long.parseLong(o.toString());
    }
    
    public static boolean isFloat(Object o) {
        try {
            Long.parseLong(o.toString());
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public static float getFloat(Object o) {
        return Float.parseFloat(o.toString());
    }
    
    public static boolean isIP(Object o) {
        String s = o.toString();
        return s.matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    }
    
    public static String listToString(List<? extends Object> strings) {
        return listToString(strings, ", ");
    }
    
    public static String listToString(List<? extends Object> strings, String seperator) {
        String m = "";
        
        for(int i = 0; i < strings.size(); i++) {
            m += strings.get(i).toString();
            if(i < (strings.size() - 1)) m += seperator;
        }
        
        return m;
    }
    
    public static String setToString(Set<? extends Object> strings, String seperator) {
        return listToString(new ArrayList<Object>(strings));
    }
    
    public static String twoDecimalPlaces(double x) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(x);
    }
    
    public static String getOrdinal(int i) {
        int hundredRemainder = i % 100;
        if(hundredRemainder >= 10 && hundredRemainder <= 20) {
            return "th";
        }
        int tenRemainder = i % 10;
        switch (tenRemainder) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
    
    public double diceDouble(double lower, double higher) {
        Random r = new Random();
        return lower+(r.nextDouble()*(higher-lower));
    }

    public static void addArrayToList(List<String> messages, String[] message) {
        for(String s : message) {
            messages.add(s);
        }
    }
    
    public static Map<String, String> getDomsJSON(String line) {
        try {
            line = line.replaceAll("\\n","\\\\n");
            String[] parts = line.split(SEPERATOR_REGEX.pattern());
            
            Map<String, String> data = new HashMap<String, String>();
            
            for(String s : parts) {
                Matcher m = Pattern.compile(ATTRIBUTE_SEPERATOR_REGEX).matcher(s);
                m.find();
                
                String key = m.group(2).toLowerCase();
                String value = m.group(4).replaceFirst("\"", "");
                value = value.substring(0, value.length()-1);
                value = value.replaceAll("&q", "\"");
                data.put(key, value);
            }
            
            return data;
        } catch(Exception e) {return null;}
    }
    
    public static DyeColor getDyeColor(String s) {
        for(DyeColor color : DyeColor.values()) {
            if(color.name().equalsIgnoreCase(s)) return color;
        }
        return null;
    }
    
    //Plugin Utils
    public static void setPlugin(CreditShopsPlugin plugin) {
        Base.plugin = plugin;
    }
    
    public static CreditShopsPlugin getPlugin() {
        return plugin;
    }
    
    public static File getDataFolder() {
        return getPlugin().getDataFolder();
    }
    
    public static YamlConfiguration getConfig() {
        return getConfigManager().getCFG();
    }
    
    public static ConfigManager getConfigManager() {
        return DataManager.CONFIG_MANAGER;
    }
    
    //Location Utils
    public static String getLocationString(Location location) {
        return location.getX() + ", " + location.getY() + ", " + location.getZ()
                + " " + location.getWorld().getName();
    }
    
    public static String getStringLocation (Chunk chunk) {
        return chunk.getX() + ", " + chunk.getZ() + " : " + chunk.getWorld().getName();
    }
    
    public static boolean isCoordBetweenCoords(int checkX, int checkZ, int outerX, int outerZ, int maxX, int maxZ) {
        if(checkX >= Math.min(outerX, maxX) && checkX <= Math.max(outerX, maxX)) {
            if(checkZ >= Math.min(outerZ, maxZ) && checkZ <= Math.max(outerZ, maxZ)) { return true; }
        }
        return false;
    }
    
    //Player Utils
    public static boolean hasPermission(CommandSender sender, String permission) {
        if(!isPlayer(sender)) return true;
        if(sender.isOp()) return true;
        if(sender.hasPermission(getPlugin().getName() + ".*")) return true;
        
        if(PluginHook.VAULT_HOOK.isHooked() && PluginHook.VAULT_HOOK.getPermission() != null) {
            return PluginHook.VAULT_HOOK.getPermission().has(sender, permission);
        }
        
        if(permission.equals(getPlugin().getName() + ".none")) return true;
        return sender.hasPermission(permission);
    }
    
    public static boolean hasPermission(Player player, String permission) {return hasPermission((CommandSender) player, permission);}
    
    public static boolean hasPermission(OfflinePlayer player, String permission) {
        if(player.getName().equalsIgnoreCase("CONSOLE")) return true;
        if(!player.isOnline()) return false;
        return hasPermission((CommandSender) player.getPlayer(), permission);
    }
    
    public static boolean canSee(CommandSender p, OfflinePlayer target) {
        if(!isPlayer(p)) return true;
        if(target == null) return true;
        if(!target.isOnline()) return true;
        return getPlayer(p).canSee(target.getPlayer());
    }
    
    public static boolean canSee(OfflinePlayer player, OfflinePlayer target) {
        if(!player.isOnline()) return player.isOp();
        return canSee((CommandSender) getPlayer(player), target);
    }
    
    public static boolean isVisible(OfflinePlayer t) {
        if(t == null) return true;
        if(!t.isOnline()) return true;
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.getName().equalsIgnoreCase(t.getName())) continue;
            if(!canSee((CommandSender) p, t)) return false;
        }
        return true;
    }
    
    public static List<OfflinePlayer> getPlayersList() {
        List<OfflinePlayer> rv = new ArrayList<OfflinePlayer>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!isVisible(p)) continue;
            rv.add(Bukkit.getOfflinePlayer(p.getName()));
        }
        return rv;
    }
    
    public static List<Player> getOnlinePlayers(CommandSender rel) {
        List<Player> players = new ArrayList<Player>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!canSee(rel, p)) continue;
            players.add(p);
        }
        return players;
    }
    
    //Time Utils
    public static long getNow() {
        return System.currentTimeMillis();
    }
    
    public static boolean isValidTime(String input) {
        String[] names = new String[]{
            ("year"),
            ("years"),
            ("month"),
            ("months"),
            ("day"),
            ("days"),
            ("hour"),
            ("hours"),
            ("minute"),
            ("minutes"),
            ("second"),
            ("seconds")
        };
        
        Pattern timePattern = Pattern.compile(
        "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        
        Matcher m = timePattern.matcher(input);
        
        while(m.find()) {
            	if (m.group() == null || m.group().isEmpty()) {
                    continue;
                }
                for (int i = 0; i < m.groupCount(); i++) {
                    if (m.group(i) != null && !m.group(i).isEmpty()) {
                        return true;
                    }
                }
        }
        
        return false;
    }
    
    public static String getTimeDifference(Date late) {return Base.getTimeDifference(new Date(), late);}
    public static String getTimeDifference(long late) {return Base.getTimeDifference(new Date(), new Date(late));}
    public static String getTimeDifference(long early, long late) {return Base.getTimeDifference(new Date(early), new Date(late));}
    
    public static String getTimeDifference(Date early, Date late) {
        StringBuilder sb = new StringBuilder();
        long diffInSeconds = (late.getTime() - early.getTime()) / 1000;

        /*long diff[] = new long[]{0, 0, 0, 0};
        /* sec *  diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        /* min *  diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours *  diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days * diff[0] = (diffInSeconds = (diffInSeconds / 24));
         */
        long sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        long min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        long hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        long days = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
        long months = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
        long years = (diffInSeconds = (diffInSeconds / 12));

        if (years > 0) {
            if (years == 1) {
                sb.append("a year");
            } else {
                sb.append(years + " years");
            }
            if (years <= 6 && months > 0) {
                if (months == 1) {
                    sb.append(" and a month");
                } else {
                    sb.append(" and " + months + " months");
                }
            }
        } else if (months > 0) {
            if (months == 1) {
                sb.append("a month");
            } else {
                sb.append(months + " months");
            }
            if (months <= 6 && days > 0) {
                if (days == 1) {
                    sb.append(" and a day");
                } else {
                    sb.append(" and " + days + " days");
                }
            }
        } else if (days > 0) {
            if (days == 1) {
                sb.append("a day");
            } else {
                sb.append(days + " days");
            }
            if (days <= 3 && hrs > 0) {
                if (hrs == 1) {
                    sb.append(" and an hour");
                } else {
                    sb.append(" and " + hrs + " hours");
                }
            }
        } else if (hrs > 0) {
            if (hrs == 1) {
                sb.append("an hour");
            } else {
                sb.append(hrs + " hours");
            }
            if (min > 1) {
                sb.append(" and " + min + " minutes");
            }
        } else if (min > 0) {
            if (min == 1) {
                sb.append("a minute");
            } else {
                sb.append(min + " minutes");
            }
            if (sec > 1) {
                sb.append(" and " + sec + " seconds");
            }
        } else {
            if (sec <= 1) {
                sb.append("about a second");
            } else {
                sb.append("about " + sec + " seconds");
            }
        }


        /*String result = new String(String.format(
        "%d day%s, %d hour%s, %d minute%s, %d second%s ago",
        diff[0],
        diff[0] > 1 ? "s" : "",
        diff[1],
        diff[1] > 1 ? "s" : "",
        diff[2],
        diff[2] > 1 ? "s" : "",
        diff[3],
        diff[3] > 1 ? "s" : ""));*/
        return sb.toString();
    }
    
    public static Date addStringToNow(String input) {
        boolean found = false;
        
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        Date now = new Date();
        String[] names = new String[]{
            ("year"),
            ("years"),
            ("month"),
            ("months"),
            ("day"),
            ("days"),
            ("hour"),
            ("hours"),
            ("minute"),
            ("minutes"),
            ("second"),
            ("seconds")
        };
        
        Pattern timePattern = Pattern.compile(
        "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
        + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        
        Matcher m = timePattern.matcher(input);
        
        while(m.find()) {
            	if (m.group() == null || m.group().isEmpty()) {
                    continue;
                }
                for (int i = 0; i < m.groupCount(); i++) {
                    if (m.group(i) != null && !m.group(i).isEmpty()) {
                        found = true;
                    }
                    if(found) {
                        if (m.group(1) != null && !m.group(1).isEmpty()) {
                            years = Integer.parseInt(m.group(1));
                        }
                        if (m.group(2) != null && !m.group(2).isEmpty()) {
                            months = Integer.parseInt(m.group(2));
                        }
                        if (m.group(3) != null && !m.group(3).isEmpty()) {
                            weeks = Integer.parseInt(m.group(3));
                        }
                        if (m.group(4) != null && !m.group(4).isEmpty()) {
                            days = Integer.parseInt(m.group(4));
                        }
                        if (m.group(5) != null && !m.group(5).isEmpty()) {
                            hours = Integer.parseInt(m.group(5));
                        }
                        if (m.group(6) != null && !m.group(6).isEmpty()) {
                            minutes = Integer.parseInt(m.group(6));
                        }
                        if (m.group(7) != null && !m.group(7).isEmpty()) {
                            seconds = Integer.parseInt(m.group(7));
                        }
                        break;
                    }
                }
        }
        
        Calendar c = Calendar.getInstance();
        if (years > 0) {
            c.add(Calendar.YEAR, years);
        }
        if (months > 0)  {
            c.add(Calendar.MONTH, months);
        }
        if (weeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, weeks);
        }
        if (days > 0) {
            c.add(Calendar.DAY_OF_MONTH, days);
        }
        if (hours > 0) {
            c.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes > 0) {
            c.add(Calendar.MINUTE, minutes);
        }
        if (seconds > 0) {
            c.add(Calendar.SECOND, seconds);
        }
        now = c.getTime();
        return now;
    }
    
    public static String getHumanDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("d");
        int day = getInt(format.format(date));
        String ordinal = day + Base.getOrdinal(day);
        
        String humanDay = new SimpleDateFormat("EEEE").format(date);
        String humanMonth = new SimpleDateFormat("MMMM").format(date);
        String humanYear = new SimpleDateFormat("yyyy").format(date);
        String time = new SimpleDateFormat("h:mm:ss a").format(date);
        
        String rv = humanDay + ", the " + ordinal + " of " + humanMonth + " " + humanYear + " at " + time;
        
        return rv;
    }

    public static String getHumanTimeAway(Date unbanDate) {
        Long NowInMilli = (new Date()).getTime();
        Long TargetInMilli = unbanDate.getTime();
        Long diffInSeconds = (TargetInMilli - NowInMilli) / 1000+1;

        long diff[] = new long[] {0,0,0,0,0};
        /* sec */diff[4] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        /* min */diff[3] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */diff[1] = (diffInSeconds = (diffInSeconds / 24)) >= 31 ? diffInSeconds % 31: diffInSeconds;
        /* months */diff[0] = (diffInSeconds = (diffInSeconds / 31));
        
        String message = "";
        
        if(diff[0] > 0) {
            message += diff[0] + " month";
            if(diff[0] > 1) {
                message += "s";
            }
            return message;
        }
        if(diff[1] > 0) {
            message += diff[1] + " day";
            if(diff[1] > 1) {
                message += "s";
            }
            return message;
        }
        if(diff[2] > 0) {
            message += diff[2] + " hour";
            if(diff[2] > 1) {
                message += "s";
            }
            return message;
        }
        if(diff[3] > 0) {
            message += diff[3] + " minute";
            if(diff[3] > 1) {
                message += "s";
            }
            return message;
        }
        if(diff[4] > 0) {
            message += diff[4] + " second";
            if(diff[4] > 1) {
                message += "s";
            }
            return message;
        }
        
        return "Invalid Time Diff!";
    }
    
    //Material Utils
    public static boolean isAir(Block type) {
        if(type == null) return true;
        return isAir(type.getType());
    }
    
    public static boolean isAir(Material type) {
        if(type == null) return true;
        if(type.equals(Material.AIR)) return true;
        return !type.isSolid();
    }
    
    //Economy Utils
    public static boolean useEcon() {
        return VaultHook.VAULT_HOOK.isHooked() && VaultHook.VAULT_HOOK.getEconomy() != null;
    }
    
    public static double getBalance(String player) {
        try {
            return VaultHook.VAULT_HOOK.getEconomy().getBalance(player);
        } catch(Exception e) {} catch(Error e) {}
        return 0.0d;
    }
    
    public static String formatEcon(double amt) {
        try {
            return VaultHook.VAULT_HOOK.getEconomy().format(amt);
        } catch(Exception e) { } catch(Error e) {}
        return Base.twoDecimalPlaces(amt);
    }
    
    public static void chargePlayer(OfflinePlayer player, double amt) {
        if(player == null) return;
        chargePlayer(player.getName(), amt);
    }
    
    public static void chargePlayer(String player, double amt) {
        if(player == null) return;
        try {
            if(amt == 0) return;
            if(amt < 0) {
                VaultHook.VAULT_HOOK.getEconomy().depositPlayer(player, -amt);
            } else if(amt > 0) {
                VaultHook.VAULT_HOOK.getEconomy().withdrawPlayer(player, amt);
            }
        } catch(Exception e) {} catch(Error e) {}
    }
}
