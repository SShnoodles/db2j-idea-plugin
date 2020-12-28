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

    public final static String SEPARATOR = System.getProperty("line.separator");

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
            column.setDecimalDigits(dataType.scale);
            column.setNullable(!dasColumn.isNotNull());
            column.setSize(dataType.size == DataType.NO_SIZE ? 0 : dataType.size);
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

    public void generateEntity(Table table, String domainPackage, Template template, boolean isOverWriteFiles, String newClassName) {
        String projectPath = domainPackage + "/models/";
        String newFileName = StringUtil.isEmpty(newClassName) ? StringUtil.underlineToHumpTopUpperCase(table.getName()) : newClassName;
        FileUtil.write2JavaFiles(
                projectPath + newFileName + template.endsWith(),
                domainPackage(domainPackage) + template.tableDataToString(table, newClassName),
                isOverWriteFiles);
    }

    public void generateRepository(int version, Table table, String domainPackage, boolean isOverWriteFiles, String newClassName) {
        if (version != 0) return;
        RepositoryTemplate template = new RepositoryTemplate();
        String projectPath = domainPackage + "/repositories/";
        String newFileName = StringUtil.isEmpty(newClassName) ? StringUtil.underlineToHumpTopUpperCase(table.getName()) : newClassName;
        FileUtil.write2JavaFiles(
                projectPath + newFileName + template.endsWith(),
                repositoryPackage(domainPackage) + template.tableDataToString(table, newClassName),
                isOverWriteFiles);
    }

    public void generateController(int version, Table table, String controllerPackage, boolean isOverWriteFiles, String newClassName) {
        var controllerTemplate = version == 0 ? new ControllerTemplate() : new ControllerTypedTemplate();
        var criteriaTemplate = new CriteriaTemplate();
        var updateTemplate = new UpdateTemplate();
        var recordTemplate = new InfoTemplate();
        var dataMapperTemplate = new DataMapperTemplate();
        var updaterForUpdateTemplate = new UpdaterForUpdateTemplate();

        String projectPath = controllerPackage + "/";
        String apiDataPath = projectPath + "/models/";
        String apiComponentsPath = projectPath + "/components/";
        String apiControllersPath = projectPath + "/controllers/";
        String newFileName = StringUtil.isEmpty(newClassName) ? StringUtil.underlineToHumpTopUpperCase(table.getName()) : newClassName;
        if (version == 0) {
            FileUtil.write2JavaFiles(
                    apiDataPath + newFileName + criteriaTemplate.endsWith(),
                    apiDataPackage(controllerPackage) + criteriaTemplate.tableDataToString(table, newClassName),
                    isOverWriteFiles);
        }

        FileUtil.write2JavaFiles(
                apiDataPath + newFileName + recordTemplate.endsWith(),
                apiDataPackage(controllerPackage) + recordTemplate.tableDataToString(table, newClassName),
                isOverWriteFiles);

        FileUtil.write2JavaFiles(
                apiDataPath + newFileName + updateTemplate.endsWith(),
                apiDataPackage(controllerPackage) + updateTemplate.tableDataToString(table, newClassName),
                isOverWriteFiles);

        FileUtil.write2IfExistFiles(
                apiComponentsPath + dataMapperTemplate.endsWith(),
                apiComponentPackage(controllerPackage) + dataMapperTemplate.tableDataToString(table, newClassName),
                new DataMapperSimpleTemplate().tableDataToString(table, newClassName));

        FileUtil.write2IfExistFiles(
                apiComponentsPath + updaterForUpdateTemplate.endsWith(),
                apiComponentPackage(controllerPackage) + updaterForUpdateTemplate.tableDataToString(table, newClassName),
                new UpdaterForUpdateSimpleTemplate().tableDataToString(table, newClassName));

        FileUtil.write2JavaFiles(
                apiControllersPath + newFileName + controllerTemplate.endsWith(),
                apiControllerPackage(controllerPackage) + controllerTemplate.tableDataToString(table, newClassName),
                isOverWriteFiles);
    }

    private static String domainPackage(String packagePath) {
        String parsePackage = parsePackage(packagePath);
        return "".equals(parsePackage) ? "package domain.models;" : "package " + parsePackage + ".models;" + SEPARATOR + SEPARATOR;
    }

    private static String repositoryPackage(String packagePath) {
        String parsePackage = parsePackage(packagePath);
        return "".equals(parsePackage) ? "package domain.repositories;" : "package " + parsePackage + ".repositories;" + SEPARATOR + SEPARATOR;
    }

    private static String apiControllerPackage(String packagePath) {
        String parsePackage = parsePackage(packagePath);
        return "".equals(parsePackage) ? "package api.controllers;" : "package " + parsePackage + ".controllers;" + SEPARATOR + SEPARATOR;
    }

    private static String apiComponentPackage(String packagePath) {
        String parsePackage = parsePackage(packagePath);
        return "".equals(parsePackage) ? "package api.components;" : "package " + parsePackage + ".components;" + SEPARATOR + SEPARATOR;
    }

    private static String apiDataPackage(String packagePath) {
        String parsePackage = parsePackage(packagePath);
        return "".equals(parsePackage) ? "package api.models;" : "package " + parsePackage + ".models;" + SEPARATOR + SEPARATOR;
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
