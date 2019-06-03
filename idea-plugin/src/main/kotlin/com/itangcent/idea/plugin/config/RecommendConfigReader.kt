package com.itangcent.idea.plugin.config

import com.google.inject.Inject
import com.google.inject.name.Named
import com.itangcent.idea.plugin.settings.SettingBinder
import com.itangcent.intellij.config.ConfigReader
import com.itangcent.intellij.config.MutableConfigReader
import com.itangcent.intellij.extend.guice.PostConstruct
import com.itangcent.intellij.logger.Logger

class RecommendConfigReader : ConfigReader {

    @Inject
    @Named("delegate_config_reader")
    val configReader: ConfigReader? = null

    @Inject(optional = true)
    val settingBinder: SettingBinder? = null

    @Inject
    val logger: Logger? = null

    override fun first(key: String): String? {
        return configReader!!.first(key)
    }

    override fun foreach(keyFilter: (String) -> Boolean, action: (String, String) -> Unit) {
        configReader!!.foreach(keyFilter, action)
    }

    override fun foreach(action: (String, String) -> Unit) {
        configReader!!.foreach(action)
    }

    override fun read(key: String): Collection<String>? {
        return configReader!!.read(key)
    }

    @PostConstruct
    fun init() {
        if (settingBinder?.read()?.useRecommendConfig == true) {
            if (configReader is MutableConfigReader) {
                configReader.loadConfigInfoContent(RECOMMEND_CONFIG)
                logger!!.info("use recommend config")
            } else {
                logger!!.warn("failed to use recommend config")
            }
        }
    }

    companion object {
        const val RECOMMEND_CONFIG = "#Get the module from the annotation,group the apis\n" +
                "module=#module\n" +
                "\n" +
                "#Ignore class/api\n" +
                "ignore=#ignore\n" +
                "\n" +
                "#Additional json parsing rules\n" +
                "#Support for Jackson annotations\n" +
                "json.rule.field.name=@com.fasterxml.jackson.annotation.JsonProperty#value\n" +
                "#Support for Gson annotations\n" +
                "json.rule.field.name=@com.google.gson.annotations.SerializedName#value\n" +
                "#The ObjectId and Date are parsed as strings\n" +
                "json.rule.convert[org.bson.types.ObjectId]=java.lang.String\n" +
                "json.rule.convert[java.util.Date]=java.lang.String"
    }
}