import javafx.application.Application;
import javafx.stage.Stage;

public class Elekton extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Users startForm = new Users();
    }

    public static void main(String[] args) {
        launch(args);
    }

}