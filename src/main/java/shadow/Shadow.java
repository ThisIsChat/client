package shadow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import client.CommandLinesParams;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.logging.Logger;
import java.io.IOException;
import lombok.Setter;
import lombok.Getter;
import java.io.File;

public class Shadow
{
    private Logger logger;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    static private class PersonalData
    {
        private String login;
        private String password;

        private String surname;
        private String name;
    }

    private PersonalData personalData;
    private ObjectMapper objectMapper;
    private File shadowFile;

    {
        logger = Logger.getLogger(Shadow.class.getName());
        objectMapper = new ObjectMapper();
    }

    public Shadow()
    {
        shadowFile = new File(CommandLinesParams.getSystemDirectory() + "/shadow.json");

        if (!shadowFile.exists())
        {
            try
            {
                shadowFile.createNewFile();
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        try
        {
            personalData = objectMapper.readValue(shadowFile, new TypeReference<PersonalData>(){});
        }
        catch (IOException e)
        {
            logger.info("Catch while read value from shadow file");
            personalData = new PersonalData();
        }
    }

    private void write()
    {
        try
        {
            objectMapper.writeValue(shadowFile, personalData);
        } catch (IOException e)
        {
            logger.info("Catch while try save login password");
        }
    }

    public void saveLoginPassword(final String login, final String password)
    {
        personalData.setLogin(login);
        personalData.setPassword(password);
        write();
    }

    public void savePersonalData(final String login, final String password, final String name, final String surname)
    {
        personalData.setLogin(login);
        personalData.setPassword(password);
        personalData.setName(name);
        personalData.setSurname(surname);
        write();
    }

    public void saveNameSurname(final String name, final String surname)
    {
        personalData.setName(name);
        personalData.setSurname(surname);
        write();
    }

    public final String getLogin()
    {
        try
        {
            personalData = objectMapper.readValue(shadowFile, new TypeReference<PersonalData>(){});
        } catch (IOException e)
        {
            personalData = new PersonalData();
        }
        finally
        {
            return personalData.getLogin();
        }
    }

    public final String getPassword()
    {
        try
        {
            personalData = objectMapper.readValue(shadowFile, new TypeReference<PersonalData>(){});
        } catch (IOException e)
        {
            personalData = new PersonalData();
        }
        finally
        {
            return personalData.getPassword();
        }
    }

    public final String getName()
    {
        try
        {
            personalData = objectMapper.readValue(shadowFile, new TypeReference<PersonalData>(){});
        } catch (IOException e)
        {
            personalData = new PersonalData();
        }
        finally
        {
            return personalData.getName();
        }
    }

    public final String getSurname()
    {
        try
        {
            personalData = objectMapper.readValue(shadowFile, new TypeReference<PersonalData>(){});
        } catch (IOException e)
        {
            personalData = new PersonalData();
        }
        finally
        {
            return personalData.getSurname();
        }
    }

}
