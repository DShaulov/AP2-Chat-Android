package david.advanced_programming_2.ap2_chat_android;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MessageModel.class, ContactModel.class}, version = 1, exportSchema = false)
public abstract class AppDB extends RoomDatabase {
    public abstract MessageDao messageDao();
    public abstract ContactDao contactDao();
}
