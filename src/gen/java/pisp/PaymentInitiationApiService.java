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
package pisp;

import pisp.*;
import pisp.dto.*;

import pisp.dto.PaymentInitRequestDTO;

import java.util.List;

import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

public abstract class PaymentInitiationApiService {

    public abstract Response getPaymentInitRequestById(HttpServletRequest request, String username);

    public abstract Response makePaymentInitiationRequest(String clientId, String purchaseId, PaymentInitRequestDTO body);

    public abstract Response selectDebtorBank(HttpServletRequest request, String username, DebtorBankDTO body);

    public abstract Response selectDebtorAccount(HttpServletRequest request, String username, BankAccountDTO body);

    public abstract Response addAuthorizationCode(String username, HttpServletRequest request, AuthCodeDTO body);

    public abstract Response getPaymentStatusFromBank(HttpServletRequest request, String username);

}

