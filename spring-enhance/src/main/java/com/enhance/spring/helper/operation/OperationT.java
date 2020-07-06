package com.enhance.spring.helper.operation;

/**
 * 有返回值的操作
 *
 * @author gongliangjun 2019/4/26
 */

public interface OperationT<T> {

    /**
     * 有返回值的操作
     *
     * @return 返回
     */
    T execute();
}
