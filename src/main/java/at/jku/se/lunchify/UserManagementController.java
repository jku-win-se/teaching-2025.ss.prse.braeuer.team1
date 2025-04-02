package at.jku.se.lunchify;

import java.io.IOException;

public class UserManagementController {

    public void onNewUserButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("user-creation-view.fxml");
    }

    public void onEditUserButtonClick() {
    }

    //Klick auf Spalte soll Änderungs-View aufrufen

}