package com.enhance.spring.helper;

import com.enhance.spring.helper.operation.Operation;
import com.enhance.spring.helper.operation.OperationT;
import org.springframework.transaction.annotation.Transactional;

/**
 * 事务操作帮助类
 *
 * @author gongliangjun 2020/06/13 5:27 PM
 */
public class TransactionalHelper {

  @Transactional(rollbackFor = Exception.class)
  public void transactionOperation(final Operation operation) {
    operation.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  public <T> T transactionOperation(final OperationT<T> operation) {
    return operation.execute();
  }
}
