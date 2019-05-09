package com.webank.webase.front.contract.entity;

import com.webank.webase.front.base.ConstantCode;
import java.util.List;
import lombok.Data;
import org.fisco.bcos.web3j.protocol.core.methods.response.AbiDefinition;
import org.hibernate.validator.constraints.NotEmpty;

/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * abiMeta interface parameter.
 * 
 */
@Data
public class ReqSendAbi {
  //  @NotBlank(message = ConstantCode.PARAM_FAIL_CONTRACTNAME_IS_EMPTY)
    private String contractName;
 //   @NotBlank(message = ConstantCode.PARAM_FAIL_VERSION_IS_EMPTY)
    private String version;
    @NotEmpty(message = ConstantCode.PARAM_FAIL_ABIINFO_IS_EMPTY)
    private List<AbiDefinition> abiInfo;
    private String binaryCode;
}