package cc.ssnoodles.plugin.services;

import cc.ssnoodles.plugin.model.Config;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * 配置持久化
 * @author ssnoodles
 * @version 1.0
 * Create at 2019-02-10 12:57
 */
@State(name = "Db2jService", storages = {@Storage("db2j-config.xml")})
public class PersistentStateService implements PersistentStateComponent<PersistentStateService> {
    private Map<String, Config> config;

    @Nullable
    public static PersistentStateService getInstance(Project project) {
        return ServiceManager.getService(project, PersistentStateService.class);
    }

    @Nullable
    @Override
    public PersistentStateService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PersistentStateService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public Map<String, Config> getConfig() {
        return config;
    }

    public PersistentStateService setConfig(Map<String, Config> config) {
        this.config = config;
        return this;
    }
}
