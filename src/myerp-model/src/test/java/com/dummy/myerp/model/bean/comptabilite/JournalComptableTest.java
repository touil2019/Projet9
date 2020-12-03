package com.dummy.myerp.model.bean.comptabilite;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JournalComptableTest {

    List<JournalComptable> journalComptableList = new ArrayList<>();

    /**
     * Avant chaque test initialiser la variable
     */
    @Before
            public void InitJournalComptable() {
        JournalComptable journalComptable = new JournalComptable();
        journalComptable.setLibelle("Achat");
        journalComptable.setCode("AC");
        journalComptableList.add(journalComptable);
    }



    /**
     *
     * test sur la méthode getByCode
     * entrant:JournalComptable
     * sortant:Journal Comptable
     * attendu:Vérification de la correspondance entre le code et le libelle dans le journal comptable
     *
     */
    @Test
    public void checkMethodGetByCode(){
        JournalComptable journalComptable = new JournalComptable();
        journalComptable.setLibelle("Achat");
        journalComptable.setCode("AC");
        List<JournalComptable> list= new ArrayList<>();
        list.add(journalComptable);
        Assert.assertEquals(JournalComptable.getByCode(list,"AC").getLibelle(),"Achat");
        Assert.assertEquals(JournalComptable.getByCode(list,"AC").getCode(),"AC");
    }

    /**
     *
     * test sur la méthode getByCode
     * entrant:JournalComptable
     * sortant:retour null JournalComptable
     * attendu:Vérification de la non correspondance entre le libellé et le code dans le JournalComptable
     *
     */
    @Test
    public void checkMethodGetByCode_withNullReturn(){

        Assert.assertEquals(JournalComptable.getByCode(journalComptableList,"VE"),null);
    }

    /**
     *
     * Test la création d'un journal comptable au format
     * entrant: JournalComptable
     * sortant: JournalComptable au format
     * attendu: vérification de la correspondance des variablas au format String
     *
     */
    @Test
    public void chekToStringMethod(){
        JournalComptable journalComptable=new JournalComptable("AC","Achat");
        Assert.assertEquals(journalComptable.toString(),"JournalComptable{code='AC', libelle='Achat'}");
    }

}
