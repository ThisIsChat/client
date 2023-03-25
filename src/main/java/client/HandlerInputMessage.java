package client;

import msg_processor.GenericPack;
import msg_processor.SearchPersonAnswer;

public interface HandlerInputMessage
{
    void handleTextMessage(final GenericPack msg);

    void handleSearchPersonAnswer(final SearchPersonAnswer msg);

    void close();
}
