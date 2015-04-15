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

/**
 * Money Class
 * 
 * @author simplyianm
 * @since 0.1
 */
public class MoneyHandler {
    /**
     * Plotzy Main Class
     * 
     * @since 0.1
     */
    private static Plotzy pl;
    
    /**
     * Constructor
     * 
     * @param instance 
     * 
     * @since 0.1
     */
    public MoneyHandler(Plotzy instance) {
        pl = instance;
    }
    
    /**
     * Gets the money of the player.
     * 
     * @param playerName 
     * @return Player money
     * 
     * @since 0.1
     */
    public static double get(String playerName) {
        return pl.getMethod().getAccount(playerName).balance();
    }
    
    /**
     * Adds money to the player's account.
     * 
     * @param playerName 
     * 
     * @param amount 
     * @return 
     * @since 0.1
     */
    public static boolean add(String playerName, double amount) {
        return pl.getMethod().getAccount(playerName).add(amount);
    }
    
    /**
     * Subtracts money from the player's account.
     * 
     * @param playerName 
     * @param amount 
     * @return
     * 
     * @since 0.1
     */
    public static boolean subtract(String playerName, double amount) {
        return pl.getMethod().getAccount(playerName).subtract(amount);
    }
}
