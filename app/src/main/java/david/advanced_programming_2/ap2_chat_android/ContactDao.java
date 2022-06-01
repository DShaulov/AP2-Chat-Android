package david.advanced_programming_2.ap2_chat_android;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contactmodel")
    List<ContactModel> index();
    @Query("SELECT * FROM contactmodel WHERE id = :id")
    ContactModel get(int id);
    @Insert
    void insert(ContactModel contact);
    @Update
    void update(ContactModel contact);
    @Delete
    void delete(ContactModel contact);
}
