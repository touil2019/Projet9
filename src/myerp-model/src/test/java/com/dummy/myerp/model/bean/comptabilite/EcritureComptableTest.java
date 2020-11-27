package com.dummy.myerp.model.bean.comptabilite;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;


public class EcritureComptableTest {

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                vLibelle,
                vDebit, vCredit);
        return vRetour;
    }

    private EcritureComptable vEcriture ;



    /**
     * Avant chaque test initialiser la variable
     */
    @Before
    public void initCompatibiliteManagerImpl(){
        vEcriture = new EcritureComptable();

        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
    }

    /**
     * Après chaque test, réinitialisez la variable vEcriture
     */
    @After
    public void ResetvEcritureComptable(){
        vEcriture=new EcritureComptable();
    }

    /**
     * Teste l'équilibre des lignes comptables entre Débit et Crédit
     * entrant:EcritureComptable
     * sortant: la valeur True ou false
     * attendu: Vérifier les lignes d'écritures à l'équilibre et non équilibrés
     */
    @Test
    public void isEquilibre() {
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();

        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());

        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }

    /**
     * Teste l'équalité entre les valeurs numériques et le total des lignes d'écritures au crédit au format
     *entrant: EcritureComptable
     *sortant: montant total des lignes d'écritures aux crédits
     *attendu: Vérifier le retour du montant total des crédits avec 2 chiffres après la virgule
     */
    @Test
    public void getTotalDebit(){
        Assert.assertEquals(vEcriture.getTotalCredit(), BigDecimal.valueOf(33+301+7).setScale(2));
    }

    /**
     * Teste l'equalité entre les valeurs numériques et le total des lignes d'écritures au débit au format
     *entrant: EcritureComptable
     *sortant: montant total des lignes d'écritures aux débits
     *attendu: Vérifier le retour du montant total des débits avec 2 chiffres après la virgule
     */
    @Test
    public void getTotalCredit() {
        Assert.assertEquals(vEcriture.getTotalDebit(), BigDecimal.valueOf(200.50+100.50+40).setScale(2));
    }
}


