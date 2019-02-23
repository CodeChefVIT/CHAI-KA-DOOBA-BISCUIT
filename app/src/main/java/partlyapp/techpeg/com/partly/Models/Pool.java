package partlyapp.techpeg.com.partly.Models;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Pool {
    String pool_token;
    String name;
    Bitmap icon;
    ArrayList<Member> members;

    public Pool(){
        members=new ArrayList<>();
    }

    public Pool(String name){
        this.name=name;
        members=new ArrayList<>();
    }

    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public String getPool_token() {
        return pool_token;
    }

    public void setPool_token(String pool_token) {
        this.pool_token = pool_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

}
