package com.aec.statssrv;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.aec.statssrv.StatsServiceApplication;

// Le decimos explícitamente a Spring qué clase arrancar
@SpringBootTest(classes = StatsServiceApplication.class)
class AecApplicationTests {

    @Test
    void contextLoads() {
        // Si la aplicación arranca sin errores, este test pasa.
    }
}

