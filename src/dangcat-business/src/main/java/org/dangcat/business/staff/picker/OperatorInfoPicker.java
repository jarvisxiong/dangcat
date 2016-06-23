package org.dangcat.business.staff.picker;

import org.dangcat.business.staff.domain.OperatorInfoBase;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.model.Column;
import org.dangcat.web.serialize.json.PickerInfo;

public class OperatorInfoPicker extends PickerInfo
{
    @Override
    public String getDisplayField()
    {
        return OperatorInfoBase.Name;
    }

    @Override
    public String[] getFilterFields()
    {
        return new String[] { OperatorInfoBase.Name, OperatorInfoBase.No, OperatorInfoBase.Email, OperatorInfoBase.Tel };
    }

    @Override
    public String getJndiName()
    {
        return "Staff/OperatorInfo/pick";
    }

    @Override
    public Column[] getPickListFields()
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(OperatorInfoBase.class);
        return entityMetaData.getTable().getColumns().toArray(new Column[0]);
    }

    @Override
    public Class<?> getResultClass()
    {
        return OperatorInfoBase.class;
    }

    @Override
    public String getValueField()
    {
        return OperatorInfoBase.Id;
    }
}
