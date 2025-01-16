package code;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    // 地圖長寬參數
    public static final double MAP_WIDTH = 1050;
    public static final double MAP_HEIGHT = 600;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene1.fxml"));
        Scene scene = new Scene(loader.load(), MAP_WIDTH, MAP_HEIGHT);

        // // 將地圖長寬參數傳遞給控制器
        // Scene1Controller controller = loader.getController();

        stage.setTitle("Game Map");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
