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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author      Dominic
 * @since       11/10/2013
 */
public class DomsThread extends Base implements Runnable {
    private static final List<DomsThread> THREADS = new ArrayList<DomsThread>();
    
    //Static
    public static void stopAllThreads() {
        for(DomsThread t : THREADS) {
            t.stopThread();
        }
    }
    
    public static void registerThread(DomsThread thread) {
        DomsThread.getThreads().add(thread);
    }
    
    public static List<DomsThread> getThreads() {
        return DomsThread.THREADS;
    }
    
    //Instance
    private BukkitTask thread;
    
    public DomsThread(long delay, long repeat) {
        this(delay, repeat, false);
    }
    
    public DomsThread(long delay, long repeat, boolean async) {
        delay = delay * 20L;
        repeat = repeat * 20L;
        
        if(async) {
            this.thread = Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), this, delay, repeat);
        } else {
            this.thread = Bukkit.getScheduler().runTaskTimer(getPlugin(), this, delay, repeat);
        }
        
        DomsThread.registerThread(this);
    }
    
    public BukkitTask getThread() {
        return this.thread;
    }
    
    public void stopThread() {
        if(this.thread == null) return;
        this.getThread().cancel();
    }

    @Override
    public void run() {
    }
}
