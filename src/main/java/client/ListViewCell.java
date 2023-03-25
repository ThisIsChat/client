package client;

import components.msg_cloud.MsgCloud;
import javafx.scene.control.ListCell;

/**
 * Класс для выравнивания виджетов сообщений в поле вывода сообщений
 * В зависимости от отправителя
 */
public final class ListViewCell extends ListCell<MsgCloud>
{
    private String myId;

    public ListViewCell(final String myId)
    {
        this.myId = myId;
    }

    @Override
    protected void updateItem(MsgCloud msgCloud, boolean b)
    {
        super.updateItem(msgCloud, b);

        if(b)
            setGraphic(null);
        else
        {
            if(msgCloud.thisIsMyMessage(myId))
                msgCloud.setAlignmentRight();
            else
                msgCloud.setAlignmentLeft();

            setGraphic(msgCloud);
        }
    }
}
