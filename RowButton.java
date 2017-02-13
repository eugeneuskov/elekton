import javax.swing.JButton;


public class RowButton extends JButton {

    private int dataId = 0;
    private int index  = 0;

    public RowButton (String textButton, int id, int index) {

        super(textButton);
        this.setDataId(id);
        this.setIndex(index);

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
