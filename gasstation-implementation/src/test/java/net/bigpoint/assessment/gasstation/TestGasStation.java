package net.bigpoint.assessment.gasstation;

import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import net.bigpoint.assessment.gasstation.impl.GasStationImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TestGasStation {

    private static final Logger log = LoggerFactory.getLogger(TestGasStation.class);

    @Test
    public void addGas() {
        if (log.isInfoEnabled()) {
            log.info("Test addGas ***");
        }

        GasStation gasStation = new GasStationImpl();

        GasPump dieselGasPump = new GasPump(GasType.DIESEL, 20d);

        assertDoesNotThrow(() -> gasStation.addGasPump(dieselGasPump));
    }

    @Test
    public void getGasPumps() {
        if (log.isInfoEnabled()) {
            log.info("Test getGasPumps ***");
        }

        GasStation gasStation = new GasStationImpl();

        GasPump dieselGasPump = new GasPump(GasType.DIESEL, 20d);
        gasStation.addGasPump(dieselGasPump);

        GasPump regularGasPump = new GasPump(GasType.REGULAR, 20d);
        gasStation.addGasPump(regularGasPump);

        Assertions.assertEquals(2, gasStation.getGasPumps().size());
    }

    @Test
    public void getRevenue() throws GasTooExpensiveException, NotEnoughGasException {
        if (log.isInfoEnabled()) {
            log.info("Test getRevenue ***");
        }

        double price = 1d;
        double buyAmount = 20d;

        GasStation gasStation = new GasStationImpl();

        GasPump dieselGasPump = new GasPump(GasType.DIESEL, buyAmount);
        gasStation.addGasPump(dieselGasPump);

        gasStation.setPrice(GasType.DIESEL, price);

        gasStation.buyGas(GasType.DIESEL, buyAmount, price);

        Assertions.assertEquals(buyAmount * price, gasStation.getRevenue());
    }

    @Test
    public void getNumberOfSales() throws GasTooExpensiveException, NotEnoughGasException {
        if (log.isInfoEnabled()) {
            log.info("Test getNumberOfSales ***");
        }

        double price = 1d;
        double buyAmount = 20d;

        GasStation gasStation = new GasStationImpl();

        GasPump dieselGasPump = new GasPump(GasType.DIESEL, buyAmount);
        gasStation.addGasPump(dieselGasPump);

        gasStation.setPrice(GasType.DIESEL, price);

        gasStation.buyGas(GasType.DIESEL, buyAmount, price);

        Assertions.assertEquals(1d, gasStation.getNumberOfSales());
    }

    @Test
    public void getPrice() {
        if (log.isInfoEnabled()) {
            log.info("Test getPrice ***");
        }

        double price = 10d;

        GasStation gasStation = new GasStationImpl();

        gasStation.setPrice(GasType.DIESEL, price);

        Assertions.assertEquals(price, gasStation.getPrice(GasType.DIESEL));
    }

    @Test
    public void setPrice() {
        if (log.isInfoEnabled()) {
            log.info("Test setPrice ***");
        }

        GasStation gasStation = new GasStationImpl();

        assertDoesNotThrow(() -> gasStation.setPrice(GasType.DIESEL, 10d));
    }
}
