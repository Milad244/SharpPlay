import java.sql.Date;
import java.sql.Time;

public class Song {
    private final int id;
    private final String fileName;
    private String title;
    private String author;
    private final Time duration;
    private final Date added;
    private int color;

    public Song(int id, String fileName, String title, String author, Time duration, Date added, int color) {
        this.id = id;
        this.fileName = fileName;
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.added = added;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Time getDuration() {
        return duration;
    }

    public Date getAdded() {
        return added;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", duration=" + duration +
                ", added=" + added +
                ", color=" + color +
                '}';
    }
}
