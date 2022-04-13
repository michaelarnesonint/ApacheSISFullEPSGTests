package org.iogp.gigs;

import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.datum.Ellipsoid;

public class EpsgEllipsoid {

    private final String epsgCode;
    private final String name;
    private final double semiMajorAxis;
    private final String unitEpsgCode;
    private final double invFlattening;
    private final double semiMinorAxis;
    private final boolean deprecated;
    private final String[] aliases;
    
    public EpsgEllipsoid(String[] tokens, String[] aliases) {
        this.epsgCode = tokens[0];
        this.name = tokens[1];
        this.semiMajorAxis = Double.parseDouble(tokens[2]);
        this.unitEpsgCode = tokens[3];
        this.invFlattening = ParsingUtils.parseDouble(tokens[4]);
        this.semiMinorAxis = ParsingUtils.parseDouble(tokens[5]);
        if (tokens[12]!= null && tokens[12].equals("1")) {
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

    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    public String getUnitEpsgCode() {
        return unitEpsgCode;
    }

    public double getInvFlattening() {
        return invFlattening;
    }

    public double getSemiMinorAxis() {
        return semiMinorAxis;
    }
    
    public boolean hasValidSemiMinorAxis() {
        return !Double.isNaN(semiMinorAxis);
    }
    
    public boolean hasValidSemiMajorAxis() {
        return !Double.isNaN(semiMajorAxis);
    }
    
    public boolean hasValidInvFlattening() {
        return !Double.isNaN(invFlattening);
    }

    public boolean isDeprecated() {
        return deprecated;
    }
    
    public boolean nameMatches(Ellipsoid sisEllipsoid) {
        ReferenceIdentifier riName = sisEllipsoid.getName();
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
