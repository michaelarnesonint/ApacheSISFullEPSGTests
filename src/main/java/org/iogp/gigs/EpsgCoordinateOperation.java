package org.iogp.gigs;

import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.CoordinateOperation;

public class EpsgCoordinateOperation {

    private final String epsgCode;
    private final String name;
    private final String type;
    private final String sourceCrsEpsgCode;
    private final String targetCrsEpsgCode;
    private final String coordTfmVersion;
    private final String coordOperationVariant;
    private final String areaOfUseEpsgCode;
    private final String coordOperationScope;
    private final Double coordOperationAccuracy;
    private final String coordOperationMethodEpsgCode;
    private final String uomEpsgCodeSourceCoordDiff;
    private final String uomEpsgCodeTargetCoordDiff;
    private final String[] aliases;
    private final boolean deprecated;
    
    public EpsgCoordinateOperation(String[] tokens, String[] aliases) {
        this.epsgCode = tokens[0];
        this.name = tokens[1];
        this.type = tokens[2];
        this.sourceCrsEpsgCode = tokens[3];
        this.targetCrsEpsgCode = tokens[4];
        this.coordTfmVersion = tokens[5];
        this.coordOperationVariant = tokens[6];
        this.areaOfUseEpsgCode = tokens[7];
        this.coordOperationScope = tokens[8];
        Double foundOperationAccuracy = null;
        try {
            foundOperationAccuracy = Double.parseDouble(tokens[9]);
        } catch(Exception ex) {
            foundOperationAccuracy = null;
        }
        this.coordOperationAccuracy = foundOperationAccuracy;
        this.coordOperationMethodEpsgCode = tokens[10];
        this.uomEpsgCodeSourceCoordDiff = tokens[11];
        this.uomEpsgCodeTargetCoordDiff = tokens[12];
        this.deprecated = tokens[19] != null && tokens[19].equals("1");
        this.aliases = aliases;
    }

    public String getEpsgCode() {
        return epsgCode;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSourceCrsEpsgCode() {
        return sourceCrsEpsgCode;
    }

    public String getTargetCrsEpsgCode() {
        return targetCrsEpsgCode;
    }

    public String getCoordTfmVersion() {
        return coordTfmVersion;
    }

    public String getCoordOperationVariant() {
        return coordOperationVariant;
    }

    public String getAreaOfUseEpsgCode() {
        return areaOfUseEpsgCode;
    }

    public String getCoordOperationScope() {
        return coordOperationScope;
    }

    public Double getCoordOperationAccuracy() {
        return coordOperationAccuracy;
    }

    public String getCoordOperationMethodEpsgCode() {
        return coordOperationMethodEpsgCode;
    }

    public String getUomEpsgCodeSourceCoordDiff() {
        return uomEpsgCodeSourceCoordDiff;
    }

    public String getUomEpsgCodeTargetCoordDiff() {
        return uomEpsgCodeTargetCoordDiff;
    }

    public boolean isDeprecated() {
        return deprecated;
    }
    
    public boolean nameMatches(CoordinateOperation operation) {
        ReferenceIdentifier riName = operation.getName();
        if (riName == null) {
            return false;
        }
        String sisName = riName.getCode();
        if (sisName == null) {
            return false;
        }
        if (sisName.equals(name)) {
            return true;
        }
        if (sisName.contains("'")) {
            String sisNameWithoutQuotes = sisName.replace("/'", "");
            if (sisName.equals(sisNameWithoutQuotes)) {
                return true;
            }
        }
        for (String currentAlias : aliases) {
            if (currentAlias.equals(sisName)) {
                return true;
            }
        }
        return false;
    }
}
