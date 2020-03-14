/*
 * Copyright (C) 2018 David A. Mancilla
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
package com.eljaguar.mvnlaslo.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.System.out;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.GenbankReaderHelper;
import org.biojava.nbio.core.sequence.io.GenbankWriterHelper;

/**
 *
 * @author David A. Mancilla
 */
public class GenBank extends SourceFile {

    private String description;
    private int cdsStart;
    private int cdsEnd;
    private String location;
    private String synonym;
    private String proxyConf;
    private static String PROXY_FILE = "proxy";
    private final static String E_FETCH = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?";

    private static String HEADER
            = "Gen" + ROW_DELIMITER
            + "GeneSynonym" + ROW_DELIMITER
            + "Note" + ROW_DELIMITER
            + "AccessionID" + ROW_DELIMITER
            + "CDS_Start" + ROW_DELIMITER
            + "CDS_End" + ROW_DELIMITER
            + "Location" + ROW_DELIMITER;

    /**
     * Blank constructor.
     */
    public GenBank() {
        this.description = "";
        this.cdsStart = 0;
        this.cdsEnd = 0;
        this.proxyConf = "";
    }

    /**
     *
     * @param synonym
     * @param description
     * @param cdsStart
     * @param cdsEnd
     */
    public GenBank(String synonym, String description, int cdsStart,
            int cdsEnd) {
        this.description = description;
        this.cdsStart = cdsStart;
        this.cdsEnd = cdsEnd;
        this.synonym = synonym;
        this.proxyConf = "";
    }

    /**
     * Save GenBank downloaded file.
     *
     * @param path
     * @param dnaFile
     * @param name
     * @return Path of the new file
     */
    public static String makeFile(String path,
            LinkedHashMap<String, DNASequence> dnaFile, String name) {
        String absolutePath = path + "\\" + name + ".gb";
        File file = new File(absolutePath);

        if (dnaFile == null) {
            return null;
        }

        try {
            GenbankWriterHelper.writeNucleotideSequence(file,
                    dnaFile.values());
        } catch (Exception ex) {
            out.println("DNAFile size: " + dnaFile.size());
            out.println("ERROR: " + ex.getMessage() + ". "
                    + ex.getLocalizedMessage());
            out.println("*Method: GenBank-makeFile*");
            return null;
        }

        return absolutePath;
    }

    /**
     * Added an additional option to configurate the proxy settings: Must exist
     * an file "proxy" containing the following parameters: * proxy.site,
     * port.number
     *
     * @return
     */
    @SuppressWarnings("NestedAssignment")
    public static String getProxyConfiguration() {

        String proxy = "";

        if (!(new File(PROXY_FILE).exists())) {
            return "";
        }

        try (BufferedReader br = new BufferedReader(
                new FileReader(getPROXY_FILE()))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                proxy = sCurrentLine;
            }

        } catch (IOException e) {
            out.println("ERROR: " + e.getLocalizedMessage());
            out.println("*Method: GenBank-getProxyConfiguration*");
        }

        return proxy;
    }

    /**
     *
     * @return
     */
    public static boolean connectToProxy() {

        try {
            String proxyConn = getProxyConfiguration();

            if (proxyConn.length() > 0) {
                String proxyParm[] = proxyConn.split(",");

                // defined a proxy connection
                System.setProperty("http.proxyHost", proxyParm[0].trim());
                System.setProperty("http.proxyPort", proxyParm[1].trim());

                // If proxy requires authentication, 
                if (proxyParm.length == 4) {
                    System.setProperty("http.proxyUser", proxyParm[2].trim());
                    System.setProperty("http.proxyPassword", proxyParm[3].trim());
                }
            }
        } catch (Exception ex) {
            out.println("ERROR: " + ex.getMessage());
            out.println("*Method: GenBank-connectToProxy*");
            return false;
        }

        return true;
    }

    /**
     *
     * @param host
     * @param port
     * @param user
     * @param password
     * @return
     */
    public static boolean connectToProxy(String host, String port, String user,
            String password) {

        try {
            // defined a proxy connection
            System.setProperty("http.proxyHost", host.trim());
            System.setProperty("http.proxyPort", port.trim());
            System.setProperty("http.proxyUser", user.trim());
            System.setProperty("http.proxyPassword", password.trim());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Download a NCBI sequence by ID
     *
     * @param genBankId
     * @return
     * @throws Exception
     */
    @SuppressWarnings("SleepWhileInLoop")
    public static LinkedHashMap<String, DNASequence>
            downLoadSequenceForId(List<String> genBankId) throws Exception {

        LinkedHashMap<String, DNASequence> dnaFile;
        LinkedHashMap<String, DNASequence> tmp = null;
        String request;
        URL ncbiGenbank;

        dnaFile = new LinkedHashMap<>();

        //connectToProxy();
        for (String transcriptoId : genBankId) {
            try {
                request = String.format("db=nuccore&id=%s&rettype=gb&retmode=text",
                        transcriptoId.trim());

                // Request to NCBI e-fetch
                ncbiGenbank = new URL(E_FETCH + request);
                tmp = GenbankReaderHelper.readGenbankDNASequence(
                        ncbiGenbank.openStream());
                out.println(transcriptoId.trim() + "...");

            } catch (MalformedURLException ex) {
                out.println("ERROR: Malformed URL Exception. Cause: "
                        + ex.getCause().getMessage());
                //return null;
            } catch (IOException ex) {

                if (ex.getLocalizedMessage().contains("400")) {
                    out.println("ERROR: " + transcriptoId + " code not found.");
                } else {
                    out.println("ERROR: IO Exception (" + transcriptoId
                            + "). " + ex.getMessage());
                }
                //return null;
            }

            if (tmp != null && tmp.size() > 0) {
                dnaFile.putAll(tmp);
            }
            Thread.sleep(350);
        }

        return dnaFile;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        /*try {
            LinkedHashMap<String, DNASequence> downLoadSequenceForId;
            downLoadSequenceForId = downLoadSequenceForId("NM_001275794.1,NM_005690");
        } catch (Exception ex) {
            Logger.getLogger(GenBank.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    /**
     *
     * @return
     */
    public String getSynonym() {
        return synonym;
    }

    /**
     *
     * @param synonym
     */
    public void setSynonym(String synonym) {
        this.synonym = synonym.replace(';', ',');
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description.replace(';', ',');
    }

    /**
     *
     * @return
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @param pos
     */
    public void setLocation(int pos) {
        if (this.getCdsEnd() != 0) {
            if (pos < getCdsStart()) {
                setLocation("5'UTR");
            } else if (pos > getCdsEnd()) {
                setLocation("3'UTR");
            } else {
                setLocation("CDS");
            }
        }
    }

    /**
     *
     * @return
     */
    public int getCdsStart() {
        return cdsStart;
    }

    /**
     *
     * @return
     */
    public int getCdsEnd() {
        return cdsEnd;
    }

    /**
     *
     * @param cds
     */
    public void setCDS(String cds) {

        if (cds == null) {
            return;
        }

        String[] parts = cds.split("\\.\\.");

        if (parts.length > 0) {
            this.setCdsStart(new Integer(parts[0]));
            this.setCdsEnd(new Integer(parts[1]));
        }
    }

    /**
     *
     * @return
     */
    public static String getHeader() {
        return GenBank.getHEADER();
    }

    /**
     *
     * @return
     */
    @Override
    public String toRowCSV() {
        return getGeneID().replace(';', ',') + ROW_DELIMITER
                + getSynonym() + ROW_DELIMITER
                + getTranscriptID().replace(';', ',') + ROW_DELIMITER
                + getDescription() + ROW_DELIMITER
                + getCdsStart() + ROW_DELIMITER
                + getCdsEnd() + ROW_DELIMITER
                + getLocation() + ROW_DELIMITER;
    }

    /**
     * @return the proxyConf
     */
    public String getProxyConf() {
        return proxyConf;
    }

    /**
     * @param cdsEnd the cdsEnd to set
     */
    public void setCdsEnd(int cdsEnd) {
        this.cdsEnd = cdsEnd;
    }

    /**
     * @param cdsStart the cdsStart to set
     */
    public void setCdsStart(int cdsStart) {
        this.cdsStart = cdsStart;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @param proxyConf the proxyConf to set
     */
    public void setProxyConf(String proxyConf) {
        this.proxyConf = proxyConf;
    }

    /**
     * @return the HEADER
     */
    public static String getHEADER() {
        return HEADER;
    }

    /**
     * @return the PROXY_FILE
     */
    public static String getPROXY_FILE() {
        return PROXY_FILE;
    }

    /**
     * @param aHEADER the HEADER to set
     */
    public static void setHEADER(String aHEADER) {
        HEADER = aHEADER;
    }

    /**
     * @param aPROXY_FILE the PROXY_FILE to set
     */
    public static void setPROXY_FILE(String aPROXY_FILE) {
        PROXY_FILE = aPROXY_FILE;
    }
}
