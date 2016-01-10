package com.betfair.aping.entities;

import com.betfair.aping.enums.ExecutionReportErrorCode;
import com.betfair.aping.enums.ExecutionReportStatus;

import java.util.List;

/**
 * Created by Eugene on 10.01.2016.
 */
public class UpdateExecutionReport {

    String customerRef; //    Echo of the customerRef if passed.

    ExecutionReportStatus status;

    ExecutionReportErrorCode errorCode;

    String marketId; //    Echo of marketId passed


    public String getCustomerRef() {
        return customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    public ExecutionReportErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ExecutionReportErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public List<UpdateInstructionReport> getInstructionReports() {
        return instructionReports;
    }

    public void setInstructionReports(List<UpdateInstructionReport> instructionReports) {
        this.instructionReports = instructionReports;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public ExecutionReportStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionReportStatus status) {
        this.status = status;
    }

    List<UpdateInstructionReport> instructionReports;


}
