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

package pisp.PispFlow;

import org.apache.commons.logging.Log;
import pisp.PispFlow.impl.PispFlowUK;
import pisp.exception.PispException;
import pisp.utilities.constants.Constants;
import pisp.utilities.constants.ErrorMessages;
import org.apache.commons.logging.LogFactory;

/**
 * Initialize the PispFlow object to work with a specific API specification of ASPSP.
 * pispFlow is a Interface and any classes written for payment initiation with banks, should.
 * implement it. Here such a class is initialized, considering the API spec of the bank.
 */
class PispFlowFactory {

    private static Log log = LogFactory.getLog(PispFlowFactory.class);

    /**
     * Generate and return suitable pispFlow child class for the bank standard given.
     *
     * @param bankStandard The bank API standard of the payer/debtor bank.
     * @param bankID       The bank ID. Needed to initialize pispFlow class.
     * @return pispFlow initialized with the API standard.
     */
    static PispFlow getPispFlow(String bankStandard, String bankID) {

        PispFlow pispFlow;
        switch (bankStandard) {
            case Constants.OPEN_BANKING_UK: {
                pispFlow = new PispFlowUK(bankID);

                return pispFlow;
                //}case Constants.OPEN_BANKING_BERLIN: {
                //pispFlow = new PispFlowUK(bankID);

                // return pispFlow;
            }
            default: {
                log.error(ErrorMessages.BANK_API_NOT_RECOGNISED);
                throw new PispException(ErrorMessages.BANK_API_NOT_RECOGNISED);
            }
        }
    }
}
