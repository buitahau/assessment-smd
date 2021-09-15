package net.bigpoint.assessment.gasstation.dto;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GasStationGasTypeDto {

    private GasType gasType;

    private AtomicReference<Double> price = new AtomicReference<>(0d);

    private AtomicReference<Double> amount = new AtomicReference<>(0d);

    private List<GasPump> gasPumps = Collections.synchronizedList(new ArrayList<>());

    public GasStationGasTypeDto() {
    }

    public GasType getGasType() {
        return gasType;
    }

    public void setGasType(GasType gasType) {
        this.gasType = gasType;
    }

    public AtomicReference<Double> getPrice() {
        return price;
    }

    public void setPrice(AtomicReference<Double> price) {
        this.price = price;
    }

    public AtomicReference<Double> getAmount() {
        return amount;
    }

    public void setAmount(AtomicReference<Double> amount) {
        this.amount = amount;
    }

    public List<GasPump> getGasPumps() {
        return gasPumps;
    }

    public void setGasPumps(List<GasPump> gasPumps) {
        this.gasPumps = gasPumps;
    }
}
