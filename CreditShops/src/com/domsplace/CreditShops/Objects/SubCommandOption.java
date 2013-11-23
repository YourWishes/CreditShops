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

package com.domsplace.CreditShops.Objects;

import com.domsplace.CreditShops.Bases.Base;
import com.domsplace.CreditShops.Exceptions.InvalidItemException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author      Dominic
 * @since       05/10/2013
 */
public class SubCommandOption extends Base {
    public static final SubCommandOption PLAYERS_OPTION = new SubCommandOption("[PLAYER]");
    public static final SubCommandOption ITEM_OPTION = new SubCommandOption("[ITEM]");
    public static final SubCommandOption SHOP_OPTION = new SubCommandOption("[SHOP]");
    
    //Instance
    private String option;
    private List<SubCommandOption> subOptions;
    
    public SubCommandOption(String option) {
        this.option = option;
        this.subOptions = new ArrayList<SubCommandOption>();
    }
    
    public SubCommandOption(String option, SubCommandOption... options) {
        this(option);
        for(SubCommandOption o : options) {
            this.subOptions.add(o);
        }
    }
    
    public SubCommandOption(String option, String... options) {
        this(option);
        for(String s : options) {
            this.subOptions.add(new SubCommandOption(s));
        }
    }
    
    public SubCommandOption(SubCommandOption option, SubCommandOption... options) {
        this(option.option, options);
    }
    
    public SubCommandOption(SubCommandOption option, String... options) {
        this(option.option, options);
    }
    
    public String getOption() {return this.option;}
    public List<SubCommandOption> getSubCommandOptions() {return new ArrayList<SubCommandOption>(this.subOptions);}

    public List<String> getOptionsFormatted(CommandSender sender) {
        List<String> returnV = new ArrayList<String>();
        if(this.compare(SubCommandOption.PLAYERS_OPTION)) {
            for(Player p : Base.getOnlinePlayers(sender)) {
                returnV.add(p.getName());
            }
        } else if(this.compare(SubCommandOption.ITEM_OPTION)) {
            for(Material m : Material.values()) {
                if(m.equals(Material.AIR)) continue;
                returnV.add(m.name());
            }
        } else if(this.compare(SubCommandOption.SHOP_OPTION)) {
            for(Shop s : Shop.getShops()) {
                returnV.add(s.getName());
            }
        } else {
            returnV.add(this.option);
        }
        return returnV;
    }
    
    public static String reverse(String s, CommandSender sender) {
        if(Base.getPlayer(sender, s) != null) return SubCommandOption.PLAYERS_OPTION.option;
        if(Shop.getShop(s) != null) return SubCommandOption.SHOP_OPTION.option;
        try {if(DomsItem.guessItem(s) != null) return SubCommandOption.ITEM_OPTION.option;} catch(InvalidItemException e){}
        return s;
    }
    
    public List<String> getOptionsAsStringList(CommandSender sender) {
        List<String> returnV = new ArrayList<String>();
        for(SubCommandOption sc : this.subOptions) {
            returnV.addAll(sc.getOptionsFormatted(sender));
        }
        
        return returnV;
    }
    
    public List<String> tryFetch(String[] args, int lvl, CommandSender sender) {
        List<String> opts = new ArrayList<String>();
        
        lvl = lvl + 1;
        int targetLevel = args.length;
        
        if(targetLevel > lvl) {
            for(SubCommandOption sco : this.subOptions) {
                String s = args[lvl-1].toLowerCase();
                s = reverse(s, sender);
                if(!sco.getOption().toLowerCase().startsWith(s.toLowerCase())) continue;
                opts.addAll(sco.tryFetch(args, lvl, sender));
            }
        } else {
            return this.getOptionsAsStringList(sender);
        }
        
        return opts;
    }
    
    public boolean compare(SubCommandOption option) {
        if(option.getOption().equalsIgnoreCase(this.getOption())) return true;
        return false;
    }
}
