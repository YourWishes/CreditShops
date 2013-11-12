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

import com.domsplace.CreditShops.Hooks.VaultHook;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * @author      Dominic
 * @since       11/10/2013
 */
public class PluginHook extends Base{
    //Constants
    private static final List<PluginHook> PLUGIN_HOOKS = new ArrayList<PluginHook>();
    
    public static final VaultHook VAULT_HOOK = new VaultHook();
    
    //Static
    private static void hookPlugin(PluginHook hook) {
        PLUGIN_HOOKS.add(hook);
    }
    
    public static PluginHook getHookFromPlugin(Plugin plugin) {
        for(PluginHook hook : PLUGIN_HOOKS) {
            if(hook == null) continue;
            if(hook.getPluginName().equals(plugin.getName())) return hook;
        }
        
        return null;
    }
    
    public static void hookAll() {
        for(PluginHook plugin : PLUGIN_HOOKS) {
            if(plugin.hook());
        }
    }
    
    public static void unhookAll() {
        for(PluginHook plugin : PLUGIN_HOOKS) {
            plugin.unHook();
        }
    }
    
    //Instance
    private String pluginName;
    private Plugin plugin;
    private boolean shouldHook = false;
    
    public PluginHook(String plugin) {
        this.pluginName = plugin;
        hookPlugin(this);
    }
    
    public String getPluginName() {return this.pluginName;}
    public Plugin getHookedPlugin() {return this.plugin;}
    
    public void onUnhook() {}
    public void onHook() {}
    
    public boolean isHooked() {return this.plugin != null;}
    
    public boolean shouldHook() {return this.shouldHook;}
    public void shouldHook(boolean t) {this.shouldHook = t;}
    
    public boolean hook() {
        if(!shouldHook) return false;
        try {
            this.plugin = tryHook();
            if(this.plugin != null) {
                this.onHook();
                return true;
            } else return false;
        } catch(Exception e) {
            this.plugin = null;
            return false;
        } catch(NoClassDefFoundError e) {
            this.plugin = null;
            return false;
        }
    }
    
    public Plugin tryHook() throws NoClassDefFoundError {
        Plugin p = Bukkit.getPluginManager().getPlugin(this.pluginName);
        
        if(p == null || !p.isEnabled()) return null;
        
        return p;
    }
    
    
    public void unHook() {
        if(!this.isHooked()) return;
        this.onUnhook();
        this.plugin = null;
    }
}
