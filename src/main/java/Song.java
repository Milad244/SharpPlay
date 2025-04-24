import java.sql.Date;
import java.sql.Time;

public class Song {
    private final int id;
    private final String fileName;
    private final String title;
    private final String author;
    private final Time duration;
    private final Date added;
    private final int color;

    public Song(String fileName, String title, String author, Time duration, Date added, int color) {
        id = -1;
        this.fileName = fileName;
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.added = added;
        this.color = color;
    }

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

    public String getAuthor() {
        return author;
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
