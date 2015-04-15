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

import com.nijikokun.register.payment.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plotzy Main Class
 * 
 * @author simplyianm
 * @since 0.1
 */
public class Plotzy extends JavaPlugin {
    /**
     * Console output
     * 
     * @since 0.1
     */
    private static final Logger log = Logger.getLogger("Minecraft");

    /**
     * @return the log
     */
    public static Logger getLog() {
        return log;
    }
    
    /**
     * Block listener
     * 
     * @since 0.1
     */
    private final PlotzyBL blockListener = new PlotzyBL(this);    
    
    /**
     * Player listener
     * 
     * @since 0.1
     */
    private final PlotzyPL playerListener = new PlotzyPL(this);    
    
    /**
     * Server listener
     * 
     * @since 0.1
     */
    private final PlotzySL serverListener = new PlotzySL(this);
    
    /**
     * Money
     * 
     * @since 0.1
     */
    private Commands commands = new Commands(this);
    
    /**
     * HashMap of player locations
     * 
     * @since 0.1
     */
    private HashMap<String, Block> playerLocs;
    
    /**
     * Method of payment
     * 
     * @since 0.1
     */
    private Method method = null;
    
    /**
     * Global plot ResultSet
     * 
     * @since 0.3
     */
    private HashMap<String, Plot> plotList;
    
    /**
     * Access to static plot functions
     * 
     * @since 0.3
     */
    private PlotFunctions pf;
    
    /**
     * Soon this will mean something.
     * 
     * @since 0.3
     */
    private boolean mysql = true;
    
    /**
     * Triggered on the enabling of the plugin.
     * 
     * @since 0.1
     */
    @Override
    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        
        pm.registerEvent(Event.Type.BLOCK_BREAK, getBlockListener(), Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, getBlockListener(), Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, getPlayerListener(), Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, getPlayerListener(), Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, getServerListener(), Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, getServerListener(), Event.Priority.Monitor, this);
        
        this.playerLocs = new HashMap<String, Block>();
        setupPermissions(); //Permissions support @since 0.2
        this.plotList = this.getPlotList();
        getLog().info("[Plotzy] Plugin enabled."); //sc19.servercraft.co:3145
    }

    /**
     * Triggered on the disabling of the plugin.
     * 
     * @since 0.1
     */
    @Override
    public void onDisable() {
        method = null;
        getLog().info("[Plotzy] Plugin disabled.");
    }
    
    /**
     * Commands
     * 
     * @param sender
     * @param cmd
     * @param commandLabel
     * @param args
     * @return Did the person reach the command or not?
     * 
     * @since 0.1
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String command = cmd.getName().toLowerCase();
        if (command.equals("pz")) return this.getCommands().pz(sender, commandLabel, args);
        return false;
    }
    
    /**
     * Permissions support
     * 
     * @since 0.2
     */
    private void setupPermissions() {
        if (permissionHandler != null) return;
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
        if (permissionsPlugin == null) {
            this.cnsl("Permission system not detected, defaulting to OP");
            return;
        }
        permissionHandler = ((Permissions) permissionsPlugin).getHandler();
        this.cnsl("Found and will use plugin "+((Permissions)permissionsPlugin).getDescription().getFullName());
    }
    
    /**
     * Output to console with prefix
     * 
     * @param msg 
     * 
     * @since 0.2
     */
    public void cnsl(String msg) {
        this.getLog().info("[Plotzy] " + msg);
    }
    
    /**
     * Gets a HashMap of all plots.
     * 
     * @return HashMap
     * 
     * @since 0.3
     */
    public static HashMap<String, Plot> getPlotList() {
        HashMap<String, Plot> plotList = new HashMap<String, Plot>();
        boolean mysql = true;
        if (mysql == true) {
            try {
                ResultSet plots = Database.getResultSet("SELECT * FROM " + Database.prefix + "plotzy_plots");
                while (plots.next()) {
                    String plotName = plots.getString("pl_name");
                    Location plotCenter = new Location(Bukkit.getServer().getWorld(plots.getString("pl_world")), plots.getInt("pl_x"), plots.getInt("pl_y"), plots.getInt("pl_z"));
                    int plotSize = plots.getInt("pl_size");
                    plotList.put(plotName, new Plot(plotName, plotCenter, plotSize));
                }
                return plotList;
            } catch (SQLException ex) {
                Database.sqlErrors(ex);
            }
        }
        return null;
    }

    /**
     * @return the blockListener
     */
    public PlotzyBL getBlockListener() {
        return blockListener;
    }

    /**
     * @return the playerListener
     */
    public PlotzyPL getPlayerListener() {
        return playerListener;
    }

    /**
     * @return the serverListener
     */
    public PlotzySL getServerListener() {
        return serverListener;
    }

    /**
     * @return the commands
     */
    public Commands getCommands() {
        return commands;
    }

    /**
     * @return the playerLocs
     */
    public HashMap<String, Block> getPlayerLocs() {
        return playerLocs;
    }

    /**
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @return the pf
     */
    public PlotFunctions getPf() {
        return pf;
    }

    /**
     * @return the mysql
     */
    public boolean isMysql() {
        return mysql;
    }
}