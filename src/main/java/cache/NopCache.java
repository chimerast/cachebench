package cache;

public class NopCache implements ICache {

    @Override
    public void reset() throws Exception {
    }

    @Override
    public String get(String key) throws Exception {
        return "";
    }

    @Override
    public void set(String key, String value) throws Exception {
    }
}
