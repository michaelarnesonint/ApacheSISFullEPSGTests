package org.iogp.gigs;

import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.SingleCRS;

public class EpsgCrs {

    private final String epsgCode;
    private final String name;
    private final String areaOfUseEpsgCode;
    private final String coordSystemCode;
    private final String datumCode;
    private final String sourceGeogCrsCode;
    private final String projectionConversionCode;
    private final String compoundHorizonCrsCode;
    private final String compoundVerticalCrsCode;
    private final boolean deprecated;
    private final boolean geocentric;
    private final String[] aliases;

    public EpsgCrs(String[] tokens, String[] aliases) {
        this.epsgCode = tokens[0];
        this.name = tokens[1];
        this.areaOfUseEpsgCode = tokens[2];
        this.coordSystemCode = ParsingUtils.parseCode(tokens[4]);
        this.datumCode = ParsingUtils.parseCode(tokens[5]);
        this.sourceGeogCrsCode = ParsingUtils.parseCode(tokens[6]);
        this.projectionConversionCode = ParsingUtils.parseCode(tokens[7]);
        this.compoundHorizonCrsCode = ParsingUtils.parseCode(tokens[8]);
        this.compoundVerticalCrsCode = ParsingUtils.parseCode(tokens[9]);
        this.deprecated = tokens[17] != null && tokens[17].equals("1");
        this.geocentric = tokens[3] != null && tokens[3].equals("geocentric");
        this.aliases = aliases;
    }

    public String getEpsgCode() {
        return epsgCode;
    }

    public String getName() {
        return name;
    }

    public String getAreaOfUseEpsgCode() {
        return areaOfUseEpsgCode;
    }

    public String getCoordSystemCode() {
        return coordSystemCode;
    }

    public String getDatumCode() {
        return datumCode;
    }

    public String getSourceGeogCrsCode() {
        return sourceGeogCrsCode;
    }

    public String getProjectionConversionCode() {
        return projectionConversionCode;
    }

    public String getCompoundHorizonCrsCode() {
        return compoundHorizonCrsCode;
    }

    public String getCompoundVerticalCrsCode() {
        return compoundVerticalCrsCode;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public boolean isGeocentric() {
        return geocentric;
    }
    
    public boolean nameMatches(SingleCRS geodeticCrs) {
        ReferenceIdentifier riName = geodeticCrs.getName();
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
