/**
 * Copyright 2014-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.webase.front.rabbitmq.callback;

import com.webank.webase.front.rabbitmq.RabbitMQPublisher;
import com.webank.webase.front.rabbitmq.entity.message.EventLogPushMessage;
import org.fisco.bcos.channel.event.filter.ServiceEventLogPushCallback;
import org.fisco.bcos.web3j.tx.txdecode.LogResult;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 指定exchangeName和routingKey, callback后直接push到对应的mq中
 * 可以用groupId作routingKey
 * @author marsli
 */
public class MQServiceEventLogPushCallback extends ServiceEventLogPushCallback {

	private static final Logger logger =
			LoggerFactory.getLogger(MQServiceEventLogPushCallback.class);

	private String exchangeName = "event_exchange";
	private String routingKey = "";
	private RabbitMQPublisher rabbitMQPublisher;

	public MQServiceEventLogPushCallback(RabbitMQPublisher rabbitMQPublisher,
											 String exchangeName, String routingKey) {
		this.rabbitMQPublisher = rabbitMQPublisher;
		if(exchangeName != null){
			this.exchangeName = exchangeName;
		}
		if(routingKey != null) {
			this.routingKey = routingKey;
		}
	}


	/**
	 * 根据Log对象中的blockNumber，transactionIndex，logIndex进行去重
	 * @param status
	 * @param logs
	 */
	@Override
	public void onPushEventLog(int status, List<LogResult> logs) {
		logger.info(
				"MQServiceEventLogPushCallback " +
						"onPushEventLog, params: {}, status: {}, logs: {}",
				getFilter().getParams(),
				status,
				logs);
		// 推送到指定的MQ中
		pushMessage2MQ(status, logs);
	}

	private void pushMessage2MQ(int status, List<LogResult> logs) {
		EventLogPushMessage eventLogPushMessage = new EventLogPushMessage();
		eventLogPushMessage.setStatus(status);
		eventLogPushMessage.setLogs(logs);
		rabbitMQPublisher.sendToTradeFinishedByString(exchangeName, routingKey,
				eventLogPushMessage.toString());
	}

}
