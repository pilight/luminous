package de.denisulmer.luminous.v2;

public class Device
{
    protected final int TYPE_SOCKET = 1;

    private int mType;
    private int mUnit;
    private int mId;
    private String mName;
    private String mDescription;
    private String mState;
    private String mProtocol;
    private String mLocation;
    private String mLocationDesc;

    public Device() { }

    public void setName(String s)
    {
        mName = s;
    }
    public String getName()
    {
        return mName;
    }

    public void setState(String s)
    {
        mState = s;
    }
    public String getState()
    {
        return mState;
    }

    public void setProtocol(String s)
    {
        mProtocol = s.substring(2, s.length()-2);
    }
    public String getProtocol()
    {
        return mProtocol;
    }

    public void setDescription(String s)
    {
        mDescription = s;
    }
    public String getDescription()
    {
        return mDescription;
    }

    public void setId(int i)
    {
        mId = i;
    }
    public int getId()
    {
        return mId;
    }

    public void setUnit(int i)
    {
        mUnit = i;
    }
    public int getUnit()
    {
        return mUnit;
    }

    public void setType(int i)
    {
        mType = i;
    }
    public int getType()
    {
        return mType;
    }

    public void setLocation(String s)
    {
        mLocation = s;
    }
    public String getLocation()
    {
        return mLocation;
    }

    public String getLocationDesc() {
        return mLocationDesc;
    }

    public void setLocationDesc(String mLocationDesc) {
        this.mLocationDesc = mLocationDesc;
    }
}

