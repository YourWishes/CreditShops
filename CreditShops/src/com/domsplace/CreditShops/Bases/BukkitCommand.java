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
    
import com.domsplace.CreditShops.Objects.SubCommandOption;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

/**
 * @author      Dominic
 * @since       11/10/2013
 */
public class BukkitCommand extends Base implements CommandExecutor, TabCompleter {
    private static final List<BukkitCommand> COMMANDS = new ArrayList<BukkitCommand>();
    
    private static PluginCommand registerCommand(BukkitCommand command) {
        try {
            PluginCommand cmd = getPlugin().getCommand(command.getCommand());
            cmd.setExecutor(command);
            cmd.setPermissionMessage(colorise(Base.getPermissionMessage()));
            cmd.setTabCompleter(command);
            COMMANDS.add(command);
            return cmd;
        } catch(Exception e) {
            error("Failed to Register Command \"" + command.command + "\"", e);
            return null;
        }
    }
    
    public static List<BukkitCommand> getCommands() {return new ArrayList<BukkitCommand>(COMMANDS);}

    public static BukkitCommand getCommand(String command) {
        for(BukkitCommand bc : COMMANDS) {
            if(!bc.getCommand().equalsIgnoreCase(command)) continue;
            return bc;
        }
        return null;
    }
    
    public static BukkitCommand getCommandSearchAliases(String name) {
        for(BukkitCommand bc : COMMANDS) {
            if(bc.isCommand(name)) return bc;
        }
        
        return null;
    }
    
    //Instance
    private String command;
    private PluginCommand cmd;
    private List<SubCommandOption> subOptions;
    
    public BukkitCommand(String command) {
        this.command = command;
        this.cmd = BukkitCommand.registerCommand(this);
        this.subOptions = new ArrayList<SubCommandOption>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase(this.command)) {
            if(!hasPermission(sender, cmd.getPermission())) return noPermission(sender, cmd, label, args);
            boolean result = false;
            //Error Handling
            try {
                result = this.cmd(sender, cmd, label, args);
            } catch(Exception e) {
                debug("Exception e:" + e.getMessage());
                error("Command Execution failed \"" + this.toFullCommand(sender, cmd, label, args) + "\" Show to Plugin Author!", e);
                sendMessage(sender, ChatError + "A command error occured and the command was not finished successfully, please contact an admin!");
            }
            
            if(!result) return commandFailed(sender, cmd, label, args);
            return commandSuccess(sender, cmd, label, args);
        }
        
        return badCommand(sender, cmd, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase(this.getCommand())) {
            List<String> tab = this.tab(sender, cmd, label, args);
            if(tab != null) {
                return tabSuccess(sender, cmd, label, args, tab);
            }
            
            return this.tabFailed(sender, cmd, label, args);
        }
        return badTab(sender, cmd, label, args);
    }
    
    public String getCommand() { return this.command; }
    public PluginCommand getCmd() {return this.cmd;}
    public List<SubCommandOption> getSubCommandOptions() {return new ArrayList<SubCommandOption>(this.subOptions);}
    
    public void addSubCommandOption(SubCommandOption o) {this.subOptions.add(o);}
    public void removeSubCommandOption(SubCommandOption o) {this.subOptions.remove(o);}
    
    public boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {return false;}
    public boolean badCommand(CommandSender sender, Command cmd, String label, String[] args) {return false;}
    public boolean commandSuccess(CommandSender sender, Command cmd, String label, String[] args) {return true;}
    public boolean commandFailed(CommandSender sender, Command cmd, String label, String[] args) {return false;}
    
    public List<String> tab(CommandSender sender, Command cmd, String label, String[] args) {return this.getArgumentGuesses(args, sender);}
    public List<String> tabFailed(CommandSender sender, Command cmd, String label, String[] args) {return null;}
    public List<String> badTab(CommandSender sender, Command cmd, String label, String[] args) {return null;}
    public List<String> tabSuccess(CommandSender sender, Command cmd, String label, String[] args, List<String> successValue) {return successValue;}
    
    public boolean noPermission(CommandSender sender, Command cmd, String label, String[] args) {
        cmd.setPermissionMessage(colorise(Base.getPermissionMessage()));
        sender.sendMessage(cmd.getPermissionMessage());
        return true;
    }
    
    public boolean fakeExecute(CommandSender sender, String commandLine) {
        if(commandLine.startsWith("/")) commandLine = commandLine.replaceFirst("/", "");
        
        String[] s = commandLine.split(" ");
        if(s.length < 1) return false;
        
        String lbl = s[0];
        String[] args = new String[0];
        if(s.length > 1) {
            args = new String[s.length - 1];
            
            for(int i = 1; i < s.length; i++) {
                args[i-1] = s[i];
            }
        }
        
        return this.onCommand(sender, cmd, lbl, args);
    }
    
    public List<String> getArgumentGuesses(String[] args, CommandSender sender) {
        List<String> options = new ArrayList<String>();
        if(args.length == 0) {
            for(SubCommandOption sco : this.subOptions) {
                options.addAll(sco.getOptionsFormatted(sender));
            }
        } else if(args.length == 1) {
            for(SubCommandOption sco : this.subOptions) {
                for(String s : sco.getOptionsFormatted(sender)) {
                    if(!s.toLowerCase().startsWith(args[0].toLowerCase())) continue;
                    options.add(s);
                }
            }
        } else if(args.length > 1) {            
            List<String> matches = new ArrayList<String>();
            
            for(SubCommandOption sco : this.subOptions) {
                String s = args[0].toLowerCase();
                s = SubCommandOption.reverse(s, sender);
                if(!sco.getOption().toLowerCase().startsWith(s.toLowerCase())) continue;
                matches.addAll(sco.tryFetch(args, 1, sender));
            }
            
            if(args[args.length - 1].replaceAll(" ", "").equalsIgnoreCase("")) return matches;
            
            List<String> closeMatch = new ArrayList<String>();
            
            for(String match : matches) {
                if(match.toLowerCase().startsWith(args[args.length-1].toLowerCase())) closeMatch.add(match);
            }
            
            options.addAll(closeMatch);
        }
        return options;
    }
    
    public List<String> getAliases() {return DataManager.PLUGIN_MANAGER.getYML().getStringList("commands." + this.command + ".aliases");}

    public boolean isCommand(String name) {
        if(name.equalsIgnoreCase(this.command)) return true;
        for(String s : this.getAliases()) {
            if(s.equalsIgnoreCase(name)) return true;
        }
        
        return false;
    }

    private String toFullCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String s = isPlayer(sender) ? "/" : "";
        s += label + " " + Base.arrayToString(args, " ");
        return s;
    }
}
