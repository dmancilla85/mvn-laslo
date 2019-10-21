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

/**
 * @author David
 * Some useful biologic patterns 
 */
public class BiologicPatterns {
  
    // Pumilio
    public static final String PUM1 = "UGUANAUA";
    public static final String PUM2 = "UGUANAUARNNNNBBBBSCCS";
    
    //  Methyl-6-Adenosine Degenerated Consensus
    public static final String MET6_A_DEG_CONSENSUS = "RRACH";

    // Polyadenilation signal 1
    public final static String POLY_A_SGN_1 = "AUUAAA"; // CPSF Binding site
    
    // Polyadenilation signal 2
    public final static String POLY_A_SGN_2 = "AUAAAA"; // CPSF Binding site

    /**
     * Kozak-like sequences in various eukaryotes: 
     * * Vertebrate                 gccRccATGG[2] 
     * * Fruit fly/Arthropoda       cAAacATG[7] 
     * * Yeast Ascomycota           aAaAaAATGTCt[8] 
     * * Slime mold/Amoebozoa       aaaAAAATGRna[9] 
     * * Ciliate	Ciliophora  nTaAAAATGRct[9] 
     * * Plasmodium Apicomplexa     taaAAAATGAan[9] 
     * * Toxoplasma Apicomplexa     gncAaaATGg[10]
     * * Trypanosomatidae           nnnAnnATGnC[9] 
     * * Terrestrial plants         AACAATGGC[11]
     */
    public final static String KOZAK_LIKE_STRONG = "SVVSVVRVVAUGG";
    public final static String KOZAK_LIKE_WEAK = "SVNVNNVVVAUGR";
    // SVNSNNRVVAUGR to match with NM_079620.3 (NCBI)
    // VNNSNVRVVAUGN to match with NM_001297949.1?

    public final static String GU_RICH_PATTERN = "KKKKKK"; 
    // CSTF Binding site
    public final static String CLEAVAGE_FACTOR1_SITE = "UGUAA";
    public final static String SMAUG_0 = "CNGG";
    public final static String SMAUG_1 = "CNGGN";
    public final static String SMAUG_2 = "CNGGNN";
    public final static String SMAUG_3 = "CNGGNNN";
    public final static String SMAUG_4 = "CNGGNNNN";
}
