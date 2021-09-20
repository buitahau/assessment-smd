package net.bigpoint.assessment.gasstation.impl;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.dto.GasPumpWrapper;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class GasStationImpl implements GasStation, Serializable {

    private static final long serialVersionUID = 5426427971164630738L;

    private Map<GasType, List<GasPumpWrapper>> gasPumps = new HashMap<>();

    private Map<GasType, Double> prices = new HashMap<>();

    private AtomicReference<Double> totalRevenue = new AtomicReference<>(0d);

    private AtomicInteger numberOfSuccessfulSales = new AtomicInteger(0);

    private AtomicInteger numberOfCancellationsNoGas = new AtomicInteger(0);

    private AtomicInteger numberOfCancellationsTooExpensive = new AtomicInteger(0);

    private static final Logger log = LoggerFactory.getLogger(GasStationImpl.class);

    @Override
    public void addGasPump(GasPump pump) {
        getGasPumpWrappers(pump.getGasType()).add(new GasPumpWrapper(pump));
    }

    @Override
    public Collection<GasPump> getGasPumps() {
        return gasPumps.values().stream()
                .flatMap(list -> list.stream().map(l -> l.getGasPump()).collect(Collectors.toList()).stream())
                .collect(Collectors.toList());
    }

    @Override
    public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter)
            throws NotEnoughGasException, GasTooExpensiveException {

        validateMaxPrice(type, maxPricePerLiter);

        GasPumpWrapper gasPumpWrapper = getAvailableGasPump(type, amountInLiters);

        gasPumpWrapper.pumpGas(amountInLiters);

        double price = amountInLiters * prices.get(type);
        totalRevenue.set(totalRevenue.get() + price);
        numberOfSuccessfulSales.getAndIncrement();
        return price;
    }

    private GasPumpWrapper getAvailableGasPump(GasType type, double amountInLiters) throws NotEnoughGasException {
        List<GasPumpWrapper> gasPumpWrappers = getGasPumpWrappers(type);

        for (GasPumpWrapper gasPumpWrapper : gasPumpWrappers) {
            synchronized (gasPumpWrapper) {
                if (gasPumpWrapper.checkRemainingAmount(amountInLiters)) {
                    gasPumpWrapper.increasePlanAmount(amountInLiters);
                    return gasPumpWrapper;
                }
            }
        }

        numberOfCancellationsNoGas.getAndIncrement();
        throw new NotEnoughGasException();
    }

    private void validateMaxPrice(GasType type, double maxPricePerLiter) throws GasTooExpensiveException {
        Double price = prices.get(type);

        if (price == null || price > maxPricePerLiter) {
            numberOfCancellationsTooExpensive.getAndIncrement();
            throw new GasTooExpensiveException();
        }
    }

    private List<GasPumpWrapper> getGasPumpWrappers(GasType gasType) {
        List<GasPumpWrapper> gasPumpWrappers = gasPumps.get(gasType);
        if (gasPumpWrappers == null) {
            gasPumpWrappers = new ArrayList<>();
            gasPumps.put(gasType, gasPumpWrappers);
        }
        return gasPumpWrappers;
    }

    @Override
    public double getRevenue() {
        return totalRevenue.get();
    }

    @Override
    public int getNumberOfSales() {
        return numberOfSuccessfulSales.get();
    }

    @Override
    public int getNumberOfCancellationsNoGas() {
        return numberOfCancellationsNoGas.get();
    }

    @Override
    public int getNumberOfCancellationsTooExpensive() {
        return numberOfCancellationsTooExpensive.get();
    }

    @Override
    public double getPrice(GasType type) {
        Double price = prices.get(type);
        return price != null ? price : 0d;
    }

    @Override
    public void setPrice(GasType type, double price) {
        prices.put(type, price);
    }
}
