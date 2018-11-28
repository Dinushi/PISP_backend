/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 *
 */
package pisp.mappings;

import pisp.dto.PaymentInitRequestItemsPurchasedDTO;
import pisp.models.Item;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is to map PurchaseItemDTO with internal model-Items.
 */
public class PurchaseItemMapping {

    /**
     * return a list of items purchased under each payment initiation request.
     * @param itemsPurchasedList
     * @return
     */
    public static List createPurchaseItemMapping(List<PaymentInitRequestItemsPurchasedDTO>  itemsPurchasedList) {
        List<Item> itemsPurchased = new ArrayList<Item>();

        Iterator<PaymentInitRequestItemsPurchasedDTO> itemsIterator = itemsPurchasedList.iterator();
        while (itemsIterator.hasNext()) {
            PaymentInitRequestItemsPurchasedDTO paymentInitRequestItemsPurchasedDTO = itemsIterator.next();
            Item item = new Item();
            item.setItemCode(paymentInitRequestItemsPurchasedDTO.getItemCode());
            item.setQuantityPurchased(paymentInitRequestItemsPurchasedDTO.getQuantity());
            item.setPricePerUnit(Float.parseFloat(paymentInitRequestItemsPurchasedDTO.getCost()));
            item.setCurrency(paymentInitRequestItemsPurchasedDTO.getCurrency());
            itemsPurchased.add(item);
        }
        return itemsPurchased;
    }
}
