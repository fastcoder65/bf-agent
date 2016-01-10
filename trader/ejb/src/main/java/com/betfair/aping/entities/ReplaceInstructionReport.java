package com.betfair.aping.entities;

import com.betfair.aping.enums.InstructionReportErrorCode;
import com.betfair.aping.enums.InstructionReportStatus;

/**
 * Created by Eugene on 10.01.2016.
 */
public class ReplaceInstructionReport {

    InstructionReportStatus status;         // whether the command succeeded or failed


    InstructionReportErrorCode errorCode;   //  cause of failure, or null if command succeeds


    CancelInstructionReport cancelInstructionReport; //7 Cancelation report for the original order


    PlaceInstructionReport  placeInstructionReport;


    public CancelInstructionReport getCancelInstructionReport() {
        return cancelInstructionReport;
    }

    public void setCancelInstructionReport(CancelInstructionReport cancelInstructionReport) {
        this.cancelInstructionReport = cancelInstructionReport;
    }

    public InstructionReportErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(InstructionReportErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public PlaceInstructionReport getPlaceInstructionReport() {
        return placeInstructionReport;
    }

    public void setPlaceInstructionReport(PlaceInstructionReport placeInstructionReport) {
        this.placeInstructionReport = placeInstructionReport;
    }

    public InstructionReportStatus getStatus() {
        return status;
    }

    public void setStatus(InstructionReportStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReplaceInstructionReport{" +
                "cancelInstructionReport=" + cancelInstructionReport +
                ", status=" + status +
                ", errorCode=" + errorCode +
                ", placeInstructionReport=" + placeInstructionReport +
                '}';
    }
}
