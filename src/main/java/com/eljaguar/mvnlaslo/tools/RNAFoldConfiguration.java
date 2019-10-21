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
public class RNAFoldConfiguration {
    private static boolean avoidLonelyPairs;
    private static boolean avoidGUPairs;
    public final static int DEFAULT_TEMP = 37; 
    public final static int SEQUENCE_MAX_SIZE = 20000;
}
