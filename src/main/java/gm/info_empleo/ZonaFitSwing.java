package gm.info_empleo;

import com.formdev.flatlaf.FlatDarculaLaf;
import gm.info_empleo.gui.InfoEmpleoForma;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.*;

//@SpringBootApplication
public class InfoEmpleoSwing {
    public static void main(String[] args) {
        // configuramos el modo oscuro
        FlatDarculaLaf.setup();
        // Instancia la fabrica de spring
        ConfigurableApplicationContext contextoSpring =
                new SpringApplicationBuilder(InfoEmpleoSwing.class)
                        .headless(false)
                        .web(WebApplicationType.NONE)
                        .run(args);
        // Crear un objeto de Swing
        SwingUtilities.invokeLater(()-> {
            InfoEmpleoForma zonaFitForma = contextoSpring.getBean(InfoEmpleoForma.class);
            zonaFitForma.setVisible(true);
        });
    }
}
