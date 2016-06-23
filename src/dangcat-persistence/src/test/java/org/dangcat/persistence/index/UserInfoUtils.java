package org.dangcat.persistence.index;

import java.util.Collection;
import java.util.Date;

import junit.framework.Assert;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Table;

class UserInfoUtils
{
    protected static void assertEntityData(UserInfo userInfo, int index)
    {
        Assert.assertEquals(index, userInfo.getId().intValue());
        Assert.assertEquals(getName(index), userInfo.getName());
        Assert.assertEquals(getAddress(index), userInfo.getAddress());
        Assert.assertEquals(getAge(index), userInfo.getAge());
        Assert.assertEquals(getRegisterTime(index), userInfo.getRegisterTime());
    }

    protected static void assertEntityList(Collection<UserInfo> dataList, int index)
    {
        for (UserInfo userInfo : dataList)
        {
            assertEntityData(userInfo, index);
            index++;
        }
    }

    protected static void assertRowData(Row row, int index)
    {
        Assert.assertEquals(index, row.getField(UserInfo.Id).getInteger().intValue());
        Assert.assertEquals(getName(index), row.getField(UserInfo.Name).getString());
        Assert.assertEquals(getAddress(index), row.getField(UserInfo.Address).getString());
        Assert.assertEquals(getAge(index), row.getField(UserInfo.Age).getInteger());
        Assert.assertEquals(getRegisterTime(index), row.getField(UserInfo.RegisterTime).getDate());
    }

    protected static void assertRowList(Collection<Row> dataList, int index)
    {
        for (Row row : dataList)
        {
            assertRowData(row, index);
            index++;
        }
    }

    protected static void createData(Collection<UserInfo> dataList, int count)
    {
        for (int index = 0; index < count; index++)
        {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(index);
            createData(userInfo, index);
            dataList.add(userInfo);
        }
    }

    protected static void createData(Table table, int count)
    {
        for (int index = 0; index < count; index++)
        {
            Row row = table.getRows().createNewRow();
            row.getField(UserInfo.Id).setInteger(index);
            row.getField(UserInfo.Name).setString(getName(index));
            row.getField(UserInfo.Address).setString(getAddress(index));
            row.getField(UserInfo.Age).setInteger(getAge(index));
            row.getField(UserInfo.RegisterTime).setDate(getRegisterTime(index));
            table.getRows().add(row);
        }
    }

    protected static void createData(UserInfo userInfo, int index)
    {
        userInfo.setName(getName(index));
        userInfo.setAddress(getAddress(index));
        userInfo.setAge(getAge(index));
        userInfo.setRegisterTime(getRegisterTime(index));
    }

    protected static String getAddress(int index)
    {
        return UserInfo.Address + " " + (index % 10);
    }

    protected static Integer getAge(int index)
    {
        int num = index;
        while (num > 100)
            num /= 20;
        return index % 20 + num;
    }

    protected static String getName(int index)
    {
        return UserInfo.Name + " " + index;
    }

    protected static Date getRegisterTime(int index)
    {
        int diffDate = index % 20;
        Date now = DateUtils.clear(DateUtils.DAY, DateUtils.now());
        return DateUtils.add(DateUtils.DAY, now, diffDate);
    }

    protected static Table getTable()
    {
        Table table = new Table("UserInfoIndex");
        table.getColumns().add(UserInfo.Id, Integer.class, true);
        table.getColumns().add(UserInfo.Name, String.class, 40);
        table.getColumns().add(UserInfo.Address, String.class, 120);
        table.getColumns().add(UserInfo.Age, Integer.class);
        table.getColumns().add(UserInfo.RegisterTime, Date.class);
        return table;
    }
}
