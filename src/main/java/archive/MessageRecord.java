package archive;

import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class MessageRecord
{
    static final private String dateFormat;

    static
    {
        dateFormat = "yyyy-MM-dd HH:mm";
    }

    public void setDate(String date)
    {
        this.date = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(dateFormat));
    }

    public String getDate()
    {
        return date.format(DateTimeFormatter.ofPattern(dateFormat));
    }

    private String idFrom;
    private LocalDateTime date;
    private String text;

}
