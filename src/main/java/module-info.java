module lk.ijse.hibernate.serenitymentalhealththerapycenter.simplechatapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens lk.ijse.inp.simplechatapplication.controller to javafx.fxml;
    exports lk.ijse.inp.simplechatapplication;

}