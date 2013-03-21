package cache;

public interface ICache {
    public void reset() throws Exception;

    public String get(String key) throws Exception;

    public void set(String key, String value) throws Exception;
}
