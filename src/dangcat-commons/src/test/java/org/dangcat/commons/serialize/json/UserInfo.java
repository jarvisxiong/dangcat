package org.dangcat.commons.serialize.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserInfo implements DataStatus
{
    private Set<String> addresses = new HashSet<String>();
    private Set<String> addresses2 = null;
    private HashSet<String> addresses3 = null;
    private double balance = 0;
    private Date borth;
    private DataState dataState = null;
    private String[] friends = null;
    private Integer id;
    private String name;
    private HashMap<String, Integer> roomMap1 = new HashMap<String, Integer>();
    private Map<String, Integer> roomMap2 = null;
    private List<Integer> rooms = new ArrayList<Integer>();
    private List<Integer> rooms2 = null;
    private LinkedList<Integer> rooms3 = null;
    private Collection<Integer> rooms4 = null;
    private long total = 0;

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserInfo other = (UserInfo) obj;
        if (addresses == null)
        {
            if (other.addresses != null)
                return false;
        }
        else if (!addresses.equals(other.addresses))
            return false;
        if (addresses2 == null)
        {
            if (other.addresses2 != null)
                return false;
        }
        else if (!addresses2.equals(other.addresses2))
            return false;
        if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
            return false;
        if (borth == null)
        {
            if (other.borth != null)
                return false;
        }
        else if (!borth.equals(other.borth))
            return false;
        if (dataState == null)
        {
            if (other.dataState != null)
                return false;
        }
        else if (!dataState.equals(other.dataState))
            return false;
        if (!Arrays.equals(friends, other.friends))
            return false;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (rooms == null)
        {
            if (other.rooms != null)
                return false;
        }
        else if (!rooms.equals(other.rooms))
            return false;
        if (rooms2 == null)
        {
            if (other.rooms2 != null)
                return false;
        }
        else if (!rooms2.equals(other.rooms2))
            return false;
        if (rooms3 == null)
        {
            if (other.rooms3 != null)
                return false;
        }
        else if (!rooms3.equals(other.rooms3))
            return false;
        if (rooms4 == null)
        {
            if (other.rooms4 != null)
                return false;
        }
        else if (!rooms4.equals(other.rooms4))
            return false;
        if (roomMap1 == null)
        {
            if (other.roomMap1 != null)
                return false;
        }
        else if (!roomMap1.equals(other.roomMap1))
            return false;
        if (roomMap2 == null)
        {
            if (other.roomMap2 != null)
                return false;
        }
        else if (!roomMap2.equals(other.roomMap2))
            return false;
        if (total != other.total)
            return false;
        return true;
    }

    public Set<String> getAddresses()
    {
        return addresses;
    }

    public Set<String> getAddresses2()
    {
        return addresses2;
    }

    public HashSet<String> getAddresses3()
    {
        return addresses3;
    }

    public double getBalance()
    {
        return balance;
    }

    public Date getBorth()
    {
        return borth;
    }

    @Override
    public DataState getDataState()
    {
        return dataState;
    }

    public String[] getFriends()
    {
        return friends;
    }

    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public HashMap<String, Integer> getRoomMap1()
    {
        return roomMap1;
    }

    public Map<String, Integer> getRoomMap2()
    {
        return roomMap2;
    }

    public List<Integer> getRooms()
    {
        return rooms;
    }

    public List<Integer> getRooms2()
    {
        return rooms2;
    }

    public LinkedList<Integer> getRooms3()
    {
        return rooms3;
    }

    public Collection<Integer> getRooms4()
    {
        return rooms4;
    }

    public long getTotal()
    {
        return total;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
        result = prime * result + ((addresses2 == null) ? 0 : addresses2.hashCode());
        long temp;
        temp = Double.doubleToLongBits(balance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((borth == null) ? 0 : borth.hashCode());
        result = prime * result + ((dataState == null) ? 0 : dataState.hashCode());
        result = prime * result + Arrays.hashCode(friends);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((rooms == null) ? 0 : rooms.hashCode());
        result = prime * result + ((rooms2 == null) ? 0 : rooms2.hashCode());
        result = prime * result + ((rooms3 == null) ? 0 : rooms3.hashCode());
        result = prime * result + ((rooms4 == null) ? 0 : rooms4.hashCode());
        result = prime * result + ((roomMap1 == null) ? 0 : roomMap1.hashCode());
        result = prime * result + ((roomMap2 == null) ? 0 : roomMap2.hashCode());
        result = prime * result + (int) (total ^ (total >>> 32));
        return result;
    }

    public void setAddresses(Set<String> addresses)
    {
        this.addresses = addresses;
    }

    public void setAddresses2(Set<String> addresses2)
    {
        this.addresses2 = addresses2;
    }

    public void setAddresses3(HashSet<String> addresses3)
    {
        this.addresses3 = addresses3;
    }

    public void setBalance(double balance)
    {
        this.balance = balance;
    }

    public void setBorth(Date borth)
    {
        this.borth = borth;
    }

    @Override
    public void setDataState(DataState dataState)
    {
        this.dataState = dataState;
    }

    public void setFriends(String[] friends)
    {
        this.friends = friends;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setRoomMap1(HashMap<String, Integer> romMap1)
    {
        this.roomMap1 = romMap1;
    }

    public void setRoomMap2(Map<String, Integer> romMap2)
    {
        this.roomMap2 = romMap2;
    }

    public void setRooms(List<Integer> rooms)
    {
        this.rooms = rooms;
    }

    public void setRooms2(List<Integer> rooms2)
    {
        this.rooms2 = rooms2;
    }

    public void setRooms3(LinkedList<Integer> rooms3)
    {
        this.rooms3 = rooms3;
    }

    public void setRooms4(Collection<Integer> rooms4)
    {
        this.rooms4 = rooms4;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append("Id = " + this.getId());
        info.append(", Name = " + this.getName());
        info.append(", Borth = " + this.getBorth());
        info.append(", Balance = " + this.getBalance());
        info.append(", Total = " + this.getTotal());
        info.append(", Addresses = [");
        for (String address : this.addresses)
        {
            info.append(address);
            info.append(", ");
        }
        info.append("]");
        if (this.getFriends() != null)
        {
            info.append(", Friends = [");
            for (String friend : this.getFriends())
            {
                info.append(friend);
                info.append(", ");
            }
            info.append("]");
        }
        info.append(", Rooms = [");
        for (Integer room : rooms)
        {
            info.append(room);
            info.append(", ");
        }
        info.append("]");
        return info.toString();
    }
}
