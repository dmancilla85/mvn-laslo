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

import static com.eljaguar.mvnlaslo.core.SequenceAnalizer.reverseIt;
import com.eljaguar.mvnlaslo.io.BioMartFasta;
import com.eljaguar.mvnlaslo.io.EnsemblFasta;
import com.eljaguar.mvnlaslo.io.FlyBaseFasta;
import com.eljaguar.mvnlaslo.io.GenBank;
import com.eljaguar.mvnlaslo.io.Generic;
import com.eljaguar.mvnlaslo.io.InputSequence;
import com.eljaguar.mvnlaslo.io.SourceFile;
import com.eljaguar.mvnlaslo.io.Vienna;
import static java.lang.System.out;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

/**
 * Main class for stem-loops
 *
 * @author David A. Mancilla
 * @version 1.0
 * @since 2016-12-13
 */
public class StemLoop {

  private SourceFile id_fasta;
  private InputSequence mode;
  private String rnaHairpinSequence;
  private String loop;
  private String hairpinStructure;
  private String additional5Seq;
  private String additional3Seq;
  private String loopPattern;
  private String viennaStructure;
  private int sequenceLength;
  private int startsAt;
  private int endsAt;
  private int bulge;
  private int internalLoops;
  private char predecessor2Loop;
  private char predecessorLoop;
  private double percent_AG;
  private double percent_GU;
  private double percent_CG;
  private double percent_AU;
  private double percA_sequence;
  private double percG_sequence;
  private double percC_sequence;
  private double percU_sequence;
  private double relativePos;
  private boolean reversed;
  private double mfe;
  private List<Integer> additionalSeqLocations;
  private final List<BaseVariable> patternVariables;
  private static int maxPatternLength;
  private static boolean hasAdditionalSequence;
  private static boolean hasTwoSenses;

  /**
   *
   * @return
   */
  public String getAdditional5Seq() {
    return additional5Seq;
  }

  /**
   *
   * @param additional5Seq
   */
  public void setAdditional5Seq(String additional5Seq) {
    this.additional5Seq = additional5Seq;
  }

  /**
   *
   * @return
   */
  public String getAdditional3Seq() {
    return additional3Seq;
  }

  /**
   *
   * @param additional3Seq
   */
  public void setAdditional3Seq(String additional3Seq) {
    this.additional3Seq = additional3Seq;
  }

  /**
   *
   * @param number
   * @return
   */
  private String getFormattedNumber(Double number, int digits) {
    NumberFormat numberFormatter
      = NumberFormat.getNumberInstance(Locale.getDefault());
    numberFormatter.setMinimumFractionDigits(digits);
    return numberFormatter.format(number);
  }

  private String getFormattedNumber(Double number) {
    return getFormattedNumber(number, 3);
  }

  /**
   *
   * @param mode
   */
  public StemLoop(InputSequence mode) {
    super();
    this.mode = mode;

    switch (mode) {
      case ENSEMBL:
        this.id_fasta = new EnsemblFasta();
        break;

      case FLYBASE:
        this.id_fasta = new FlyBaseFasta();
        break;

      case BIOMART:
        this.id_fasta = new BioMartFasta();
        break;

      case GENERIC:
        this.id_fasta = new Generic();
        break;

      case GENBANK:
        this.id_fasta = new GenBank();
        break;

      case VIENNA:
        this.id_fasta = new Vienna();
        break;

    }

    //this.stemLoopId = 0;
    this.loop = ""; //$NON-NLS-1$
    this.rnaHairpinSequence = null;
    this.sequenceLength = 0;
    this.percent_AG = 0;
    this.percent_GU = 0;
    this.percent_CG = 0;
    this.percent_AU = 0;
    this.percA_sequence = 0;
    this.percC_sequence = 0;
    this.percG_sequence = 0;
    this.percU_sequence = 0;
    this.loopPattern = "";
    this.endsAt = 0;
    this.startsAt = 0;
    this.bulge = 0;
    this.reversed = false;
    this.internalLoops = 0;
    this.relativePos = 0.0;
    this.predecessorLoop = 0;
    this.predecessor2Loop = 0;
    this.mfe = (float) 0.0;
    this.viennaStructure = "";
    this.additional5Seq = "";
    this.additional3Seq = "";
    this.additionalSeqLocations = new ArrayList<>();
    this.patternVariables = new ArrayList<>();
  }

  /**
   *
   * @return
   */
  public int getMismatches() {
    return getBulge();
  }

  /**
   *
   * @param invert
   */
  public void setReverse(boolean invert) {
    this.setReversed(invert);
  }

  /**
   *
   * @return
   */
  public boolean getReversed() {
    return isReversed();
  }

  /**
   *
   * @param mismatches
   */
  public void setMismatches(int mismatches) {
    this.setBulge(mismatches);
  }

  /**
   *
   * @param pattern
   */
  public void setLoopPattern(String pattern) {
    this.loopPattern = pattern;
    char aux;

    this.patternVariables.clear();

    for (int i = 0; i < pattern.length(); i++) {
      aux = pattern.charAt(i);
      if (aux == 'N' || aux == 'K' || aux == 'M' || aux == 'W'
        || aux == 'B' || aux == 'D' || aux == 'R' || aux == 'H'
        || aux == 'S' || aux == 'Y' || aux == 'V') {
        this.patternVariables.add(new BaseVariable('N', i));
      }
    }
  }

  /**
   *
   * @return
   */
  public double getRelativePos() {
    return this.relativePos;
  }

  /**
   *
   * @param relativePos
   */
  public void setRelativePos(double relativePos) {
    this.relativePos = relativePos;
  }

  /**
   *
   * @param mode
   * @return
   */
  public static String getHeader(InputSequence mode) {

    String header = "";
    String nVariablesTable = "";
    String additionalSequenceCol = "";
    String senseColumn = "";

    switch (mode) {
      case ENSEMBL:
        header = EnsemblFasta.getHeader();
        break;

      case FLYBASE:
        header = FlyBaseFasta.getHeader();
        break;

      case BIOMART:
        header = BioMartFasta.getHeader();
        break;

      case GENBANK:
        header = GenBank.getHeader();
        break;

      case GENERIC:
        header = Generic.getHeader();
        break;

      case VIENNA:
        header = "";
    }

    for (int i = 0; i < getMaxPatternLength(); i++) {
      nVariablesTable += "N" + i + SourceFile.ROW_DELIMITER;
    }

    if (isHasAdditionalSequence()) {
      additionalSequenceCol = "AdditionalSeqMatches" + SourceFile.ROW_DELIMITER
        + "AdditionalSeqPositions" + SourceFile.ROW_DELIMITER;
    }

    if (isHasTwoSenses()) {
      senseColumn = "Sense" + SourceFile.ROW_DELIMITER;
    }

    //StemLoop.COLUMN_VARS.sort(null);
    return header
      + "LoopPattern" + SourceFile.ROW_DELIMITER
      + "TerminalPair" + SourceFile.ROW_DELIMITER
      + "N-2" + SourceFile.ROW_DELIMITER
      + "N-1" + SourceFile.ROW_DELIMITER
      + "Loop" + SourceFile.ROW_DELIMITER
      + "StemLoopSequence" + SourceFile.ROW_DELIMITER
      + "Additional5Seq" + SourceFile.ROW_DELIMITER
      + "Additional3Seq" + SourceFile.ROW_DELIMITER
      + "PredictedStructure" + SourceFile.ROW_DELIMITER
      + "ViennaBracketStr" + SourceFile.ROW_DELIMITER
      + "Pairments" + SourceFile.ROW_DELIMITER
      + "WooblePairs" + SourceFile.ROW_DELIMITER
      + "Bulges" + SourceFile.ROW_DELIMITER
      + "InternalLoops" + SourceFile.ROW_DELIMITER
      + "SequenceLength" + SourceFile.ROW_DELIMITER
      + "StartsAt" + SourceFile.ROW_DELIMITER
      + "EndsAt" + SourceFile.ROW_DELIMITER
      + "A_PercentSequence" + SourceFile.ROW_DELIMITER
      + "C_PercentSequence" + SourceFile.ROW_DELIMITER
      + "G_PercentSequence" + SourceFile.ROW_DELIMITER
      + "U_PercentSequence" + SourceFile.ROW_DELIMITER
      + "AU_PercentPairs" + SourceFile.ROW_DELIMITER
      + "CG_PercentPairs" + SourceFile.ROW_DELIMITER
      + "GU_PercentPairs" + SourceFile.ROW_DELIMITER
      + "PurinePercentPairs" + SourceFile.ROW_DELIMITER
      + "RnaFoldMFE" + SourceFile.ROW_DELIMITER
      + "RelativePosition" + SourceFile.ROW_DELIMITER
      + senseColumn
      + nVariablesTable
      + additionalSequenceCol
      + "fornaVisualization" + SourceFile.ROW_DELIMITER;
  }

  /**
   *
   * @return
   */
  public int getEndsAt() {
    return endsAt;
  }

  public String isReverse() {
    if (!this.isReversed()) {
      return "+";
    } else {
      return "-";
    }
  }

  /**
   *
   * @return
   */
  public String getGeneID() {
    return this.getId_fasta().getGeneID();
  }

  /**
   *
   * @return
   */
  public String getGeneSymbol() {
    if (this.getId_fasta().getGeneSymbol() != null) {
      return this.getId_fasta().getGeneSymbol();
    } else {
      return "";
    }
  }

  /**
   *
   * @return
   */
  public String getGUPairs() {
    long pairs = Math.round(this.getPercent_GU() * this.getPairments());

    return pairs + "";
  }

  /**
   *
   * @return
   */
  public String getHairpinStructure() {
    return hairpinStructure; //drawHairpinStructure();
  }

  /**
   *
   * @return
   */
  public String getLoop() {

    String theLoop = this.loop;

    if (isReversed()) {
      theLoop = reverseIt(theLoop);
    }

    return theLoop;
  }

  /**
   *
   * @return
   */
  public String getLoopID() {
    String theLoop, terminal;

    theLoop = this.getLoop();
    terminal = this.getTerminalPair();

    if (isReversed()) {
      theLoop = reverseIt(theLoop);
      terminal = reverseIt(terminal);
    }

    return this.getPredecessorLoop() + theLoop
      + "(" + terminal + ")|" + this.getPairments();
  }

  /**
   *
   * @return
   */
  public String getLoopPattern() {

    return this.loopPattern;
  }

  /**
   *
   * @return
   */
  public InputSequence getMode() {
    return mode;
  }

  /**
   *
   * @return
   */
  public double getPercA_sequence() {
    return percA_sequence;
  }

  /**
   *
   * @return
   */
  public double getPercC_sequence() {
    return percC_sequence;
  }

  /**
   *
   * @return
   */
  public double getPercent_AG() {
    return percent_AG;
  }

  /**
   *
   * @return
   */
  public double getPercent_AU() {
    return percent_AU;
  }

  /**
   *
   * @return
   */
  public double getPercent_CG() {
    return percent_CG;
  }

  /**
   *
   * @return
   */
  public double getPercent_GU() {
    return percent_GU;
  }

  /**
   *
   * @return
   */
  public double getPercG_sequence() {
    return percG_sequence;
  }

  /**
   *
   * @return
   */
  public double getPercU_sequence() {
    return percU_sequence;
  }

  /**
   *
   * @return
   */
  public char getPredecessorLoop() {
    return predecessorLoop;
  }

  /**
   *
   * @return
   */
  public String getAdditionalSequenceCount() {

    Integer count = 0;

    if (this.getAdditionalSeqLocations() != null) {
      count = this.getAdditionalSeqLocations().size();
    }

    return count.toString();
  }

  /**
   *
   * @return
   */
  public String getAdditionalSequenceLocations() {

    String locations = ""; //$NON-NLS-1$

    if (this.getAdditionalSeqLocations() == null) {
      return "";
    }

    Iterator<Integer> itr = this.getAdditionalSeqLocations().iterator();

    while (itr.hasNext()) {

      Integer element = itr.next();

      if (itr.hasNext()) {
        locations = locations.concat(element + ","); //$NON-NLS-1$
      } else {
        locations = locations.concat(Integer.toString(element));
      }
    }

    return locations;
  }

  /**
   *
   * @return
   */
  public String getRnaHairpinSequence() {
    return rnaHairpinSequence;
  }

  /**
   *
   * @return
   */
  public int getSequenceLength() {
    return sequenceLength;
  }

  /**
   *
   * @return
   */
  public int getStartsAt() {
    return startsAt;
  }

  /**
   *
   * @return
   */
  public int getPairments() {
    int count = StringUtils.countMatches(getViennaStructure(), "(");
    return count;
  }

  /**
   *
   * @return
   */
  public String getTerminalPair() {
    Character a = getRnaHairpinSequence().charAt(getViennaStructure().lastIndexOf("("));
    Character b = getRnaHairpinSequence().charAt(getViennaStructure().indexOf(")"));

    return a.toString() + b.toString();
  }

  /**
   *
   * @return
   */
  public String getTranscriptID() {
    return this.getId_fasta().getTranscriptID();
  }

  /**
   *
   * @param endsAt
   */
  public void setEndsAt(int endsAt) {
    this.endsAt = endsAt;
  }

  /**
   *
   * @param loop
   */
  public void setLoop(String loop) {
    char aux;
    this.loop = loop;

    for (int i = 0; i < loop.length(); i++) {
      for (BaseVariable baseV : patternVariables) {
        if (baseV.getPosition() == i) {
          baseV.setValue(loop.charAt(i));
          //out.println(baseV); //test
        }
      }
    }

  }

  public void setLocation(int pos) {
    ((GenBank) getId_fasta()).setLocation(pos);
  }

  /**
   *
   */
  public void checkPairments() {

    String seq = this.getRnaHairpinSequence();
    int woobleCount = 0;
    int CG = 0, AU = 0;
    StringBuilder aux = new StringBuilder(this.getViennaStructure());
    int firstIzq;

    if (this.getLoop().isEmpty() || this.getViennaStructure().isEmpty()) {
      return;
    }

    // Count internal loops and internalLoops
    /*mismatch = StringUtils.countMatches(this.viennaStructure, "(.(")
                + StringUtils.countMatches(this.viennaStructure, ").)");

        bulge = StringUtils.countMatches(this.viennaStructure, "..(")
                + StringUtils.countMatches(this.viennaStructure, ")..");*/
    try {
      firstIzq = this.getViennaStructure().lastIndexOf('(');
      int firstDer = this.getViennaStructure().indexOf(')');

      for (int i = firstIzq; i >= 0 && firstDer
        < getViennaStructure().length(); i--) {
        if (getViennaStructure().charAt(i) == '(') {
          while (getViennaStructure().charAt(firstDer) != ')') {
            firstDer++;
          }

          if (getViennaStructure().charAt(firstDer) == ')') {
            if (/*SequenceAnalizer.isComplementaryRNAWooble(
                          seq.charAt(i), seq.charAt(firstDer))*/
              (seq.charAt(i) == 'U' && seq.charAt(firstDer) == 'G')
              || (seq.charAt(i) == 'G'
              && seq.charAt(firstDer) == 'U')) {
              aux.setCharAt(i, '{');
              aux.setCharAt(firstDer, '}');
              woobleCount++;
            } else {
              if ((seq.charAt(i) == 'U'
                && seq.charAt(firstDer) == 'A')
                || (seq.charAt(i) == 'A'
                && seq.charAt(firstDer) == 'U')) {
                AU++;
              }

              if ((seq.charAt(i) == 'C' && seq.charAt(firstDer) == 'G')
                || (seq.charAt(i) == 'G'
                && seq.charAt(firstDer) == 'C')) {
                CG++;
              }
            }
          }

          firstDer++;
        }
      }

    } catch (Exception e) {
      out.println("checkPairments-ERROR: " + e.getMessage());
    }
    this.setHairpinStructure(aux.toString());
    this.setPercent_AU(AU / (double) (AU + CG + woobleCount));
    this.setPercent_CG(CG / (double) (AU + CG + woobleCount));
    this.setPercent_GU(woobleCount / (double) (AU + CG + woobleCount));
  }

  /**
   *
   */
  public final void checkInternalLoops() {
    this.setInternalLoops(0);
    this.setBulge(0);
    String auxStruct = this.getViennaStructure();
    int len, aux, auxI;
    auxStruct = auxStruct.replaceAll("\\.", "a");
    auxStruct = auxStruct.replaceAll("([a-z])\\1+", "$1");

    len = auxStruct.length();
    aux = auxStruct.indexOf(")");
    auxI = auxStruct.lastIndexOf("(");

    for (int i = 0; (auxI - i) >= 0 && (aux + i) < len; i++) {

      if (auxStruct.charAt(auxI - i) == 'a'
        && auxStruct.charAt(aux + i) == 'a') {
        this.setInternalLoops(this.getInternalLoops() + 1);
      } else
        if ((auxStruct.charAt(auxI - i) == 'a'
          && auxStruct.charAt(aux + i) != 'a')
          || (auxStruct.charAt(auxI - i) != 'a'
          && auxStruct.charAt(aux + i) == 'a')) {
          this.setBulge(this.getBulge() + 1);
        }
    }
  }

  /**
   *
   * @param startPosLoop
   */
  public final void setNLoop(int startPosLoop) {

    char precedes = ' ',
      precedes2 = ' ';
    int matchFirst,
      matchLast,
      startPos;

    startPos = startPosLoop;

    if (isReversed()) {
      this.setRnaHairpinSequence(reverseIt(this.getRnaHairpinSequence()));

      matchFirst = getRnaHairpinSequence().indexOf(reverseIt(getLoop()));
      matchLast = getRnaHairpinSequence().lastIndexOf(reverseIt(getLoop()));

      if (matchFirst == matchLast) {
        startPos = matchFirst;
      } else {
        startPos = matchLast;
      }
    }

    try {
      if (this.getRnaHairpinSequence() != null) {
        precedes = this.getRnaHairpinSequence().charAt(startPos - 1);
        precedes2 = this.getRnaHairpinSequence().charAt(startPos - 2);
      }

      this.setPredecessorLoop(precedes);
      this.setPredecessor2Loop(precedes2);

      if (isReversed()) {
        this.setRnaHairpinSequence(reverseIt(this.getRnaHairpinSequence()));
      }
    } catch (Exception ex) {
      out.println("\nERROR: " + ex.getMessage());
      out.println(this.getId_fasta().toRowCSV());
      out.println(this);
    }
  }

  /**
   *
   * @param percA_sequence
   */
  public final void setPercA_sequence(float percA_sequence) {
    this.percA_sequence = percA_sequence;
  }

  /**
   *
   * @param percC_sequence
   */
  public final void setPercC_sequence(float percC_sequence) {
    this.percC_sequence = percC_sequence;
  }

  /**
   *
   */
  public void setPercent_AG() {

    float myPercent_AG = 0;
    int count;
    String aux;
    aux = getRnaHairpinSequence();

    if (aux != null) {
      count = aux.length() - aux.replace("A", "").length();
      count += aux.length() - aux.replace("G", "").length();

      myPercent_AG = count / (float) aux.length();
    }

    this.setPercent_AG(myPercent_AG);
  }

  /**
   *
   * @return
   */
  public double getMfe() {
    return mfe;
  }

  /**
   *
   * @param mfe
   */
  public void setMfe(Double mfe) {
    this.setMfe((double) mfe);
  }

  /**
   *
   * @return
   */
  public String getStructure() {
    return getViennaStructure();
  }

  /**
   *
   * @param structure
   */
  public void setStructure(String structure) {
    this.setViennaStructure(structure);
  }

  /**
   *
   */
  public void setPercent_AU() {
    float percentAU;
    int count = 0;
    int size;
    String aux = getRnaHairpinSequence();
    size = (aux.length() - getLoop().length()) / 2;

    for (int i = 0; i < size; i++) {

      if ((aux.charAt(i) == 'A' && aux.charAt(aux.length() - 1 - i) == 'U')
        || (aux.charAt(i) == 'U' && aux.charAt(aux.length() - 1 - i) == 'A')) {
        count++;
      }
    }

    percentAU = count / (float) size;

    this.setPercent_AU(percentAU);
  }

  /**
   *
   */
  public void setPercent_CG() {
    float percentCG;
    int count = 0;
    int size;
    String aux = getRnaHairpinSequence();
    size = (aux.length() - getLoop().length()) / 2;

    for (int i = 0; i < size; i++) {

      if ((aux.charAt(i) == 'C' && aux.charAt(aux.length() - 1 - i) == 'G')
        || (aux.charAt(i) == 'G' && aux.charAt(aux.length() - 1 - i) == 'C')) {
        count++;
      }
    }

    percentCG = count / (float) size;

    this.setPercent_CG(percentCG);
  }

  /**
   *
   * @param wooble
   */
  public void setPercent_GU(int wooble) {
    float percentGU;
    int size;
    String aux = getRnaHairpinSequence();
    size = (aux.length() - this.getLoop().length()) / 2;

    aux = aux.substring(0, size);

    percentGU = (float) wooble / aux.length();
    this.setPercent_GU(percentGU);
  }

  /**
   *
   * @param percG_sequence
   */
  public void setPercG_sequence(float percG_sequence) {
    this.percG_sequence = percG_sequence;
  }

  /**
   *
   * @param percU_sequence
   */
  public void setPercU_sequence(float percU_sequence) {
    this.percU_sequence = percU_sequence;
  }

  /**
   *
   * @param locations
   */
  public void setAdditionalSeqLocations(final List<Integer> locations) {
    this.additionalSeqLocations = locations;
  }

  /**
   *
   * @param rnaHairpinSequence
   */
  public void setRnaHairpinSequence(String rnaHairpinSequence) {
    this.rnaHairpinSequence = rnaHairpinSequence;
  }

  /**
   *
   * @param sequenceLength
   */
  public void setSequenceLength(final int sequenceLength) {
    this.sequenceLength = sequenceLength;
  }

  /**
   *
   * @param startsAt
   */
  public void setStartsAt(int startsAt) {
    this.startsAt = startsAt;
  }

  /**
   *
   * @param id
   */
  public void setTags(String id) {
    switch (this.getMode()) {
      case ENSEMBL:
        ((EnsemblFasta) getId_fasta()).setEnsemblTags(id);
        break;
      case FLYBASE:
        ((FlyBaseFasta) getId_fasta()).setFlyBaseTags(id);
        break;
      case BIOMART:
        ((BioMartFasta) getId_fasta()).setBioMartTags(id);
        break;
      case GENERIC:
        ((Generic) getId_fasta()).setGenericTags(id);
        break;
    }
  }

  /**
   *
   * @param gene
   * @param synonym
   * @param transcript
   * @param description
   * @param cds
   */
  public void setTags(String gene, String synonym, String transcript,
    String description,
    String cds) {
    getId_fasta().setGeneID(gene);
    getId_fasta().setTranscriptID(transcript);
    ((GenBank) getId_fasta()).setDescription(description);
    ((GenBank) getId_fasta()).setCDS(cds);
    ((GenBank) getId_fasta()).setSynonym(synonym);
  }

  /**
   *
   * @return
   */
  @Override
  public String toString() {
    return "{" + this.id_fasta.getGeneID() + "; "
      + this.startsAt + "; "
      + this.endsAt + "; "
      + this.loop + "; "
      + this.rnaHairpinSequence + "; "
      + this.viennaStructure + "}";
  }

  /**
   *
   * @return
   */
  public String toRowCSV() {

    String nVariablesValues = "";
    String additionalSeqValues = "";
    String senseValue = "";
    String fornaSequence
      = "http://nibiru.tbi.univie.ac.at/forna/forna.html?id=fasta&file=>"
      + this.getGeneSymbol()
      + "\\n" + this.rnaHairpinSequence
      + "\\n" + this.viennaStructure;
    String aux;
    boolean stop;

    for (int i = 0; i < getMaxPatternLength(); i++) {
      aux = "";
      stop = false;
      for (int j = 0; j < patternVariables.size() && !stop; j++) {
        if (patternVariables.get(j).getPosition() == i) {
          aux = patternVariables.get(j).getValue().toString();
          stop = true;
          patternVariables.remove(j);
        }
      }

      nVariablesValues += aux + SourceFile.ROW_DELIMITER;
    }

    if (isHasAdditionalSequence()) {
      additionalSeqValues = this.getAdditionalSequenceCount()
        + SourceFile.ROW_DELIMITER
        + this.getAdditionalSequenceLocations()
        + SourceFile.ROW_DELIMITER;
    }

    if (isHasTwoSenses()) {
      senseValue = this.isReverse() + SourceFile.ROW_DELIMITER;
    }

    return this.getId_fasta().toRowCSV()
      + this.getLoopPattern() + SourceFile.ROW_DELIMITER
      + this.getTerminalPair() + SourceFile.ROW_DELIMITER
      + this.getPredecessor2Loop() + SourceFile.ROW_DELIMITER //n-2
      + this.getPredecessorLoop() + SourceFile.ROW_DELIMITER
      + this.getLoop() + SourceFile.ROW_DELIMITER
      + this.getRnaHairpinSequence() + SourceFile.ROW_DELIMITER
      + this.getAdditional5Seq() + SourceFile.ROW_DELIMITER
      + this.getAdditional3Seq() + SourceFile.ROW_DELIMITER
      + this.getHairpinStructure() + SourceFile.ROW_DELIMITER
      + this.getStructure() + SourceFile.ROW_DELIMITER
      + this.getPairments() + SourceFile.ROW_DELIMITER /* para que me de apareamientos */
      + this.getGUPairs() + SourceFile.ROW_DELIMITER
      + this.getMismatches() + SourceFile.ROW_DELIMITER
      + this.getInternalLoops() + SourceFile.ROW_DELIMITER
      + this.getSequenceLength() + SourceFile.ROW_DELIMITER
      + this.getStartsAt() + SourceFile.ROW_DELIMITER
      + this.getEndsAt() + SourceFile.ROW_DELIMITER
      + getFormattedNumber(this.getPercA_sequence()) + SourceFile.ROW_DELIMITER
      + getFormattedNumber(this.getPercC_sequence()) + SourceFile.ROW_DELIMITER
      + getFormattedNumber(this.getPercG_sequence()) + SourceFile.ROW_DELIMITER
      + getFormattedNumber(this.getPercU_sequence()) + SourceFile.ROW_DELIMITER
      + getFormattedNumber(this.getPercent_AU()) + SourceFile.ROW_DELIMITER
      + getFormattedNumber(this.getPercent_CG()) + SourceFile.ROW_DELIMITER
      + getFormattedNumber(this.getPercent_GU()) + SourceFile.ROW_DELIMITER
      + getFormattedNumber(this.getPercent_AG()) + SourceFile.ROW_DELIMITER
      + getFormattedNumber(this.getMfe(), 5) + SourceFile.ROW_DELIMITER
      + getFormattedNumber(this.getRelativePos()) + SourceFile.ROW_DELIMITER
      + senseValue
      + nVariablesValues
      + additionalSeqValues
      + fornaSequence + SourceFile.ROW_DELIMITER;
  }

  /**
   * @return the additionalSeqLocations
   */
  public List<Integer> getAdditionalSeqLocations() {
    return Collections.unmodifiableList(additionalSeqLocations);
  }

  /**
   * @return the bulge
   */
  public int getBulge() {
    return bulge;
  }

  /**
   * @return the id_fasta
   */
  public SourceFile getId_fasta() {
    return id_fasta;
  }

  /**
   * @return the internalLoops
   */
  public int getInternalLoops() {
    return internalLoops;
  }

  /**
   * @return the predecessor2Loop
   */
  public char getPredecessor2Loop() {
    return predecessor2Loop;
  }

  /**
   * @return the viennaStructure
   */
  public String getViennaStructure() {
    return viennaStructure;
  }

  /**
   * @return the reversed
   */
  public boolean isReversed() {
    return reversed;
  }

  /**
   * @param bulge the bulge to set
   */
  public void setBulge(int bulge) {
    this.bulge = bulge;
  }

  /**
   * @param hairpinStructure the hairpinStructure to set
   */
  public void setHairpinStructure(String hairpinStructure) {
    this.hairpinStructure = hairpinStructure;
  }

  /**
   * @param id_fasta the id_fasta to set
   */
  public void setId_fasta(SourceFile id_fasta) {
    this.id_fasta = id_fasta;
  }

  /**
   * @param internalLoops the internalLoops to set
   */
  public void setInternalLoops(int internalLoops) {
    this.internalLoops = internalLoops;
  }

  /**
   * @param mfe the mfe to set
   */
  public void setMfe(double mfe) {
    this.mfe = mfe;
  }

  /**
   * @param mode the mode to set
   */
  public void setMode(InputSequence mode) {
    this.mode = mode;
  }

  /**
   * @param percA_sequence the percA_sequence to set
   */
  public void setPercA_sequence(double percA_sequence) {
    this.percA_sequence = percA_sequence;
  }

  /**
   * @param percC_sequence the percC_sequence to set
   */
  public void setPercC_sequence(double percC_sequence) {
    this.percC_sequence = percC_sequence;
  }

  /**
   * @param percG_sequence the percG_sequence to set
   */
  public void setPercG_sequence(double percG_sequence) {
    this.percG_sequence = percG_sequence;
  }

  /**
   * @param percU_sequence the percU_sequence to set
   */
  public void setPercU_sequence(double percU_sequence) {
    this.percU_sequence = percU_sequence;
  }

  /**
   * @param percent_AG the percent_AG to set
   */
  public void setPercent_AG(double percent_AG) {
    this.percent_AG = percent_AG;
  }

  /**
   * @param percent_AU the percent_AU to set
   */
  public void setPercent_AU(double percent_AU) {
    this.percent_AU = percent_AU;
  }

  /**
   * @param percent_CG the percent_CG to set
   */
  public void setPercent_CG(double percent_CG) {
    this.percent_CG = percent_CG;
  }

  /**
   * @param percent_GU the percent_GU to set
   */
  public void setPercent_GU(double percent_GU) {
    this.percent_GU = percent_GU;
  }

  /**
   * @param predecessor2Loop the predecessor2Loop to set
   */
  public void setPredecessor2Loop(char predecessor2Loop) {
    this.predecessor2Loop = predecessor2Loop;
  }

  /**
   * @param predecessorLoop the predecessorLoop to set
   */
  public void setPredecessorLoop(char predecessorLoop) {
    this.predecessorLoop = predecessorLoop;
  }

  /**
   * @param reversed the reversed to set
   */
  public void setReversed(boolean reversed) {
    this.reversed = reversed;
  }

  /**
   * @param viennaStructure the viennaStructure to set
   */
  public void setViennaStructure(String viennaStructure) {
    this.viennaStructure = viennaStructure;
  }

  /**
   * @return the maxPatternLength
   */
  public static int getMaxPatternLength() {
    return maxPatternLength;
  }

  /**
   * @return the hasAdditionalSequence
   */
  public static boolean isHasAdditionalSequence() {
    return hasAdditionalSequence;
  }

  /**
   * @return the hasTwoSenses
   */
  public static boolean isHasTwoSenses() {
    return hasTwoSenses;
  }

  /**
   * @param aHasAdditionalSequence the hasAdditionalSequence to set
   */
  public static void setHasAdditionalSequence(boolean aHasAdditionalSequence) {
    hasAdditionalSequence = aHasAdditionalSequence;
  }

  /**
   * @param aHasTwoSenses the hasTwoSenses to set
   */
  public static void setHasTwoSenses(boolean aHasTwoSenses) {
    hasTwoSenses = aHasTwoSenses;
  }

  /**
   * @param aMaxPatternLength the maxPatternLength to set
   */
  public static void setMaxPatternLength(int aMaxPatternLength) {
    maxPatternLength = aMaxPatternLength;
  }

}
