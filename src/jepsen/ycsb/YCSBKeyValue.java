package jepsen.ycsb;

public class YCSBKeyValue {
    Integer key;
    Integer value;

    public YCSBKeyValue(Integer key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    public static void main(String[] args) {
        YCSBKeyValue kv = new YCSBKeyValue(1, null);
    }
}
