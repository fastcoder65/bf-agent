package com.betfair.aping.entities;

import com.betfair.aping.enums.PersistenceType;

/**
 * Created by Eugene on 10.01.2016.
 */
public class UpdateInstruction {

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
    }

    public PersistenceType getNewPersistenceType() {
        return newPersistenceType;
    }

    public void setNewPersistenceType(PersistenceType newPersistenceType) {
        this.newPersistenceType = newPersistenceType;
    }

    String betId;                       //    Unique identifier for the bet

    PersistenceType newPersistenceType; //   The new persistence type to update this bet to


}
