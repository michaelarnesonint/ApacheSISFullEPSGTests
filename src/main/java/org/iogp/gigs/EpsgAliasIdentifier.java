package org.iogp.gigs;

import java.util.Objects;

public class EpsgAliasIdentifier {

    private EpsgInsertQuery.TableType tableType;
    private String referenceEpsgCode;
    
    public EpsgAliasIdentifier(EpsgInsertQuery.TableType tableType, String referenceEpsgCode) {
        this.tableType = tableType;
        this.referenceEpsgCode = referenceEpsgCode;
    }

    public EpsgInsertQuery.TableType getTableType() {
        return tableType;
    }

    public String getReferenceEpsgCode() {
        return referenceEpsgCode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.tableType);
        hash = 79 * hash + Objects.hashCode(this.referenceEpsgCode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EpsgAliasIdentifier other = (EpsgAliasIdentifier) obj;
        if (!Objects.equals(this.referenceEpsgCode, other.referenceEpsgCode)) {
            return false;
        }
        if (this.tableType != other.tableType) {
            return false;
        }
        return true;
    }
    
    
    
}
