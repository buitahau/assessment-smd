package net.bigpoint.assessment.gasstation.dto;

import net.bigpoint.assessment.gasstation.GasPump;

public class GasPumpWrapper {

    private volatile double planAmount = 0d;

    private GasPump gasPump;

    public GasPumpWrapper(GasPump gasPump) {
        this.gasPump = gasPump;
    }

    public void pumpGas(double amount) {
        synchronized (gasPump) {
            planAmount -= amount;
            gasPump.pumpGas(amount);
        }
    }

    public GasPump getGasPump() {
        return gasPump;
    }

    public void increasePlanAmount(double amount) {
        this.planAmount += amount;
    }

    public boolean checkRemainingAmount(double amount) {
        return gasPump.getRemainingAmount() - planAmount >= amount;
    }
}
