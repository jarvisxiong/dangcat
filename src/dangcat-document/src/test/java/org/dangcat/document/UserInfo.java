package org.dangcat.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.DataStatus;

public class UserInfo implements DataStatus
{
    private Set<String> addresses = new HashSet<String>();
    private double balance = 0;
    private Date borth;
    private DataState dataState = null;
    private String[] friends = null;
    private Integer id;
    private String name;
    private List<Integer> rooms = new ArrayList<Integer>();
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
        if (total != other.total)
            return false;
        return true;
    }

    public Set<String> getAddresses()
    {
        return addresses;
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

    public List<Integer> getRooms()
    {
        return rooms;
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
        long temp;
        temp = Double.doubleToLongBits(balance);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((borth == null) ? 0 : borth.hashCode());
        result = prime * result + ((dataState == null) ? 0 : dataState.hashCode());
        result = prime * result + Arrays.hashCode(friends);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((rooms == null) ? 0 : rooms.hashCode());
        result = prime * result + (int) (total ^ (total >>> 32));
        return result;
    }

    public void setAddresses(Set<String> addresses)
    {
        this.addresses = addresses;
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

    public void setRooms(List<Integer> rooms)
    {
        this.rooms = rooms;
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
