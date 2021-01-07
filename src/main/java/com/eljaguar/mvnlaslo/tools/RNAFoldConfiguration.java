/*
 * Copyright (C) 2019 dmancilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.eljaguar.mvnlaslo.tools;

/**
 * 
 * @author dmancilla
 */
public final class RNAFoldConfiguration {
    private static boolean avoidLonelyPairs;
    private static boolean avoidGUPairs;
    public static final  int DEFAULT_TEMP = 37; 
    public static final  int SEQUENCE_MAX_SIZE = 20000;

    private RNAFoldConfiguration(){
        // not called
    }
    
    public static boolean isAvoidLonelyPairs() {
        return avoidLonelyPairs;
    }

    public static void setAvoidLonelyPairs(boolean avoidLonelyPairs) {
        RNAFoldConfiguration.avoidLonelyPairs = avoidLonelyPairs;
    }

    public static boolean isAvoidGUPairs() {
        return avoidGUPairs;
    }

    public static void setAvoidGUPairs(boolean avoidGUPairs) {
        RNAFoldConfiguration.avoidGUPairs = avoidGUPairs;
    }
    
    
}
