import javax.swing.JTextArea;

public class RowArea extends JTextArea {
    private int dataId = 0;

    public RowArea(int id) {

        super(3,1);
        this.setDataId(id);

    }

    public void setDataId(int id) {
        this.dataId = id;
    }

    public int getDataId() {
        return this.dataId;
    }
}
