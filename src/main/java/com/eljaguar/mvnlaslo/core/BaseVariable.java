/*
 * Copyright (C) 2019 David A. Mancilla
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
package com.eljaguar.mvnlaslo.core;

/**
 * 
 * @author David A. Mancilla
 */
public class BaseVariable {
   private Character base;
   private Integer position;
   private Character value;

  /**
   * 
   * @param base
   * @param position
   * @param value 
   */
   BaseVariable(Character base, Integer position){
       this.base = base;
       this.position = position;
       this.value = null;
   }
   
    /**
     * @return the base
     */
    public Character getBase() {
        return base;
    }

    /**
     * @return the position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * @param base the base to set
     */
    public void setBase(Character base) {
        this.base = base;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Integer position) {
        this.position = position;
    }
    
    public void setValue(Character value){
        if(value == 'A' || value == 'C' || value == 'T' 
            || value == 'U' || value == 'G'){
            this.value = value;
        }
    }
    
    public Character getValue(){
        return this.value;
    }
    
   @Override
    public String toString(){
        return "[" + base + "; " + position + ";" + value + "]";
    }
}
