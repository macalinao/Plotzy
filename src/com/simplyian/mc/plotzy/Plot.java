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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Plotzy Plot Object
 * 
 * @author simplyianm
 * @since 0.3
 */
public class Plot {
    /**
     * Plot name
     * 
     * @since 0.3
     */
    private String pname;
    
    /**
     * Plot center
     * 
     * @since 0.3
     */
    private Location pcenter;
    
    /**
     * Plot size
     * 
     * @since 0.3
     */
    private int psize;
    
    /**
     * Flags
     * 
     * @since 0.3
     */
    private HashMap<String, String> flags;
    
    /**
     * Roles
     * 
     * @since 0.3
     */
    private HashMap<String, Integer> roles;
    
    /**
     * Use MySQL?
     * 
     * @since 0.3
     */
    private boolean mysql = true;
    
    /**
     * Constructor
     * 
     * @param plotName
     * @param plotCenter
     * @param plotSize 
     * 
     * @since 0.3
     */
    public Plot(String plotName, Location plotCenter, int plotSize) {
        this.pname = plotName;
        this.pcenter = plotCenter;
        this.psize = plotSize;
        this.flags = this.getFlags();
        this.roles = this.getRoles();
    }
    
    /**
     * Gets the plot name.
     * 
     * @return Plot name
     * 
     * @since 0.3
     */
    public String getName() {
        return this.pname;
    }
    
    /**
     * Gets the plot center.
     * 
     * @return Plot center
     * 
     * @since 0.3
     */
    public Location getCenter() {
        return this.pcenter;
    }
    
    /**
     * Gets the plot size.
     * 
     * @return Plot size
     */
    public int getSize() {
        return this.psize;
    }
    
    /**
     * Deletes the plot.
     * 
     * @since 0.3
     */
    public void delete() { //Somehow, we have to refresh the plot HashMap.
        Database.execute("DELETE FROM " + Database.prefix + "plotzy_plots WHERE pl_name = '" + this.getName() + "'");
        Database.execute("DELETE FROM " + Database.prefix + "plotzy_players WHERE py_plot = '" + this.getName() + "'");
        Database.execute("DELETE FROM " + Database.prefix + "plotzy_flags WHERE fl_plot = '" + this.getName() + "'");
    }
    
    /**
     * Sets the plot size.
     * 
     * @param size 
     * 
     * @since 0.3
     */
    public void setSize(int size) {
        Database.execute("UPDATE " + Database.prefix + "plotzy_plots SET pl_size = '" + size + "' WHERE pl_name = '" + this.getName() + "'");
        this.psize = size;
    }
    
    /**
     * Expands the plot by the given amount.
     * 
     * @param amount 
     * 
     * @since 0.3
     */
    public void expand(int amount) {
        this.setSize(this.getSize() + amount);
    }
    
    /**
     * Shrinks the plot by the given amount.
     * 
     * @param amount 
     * 
     * @since 0.3
     */
    public void shrink(int amount) {
        this.setSize(this.getSize() - amount);
    }
    
    /**
     * Checks if the plot contains the given location.
     * 
     * @param loc
     * @return boolean
     * 
     * @since 0.3
     */
    public boolean contains(Location loc) {
        return Math.floor(loc.distanceSquared(this.getCenter())) <= Math.pow(this.getSize(), 2D) ? true : false;
    }
    
    /**
     * Gets all of the roles for the plot.
     * 
     * @return Flags
     * 
     * @since 0.3
     */
    private HashMap<String, Integer> getRoles() {
        HashMap<String, Integer> roleList = new HashMap<String, Integer>();
        if (this.mysql == true) {
            ResultSet rs = Database.getResultSet("SELECT py_player, py_role FROM " + Database.prefix + "plotzy_players WHERE py_plot = '" + this.getName() + "'");
            try {
                while (rs.next()) {
                    roleList.put(rs.getString("py_player"), rs.getInt("py_role"));
                }
            } catch (SQLException ex) {
                Database.sqlErrors(ex);
            }
        }
        return roleList;
    }
    
    /**
     * Sets a player's role within the plot.
     * 
     * @param player Player to give role to
     * @param role
     *      1 = Owner, 2 = Builder, 3 = Resident
     * @return Player role
     * 
     * @since 0.1
     */
    public int setRole(String player, int role) {
        if (this.mysql == true) {
            int count = Database.getInteger("SELECT COUNT(*) FROM " + Database.prefix + "plotzy_players WHERE py_player = '" + player + "' AND py_plot = '" + this.getName() + "'");
            if (count < 1) {
                Database.execute("INSERT INTO " + Database.prefix + "plotzy_players VALUES (0, '" + this.getName() + "', '" + player + "', '" + role + "')");
            } else {
                Database.execute("UPDATE " + Database.prefix + "plotzy_players SET py_role = '" + role + "' WHERE py_player = '" + player + "' AND py_plot = '" + this.getName() + "' LIMIT 1");
            }
            System.out.print(count);
        }
        Integer oldPlayerRole = this.roles.put(player, role);
        return oldPlayerRole == null ? 0 : oldPlayerRole.intValue();
    }
    
    /**
     * Returns the player's role within the plot.
     * 
     * @param player
     * @return Player role:
     *      1 = Owner, 2 = Builder, 3 = Resident
     * 
     * @since 0.3
     */
    public int getRole(String player) {
        return this.roles.get(player) == null ? 0 : this.roles.get(player);
    }
    
    /**
     * Gets all of the flags for the plot.
     * 
     * @return Flags
     * 
     * @since 0.3
     */
    private HashMap<String, String> getFlags() {
        HashMap<String, String> flagList = new HashMap<String, String>();
        if (this.mysql == true) {
            ResultSet rs = Database.getResultSet("SELECT fl_flag, fl_value FROM " + Database.prefix + "plotzy_flags WHERE fl_plot = '" + this.getName() + "'");
            try {
                while (rs.next()) {
                    flagList.put(rs.getString("fl_flag"), rs.getString("fl_value"));
                }
            } catch (SQLException ex) {
                Database.sqlErrors(ex);
            }
        }
        return flagList;
    }
    
    /**
     * Sets a flag.
     * 
     * @param flagName
     * @param flagValue
     * @return Old flag value
     * 
     * @since 0.3
     */
    public String setFlag(String flagName, String flagValue) {
        if (this.mysql == true) {
            int count = Database.getInteger("SELECT COUNT(*) FROM " + Database.prefix + "plotzy_flags WHERE fl_flag = '" + flagName + "' AND fl_value = '" + flagValue + "' AND fl_plot = '" + this.getName() + "'");
            if (count < 1) {
                Database.execute("INSERT INTO " + Database.prefix + "plotzy_flags VALUES (0, '" + this.getName() + "', '" + flagName + "', '" + flagValue + "')");
            } else {
                Database.execute("UPDATE " + Database.prefix + "plotzy_flags SET fl_value = '" + flagValue + "' WHERE fl_flag = '" + flagName + "' AND fl_plot = '" + this.getName() + "' LIMIT 1");
            }
        }
        return this.flags.put(flagName, flagValue);
    }
    
    /**
     * Sets a flag as a boolean.
     * 
     * @param flagName
     * @param flagValue
     * @return Old flag value
     * 
     * @since 0.3
     */
    public String setFlag(String flagName, boolean flagValue) {
        return this.setFlag(flagName, Boolean.toString(flagValue));
    }
    
    /**
     * Sets a flag as an integer.
     * 
     * @param flagName
     * @param flagValue
     * @return Old flag value
     * 
     * @since 0.4
     */
    public String setFlag(String flagName, int flagValue) {
        return this.setFlag(flagName, Integer.toString(flagValue));
    }
    
    /**
     * Gets a flag.
     * 
     * @param flagName
     * @return Flag value
     * 
     * @since 0.3
     */
    public String getFlag(String flagName) {
        return this.flags.get(flagName);
    }
    
    /**
     * Gets a flag as a boolean.
     * 
     * @param flagName
     * @return Flag value as boolean
     * 
     * @since 0.3
     */
    public boolean getFlagBoolean(String flagName) {
        return Boolean.parseBoolean(getFlag(flagName));
    }
    
    /**
     * Gets a flag as an integer.
     * 
     * @param flagName
     * @return Flag value as integer
     * 
     * @since 0.4
     */
    public int getFlagInteger(String flagName) {
        return Integer.parseInt(getFlag(flagName));
    }
    
    /**
     * Checks if a player can break blocks in the plot.
     * (Owner or Builder)
     * 
     * @param player
     * @return Boolean
     * 
     * @since 0.1
     */
    public boolean canBreakBlocks(Player player) {
        if (Plotzy.hasPermission(player, "plotzy.admin.override.block.break")) return true;
        if (player.isOp()) return true;
        int role = this.getRole(player.getName());
        return role == 1 || role == 2 ? true : false;
    }
    
    /**
     * Checks if a player can place blocks in the plot.
     * (Owner or Builder)
     * 
     * @param player
     * @return Boolean
     * 
     * @since 0.3
     */
    public boolean canPlaceBlocks(Player player) {
        if (Plotzy.hasPermission(player, "plotzy.admin.override.block.place")) return true;
        if (player.isOp()) return true;
        int role = this.getRole(player.getName());
        return role == 1 || role == 2 ? true : false;
    }
    
    /**
     * Checks if a player can use buttons in the plot.
     * (Owner, Builder, or Resident)
     * 
     * @param player
     * @return Boolean
     * 
     * @since 0.1
     */
    public boolean canUseButtons(Player player) {
        if (Plotzy.hasPermission(player, "plotzy.admin.override.use.buttons")) return true;
        if (player.isOp()) return true;
        if (this.getFlagBoolean("private") == true) {
            int role = this.getRole(player.getName());
            return role == 1 || role == 2 || role == 3 ? true : false;
        } else {
            return true;
        }
    }    
    /**
     * Checks if a player can use levers in the plot.
     * (Owner, Builder, or Resident)
     * 
     * @param player
     * @return Boolean
     * 
     * @since 0.3
     */
    public boolean canUseLevers(Player player) {
        if (Plotzy.hasPermission(player, "plotzy.admin.override.use.levers")) return true;
        if (player.isOp()) return true;
        if (this.getFlagBoolean("private") == true) {
            int role = this.getRole(player.getName());
            return role == 1 || role == 2 || role == 3 ? true : false;
        } else {
            return true;
        }
    }
    
    /**
     * Checks if the player can delete the given plot.
     * (Owner)
     * 
     * @param player
     * @return Boolean
     * 
     * @since 0.3
     */
    public boolean canDelete(Player player) {
        if (Plotzy.hasPermission(player, "plotzy.admin.override.delete")) return true;
        if (player.isOp()) return true;
        int role = this.getRole(player.getName());
        return role == 1 ? true : false;
    }    
    
    /**
     * Checks if a player can expand the plot.
     * (Owner)
     * 
     * @param player
     * @return Boolean
     * 
     * @since 0.3
     */
    public boolean canExpand(Player player) {
        if (Plotzy.hasPermission(player, "plotzy.admin.override.expand")) return true;
        if (player.isOp()) return true;
        int role = this.getRole(player.getName());
        return role == 1 ? true : false;
    }    
    
    /**
     * Checks if a player can shrink the plot.
     * (Owner)
     * 
     * @param player
     * @return Boolean
     * 
     * @since 0.3
     */
    public boolean canShrink(Player player) {
        if (Plotzy.hasPermission(player, "plotzy.admin.shrink")) return true;
        if (player.isOp()) return true;
        int role = this.getRole(player.getName());
        return role == 1 ? true : false;
    }
    
    /**
     * Checks if a flag exists.
     * 
     * @param flag
     * @return Does the flag exist?
     * 
     * @since 0.4
     */
    public boolean flagExists(String flag) {
        return flags.containsKey(flag);
    }
}