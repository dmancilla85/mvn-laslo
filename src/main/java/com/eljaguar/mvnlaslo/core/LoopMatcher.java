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
package com.eljaguar.mvnlaslo.core;

import com.eljaguar.mvnlaslo.io.FASTACorrector;
import com.eljaguar.mvnlaslo.io.InputSequence;
import com.eljaguar.mvnlaslo.io.SourceFile;
import static com.eljaguar.mvnlaslo.io.SourceFile.*;
import com.eljaguar.mvnlaslo.io.Vienna;
import com.eljaguar.mvnlaslo.tools.RNAFoldConfiguration;
import com.eljaguar.mvnlaslo.tools.UShuffle;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.round;
import static java.lang.System.err;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JProgressBar;
import org.biojava.nbio.core.sequence.DNASequence;
import static org.biojava.nbio.core.sequence.io.FastaReaderHelper.readFastaDNASequence;
import static org.biojava.nbio.core.sequence.io.GenbankReaderHelper.readGenbankDNASequence;

/**
 * @author David
 *
 */
public class LoopMatcher {

    private String pathOut;
    private String pathIn;
    private ArrayList<String> loopPatterns;
    private InputSequence inputType;
    private int minLength;
    private int maxLength;
    private int maxWooble;
    private ResourceBundle bundle;
    private int maxMismatch;
    private File[] fileList;
    private boolean extendedMode;
    private boolean makeRandoms;
    private int numberOfRandoms;
    private int kLetRandoms;
    private File actualFile;
    private String additionalSequence;
    private boolean searchReverse;
    private int progress;
    private int temperature;
    private boolean avoidLonelyPairs;
    private JProgressBar jpBar;

    /**
     *
     * @param pathOut
     * @param pathIn
     * @param loopPatterns
     * @param additionalSequence
     * @param inputType
     * @param minLength
     * @param maxLength
     * @param maxWooble
     * @param maxMismatch
     * @param locale
     * @param kLetRandoms
     * @param searchReverse
     */
    public LoopMatcher(String pathOut, String pathIn,
      ArrayList<String> loopPatterns, String additionalSequence,
      InputSequence inputType, int minLength, int maxLength,
      int maxWooble, int maxMismatch, Locale locale, int kLetRandoms,
      boolean searchReverse) {
        this.pathOut = pathOut;
        this.loopPatterns = loopPatterns;
        this.inputType = inputType;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.maxWooble = maxWooble;
        this.maxMismatch = maxMismatch;
        this.fileList = null;
        this.extendedMode = false;
        this.makeRandoms = false;
        this.progress = 0;
        this.numberOfRandoms = 0;
        this.additionalSequence = additionalSequence;
        this.kLetRandoms = kLetRandoms;
        this.bundle = ResourceBundle.getBundle("Bundle", locale);
        SequenceAnalizer.setBundle(bundle);
        this.searchReverse = searchReverse;
        this.temperature = RNAFoldConfiguration.DEFAULT_TEMP;
    }

    /**
     * Constructor
     */
    public LoopMatcher() {
        this("", "", new ArrayList<>(), BiologicPatterns.PUM1,
          InputSequence.ENSEMBL, //NOI18N
          4, 16, 2, 0, new Locale("es", "ES"), 2, false);
        SequenceAnalizer.setBundle(bundle);
    }

    /**
     *
     * @return
     */
    public boolean isExtendedMode() {
        return extendedMode;
    }

    public void setProgressBar(JProgressBar bar) {
        this.jpBar = bar;
    }

    /**
     *
     * @param extendedMode
     */
    public void setExtendedMode(boolean extendedMode) {
        this.extendedMode = extendedMode;
    }

    /**
     *
     * @return
     */
    public boolean isSearchReverse() {
        return searchReverse;
    }

    /**
     *
     * @param searchReverse
     */
    public void setSearchReverse(boolean searchReverse) {
        this.searchReverse = searchReverse;

        StemLoop.setHasTwoSenses(searchReverse);
    }

    public int getProgress() {
        return this.progress;
    }

    /**
     *
     * @return
     */
    public int getkLetRandoms() {
        return kLetRandoms;
    }

    /**
     *
     * @param avoidLonelyPairs
     */
    public void setAvoidLonelyPairs(boolean avoidLonelyPairs) {
        this.avoidLonelyPairs = avoidLonelyPairs;
    }

    /**
     *
     * @return
     */
    public boolean getAvoidLonelyPairs() {
        return this.avoidLonelyPairs;
    }

    /**
     *
     * @param kLetRandoms
     */
    public void setkLetRandoms(int kLetRandoms) {
        this.kLetRandoms = kLetRandoms;
    }

    /**
     *
     * @return
     */
    public String getAdditionalSequence() {
        return additionalSequence;
    }

    /**
     *
     * @param additionalSequence
     */
    public void setAdditionalSequence(String additionalSequence) {
        this.additionalSequence = additionalSequence.trim();

        StemLoop.setHasAdditionalSequence(additionalSequence.trim().length() > 0);
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public File[] getFileList() {
        return fileList;
    }

    /**
     *
     * @param mode
     */
    public void setIsExtendedMode(boolean mode) {
        this.setExtendedMode(mode);
    }

    /**
     *
     * @return
     */
    public boolean getIsExtendedMode() {
        return this.isExtendedMode();
    }

    /**
     *
     * @return
     */
    public String getPathOut() {
        return pathOut;
    }

    /**
     *
     * @param pathOut
     */
    public void setPathOut(String pathOut) {
        this.pathOut = pathOut;
    }

    /**
     *
     * @return
     */
    public String getPathIn() {
        return pathIn;
    }

    /**
     *
     * @param pathIn
     */
    public void setPathIn(String pathIn) {

        File aux = new File(pathIn);

        if (aux.isDirectory()) {
            this.pathIn = pathIn;
        } else {
            this.pathIn = aux.getParent();

        }
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public ArrayList<String> getLoopPatterns() {
        return loopPatterns;
    }

    /**
     *
     * @param loopPatterns
     */
    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setLoopPatterns(ArrayList<String> loopPatterns) {
        int maxWord = 0;

        this.loopPatterns = loopPatterns;

        for (int i = 0; i < loopPatterns.size(); i++) {
            if (loopPatterns.get(i).trim().length() > maxWord) {
                maxWord = loopPatterns.get(i).trim().length();
            }
        }

        StemLoop.setMaxPatternLength(maxWord);
    }

    /**
     *
     * @param bundle
     */
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    /**
     *
     * @return
     */
    public InputSequence getInputType() {
        return inputType;
    }

    /**
     *
     * @param inputType
     */
    public void setInputType(InputSequence inputType) {
        this.inputType = inputType;
    }

    /**
     *
     * @return
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     *
     * @param minLength
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    /**
     *
     * @return
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     *
     * @param maxLength
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     *
     * @return
     */
    public int getMaxWooble() {
        return maxWooble;
    }

    /**
     *
     * @param maxWooble
     */
    public void setMaxWooble(int maxWooble) {
        this.maxWooble = maxWooble;
    }

    /**
     *
     * @return
     */
    public int getMaxMismatch() {
        return maxMismatch;
    }

    /**
     *
     * @param maxMismatch
     */
    public void setMaxMismatch(int maxMismatch) {
        this.maxMismatch = maxMismatch;
    }

    /**
     *
     * @param sFileName d
     * @param stemResearch ed
     * @param headerList d
     */
    public final void writeCSV(final String sFileName,
      final List<StemLoop> stemResearch, final String headerList) {

        CSVWriter writer;

        if (stemResearch.isEmpty() && headerList == null) {
            return;
        }

        try {
            writer = new CSVWriter(new FileWriter(sFileName), ';',
              CSVWriter.DEFAULT_QUOTE_CHARACTER,
              CSVWriter.DEFAULT_ESCAPE_CHARACTER,
              CSVWriter.DEFAULT_LINE_END);

            if (headerList != null) {
                String[] header;
                header = headerList.split(";"); //NOI18N
                writer.writeNext(header);
            }

            for (StemLoop stemResearch1 : stemResearch) {
                String[] data;
                data = stemResearch1.toRowCSV().split(";");
                writer.writeNext(data);
            }

            writer.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            out.println(java.text.MessageFormat.format(
              getBundle()
                .getString("ERROR_EX"), new Object[]{e.getMessage()}));
            out.println("*Method: writeCSV*");
        }
    }

    /**
     * Union two lists of files
     *
     * @param a
     * @param b
     * @return
     */
    public File[] unionFiles(File[] a, File[] b) {
        Set<File> set;
        set = new HashSet<>(Arrays.asList(a));
        set.addAll(Arrays.asList(b));
        return set.toArray(new File[set.size()]);
    }

    /**
     *
     * @return
     */
    public boolean startReadingFiles() {
        // To check the elapsed time
        Calendar ini, fin;
        boolean isGenBank = false;
        ini = Calendar.getInstance();
        out.println(java.text.MessageFormat.format(getBundle()
          .getString("START_TIME"),
          new Object[]{Calendar.getInstance().getTime()}));
        out.flush();

        if (getFileList() == null) {
            return false;
        }

        if (this.isMakeRandoms()) {
            out.print(getBundle().getString("MAKING_RANDOM_SEQUENCES"));
            for (File currentFile : getFileList()) {
                if ((currentFile.toString().endsWith(GENBANK_EXT)
                  || currentFile.toString().endsWith(FASTA_EXT)
                  || currentFile.toString().endsWith(FASTA_EXT_2))
                  && currentFile.isFile()) {

                    //fileName = currentFile.getName();
                    LinkedHashMap<String, DNASequence> dnaFile = null;

                    try {
                        if (currentFile.toString().endsWith(GENBANK_EXT)) {
                            dnaFile
                              = readGenbankDNASequence(currentFile, false);
                            isGenBank = true;
                        } else {
                            dnaFile = readFastaDNASequence(currentFile, false);
                        }

                    } catch (IOException ex) {
                        out.println(java.text.MessageFormat.format(
                          getBundle().getString("ERROR_EX"),
                          new Object[]{ex.getMessage()}));
                        out.println("*Method: startReadingFiles*");
                    } catch (Exception ex) {
                        out.println(java.text.MessageFormat.format(
                          getBundle().getString("ERROR_EX"),
                          new Object[]{ex.getMessage()}));
                        out.println("*Method: startReadingFiles*");
                    }
                    UShuffle.makeShuffleSequences(getPathOut(),
                      currentFile.getName(), dnaFile, getNumberOfRandoms(),
                      getkLetRandoms(), isGenBank);
                }
            }
            out.print(getBundle().getString("DONE"));

            File folder;
            String path1 = getPathIn();

            if (path1 == null) {
                // Is a NCBI file
                path1 = getPathOut();
            }

            folder = new File(path1 + UShuffle.getRandomDir());
            File[] randomFiles;
            randomFiles = folder.listFiles();
            setFileList(unionFiles(getFileList(), randomFiles));
        }

        // I. File level (hacer un hilo)
        for (File currentFile : getFileList()) {
            if (currentFile.isFile()
              && (currentFile.toString().endsWith(GENBANK_EXT)
              || currentFile.toString().endsWith(FASTA_EXT)
              || currentFile.toString().endsWith(FASTA_EXT_2)
              || currentFile.toString().endsWith(VIENNA_EXT))) {

                this.setActualFile(currentFile);
                callProcessThreads();
            }
        }

        fin = Calendar.getInstance();
        out.print(java.text.MessageFormat.format(getBundle()
          .getString("TOTAL_TIME"),
          new Object[]{((fin.getTimeInMillis()
              - ini.getTimeInMillis()) / 1000)}));

        out.flush();

        // Memory cleaning
        setFileList(null);
        return true;
    }

    /**
     * Process the files selected.
     */
    @SuppressWarnings({"ValueOfIncrementOrDecrementUsed", "empty-statement"})
    public final void callProcessThreads() {

        CSVWriter writer;
        Vienna vienna = null;
        boolean isGenBank = false;
        boolean isVienna = false;
        boolean isFasta = false;
        boolean formatFile;
        String fileName, fileOut;
        final int MAX_HILOS = 15;
        int totalSecuencias;
        int nHilos = MAX_HILOS;
        int i;
        int count;

        Iterator<String> patternItr = getLoopPatterns().iterator();
        ExecutorService pool;
        CountDownLatch latch;
        Calendar ini, fin;
        String mode = "local";

        if (this.extendedMode) {
            mode = "global";
        }

        try {
            fileName = getActualFile().getName();
            out.print(java.text.MessageFormat.format(getBundle()
              .getString("FILE_PRINT"), new Object[]{fileName}));
            out.flush();
            fileName = fileName.replaceFirst("[.][^.]+$", "");
            fileOut = this.getPathOut() + "\\" + fileName + "."
              + mode + "." + this.minLength + "." + this.maxLength
              + CSV_EXT;

            if (new File(fileOut).exists()) {
                try {
                    (new File(fileOut)).delete();

                } catch (Exception io) {
                    out.println(io.getMessage());
                    err.flush();
                    return;
                }
            }

            // Generation of the iterator of {id,sequence}
            LinkedHashMap<String, DNASequence> fasta;
            fasta = new LinkedHashMap<>();

            isGenBank = getActualFile().getName().endsWith(GENBANK_EXT);
            isVienna = getActualFile().getName().endsWith(VIENNA_EXT);
            isFasta = getActualFile().getName().endsWith(FASTA_EXT)
              || getActualFile().getName().endsWith(FASTA_EXT_2);

            if (isGenBank) {
                fasta = readGenbankDNASequence(getActualFile());
            }
            if (isVienna) {
                vienna = new Vienna(getActualFile());
            }
            if (isFasta) {
                fasta = readFastaDNASequence(getActualFile(), false);
            }

            if (fasta.isEmpty() && !isVienna) {
                out.println(getBundle().getString("INVALID_FILE_FORMAT"));
                out.println(getBundle().getString("TRYING_TO_FIX"));
                formatFile = FASTACorrector.formatFile(getActualFile()
                  .getAbsolutePath());
                if (formatFile) {
                    fasta = readFastaDNASequence(getActualFile(), false);
                } else {
                    out.println(getBundle().getString("CANT_PROCESS"));
                    return;
                }
            }

            if (isFasta) {
                this.setInputType(SourceFile.detectHeader(
                  fasta.entrySet().iterator().next().getValue()
                    .getOriginalHeader()));
            }
            if (isGenBank) {
                this.setInputType(InputSequence.GENBANK);
            }

            if (isVienna) {
                this.setInputType(InputSequence.VIENNA);
            }

            totalSecuencias = isVienna ? 1 : fasta.entrySet().size();

            writer = new CSVWriter(new FileWriter(fileOut), ';',
              CSVWriter.DEFAULT_QUOTE_CHARACTER,
              CSVWriter.DEFAULT_ESCAPE_CHARACTER,
              CSVWriter.DEFAULT_LINE_END);
            writer.writeNext(StemLoop.getHeader(this.getInputType())
              .split(";"));

            // II. Transcript level
            if (totalSecuencias < nHilos) {
                nHilos = totalSecuencias;
            }

            pool = Executors.newFixedThreadPool(nHilos);
            latch = new CountDownLatch(nHilos);
            i = 0;

            ini = Calendar.getInstance();
            this.progress = 0;
            count = 1;

            // a. FASTA and GenBank way
            for (Map.Entry<String, DNASequence> entry : fasta.entrySet()) {

                DNASequence element = entry.getValue();
                LoopMatcherThread thread = new LoopMatcherThread(
                  isExtendedMode(), getAdditionalSequence(),
                  getMaxLength(), getMinLength(), element,
                  getInputType(), patternItr, writer, isSearchReverse(),
                  bundle, temperature, avoidLonelyPairs);

                this.progress = (int) round(count / 
                  (double) totalSecuencias * 100);
                jpBar.setValue(progress);

                if (i++ < nHilos) {
                    //out.println("Seq: " + count + " - Thread latch #" + i);
                    thread.setLatch(latch);
                    pool.execute(thread);
                } else {
                    i = 1;
                    latch.await();
                    pool.shutdown();

                    if (totalSecuencias - count < nHilos) {
                        nHilos = totalSecuencias - count + 1;
                    }

                    // wait for pool ending
                    while (!pool.isTerminated());

                    if (nHilos > 0) {
                        pool = Executors.newFixedThreadPool(nHilos);
                        latch = new CountDownLatch(nHilos);
                        thread.setLatch(latch);
                        pool.execute(thread);
                    }
                }
                count++;
            }

            // b. Vienna way
            if (isVienna) {

                LoopMatcherThread thread = new LoopMatcherThread(vienna,
                  getMaxLength(), getMinLength(), getAdditionalSequence(),
                  patternItr, writer, bundle);

                this.progress = (int) round(count / 
                  (double) totalSecuencias * 100);
                jpBar.setValue(progress);

                thread.setLatch(latch);
                pool.execute(thread);
            }

            if (latch.getCount() > 0) {
                latch.await();
                pool.shutdown();
            }

            while (!pool.isTerminated());

            writer.close();
            fasta.clear();

            fin = Calendar.getInstance();

            out.printf(getBundle().getString("SUMMARY"),
              totalSecuencias,
              (fin.getTimeInMillis() - ini.getTimeInMillis()) / (double) 1000);

        } catch (FileNotFoundException ex) {
            out.println(getBundle().getString("CANT_OPEN_FILE"));
            out.println(java.text.MessageFormat.format(
              getBundle()
                .getString("ERROR_EX"), new Object[]{ex.getMessage()}));
            out.println("*Method: callProcessThreads*");
        } catch (Exception ex) {
            out.println(java.text.MessageFormat.format(
              getBundle()
                .getString("ERROR_EX"), new Object[]{ex.getMessage()}));
            out.println("*Method: callProcessThreads*");
        }
    }

    /**
     *
     * @return
     */
    public boolean isMakeRandoms() {
        return makeRandoms;
    }

    /**
     *
     * @param makeRandoms
     */
    public void setMakeRandoms(boolean makeRandoms) {
        this.makeRandoms = makeRandoms;
    }

    /**
     *
     * @return
     */
    public int getNumberOfRandoms() {
        return numberOfRandoms;
    }

    /**
     *
     * @param numberOfRandoms
     */
    public void setNumberOfRandoms(int numberOfRandoms) {
        this.numberOfRandoms = numberOfRandoms;
    }

    /**
     * @return the actualFile
     */
    public File getActualFile() {
        return actualFile;
    }

    /**
     * @return the bundle
     */
    public ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * @param actualFile the actualFile to set
     */
    public void setActualFile(File actualFile) {
        this.actualFile = actualFile;
    }

    /**
     * @param fileList the fileList to set
     */
    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setFileList(File[] fileList) {
        this.fileList = fileList;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
