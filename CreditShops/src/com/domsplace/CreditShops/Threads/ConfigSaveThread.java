/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.domsplace.CreditShops.Threads;

import com.domsplace.CreditShops.Bases.DataManager;
import com.domsplace.CreditShops.Bases.DomsThread;

/**
 *
 * @author Dominic Masters
 */
public class ConfigSaveThread extends DomsThread {
    public ConfigSaveThread() {
        super(1, 300);
    }
    
    @Override
    public void run() {
        if(DataManager.saveAll()) return;
        log("Failed to save Config! Check Console for Errors!");
    }
}
