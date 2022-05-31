/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iogp.gigs;

import java.io.File;
import java.util.Map;
import java.util.Set;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;
import org.apache.sis.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.GeodeticCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.datum.VerticalDatum;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;

/**
 *
 * @author Michael
 */
public class FullEpsgDatasetTest {

    @Test
    public void testGeodeticDatum() throws Exception {
        EpsgSqlFileReader reader = new EpsgSqlFileReader();
        reader.parseDataFile(new File("C:\\Users\\Michael\\GIGS_TEST\\EPSG_DB_FILES\\EPSG-v9_9_1-PostgreSQL\\PostgreSQL_Data_Script.sql"));

        DatumAuthorityFactory datumFactory = (DatumAuthorityFactory) CRS.getAuthorityFactory("EPSG");

        int numberOfMissingEntries = 0;
        int numberOfCorrectEntries = 0;
        int numberOfIncorrectEntries = 0;
        int numberOfExceptionEntries = 0;

        Map<String, EpsgDatum> epsgGeodeticDatumsMap = reader.getEpsgGeodeticDatumsMap();
        for (Map.Entry<String, EpsgDatum> currentEntry : epsgGeodeticDatumsMap.entrySet()) {
            String epsgCode = currentEntry.getKey();
            EpsgDatum epsgGeodeticDatum = currentEntry.getValue();
            try {
                GeodeticDatum sisGeodeticDatum = datumFactory.createGeodeticDatum(epsgCode);
                if (sisGeodeticDatum == null) {
                    numberOfMissingEntries++;
                    System.out.println("No match found for geodetic datum: " + epsgGeodeticDatum.getEpsgCode() + ":" + epsgGeodeticDatum.getName());
                    continue;
                }
                if (!epsgGeodeticDatum.nameMatches(sisGeodeticDatum)) {
                    System.out.println("Name mismatch for geodetic datum: " + epsgGeodeticDatum.getEpsgCode() + ":" + epsgGeodeticDatum.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                if (!epsgCodeMatchesSisObject(epsgGeodeticDatum.getEllipsoidEpsgCode(), sisGeodeticDatum.getEllipsoid())) {
                    System.out.println("Ellipsoid name mismatch for geodetic datum: " + epsgGeodeticDatum.getEpsgCode() + ":" + epsgGeodeticDatum.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                if (!epsgCodeMatchesSisObject(epsgGeodeticDatum.getPrimeMeridianEpsgCode(), sisGeodeticDatum.getPrimeMeridian())) {
                    System.out.println("Prime meridian name mismatch for geodetic datum: " + epsgGeodeticDatum.getEpsgCode() + ":" + epsgGeodeticDatum.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                System.out.println("Successfully validated geodetic datum: " + epsgGeodeticDatum.getEpsgCode() + ":" + epsgGeodeticDatum.getName());
                numberOfCorrectEntries++;
            } catch (Exception ex) {
                System.out.println("Exception occured for geodetic datum: " + epsgGeodeticDatum.getEpsgCode() + ":" + epsgGeodeticDatum.getName() + ":" + ex.getMessage());
                numberOfExceptionEntries++;
            }
        }

        System.out.println("Finished Geodetic Datums Comparision");
        System.out.println("Number of valid geodetic datums found " + numberOfCorrectEntries);
        System.out.println("Number of missing geodetic datums found " + numberOfMissingEntries);
        System.out.println("Number of invalid geodetic datums found " + numberOfIncorrectEntries);
        System.out.println("Number of geodetic datums with exception thrown " + numberOfExceptionEntries);
        if (numberOfIncorrectEntries > 0) {
            Assert.fail("Some geodetic datums in EPSG Database did not match what was in Apache SIS");
        }
    }

    @Test
    public void testVerticalDatum() throws Exception {
        EpsgSqlFileReader reader = new EpsgSqlFileReader();
        reader.parseDataFile(new File("C:\\Users\\Michael\\GIGS_TEST\\EPSG_DB_FILES\\EPSG-v9_9_1-PostgreSQL\\PostgreSQL_Data_Script.sql"));

        DatumAuthorityFactory datumFactory = (DatumAuthorityFactory) CRS.getAuthorityFactory("EPSG");

        int numberOfMissingEntries = 0;
        int numberOfCorrectEntries = 0;
        int numberOfIncorrectEntries = 0;
        int numberOfExceptionEntries = 0;

        Map<String, EpsgDatum> epsgVerticalDatumsMap = reader.getEpsgVerticalDatumsMap();
        for (Map.Entry<String, EpsgDatum> currentEntry : epsgVerticalDatumsMap.entrySet()) {
            String epsgCode = currentEntry.getKey();
            EpsgDatum epsgVerticalDatum = currentEntry.getValue();
            try {
                VerticalDatum sisVerticalDaum = datumFactory.createVerticalDatum(epsgCode);
                if (sisVerticalDaum == null) {
                    numberOfMissingEntries++;
                    System.out.println("No match found for geodetic datum: " + epsgVerticalDatum.getEpsgCode() + ":" + epsgVerticalDatum.getName());
                    continue;
                }
                if (!epsgVerticalDatum.nameMatches(sisVerticalDaum)) {
                    System.out.println("Name mismatch for geodetic datum: " + epsgVerticalDatum.getEpsgCode() + ":" + epsgVerticalDatum.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                System.out.println("Successfully validated geodetic datum: " + epsgVerticalDatum.getEpsgCode() + ":" + epsgVerticalDatum.getName());
                numberOfCorrectEntries++;
            } catch (Exception ex) {
                System.out.println("Exception occured for geodetic datum: " + epsgVerticalDatum.getEpsgCode() + ":" + epsgVerticalDatum.getName() + ":" + ex.getMessage());
                numberOfExceptionEntries++;
            }
        }

        System.out.println("Finished Prime Meridian Comparision");
        System.out.println("Number of valid geodetic datums found " + numberOfCorrectEntries);
        System.out.println("Number of missing geodetic datums found " + numberOfMissingEntries);
        System.out.println("Number of invalid geodetic datums found " + numberOfIncorrectEntries);
        System.out.println("Number of geodetic datums with exception thrown " + numberOfExceptionEntries);
        if (numberOfIncorrectEntries > 0) {
            Assert.fail("Some geodetic datums in EPSG Database did not match what was in Apache SIS");
        }
    }

    @Test
    public void testPrimeMeridian() throws Exception {
        EpsgSqlFileReader reader = new EpsgSqlFileReader();
        reader.parseDataFile(new File("C:\\Users\\Michael\\GIGS_TEST\\EPSG_DB_FILES\\EPSG-v9_9_1-PostgreSQL\\PostgreSQL_Data_Script.sql"));

        DatumAuthorityFactory datumFactory = (DatumAuthorityFactory) CRS.getAuthorityFactory("EPSG");

        int numberOfMissingEntries = 0;
        int numberOfCorrectEntries = 0;
        int numberOfIncorrectEntries = 0;
        int numberOfExceptionEntries = 0;

        Map<String, EpsgPrimeMeridian> epsgCodeToPrimeMeridiansMap = reader.getEpsgCodeToPrimeMeridiansMap();
        for (Map.Entry<String, EpsgPrimeMeridian> currentEntry : epsgCodeToPrimeMeridiansMap.entrySet()) {
            String epsgCode = currentEntry.getKey();
            EpsgPrimeMeridian epsgPrimeMeridian = currentEntry.getValue();
            try {
                PrimeMeridian sisPrimeMeridian = datumFactory.createPrimeMeridian(epsgCode);
                if (sisPrimeMeridian == null) {
                    numberOfMissingEntries++;
                    System.out.println("No match found for prime meridian: " + epsgPrimeMeridian.getEpsgCode() + ":" + epsgPrimeMeridian.getName());
                    continue;
                }
                if (!epsgPrimeMeridian.nameMatches(sisPrimeMeridian)) {
                    System.out.println("Name mismatch for prime meridian: " + epsgPrimeMeridian.getEpsgCode() + ":" + epsgPrimeMeridian.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                if (Math.abs(epsgPrimeMeridian.getGreenwichLongitude() - sisPrimeMeridian.getGreenwichLongitude()) > 1e-7) {
                    System.out.println("Mismatch in greenwich longitude for prime meridian: " + epsgPrimeMeridian.getEpsgCode() + ":" + epsgPrimeMeridian.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }

                System.out.println("Successfully validated prime meridian: " + epsgPrimeMeridian.getEpsgCode() + ":" + epsgPrimeMeridian.getName());
                numberOfCorrectEntries++;
            } catch (Exception ex) {
                System.out.println("Exception occured for prime meridian: " + epsgPrimeMeridian.getEpsgCode() + ":" + epsgPrimeMeridian.getName() + ":" + ex.getMessage());
                numberOfExceptionEntries++;
            }
        }

        System.out.println("Finished Prime Meridian Comparision");
        System.out.println("Number of valid prime meridians found " + numberOfCorrectEntries);
        System.out.println("Number of missing prime meridians found " + numberOfMissingEntries);
        System.out.println("Number of invalid prime meridians found " + numberOfIncorrectEntries);
        System.out.println("Number of prime meridians with exception thrown " + numberOfExceptionEntries);
        if (numberOfIncorrectEntries > 0) {
            Assert.fail("Some prime meridians in EPSG Database did not match what was in Apache SIS");
        }
    }

    @Test
    public void testTransformations() throws Exception {
        EpsgSqlFileReader reader = new EpsgSqlFileReader();
        reader.parseDataFile(new File("C:\\Users\\Michael\\GIGS_TEST\\EPSG_DB_FILES\\EPSG-v9_9_1-PostgreSQL\\PostgreSQL_Data_Script.sql"));

        int numberOfMissingEntries = 0;
        int numberOfCorrectEntries = 0;
        int numberOfIncorrectEntries = 0;
        int numberOfExceptionEntries = 0;

        CoordinateOperationAuthorityFactory opFactory = (CoordinateOperationAuthorityFactory) CRS.getAuthorityFactory("EPSG");

        Map<String, EpsgCoordinateOperation> epsgCodeToTransformationMap = reader.getEpsgCodeToTransformationMap();
        for (Map.Entry<String, EpsgCoordinateOperation> currentEntry : epsgCodeToTransformationMap.entrySet()) {
            String epsgCode = currentEntry.getKey();
            EpsgCoordinateOperation epsgTransformation = currentEntry.getValue();
            try {
                CoordinateOperation sisProjection = opFactory.createCoordinateOperation(epsgCode);
                if (sisProjection == null) {
                    numberOfMissingEntries++;
                    System.out.println("No match found for transforation: " + epsgTransformation.getEpsgCode() + ":" + epsgTransformation.getName());
                    continue;
                }
                if (!epsgTransformation.nameMatches(sisProjection)) {
                    System.out.println("Name mismatch for transforation: " + epsgTransformation.getEpsgCode() + ":" + epsgTransformation.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                numberOfCorrectEntries++;
            } catch (Exception ex) {
                System.out.println("Exception occured for transforation: " + epsgTransformation.getEpsgCode() + ":" + epsgTransformation.getName() + ":" + ex.getMessage());
                numberOfExceptionEntries++;
            }
        }
        System.out.println("Finished Map Projection Comparision");
        System.out.println("Number of valid transformations found " + numberOfCorrectEntries);
        System.out.println("Number of missing transformations found " + numberOfMissingEntries);
        System.out.println("Number of invalid transformations found " + numberOfIncorrectEntries);
        System.out.println("Number of transformations with exception thrown " + numberOfExceptionEntries);
        if (numberOfIncorrectEntries > 0) {
            Assert.fail("Some transformations in EPSG Database did not match what was in Apache SIS");
        }
    }

    @Test
    public void testConversionMapProjection() throws Exception {
        EpsgSqlFileReader reader = new EpsgSqlFileReader();
        reader.parseDataFile(new File("C:\\Users\\Michael\\GIGS_TEST\\EPSG_DB_FILES\\EPSG-v9_9_1-PostgreSQL\\PostgreSQL_Data_Script.sql"));

        int numberOfMissingEntries = 0;
        int numberOfCorrectEntries = 0;
        int numberOfIncorrectEntries = 0;
        int numberOfExceptionEntries = 0;

        CoordinateOperationAuthorityFactory opFactory = (CoordinateOperationAuthorityFactory) CRS.getAuthorityFactory("EPSG");

        Map<String, EpsgCoordinateOperation> epsgCodeToConversionMap = reader.getEpsgCodeToConversionMap();
        for (Map.Entry<String, EpsgCoordinateOperation> currentEntry : epsgCodeToConversionMap.entrySet()) {
            String epsgCode = currentEntry.getKey();
            EpsgCoordinateOperation epsgProjection = currentEntry.getValue();
            try {
                CoordinateOperation sisProjection = opFactory.createCoordinateOperation(epsgCode);
                if (sisProjection == null) {
                    numberOfMissingEntries++;
                    System.out.println("No match found for map projection: " + epsgProjection.getEpsgCode() + ":" + epsgProjection.getName());
                    continue;
                }
                if (!epsgProjection.nameMatches(sisProjection)) {
                    System.out.println("Name mismatch for map projection: " + epsgProjection.getEpsgCode() + ":" + epsgProjection.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                numberOfCorrectEntries++;
            } catch (Exception ex) {
                System.out.println("Exception occured for map projection: " + epsgProjection.getEpsgCode() + ":" + epsgProjection.getName() + ":" + ex.getMessage());
                numberOfExceptionEntries++;
            }
        }

        System.out.println("Finished Map Projection Comparision");
        System.out.println("Number of valid map projections found " + numberOfCorrectEntries);
        System.out.println("Number of missing map projections found " + numberOfMissingEntries);
        System.out.println("Number of invalid map projections found " + numberOfIncorrectEntries);
        System.out.println("Number of map projections with exception thrown " + numberOfExceptionEntries);
        if (numberOfIncorrectEntries > 0) {
            Assert.fail("Some map projections in EPSG Database did not match what was in Apache SIS");
        }
    }

    @Test
    public void testVerticalCRS() throws Exception {
        EpsgSqlFileReader reader = new EpsgSqlFileReader();
        reader.parseDataFile(new File("C:\\Users\\Michael\\GIGS_TEST\\EPSG_DB_FILES\\EPSG-v9_9_1-PostgreSQL\\PostgreSQL_Data_Script.sql"));

        int numberOfMissingEntries = 0;
        int numberOfCorrectEntries = 0;
        int numberOfIncorrectEntries = 0;
        int numberOfExceptionEntries = 0;

        CRSAuthorityFactory authorityFactory = CRS.getAuthorityFactory("EPSG");

        Map<String, EpsgCrs> epsgCodeToVerticalCrsMap = reader.getEpsgCodeToVerticalCrsMap();
        for (Map.Entry<String, EpsgCrs> currentEntry : epsgCodeToVerticalCrsMap.entrySet()) {
            String epsgCode = currentEntry.getKey();
            EpsgCrs epsgVerticalCrs = currentEntry.getValue();
            try {
                VerticalCRS sisVerticalCrs = authorityFactory.createVerticalCRS(epsgCode);
                if (sisVerticalCrs == null) {
                    numberOfMissingEntries++;
                    System.out.println("No match found for vertical crs: " + epsgVerticalCrs.getEpsgCode() + ":" + epsgVerticalCrs.getName());
                    continue;
                }
                if (!epsgVerticalCrs.nameMatches(sisVerticalCrs)) {
                    System.out.println("Name mismatch for vertical crs: " + epsgVerticalCrs.getEpsgCode() + ":" + epsgVerticalCrs.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                VerticalDatum datum = sisVerticalCrs.getDatum();
                if (!epsgCodeMatchesSisObject(epsgVerticalCrs.getDatumCode(), datum)) {
                    System.out.println("Datum mismatch for vertical crs: " + epsgVerticalCrs.getEpsgCode() + ":" + epsgVerticalCrs.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                CoordinateSystem coordinateSystem = sisVerticalCrs.getCoordinateSystem();
                if (!epsgCodeMatchesSisObject(epsgVerticalCrs.getCoordSystemCode(), coordinateSystem)) {
                    System.out.println("CS mismatch for vertical crs: " + epsgVerticalCrs.getEpsgCode() + ":" + epsgVerticalCrs.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }

                numberOfCorrectEntries++;
            } catch (Exception ex) {
                System.out.println("Exception occured for vertical crs: " + epsgVerticalCrs.getEpsgCode() + ":" + epsgVerticalCrs.getName() + ":" + ex.getMessage());
                numberOfExceptionEntries++;
            }
        }

        System.out.println("Finished Vertical CRS Comparision");
        System.out.println("Number of valid vertical crss found " + numberOfCorrectEntries);
        System.out.println("Number of missing vertical crss found " + numberOfMissingEntries);
        System.out.println("Number of invalid vertical crss found " + numberOfIncorrectEntries);
        System.out.println("Number of vertical crss with exception thrown " + numberOfExceptionEntries);
        if (numberOfIncorrectEntries > 0) {
            Assert.fail("Some vertical crss in EPSG Database did not match what was in Apache SIS");
        }
    }

    @Test
    public void testProjectedCRS() throws Exception {
        EpsgSqlFileReader reader = new EpsgSqlFileReader();
        reader.parseDataFile(new File("C:\\Users\\Michael\\GIGS_TEST\\EPSG_DB_FILES\\EPSG-v9_9_1-PostgreSQL\\PostgreSQL_Data_Script.sql"));

        int numberOfMissingEntries = 0;
        int numberOfCorrectEntries = 0;
        int numberOfIncorrectEntries = 0;
        int numberOfExceptionEntries = 0;

        CRSAuthorityFactory authorityFactory = CRS.getAuthorityFactory("EPSG");

        Map<String, EpsgCrs> epsgCodeToProjectedCrsMap = reader.getEpsgCodeToProjectedCrsMap();
        for (Map.Entry<String, EpsgCrs> currentEntry : epsgCodeToProjectedCrsMap.entrySet()) {
            String epsgCode = currentEntry.getKey();
            EpsgCrs epsgProjectedCrs = currentEntry.getValue();
            try {
                ProjectedCRS sisProjectedCrs = authorityFactory.createProjectedCRS(epsgCode);
                if (sisProjectedCrs == null) {
                    numberOfMissingEntries++;
                    System.out.println("No match found for projected crs: " + epsgProjectedCrs.getEpsgCode() + ":" + epsgProjectedCrs.getName());
                    continue;
                }
                if (!epsgProjectedCrs.nameMatches(sisProjectedCrs)) {
                    System.out.println("Name mismatch for projected crs: " + epsgProjectedCrs.getEpsgCode() + ":" + epsgProjectedCrs.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                GeodeticDatum datum = sisProjectedCrs.getDatum();
                if (!epsgCodeMatchesSisObject(epsgProjectedCrs.getDatumCode(), datum)) {
                    System.out.println("Datum mismatch for projected crs: " + epsgProjectedCrs.getEpsgCode() + ":" + epsgProjectedCrs.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                CoordinateSystem coordinateSystem = sisProjectedCrs.getCoordinateSystem();
                if (!epsgCodeMatchesSisObject(epsgProjectedCrs.getCoordSystemCode(), coordinateSystem)) {
                    System.out.println("CS mismatch for projected crs: " + epsgProjectedCrs.getEpsgCode() + ":" + epsgProjectedCrs.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }

                numberOfCorrectEntries++;
            } catch (Exception ex) {
                System.out.println("Exception occured for projected crs: " + epsgProjectedCrs.getEpsgCode() + ":" + epsgProjectedCrs.getName() + ":" + ex.getMessage());
                numberOfExceptionEntries++;
            }
        }

        System.out.println("Finished Projected CRS Comparision");
        System.out.println("Number of valid projected crss found " + numberOfCorrectEntries);
        System.out.println("Number of missing projected crss found " + numberOfMissingEntries);
        System.out.println("Number of invalid projected crss found " + numberOfIncorrectEntries);
        System.out.println("Number of projected crss with exception thrown " + numberOfExceptionEntries);
        if (numberOfIncorrectEntries > 0) {
            Assert.fail("Some projected crss in EPSG Database did not match what was in Apache SIS");
        }
    }

    @Test
    public void testGeodeticCRS() throws Exception {
        EpsgSqlFileReader reader = new EpsgSqlFileReader();
        reader.parseDataFile(new File("C:\\Users\\Michael\\GIGS_TEST\\EPSG_DB_FILES\\EPSG-v9_9_1-PostgreSQL\\PostgreSQL_Data_Script.sql"));

        int numberOfMissingEntries = 0;
        int numberOfCorrectEntries = 0;
        int numberOfIncorrectEntries = 0;
        int numberOfExceptionEntries = 0;

        CRSAuthorityFactory authorityFactory = CRS.getAuthorityFactory("EPSG");

        Map<String, EpsgCrs> epsgCodeToGeodeticCrsMap = reader.getEpsgCodeToGeodeticCrsMap();
        for (Map.Entry<String, EpsgCrs> currentEntry : epsgCodeToGeodeticCrsMap.entrySet()) {
            String epsgCode = currentEntry.getKey();
            EpsgCrs epsgGeodeticCrs = currentEntry.getValue();
            try {
                GeodeticCRS sisGeodeticCrs;
                if (epsgGeodeticCrs.isGeocentric()) {
                    sisGeodeticCrs = authorityFactory.createGeocentricCRS(epsgCode);
                } else {
                    sisGeodeticCrs = authorityFactory.createGeographicCRS(epsgCode);
                }
                if (sisGeodeticCrs == null) {
                    numberOfMissingEntries++;
                    System.out.println("No match found for geodetic crs: " + epsgGeodeticCrs.getEpsgCode() + ":" + epsgGeodeticCrs.getName());
                    continue;
                }
                if (!epsgGeodeticCrs.nameMatches(sisGeodeticCrs)) {
                    System.out.println("Name mismatch for geodetic crs: " + epsgGeodeticCrs.getEpsgCode() + ":" + epsgGeodeticCrs.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                GeodeticDatum datum = sisGeodeticCrs.getDatum();
                if (!epsgCodeMatchesSisObject(epsgGeodeticCrs.getDatumCode(), datum)) {
                    System.out.println("Datum mismatch for geodetic crs: " + epsgGeodeticCrs.getEpsgCode() + ":" + epsgGeodeticCrs.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                CoordinateSystem coordinateSystem = sisGeodeticCrs.getCoordinateSystem();
                if (!epsgCodeMatchesSisObject(epsgGeodeticCrs.getCoordSystemCode(), coordinateSystem)) {
                    System.out.println("CS mismatch for geodetic crs: " + epsgGeodeticCrs.getEpsgCode() + ":" + epsgGeodeticCrs.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }

                numberOfCorrectEntries++;
            } catch (Exception ex) {
                System.out.println("Exception occured for geodetic crs: " + epsgGeodeticCrs.getEpsgCode() + ":" + epsgGeodeticCrs.getName() + ":" + ex.getMessage());
                numberOfExceptionEntries++;
            }
        }

        System.out.println("Finished Geodetic CRS Comparision");
        System.out.println("Number of valid geodetic crss found " + numberOfCorrectEntries);
        System.out.println("Number of missing geodetic crss found " + numberOfMissingEntries);
        System.out.println("Number of invalid geodetic crss found " + numberOfIncorrectEntries);
        System.out.println("Number of geodetic crss with exception thrown " + numberOfExceptionEntries);
        if (numberOfIncorrectEntries > 0) {
            Assert.fail("Some geodetic crss in EPSG Database did not match what was in Apache SIS");
        }
    }

    private boolean epsgCodeMatchesSisObject(String epsgCode, IdentifiedObject object) {
        if (epsgCode == null || epsgCode.isEmpty()) {
            return true;
        }
        if (object == null) {
            return false;
        }
        Set<ReferenceIdentifier> identifiers = object.getIdentifiers();
        for (ReferenceIdentifier currentIdentifier : identifiers) {
            if ("EPSG".equalsIgnoreCase(currentIdentifier.getCodeSpace().trim())) {
                return epsgCode.equals(currentIdentifier.getCode().trim());
            }
        }
        return false;
    }

    @Test
    public void testEllipsoid() throws Exception {
        EpsgSqlFileReader reader = new EpsgSqlFileReader();
        reader.parseDataFile(new File("C:\\Users\\Michael\\GIGS_TEST\\EPSG_DB_FILES\\EPSG-v9_9_1-PostgreSQL\\PostgreSQL_Data_Script.sql"));

        DatumAuthorityFactory datumFactory = (DatumAuthorityFactory) CRS.getAuthorityFactory("EPSG");

        int numberOfMissingEntries = 0;
        int numberOfCorrectEntries = 0;
        int numberOfIncorrectEntries = 0;
        int numberOfExceptionEntries = 0;

        Map<String, EpsgUnit> epsgCodesToUnitsMap = reader.getEpsgCodesToUnitsMap();
        Map<String, EpsgEllipsoid> epsgCodesToEllipsoidsMap = reader.getEpsgCodesToEllipsoidsMap();
        for (Map.Entry<String, EpsgEllipsoid> currentEntry : epsgCodesToEllipsoidsMap.entrySet()) {
            String epsgCode = currentEntry.getKey();
            EpsgEllipsoid epsgEllipsoid = currentEntry.getValue();
            String unitEpsgCode = epsgEllipsoid.getUnitEpsgCode();
            EpsgUnit epsgUnit = null;
            if (unitEpsgCode != null) {
                epsgUnit = epsgCodesToUnitsMap.get(unitEpsgCode);
            }
            try {
                Ellipsoid sisEllipsoid = datumFactory.createEllipsoid(epsgCode);
                if (sisEllipsoid == null) {
                    numberOfMissingEntries++;
                    System.out.println("No match found for ellipsoid: " + epsgEllipsoid.getEpsgCode() + ":" + epsgEllipsoid.getName());
                    continue;
                }
                Unit<Length> sisUnit = sisEllipsoid.getAxisUnit();
                if (!epsgEllipsoid.nameMatches(sisEllipsoid)) {
                    System.out.println("Name mismatch for ellipsoid: " + epsgEllipsoid.getEpsgCode() + ":" + epsgEllipsoid.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                double epsgSemiMajorAxis = epsgEllipsoid.getSemiMajorAxis();
                double sisSemiMajorAxis = sisEllipsoid.getSemiMajorAxis();
                if (epsgEllipsoid.hasValidSemiMajorAxis() && !validateEllipsoidAxisValues(epsgSemiMajorAxis, epsgUnit, sisSemiMajorAxis, sisUnit)) {
                    System.out.println("Mismatch in semi major axis for ellipsoid: " + epsgEllipsoid.getEpsgCode() + ":" + epsgEllipsoid.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                double epsgSemiMinorAxis = epsgEllipsoid.getSemiMinorAxis();
                double sisSemiMinorAxis = sisEllipsoid.getSemiMinorAxis();
                if (epsgEllipsoid.hasValidSemiMinorAxis() && !validateEllipsoidAxisValues(epsgSemiMinorAxis, epsgUnit, sisSemiMinorAxis, sisUnit)) {
                    System.out.println("Mismatch in semi minor axis for ellipsoid: " + epsgEllipsoid.getEpsgCode() + ":" + epsgEllipsoid.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }
                double epsgInvFlattening = epsgEllipsoid.getInvFlattening();
                double sisInvFlattening = sisEllipsoid.getInverseFlattening();
                if (epsgEllipsoid.hasValidInvFlattening() && Math.abs(epsgInvFlattening - sisInvFlattening) > 1E-7) {
                    System.out.println("Mismatch in inverse flattening for ellipsoid: " + epsgEllipsoid.getEpsgCode() + ":" + epsgEllipsoid.getName());
                    numberOfIncorrectEntries++;
                    continue;
                }

                System.out.println("Successfully validated ellipsoid: " + epsgEllipsoid.getEpsgCode() + ":" + epsgEllipsoid.getName());
                numberOfCorrectEntries++;
            } catch (Exception ex) {
                System.out.println("Exception occured for ellipsoid: " + epsgEllipsoid.getEpsgCode() + ":" + epsgEllipsoid.getName() + ":" + ex.getMessage());
                numberOfExceptionEntries++;
            }
        }

        System.out.println("Finished Ellipsoid Comparision");
        System.out.println("Number of valid ellipsoids found " + numberOfCorrectEntries);
        System.out.println("Number of missing ellipsoids found " + numberOfMissingEntries);
        System.out.println("Number of invalid ellipsoids found " + numberOfIncorrectEntries);
        System.out.println("Number of ellipsoids with exception thrown " + numberOfExceptionEntries);
        if (numberOfIncorrectEntries > 0) {
            Assert.fail("Some ellipsoids in EPSG Database did not match what was in Apache SIS");
        }
    }

    private boolean validateEllipsoidAxisValues(double epsgValue, EpsgUnit unit, double sisValue, Unit sisUnit) {
        double epsgComparisonValue = epsgValue;
        if (unit != null && unit.supportsConversionToBaseUnit()) {
            epsgComparisonValue = unit.convertToBaseUnit(epsgValue);
        }
        double comparisonSisValue = sisValue;
        if (sisUnit != null && sisUnit.getSystemUnit() != null) {
            UnitConverter converter = sisUnit.getConverterTo(sisUnit.getSystemUnit());
            comparisonSisValue = converter.convert(sisValue);
        }
        return Math.abs(epsgComparisonValue - comparisonSisValue) < 1E-7;
    }

    //@Test
    public void testUnits() throws Exception {
        EpsgSqlFileReader reader = new EpsgSqlFileReader();
        reader.parseDataFile(new File("C:\\Users\\Michael\\GIGS_TEST\\EPSG_DB_FILES\\EPSG-v9_9_1-PostgreSQL\\PostgreSQL_Data_Script.sql"));
        CSAuthorityFactory csAuthorityFactory = (CSAuthorityFactory) CRS.getAuthorityFactory("EPSG");

        int numberOfMissingNameEntries = 0;
        int numberOfMissingEntries = 0;
        int numberOfCorrectEntries = 0;
        int numberOfIncorrectEntries = 0;
        int numberOfExceptionEntries = 0;

        Map<String, EpsgUnit> epsgCodesToUnitsMapo = reader.getEpsgCodesToUnitsMap();
        for (Map.Entry<String, EpsgUnit> currentEntry : epsgCodesToUnitsMapo.entrySet()) {
            String epsgCode = currentEntry.getKey();
            EpsgUnit epsgUnit = currentEntry.getValue();
            String epsgSystemUnitEpsgCode = epsgUnit.getSystemUnitEpsgCode();
            EpsgUnit epsgSystemUnit = null;
            if (epsgSystemUnitEpsgCode != null) {
                epsgSystemUnit = epsgCodesToUnitsMapo.get(epsgSystemUnitEpsgCode);
            }
            try {

                Unit sisUnit = csAuthorityFactory.createUnit(epsgCode);
                if (sisUnit == null) {
                    numberOfMissingEntries++;
                    System.out.println("No match found for unit: " + epsgCode + ":" + epsgUnit.getName());
                    continue;
                }
                if (sisUnit.getName() == null) {
                    numberOfMissingNameEntries++;
                    System.out.println("No name found for unit: " + epsgCode + ":" + epsgUnit.getName());
                    continue;
                }
                if (!epsgUnit.nameMatches(sisUnit.getName()) && !epsgUnit.nameMatches(sisUnit.getSymbol())) {
                    numberOfIncorrectEntries++;
                    System.out.println("Name mismatch for unit: " + epsgCode + ":" + epsgUnit.getName());
                    continue;
                }
                Unit sisSystemUnit = sisUnit.getSystemUnit();
                if (sisSystemUnit != null && epsgSystemUnit != null && !epsgSystemUnit.nameMatches(sisSystemUnit.getName())
                        && !epsgSystemUnit.nameMatches(sisSystemUnit.getSymbol())) {
                    numberOfIncorrectEntries++;
                    System.out.println("Name mismatch for for system unit of : " + epsgCode + ":" + epsgUnit.getName());
                    //continue;
                }

                //do base unit  conversion test
                if (epsgUnit.supportsConversionToBaseUnit()) {
                    UnitConverter converter = sisUnit.getConverterTo(sisSystemUnit);
                    double actualConversion = converter.convert(1);
                    double expectedConverions = epsgUnit.convertToBaseUnit(1);
                    if (Math.abs(actualConversion - expectedConverions) > 1E-7) {
                        numberOfIncorrectEntries++;
                        System.out.println("Conversion value: " + actualConversion + " is not within treshold of expected value: " + expectedConverions);

                        continue;
                    }
                }
                numberOfCorrectEntries++;
                System.out.println("Successfully validated unit " + epsgUnit.getEpsgCode() + ":" + epsgUnit.getName());
            } catch (Exception ex) {
                System.out.println("Exception occured for unit " + epsgUnit.getEpsgCode() + ":" + epsgUnit.getName() + ":" + ex.getMessage());
                numberOfExceptionEntries++;
            }
        }
        System.out.println("Finished Unit Comparision");
        System.out.println("Number of valid units found " + numberOfCorrectEntries);
        System.out.println("Number of missing units found " + numberOfMissingEntries);
        System.out.println("Number of invalid units found " + numberOfIncorrectEntries);
        System.out.println("Number of units with missing names found " + numberOfMissingNameEntries);
        System.out.println("Number of units with exception thrown " + numberOfExceptionEntries);
        if (numberOfIncorrectEntries > 0) {
            Assert.fail("Some units in EPSG Database did not match what was in Apache SIS");
        }
    }

}
