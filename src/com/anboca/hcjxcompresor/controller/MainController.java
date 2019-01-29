package com.anboca.hcjxcompresor.controller;

import com.googlecode.htmlcompressor.compressor.Compressor;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.googlecode.htmlcompressor.compressor.XmlCompressor;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

public class MainController {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button settingsButton;

    @FXML
    private Button aboutButton;

    private JFXSnackbar snackbar;

    @FXML
    private ImageView dragDropImage;

    private boolean mReplaceFiles = true;
    private boolean mCompressCss = true;
    private boolean mCompressHtml = true;
    private boolean mCompressJs = true;
    private boolean mCompressXml = true;
    private Preferences mPrefs;

    private int mFilesCounter;

    public MainController(){}

    @FXML
    private void initialize(){
        Image settingsImage = new Image(MainController.class.getResourceAsStream("/resources/settings.png"));
        Image aboutImage = new Image(MainController.class.getResourceAsStream("/resources/info.png"));
        if (settingsButton!=null)
            settingsButton.setGraphic(new ImageView(settingsImage));
        if (aboutButton!=null)
            aboutButton.setGraphic(new ImageView(aboutImage));
        snackbar = new JFXSnackbar(mainPane);
        snackbar.setPrefWidth(450.0);
        if (snackbar!=null)
            snackbar.fireEvent(new SnackbarEvent(new JFXSnackbarLayout("Drag files or folders to the image to compress!!!")));
    }

    private void loadSettings() {
        mPrefs = Preferences.userNodeForPackage(this.getClass());
        mReplaceFiles = mPrefs.getBoolean("mReplaceFiles",true);
        mCompressCss = mPrefs.getBoolean("mCompressCss",true);
        mCompressHtml = mPrefs.getBoolean("mCompressHtml",true);
        mCompressJs = mPrefs.getBoolean("mCompressJs",true);
        mCompressXml = mPrefs.getBoolean("mCompressXml",true);
        System.out.println("Preferences Loaded.");
    }

    @FXML
    private void openSettings(){
        System.out.println("Settings.");
        Stage stage = new Stage();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/resources/settings.fxml"));
            stage.setTitle("HCJXCompressor - Settings");
            stage.setScene(new Scene(root,450,300));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error: "+e.getMessage());
        }
    }

    @FXML
    private void openAbout(){
        System.out.println("About.");
        Stage stage = new Stage();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/resources/about.fxml"));
            stage.setTitle("HCJXCompressor - About");
            stage.setScene(new Scene(root,450,300));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error: "+e.getMessage());
        }
    }

    @FXML
    private void dragOver(DragEvent event){
        if (event.getDragboard().hasFiles()){
            event.acceptTransferModes(TransferMode.ANY);
        }
        event.consume();
    }

    private void blockUI(boolean state){
        settingsButton.setDisable(state);
        aboutButton.setDisable(state);
    }

    @FXML
    private void onDragDropped(DragEvent event){
        boolean replaceConfirmation=false;
        loadSettings();
        mFilesCounter=0;
        blockUI(true);
        List<File> files = event.getDragboard().getFiles();
        String extension;
        if (mReplaceFiles) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Do you want to replace files?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES)
                replaceConfirmation = true;
            if (!replaceConfirmation) {
                blockUI(false);
                return;
            }
        }
        System.out.println("Compresing files: "+new Date().toString());
        if (files!=null && files.size()>0){
            for(int i=0;i<files.size();i++)
                if (files.get(i).isDirectory())
                    iterateFolder(files.get(i));
                else{
                    extension = files.get(i).getName();
                    if (extension.contains(".")) {
                        extension = extension.substring(extension.indexOf(".")).toLowerCase();
                    }else
                        extension = null;
                    if (extension!=null){
                        if ((extension.contains(".html") || extension.contains(".htm") || extension.contains(".xhtml")) && mCompressHtml)
                            compressHtmlCssJs(files.get(i));
                        if (extension.contains(".css") && mCompressCss)
                            compressHtmlCssJs(files.get(i));
                        if (extension.contains(".js") && mCompressJs)
                            compressHtmlCssJs(files.get(i));
                        if (extension.contains(".xml") && mCompressXml)
                            compressXMLFile(files.get(i));
                    }

                }
        }
        System.out.println("End of processing: "+new Date().toString());
        if (snackbar!=null) {
            snackbar.fireEvent(new SnackbarEvent(new JFXSnackbarLayout("" + mFilesCounter + " files compressed!!!")));
        }
        blockUI(false);
    }

    public void iterateFolder(final File folder){
        String aux;
        for (final File fileEntry: folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                iterateFolder(fileEntry);
            } else{
                aux = fileEntry.getName();
                if (aux.contains(".")){
                    aux = aux.substring(aux.indexOf(".")).toLowerCase();
                }else
                    aux = null;
                if (aux!=null){
                    if ((aux.contains(".html") || aux.contains(".htm") || aux.contains(".xhtml")) && mCompressHtml)
                        compressHtmlCssJs(fileEntry);
                    if (aux.contains(".css") && mCompressCss)
                        compressHtmlCssJs(fileEntry);
                    if (aux.contains(".js") && mCompressJs)
                        compressHtmlCssJs(fileEntry);
                    if (aux.contains(".xml") && mCompressXml)
                        compressXMLFile(fileEntry);
                }
            }
        }
    }

    public void compressHtmlCssJs(File file){
        mFilesCounter++;
        File auxFile;
        BufferedReader br = null;
        String fileString="";
        String result=null;
        try {
            br = new BufferedReader(new FileReader(file));
            if (br!=null){
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line!=null){
                    sb.append(line);
                    line = br.readLine();
                }
                fileString = sb.toString();
                if (fileString!="") {
                    Compressor compressor = new HtmlCompressor();
                    ((HtmlCompressor) compressor).setEnabled(true); //if false all compression is off (default is true)
                    ((HtmlCompressor) compressor).setRemoveComments(true); //if false keeps HTML comments (default is true)
                    ((HtmlCompressor) compressor).setRemoveMultiSpaces(true); //if false keeps multiple whitespace characters (default is true)
                    ((HtmlCompressor) compressor).setRemoveIntertagSpaces(true);//removes iter-tag whitespace characters
                    ((HtmlCompressor) compressor).setRemoveQuotes(true); //removes unnecessary tag attribute quotes
                    ((HtmlCompressor) compressor).setCompressCss(true); //compress css using Yahoo YUI Compressor
                    ((HtmlCompressor) compressor).setCompressJavaScript(true); //compress js using Yahoo YUI Compressor
                    ((HtmlCompressor) compressor).setYuiCssLineBreak(80); //--line-break param for Yahoo YUI Compressor
                    ((HtmlCompressor) compressor).setYuiJsDisableOptimizations(true); //--disable-optimizations param for Yahoo YUI Compressor
                    ((HtmlCompressor) compressor).setYuiJsLineBreak(-1); //--line-break param for Yahoo YUI Compressor
                    ((HtmlCompressor) compressor).setYuiJsNoMunge(true); //--nomunge param for Yahoo YUI Compressor
                    ((HtmlCompressor) compressor).setYuiJsPreserveAllSemiColons(true);//--preserve-semi param for Yahoo YUI Compressor
                    result = compressor.compress(fileString);
                    //Save file to new File
                    if (result!=null) {
                        auxFile=null;
                        if (mReplaceFiles)
                            auxFile = file;
                        else
                            auxFile = new File(file.getPath() + ".min");
                        BufferedWriter bw = new BufferedWriter(new FileWriter(auxFile));
                        bw.write(result);
                        bw.close();
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: "+e.getMessage());
        } catch (IOException e) {
            System.out.println("Error: "+e.getMessage());
        }
    }

    public void compressXMLFile(File file){
        mFilesCounter++;
        File auxFile;
        BufferedReader br = null;
        String fileString="";
        String result=null;
        try {
            br = new BufferedReader(new FileReader(file));
            if (br!=null){
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line!=null){
                    sb.append(line);
                    line = br.readLine();
                }
                fileString = sb.toString();
                if (fileString!="") {
                    Compressor compressor = new XmlCompressor();
                    ((XmlCompressor) compressor).setEnabled(true); //if false all compression is off (default is true)
                    ((XmlCompressor) compressor).setRemoveComments(true); //if false keeps XML comments (default is true)
                    ((XmlCompressor) compressor).setRemoveIntertagSpaces(true);//removes iter-tag whitespace characters  (default is true)
                    result = compressor.compress(fileString);
                    //Save file to new File
                    if (result!=null) {
                        auxFile=null;
                        if (mReplaceFiles)
                            auxFile = file;
                        else
                            auxFile = new File(file.getPath() + ".min");
                        BufferedWriter bw = new BufferedWriter(new FileWriter(auxFile));
                        bw.write(result);
                        bw.close();
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: "+e.getMessage());
        } catch (IOException e) {
            System.out.println("Error: "+e.getMessage());
        }
    }

}
