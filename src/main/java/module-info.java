module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens client to javafx.fxml;
    exports client;

    opens components.login_password to javafx.fxml;
    exports components.login_password;

    opens components.signin to javafx.fxml;
    exports components.signin;

    opens components.msg_cloud to javafx.fxml;
    exports components.msg_cloud;

    requires msg_processor;
    requires lombok;
    requires com.fasterxml.jackson.databind;
    requires java.logging;

    exports archive;
    exports components.chat_room;

    opens shadow;
    opens components.chat_room;
    opens components.founded_person;
}