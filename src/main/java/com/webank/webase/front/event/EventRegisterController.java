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

package com.webank.webase.front.event;

import com.webank.webase.front.base.code.ConstantCode;
import com.webank.webase.front.base.response.BaseResponse;
import com.webank.webase.front.event.entity.ReqEventLogPushRegister;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.webank.webase.front.util.RabbitMQUtils.*;

/**
 * @author marsli
 */
@Api(value = "/event", tags = "event notify of event log push and block notify")
@Slf4j
@RestController
@RequestMapping(value = "event")
public class EventRegisterController {

    @Autowired
    private EventRegisterService eventRegisterService;

    @ApiOperation(value = "registerBlockNotify",
            notes = "register registerBlockNotify and push message to mq")
    @ApiImplicitParam(name = "ReqEventLogPushRegister", value = "EventLogUserParams与消息队列名",
            required = true, dataType = "ReqEventLogPushRegister")
    @PostMapping("blockNotify")
    public Object registerBlockNotify(
            @Valid @RequestBody ReqEventLogPushRegister reqEventLogPushRegister) {
        log.info("start registerEventLogPush. reqEventLogPushRegister:{}", reqEventLogPushRegister);
        String appId = reqEventLogPushRegister.getAppId();
        int groupId = reqEventLogPushRegister.getGroupId();
        String exchangeName = reqEventLogPushRegister.getExchangeName();
        // username as queue name
        String queueName = reqEventLogPushRegister.getQueueName();

        // "username_routingKey"
        String blockRoutingKey = queueName + "_" + ROUTING_KEY_BLOCK;
        eventRegisterService.registerBlockNotify(appId, groupId,
                exchangeName, queueName, blockRoutingKey);

        return new BaseResponse(ConstantCode.RET_SUCCESS);
    }


    @ApiOperation(value = "registerEventLogPush",
            notes = "register eventLogPushCallBack and push message to mq")
    @ApiImplicitParam(name = "ReqEventLogPushRegister", value = "EventLogUserParams与消息队列名",
            required = true, dataType = "ReqEventLogPushRegister")
    @PostMapping("eventLogPush")
    public Object registerEventLogPush(
            @Valid @RequestBody ReqEventLogPushRegister reqEventLogPushRegister) {
        log.info("start registerEventLogPush. reqEventLogPushRegister:{}", reqEventLogPushRegister);
        int groupId = reqEventLogPushRegister.getGroupId();
        String appId = reqEventLogPushRegister.getAppId();
        String fromBlock = reqEventLogPushRegister.getFromBlock();
        String toBlock = reqEventLogPushRegister.getToBlock();
        String contractAddress = reqEventLogPushRegister.getContractAddress();
        List<String> topicList = reqEventLogPushRegister.getTopicList();
        String contractAbi = reqEventLogPushRegister.getContractAbi();
        String exchangeName = reqEventLogPushRegister.getExchangeName();
        // username as queue name
        String queueName = reqEventLogPushRegister.getQueueName();

        // bind queue to exchange by routing key "username_event"
        String eventRoutingKey = queueName + "_" + ROUTING_KEY_EVENT;
        // register event log push in service
        eventRegisterService.registerDecodedEventLogPush(appId, groupId,
                exchangeName, queueName, eventRoutingKey,
                contractAbi, fromBlock, toBlock, contractAddress, topicList);

        return new BaseResponse(ConstantCode.RET_SUCCESS);
    }

}
