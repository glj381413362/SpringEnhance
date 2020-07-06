package com.enhance.spring.script.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * java 源码解析器
 *
 */
public class ClassResolver {

  /** 类 */
  private static final String CLZ = "class";
  /** 包 */
  private static final String PACKAGE = "package";
  /** java 包匹配 */
  private static final String PACKAGE_PATTERN = "package\\s*\\S*";
  /** 类名称匹配 */
  private static final String CLASS_NAME_PATTERN = "class[\\s*]\\S*";

  /**
   * 解析源码
   *
   * @param sourceCoe 源码
   * @return 解析结果
   */
  public static Result resolver(final String sourceCoe) {
    final String packageName = resolverPackage(sourceCoe);
    final String className = resolverClass(sourceCoe);
    return Result.of(packageName, className);
  }

  private static String resolverPackage(final String sourceCoe) {
    final Pattern pattern = Pattern.compile(PACKAGE_PATTERN);
    final Matcher matcher = pattern.matcher(sourceCoe);
    if (!matcher.find()) {
      throw new RuntimeException("source code package not found!");
    }
    String matchPackage = matcher.group(0);
    matchPackage = StringUtils.remove(matchPackage, PACKAGE).trim();
    return StringUtils.remove(matchPackage, ";").trim();
  }

  private static String resolverClass(final String sourceCoe) {

    final Pattern pattern = Pattern.compile(CLASS_NAME_PATTERN);
    final Matcher matcher = pattern.matcher(sourceCoe);
    if (!matcher.find()) {
      throw new RuntimeException("source code class not found!");
    }
    String matchClass = matcher.group(0);
    return StringUtils.remove(matchClass, CLZ).trim();
  }

  @Data
  public static class Result {
    private String packageName;
    private String className;
    private String classFullName;
    private String classFileName;

    public static Result of(final String packageName, final String className) {
      final Result result = new Result();
      result.packageName = packageName;
      result.className = className;
      result.classFullName = result.packageName + "." + result.className;
      result.classFileName = result.className + ".java";
      return result;
    }
  }
}
