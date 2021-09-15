package net.bigpoint.assessment.gasstation.dto;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GasStationStatisticDto {

    private AtomicReference<Double> totalRevenue = new AtomicReference<>(0d);

    private AtomicInteger numberOfSuccessfulSales = new AtomicInteger(0);

    private AtomicInteger numberOfCancellationsNoGas = new AtomicInteger(0);

    private AtomicInteger numberOfCancellationsTooExpensive = new AtomicInteger(0);

    public AtomicInteger getNumberOfSuccessfulSales() {
        return numberOfSuccessfulSales;
    }

    public AtomicReference<Double> getTotalRevenue() {
        return totalRevenue;
    }

    public AtomicInteger getNumberOfCancellationsNoGas() {
        return numberOfCancellationsNoGas;
    }

    public AtomicInteger getNumberOfCancellationsTooExpensive() {
        return numberOfCancellationsTooExpensive;
    }
}
