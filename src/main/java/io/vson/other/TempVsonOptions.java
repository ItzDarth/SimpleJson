
package io.vson.other;


public class TempVsonOptions {

    private IVsonProvider[] dsf;
    private boolean legacyRoot;

    public TempVsonOptions() {
        dsf=new IVsonProvider[0];
        legacyRoot=true;
    }

    public IVsonProvider[] getDsfProviders() { return dsf.clone(); }

    public void setDsfProviders(IVsonProvider[] value) { dsf=value.clone(); }

    public boolean getParseLegacyRoot() { return legacyRoot; }

    public void setParseLegacyRoot(boolean value) { legacyRoot=value; }

    @Deprecated
    public boolean getEmitRootBraces() { return true; }

    @Deprecated
    public void setEmitRootBraces(boolean value) { }

}
