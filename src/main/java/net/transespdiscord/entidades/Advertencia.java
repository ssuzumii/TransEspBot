package net.transespdiscord.entidades;

import jakarta.persistence.*;

import java.util.concurrent.ScheduledFuture;

@Entity
@Table(name = "advertencia")
public class Advertencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_advertencia", nullable = false)
    private Integer id;

    @Column(name = "id_usuarie", nullable = false, length = 20)
    private String idUsuarie;

    @Column(name = "motivo", nullable = false, length = 300)
    private String motivo;

    @Column(name = "id_responsable", nullable = false, length = 20)
    private String idResponsable;

    @Column(name = "unix_alta", nullable = false)
    private Long unixAlta;

    @Column(name = "unix_baja")
    private Long unixBaja;

    @Transient
    private ScheduledFuture tarea;

    public Advertencia() {
    }

    public Advertencia(String idUsuarie, String motivo, String idResponsable, Long unixAlta, Long unixBaja) {
        this.idUsuarie = idUsuarie;
        this.motivo = motivo;
        this.idResponsable = idResponsable;
        this.unixAlta = unixAlta;
        this.unixBaja = unixBaja;
    }

    public Long getUnixBaja() {
        return unixBaja;
    }

    public void setUnixBaja(Long unixBaja) {
        this.unixBaja = unixBaja;
    }

    public Long getUnixAlta() {
        return unixAlta;
    }

    public void setUnixAlta(Long unixAlta) {
        this.unixAlta = unixAlta;
    }

    public String getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(String idResponsable) {
        this.idResponsable = idResponsable;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getIdUsuarie() {
        return idUsuarie;
    }

    public void setIdUsuarie(String idUsuarie) {
        this.idUsuarie = idUsuarie;
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
}