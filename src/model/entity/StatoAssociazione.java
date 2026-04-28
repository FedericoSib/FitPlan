package model.entity;

public enum StatoAssociazione {
    NESSUNA,    // Il cliente è libero
    PENDING,    // Richiesta inviata, in attesa del PT
    ASSOCIATO   // Il PT ha accettato
}