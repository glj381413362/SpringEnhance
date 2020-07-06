package com.enhance.spring.message;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 支持多资源的国际化消息工具.
 *
 */
@Slf4j
public class MultiReloadableResourceBundleMessageSource extends
    ReloadableResourceBundleMessageSource {
	private static final String PROPERTIES_SUFFIX = ".properties";

	/**
	 * 加载 "classpath*:" 前缀的资源文件
	 */
	private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	@Override
	protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder) {
		if (filename.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
			return refreshClassPathProperties(filename, propHolder);
		} else {
			return super.refreshProperties(filename, propHolder);
		}
	}

	/**
	 * 扩展父类的 refreshProperties 方法, 增加对 "classpath*:" 格式资源的支持
	 *
	 * @param filename   String
	 * @param propHolder PropertiesHolder
	 * @return PropertiesHolder
	 */
	private PropertiesHolder refreshClassPathProperties(String filename, PropertiesHolder propHolder) {
		Properties properties = new Properties();
		long lastModified = -1;
		try {
			// 父类 ReloadableResourceBundleMessageSource 使用 ResourceLoader::getResource, 只会加载第一个资源文件
			Resource[] resources = resolver.getResources(filename + PROPERTIES_SUFFIX);
			for (Resource resource : resources) {
				String sourcePath = resource.getURL().toString().replace(PROPERTIES_SUFFIX, "");
				PropertiesHolder holder = super.refreshProperties(sourcePath, propHolder);
				properties.putAll(Objects.requireNonNull(holder.getProperties()));
				if (lastModified < resource.lastModified()) {
					lastModified = resource.lastModified();
				}
			}
		} catch (IOException io) {
			logger.info("Fail to load message properties with name [" + filename + "].", io);
			lastModified = -1;
		} catch (NullPointerException npe) {
			logger.info("Message properties with name [" + filename + "] does not exist.", npe);
			lastModified = -1;
		}
		return new PropertiesHolder(properties, lastModified);
	}
}

