/*
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *   This software is the property of WSO2 Inc. and its suppliers, if any.
 *   Dissemination of any information or reproduction of any material contained
 *   herein is strictly forbidden, unless permitted by WSO2 in accordance with
 *   the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 *   language governing the permissions and limitations under this license,
 *   please see the license as well as any agreement you’ve entered into with
 *   WSO2 governing the purchase of this software and any associated services.
 */
package com.wso2.finance.open.banking.pisp;

import com.wso2.finance.open.banking.pisp.dto.EShopProfileDTO;
import com.wso2.finance.open.banking.pisp.dto.LoginCredentialsDTO;
import com.wso2.finance.open.banking.pisp.dto.PSUProfileDTO;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

/**
 * This class contains abstract methods that come under user API.
 */
public abstract class UserApiService {

    public abstract Response addNewEShop(EShopProfileDTO body);

    public abstract Response deleteEShop(String username, HttpServletRequest request);

    public abstract Response eShopLogin(HttpServletRequest request, LoginCredentialsDTO body);

    public abstract Response getEShopProfile(String username, HttpServletRequest request);

    public abstract Response updateEShopProfile(String username, HttpServletRequest request,
                                                EShopProfileDTO body);

    public abstract Response addNewPSU(PSUProfileDTO body);

    public abstract Response loginPSU(String paymentInitReqId, HttpServletRequest request, LoginCredentialsDTO body);
}

