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

    public void generateEntity(Table table, String projectPath, String domainPackage, Template template, boolean isOverWriteFiles) {
        projectPath = projectPath + "/" + domainPackage + "/";
        FileUtil.write2JavaFiles(
                projectPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + template.endsWith(),
                domainPackage(domainPackage) + template.tableDataToString(table),
                isOverWriteFiles);
    }

    public void generateRepository(Table table, String projectPath, String domainPackage, boolean isOverWriteFiles) {
        RepositoryTemplate template = new RepositoryTemplate();
        projectPath = projectPath + "/" + domainPackage + "/";
        FileUtil.write2JavaFiles(
                projectPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + template.endsWith(),
                domainPackage(domainPackage) + template.tableDataToString(table),
                isOverWriteFiles);
    }

    public void generateController(Table table, String projectPath, String controllerPackage, boolean isOverWriteFiles) {
        ControllerTemplate controllerTemplate = new ControllerTemplate();
        CriteriaTemplate criteriaTemplate = new CriteriaTemplate();
        FormTemplate formTemplate = new FormTemplate();
        DtoTemplate dtoTemplate = new DtoTemplate();
        RefTemplate refTemplate = new RefTemplate();
        DataMapperTemplate dataMapperTemplate = new DataMapperTemplate();
        UpdaterTemplate updaterTemplate = new UpdaterTemplate();

        projectPath = projectPath + "/" + controllerPackage + "/";
        String apiDataPath = projectPath + "/data/";
        FileUtil.write2JavaFiles(
                apiDataPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + criteriaTemplate.endsWith(),
                apiDataPackage(controllerPackage) + criteriaTemplate.tableDataToString(table),
                isOverWriteFiles);

        FileUtil.write2JavaFiles(
                apiDataPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + formTemplate.endsWith(),
                apiDataPackage(controllerPackage) + formTemplate.tableDataToString(table),
                isOverWriteFiles);

        FileUtil.write2JavaFiles(
                apiDataPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + dtoTemplate.endsWith(),
                apiDataPackage(controllerPackage) + dtoTemplate.tableDataToString(table),
                isOverWriteFiles);

        FileUtil.write2JavaFiles(
                apiDataPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + refTemplate.endsWith(),
                apiDataPackage(controllerPackage) + refTemplate.tableDataToString(table),
                isOverWriteFiles);

        FileUtil.write2IfExistFiles(
                projectPath + dataMapperTemplate.endsWith(),
                apiDataPackage(controllerPackage) + dataMapperTemplate.tableDataToString(table),
                new DataMapperSimpleTemplate().tableDataToString(table));

        FileUtil.write2IfExistFiles(
                projectPath + updaterTemplate.endsWith(),
                apiDataPackage(controllerPackage) + updaterTemplate.tableDataToString(table),
                new UpdaterSimpleTemplate().tableDataToString(table));

        FileUtil.write2JavaFiles(
                projectPath + StringUtil.underlineToHumpTopUpperCase(table.getName()) + controllerTemplate.endsWith(),
                domainPackage(controllerPackage) + controllerTemplate.tableDataToString(table),
                isOverWriteFiles);
    }

    private static String domainPackage(String packagePath) {
        String parsePackage = parsePackage(packagePath);
        String separator = System.getProperty("line.separator");
        return "".equals(parsePackage) ? "" : "package " + parsePackage + ";" + separator + separator;
    }

    private static String apiDataPackage(String packagePath) {
        String parsePackage = parsePackage(packagePath);
        String separator = System.getProperty("line.separator");
        return "".equals(parsePackage) ? "" : "package " + parsePackage + ".data;" + separator + separator;
    }

    private static String parsePackage(String packagePath) {
        String packageString = "";

        int index = packagePath.indexOf("src/main/java");
        if (index == -1) {
            index = packagePath.indexOf("src\\main\\java");
        }
        if (index == -1) {
            return packageString;
        }
        String substring = packagePath.substring(index + 13);
        if (substring.contains("/")) {
            packageString = substring.replace("/", ".");
        }else {
            packageString = substring.replace("\\", ".");
        }

        if (packageString.startsWith(".")) {
            packageString = packageString.substring(1);
        }
        if (packageString.endsWith(".")) {
            packageString = packageString.substring(0, packageString.length() - 1);
        }
        return packageString;
    }

}
