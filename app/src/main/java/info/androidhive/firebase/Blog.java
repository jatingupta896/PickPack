package info.androidhive.firebase;

/**
 * Created by new on 09-Jan-17.
 */

public class Blog {
    private String title, Desc;
    private String Image;
    private String username;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Blog() {


    }

    public Blog(String title, String Desc, String Image, String username, long time, String uid) {
        this.title = title;
        this.Desc = Desc;
        this.Image = Image;
        this.username = username;
        this.time = time;
        this.uid = uid;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
