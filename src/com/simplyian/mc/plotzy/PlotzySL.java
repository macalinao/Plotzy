/*
 * Plotzy Land Protection System for Bukkit
 * Copyright (C) 2011 simplyianm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.simplyian.mc.plotzy;

import com.nijikokun.register.payment.Methods;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

/**
 * Plotzy Server Listener
 * 
 * @author simplyianm
 * @since 0.2
 */
public class PlotzySL extends ServerListener {
    /**
     * Plotzy Main Class Instance
     * 
     * @since 0.1
     */
    private Plotzy pl;
    
    /**
     * Methods
     * 
     * @since 0.1
     */
    private Methods Methods = null;

    /**
     * Constructor
     * 
     * @param instance
     * 
     * @since 0.1
     */
    public PlotzySL(Plotzy instance) {
        this.pl = instance;
        this.Methods = new Methods();
    }

    /**
     * Triggered when a plugin is enabled.
     * 
     * @param event 
     * 
     * @since 0.1
     */
    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (this.Methods.hasMethod() == false) { //If we don't have a method yet
            if(this.Methods.setMethod(event.getPlugin()) == true) {
                this.pl.method = this.Methods.getMethod();
                pl.getLog().info("[Plotzy] Payment Method Found: " + this.pl.getMethod().getName() + " version " + this.pl.getMethod().getVersion() + ".");
            }
        }
    }
    
    /**
     * Triggered when a plugin is disabled.
     * 
     * @param event 
     * 
     * @since 0.1
     */
    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        // Check to see if the plugin thats being disabled is the one we are using
        if (this.Methods != null && this.Methods.hasMethod()) {
            if(this.Methods.checkDisabled(event.getPlugin()) == true) {
                this.pl.method = null;
                pl.getLog().info("[Plotzy] The payment system of choice was disabled. Hopefully you're shutting down or reloading.");
            }
        }
    }
}