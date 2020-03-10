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
public class FlyBaseFastaID extends SourceFile {

    private static String TYPE = "type=";
    private static String LOCATION = "loc="; 
    private static String NAME = "name="; 
    private static String DBXREF = "dbxref="; 
    private static String SCORE = "score="; 
    private static String CHECKSUM = "MD5="; 
    private static String GENE_ID = "parent="; 
    private static String RELEASE = "release="; 
    private static String SPECIES = "species="; 
    private static String HEADER = 
            "GeneID" + getROW_DELIMITER() 
            + "TranscriptID" + getROW_DELIMITER() 
            + "Name" + getROW_DELIMITER() 
            + "Release" + getROW_DELIMITER() 
            + "DBXREF" + getROW_DELIMITER() 
            + "Score" + getROW_DELIMITER()
            + "Checksum" + getROW_DELIMITER() 
            + "Species" + getROW_DELIMITER();

    /**
     * @return the CHECKSUM
     */
    public static String getCHECKSUM() {
        return CHECKSUM;
    }

    /**
     * @return the DBXREF
     */
    public static String getDBXREF() {
        return DBXREF;
    }

    /**
     * @return the GENE_ID
     */
    public static String getGENE_ID() {
        return GENE_ID;
    }

    /**
     * @return the HEADER
     */
    public static String getHEADER() {
        return HEADER;
    }

    /**
     * @return the LOCATION
     */
    public static String getLOCATION() {
        return LOCATION;
    }

    /**
     * @return the NAME
     */
    public static String getNAME() {
        return NAME;
    }

    /**
     * @return the RELEASE
     */
    public static String getRELEASE() {
        return RELEASE;
    }

    /**
     * @return the SCORE
     */
    public static String getSCORE() {
        return SCORE;
    }

    /**
     * @return the SPECIES
     */
    public static String getSPECIES() {
        return SPECIES;
    }

    /**
     * @return the TYPE
     */
    public static String getTYPE() {
        return TYPE;
    }

    /**
     * @param aCHECKSUM the CHECKSUM to set
     */
    public static void setCHECKSUM(String aCHECKSUM) {
        CHECKSUM = aCHECKSUM;
    }

    /**
     * @param aDBXREF the DBXREF to set
     */
    public static void setDBXREF(String aDBXREF) {
        DBXREF = aDBXREF;
    }

    /**
     * @param aGENE_ID the GENE_ID to set
     */
    public static void setGENE_ID(String aGENE_ID) {
        GENE_ID = aGENE_ID;
    }

    /**
     * @param aHEADER the HEADER to set
     */
    public static void setHEADER(String aHEADER) {
        HEADER = aHEADER;
    }

    /**
     * @param aLOCATION the LOCATION to set
     */
    public static void setLOCATION(String aLOCATION) {
        LOCATION = aLOCATION;
    }

    /**
     * @param aNAME the NAME to set
     */
    public static void setNAME(String aNAME) {
        NAME = aNAME;
    }

    /**
     * @param aRELEASE the RELEASE to set
     */
    public static void setRELEASE(String aRELEASE) {
        RELEASE = aRELEASE;
    }

    /**
     * @param aSCORE the SCORE to set
     */
    public static void setSCORE(String aSCORE) {
        SCORE = aSCORE;
    }

    /**
     * @param aSPECIES the SPECIES to set
     */
    public static void setSPECIES(String aSPECIES) {
        SPECIES = aSPECIES;
    }

    /**
     * @param aTYPE the TYPE to set
     */
    public static void setTYPE(String aTYPE) {
        TYPE = aTYPE;
    }

    private String location;
    private String name;
    private String dbxref;
    private String score;
    private String checksum;
    private String release;
    private String species;

    /**
     *
     */
    public FlyBaseFastaID() {
        this.transcriptID = "";
        this.geneID = "";
        this.location = "";     //$NON-NLS-1$
        this.checksum = "";     //$NON-NLS-1$
        this.release = "";      //$NON-NLS-1$
        this.species = "";      //$NON-NLS-1$
        this.name = "";         //$NON-NLS-1$
        this.dbxref = "";       //$NON-NLS-1$
        this.score = "";        //$NON-NLS-1$
    }

    /**
     *
     * @return
     */
    public static String getHeader() {
        return FlyBaseFastaID.getHEADER();
    }

    @Override
    public String toString() {
        return "FlyBaseFastaID [transcriptID=" + getTranscriptID() + ", "
                + "geneID=" + getGeneID() + ", location=" + getLocation()
                + ", name=" + getName() + ", dbxref=" + getDbxref() + ", "
                + "score=" + getScore() + ", checksum=" + getChecksum()
                + ", release=" + getRelease() + ", species=" + getSpecies() + "]";
    }

    /**
     *
     * @param idSequence
     */
    public void setFlyBaseTags(String idSequence) {

        int index, index2;
        String idsequence;

        idsequence = idSequence;

        if (idSequence == null || idSequence.length() <= 0) {
            return;
        }

        String aux = idSequence.substring(0, idSequence.indexOf(' '));

        // get Splice number and transcriptID
        index = aux.indexOf('.');

        if (index > 0) {

            // this.id_ensbl.spliceNumber = aux.substring(index + 1).trim();
            setTranscriptID(aux.substring(0, index - 1).trim());
        } else {
            setTranscriptID(aux.trim());
        }

        index2 = getTranscriptID().indexOf(';');

        if (index2 > 0) {
            aux = getTranscriptID().substring(getTranscriptID().length(), index2);
        }

        // get location
        index = idSequence.indexOf(FlyBaseFastaID.getLOCATION(), 0);

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFastaID.getLOCATION().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFastaID.getLOCATION().length());
            }

            setLocation(aux.trim());
        }

        // get name
        index = idsequence.indexOf(FlyBaseFastaID.getNAME(), 0);

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFastaID.getNAME().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFastaID.getNAME().length());
            }

            setName(aux.trim());
        }

        // get dbxref
        index = idsequence.indexOf(FlyBaseFastaID.getDBXREF(), 0);

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFastaID.getDBXREF().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFastaID.getDBXREF().length());
            }

            setDbxref(aux.trim());
        }

        // get score
        index = idsequence.indexOf(FlyBaseFastaID.getSCORE(), 0);

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFastaID.getSCORE().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFastaID.getSCORE().length());
            }

            setScore(aux.trim());
        }

        // get checksum
        index = idsequence.indexOf(FlyBaseFastaID.getCHECKSUM(), 0);

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFastaID.getCHECKSUM().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFastaID.getCHECKSUM().length());
            }

            setChecksum(aux.trim());
        }

        // get GeneID
        index = idsequence.indexOf(FlyBaseFastaID.getGENE_ID(), 0);

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFastaID.getGENE_ID().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFastaID.getGENE_ID().length());
            }

            setGeneID(aux.trim());
        }

        // get release
        index = idsequence.indexOf(FlyBaseFastaID.getRELEASE(), 0);

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFastaID.getRELEASE().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFastaID.getRELEASE().length());
            }

            setRelease(aux.trim());
        }

        // get specie
        index = idsequence.indexOf(FlyBaseFastaID.getSPECIES(), 0);

        if (index > 0) {
            idsequence = idsequence.substring(index);
            index2 = idsequence.indexOf(';');

            if (index2 > 0) {
                aux = idsequence.substring(FlyBaseFastaID.getSPECIES().length(), index2);
            } else {
                aux = idsequence.substring(FlyBaseFastaID.getSPECIES().length());
            }

            setSpecies(aux.trim());
        }
    }

    @Override
    public String toRowCSV() {
        return getGeneID() + getROW_DELIMITER()
                + getTranscriptID() + getROW_DELIMITER()
                + getName() + getROW_DELIMITER()
                + getRelease() + getROW_DELIMITER()
                + getDbxref() + getROW_DELIMITER()
                + getScore() + getROW_DELIMITER()
                + getChecksum() + getROW_DELIMITER()
                + getSpecies() + getROW_DELIMITER();
    }

    /**
     * @return the checksum
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * @return the dbxref
     */
    public String getDbxref() {
        return dbxref;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the release
     */
    public String getRelease() {
        return release;
    }

    /**
     * @return the score
     */
    public String getScore() {
        return score;
    }

    /**
     * @return the species
     */
    public String getSpecies() {
        return species;
    }

    /**
     * @param checksum the checksum to set
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * @param dbxref the dbxref to set
     */
    public void setDbxref(String dbxref) {
        this.dbxref = dbxref;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param release the release to set
     */
    public void setRelease(String release) {
        this.release = release;
    }

    /**
     * @param score the score to set
     */
    public void setScore(String score) {
        this.score = score;
    }

    /**
     * @param species the species to set
     */
    public void setSpecies(String species) {
        this.species = species;
    }
}
