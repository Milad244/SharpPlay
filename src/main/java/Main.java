import java.sql.Date;
import java.sql.Time;

public class Main {
    private static DatabaseHandler db;

    public static void main(String[] args) {
        db = DatabaseHandler.getHandler();
        //testInsert();
        System.out.println(db.getSongs());
    }

    private static void testInsert() {
        Time duration = new Time(0, 3, 51);
        Date added = new Date(System.currentTimeMillis());

        db.insertSong(new Song(0, "Burn.mp3", "Burn", "Ellie Goulding", duration, added, 1));
    }
}
