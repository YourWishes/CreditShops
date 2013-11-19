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

package com.domsplace.CreditShops;

import com.domsplace.CreditShops.Threads.ConfigSaveThread;
import com.domsplace.CreditShops.Commands.*;
import com.domsplace.CreditShops.Bases.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author      Dominic
 * @since       12/11/2013
 */
public class CreditShopsPlugin extends JavaPlugin {
    private boolean enabled = false;
    
    //Commands
    private PriceCommand priceCommand;
    
    //Listeners
    
    //Threads
    private ConfigSaveThread configSaveThread;
    
    @Override
    public void onEnable() {
        //Register Plugin
        Base.setPlugin(this);
        
        //Load Data
        if(!DataManager.loadAll()) {
            this.disable();
            return;
        }
        
        //Load Commands
        this.priceCommand = new PriceCommand();
        
        //Load Listeners
        
        //Load Threads
        this.configSaveThread = new ConfigSaveThread();
        
        PluginHook.hookAll();
        
        this.enabled = true;
        Base.debug("Finished Loading " + this.getName() + ", " + BukkitCommand.getCommands().size() + " commands registered.");
    }
    
    @Override
    public void onDisable() {
        if(!enabled) {
            return;
        }
        
        //Unhook Economy
        
        DomsThread.stopAllThreads();
        DataManager.saveAll();
    }
    
    public void disable() {
        Bukkit.getPluginManager().disablePlugin(this);
    }
}
