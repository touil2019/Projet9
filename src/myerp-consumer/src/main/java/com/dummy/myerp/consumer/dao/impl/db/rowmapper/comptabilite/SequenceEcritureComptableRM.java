package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import com.dummy.myerp.consumer.dao.impl.cache.JournalComptableDaoCache;
import com.dummy.myerp.consumer.db.helper.ResultSetHelper;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;


import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * {@link RowMapper} de {@link SequenceEcritureComptable}
 */
public class SequenceEcritureComptableRM implements RowMapper< SequenceEcritureComptable > {

    /** JournalComptableDaoCache */
    private final JournalComptableDaoCache journalComptableDaoCache = new JournalComptableDaoCache();

    @Override
    public SequenceEcritureComptable mapRow(ResultSet pRS, int pRowNum) throws SQLException {
        SequenceEcritureComptable vBean = new SequenceEcritureComptable();
        vBean.setJournalComptable(journalComptableDaoCache.getByCode( pRS.getString("journal_code")));
        vBean.setAnnee(ResultSetHelper.getInteger(pRS,"annee"));
        vBean.setDerniereValeur(ResultSetHelper.getInteger(pRS,"derniere_valeur"));
        return vBean;
    }
}
