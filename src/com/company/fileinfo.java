package com.company;

import java.util.ArrayList;
import java.util.List;

public class fileinfo {
    String name = new String();
    String MainPath = new String();
    ArrayList<String> TPlist = new ArrayList<>();
    String newname =new String();

    public String getName() {
        return name;
    }

    public String getNewname() {
        return newname;
    }

    public void setNewname(String newname) {
        this.newname = newname;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getMainPath() {
        return MainPath;
    }

    public void setMainPath(String mainPath) {
        MainPath = mainPath;
    }

    public ArrayList<String> getTPlist() {
        return TPlist;
    }

    public void setTPlist(ArrayList<String> TPlist) {
        this.TPlist = TPlist;
    }
}





