package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;


//**  * Comptabilite manager implementation. */

public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }


    @Override
    public List<SequenceEcritureComptable> getListSequenceEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListSequenceEcritureComptable();
    }

    @Override
    public SequenceEcritureComptable getSequenceEcritureComptable(String pJournal, Integer pAnnee) {
        return getDaoProxy().getComptabiliteDao().getSequenceEcritureComptable(pJournal, pAnnee);
    }


    @Override
    public void updateSequenceEcritureComptable(SequenceEcritureComptable sequenceEcritureComptable){
        getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(sequenceEcritureComptable);
    }

    @Override
    public void insertSequenceEcritureComptable(SequenceEcritureComptable sequenceEcritureComptable){
        getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(sequenceEcritureComptable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pEcritureComptable.getDate());
        Integer annee = calendar.get(Calendar.YEAR);

        SequenceEcritureComptable vSequenceEcritureComptable = getSequenceEcritureComptable(pEcritureComptable.getJournal().getCode(),annee);

        if (vSequenceEcritureComptable != null){
            vSequenceEcritureComptable.setDerniereValeur(vSequenceEcritureComptable.getDerniereValeur()+1);
            updateSequenceEcritureComptable(vSequenceEcritureComptable);
        }
        else
        {
            vSequenceEcritureComptable = new SequenceEcritureComptable(annee,1);
            vSequenceEcritureComptable.setJournalComptable(pEcritureComptable.getJournal());
            insertSequenceEcritureComptable(vSequenceEcritureComptable);
        }

        /* 3.  Mettre à jour la référence de l'écriture avec la référence calculée (RG_Compta_5)*/
        pEcritureComptable.setReference(reference(vSequenceEcritureComptable));

    }

    /**
     * method to create a reference
     * @param sequenceEcritureComptable
     * @return
     */
    public String reference(SequenceEcritureComptable sequenceEcritureComptable){
        String reference = sequenceEcritureComptable.getJournalComptable().getCode() + "-"
                + sequenceEcritureComptable.getAnnee() + "/"
                + String.format("%05d", sequenceEcritureComptable.getDerniereValeur() );
        return  reference;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        checkEcritureComptableViolations(pEcritureComptable);

        // ==== RG_Compta_3 :  	Une écriture comptable doit contenir au moins deux lignes d'écriture :
        // une au débit et une au crédit.
        checkNumberOflineInEcriture(pEcritureComptable);

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        checkEcritureComptableEquilibree(pEcritureComptable);



    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
                        pEcritureComptable.getReference());

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null
                        || !pEcritureComptable.getId().equals(vECRef.getId())) {
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    protected boolean checkEcritureComptableViolations(EcritureComptable pEcritureComptable) throws FunctionalException{
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                    new ConstraintViolationException(
                            "L'écriture comptable ne respecte pas les contraintes de validation",
                            vViolations));
        } else return true;
    }

    // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
    protected void checkEcritureComptableEquilibree(EcritureComptable pEcritureComptable) throws FunctionalException{
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }
    }



    // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
    protected boolean checkNumberOflineInEcriture(EcritureComptable pEcritureComptable) throws FunctionalException{
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On test le nombre de lignes car si l'écriture à une seule ligne
        //      avec un montant au débit et un montant au crédit ce n'est pas valable
        if (pEcritureComptable.getListLigneEcriture().size() < 2
                || vNbrCredit < 1
                || vNbrDebit < 1) {
            throw new FunctionalException(
                    "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }else return true;
    }

    // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...
    public void checkFormatEtContenuOfReferenceOfEcritureCompatble(EcritureComptable ecritureComptable)
            throws FunctionalException{
        String codeJournal = ecritureComptable.getJournal().getCode();
        String codeJournalReference = ecritureComptable.getReference().substring(0,2);

        if (!codeJournal.equals(codeJournalReference)){
            throw new FunctionalException(
                    "Les codes du journal comptable et de la reference ne sont pas identiques");
        }

        String dateReference = ecritureComptable.getReference().substring(3,7);
        String dateEcritureComptable = ecritureComptable.getDate().toString();
        dateEcritureComptable=dateEcritureComptable.
                substring(dateEcritureComptable.length()-4);

        if (!dateReference.equals(dateEcritureComptable)){
            throw new FunctionalException(
                    "Les dates de l'ecriture comptable et de la reference ne sont pas identiques");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
    @Override
    public void deleteSequenceEcritureComptable(SequenceEcritureComptable sequenceEcritureComptable) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteSequenceEcritureComptable(sequenceEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
}