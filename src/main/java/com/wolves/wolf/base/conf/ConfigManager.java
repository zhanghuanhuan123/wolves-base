package com.wolves.wolf.base.conf;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.convert.DisabledListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static HashMap<String, ConfigManager> hashMap = new HashMap();
    private FileBasedConfiguration config;
    private FileBasedConfigurationBuilder builder;
    private String filePath;

    private ConfigManager(String configFileName)
            throws ConfigurationException {
        this.config = null;
        // 强制使用classpath内的配置文件 START
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = null;
        logger.info("this.filePath...."+loader);
        if (loader != null) {
            url = loader.getResource(configFileName);
            logger.info("this.filePath...." + url);
        } else {
            url = ClassLoader.getSystemResource(configFileName);
            logger.info("this.getSystemResource...." + url);
        }
        if (url != null)
            this.filePath = url.getFile();
        else {
            throw new RuntimeException("Cannot locate configuration source " + configFileName);
        }

        if (this.filePath.toLowerCase().endsWith("xml")) {
            logger.info("this.filePath...."+this.filePath);

            this.builder = new FileBasedConfigurationBuilder(XMLConfiguration.class)
                    .configure(new BuilderParameters[]{
                            (BuilderParameters) ((PropertiesBuilderParameters) ((PropertiesBuilderParameters) new Parameters()
                                    .properties()
                                    .setFileName(this.filePath))
                                    .setListDelimiterHandler(new DisabledListDelimiterHandler()))
                                    .setThrowExceptionOnMissing(true)});

            this.config = ((XMLConfiguration) this.builder.getConfiguration());
        } else if (this.filePath.toLowerCase().endsWith("properties")) {
            this.builder = new FileBasedConfigurationBuilder(PropertiesConfiguration.class)
                    .configure(new BuilderParameters[]{
                            (BuilderParameters) ((PropertiesBuilderParameters) ((PropertiesBuilderParameters) new Parameters()
                                    .properties()
                                    .setFileName(this.filePath))
                                    .setListDelimiterHandler(new DisabledListDelimiterHandler()))
                                    .setThrowExceptionOnMissing(true)});

            this.config = ((PropertiesConfiguration) this.builder.getConfiguration());
        }
        hashMap.put(configFileName, this);
    }

    public static ConfigManager getSingleton(String configFileName) throws ConfigurationException {
        if (hashMap.get(configFileName) == null) {
            hashMap.put(configFileName, new ConfigManager(configFileName));
        }
        return (ConfigManager) hashMap.get(configFileName);
    }

    public String selectValue(String path) {
/* 70 */
        return this.config.getString(path);
    }

    public void setValue(String path, String value) throws ConfigurationException {
        this.config.setProperty(path, value);
    }

    public String[] selectValues(String path) throws ConfigurationException {
        return this.config.getStringArray(path);
    }

    public String getFilePath() {
/* 82 */
        return this.filePath;
    }

    public void save() throws ConfigurationException {
        this.builder.save();
    }

    public static void main(String[] args) throws ConfigurationException {
        ConfigManager configManager = getSingleton("test.properties");
        System.out.println(configManager.selectValue("database.host"));
        configManager.setValue("database.host", "new.host.com.cn");
        configManager.save();
        configManager = getSingleton("test.xml");
        System.out.println(Arrays.asList(configManager.selectValues("processing.paths.path")));
        configManager.setValue("processing.paths.path", "new_path");
        configManager.save();
    }
}