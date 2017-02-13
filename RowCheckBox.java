import javax.swing.JCheckBox;
import java.awt.*;

public class RowCheckBox extends JCheckBox {

    private int dataId = 0;
    private int index  = 0;

    public RowCheckBox (String textCheckBox, boolean selected, int id, int index) {

        super(textCheckBox, selected);
        this.setDataId(id);
        this.setIndex(index);
        this.setBackground(new Color(219, 244, 249));

    }

    public void setDataId(int id) {
        this.dataId = id;
    }

    public int getDataId() {
        return this.dataId;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

}
