package org.iogp.gigs;

public class EpsgInsertQuery {
    
    public enum TableType {
        ALIAS,
        UNIT_OF_MEASURE,
        ELLIPSOID,
        PRIME_MERIDIAN,
        DATUM,
        CRS,
        COORDINATE_OPERATION,
        OTHER
        
    }
    
    private TableType tableType;
    private String[] values;

    public EpsgInsertQuery(TableType tableType, String[] values) {
        this.tableType = tableType;
        this.values = values;
    }

    public TableType getTableType() {
        return tableType;
    }

    public String[] getValues() {
        return values;
    }

}
