package org.dangcat.commons.serialize.json;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class TestJsonSerializeBean
{
    private List<UserInfo> createData(int count)
    {
        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
        for (int i = 0; i < count; i++)
        {
            UserInfo userInfo = new UserInfo();
            this.createData(userInfo, i);
            userInfoList.add(userInfo);
        }
        return userInfoList;
    }

    private void createData(UserInfo userInfo, int i)
    {
        userInfo.setId(i);
        userInfo.setName("Name " + i);
        userInfo.setBalance(i * 10 + i);
        userInfo.setBorth(new Date());
        userInfo.setFriends(new String[] { "Friend 1" + i, "Friend 2" + i, "Friend 3" + i, "Friend 4" + i });
        userInfo.setTotal(i * 1000);
        userInfo.getAddresses().add("Address " + i);
        userInfo.getAddresses().add("Address " + (i + 1));

        HashSet<String> addresses2 = new HashSet<String>();
        addresses2.add("Address2 " + i);
        addresses2.add("Address2 " + (i + 1));
        userInfo.setAddresses2(addresses2);

        HashSet<String> addresses3 = new HashSet<String>();
        addresses3.add("Address3 " + i);
        addresses3.add("Address3 " + (i + 1));
        userInfo.setAddresses3(addresses3);

        userInfo.getRooms().add(i);
        userInfo.setDataState(DataState.Modified);
        userInfo.getRooms().add(i * 10);

        List<Integer> room2 = new LinkedList<Integer>();
        for (int j = 0; j < 5; j++)
            room2.add(i * 10 + j);
        userInfo.setRooms2(room2);

        LinkedList<Integer> room3 = new LinkedList<Integer>();
        for (int j = 0; j < 5; j++)
            room3.add(i * 10 + j);
        userInfo.setRooms3(room3);

        List<Integer> room4 = new LinkedList<Integer>();
        for (int j = 0; j < 5; j++)
            room4.add(i * 10 + j);
        userInfo.setRooms4(room4);

        userInfo.getRoomMap1().put("roomMap1 " + i, i);
        userInfo.getRoomMap1().put("roomMap1 " + (i + 1), i + 1);

        HashMap<String, Integer> roomMap2 = new HashMap<String, Integer>();
        roomMap2.put("rowMap2 " + i, i);
        roomMap2.put("rowMap2 " + (i + 1), i + 1);
        userInfo.setRoomMap2(roomMap2);
    }

    private ParamBean createParamBean(int count)
    {
        ParamBean paramBean = new ParamBean();
        this.createData(paramBean, 0);
        for (int i = 1; i <= count; i++)
        {
            UserInfo userInfo3 = new UserInfo();
            this.createData(userInfo3, i * 1000);
            paramBean.getManagers().put("Manager" + i, userInfo3);
        }
        return paramBean;
    }

    @Test
    public void testdeserializeList() throws IOException
    {
        List<UserInfo> userInfoList = this.createData(10);
        String data = JsonSerializer.serialize(userInfoList);
        List<UserInfo> userInfo2List = JsonDeserializer.deserializeList(data, UserInfo.class);
        Assert.assertEquals(userInfoList.size(), userInfo2List.size());
        for (UserInfo userInfo : userInfo2List)
            Assert.assertTrue(userInfoList.contains(userInfo));
    }

    @Test
    public void testdeserializeObject() throws IOException
    {
        List<UserInfo> userInfoList = this.createData(1);
        UserInfo userInfo1 = userInfoList.get(0);
        String data = JsonSerializer.serialize(userInfo1);
        UserInfo userInfo2 = JsonDeserializer.deserializeObject(data, UserInfo.class);
        Assert.assertEquals(userInfo1, userInfo2);
    }

    @Test
    public void testSerialize() throws IOException
    {
        this.testSerialize(1);
    }

    private void testSerialize(int count) throws IOException
    {
        List<UserInfo> userInfoList = this.createData(count);
        File file = File.createTempFile("DATA", ".json");
        JsonSerializer.serialize(userInfoList, new FileWriter(file));

        List<UserInfo> userInfo2List = JsonDeserializer.deserialize(new FileReader(file), UserInfo.class);
        Assert.assertEquals(userInfoList.size(), userInfo2List.size());
        for (UserInfo userInfo : userInfo2List)
            Assert.assertTrue(userInfoList.contains(userInfo));
        file.delete();
    }

    @Test
    public void testSerializeList() throws IOException
    {
        this.testSerialize(100);
    }

    @Test
    public void testSerializeParamBean() throws IOException
    {
        ParamBean srcParamBean = this.createParamBean(40);
        File file = File.createTempFile("DATA", ".json");
        JsonSerializer.serialize(srcParamBean, new FileWriter(file));

        ParamBean dstParamBean = JsonDeserializer.deserialize(new FileReader(file), new Class<?>[] { ParamBean.class }, null);
        Assert.assertEquals(srcParamBean.toString(), dstParamBean.toString());
        file.delete();
    }
}
