package org.dangcat.persistence.entity.domain;

public class TeacherInfoUtils extends EntitySimulatorUtils<TeacherInfo>
{
    public TeacherInfoUtils()
    {
        super(TeacherInfo.class);
    }

    @Override
    public void createTable()
    {
        createTable(TeacherInfo.class);
        createTable(MemberInfo.class);
    }

    @Override
    public void dropTable()
    {
        dropTable(MemberInfo.class);
        dropTable(TeacherInfo.class);
    }
}
