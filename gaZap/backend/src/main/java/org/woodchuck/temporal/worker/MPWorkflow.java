package org.woodchuck.temporal.worker;

import org.springframework.core.annotation.Order;
import org.woodchuck.dtos.MaterialStructureParams;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.woodchuck.temporal.workflows.MPSpec;

@WorkflowInterface
public interface MPWorkflow {
   
    @WorkflowMethod
    void processMP(MPSpec spec);

    // @SignalMethod
    // void paymentAuthorized(String transactionId, String authorizationId);

    // @SignalMethod
    // void paymentDeclined(String transactionId, String cause);

    // @SignalMethod
    // void getChemicalElement(String elementId);
    
    // @SignalMethod
    // void getMaterialDetails(MaterialStructureParams params);

    // @SignalMethod
    // void getProvenance(MaterialStructureParams params);

    // @SignalMethod
    // void getDOI(MaterialStructureParams params);

    // @SignalMethod
    // void getCIFfile(String materialId);

    // @QueryMethod
    // Order getOrder();

    // @QueryMethod
    // Shipping getShipping();

    // @QueryMethod
    // PaymentAuthorization getPayment();

    // @QueryMethod
    // RefundRequest getRefund();
}


