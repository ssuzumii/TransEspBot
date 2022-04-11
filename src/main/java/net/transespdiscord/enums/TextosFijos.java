package net.transespdiscord.enums;

public enum TextosFijos {

    NOMBRE_BOT("Butterfree"),
    COMANDO_SIN_PERMISO("No tienes permiso para ejecutar este comando.");

    public final String texto;

    TextosFijos(String mensaje) {
        this.texto = mensaje;
    }
}
