package com.example.huffmanencoding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.*;
import java.io.*;
import java.util.Map;


public class HelloController {
    @FXML
    private Button btnSelectOutputImage;
    @FXML
    private MenuItem exitApp;
    @FXML
    private Menu fileMenu;
    @FXML
    private TextField imageSize2;
    @FXML
    private Button inputChooserFile;
    @FXML
    private TextField inputPathTextField;
    @FXML
    private ImageView ivFiles2;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TextField outputPathTextField;
    @FXML
    private Pane treePane;
    @FXML
    private Canvas canvas;
    private Window stage;
    private Stage treeStage;

    //test
    private static int gap = 30;
    private static int vGap = 50;
    private Tree tree;

    @FXML
    void exitItemAction(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void openItemAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (file != null) {
            // Handle opening the file here
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File Opened");
            alert.setHeaderText(null);
            alert.setContentText("File opened: " + file.getAbsolutePath());
            alert.showAndWait();

            inputPathTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void saveItemAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
        if (file != null) {
            // Handle saving the file here
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File Saved");
            alert.setHeaderText(null);
            alert.setContentText("File saved: " + file.getAbsolutePath());
            alert.showAndWait();
        }
    }


    @FXML
    void selectOutputImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Input Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().clear();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg", "*.bin");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            ivFiles2.setImage(new Image(file.toURI().toString()));
            String size = String.format("%.2f KB", file.length() / 1024.0);
            imageSize2.setText(size);
        } else {
            System.out.println("A file is invalid");
        }
    }

    @FXML
    void decodeAction(ActionEvent event) {
        String inputPath = inputPathTextField.getText();
        String outputPath = outputPathTextField.getText();

        // validate paths

        File input = new File(inputPath);
        File output = new File(outputPath);

        try (FileInputStream inputStream = new FileInputStream(input)) {
            try (FileOutputStream outputStream = new FileOutputStream(output)) {
                HuffmanDecoder.decode(inputStream, outputStream);
                Map<Integer, String> codes = HuffmanDecoder.getCodes();
                Tree tree = new Tree(codes);
                if (codes == null) {
                    System.err.println("Huffman codes not available.");
                    return;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't open a file!");
            return;
        } catch (IOException e) {
            System.err.println("Could not read the input file!");
            return;
        }
    }

    @FXML
    void buttonChooserFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (file != null) {
            // Handle opening the file here
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File Opened");
            alert.setHeaderText(null);
            alert.setContentText("File opened: " + file.getAbsolutePath());
            alert.showAndWait();

            inputPathTextField.setText(file.getAbsolutePath());
        }

    }
    @FXML
    void outputChooserClick(ActionEvent event) {

       DirectoryChooser directoryChooser = new DirectoryChooser();

        File directory = directoryChooser.showDialog(stage);

        if(directory != null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File Opened");
            alert.setHeaderText(null);
            alert.setContentText("File opened: " + directory.getAbsolutePath());
            alert.showAndWait();
            outputPathTextField.setText(directory.getAbsolutePath());
        }
    }

    @FXML
    void treeDisplay(ActionEvent event) {
        //testing
        Map<Integer, String> codes = HuffmanDecoder.getCodes();
        Tree tree = new Tree(codes);
        if (codes == null) {
            System.err.println("Huffman codes not available.");
            return;
        }

        treeStage = new Stage();
        treeStage.setTitle("Huffman Tree Visualization");

        ScrollPane treeLayout = new ScrollPane();

        Canvas canvas = new Canvas(1000, 1000);
        treeLayout.setContent(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        HuffmanTreeDrawer.display(tree.getRoot(), canvas.getWidth() / 2 - 26, 10, gc, 500);

        ScrollPane scrollPane = new ScrollPane(treeLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene treeScene = new Scene(scrollPane, 800, 600);
        treeStage.setScene(treeScene);

        treeStage.show();

    }

}



