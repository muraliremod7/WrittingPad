package com.indianservers.writingpad.model;

/**
 * Created by JNTUH on 21-07-2017.
 */

public class SpinnerModel {

    private  String imageName ="";
    private  String Image="";
    private  String Url="";

    /*********** Set Methods ******************/
    public void setImageName(String CompanyName)
    {
        this.imageName = CompanyName;
    }

    public void setImage(String Image)
    {
        this.Image = Image;
    }

    public void setUrl(String Url)
    {
        this.Url = Url;
    }

    /*********** Get Methods ****************/
    public String getImageName()
    {
        return this.imageName;
    }

    public String getImage()
    {
        return this.Image;
    }

    public String getUrl()
    {
        return this.Url;
    }
}
