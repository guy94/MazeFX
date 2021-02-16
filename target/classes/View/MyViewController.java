package View;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyViewController implements IView, Initializable, Observer
{

    public AnchorPane anchor_help_first,anchor_help_second,anchorPane_save , anchorPane_about,anchorPane_maze, mainAnchorPane, anchorPane_mazeGenerate, anchorPane_front, anchorPane_load, anchorPane_settings, anchorPane_solve;
    public BorderPane anchorPane_options;
    public Menu menu_file, menu_help, menu_exit, menu_about;
    public MenuItem menu_new, menu_save, menu_load, menu_settings, menu_properties;
    public Label menuLabel_help,menuLabel_about, menuLabel_exit, label_threadPoolSize, label_generateAlgo, label_searchAlgo, label_threadSizeValue, label_generatorType, label_searchingType;

    public ComboBox ComboBoxPlayer ,comboBox_loadMaze, comboBox_generateMaze, comboBox_solveAlgo;
    public Button Next_button,prev_button,button_backToGame, button_closeProperties, button_loadTheMaze, button_mainScreen_about, button_generate, button_solve, button_generateNewMaze, button_ok_save, button_cancel_save;
    public Slider slider_volume;
    public CheckBox checkBox_mute;
    public ArrayList<MediaPlayer> playList;
    public MediaView mediaView, MJ_mediaView;
    public MediaPlayer MJ_mediaPlayer;
    public TextField textField_row, textField_col, textField_save;
    public MazeDisplayer mazeDisplayer;
    private MyViewModel viewModel;
    public Stage mjStage = new Stage();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel = new MyViewModel(mazeDisplayer);
        this.viewModel.assignObserver(this);
        menu_save.setDisable(true);

        mazeDisplayer.getGraphicsContext().getCanvas().widthProperty().bind(anchorPane_maze.widthProperty());
        mazeDisplayer.getGraphicsContext().getCanvas().heightProperty().bind(anchorPane_maze.heightProperty());
        mazeDisplayer.getGraphicsContext().getCanvas().widthProperty().addListener(event -> mazeDisplayer.draw());
        mazeDisplayer.getGraphicsContext().getCanvas().heightProperty().addListener(event -> mazeDisplayer.draw());
        anchorPane_maze.prefHeightProperty().bind(anchorPane_solve.heightProperty());
        anchorPane_maze.prefWidthProperty().bind(anchorPane_solve.widthProperty());

        comboBox_generateMaze.getItems().addAll("MyMazeGenerator", "EmptyMazeGenerator", "SimpleMazeGenerator");
        comboBox_generateMaze.setPromptText("MyMazeGenerator");
        comboBox_generateMaze.valueProperty().bindBidirectional(viewModel.mazeGenPropProperty());
        comboBox_generateMaze.setValue("MyMazeGenerator");

        comboBox_solveAlgo.getItems().addAll("BestFirstSearch", "DepthFirstSearch", "BreadthFirstSearch");
        comboBox_solveAlgo.setPromptText("BestFirstSearch");
        comboBox_solveAlgo.valueProperty().bindBidirectional(viewModel.solveAlgoPropProperty());
        comboBox_solveAlgo.setValue("BestFirstSearch");

        ComboBoxPlayer.getItems().addAll("Michael Jordan", "Donald Duck", "Lola Bunny");
        ComboBoxPlayer.setPromptText("Michael Jordan");
        ComboBoxPlayer.valueProperty().bindBidirectional(viewModel.playerPathProperty());
        ComboBoxPlayer.setValue("Michael Jordan");

        label_generatorType.textProperty().bind(comboBox_generateMaze.valueProperty());
        label_searchingType.textProperty().bind(comboBox_solveAlgo.valueProperty());

        viewModel.mazeNameToSaveProperty().bind(textField_save.textProperty());
        viewModel.mazeToloadProperty().bind(comboBox_loadMaze.valueProperty());

        button_mainScreen_about.toBack();
        button_backToGame.setVisible(false);

        Next_button.setVisible(false);
        prev_button.setVisible(false);

        playMusic();
        setRatios();

        //Winning Video
        String videoFile = "resources\\MJ_cantTouch.mp4";
        Media videoMedia = new Media(new File(videoFile).toURI().toString());
        MJ_mediaPlayer = new MediaPlayer(videoMedia);
        MJ_mediaView = new MediaView();
        MJ_mediaView.setMediaPlayer(MJ_mediaPlayer);

    }

    public Stage returnStage()
    {
        return mjStage;
    }
    public MyViewModel getViewModel()
    {
        return this.viewModel;
    }

    @Override
    public void update(java.util.Observable o, Object arg) {

        if(((String)arg).equals("finish line")) {
            mediaView.getMediaPlayer().pause();
            MJ_mediaView.getMediaPlayer().setMute(false);
            Group root = new Group();
            root.getChildren().add(MJ_mediaView);
            Scene scene = new Scene(root, 620, 480);

            mjStage = new Stage();
            mjStage.setScene(scene);
            mjStage.show();
            MJ_mediaView.getMediaPlayer().play();

            MJ_mediaView.getMediaPlayer().setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    MJ_mediaView.getMediaPlayer().stop();
                    mjStage.close();
                    mediaView.getMediaPlayer().play();
                }
            });

            mjStage.setOnCloseRequest(e -> {
                e.consume();
                MJ_mediaView.getMediaPlayer().stop();
                mjStage.close();
                mediaView.getMediaPlayer().play();
            });
        }

        else if(((String)arg).equals("maze created"))
        {
            menu_save.setDisable(false);
        }

        else if(((String)arg).equals("Name already exists"))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setHeaderText("Maze Save Error");
            alert.getDialogPane().setContentText("Name already exists. please enter a new name");
            alert.setOnCloseRequest(e -> {
                anchorPane_save.toFront();
                mazeDisplayer.requestFocus();
            });
            alert.showAndWait();
        }

        else if(((String)arg).equals("save completed"))
        {
            anchorPane_save.toBack();
            mazeDisplayer.requestFocus();
        }

    }


    @Override
    public void menuAction(ActionEvent menuKind)
    {
        //new
        menu_new.setOnAction(e ->
        {

            anchorPane_mazeGenerate.toFront();
            button_mainScreen_about.toFront();
            button_solve.setVisible(true);
            comboBox_solveAlgo.setVisible(true);
        });

        //save
        menu_save.setOnAction(e ->
        {
            anchorPane_save.toFront();
        });

        //okButtonSave
        button_ok_save.setOnAction(e ->
        {
            if(viewModel.getMazeNameToSave().equals(""))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.getDialogPane().setHeaderText("Maze Save");
                alert.getDialogPane().setContentText("Please enter a name, or press cancel.");
                alert.showAndWait();
            }
            else
            {
                viewModel.saveMaze();
                mazeDisplayer.requestFocus();
            }
            button_mainScreen_about.toFront();
            button_backToGame.toFront();
        });

        button_cancel_save.setOnAction(e ->{
            anchorPane_solve.toFront();
            button_mainScreen_about.toFront();
            button_backToGame.toFront();
            mazeDisplayer.requestFocus();
        });

        //load
        menu_load.setOnAction(e -> {
            anchorPane_load.toFront();
            button_mainScreen_about.toFront();
            button_backToGame.toFront();
            ArrayList<String> Mazes = viewModel.getMazeNames();
            comboBox_loadMaze.getItems().clear();
            for(int i=0; i <Mazes.size();i++)
            {
                comboBox_loadMaze.getItems().add(Mazes.get(i));
            }
        }
        );

        //loadTheMaze
        button_loadTheMaze.setOnAction(e ->
        {
            if(comboBox_loadMaze.getValue() == null)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.getDialogPane().setHeaderText("Maze Load");
                alert.getDialogPane().setContentText("Please save a maze before trying to load one. Go to: File -> New. Generate a new maze, and save it.");
                alert.showAndWait();
            }
            else
                {
                //getLoadedMaze();
                viewModel.uploadMaze();
                comboBox_solveAlgo.setVisible(true);
                button_solve.setVisible(true);
                anchorPane_solve.toFront();
                button_mainScreen_about.toFront();
                mazeDisplayer.requestFocus();
               }
        });


        //settings
        menu_settings.setOnAction(e -> {
            anchorPane_settings.toFront();
            button_mainScreen_about.toFront();
            button_backToGame.toFront();}

        );

        //mute
        if(menuKind.getSource() == checkBox_mute)
        {
            if(checkBox_mute.isSelected())
                //tempMedia.setMute(true);
                mediaView.getMediaPlayer().setMute(true);
            else
                mediaView.getMediaPlayer().setMute(false);
        }

        //main screen button
        button_mainScreen_about.setOnAction(e -> {
            anchorPane_front.toFront();
            button_backToGame.toFront();
        });

        //backToGame button
        button_backToGame.setOnAction(e -> {anchorPane_solve.toFront();
        button_backToGame.toBack();
        button_mainScreen_about.toFront();
        mazeDisplayer.requestFocus();
        });

        //properties
        menu_properties.setOnAction( e -> {anchorPane_options.toFront();button_mainScreen_about.toFront();});

        //closeProperties
        button_closeProperties.setOnAction(e -> anchorPane_options.toBack());
    }

    @Override
    public void mouseEvents(MouseEvent mouseEvent)
    {
        //about
        menuLabel_about.setOnMouseClicked(e -> {anchorPane_about.toFront();button_mainScreen_about.toFront();
            button_backToGame.toFront();});

        menuLabel_help.setOnMouseClicked(event -> {
            anchor_help_first.toFront();
            Next_button.setVisible(true);
            button_backToGame.toFront();
            button_mainScreen_about.toFront();
        });
    }

    @Override
    public void addMouseScrolling(Node node) {
        node.setOnScroll((ScrollEvent event) -> {
            // Adjust the zoom factor as per your requirement
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY < 0){
                zoomFactor = 2.0 - zoomFactor;
            }
            node.setScaleX(node.getScaleX() * zoomFactor);
            node.setScaleY(node.getScaleY() * zoomFactor);

            Scale newScale = new Scale();
            newScale.setPivotX(node.getScaleX());
            newScale.setPivotY(node.getScaleY());
            node.getTransforms().add(newScale);
        });
    }

    @Override
    //exit button pressed
    public void closeProgram()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Closing Program");
        alert.setHeaderText("You are going to lose the game");
        alert.setContentText("Are you sure you want to exit the program?");
        alert.showAndWait();

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK)
        {
            viewModel.stopServers();
            Platform.exit();
            System.exit(0);
        }
    }

    public void getLoadedMaze()
    {
        this.viewModel.mazeToloadProperty().bind(comboBox_loadMaze.valueProperty());
    }

    @Override
    public void playMusic() {
        playList = new ArrayList<>();
        String path = "resources\\music";
        File folder = new File(path);
        File[] listofFiles = folder.listFiles();
        File file;// = null;

        for (int i = 0; i < listofFiles.length; i++)
        {
            file = new File(listofFiles[i].getAbsolutePath());
            try
            {
                playList.add(new MediaPlayer(new Media(file.toURI().toURL().toString())));
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
        }

        if (playList.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Audio File Not Found");
            alert.setHeaderText("Audio File?");
            alert.setContentText("Place the videos onto the following location: ../Railway/Videos/File.mp4");
            alert.showAndWait();
        }

        mediaView = new MediaView(playList.get(0));
        for (int j = 0; j < playList.size() ; j++)
        {
            MediaPlayer player = playList.get(j);
            MediaPlayer nextPlayer = playList.get((j + 1) % playList.size());

            player.setVolume(player.getVolume() - 0.5);
            slider_volume.setValue(player.getVolume() * 100);
            slider_volume.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    player.setVolume(slider_volume.getValue() / 100);
                }
            });

            player.setOnEndOfMedia(new Runnable()
            {
                @Override
                public void run()
                {
                    mediaView.setMediaPlayer(nextPlayer);
                    nextPlayer.play();
                    if(checkBox_mute.isSelected())
                        nextPlayer.setMute(true);
                }
            });
        }
        mediaView.setMediaPlayer(playList.get(0));
        mediaView.getMediaPlayer().play();

        playList.get(playList.size() - 1).setOnEndOfMedia(new Runnable() {
            @Override
            public void run()
            {
                playMusic();
                if(checkBox_mute.isSelected())
                    mediaView.getMediaPlayer().setMute(true);
            }
        });
    }

    public void setRatios()
    {
        anchorPane_front.setPrefWidth(mainAnchorPane.getWidth());
        anchorPane_front.setPrefHeight(mainAnchorPane.getHeight());

        anchorPane_mazeGenerate.setPrefWidth(mainAnchorPane.getWidth());
        anchorPane_mazeGenerate.setPrefHeight(mainAnchorPane.getHeight());

    }

    //Help Screen
    public void Next_andPrev(ActionEvent event) {
        Object source = event.getSource();
        String btnText = "";
        if (source instanceof Button) {
            Button btn = (Button) source;
            btnText = btn.getText();
        }
        if(btnText.equals("Next"))
        {
            anchor_help_second.toFront();
            Next_button.setVisible(false);
            prev_button.setVisible(true);
            button_mainScreen_about.toFront();
            button_backToGame.toFront();

        }
        else if(btnText.equals("Previous"))
        {
            anchor_help_first.toFront();
            Next_button.setVisible(true);
            prev_button.setVisible(false);
            button_mainScreen_about.toFront();
            button_backToGame.toFront();
        }

    }

    @Override
    public void generateAndSolveListeners(ActionEvent event)
    {
        Object source = event.getSource();
        String btnText = "";
        if(source instanceof Button)
        {
            Button btn = (Button) source;
            btnText = btn.getText();
        }

        if(btnText.equals("Generate!"))
        {
            //button_generate.setVisible(false);
            //button_solve.setVisible(true);
            button_backToGame.setVisible(true);
            button_backToGame.toBack();

            int row = 0, col = 0;
            try
            {
                row =  Integer.parseInt(textField_row.getText());
                col = Integer.parseInt(textField_col.getText());
                if(row > 1 && col > 1)
                {
                    viewModel.createMaze(row, col);

                    anchorPane_maze.setStyle("-fx-padding: 5;" +
                            "-fx-border-style: solid inside;" +
                            "-fx-border-width: 10;" +
                            "-fx-border-insets: 0;" +
                            "-fx-border-radius: 0;" +
                            "-fx-border-color: linear-gradient(#DC143C, #191970, #FFD700);");

                    anchorPane_solve.toFront();
                    button_mainScreen_about.toFront();
                    mazeDisplayer.requestFocus();
                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.getDialogPane().setHeaderText("Maze row size and column size have to bigger than 1.");
                    alert.showAndWait();
                }
            }
            catch(NumberFormatException e)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.getDialogPane().setHeaderText("Maze Values are Integers Only");
                alert.showAndWait();
            }
        }

        else if(btnText.equals("Solve for me please!"))
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getDialogPane().setHeaderText("It was to hard for you ha??");
            alert.getDialogPane().setContentText("Press OK if you want the system to solve the maze.");

            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK)
            {
                //button_generate.setVisible(true);
                comboBox_solveAlgo.setVisible(false);
                button_solve.setVisible(false);
                viewModel.solveMaze();
            }
            mazeDisplayer.requestFocus();
        }

        else if(btnText.equals("Generate a new maze"))
        {
            anchorPane_mazeGenerate.toFront();
            button_solve.setVisible(true);
            comboBox_solveAlgo.setVisible(true);
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        if(keyEvent.getCode() == KeyCode.CONTROL)
        {
            addMouseScrolling(anchorPane_maze);
        }
        viewModel.MoveThePlayer(keyEvent);
    }

    @Override
    public void keyRelease(KeyEvent keyEvent)
    {
        if(keyEvent.getCode() == KeyCode.CONTROL)
        {
            anchorPane_maze.setOnScroll((ScrollEvent event) -> {
            });
        }
    }
}
