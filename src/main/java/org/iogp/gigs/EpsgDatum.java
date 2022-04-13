package org.iogp.gigs;

import java.text.Normalizer;
import java.util.regex.Pattern;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.datum.Datum;

public class EpsgDatum {

    private final String epsgCode;
    private final String name;
    private final String ellipsoidEpsgCode;
    private final String primeMeridianEpsgCode;
    private final String areaOfUseEpsgCode;
    private final String[] aliases;
    private final boolean deprecated;

    public EpsgDatum(String[] tokens, String[] aliases) {
        this.epsgCode = tokens[0];
        this.name = tokens[1];
        this.ellipsoidEpsgCode = tokens[5];
        this.primeMeridianEpsgCode = tokens[6];
        this.areaOfUseEpsgCode = tokens[7];
        if (tokens[14] != null && tokens[4].equals("1")) {
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

    public String getEllipsoidEpsgCode() {
        return ellipsoidEpsgCode;
    }

    public String getPrimeMeridianEpsgCode() {
        return primeMeridianEpsgCode;
    }

    public String getAreaOfUseEpsgCode() {
        return areaOfUseEpsgCode;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public boolean nameMatches(Datum datum) {
        ReferenceIdentifier riName = datum.getName();
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
        String sisEnglishName = removeAccents(sisName);
        if (sisEnglishName.equals(name)) {
            return true;
        }
        return false;
    }

    private String removeAccents(String originalString) {
        String nfdNormalizedString = Normalizer.normalize(originalString, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String removedAccents = pattern.matcher(nfdNormalizedString).replaceAll("");
        return removedAccents.replace("'", "");
    }
}
