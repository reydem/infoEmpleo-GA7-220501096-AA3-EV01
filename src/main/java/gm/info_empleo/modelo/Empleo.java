package gm.info_empleo.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "info_empleo") // Asegura que el nombre de la tabla coincida con la BD
public class Empleo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String nombre;  // Nombre de la persona que publica el empleo

    @Column(nullable = false, length = 255)
    private String empresa; // Nombre de la empresa

    @Column(nullable = false, length = 500)
    private String descripcion; // Descripción del empleo

    @Column(nullable = false)
    private double salario; // Salario del empleo
}
