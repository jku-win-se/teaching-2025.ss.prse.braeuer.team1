package at.jku.se.lunchify;

import java.io.IOException;

public class UserManagementController {

    public void onNewUserButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("user-creation-view.fxml");
    }

    public void onEditUserButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("user-editing-view.fxml");
    }
}