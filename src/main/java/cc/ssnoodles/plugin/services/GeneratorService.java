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

    /**
     * assign db2j table
     */
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
            column.setDecimalDigits(dataType.scale > 0);

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

    public void generateEntity(Table table, String projectPath, String domainPackage, Template template, boolean isOverWriteFiles, String newClassName) {
        projectPath = projectPath + "/" + domainPackage + "/";
        String newFileName = StringUtil.isEmpty(newClassName) ? StringUtil.underlineToHumpTopUpperCase(table.getName()) : newClassName;
        FileUtil.write2JavaFiles(
                projectPath + newFileName + template.endsWith(),
                domainPackage(domainPackage) + template.tableDataToString(table, newClassName),
                isOverWriteFiles);
    }

    public void generateRepository(Table table, String projectPath, String domainPackage, boolean isOverWriteFiles, String newClassName) {
        RepositoryTemplate template = new RepositoryTemplate();
        projectPath = projectPath + "/" + domainPackage + "/";
        String newFileName = StringUtil.isEmpty(newClassName) ? StringUtil.underlineToHumpTopUpperCase(table.getName()) : newClassName;
        FileUtil.write2JavaFiles(
                projectPath + newFileName + template.endsWith(),
                domainPackage(domainPackage) + template.tableDataToString(table, newClassName),
                isOverWriteFiles);
    }

    public void generateController(Table table, String projectPath, String controllerPackage, boolean isOverWriteFiles, String newClassName) {
        ControllerTemplate controllerTemplate = new ControllerTemplate();
        CriteriaTemplate criteriaTemplate = new CriteriaTemplate();
        NewDataTemplate newDataTemplate = new NewDataTemplate();
        UpdateTemplate updateTemplate = new UpdateTemplate();
        RecordTemplate recordTemplate = new RecordTemplate();
        DataMapperTemplate dataMapperTemplate = new DataMapperTemplate();
        UpdaterForNewTemplate updaterForNewTemplate = new UpdaterForNewTemplate();
        UpdaterForUpdateTemplate updaterForUpdateTemplate = new UpdaterForUpdateTemplate();

        projectPath = projectPath + "/" + controllerPackage + "/";
        String apiDataPath = projectPath + "/data/";
        String newFileName = StringUtil.isEmpty(newClassName) ? StringUtil.underlineToHumpTopUpperCase(table.getName()) : newClassName;
        FileUtil.write2JavaFiles(
                apiDataPath + newFileName + criteriaTemplate.endsWith(),
                apiDataPackage(controllerPackage) + criteriaTemplate.tableDataToString(table, newClassName),
                isOverWriteFiles);

        FileUtil.write2JavaFiles(
                apiDataPath + newDataTemplate.startsWith() + newFileName + newDataTemplate.endsWith(),
                apiDataPackage(controllerPackage) + newDataTemplate.tableDataToString(table, newClassName),
                isOverWriteFiles);

        FileUtil.write2JavaFiles(
                apiDataPath + newFileName + recordTemplate.endsWith(),
                apiDataPackage(controllerPackage) + recordTemplate.tableDataToString(table, newClassName),
                isOverWriteFiles);

        FileUtil.write2JavaFiles(
                apiDataPath + newFileName + updateTemplate.endsWith(),
                apiDataPackage(controllerPackage) + updateTemplate.tableDataToString(table, newClassName),
                isOverWriteFiles);

        FileUtil.write2IfExistFiles(
                projectPath + dataMapperTemplate.endsWith(),
                domainPackage(controllerPackage) + dataMapperTemplate.tableDataToString(table, newClassName),
                new DataMapperSimpleTemplate().tableDataToString(table, newClassName));

        FileUtil.write2IfExistFiles(
                projectPath + updaterForNewTemplate.endsWith(),
                domainPackage(controllerPackage) + updaterForNewTemplate.tableDataToString(table, newClassName),
                new UpdaterForNewSimpleTemplate().tableDataToString(table, newClassName));

        FileUtil.write2IfExistFiles(
                projectPath + updaterForUpdateTemplate.endsWith(),
                domainPackage(controllerPackage) + updaterForUpdateTemplate.tableDataToString(table, newClassName),
                new UpdaterForUpdateSimpleTemplate().tableDataToString(table, newClassName));

        FileUtil.write2JavaFiles(
                projectPath + newFileName + controllerTemplate.endsWith(),
                domainPackage(controllerPackage) + controllerTemplate.tableDataToString(table, newClassName),
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
