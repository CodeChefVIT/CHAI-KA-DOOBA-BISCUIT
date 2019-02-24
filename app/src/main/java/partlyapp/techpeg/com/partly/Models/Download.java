package partlyapp.techpeg.com.partly.Models;

import com.orm.SugarRecord;

import java.util.ArrayList;


public class Download {
    String download_token;
    String link;
    String fileName;
    long fileSize;
    boolean isTorrent;
    boolean isActive;
    ArrayList<Job> jobsList;

    public Download(){
        isTorrent=false;
        jobsList=new ArrayList<>();
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Download(String link){
        this.link=link;
    }

    public String getDownload_token() {
        return download_token;
    }

    public void setDownload_token(String download_token) {
        this.download_token = download_token;
    }

    public ArrayList<Job> getJobsList() {
        return jobsList;
    }

    public void setJobsList(ArrayList<Job> jobsList) {
        this.jobsList = jobsList;

    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public boolean isTorrent() {
        return isTorrent;
    }

    public void setTorrent(boolean torrent) {
        isTorrent = torrent;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
