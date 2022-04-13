package org.iogp.gigs;

public class EpsgAlias {

    private final EpsgAliasIdentifier identifier;
    private final String alias;

    public EpsgAlias(EpsgAliasIdentifier identifier, String alias) {
        this.identifier = identifier;
        this.alias = alias;
    }

    public EpsgAliasIdentifier getIdentifier() {
        return identifier;
    }

    public String getAlias() {
        return alias;
    }

}
