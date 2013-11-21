package com.domsplace.CreditShops.Hooks;

import com.domsplace.CreditShops.Bases.PluginHook;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook extends PluginHook {
    private Permission permission = null;
    private Chat chat = null;
    private Economy economy = null;
    
    public VaultHook() {
        super("Vault");
        this.shouldHook(true);
    }
    
    public Permission getPermission() {
        try {
            this.setupPermission();
            return permission;
        } catch(NoClassDefFoundError e) {
            return null;
        }
    }
    
    public Chat getChat() {
        try {
            this.setupChat();
            return chat;
        } catch(NoClassDefFoundError e) {
            return null;
        }
    }
    
    public Economy getEconomy() {
        try {
            this.setupEconomy();
            return economy;
        } catch(NoClassDefFoundError e) {
            return null;
        }
    }
    
    private boolean setupPermission() {
        try {
            RegisteredServiceProvider<Permission> provider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            if (provider != null) {
                permission = provider.getProvider();
            }

            return (permission != null);
        } catch(NoClassDefFoundError e) {
            permission = null;
            return false;
        }
    }
    
    private boolean setupChat() {
        try {
            RegisteredServiceProvider<Chat> provider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
            if (provider != null) {
                chat = provider.getProvider();
            }

            return (chat != null);
        } catch(NoClassDefFoundError e) {
            chat = null;
            return false;
        }
    }
    
    private boolean setupEconomy() {
        try {
            RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (provider != null) {
                economy = provider.getProvider();
            }

            return (economy != null);
        } catch(NoClassDefFoundError e) {
            economy = null;
            return false;
        }
    }
    
    @Override
    public void onHook() {
        super.onHook();
        this.setupPermission();
        this.setupChat();
        this.setupEconomy();
    }
    
    @Override
    public void onUnhook() {
        super.onUnhook();
        this.permission = null;
        this.chat = null;
        this.economy = null;
    }
}
