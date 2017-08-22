package com.indianservers.writtingpad.model;

/**
 * Created by JNTUH on 22-08-2017.
 */

public class SpinnerFolderModel{
    private String FolderName="";
    public SpinnerFolderModel(String folderName) {
        this.FolderName = folderName;
    }

    public SpinnerFolderModel() {

    }

    public void setFolderName(String folderName) {
        FolderName = folderName;
    }
    public String getFolderName() {
        return FolderName;
    }
}
