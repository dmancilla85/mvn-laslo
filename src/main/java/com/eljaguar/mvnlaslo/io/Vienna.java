/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eljaguar.mvnlaslo.io;

import java.io.File;
import java.io.IOException;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 *
 * @author David
 */
public class Vienna extends SourceFile {

    private String sequence;
    private String brackets;
    private double mfe;

    public Vienna(){
        
    }
    
    public Vienna(File viennaFile) {
        this.sequence = "";
        this.brackets = "";
        this.mfe = 0.0;
        
        readViennaFile(viennaFile);

    }

    @Override
    public String toString() {
        return this.sequence + "\n" + this.brackets + "\n" + this.mfe;
    }

    /**
     * @return the brackets
     */
    public String getBrackets() {
        return brackets;
    }

    /**
     * @return the mfe
     */
    public double getMfe() {
        return mfe;
    }

    /**
     * @return the sequence
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * @param brackets the brackets to set
     */
    public void setBrackets(String brackets) {
        this.brackets = brackets;
    }

    /**
     * @param mfe the mfe to set
     */
    public void setMfe(double mfe) {
        this.mfe = mfe;
    }

    /**
     * @param sequence the sequence to set
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toRowCSV(){
        return "";
    }
    
    private boolean readViennaFile(File file) {

        Object aux[];

        try {
            Stream<String> lines = Files.lines(Paths.get(file.getAbsolutePath()));
            aux = lines.toArray();

            if (aux == null || aux.length < 2) {
                throw new IOException("Invalid file format");
            }

            this.sequence = aux[0].toString();
            this.brackets = aux[1].toString().split("\t")[0];
            this.mfe = Double.parseDouble(aux[1]
              .toString().split("\t")[1]
              .replace("(", "")
              .replace(")", ""));

        } catch (IOException io) {
            out.println("Error: " + io.getMessage());
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        String fName = "C:\\Users\\David\\Desktop\\TEST\\20Mar11-21-05-16_1.b";
        System.out.println();
        Vienna vienna = new Vienna(null);
        // Method #2 - Read file with a filter
        vienna.readViennaFile(new File(fName));

        System.out.println(vienna);
    }

}
