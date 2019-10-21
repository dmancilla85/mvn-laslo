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
package com.eljaguar.mvnlaslo.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author David
 *
 */
public class FASTACorrector {

    protected final static String HEADER_START = ">";
    protected final static String BLANK = " ";
    protected final static String NEW_LINE = System.getProperty("line.separator");
    protected final static String HEADER_DESC = "Sequence ID";

    /**
     * Format bad FASTA files.
     * @param fileName
     * @return 
     */
    @SuppressWarnings("ValueOfIncrementOrDecrementUsed")
    public static boolean formatFile(String fileName) {
        try {
            Path filePath = Paths.get(fileName);
            int count = 1;
            List<String> fileContent;
            fileContent = new ArrayList<>(
                    Files.readAllLines(filePath, StandardCharsets.UTF_8));
            String line;

            for (int i = 0; i < fileContent.size(); i++) {
                line = fileContent.get(i);

                // put a header if the sequence doesnt have
                if (i == 0 && !line.contains(HEADER_START)) {
                    line = HEADER_START + HEADER_DESC + " " + (count++) + NEW_LINE + line;
                } else if (!line.contains(HEADER_START)) {
                    line = line.replaceAll(BLANK, "");
                    line = line.replaceAll(NEW_LINE, "");
                } else {
                    line = NEW_LINE + line;
                }
                fileContent.set(i, line);
            }

            Files.write(filePath, fileContent, StandardCharsets.UTF_8);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FASTACorrector.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
