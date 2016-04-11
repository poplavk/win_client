package gui;

import aleksey2093.GiveMeSettings;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Created by aleks on 09.04.2016.
 */
public class SettingsDescriptor extends GiveMeSettings {
    int socialNetwork;
    Boolean photoFlag;
    Boolean fioFlag;
    Boolean bithdayFlag;
    Boolean cityFlag;
    Boolean workFlag;
    Boolean phoneFlag;
    int encryption;

    public  SettingsDescriptor() {
        encryption = getEncryption();
        byte[] res = getSocialStg();
        socialNetwork = res[0];
        photoFlag = (res[1] == 1); //Boolean.getBoolean(ByteBuffer.wrap(res,1,1).toString());
        fioFlag = (res[2] == 1);
        bithdayFlag = (res[3] == 1);//Boolean.getBoolean(ByteBuffer.wrap(res,3,1).toString());
        cityFlag = (res[4] == 1);//Boolean.getBoolean(ByteBuffer.wrap(res,4,1).toString());
        workFlag = (res[5] == 1);//Boolean.getBoolean(ByteBuffer.wrap(res,5,1).toString());
        phoneFlag = (res[6] == 1);//Boolean.getBoolean(ByteBuffer.wrap(res,6,1).toString());
    }
    public int getEncryptNow()
    {
        return encryption;
    }
    public int getSocialNetwork() {
        return socialNetwork;
    }
    public boolean getPhoto() {
        return photoFlag;
    }
    public boolean getFio() {
        return fioFlag;
    }
    public boolean getBithDay()
    {
        return bithdayFlag;
    }
    public boolean getCity()
    {
        return cityFlag;
    }
    public boolean getWork()
    {
        return workFlag;
    }
    public boolean getPhone()
    {
        return phoneFlag;
    }

}
