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

    /**
     * variable.
     */
    private Character base;
    /**
     * variable.
     */
    private Integer position;

    /**
     * variable.
     */
    private Character value;

    /**
     *
     * @param pbase
     * @param position
     * @param value
     */
    BaseVariable(final Character pbase, Integer position) {
        this.base = pbase;
        this.position = position;
        this.value = null;
    }

    /**
     * @return the base
     */
    public final Character getBase() {
        return base;
    }

    /**
     * @return the position
     */
    public final Integer getPosition() {
        return position;
    }

    /**
     * @param base the base to set
     */
    public final void setBase(final Character base) {
        this.base = base;
    }

    /**
     * @param position the position to set
     */
    public final void setPosition(final Integer position) {
        this.position = position;
    }

    /**
     *
     * @param pvalue sdf
     */
    public final void setValue(final Character pvalue) {
        if (pvalue == 'A' || pvalue == 'C' || pvalue == 'T'
          || pvalue == 'U' || pvalue == 'G') {
            this.value = pvalue;
        }
    }

    /**
     * @return valor
     */
    public final Character getValue() {
        return this.value;
    }

    @Override
    public final String toString() {
        return "[" + base + "; " + position + ";" + value + "]";
    }
}
