package net.bigpoint.assessment.gasstation;

import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import net.bigpoint.assessment.gasstation.impl.GasStationImpl;
import net.bigpoint.assessment.gasstation.util.ThreadUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class TestGasStationBuyGas {

    private static final Logger log = LoggerFactory.getLogger(TestGasStationBuyGas.class);

    @Test
    public void buyGasNoException() {
        if (log.isInfoEnabled()) {
            log.info("Test buyGasNoException ***");
        }
        GasStation gasStation = new GasStationImpl();

        GasPump dieselGasPump = new GasPump(GasType.DIESEL, 20d);
        gasStation.addGasPump(dieselGasPump);

        gasStation.setPrice(GasType.DIESEL, 1d);

        assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 20d, 4d));
    }

    @Test
    public void buyGasThrowNotEnoughGasException() {
        if (log.isInfoEnabled()) {
            log.info("Test buyGasThrowNotEnoughGasException ***");
        }

        GasStation gasStation = new GasStationImpl();

        GasPump dieselGasPump = new GasPump(GasType.DIESEL, 10d);
        gasStation.addGasPump(dieselGasPump);

        gasStation.setPrice(GasType.DIESEL, 1d);

        assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 10d, 4d));
        assertThrows(NotEnoughGasException.class, () -> gasStation.buyGas(GasType.DIESEL, 1d, 4d));

        assertEquals(1, gasStation.getNumberOfCancellationsNoGas());
    }

    @Test
    public void buyGasThrowGasTooExpensiveException() {
        if (log.isInfoEnabled()) {
            log.info("Test buyGasThrowGasTooExpensiveException ***");
        }

        GasStation gasStation = new GasStationImpl();

        GasPump dieselGasPump = new GasPump(GasType.DIESEL, 10d);
        gasStation.addGasPump(dieselGasPump);

        gasStation.setPrice(GasType.DIESEL, 6d);

        assertThrows(GasTooExpensiveException.class, () -> gasStation.buyGas(GasType.DIESEL, 10d, 5d));
        assertEquals(1, gasStation.getNumberOfCancellationsTooExpensive());
    }

    @Test
    public void buyGas4Clients() {
        if (log.isInfoEnabled()) {
            log.info("Test buyGas4Clients ***");
        }

        GasStation gasStation = new GasStationImpl();

        GasPump dieselGasPump1 = new GasPump(GasType.DIESEL, 200d);
        gasStation.addGasPump(dieselGasPump1);

        GasPump dieselGasPump2 = new GasPump(GasType.DIESEL, 150d);
        gasStation.addGasPump(dieselGasPump2);

        GasPump dieselGasPump3 = new GasPump(GasType.DIESEL, 100d);
        gasStation.addGasPump(dieselGasPump3);

        gasStation.setPrice(GasType.DIESEL, 1d);

        Thread client1 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 100d, 1d)));
        Thread client2 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 100d, 1d)));
        Thread client3 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 150d, 1d)));
        Thread client4 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 100d, 1d)));

        client1.start();
        client2.start();
        client3.start();
        client4.start();

        ThreadUtil.sleep(250);
    }

    @Test
    public void buyGas4Clients2Types() {
        if (log.isInfoEnabled()) {
            log.info("Test buyGas4Clients2Types ***");
        }

        GasStation gasStation = new GasStationImpl();

        GasPump dieselGasPump1 = new GasPump(GasType.DIESEL, 50d);
        gasStation.addGasPump(dieselGasPump1);

        GasPump dieselGasPump2 = new GasPump(GasType.DIESEL, 50d);
        gasStation.addGasPump(dieselGasPump2);

        GasPump regularGasPump3 = new GasPump(GasType.REGULAR, 100d);
        gasStation.addGasPump(regularGasPump3);

        gasStation.setPrice(GasType.DIESEL, 1d);
        gasStation.setPrice(GasType.REGULAR, 1d);

        Thread client1 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 50d, 1d)));
        Thread client2 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 50d, 1d)));

        Thread client3 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.REGULAR, 60d, 1d)));
        Thread client4 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.REGULAR, 40d, 1d)));

        client1.start();
        client2.start();
        client3.start();
        client4.start();

        ThreadUtil.sleep(250);
    }

    /**
     * 3 clients are buying gas
     * Then, add one more pump and one more client will buy
     */
    @Test
    public void buyGas3ClientsAndThenAdd1Pump1CLient() {
        GasStation gasStation = new GasStationImpl();

        GasPump dieselGasPump1 = new GasPump(GasType.DIESEL, 50d);
        gasStation.addGasPump(dieselGasPump1);

        GasPump dieselGasPump2 = new GasPump(GasType.DIESEL, 50d);
        gasStation.addGasPump(dieselGasPump2);

        GasPump dieselGasPump3 = new GasPump(GasType.DIESEL, 100d);
        gasStation.addGasPump(dieselGasPump3);

        gasStation.setPrice(GasType.DIESEL, 1d);

        Thread client1 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 50d, 1d)));
        Thread client2 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 50d, 1d)));
        Thread client3 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 80d, 1d)));

        client1.start();
        client2.start();
        client3.start();
        ThreadUtil.sleep(30);

        GasPump dieselGasPump4 = new GasPump(GasType.DIESEL, 100d);
        gasStation.addGasPump(dieselGasPump4);

        Thread client4 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 100d, 1d)));

        client4.start();

        ThreadUtil.sleep(250);
    }
}
