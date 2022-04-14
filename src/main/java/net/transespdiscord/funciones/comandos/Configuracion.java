package net.transespdiscord.funciones.comandos;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.transespdiscord.crud.VariableCRUD;
import net.transespdiscord.entidades.Variable;
import net.transespdiscord.enums.TextosFijos;

public class Configuracion {
    public static void alternarAlertaBoost(SlashCommandEvent evento) {
        evento.deferReply(true).queue();

        if (!evento.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            evento.getHook().sendMessage(TextosFijos.COMANDO_SIN_PERMISO.texto).queue();
            return;
        }

        String nuevoValor = VariableCRUD.obtenerPorClave("aviso_boost").getValor().equals("0") ? "1" : "0";

        VariableCRUD.actualizar(new Variable("aviso_boost", nuevoValor));

        evento.getHook().sendMessage("Ahora las alertas de boost están **"
                + (nuevoValor.equals("0") ? "DESACTIVADAS" : "ACTIVADAS")
                + "**.").queue();
    }
}
