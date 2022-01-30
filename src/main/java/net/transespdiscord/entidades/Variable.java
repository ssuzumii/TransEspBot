package net.transespdiscord.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "variable")
public class Variable {
    @Id
    @Column(name = "clave", nullable = false, length = 15)
    private String id;

    @Column(name = "valor", nullable = false, length = 20)
    private String valor;

    public Variable() {
    }

    public Variable(String id, String valor) {
        this.id = id;
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "id='" + id + '\'' +
                ", valor='" + valor + '\'' +
                '}';
    }
}