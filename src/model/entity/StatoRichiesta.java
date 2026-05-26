package model.entity;

public enum StatoRichiesta {
    NESSUNA, //Il cliente non ha effettuato una richiesta
    PENDING, //Il cliente ha inviato una richiesta, il PT non l'ha visionata
    IN_LAVORAZIONE, //Il PT ha visualizzato la richiesta. (Ha scelto l'opzione assembla in seguito)
    COMPLETATA //Il PT ha inviato la richiesta
}
