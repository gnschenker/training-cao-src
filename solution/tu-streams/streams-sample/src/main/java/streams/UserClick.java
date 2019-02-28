package streams;

public class UserClick {
    public String userid;
    public Long registertime;
    public String regionid;
    public String gender;
    public Long viewtime;
    public String pageid;

    public UserClick(){

    }
    
    public UserClick(String userid, String regionid, String gender, String pageid){
        this.userid = userid;
        this.regionid = regionid;
        this.gender = gender;
        this.pageid = pageid;
    }
}