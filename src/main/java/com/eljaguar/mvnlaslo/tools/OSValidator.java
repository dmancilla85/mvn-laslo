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
package com.eljaguar.mvnlaslo.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.System.out;

/**
 *
 * @author Unknown
 */
public class OSValidator {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        out.println(OS);

        if (isWindows()) {
            out.println("This is Windows");
        } else if (isMac()) {
            out.println("This is Mac");
        } else if (isUnix()) {
            out.println("This is Unix or Linux");
        } else if (isSolaris()) {
            out.println("This is Solaris");
        } else {
            out.println("Your OS is not support!!");
        }

        //out.println("Number of cores: " + getNumberOfCPUCores());
    }

    /**
     *
     * @return
     */
    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    /**
     *
     * @return
     */
    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    /**
     *
     * @return
     */
    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    /**
     *
     * @return
     */
    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }

    /**
     *
     * @return
     */
    public static String getOS() {
        if (isWindows()) {
            return "win";
        } else if (isMac()) {
            return "osx";
        } else if (isUnix()) {
            return "uni";
        } else if (isSolaris()) {
            return "sol";
        }

        return "err";
    }

    /**
     *
     * @return
     */
    @SuppressWarnings({"null", "NestedAssignment"})
    public static int getNumberOfCPUCores() {
        //OSValidator osValidator; 
        //osValidator = new OSValidator();
        String command = "";
        if (OSValidator.isMac()) {
        } else if (OSValidator.isUnix()) {
            command = "lscpu";
        } else if (OSValidator.isWindows()) {
            command = "cmd /C WMIC CPU Get /Format:List";
        } else {
            command = "sysctl -n machdep.cpu.core_count";
        }
        Process process = null;
        int numberOfCores = 0;
        int sockets = 0;
        try {
            if (OSValidator.isMac()) {
                String[] cmd = {"/bin/sh", "-c", command};
                process = Runtime.getRuntime().exec(cmd);
            } else {
                process = Runtime.getRuntime().exec(command);
            }
        } catch (IOException e) {
        }

        BufferedReader reader;
        reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                if (OSValidator.isMac()) {
                    numberOfCores = line.length() > 0
                            ? Integer.parseInt(line) : 0;
                } else if (OSValidator.isUnix()) {
                    if (line.contains("Core(s) per socket:")) {
                        numberOfCores = Integer.parseInt(line.split("\\s+")[line.split("\\s+").length - 1]);
                    }
                    if (line.contains("Socket(s):")) {
                        sockets = Integer.parseInt(line.split("\\s+")[line.split("\\s+").length - 1]);
                    }
                } else if (OSValidator.isWindows()) {
                    if (line.contains("NumberOfCores")) {
                        numberOfCores = Integer.parseInt(line.split("=")[1]);
                    }
                }
            }
        } catch (IOException e) {
        }
        if (OSValidator.isUnix()) {
            return numberOfCores * sockets;
        }
        return numberOfCores;
    }

}
