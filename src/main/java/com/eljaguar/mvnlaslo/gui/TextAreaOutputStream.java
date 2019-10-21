/*
 * Copyright (C) 2018 David A. Mancilla
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.eljaguar.mvnlaslo.gui;

import java.io.*;
import javax.swing.*;

/**
 *
 * @author unknown
 */
public class TextAreaOutputStream
        extends OutputStream {

// ****************************************************************************
// INSTANCE MEMBERS
// ****************************************************************************
    private byte[] oneByte; // array for write(int val);
    private Appender appender; // most recent action

    /**
     *
     * @param txtara
     */
    public TextAreaOutputStream(JTextArea txtara) {
        this(txtara, 1000);
    }

    /**
     *
     * @param txtara
     * @param maxlin
     */
    public TextAreaOutputStream(JTextArea txtara, int maxlin) {
        if (maxlin < 1) {
            throw new 
        IllegalArgumentException("TextAreaOutputStream maximum lines must be " +
                "positive (value=" + maxlin + ")");
        }
        oneByte = new byte[1];
        appender = new Appender(txtara, maxlin);
    }

    /**
     * Clear the current console text area.
     */
    public synchronized void clear() {
        if (appender != null) {
            appender.clear();
        }
    }

    /**
     *
     */
    @Override
    public synchronized void close() {
        appender = null;
    }

    /**
     *
     */
    @Override
    public synchronized void flush() {
    }

    /**
     *
     * @param val
     */
    @Override
    public synchronized void write(int val) {
        oneByte[0] = (byte) val;
        write(oneByte, 0, 1);
    }

    /**
     *
     * @param ba
     */
    @Override
    public synchronized void write(byte[] ba) {
        write(ba, 0, ba.length);
    }

    /**
     *
     * @param ba
     * @param str
     * @param len
     */
    @Override
    public synchronized void write(byte[] ba, int str, int len) {
        if (appender != null) {
            appender.append(bytesToString(ba, str, len));
        }
    }

    /**
     *
     * @param ba
     * @param str
     * @param len
     * @return
     */
    static private String bytesToString(byte[] ba, int str, int len) {
        try {
            return new String(ba, str, len, "UTF-8");
        } catch (UnsupportedEncodingException thr) {
            return new String(ba, str, len);
        } // all JVMs are required to support UTF-8
    }
} /* END PUBLIC CLASS */
