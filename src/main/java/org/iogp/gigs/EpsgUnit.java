package org.iogp.gigs;

public class EpsgUnit {
    
    private final String epsgCode;
    private final String systemUnitEpsgCode;
    private final String name;
    private final String unitType;
    private final double factorB;
    private final double factorC;
    private final boolean supportsConversionToBaseUnit;
    private final String[] aliases;

    public EpsgUnit(String epsgCode, String systemUnitEpsgCode, String name, String unitType, double factorB, double factorC, 
            boolean supportsConversionToBaseUnit, String[] aliases) {
        this.epsgCode = epsgCode;
        this.systemUnitEpsgCode = systemUnitEpsgCode;
        this.name = name;
        this.unitType = unitType;
        this.factorB = factorB;
        this.factorC = factorC;
        this.supportsConversionToBaseUnit = supportsConversionToBaseUnit;
        this.aliases = aliases;
    }

    public String getEpsgCode() {
        return epsgCode;
    }

    public String getSystemUnitEpsgCode() {
        return systemUnitEpsgCode;
    }

    public String getName() {
        return name;
    }

    public String getUnitType() {
        return unitType;
    }

    public double getFactorB() {
        return factorB;
    }

    public double getFactorC() {
        return factorC;
    }
    
    public double convertToBaseUnit(double value) {
        return value * (factorB/factorC);
    }
    
    public boolean supportsConversionToBaseUnit() {
        return supportsConversionToBaseUnit;
    }
    
    public boolean nameMatches(String candidateName) {
        if (name.equals(candidateName)) {
            return true;
        }
        for (String currentAlias : aliases) {
            if (currentAlias.equals(candidateName)) {
                return true;
            }
        }
        return false;
    }
    
}
