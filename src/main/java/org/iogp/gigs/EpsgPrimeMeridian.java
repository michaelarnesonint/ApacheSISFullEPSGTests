package org.iogp.gigs;

import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.datum.PrimeMeridian;

public class EpsgPrimeMeridian {

    private final String epsgCode;
    private final String name;
    private final double greenwichLongitude;
    private final String unitEpsgCode;
    private final boolean deprecated;
    private final String[] aliases;
    
    public EpsgPrimeMeridian(String[] tokens, String[] aliases) {
        this.epsgCode = tokens[0];
        this.name = tokens[1];
        this.greenwichLongitude = ParsingUtils.parseDouble(tokens[2]);
        this.unitEpsgCode = tokens[3];
        if (tokens[9]!= null && tokens[9].equals("1")) {
            this.deprecated = true;
        } else {
            this.deprecated = false;
        }
        this.aliases = aliases;
    }

    public String getEpsgCode() {
        return epsgCode;
    }

    public String getName() {
        return name;
    }

    public double getGreenwichLongitude() {
        return greenwichLongitude;
    }

    public String getUnitEpsgCode() {
        return unitEpsgCode;
    }

    public boolean isDeprecated() {
        return deprecated;
    }
    
    public boolean nameMatches(PrimeMeridian sisPrimeMeridian) {
        ReferenceIdentifier riName = sisPrimeMeridian.getName();
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
        for (String currentAlias : aliases) {
            if (currentAlias.equals(sisName)) {
                return true;
            }
        }
        return false;
    }
    
}
