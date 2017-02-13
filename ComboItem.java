
public class ComboItem {

    private int key;
    private String value;

    public ComboItem(int key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
    }

    public int getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }


}
