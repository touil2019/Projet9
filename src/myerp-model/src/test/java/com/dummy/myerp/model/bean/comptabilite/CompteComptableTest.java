package com.dummy.myerp.model.bean.comptabilite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompteComptableTest {

    private CompteComptable compteComptableUnderTest;

    @BeforeEach
    void setUp() {
        compteComptableUnderTest = new CompteComptable(0, "pLibelle");
    }

    @Test
    void testToString() {
        // Setup

        // Run the test
        final String result = compteComptableUnderTest.toString();

        // Verify the results
        assertEquals("CompteComptable{numero=0, libelle='pLibelle'}", result);
    }

    @Test
    void testGetByNumero() {
        // Setup
        final List<? extends CompteComptable> pList = Arrays.asList(new CompteComptable(0, "pLibelle"));

        // Run the test
        final CompteComptable result = CompteComptable.getByNumero(pList, 0);

        // Verify the results
    }
}
