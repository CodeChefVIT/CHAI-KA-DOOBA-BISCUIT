package partlyapp.techpeg.com.partly.Models;

import com.orm.SugarRecord;

public class Member extends SugarRecord {

    String name;
    String ip_address;

    public Member(){

    }

    public Member(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }
}
