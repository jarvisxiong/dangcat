package org.dangcat.business.settle.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.dangcat.business.settle.SettleEntity;
import org.dangcat.business.settle.SettleRecord;
import org.dangcat.business.settle.SettleUnit;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.entity.LoadEntityContext;
import org.dangcat.persistence.entity.SaveEntityContext;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.index.IndexManager;
import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.tablename.DateTimeTableName;
import org.dangcat.persistence.tablename.DynamicTableUtils;
import org.dangcat.persistence.tablename.TableName;

class DateTimeExecutor
{
    private static final String DEFAULT_SQLNAME = "Settle";
    private static final String ID = "id";
    private Date dateTime = null;
    private Table destTable = null;
    private EntityManager entityManager = null;
    private EntityMetaData entityMetaData = null;
    private IndexManager<SettleEntity> indexManager = new IndexManager<SettleEntity>();
    private SettleUnit settleUnit = null;
    private Table sourceTable = null;

    DateTimeExecutor(Date dateTime)
    {
        this.dateTime = dateTime;
    }

    private SettleEntity append(SettleEntity data)
    {
        Object[] primaryKeyValues = this.entityMetaData.getPrimaryKeyValues(data);
        SettleEntity settleEntity = this.indexManager.find(primaryKeyValues);
        if (settleEntity != null)
        {
            this.settleUnit.merge(data, settleEntity);
            settleEntity.setDataState(DataState.Modified);
        }
        else
        {
            this.indexManager.add(data);
            settleEntity = data;
            settleEntity.setDataState(DataState.Insert);
        }
        return settleEntity;
    }

    private void createDestTable()
    {
        Table table = (Table) this.entityMetaData.getTable().clone();
        DateTimeTableName dateTimeTableName = (DateTimeTableName) this.entityMetaData.getTable().getTableName();
        DateTimeTableName destTableName = new DateTimeTableName(dateTimeTableName.getPrefix(), dateTimeTableName.getFieldName());
        destTableName.setDateTime(this.dateTime);
        table.setTableName(destTableName);
        this.destTable = table;
    }

    private void createSourceTable()
    {
        this.sourceTable = new Table(this.createTableName(this.dateTime));
    }

    private DateTimeTableName createTableName(Date dateTime)
    {
        DateTimeTableName dateTimeTableName = this.settleUnit.getSourceTableName();
        DateTimeTableName tableName = new DateTimeTableName(dateTimeTableName.getPrefix(), dateTimeTableName.getFieldName());
        tableName.setDateTime(dateTime);
        return tableName;
    }

    protected int execute()
    {
        if (!this.sourceTable.exists())
            return 0;

        SettleRecord settleRecord = this.getSettleRecord();
        List<SettleEntity> dataList = this.load(this.sourceTable.getTableName(), DEFAULT_SQLNAME, this.getFilterExpress(settleRecord));
        if (dataList != null)
        {
            this.loadSourceData(settleRecord);

            Collection<Object> entities = new ArrayList<Object>();
            for (SettleEntity data : dataList)
            {
                SettleEntity settleEntity = this.append(data);
                if (settleRecord.getMaxId() == null || settleRecord.getMaxId().compareTo(settleEntity.getMaxId()) < 0)
                    settleRecord.setMaxId(settleEntity.getMaxId());
                entities.add(settleEntity);
            }

            if (entities.size() > 0)
            {
                SaveEntityContext saveEntityContext = new SaveEntityContext();
                saveEntityContext.setTableName(this.destTable.getTableName());
                this.entityManager.save(saveEntityContext, entities.toArray());
                this.entityManager.save(settleRecord);
            }
        }
        return dataList == null ? 0 : dataList.size();
    }

    private FilterExpress getFilterExpress(SettleRecord settleRecord)
    {
        FilterExpress filterExpress = null;
        if (settleRecord != null && settleRecord.getMaxId() != null)
            filterExpress = new FilterUnit(ID, FilterType.gt, settleRecord.getMaxId());
        return filterExpress;
    }

    protected String getName()
    {
        return DynamicTableUtils.getActualTableName(this.sourceTable.getTableName());
    }

    private SettleRecord getSettleRecord()
    {
        String sourceTableName = this.getName();
        SettleRecord settleRecord = null;
        Table table = EntityHelper.getEntityMetaData(SettleRecord.class).getTable();
        if (!table.exists())
            table.create();
        else
            settleRecord = this.entityManager.load(SettleRecord.class, sourceTableName);
        if (settleRecord == null)
            settleRecord = new SettleRecord(sourceTableName);
        return settleRecord;
    }

    protected void initialize()
    {
        this.entityMetaData = EntityHelper.getEntityMetaData(this.settleUnit.getClassType());
        this.createDestTable();
        this.createSourceTable();
        String primaryIndexName = ValueUtils.join(this.entityMetaData.getPrimaryKeyNames());
        this.indexManager.appendIndex(primaryIndexName, true);
    }

    protected boolean isEquals(Date dateTime)
    {
        String tableName = DynamicTableUtils.getActualTableName(this.createTableName(dateTime));
        return this.getName().equals(tableName);
    }

    private List<SettleEntity> load(TableName tableName, String sqlName, FilterExpress filterExpress)
    {
        LoadEntityContext loadEntityContext = new LoadEntityContext(this.settleUnit.getClassType(), filterExpress);
        loadEntityContext.setTableName(tableName);
        loadEntityContext.setSqlName(sqlName);
        return this.entityManager.load(loadEntityContext);
    }

    private void loadSourceData(SettleRecord settleRecord)
    {
        if (!this.destTable.exists())
        {
            this.destTable.create();
            this.indexManager.clear();
        }
        else if (settleRecord.getMaxId() == null)
        {
            this.destTable.truncate();
            this.indexManager.clear();
        }
        else if (this.indexManager.getDataCollection().isEmpty())
        {
            List<SettleEntity> settleEntityList = this.load(this.destTable.getTableName(), null, null);
            if (settleEntityList != null)
            {
                for (SettleEntity settleEntity : settleEntityList)
                    this.indexManager.add(settleEntity);
            }
        }
    }

    protected void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    protected void setSettleUnit(SettleUnit settleUnit)
    {
        this.settleUnit = settleUnit;
    }
}
