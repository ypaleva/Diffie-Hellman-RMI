public class CipherKey {

    private Integer aValue;
    private Integer key;

    public CipherKey(Integer aValue, Integer key) {
        this.aValue = aValue;
        this.key = key;
    }

    public Integer getaValue() {
        return aValue;
    }

    public void setaValue(Integer aValue) {
        this.aValue = aValue;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "CipherKey{" + "key=" + key + '}';
    }
}
