package outskirts.storage.dst;

import outskirts.storage.Savable;

import java.util.List;
import java.util.Map;

public class Ett implements Savable {

    public String title;
    public String author;
    public int id;
    public String time;
    public String content;
    public float likeRate;
    public List<String> likeUsers;

    public Ett(){}

    public Ett(String title, String author, int id, String time, String content, float likeRate, List<String> likeUsers) {
        this.title = title;
        this.author = author;
        this.id = id;
        this.time = time;
        this.content = content;
        this.likeRate = likeRate;
        this.likeUsers = likeUsers;
    }

    @Override
    public String toString() {
        return "Ett{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", id=" + id +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                ", likeRate=" + likeRate +
                ", likeUsers=" + likeUsers +
                '}';
    }

    @Override
    public void onRead(Map mp) {
        title = (String)mp.get("title");
        author = (String)mp.get("author");
        id = (int)mp.get("id");
        time = (String)mp.get("time");
        content = (String)mp.get("content");
        likeRate = (float)mp.get("likeRate");
        likeUsers = (List)mp.get("likeUsers");
    }

    @Override
    public void onWrite(Map mp) {
        mp.put("title", title);
        mp.put("author", author);
        mp.put("id", id);
        mp.put("time", time);
        mp.put("content", content);
        mp.put("likeRate", likeRate);
        mp.put("likeUsers", likeUsers);
    }
}
