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

/**
 * @author David
 *
 */
public class SourceFile {

    private static String FIELD_DELIMITER = "|"; //$NON-NLS-1$
    private static String ROW_DELIMITER = ";"; //$NON-NLS-1$
    private static String LOG_EXT = ".log";
    private static String CSV_EXT = ".csv";
    private static String FASTA_EXT = ".fasta";
    private static String FASTA_EXT_2 = ".fa";
    private static String GENBANK_EXT = ".gb";
    protected String transcriptID;
    protected String geneID;
    protected String geneSymbol;
    protected String fieldDelimiter;
    protected String rowDelimiter;

    /**
     *
     */
    public SourceFile() {
        this.fieldDelimiter = FIELD_DELIMITER;
        this.rowDelimiter = ROW_DELIMITER;
        this.transcriptID = null;
        this.geneID = null;
        this.geneSymbol = null;        
    }
    
    /**
     *
     * @param header
     * @return
     */
    public static InputSequence detectHeader(String header) {

        InputSequence out = InputSequence.GENERIC;

        if(header.contains("genbank")){
            return InputSequence.GENBANK;
        }
        
        if(header.contains("@")){
            return InputSequence.GENBANK;
        }
        
        if (header.contains("gene:")
                && header.contains("gene_biotype:")
                && header.contains("transcript_biotype:")
                && header.contains("gene_symbol:")
                && header.contains("description:")) {
            out = InputSequence.ENSEMBL;
        } else {
            if (header.contains("type=")
                    && header.contains("loc=")
                    && header.contains("name=")
                    && header.contains("dbxref=")
                    && header.contains("score=")
                    && header.contains("MD5=")
                    && header.contains("release=")) {
                out = InputSequence.FLYBASE;
            } else {
                if (header.contains("|") || header.contains("=")) {
                    out = InputSequence.BIOMART;
                }
            }
        }

        return out;
    }

    /**
     * @param aGENBANK_EXT the GENBANK_EXT to set
     */
    public static void setGENBANK_EXT(String aGENBANK_EXT) {
        GENBANK_EXT = aGENBANK_EXT;
    }

    /**
     * @param aLOG_EXT the LOG_EXT to set
     */
    public static void setLOG_EXT(String aLOG_EXT) {
        LOG_EXT = aLOG_EXT;
    }

    /**
     * @param aROW_DELIMITER the ROW_DELIMITER to set
     */
    public static void setROW_DELIMITER(String aROW_DELIMITER) {
        ROW_DELIMITER = aROW_DELIMITER;
    }

    /**
     *
     * @return
     */
    public String getTranscriptID() {
        return transcriptID;
    }

    /**
     *
     * @param transcriptID
     */
    public void setTranscriptID(String transcriptID) {
        this.transcriptID = transcriptID;
    }

    /**
     *
     * @return
     */
    public String getGeneID() {
        return geneID;
    }

    /**
     *
     * @param geneID
     */
    public void setGeneID(String geneID) {
        this.geneID = geneID;
    }

    /**
     *
     * @return
     */
    public String getGeneSymbol() {
        return geneSymbol;
    }

    /**
     * @return the CSV_EXT
     */
    public static String getCSV_EXT() {
        return CSV_EXT;
    }

    /**
     * @return the FASTA_EXT
     */
    public static String getFASTA_EXT() {
        return FASTA_EXT;
    }

    /**
     * @return the FASTA_EXT_2
     */
    public static String getFASTA_EXT_2() {
        return FASTA_EXT_2;
    }

    /**
     * @return the FIELD_DELIMITER
     */
    public static String getFIELD_DELIMITER() {
        return FIELD_DELIMITER;
    }

    /**
     * @return the GENBANK_EXT
     */
    public static String getGENBANK_EXT() {
        return GENBANK_EXT;
    }

    /**
     * @return the LOG_EXT
     */
    public static String getLOG_EXT() {
        return LOG_EXT;
    }

    /**
     * @return the ROW_DELIMITER
     */
    public static String getROW_DELIMITER() {
        return ROW_DELIMITER;
    }

    /**
     * @param aCSV_EXT the CSV_EXT to set
     */
    public static void setCSV_EXT(String aCSV_EXT) {
        CSV_EXT = aCSV_EXT;
    }

    /**
     * @param aFASTA_EXT the FASTA_EXT to set
     */
    public static void setFASTA_EXT(String aFASTA_EXT) {
        FASTA_EXT = aFASTA_EXT;
    }

    /**
     * @param aFASTA_EXT_2 the FASTA_EXT_2 to set
     */
    public static void setFASTA_EXT_2(String aFASTA_EXT_2) {
        FASTA_EXT_2 = aFASTA_EXT_2;
    }

    /**
     * @param aFIELD_DELIMITER the FIELD_DELIMITER to set
     */
    public static void setFIELD_DELIMITER(String aFIELD_DELIMITER) {
        FIELD_DELIMITER = aFIELD_DELIMITER;
    }
    
    /**
     *
     * @param geneSymbol
     */
    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    /**
     *
     * @return
     */
    public String getRowDelimiter() {
        return rowDelimiter;
    }

    /**
     *
     * @param rowDelimiter
     */
    public void setRowDelimiter(String rowDelimiter) {
        this.rowDelimiter = rowDelimiter;
    }

    /**
     *
     * @return
     */
    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    /**
     *
     * @param fieldDelimiter
     */
    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    /**
     *
     * @return
     */
    public static String getHeader() {
        return "NO HEADER";
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "geneID = " + getGeneID() + " - "
                + "transcriptID = " + getTranscriptID();
    }

    /**
     *
     * @return
     */
    public String toRowCSV() {
        return getGeneID() + getROW_DELIMITER()
                + getTranscriptID() + getROW_DELIMITER();
    }
}
