package david.advanced_programming_2.ap2_chat_android;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MessagesViewModel extends ViewModel {
    private MutableLiveData<List<MessageModel>> messageData;

    public MutableLiveData<List<MessageModel>> getMessageData() {
        if (messageData == null) {
            messageData = new MutableLiveData<List<MessageModel>>();
        }
        return messageData;
    }
}
