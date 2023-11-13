package com.example.dictionaryofficial;

import com.example.APIGoogle.AudioGoogleAPI;
import com.example.APIGoogle.GoogleTransAPI;
import com.example.cilent.FavouriteWord;
import com.example.commandLine.DBConnect;
import com.example.commandLine.DictionaryCommandline;
import com.example.commandLine.DictionaryManagement;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.Scene;

import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class IntoProgramController implements Initializable {

    private String searchWord;
    @FXML
    private Button logoutButton;
    @FXML
    private AnchorPane basePane;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> suggestList;
    @FXML
    private Button searchBar;
    @FXML
    private Button search_button;
    @FXML
    private Button edit_button;
    @FXML
    private Button game_button;
    @FXML
    private Button setting_button;
    @FXML
    private Button speaker_button_1;
    @FXML
    private Button speaker_button_2;
    @FXML
    private Button favour_button;
    @FXML
    private ImageView favour_star;
    @FXML
    private ImageView border_star;
    @FXML
    private Label taskbar;
    @FXML
    private WebView searchResult;
    @FXML
    private ListView<String> historyList;

    private static List<String> historyContainer = new ArrayList<>();
    private Vector<String> settingFont ;
    public static TextField static_searchField ;

    //-----------------------------------------------------------------------------------------------------//
    public void setFont(String font, double fontSize) {
        searchField.setFont(new Font(font,fontSize));
    }


    public void removeWord() {
        searchWord = searchField.getText();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete a word");
        alert.setHeaderText("Confirmation: Are you sure to delete this word?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean check = DictionaryManagement.removeFromFront(searchWord, DBConnect.connectDB());
            searchField.setText("");
            if (check) {
                searchResult.getEngine().loadContent("Delete Successfully!");
            } else {
                searchResult.getEngine().loadContent("Some errors have occurred!");
            }
        }
    }

    public void logout(ActionEvent event) throws IOException {
        ManageScene.logout(BaseController.getStage(),basePane);
    }


    /* click button to start search */
    public void clickToSearch(ActionEvent event) throws IOException {
        searchWord = searchField.getText();

        addToHistory(searchWord);

        favour_button.setVisible(false);
        if (DictionaryCommandline.isExist(searchWord, DBConnect.connectDB())) {
            favour_button.setVisible(true);
            if (FavouriteWord.IsFavour(searchWord)) {
                favour_button.setGraphic(favour_star);
                favour_star.setVisible(true);
                border_star.setVisible(false);
            } else {
                favour_button.setGraphic(border_star);
                border_star.setVisible(true);
                favour_star.setVisible(false);
            }
        }
        String result = DictionaryCommandline.getWord(searchWord, DBConnect.connectDB());
//        String result = "<h1>" + GoogleTransAPI.translate(searchWord, GoogleTransAPI.LANGUAGE.ENGLISH, GoogleTransAPI.LANGUAGE.VIETNAMESE) + "</h1>";
        searchResult.getEngine().loadContent(result);

        suggestList.setVisible(false);
    }





    public void initHistory() throws IOException {
        File file = new File("database/history.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String word = "";
        historyContainer = new ArrayList<>();
        while ((word = br.readLine()) != null) {
            historyContainer.add(word);
            historyList.getItems().add(word);
        }
        br.close();
    }

    public void saveHistory() throws IOException {
        FileWriter fw = null;
        try {
            fw = new FileWriter("database/history.txt");
            for (String s : historyContainer) {
                fw.write(s + '\n');
            }
        } catch (Exception e) {
            System.out.println("Cant write file!");
        } finally {
            if (fw != null) fw.close();
        }
    }

    public void addToHistory(String word) {
        while (historyContainer.size() >= 50) {
            historyContainer.remove(49);
            historyList.getItems().remove(49);

        }
        historyContainer.add(0, word);
        historyList.getItems().add(0, word);
    }

    public void UKAudio(ActionEvent event) throws IOException, JavaLayerException {
        try {
            AudioGoogleAPI.getInstance().play(AudioGoogleAPI.getInstance().getAudio(searchWord, "en-UK"));
        }
        catch (IOException e) {
            System.out.println("No word was selected");
        }
    }

    public void USAudio(ActionEvent event) throws IOException, JavaLayerException {
        try {
            AudioGoogleAPI.getInstance().play(AudioGoogleAPI.getInstance().getAudio(searchWord, "en-US"));
        }
        catch (IOException e) {
            System.out.println("No word was selected");
        }
    }

    public void favourHandler(ActionEvent event) {
        if (FavouriteWord.IsFavour(searchWord)) {
            FavouriteWord.removeFavour(searchWord);
            favour_button.setGraphic(border_star);
            favour_star.setVisible(false);
            border_star.setVisible(true);
        } else {
            FavouriteWord.addFavourite(searchWord);
            favour_button.setGraphic(favour_star);
            border_star.setVisible(false);
            favour_star.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        ListView<String> suggestList = new ListView<>();
//        static_searchField = searchField;

        //init history
        try {
            initHistory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        suggestList.setVisible(false);
        favour_button.setVisible(false);
        favour_star.setVisible(false);
        border_star.setVisible(false);
        IntoProgramController tmp = new IntoProgramController();

        // setting font and font Size
        setting();


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            suggestList.getItems().clear();
            suggestList.getItems().addAll(DictionaryCommandline.suggestWord(newValue, DBConnect.connectDB()));
            suggestList.setVisible(true);
            if (newValue.isEmpty()) {
                suggestList.setVisible(false);
            }
        });

        // click for autocomplete
        suggestList.setOnMouseClicked(event -> {
            String selectedSuggestion = suggestList.getSelectionModel().getSelectedItem();
            if (selectedSuggestion != null) {
                searchField.setText(selectedSuggestion);
            }

            searchWord = searchField.getText();
            addToHistory(searchWord);
            favour_button.setVisible(false);
            if (DictionaryCommandline.isExist(searchWord, DBConnect.connectDB())) {
                favour_button.setVisible(true);
                if (FavouriteWord.IsFavour(searchWord)) {
                    favour_button.setGraphic(favour_star);
                    favour_star.setVisible(true);
                    border_star.setVisible(false);
                } else {
                    favour_button.setGraphic(border_star);
                    border_star.setVisible(true);
                    favour_star.setVisible(false);
                }
            }
            String result = DictionaryCommandline.getWord(searchWord, DBConnect.connectDB());
            searchResult.getEngine().loadContent(result);
            suggestList.setVisible(false);
        });

        // Search by history
        historyList.setOnMouseClicked(event -> {
            String selectedSuggestion = historyList.getSelectionModel().getSelectedItem();
            if (selectedSuggestion != null) {
                searchField.setText(selectedSuggestion);
            }

            searchWord = searchField.getText();
            addToHistory(searchWord);

            favour_button.setVisible(false);
            if (DictionaryCommandline.isExist(searchWord, DBConnect.connectDB())) {
                favour_button.setVisible(true);
                if (FavouriteWord.IsFavour(searchWord)) {
                    favour_button.setGraphic(favour_star);
                    favour_star.setVisible(true);
                    border_star.setVisible(false);
                } else {
                    favour_button.setGraphic(border_star);
                    border_star.setVisible(true);
                    favour_star.setVisible(false);
                }
            }
            String result = DictionaryCommandline.getWord(searchWord, DBConnect.connectDB());
            searchResult.getEngine().loadContent(result);
            suggestList.setVisible(false);
        });

        // enter to search
        searchField.setOnAction(event -> {
            searchWord = searchField.getText();
            addToHistory(searchWord);

            favour_button.setVisible(false);
            if (DictionaryCommandline.isExist(searchWord, DBConnect.connectDB())) {
                favour_button.setVisible(true);
                if (FavouriteWord.IsFavour(searchWord)) {
                    favour_button.setGraphic(favour_star);
                    favour_star.setVisible(true);
                    border_star.setVisible(false);
                } else {
                    favour_button.setGraphic(border_star);
                    border_star.setVisible(true);
                    favour_star.setVisible(false);
                }
            }
            String result = DictionaryCommandline.getWord(searchWord, DBConnect.connectDB());
            searchResult.getEngine().loadContent(result);
            suggestList.setVisible(false);
        });
    }
    // Switch Scene
    public void addScene(ActionEvent event) throws IOException {
        saveHistory();
        ManageScene.showScene(BaseController.getRoot(),BaseController.getStage(),BaseController.getScene(),event,"addAndChange.fxml");
    }

    public void showSettingScene(ActionEvent event) throws IOException {
        saveHistory();
        ManageScene.showScene(BaseController.getRoot(),BaseController.getStage(),BaseController.getScene(),event,"Setting.fxml");
    }

    public void gameScene(ActionEvent event) throws IOException {
        saveHistory();
        ManageScene.showScene(BaseController.getRoot(),BaseController.getStage(),BaseController.getScene(),event,"Game.fxml");
    }

    public void showTranslateScene(ActionEvent event) throws IOException {
        saveHistory();

        ManageScene.showScene(BaseController.getRoot(),BaseController.getStage(),BaseController.getScene(),event,"Translate.fxml");
    }

    public void setting() {
        settingFont = new Vector<>();


        try {
            File file = new File("database/setting.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                settingFont.add(scanner.nextLine());

            }

            scanner.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ManageScene.setFont(searchField);
//        suggestList.setStyle("-fx-font: "+settingFont.get(1) +"px " +"'"+settingFont.get(0) +"';");
    }


}
