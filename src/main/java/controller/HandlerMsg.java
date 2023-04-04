package сontroller;

import msg_processor.GenericPack;
import msg_processor.SearchPersonAnswer;

public interface HandlerMsg
{
    void handleInputMessage(final GenericPack inputMessage);
}
