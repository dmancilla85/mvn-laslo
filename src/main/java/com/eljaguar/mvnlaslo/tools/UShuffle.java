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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.out;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.features.FeatureInterface;
import org.biojava.nbio.core.sequence.features.Qualifier;
import static org.biojava.nbio.core.sequence.io.FastaReaderHelper.readFastaDNASequence;

/**
 *
 * @author David A. Mancilla
 */
public class UShuffle {

    private static final String COMMAND_SHUFFLE = "./ext/ushuffle.exe";
    private static final String RANDOM_PATH = "\\shuffled";

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        String path = "C:\\Users\\David\\Documents\\NetBeans"
                + "Projects\\lasloProject\\LASLO\\ext";
        String ext = ".fasta";
        String fileName = "fruitfly_chen";

        out.println("Iniciando...");

        // Generation of the iterator of id,sequence
        LinkedHashMap<String, DNASequence> fasta = null;
        try {
            fasta = readFastaDNASequence(new File("./ext/" + fileName + ext), false);
        } catch (IOException ex) {
            Logger.getLogger(UShuffle.class.getName()).log(Level.SEVERE, null, ex);
        }
        makeShuffleSequences(path, fileName, fasta, 5, 2, false);

    }

    /**
     *
     * @return
     */
    public static String getRandomDir() {
        return RANDOM_PATH;
    }

    private static String getQualifier(String name, Map qualifier) {
        String value;

        if (qualifier.get(name) != null) {
            value = ((Qualifier) ((List) (qualifier.get(name))).get(0)).getValue();
        } else {
            value = "N/A";
        }

        return value;
    }

    /**
     * Generate shuffled sequences with the k-let value indicated.
     *
     * @param path Path of the file to shuffle
     * @param filename Name of the file to shuffle
     * @param fasta Hashmap of DNASequence's from BioJava
     * @param nRandoms Number of random sequences to generate
     * @param k Value of k-let permutations
     * @param isGenBank It tells if it's a GenBank file
     * @return Last process exit value (an integer)
     */
    @SuppressWarnings("NestedAssignment")
    public static int makeShuffleSequences(String path, String filename,
            Map<String, DNASequence> fasta, int nRandoms, int k,
            boolean isGenBank) {

        char sep = '@';
        int exitVal = 0;
        String aux = "";
        String gene = "";
        String synonym = "";
        String note = "";
        String fileNameWithOutExt;
        fileNameWithOutExt = filename.replaceFirst("[.][^.]+$", "");
        String destiny;
        String sequence;
        String header = "";
        String id = "";
        String cds;
        boolean mkdirs;

        mkdirs = new File(path + RANDOM_PATH).mkdirs();

        if (!mkdirs && !new File(path + RANDOM_PATH).exists()) {
            out.println("<Error: Failed to create new 'shuffled' path.>");
        }

        for (int i = 1; i <= nRandoms; i++) {
            try {

                destiny = path + RANDOM_PATH + '\\' + fileNameWithOutExt
                        + "_rnd" + i + ".fa";
                File file = new File(destiny);

                if (file.exists()) {
                    Files.delete(file.toPath());
                }

                file.createNewFile();

                FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                try (BufferedWriter bw = new BufferedWriter(fw)) {

                    for (Map.Entry<String, DNASequence> entry : fasta.entrySet()) {
                        DNASequence element = entry.getValue();

                        if (!isGenBank) {
                            header = element.getOriginalHeader();
                        } else {
                            Map qualGene = ((FeatureInterface) element.getFeaturesByType("gene")
                                    .toArray()[0]).getQualifiers();
                            FeatureInterface featCds = ((FeatureInterface) element.getFeaturesByType("CDS").toArray()[0]);

                            if (!qualGene.isEmpty()) {

                                gene = getQualifier("gene", qualGene);
                                synonym = getQualifier("gene_synonym", qualGene);
                                note = getQualifier("note", qualGene);
                            }

                            if (featCds != null) {
                                cds = featCds.getSource();
                            } else {
                                cds = "";
                            }

                            id = element.getAccession().getID();
                            header = gene + sep + synonym + sep
                                    + note + sep + id + sep + cds;
                        }

                        sequence = element.getSequenceAsString();

                        if (sequence.length() > 30000) {
                            sequence = sequence.substring(0, 30000);
                        }

                        Random gen = new Random(java.lang.System.currentTimeMillis());

                        Process pr
                                = new ProcessBuilder(COMMAND_SHUFFLE, "-s", sequence,
                                        "-k", Integer.toString(k),
                                        "-seed", String.valueOf(gen.nextInt(99999))).start();

                        try (InputStream in = pr.getInputStream()) {
                            int c;

                            while ((c = in.read()) != -1) {

                                if (c != '\n') {
                                    aux += (char) c;
                                } else {
                                    bw.write(">" + header);
                                    bw.newLine();
                                    bw.write(aux);
                                    bw.newLine();
                                    bw.newLine();
                                    aux = "";
                                }

                            }
                        }

                        exitVal = pr.waitFor();
                        element = null;
                    }

                }
                fw = null;
                file = null;

            } catch (IOException | InterruptedException ex) {
                out.println("Error: " + ex.getMessage());
                out.println("*Method: makeShuffleSequences*");
            }
        }

        return exitVal;
    }
}
