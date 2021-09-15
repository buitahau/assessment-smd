package net.bigpoint.assessment.gasstation;

import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import net.bigpoint.assessment.gasstation.impl.GasStationImplementation;
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
        GasStation gasStation = new GasStationImplementation();

        GasPump dieselGasPump = new GasPump(GasType.DIESEL, 20d);
        dieselGasPump.setId("1");
        gasStation.addGasPump(dieselGasPump);

        gasStation.setPrice(GasType.DIESEL, 1d);

        assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 20d, 4d));
    }

    @Test
    public void buyGasThrowNotEnoughGasException() {
        if (log.isInfoEnabled()) {
            log.info("Test buyGasThrowNotEnoughGasException ***");
        }

        GasStation gasStation = new GasStationImplementation();

        GasPump dieselGasPump = new GasPump(GasType.DIESEL, 10d);
        dieselGasPump.setId("1");
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

        GasStation gasStation = new GasStationImplementation();

        GasPump dieselGasPump = new GasPump(GasType.DIESEL, 10d);
        dieselGasPump.setId("1");
        gasStation.addGasPump(dieselGasPump);

        gasStation.setPrice(GasType.DIESEL, 6d);

        assertThrows(GasTooExpensiveException.class, () -> gasStation.buyGas(GasType.DIESEL, 10d, 5d));
        assertEquals(1, gasStation.getNumberOfCancellationsTooExpensive());
    }

    @Test
    public void buyGas5Clients() {
        if (log.isInfoEnabled()) {
            log.info("Test buyGas5Clients ***");
        }

        GasStation gasStation = new GasStationImplementation();

        GasPump dieselGasPump1 = new GasPump(GasType.DIESEL, 50d);
        dieselGasPump1.setId("1");
        gasStation.addGasPump(dieselGasPump1);

        GasPump dieselGasPump2 = new GasPump(GasType.DIESEL, 50d);
        dieselGasPump2.setId("2");
        gasStation.addGasPump(dieselGasPump2);

        GasPump dieselGasPump3 = new GasPump(GasType.DIESEL, 100d);
        dieselGasPump3.setId("3");
        gasStation.addGasPump(dieselGasPump3);

        gasStation.setPrice(GasType.DIESEL, 1d);

        Thread client1 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 130d, 1d)));
        Thread client2 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 25d, 1d)));
        Thread client3 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 25d, 1d)));
        Thread client4 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 10d, 1d)));
        Thread client5 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 10d, 1d)));

        client1.start();
        client2.start();
        client3.start();
        client4.start();
        client5.start();

        ThreadUtil.sleep(250);
    }

    @Test
    public void buyGas4Clients2Types() {
        if (log.isInfoEnabled()) {
            log.info("Test buyGas4Clients2Types ***");
        }

        GasStation gasStation = new GasStationImplementation();

        GasPump dieselGasPump1 = new GasPump(GasType.DIESEL, 50d);
        dieselGasPump1.setId("1");
        gasStation.addGasPump(dieselGasPump1);

        GasPump dieselGasPump2 = new GasPump(GasType.DIESEL, 50d);
        dieselGasPump2.setId("2");
        gasStation.addGasPump(dieselGasPump2);

        GasPump regularGasPump3 = new GasPump(GasType.REGULAR, 100d);
        regularGasPump3.setId("3");
        gasStation.addGasPump(regularGasPump3);

        gasStation.setPrice(GasType.DIESEL, 1d);
        gasStation.setPrice(GasType.REGULAR, 1d);

        Thread client1 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 70d, 1d)));
        Thread client2 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 30d, 1d)));

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
        GasStation gasStation = new GasStationImplementation();

        GasPump dieselGasPump1 = new GasPump(GasType.DIESEL, 50d);
        dieselGasPump1.setId("1");
        gasStation.addGasPump(dieselGasPump1);

        GasPump dieselGasPump2 = new GasPump(GasType.DIESEL, 50d);
        dieselGasPump2.setId("2");
        gasStation.addGasPump(dieselGasPump2);

        GasPump dieselGasPump3 = new GasPump(GasType.DIESEL, 100d);
        dieselGasPump3.setId("3");
        gasStation.addGasPump(dieselGasPump3);

        gasStation.setPrice(GasType.DIESEL, 1d);

        Thread client1 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 60d, 1d)));
        Thread client2 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 40d, 1d)));
        Thread client3 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 80d, 1d)));

        client1.start();
        client2.start();
        client3.start();
        ThreadUtil.sleep(30);

        GasPump dieselGasPump4 = new GasPump(GasType.DIESEL, 100d);
        dieselGasPump4.setId("4");
        gasStation.addGasPump(dieselGasPump4);

        Thread client4 = new Thread(() -> assertDoesNotThrow(() -> gasStation.buyGas(GasType.DIESEL, 120d, 1d)));

        client4.start();

        ThreadUtil.sleep(250);
    }
}
