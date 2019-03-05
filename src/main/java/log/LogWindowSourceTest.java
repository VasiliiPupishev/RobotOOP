package log;

import static org.junit.jupiter.api.Assertions.*;

class LogWindowSourceTest {
    LogWindowSource logWS = new LogWindowSource(10);

    @org.junit.jupiter.api.Test
    void tryAddMaximalCountOfItem() {
        LogWindowSource logWS = new LogWindowSource(10);
        addItems(logWS, 10);
        assertEquals(10, logWS.size());
    }

    @org.junit.jupiter.api.Test
    void tryAddTooMachItem() {
        LogWindowSource logWS = new LogWindowSource(10);
        addItems(logWS, 16);
        assertEquals(10, logWS.size());
    }

    @org.junit.jupiter.api.Test
    void tryAddLessThanMaxItem() {
        LogWindowSource logWS = new LogWindowSource(10);
        addItems(logWS, 5);
        assertEquals(10, logWS.size());
    }

    void addItems(LogWindowSource l, int count){
        for (int i = 0; i < count; i++){
            l.append(LogLevel.Debug, Integer.toString(i));
        }
    }
}