module code {
    requires javafx.controls;
    requires javafx.fxml;

    opens code to javafx.fxml;
    exports code;
}
