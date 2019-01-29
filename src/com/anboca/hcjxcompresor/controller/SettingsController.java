package com.anboca.hcjxcompresor.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class SettingsController {

    @FXML
    private CheckBox replacesFilesCheckBox;

    @FXML
    private CheckBox cHtmlCheckBox;

    @FXML
    private CheckBox cCssCheckBox;

    @FXML
    private CheckBox cJsCheckBox;

    @FXML
    private CheckBox cXmlCheckBox;

    private boolean mReplaceFiles = true;
    private boolean mCompressCss = true;
    private boolean mCompressHtml = true;
    private boolean mCompressJs = true;
    private boolean mCompressXml = true;

    private Preferences mPrefs;

    @FXML
    private void initialize() {
        loadSettings();
        //Set saved values
        replacesFilesCheckBox.setSelected(mReplaceFiles);
        cHtmlCheckBox.setSelected(mCompressHtml);
        cCssCheckBox.setSelected(mCompressCss);
        cJsCheckBox.setSelected(mCompressJs);
        cXmlCheckBox.setSelected(mCompressXml);
        //Set listeners
        replacesFilesCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            mReplaceFiles = newValue;
            saveSettings();
            System.out.println("Replace Files: "+mReplaceFiles);
        });
        cHtmlCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            mCompressHtml = newValue;
            saveSettings();
            System.out.println("Compress Html: "+mCompressHtml);
        });
        cCssCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            mCompressCss = newValue;
            saveSettings();
            System.out.println("Compress Css: "+mCompressCss);
        });
        cJsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            mCompressJs = newValue;
            saveSettings();
            System.out.println("Compress Js: "+mCompressJs);
        });
        cXmlCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            mCompressXml = newValue;
            saveSettings();
            System.out.println("Compress Xml: "+mCompressXml);
        });

    }

    private void loadSettings() {
        mPrefs = Preferences.userNodeForPackage(this.getClass());
        mReplaceFiles = mPrefs.getBoolean("mReplaceFiles",true);
        mCompressCss = mPrefs.getBoolean("mCompressCss",true);
        mCompressHtml = mPrefs.getBoolean("mCompressHtml",true);
        mCompressJs = mPrefs.getBoolean("mCompressJs",true);
        mCompressXml = mPrefs.getBoolean("mCompressXml",true);
        System.out.println("Preferences loaded.");
    }

    private void saveSettings(){
        if (mPrefs!=null){
            mPrefs.putBoolean("mReplaceFiles",mReplaceFiles);
            mPrefs.putBoolean("mCompressCss",mCompressCss);
            mPrefs.putBoolean("mCompressHtml",mCompressHtml);
            mPrefs.putBoolean("mCompressJs",mCompressJs);
            mPrefs.putBoolean("mCompressXml",mCompressXml);
            try {
                mPrefs.sync();
                System.out.println("Preferences Saved.");
            } catch (BackingStoreException e) {
                System.out.println("Error: "+e.getMessage());
            }
        }
    }

}
