package Controllers;

import DBManager.DBManager;
import DatabaseSearch.AppRecord;
import DatabaseSearch.UserRecord;
import UserAccounts.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ethan on 4/26/2017.
 */
public class menuBarController extends UIController {

    //Variables to hold the appropriate FXML buttons and fields

    private Boolean onSearchPage = false; //Set this to true if we are switching to be on a search page to enable the reactive search
    private int searchType; //can be 0: approved forms, 1: pending apps, 2: users and this will determine the type of search called

    @FXML
    public Button backButton;
    @FXML
    public Button searchButton;
    @FXML
    public Button loginButton;
    @FXML
    public Button aboutButton;
    @FXML
    public TextField searchBar;
    @FXML
    public Label menuTitle;

    DBManager db = new DBManager();

    public Button getLoginButton() {
        return this.loginButton;
    }

    public void setOnSearchPage(Boolean b) {
        this.onSearchPage = b;
    }

    public Boolean getOnSearchPage() {
        return this.onSearchPage;
    }

    public void setSearchType(int type) {
        this.searchType = type;
    }

    public int getSearchType() {
        return this.searchType;
    }


    @FXML
    private void barSetDisplayToMainPage() throws IOException {
        menuBarSingleton.getInstance().getMenuBarController().menuTitle.setText("WELCOME TO THE TTB DATABASE SYSTEM");
        super.returnToMainPage();
    }


    //todo set search type and global search flags
    private void setDisplayToSearchResultsPage(ObservableList<AppRecord> list) {
        BorderPane pane = main.getBorderPane();
        Stage stage;
        stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();

        pane.setTop(menuBarSingleton.getInstance().getBar());
        menuBarSingleton.getInstance().getMenuBarController().menuTitle.setText("Label Search");
        try {
            searchPageController controller = menuBarSingleton.getInstance().getSearchPageController();
            controller.resultsTable.setItems(menuBarSingleton.getInstance().getGlobalData().getObservableListApp());
            controller.resultsTable.refresh();
            ScrollPane searchPage = menuBarSingleton.getInstance().getSearchPagePane();
            pane.setLeft(searchPage);
            stage.setScene(pane.getScene());
            stage.show();
            setSearchType(0);
            setOnSearchPage(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loginAction() throws IOException {
        String loginStatus = loginButton.getText();
        setOnSearchPage(false);
        if (!currentUserLabel.getText().equalsIgnoreCase("Not Logged In")) {
            System.out.println("Trying to logout");
            currentUserLabel.setText("Not Logged In");
            loginButton.setText("LOGIN");
            menuBarSingleton.getInstance().getGlobalData().setUserInformation(null);
            menuBarSingleton.getInstance().getMenuBarController().menuTitle.setText("WELCOME TO THE TTB DATABASE SYSTEM");
            super.returnToMainPage();
        } else {
            System.out.println("Trying to log in");
            BorderPane borderPane = main.getBorderPane();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("loginPage.fxml"));
            AnchorPane anchorPane = loader.load();
            Stage stage;
            stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = borderPane.getScene();
            stage.setScene(scene);
            borderPane.setTop(menuBarSingleton.getInstance().getBar());
            menuBarSingleton.getInstance().getMenuBarController().menuTitle.setText("User Login");
            borderPane.setLeft(anchorPane);
            stage.show();
        }
    }


    @FXML
    protected void barSetDisplayToAboutPage() throws IOException {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            AnchorPane newWindow = loader.load(getClass().getResource("aboutPage.fxml"));
            Scene scene = new Scene(newWindow, 515, 800);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.setScene(scene);
            stage.setFullScreen(false);
            stage.getScene().setRoot(newWindow);
            stage.show();
            aboutPageController controller = loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchFromOffPage() {
        //Set all variables equal to input data
        String searchBarContent = searchBar.getText();
        try {
            String params = " WHERE STATUS = 'Accepted' AND";
            params += " (UPPER(BRAND_NAME) LIKE UPPER('%" + searchBarContent + "%') OR UPPER(FANCIFUL_NAME) LIKE UPPER('%" + searchBarContent + "%'))";

            ArrayList<ArrayList<String>> searchParams = new ArrayList<>();

            ObservableList<AppRecord> arr = db.findLabels(searchParams, params);

            menuBarSingleton.getInstance().getGlobalData().setObservableListApp(arr);
            System.out.println("ARR IS " + menuBarSingleton.getInstance().getGlobalData().getObservableListApp());

            setDisplayToSearchResultsPage(arr);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not build a query from search criteria.");
        }
    }

    @FXML
    private void searchFromOnPagePendingForms() {
        //Set all variables equal to input data
        String searchBarContent = searchBar.getText();
        try {
            String params = " WHERE STATUS = 'Pending' AND";
            params += " (UPPER(BRAND_NAME) LIKE UPPER('%" + searchBarContent + "%') OR UPPER(FANCIFUL_NAME) LIKE UPPER('%" + searchBarContent + "%'))";

            ArrayList<ArrayList<String>> searchParams = new ArrayList<>();

            ObservableList<AppRecord> arr = db.findLabelsSA(searchParams, params);

            menuBarSingleton.getInstance().getGlobalData().setObservableListApp(arr);
            System.out.println("ARR IS " + menuBarSingleton.getInstance().getGlobalData().getObservableListApp());
            superAgentPendingApplicationSearchController controller = menuBarSingleton.getInstance().getSuperAgentPendingApplicationSearchController();
            controller.displayData(arr);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not build a query from search criteria.");
        }
    }

    @FXML
    private void searchFromOnPageApplications() {
        //Set all variables equal to input data
        String searchBarContent = searchBar.getText();
        try {
            String params = " WHERE STATUS = 'Pending' AND AGENT_ID = \'" + menuBarSingleton.getInstance().getGlobalData().getUserInformation().getUid() + "\' AND ";
            params += " (UPPER(BRAND_NAME) LIKE UPPER('%" + searchBarContent + "%') OR UPPER(FANCIFUL_NAME) LIKE UPPER('%" + searchBarContent + "%'))";

            ArrayList<ArrayList<String>> searchParams = new ArrayList<>();

            ObservableList<AppRecord> arr = db.findLabels(searchParams, params);

            menuBarSingleton.getInstance().getGlobalData().setObservableListApp(arr);
            System.out.println("ARR IS " + menuBarSingleton.getInstance().getGlobalData().getObservableListApp());
            searchPageController controller = menuBarSingleton.getInstance().getSearchPageController();
            controller.displayData(arr);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not build a query from search criteria.");
        }
    }

    @FXML
    private void searchFromOnPageForms() {
        //Set all variables equal to input data
        String searchBarContent = searchBar.getText();
        try {
            String params = " WHERE STATUS = 'Accepted' AND";
            params += " (UPPER(BRAND_NAME) LIKE UPPER('%" + searchBarContent + "%') OR UPPER(FANCIFUL_NAME) LIKE UPPER('%" + searchBarContent + "%'))";

            ArrayList<ArrayList<String>> searchParams = new ArrayList<>();

            ObservableList<AppRecord> arr = db.findLabels(searchParams, params);

            menuBarSingleton.getInstance().getGlobalData().setObservableListApp(arr);
            System.out.println("ARR IS " + menuBarSingleton.getInstance().getGlobalData().getObservableListApp());
            searchPageController controller = menuBarSingleton.getInstance().getSearchPageController();
            controller.displayData(arr);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not build a query from search criteria.");
        }
    }

    @FXML
    private void searchFromOnPageUsers() {
        //Set all variables equal to input data
        String searchBarContent = searchBar.getText();
        try {
            String options = "UPPER(LAST_NAME) LIKE UPPER('%" + searchBarContent + "%') OR UPPER(USERNAME) LIKE UPPER('%" + searchBarContent + "%')";


            ObservableList<UserRecord> arr = db.searchUsers(options);

            menuBarSingleton.getInstance().getGlobalData().setObservableListUser(arr);
            System.out.println("ARR IS " + menuBarSingleton.getInstance().getGlobalData().getObservableListUser());
            superAgentSearchUsersController controller = menuBarSingleton.getInstance().getSuperAgentSearchUsersController();
            controller.displayData(arr);
            controller.refreshView();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not build a query from search criteria.");
        }
    }

    @FXML
    public ObservableList<UserRecord> searchUsers(boolean usernameFilter, boolean emailFilter, boolean firstNameFilter, boolean lastNameFilter, boolean agentFilter, boolean superAgentFilter, boolean applicantFilter) {
        // build a query
        String searchText = searchBar.getText();
        String options = "";
        if (searchText != null && !searchText.isEmpty()) {
            if (usernameFilter) {
                if (!options.isEmpty()) {
                    options = options.concat(" and ");
                }
                options = options.concat("username like '%" + searchText + "%'");
            }
            if (emailFilter) {
                if (!options.isEmpty()) {
                    options = options.concat(" and ");
                }
                options = options.concat("email like '%" + searchText + "%'");
            }
            if (firstNameFilter) {
                if (!options.isEmpty()) {
                    options = options.concat(" and ");
                }
                options = options.concat("first_name like '%" + searchText + "%'");
            }
            if (lastNameFilter) {
                if (!options.isEmpty()) {
                    options = options.concat(" and ");
                }
                options = options.concat("last_name like '%" + searchText + "%'");
            }
        }
        if (agentFilter) {
            if (!options.isEmpty()) {
                options = options.concat(" and ");
            }
            options = options.concat("authentication=" + 2);
        }
        if (superAgentFilter) {
            if (!options.isEmpty()) {
                options = options.concat(" and ");
            }
            options = options.concat("authentication=" + 3);
        }
        if (applicantFilter) {
            if (!options.isEmpty()) {
                options = options.concat(" and ");
            }
            options = options.concat("authentication=" + 1);
        }
        ObservableList<UserRecord> userList = FXCollections.observableArrayList();
        userList.clear();
        userList = db.searchUsers(options);
        System.out.println(userList.size());
        // display users in the table view
        return userList;
    }

    @FXML
    public ObservableList<AppRecord> superAgentSimpleSearch(boolean isMalt, boolean isWine, boolean isSpirit) {
        try {
            //Set all variables equal to input data
            String searchBarContent = searchBar.getText();

            boolean firstCheck = false;

            String params = " WHERE STATUS = 'Pending' ";

            if (isMalt || isSpirit || isWine) {

                params += "AND (ALCOHOL_TYPE = ";

                if (isWine) {
                    params += "'Wine'";
                    firstCheck = true;
                }

                if (isSpirit && !firstCheck) {
                    params += "'Distilled Spirits'";
                    firstCheck = true;
                } else if (isSpirit && firstCheck) {
                    params += " OR ALCOHOL_TYPE = 'Distilled Spirits'";
                }

                if (isMalt && !firstCheck) {
                    params += "'Malt Beverages'";
                    firstCheck = true;
                } else if (isMalt && firstCheck) {
                    params += " OR ALCOHOL_TYPE = 'Malt Beverages'";
                }
                params += ")";
            } else {
                return null;
            }

            params += " AND (UPPER(BRAND_NAME) LIKE UPPER('%" + searchBarContent + "%') OR UPPER(FANCIFUL_NAME) LIKE UPPER('%" + searchBarContent + "%'))";

            ArrayList<ArrayList<String>> searchParams = new ArrayList<>();

            ObservableList<AppRecord> arr = db.findLabelsSA(searchParams, params);
            System.out.println("ARR IS " + arr);
            main.userData.setObservableListApp(arr);
            System.out.println("USERDATA IS " + main.userData.getObservableListApp());

            return arr;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not build a query from search criteria.");
            return null;
        }
    }

    @FXML
    public ObservableList<AppRecord> simpleSearch(boolean isMalt, boolean isWine, boolean isSpirit) {
        try {
            //Set all variables equal to input data
            String searchBarContent = searchBar.getText();

            boolean firstCheck = false;

            String params = " WHERE STATUS = 'Accepted' ";

            if (isMalt || isSpirit || isWine) {

                params += "AND (ALCOHOL_TYPE = ";

                if (isWine) {
                    params += "'Wine'";
                    firstCheck = true;
                }

                if (isSpirit && !firstCheck) {
                    params += "'Distilled Spirits'";
                    firstCheck = true;
                } else if (isSpirit && firstCheck) {
                    params += " OR ALCOHOL_TYPE = 'Distilled Spirits'";
                }

                if (isMalt && !firstCheck) {
                    params += "'Malt Beverages'";
                    firstCheck = true;
                } else if (isMalt && firstCheck) {
                    params += " OR ALCOHOL_TYPE = 'Malt Beverages'";
                }
                params += ")";
            } else {
                return null;
            }

            params += " AND (UPPER(BRAND_NAME) LIKE UPPER('%" + searchBarContent + "%') OR UPPER(FANCIFUL_NAME) LIKE UPPER('%" + searchBarContent + "%'))";

            ArrayList<ArrayList<String>> searchParams = new ArrayList<>();

            ObservableList<AppRecord> arr = db.findLabels(searchParams, params);
            System.out.println("ARR IS " + arr);
            menuBarSingleton.getInstance().getGlobalData().setObservableListApp(arr);
            System.out.println("USERDATA IS " + main.userData.getObservableListApp());

            return arr;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not build a query from search criteria.");
            return null;
        }
    }

    /**
     * This is the onkeypressed function for the search bar, it checks the onSearchPage flag to see if we want
     * to dynamically search and what type, of search
     * searchType can be 0:approved forms, 1: all applications, 2: users
     */
    @FXML
    public void searchProgram() {
        if (this.onSearchPage) {
            if (this.searchType == 0) {
                System.out.println("searching forms");
                searchFromOnPageForms();
                //call userSearch
            } else if (this.searchType == 1) {
                System.out.println("searching pending applications");
                searchFromOnPagePendingForms();
                //call applicationSearch
            } else if (this.searchType == 2) {
                System.out.println("searching users");
                searchFromOnPageUsers();
                //call form search
            } else if (this.searchType == 3){
                searchFromOnPageApplications();
            }

        } else {

        }
    }
}
