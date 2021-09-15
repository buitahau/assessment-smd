package net.bigpoint.assessment.gasstation.impl;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.dto.GasStationGasTypeDto;
import net.bigpoint.assessment.gasstation.dto.GasStationStatisticDto;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import net.bigpoint.assessment.gasstation.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class GasStationImplementation implements GasStation, Serializable {

    private static final long serialVersionUID = -6285729953060502745L;

    private List<GasStationGasTypeDto> gasStationGasTypes = Collections.synchronizedList(new ArrayList<>());

    private GasStationStatisticDto gasStationStatus = new GasStationStatisticDto();

    private static final Logger log = LoggerFactory.getLogger(GasStationImplementation.class);

    @Override
    public void addGasPump(GasPump pump) {
        GasStationGasTypeDto currentGasTypeStatus = getAndCreateIfNotExists(pump.getGasType());
        currentGasTypeStatus.getGasPumps().add(pump);
        currentGasTypeStatus.getAmount().set(currentGasTypeStatus.getAmount().get() + pump.getRemainingAmount());
    }

    @Override
    public Collection<GasPump> getGasPumps() {
        return gasStationGasTypes.stream()
                .flatMap(currentGasTypeStatus -> currentGasTypeStatus.getGasPumps().stream())
                .collect(Collectors.toList());
    }

    @Override
    public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter)
            throws NotEnoughGasException, GasTooExpensiveException {

        synchronized (this) {
            validate(type, amountInLiters, maxPricePerLiter);
        }

        calculateStatisticalInformation(type, amountInLiters);

        processPumpingGas(type, amountInLiters);

        return calculatePrice(type, amountInLiters);
    }

    private void calculateStatisticalInformation(GasType type, double amountInLiters) {
        // Calculate the total revenue
        gasStationStatus.getTotalRevenue().set(gasStationStatus.getTotalRevenue().get()
                + calculatePrice(type, amountInLiters));

        // Increase number of successful sales to 1
        gasStationStatus.getNumberOfSuccessfulSales().set(gasStationStatus.getNumberOfSuccessfulSales().get() + 1);

        // Calculate the remaining amount
        GasStationGasTypeDto currentGasTypeStatus = get(type);
        currentGasTypeStatus.getAmount().set(currentGasTypeStatus.getAmount().get() - amountInLiters);
    }

    private double calculatePrice(GasType type, double amountInLiters) {
        GasStationGasTypeDto currentGasTypeStatus = get(type);

        return amountInLiters * currentGasTypeStatus.getPrice().get();
    }

    private void processPumpingGas(GasType type, double amountInLiters) {
        String uuid = UUID.randomUUID().toString();
        if (log.isInfoEnabled()) {
            log.info("Start process pumping " + amountInLiters + "l gas in " + uuid);
        }

        double amountRemainingProcess = amountInLiters;
        while (amountRemainingProcess > 0d) {

            GasPump availableGasPump = getAvailableGasPumpAndSetWorkingTrue(type);

            if (availableGasPump == null) {
                // wait a while to find another available gas pump
                // we can improve by calculating the waiting time and find the pump with the lowest waiting time
                ThreadUtil.sleep(20);
                continue;
            }

            double amountPump = Math.min(amountRemainingProcess, availableGasPump.getRemainingAmount());

            if (log.isInfoEnabled()) {
                log.info("..... Pump " + availableGasPump.getId() + " is pumping " + amountPump
                        + "l gas in process " + uuid);
            }

            availableGasPump.pumpGas(amountPump);

            amountRemainingProcess -= amountPump;

            availableGasPump.setWorking(false);
        }

        if (log.isInfoEnabled()) {
            log.info("--> Finish pumping gas in process " + uuid);
        }
    }

    private synchronized GasPump getAvailableGasPumpAndSetWorkingTrue(GasType type) {
        List<GasPump> gasPumps = get(type).getGasPumps();

        for (GasPump gasPump : gasPumps) {
            if (!gasPump.isWorking() && gasPump.getRemainingAmount() > 0d) {
                gasPump.setWorking(true);
                return gasPump;
            }
        }

        return null;
    }

    private void validate(GasType type, double amountInLiters, double maxPricePerLiter)
            throws GasTooExpensiveException, NotEnoughGasException {

        validateAmount(type, amountInLiters);
        validatePrice(type, maxPricePerLiter);
    }

    private void validateAmount(GasType type, double amountInLiters) throws NotEnoughGasException {
        GasStationGasTypeDto currentGasTypeStatus = get(type);

        if (currentGasTypeStatus == null || currentGasTypeStatus.getAmount().get() < amountInLiters) {
            gasStationStatus.getNumberOfCancellationsNoGas().set(
                    gasStationStatus.getNumberOfCancellationsNoGas().get() + 1);
            throw new NotEnoughGasException();
        }
    }

    private void validatePrice(GasType type, double maxPricePerLiter) throws GasTooExpensiveException {
        GasStationGasTypeDto currentGasTypeStatus = get(type);

        if (currentGasTypeStatus == null || currentGasTypeStatus.getPrice().get() > maxPricePerLiter) {
            gasStationStatus.getNumberOfCancellationsTooExpensive().set(
                    gasStationStatus.getNumberOfCancellationsTooExpensive().get() + 1);
            throw new GasTooExpensiveException();
        }
    }

    private GasStationGasTypeDto get(GasType type) {
        for (GasStationGasTypeDto gasStationGasType : gasStationGasTypes) {
            if (gasStationGasType.getGasType().equals(type)) {
                return gasStationGasType;
            }
        }

        return null;
    }

    private GasStationGasTypeDto getAndCreateIfNotExists(GasType type) {
        GasStationGasTypeDto currentGasTypeStatus = get(type);

        if (currentGasTypeStatus == null) {
            currentGasTypeStatus = new GasStationGasTypeDto();
            currentGasTypeStatus.setGasType(type);
            gasStationGasTypes.add(currentGasTypeStatus);
        }

        return currentGasTypeStatus;
    }

    @Override
    public double getRevenue() {
        return gasStationStatus.getTotalRevenue().get();
    }

    @Override
    public int getNumberOfSales() {
        return gasStationStatus.getNumberOfSuccessfulSales().get();
    }

    @Override
    public int getNumberOfCancellationsNoGas() {
        return gasStationStatus.getNumberOfCancellationsNoGas().get();
    }

    @Override
    public int getNumberOfCancellationsTooExpensive() {
        return gasStationStatus.getNumberOfCancellationsTooExpensive().get();
    }

    @Override
    public double getPrice(GasType type) {

        GasStationGasTypeDto currentGasTypeStatus = get(type);

        if (currentGasTypeStatus == null) {
            return 0d;
        }

        return currentGasTypeStatus.getPrice().get();
    }

    @Override
    public void setPrice(GasType type, double price) {
        GasStationGasTypeDto currentGasTypeStatus = getAndCreateIfNotExists(type);
        currentGasTypeStatus.getPrice().set(price);
    }
}
