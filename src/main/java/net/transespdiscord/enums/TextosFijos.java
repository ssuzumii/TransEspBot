package net.transespdiscord.enums;

public enum TextosFijos {

    COMANDO_SIN_PERMISO("No tienes permiso para ejecutar este comando."),
    COMANDO_NO_RECONOCIDO("El comando especificado no es válido."),
    ERROR_BD("Error en BD. Contacta con la desarrolladora.");

    public final String texto;

    TextosFijos(String mensaje) {
        this.texto = mensaje;
    }
}
