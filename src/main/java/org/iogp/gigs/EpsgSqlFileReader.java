package org.iogp.gigs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EpsgSqlFileReader {

    private Map<EpsgInsertQuery.TableType, List<EpsgInsertQuery>> queriesMap = new LinkedHashMap<>();
    private Map<EpsgAliasIdentifier, List<EpsgAlias>> aliasesMap = new LinkedHashMap<>();

    private Map<String, EpsgPrimeMeridian> epsgCodeToPrimeMeridiansMap = new LinkedHashMap<>();
    private Map<String, EpsgUnit> epsgCodeToUnitsMap = new LinkedHashMap<>();
    private Map<String, EpsgEllipsoid> epsgCodeToEllipsoidsMap = new LinkedHashMap<>();
    private Map<String, EpsgDatum> epsgCodeToGeodeticDatumsMap = new LinkedHashMap<>();
    private Map<String, EpsgDatum> epsgCodeToVerticalDatumsMap = new LinkedHashMap<>();
    private Map<String, EpsgGeodeticCrs> epsgCodeToGeodeticCrsMap = new LinkedHashMap<>();
    private Map<String, EpsgCoordinateOperation> epsgCodeToConversionMap = new LinkedHashMap<>();
    private Map<String, EpsgCoordinateOperation> epsgCodeToTransformationMap = new LinkedHashMap<>();

    public EpsgSqlFileReader() {

    }

    public Map<String, EpsgDatum> getEpsgGeodeticDatumsMap() {
        return epsgCodeToGeodeticDatumsMap;
    }
    
    public Map<String, EpsgDatum> getEpsgVerticalDatumsMap() {
        return epsgCodeToVerticalDatumsMap;
    }

    public Map<String, EpsgUnit> getEpsgCodesToUnitsMap() {
        return epsgCodeToUnitsMap;
    }

    public Map<String, EpsgEllipsoid> getEpsgCodesToEllipsoidsMap() {
        return epsgCodeToEllipsoidsMap;
    }

    public Map<String, EpsgPrimeMeridian> getEpsgCodeToPrimeMeridiansMap() {
        return epsgCodeToPrimeMeridiansMap;
    }

    public Map<String, EpsgGeodeticCrs> getEpsgCodeToGeodeticCrsMap() {
        return epsgCodeToGeodeticCrsMap;
    }

    public Map<String, EpsgCoordinateOperation> getEpsgCodeToConversionMap() {
        return epsgCodeToConversionMap;
    }

    public Map<String, EpsgCoordinateOperation> getEpsgCodeToTransformationMap() {
        return epsgCodeToTransformationMap;
    }

    public void parseDataFile(File dataSqlFile) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataSqlFile)))) {
            String line = "";
            StringBuilder currentInsertQueryStringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.endsWith(");")) {
                    currentInsertQueryStringBuilder.append(line);
                    handleInsertQuery(currentInsertQueryStringBuilder.toString());
                    currentInsertQueryStringBuilder = new StringBuilder();
                    continue;
                }

                currentInsertQueryStringBuilder.append(line);
            }
        }

        buildPrimeMeridianMap();
        buildUnitOfMeasureMap();
        buildEllipsoidMap();
        buildDatumMap();
        buildCRSMaps();
        buildCoordinateOperationMaps();
    }

    private void handleInsertQuery(String insertQueryString) {
        int indexAfterTableName = insertQueryString.indexOf(" VALUES ");
        if (indexAfterTableName == -1) {
            return;
        }
        String tableName = insertQueryString.substring("INSERT INTO ".length(), indexAfterTableName);
        String valuesString = insertQueryString.substring(indexAfterTableName + " VALUES ".length());
        int indexBeforeValues = valuesString.indexOf("(");
        int indexAfterValues = valuesString.lastIndexOf(")");
        if (indexAfterValues == -1 || indexBeforeValues == -1) {
            return;
        }
        valuesString = valuesString.substring(indexBeforeValues + 1, indexAfterValues);
        String[] values = getTokensFromValuesLine(valuesString);

        EpsgInsertQuery.TableType tableType = getTableType(tableName);
        EpsgInsertQuery query = new EpsgInsertQuery(tableType, values);

        List<EpsgInsertQuery> associatedQueries = queriesMap.get(tableType);
        if (associatedQueries == null) {
            associatedQueries = new ArrayList<>();
            queriesMap.put(tableType, associatedQueries);
        }
        associatedQueries.add(query);
        if (tableType == EpsgInsertQuery.TableType.ALIAS) {
            String referenceTable = values[1];
            String referenceEpsgCode = values[2];
            String aliasString = values[4];
            EpsgAliasIdentifier aliasIdentifer = getAliasIdentifer(referenceTable, referenceEpsgCode);
            if (aliasIdentifer != null) {
                EpsgAlias alias = new EpsgAlias(aliasIdentifer, aliasString);
                List<EpsgAlias> associatedAliases = aliasesMap.get(aliasIdentifer);
                if (associatedAliases == null) {
                    associatedAliases = new ArrayList<>();
                    aliasesMap.put(aliasIdentifer, associatedAliases);
                }
                associatedAliases.add(alias);
            }
        }
    }

    private EpsgAliasIdentifier getAliasIdentifer(String referenceType, String referenceEpsgCode) {
        switch (referenceType) {
            case "Ellipsoid":
                return new EpsgAliasIdentifier(EpsgInsertQuery.TableType.ELLIPSOID, referenceEpsgCode);
            case "Prime Meridian":
                return new EpsgAliasIdentifier(EpsgInsertQuery.TableType.PRIME_MERIDIAN, referenceEpsgCode);
            case "Unit of Measure":
                return new EpsgAliasIdentifier(EpsgInsertQuery.TableType.UNIT_OF_MEASURE, referenceEpsgCode);
            case "Datum":
                return new EpsgAliasIdentifier(EpsgInsertQuery.TableType.DATUM, referenceEpsgCode);
            default:
                return null;
        }
    }

    private EpsgInsertQuery.TableType getTableType(String tableName) {
        switch (tableName) {
            case "epsg_coordinatereferencesystem":
                return EpsgInsertQuery.TableType.CRS;
            case "epsg_primemeridian":
                return EpsgInsertQuery.TableType.PRIME_MERIDIAN;
            case "epsg_ellipsoid":
                return EpsgInsertQuery.TableType.ELLIPSOID;
            case "epsg_unitofmeasure":
                return EpsgInsertQuery.TableType.UNIT_OF_MEASURE;
            case "epsg_alias":
                return EpsgInsertQuery.TableType.ALIAS;
            case "epsg_datum":
                return EpsgInsertQuery.TableType.DATUM;
            case "epsg_coordoperation":
                return EpsgInsertQuery.TableType.COORDINATE_OPERATION;
            default:
                return EpsgInsertQuery.TableType.OTHER;
        }
    }

    private void buildDatumMap() {
        List<EpsgInsertQuery> datumQueries = queriesMap.get(EpsgInsertQuery.TableType.DATUM);
        if (datumQueries == null) {
            return;
        }
        for (EpsgInsertQuery currentQuery : datumQueries) {
            parseDatumQuery(currentQuery);
        }
    }

    private void buildUnitOfMeasureMap() {
        List<EpsgInsertQuery> unitOfMeasureQueries = queriesMap.get(EpsgInsertQuery.TableType.UNIT_OF_MEASURE);
        if (unitOfMeasureQueries == null) {
            return;
        }
        for (EpsgInsertQuery currentQuery : unitOfMeasureQueries) {
            parseUnitOfMeasureQuery(currentQuery);
        }
    }

    private void buildEllipsoidMap() {
        List<EpsgInsertQuery> ellipsoidQueries = queriesMap.get(EpsgInsertQuery.TableType.ELLIPSOID);
        if (ellipsoidQueries == null) {
            return;
        }
        for (EpsgInsertQuery currentQuery : ellipsoidQueries) {
            parseEllipsoidQuery(currentQuery);
        }
    }

    private void buildPrimeMeridianMap() {
        List<EpsgInsertQuery> primeMeridianQueries = queriesMap.get(EpsgInsertQuery.TableType.PRIME_MERIDIAN);
        if (primeMeridianQueries == null) {
            return;
        }
        for (EpsgInsertQuery currentQuery : primeMeridianQueries) {
            parsePrimeMeridianQuery(currentQuery);
        }
    }

    private void buildCRSMaps() {
        List<EpsgInsertQuery> crsQueries = queriesMap.get(EpsgInsertQuery.TableType.CRS);
        if (crsQueries == null) {
            return;
        }
        for (EpsgInsertQuery currentQuery : crsQueries) {
            parseCrsQuery(currentQuery);
        }
    }

    private void buildCoordinateOperationMaps() {
        List<EpsgInsertQuery> crsQueries = queriesMap.get(EpsgInsertQuery.TableType.COORDINATE_OPERATION);
        if (crsQueries == null) {
            return;
        }
        for (EpsgInsertQuery currentQuery : crsQueries) {
            parseCoordinateOperationQuery(currentQuery);
        }
    }

    private void parseUnitOfMeasureQuery(EpsgInsertQuery query) {
        String[] tokens = query.getValues();
        String epsgCode = tokens[0];
        String name = tokens[1];
        String unitType = tokens[2];
        String baseEpsgCode = tokens[3];

        double factorB = ParsingUtils.parseDouble(tokens[4]);
        double factorC = ParsingUtils.parseDouble(tokens[5]);
        boolean supportConversion = true;
        if (tokens[4].equals("Null") || tokens[5].equals("Null")) {
            supportConversion = false;
        }
        List<String> aliasNames = new ArrayList<>();
        EpsgAliasIdentifier aliasIdentifier = new EpsgAliasIdentifier(EpsgInsertQuery.TableType.UNIT_OF_MEASURE, epsgCode);
        List<EpsgAlias> associatedAliases = aliasesMap.get(aliasIdentifier);
        if (associatedAliases != null) {
            for (EpsgAlias currentAlias : associatedAliases) {
                aliasNames.add(currentAlias.getAlias());
            }
        }
        EpsgUnit unit = new EpsgUnit(epsgCode, baseEpsgCode, name, unitType, factorB, factorC, supportConversion, aliasNames.toArray(new String[0]));
        epsgCodeToUnitsMap.put(epsgCode, unit);
    }

    private void parseEllipsoidQuery(EpsgInsertQuery query) {
        String[] tokens = query.getValues();
        String epsgCode = tokens[0];

        List<String> aliasNames = new ArrayList<>();
        EpsgAliasIdentifier aliasIdentifier = new EpsgAliasIdentifier(EpsgInsertQuery.TableType.ELLIPSOID, epsgCode);
        List<EpsgAlias> associatedAliases = aliasesMap.get(aliasIdentifier);
        if (associatedAliases != null) {
            for (EpsgAlias currentAlias : associatedAliases) {
                aliasNames.add(currentAlias.getAlias());
            }
        }
        EpsgEllipsoid ellipsoid = new EpsgEllipsoid(tokens, aliasNames.toArray(new String[0]));
        epsgCodeToEllipsoidsMap.put(epsgCode, ellipsoid);
    }

    private void parseCoordinateOperationQuery(EpsgInsertQuery query) {
        String[] tokens = query.getValues();
        String epsgCode = tokens[0];

        List<String> aliasNames = new ArrayList<>();
        EpsgAliasIdentifier aliasIdentifier = new EpsgAliasIdentifier(EpsgInsertQuery.TableType.CRS, epsgCode);
        List<EpsgAlias> associatedAliases = aliasesMap.get(aliasIdentifier);
        if (associatedAliases != null) {
            for (EpsgAlias currentAlias : associatedAliases) {
                aliasNames.add(currentAlias.getAlias());
            }
        }
        EpsgCoordinateOperation coordinateOperation = new EpsgCoordinateOperation(tokens, aliasNames.toArray(new String[0]));
        switch (tokens[2]) {
            case "conversion":
                epsgCodeToConversionMap.put(epsgCode, coordinateOperation);
                break;
            case "transformation":
                epsgCodeToTransformationMap.put(epsgCode, coordinateOperation);
                break;
        }
        return;
    }

    private void parseCrsQuery(EpsgInsertQuery query) {
        String[] tokens = query.getValues();
        String epsgCode = tokens[0];

        List<String> aliasNames = new ArrayList<>();
        EpsgAliasIdentifier aliasIdentifier = new EpsgAliasIdentifier(EpsgInsertQuery.TableType.CRS, epsgCode);
        List<EpsgAlias> associatedAliases = aliasesMap.get(aliasIdentifier);
        if (associatedAliases != null) {
            for (EpsgAlias currentAlias : associatedAliases) {
                aliasNames.add(currentAlias.getAlias());
            }
        }
        switch (tokens[3]) {
            case "geographic 2D":
            case "geographic 3D":
            case "geocentric":
                EpsgGeodeticCrs geodeticCrs = new EpsgGeodeticCrs(tokens, aliasNames.toArray(new String[0]));
                epsgCodeToGeodeticCrsMap.put(epsgCode, geodeticCrs);
                break;
            case "projected":
                break;
            case "vertical":
                break;
            case "compound":
            case "engineering":
                break;
        }
    }

    private void parsePrimeMeridianQuery(EpsgInsertQuery query) {
        String[] tokens = query.getValues();
        String epsgCode = tokens[0];

        List<String> aliasNames = new ArrayList<>();
        EpsgAliasIdentifier aliasIdentifier = new EpsgAliasIdentifier(EpsgInsertQuery.TableType.PRIME_MERIDIAN, epsgCode);
        List<EpsgAlias> associatedAliases = aliasesMap.get(aliasIdentifier);
        if (associatedAliases != null) {
            for (EpsgAlias currentAlias : associatedAliases) {
                aliasNames.add(currentAlias.getAlias());
            }
        }
        EpsgPrimeMeridian primeMeridian = new EpsgPrimeMeridian(tokens, aliasNames.toArray(new String[0]));
        epsgCodeToPrimeMeridiansMap.put(epsgCode, primeMeridian);
    }

    private void parseDatumQuery(EpsgInsertQuery currentQuery) {
        String[] values = currentQuery.getValues();
        String epsgCode = values[0];

        List<String> aliasNames = new ArrayList<>();
        EpsgAliasIdentifier aliasIdentifier = new EpsgAliasIdentifier(EpsgInsertQuery.TableType.DATUM, epsgCode);
        List<EpsgAlias> associatedAliases = aliasesMap.get(aliasIdentifier);
        if (associatedAliases != null) {
            for (EpsgAlias currentAlias : associatedAliases) {
                aliasNames.add(currentAlias.getAlias());
            }
        }
        EpsgDatum datum = new EpsgDatum(values, aliasNames.toArray(new String[0]));
        switch (values[2]) {
            case "geodetic":
                epsgCodeToGeodeticDatumsMap.put(epsgCode, datum);
                break;
            case "vertical":
                epsgCodeToVerticalDatumsMap.put(epsgCode, datum);
        }

    }

    private String[] getTokensFromValuesLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        int numberOfQuotes = 0;
        for (int i = 0; i < line.length(); i++) {
            char currentCharacter = line.charAt(i);
            if (currentCharacter == ',' && numberOfQuotes % 2 == 0) {
                tokens.add(currentToken.toString().trim());
                currentToken = new StringBuilder();
                continue;
            }
            if (currentCharacter == '\'') {
                numberOfQuotes++;
                continue;
            }
            currentToken.append(currentCharacter);
        }
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString().trim());
        } else if (line.endsWith(",")) {
            tokens.add("");
        }
        return tokens.toArray(new String[0]);
    }

}
