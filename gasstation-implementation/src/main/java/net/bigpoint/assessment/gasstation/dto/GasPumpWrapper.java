package net.bigpoint.assessment.gasstation.dto;

import net.bigpoint.assessment.gasstation.GasPump;

public class GasPumpWrapper {

    private volatile boolean isPumping = false;

    private volatile double planAmount = 0d;

    private volatile double remainingAmount = 0d;

    private GasPump gasPump;

    public GasPumpWrapper(GasPump gasPump) {
        this.remainingAmount = gasPump.getRemainingAmount();
        this.gasPump = gasPump;
    }

    public void pumpGas(double amount) {
        synchronized (gasPump) {
            isPumping = true;
            planAmount -= amount;
            remainingAmount -= amount;
            gasPump.pumpGas(amount);
            isPumping = false;
        }
    }

    public boolean isPumping() {
        return isPumping;
    }

    public GasPump getGasPump() {
        return gasPump;
    }

    public double getPlanAmount() {
        return planAmount;
    }

    public void setPlanAmount(double planAmount) {
        this.planAmount = planAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }
}
