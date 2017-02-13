import javax.swing.JLabel;


public class RowLabel extends JLabel {

    private int dataId = 0;

    public RowLabel(String text, int id) {

        super(text);
        this.setDataId(id);

    }

    public void setDataId(int id) {
        this.dataId = id;
    }

    public int getDataId() {
        return this.dataId;
    }

}
