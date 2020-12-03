package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Date;


public class ComptabiliteManagerImplTest {

    private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();

    /**
     * Tester la méthode CheckEcritureComptable
     * entrant: EcritureComptable
     * sortant: Verification de la méthode CheckEcritureComptable
     * attendu: Ne doit lever aucune contraintes
     *
     * @throws Exception
     */
    @Test
    public void checkEcritureComptableUnit() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);

        Assertions.assertDoesNotThrow( () -> {manager.checkEcritureComptableUnit(vEcritureComptable);});
    }

    /**
     * Test de la méthode CheckEcritureComptableUnit
     * entrant: EcritureComptable
     * sortant: Vérification des contraintes
     * attendu: Lever une exception car la référence ne respecte pas les contraintes
     * @throws Exception
     */
    @Test (expected = FunctionalException.class)
    public void checkEcritureComptableUnit_ContraintViolation() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setReference("ABC-2020/00001");
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);

        Assertions.assertThrows(FunctionalException.class, () -> {manager.checkEcritureComptableUnit(vEcritureComptable);});
    }
/**
 * Test de la méthode CheckEcritureComptableUnit
 * entrant: EcritureComptable
 * sortant: Vérification de la règle RG3
 * attendu: Lever une exception car il y a deux lignes de débits
 * @throws Exception
 */
@Test (expected = FunctionalException.class)
public void checkEcritureComptableUnit_RG3() throws Exception {
    EcritureComptable vEcritureComptable;
    vEcritureComptable = new EcritureComptable();
    vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
    vEcritureComptable.setDate(new Date());
    vEcritureComptable.setReference("AB-2020/00001");
    vEcritureComptable.setLibelle("Libelle");
    vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
            null, new BigDecimal(123),
            null));
   vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
            null,  new BigDecimal(123),
            null));

    manager.checkEcritureComptableUnit(vEcritureComptable);

    Assertions.assertThrows(FunctionalException.class, () -> {manager.checkEcritureComptableUnit(vEcritureComptable);});
}

    /**
     *Test la méthode checkEcritureComptableUnit_IsNotEquilibre
     *entrant: EcritureComptable
     *sortant: Vérification de la règle RG2
     *attendu: Lever une exception car il n'y pas équilibre entre crédit et débit
     * @throws Exception
     */
    @Test (expected = FunctionalException.class)
    public void checkEcritureComptableUnit_IsNotEquilibre() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setReference("AB-2020/00001");
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(1234)));
        manager.checkEcritureComptableUnit(vEcritureComptable);

        Assertions.assertThrows(FunctionalException.class, () -> {manager.checkEcritureComptableUnit(vEcritureComptable);});
    }






}
