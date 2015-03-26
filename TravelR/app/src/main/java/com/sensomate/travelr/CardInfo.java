package com.sensomate.travelr;

/**
 * Created by rohan on 7/2/15.
 */
public class CardInfo {

    private  String Title ="";
    private  String Desc ="";
    private  String Time ="";

    /*********** Set Methods ******************/
    CardInfo(String title,String Desc,String Time){
        Title=title;
        this.Desc=Desc;
        this.Time=Time;
    }
    public void setTitle(String Title)
    {
        this.Title = Title;
    }

    public void setDesc(String Desc)
    {
        this.Desc = Desc;
    }

    public void setTime(String Time)
    {
        this.Time = Time;
    }

    /*********** Get Methods ****************/

    public String getTitle()
    {
        return this.Title;
    }

    public String getDesc()
    {
        return this.Desc;
    }

    public String getTime()
    {
        return this.Time;
    }
}