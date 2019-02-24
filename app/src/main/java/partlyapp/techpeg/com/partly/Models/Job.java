package partlyapp.techpeg.com.partly.Models;

import com.orm.SugarRecord;

public class Job extends SugarRecord {

    String jobId;
    long range_start,range_end;
    String status;

    public Job(){

    }
}
