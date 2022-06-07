package david.advanced_programming_2.ap2_chat_android;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messagemodel")
    List<MessageModel> index();
    @Query("SELECT * FROM messagemodel WHERE id = :id")
    MessageModel get(int id);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MessageModel message);
    @Update
    void update(MessageModel message);
    @Delete
    void delete(MessageModel message);

}

