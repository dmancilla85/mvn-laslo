/*
 * Copyright (C) 2019 David
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
package com.eljaguar.mvnlaslo.gui;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JTextArea;

// *****************************************************************************

// STATIC MEMBERS
// *****************************************************************************
/**
 *
 */
class Appender implements Runnable {
    private final JTextArea textArea;
    private final int maxLines; // maximum lines allowed in text area
    private final LinkedList<Integer> lengths; //length of lines within text area
    private final List<String> values; // values waiting to be appended
    private int curLength; // length of current line
    private boolean clear;
    private boolean queue;

    /**
     *
     * @param txtara
     * @param maxlin
     */
    Appender(JTextArea txtara, int maxlin) {
        textArea = txtara;
        maxLines = maxlin;
        lengths = new LinkedList<>();
        values = new ArrayList<>();
        curLength = 0;
        clear = false;
        queue = true;
    }

    /**
     *
     * @param val
     */
    synchronized void append(String val) {
        values.add(val);
        if (queue) {
            queue = false;
            EventQueue.invokeLater(this);
        }
    }

    /**
     *
     */
    synchronized void clear() {
        clear = true;
        curLength = 0;
        lengths.clear();
        values.clear();
        if (queue) {
            queue = false;
            EventQueue.invokeLater(this);
        }
    }

    /**
     * MUST BE THE ONLY METHOD THAT TOUCHES textArea!
     */
    @Override
    public synchronized void run() {
        if (clear) {
            textArea.setText("");
        }
        values.stream().map((val) -> {
            curLength += val.length();
            return val;
        }).map((java.lang.String val) -> {
            if (val.endsWith(EOL1) || val.endsWith(EOL2)) {
                if (lengths.size() >= maxLines) {
                    textArea.replaceRange("", 0, lengths.removeFirst());
                }
                lengths.addLast(curLength);
                curLength = 0;
            }
            return val;
        }).forEachOrdered((val) -> {
            textArea.append(val);
        });
        values.clear();
        clear = false;
        queue = true;
    }
    private static final String EOL1 = "\n";
    private static final String EOL2 = System.getProperty("line.separator", EOL1);
    
}
