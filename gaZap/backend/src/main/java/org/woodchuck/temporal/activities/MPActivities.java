package org.woodchuck.temporal.activities;

import io.temporal.activity.ActivityInterface;
import org.woodchuck.dtos.MaterialStructureParams;


@ActivityInterface
public interface MPActivities {

    String getChemicalElement(String elementId);
    String getMaterialDetails(MaterialStructureParams params);
    String getProvenance(MaterialStructureParams params);
    String getDOI(MaterialStructureParams params);
    String getCIFfile(String materialId);

    // Shipping getShipping();    
    // void reserveOrderItems(Order order);
    // void cancelReservedItems(Order order);
    // void returnOrderItems(Order order);
    // void dispatchOrderItems(Order order);

    // PaymentAuthorization createPaymentRequest(Order order, BillingInfo billingInfo);
    // RefundRequest createRefundRequest(PaymentAuthorization payment);

    // Shipping createShipping(Order order);
    // Shipping updateShipping(Shipping shipping, ShippingStatus status);
}