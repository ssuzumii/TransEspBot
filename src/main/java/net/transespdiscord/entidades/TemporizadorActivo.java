package net.transespdiscord.entidades;

import jakarta.persistence.*;

import java.util.concurrent.ScheduledFuture;

@Entity
@Table(name = "temporizador_activo")
public class TemporizadorActivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_solicitante", nullable = false, length = 20)
    private String idSolicitante;

    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @Column(name = "tiempo_unix", nullable = false)
    private long tiempoUnix;

    @Transient
    private ScheduledFuture tarea;

    public TemporizadorActivo() {
    }

    public TemporizadorActivo(String idSolicitante, String nombre, long tiempoUnix) {
        this.idSolicitante = idSolicitante;
        this.nombre = nombre;
        this.tiempoUnix = tiempoUnix;
    }

    public long getTiempoUnix() {
        return tiempoUnix;
    }

    public void setTiempoUnix(long tiempoUnix) {
        this.tiempoUnix = tiempoUnix;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdSolicitante() {
        return idSolicitante;
    }

    public void setIdSolicitante(String idSolicitante) {
        this.idSolicitante = idSolicitante;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ScheduledFuture getTarea() {
        return tarea;
    }

    public void setTarea(ScheduledFuture tarea) {
        this.tarea = tarea;
    }

    @Override
    public String toString() {
        return "TemporizadorActivo{" +
                "id=" + id +
                ", idSolicitante='" + idSolicitante + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tiempoUnix='" + tiempoUnix + '\'' +
                '}';
    }
}