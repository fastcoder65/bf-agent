package com.betfair.aping.entities;

import com.betfair.aping.enums.ExecutionReportErrorCode;
import com.betfair.aping.enums.ExecutionReportStatus;

import java.util.List;

/**
 * Created by Eugene on 10.01.2016.
 */
public class ReplaceExecutionReport {

    String customerRef; // Echo of the customerRef if passed.

    ExecutionReportStatus status;

    ExecutionReportErrorCode errorCode;

    String  marketId; //    Echo of marketId passed

    List< ReplaceInstructionReport > instructionReports;

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

    public List<ReplaceInstructionReport> getInstructionReports() {
        return instructionReports;
    }

    public void setInstructionReports(List<ReplaceInstructionReport> instructionReports) {
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
}
