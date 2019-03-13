package cc.ssnoodles.plugin.services;

import cc.ssnoodles.db.template.*;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DataType;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.util.containers.JBIterable;
import cc.ssnoodles.db.entity.Column;
import cc.ssnoodles.db.entity.Table;
import cc.ssnoodles.db.util.FileUtil;
import cc.ssnoodles.db.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ssnoodles
 * @version 1.0
 * Create at 2019-02-10 12:51
 */
public class GeneratorService {
    private GeneratorService() {
    }

    private static final GeneratorService G = new GeneratorService();

    public static GeneratorService of() {
        return G;
    }

    public Table table(DbTable tableElement) {
        Table table = new Table();
        List<Column> columns = new ArrayList<>();
        List<String> primaryKeys = new ArrayList<>();
        JBIterable<? extends DasColumn> columnsIter = DasUtil.getColumns(tableElement);
        List<? extends DasColumn> dasColumns = columnsIter.toList();
        for (DasColumn dasColumn : dasColumns) {
            DataType dataType = dasColumn.getDataType();
            Column column = new Column();
            column.setName(dasColumn.getName());
            column.setType(dataType.typeName);
            column.setRemarks(dasColumn.getComment());
            column.setDecimalDigits(dataType.getPrecision() > 0);

            if (DasUtil.isPrimary(dasColumn)) {
                column.setPrimaryKey(true);
                primaryKeys.add(dasColumn.getName());
            }
            columns.add(column);
        }
        table.setName(tableElement.getName());
        table.setRemarks(tableElement.getComment());
        table.setColumns(columns);
        table.setPrimaryKeys(primaryKeys);
        return table;
    }

    public void generateEntity(Table table, String projectPath, String domainPackage, Template template) {
        projectPath = projectPath + "/" + domainPackage + "/";
        FileUtil.write2JavaFiles(
                projectPath + StringUtil.underlineToHumpTopUpperCase(table.getName()),
                template.tableDataToString(table));
    }

    public void generateRepository(Table table, String projectPath, String domainPackage) {
        RepositoryTemplate template = new RepositoryTemplate();
        projectPath = projectPath + "/" + domainPackage + "/";
        FileUtil.write2JavaFiles(
                projectPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + template.endsWith(),
                template.tableDataToString(table));
    }

    public void generateController(Table table, String projectPath, String controllerPackage) {
        ControllerTemplate controllerTemplate = new ControllerTemplate();
        CriteriaTemplate criteriaTemplate = new CriteriaTemplate();
        FormTemplate formTemplate = new FormTemplate();
        DtoTemplate dtoTemplate = new DtoTemplate();
        RefTemplate refTemplate = new RefTemplate();
        projectPath = projectPath + "/" + controllerPackage + "/";
        String apiDataPath = projectPath + "/data/";
        FileUtil.write2JavaFiles(
                apiDataPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + criteriaTemplate.endsWith(),
                criteriaTemplate.tableDataToString(table));
        FileUtil.write2JavaFiles(
                apiDataPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + formTemplate.endsWith(),
                formTemplate.tableDataToString(table));
        FileUtil.write2JavaFiles(
                apiDataPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + dtoTemplate.endsWith(),
                dtoTemplate.tableDataToString(table));
        FileUtil.write2JavaFiles(
                apiDataPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + refTemplate.endsWith(),
                refTemplate.tableDataToString(table));
        FileUtil.write2JavaFiles(
                projectPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + controllerTemplate.endsWith(),
                controllerTemplate.tableDataToString(table));
    }
}
