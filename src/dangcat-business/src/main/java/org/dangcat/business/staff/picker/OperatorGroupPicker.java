package org.dangcat.business.staff.picker;

import org.dangcat.business.staff.domain.OperatorGroupBase;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.model.Column;
import org.dangcat.web.serialize.json.PickerInfo;

import java.util.HashMap;
import java.util.Map;

public class OperatorGroupPicker extends PickerInfo
{
    @Override
    public String getDisplayField()
    {
        return OperatorGroupBase.Name;
    }

    @Override
    public String getJndiName()
    {
        return "Staff/OperatorGroup/pick";
    }

    @Override
    public Column[] getPickListFields()
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(OperatorGroupBase.class);
        Column nameColumn = entityMetaData.getTable().getColumns().find(OperatorGroupBase.Name);
        Column column = (Column) nameColumn.clone();
        column.setDisplaySize(nameColumn.getDisplaySize() * 2);
        return new Column[] { column };
    }

    @Override
    public Map<String, Object> getPickListProperties()
    {
        Map<String, Object> pickListProperties = new HashMap<String, Object>();
        pickListProperties.put("nodeIcon", "[SKIN]/../icons/16/person.png");
        pickListProperties.put("folderIcon", "[SKIN]/../icons/16/person.png");
        return pickListProperties;
    }

    @Override
    public Class<?> getResultClass()
    {
        return OperatorGroupBase.class;
    }

    @Override
    public String getValueField()
    {
        return OperatorGroupBase.Id;
    }

    @Override
    public boolean isTree()
    {
        return true;
    }
}
