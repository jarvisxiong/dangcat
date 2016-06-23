package org.dangcat.persistence.validator;

import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.validator.exception.DataValidateException;
import org.dangcat.persistence.validator.impl.NotNullValidator;

/**
 * Table数据基本校验。
 *
 * @author dangcat
 */
public class TableDataValidator {
    public static void validate(Table table) throws DataValidateException {
        DataValidator[] dataValidators = table.getColumns().getDataValidators();
        if (dataValidators != null && dataValidators.length > 0) {
            for (Row row : table.getRows()) {
                for (DataValidator dataValidator : dataValidators) {
                    if (dataValidator instanceof NotNullValidator) {
                        if (DataState.Insert.equals(row.getDataState()) && dataValidator.getColumn().isAutoIncrement())
                            continue;
                    }
                    dataValidator.validate(row);
                }
            }
        }
    }
}
