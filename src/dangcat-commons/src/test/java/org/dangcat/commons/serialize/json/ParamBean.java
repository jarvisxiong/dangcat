package org.dangcat.commons.serialize.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParamBean extends UserInfo
{
    private Map<String, UserInfo> managers = new LinkedHashMap<String, UserInfo>();
    private UserInfo[] students = null;
    private List<UserInfo> tearchers = new ArrayList<UserInfo>();

    public Map<String, UserInfo> getManagers()
    {
        return managers;
    }

    public UserInfo[] getStudents()
    {
        return students;
    }

    public void setStudents(UserInfo[] students)
    {
        this.students = students;
    }

    public List<UserInfo> getTearchers()
    {
        return tearchers;
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder(super.toString());
        info.append("\r\nTearchers = [");
        for (UserInfo userInfo : this.tearchers)
        {
            info.append(userInfo);
            info.append(", ");
        }
        info.append("]");
        if (this.getStudents() != null)
        {
            info.append("\r\nStudents = [");
            for (UserInfo userInfo : this.getStudents())
            {
                info.append(userInfo);
                info.append(", ");
            }
            info.append("]");
        }
        info.append("\r\nManagers = [");
        for (String name : this.managers.keySet())
        {
            info.append(name);
            info.append(" = ");
            info.append(this.managers.get(name));
            info.append(", ");
        }
        info.append("]");
        return info.toString();
    }
}
