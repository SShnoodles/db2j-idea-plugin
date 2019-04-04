package cc.ssnoodles.plugin.model;

/**
 * @author ssnoodles
 * @version 1.0
 * Create at 2019-02-03 09:09
 */
public class Config {
    /**
     * 是否生成仓库类
     */
    private boolean repository;

    /**
     * 是否生成调度类
     */
    private boolean controller;

    /**
     * 是否覆盖文件
     */
    private boolean overwriteFiles;

    /**
     * 模板类型
     */
    private String templateType;

    /**
     * 实体生成路径
     */
    private String domainPackage;

    /**
     * controller生成路径
     */
    private String controllerPackage;

    /**
     * 作者
     */
    private String author;

    public boolean isRepository() {
        return repository;
    }

    public Config setRepository(boolean repository) {
        this.repository = repository;
        return this;
    }

    public String getTemplateType() {
        return templateType;
    }

    public Config setTemplateType(String templateType) {
        this.templateType = templateType;
        return this;
    }

    public String getDomainPackage() {
        return domainPackage;
    }

    public Config setDomainPackage(String domainPackage) {
        this.domainPackage = domainPackage;
        return this;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public Config setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Config setAuthor(String author) {
        this.author = author;
        return this;
    }

    public boolean isController() {
        return controller;
    }

    public Config setController(boolean controller) {
        this.controller = controller;
        return this;
    }

    public boolean isOverwriteFiles() {
        return overwriteFiles;
    }

    public Config setOverwriteFiles(boolean overwriteFiles) {
        this.overwriteFiles = overwriteFiles;
        return this;
    }
}
