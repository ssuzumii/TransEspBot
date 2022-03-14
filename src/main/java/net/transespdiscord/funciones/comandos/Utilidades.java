package net.transespdiscord.funciones.comandos;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.transespdiscord.TransEspBot;
import net.transespdiscord.crud.TemporizadorActivoCRUD;
import net.transespdiscord.entidades.TemporizadorActivo;
import net.transespdiscord.utilidades.GestorTemporizadores;

import java.time.Instant;
import java.util.List;

public class Utilidades {
    public static void fechaUnion(SlashCommandEvent evento) {
        evento.reply(evento.getUser().getAsTag() + " te has unido por última vez al servidor en <t:"
                + evento.getMember().getTimeJoined().toEpochSecond() + ":F>.").queue();
    }

    public static void crearTemporizador(SlashCommandEvent evento) throws Exception {
        evento.deferReply(true).queue();
        Member autor = evento.getMember();

        long cantidad = evento.getOption("cantidad").getAsLong();
        String unidad = evento.getOption("unidad").getAsString();
        long segundosUnix;

        if (unidad.equalsIgnoreCase("s")) {
            segundosUnix = cantidad;

        } else if (unidad.equalsIgnoreCase("m")) {
            segundosUnix = cantidad * 60;

        } else if (unidad.equalsIgnoreCase("h")) {
            segundosUnix = cantidad * 60 * 60;

        } else if (unidad.equalsIgnoreCase("d")) {
            segundosUnix = cantidad * 24 * 60 * 60;
        } else {
            evento.getHook()
                    .sendMessage("Las unidades admitidas son `s` (segundos), `m` (minutos), `h` (horas) y `d` (días)")
                    .setEphemeral(true).queue();
            return;
        }

        String nombreAlarma;

        if (evento.getOption("nombre") != null) {
            nombreAlarma = evento.getOption("nombre").getAsString().trim();
        } else {
            nombreAlarma = "SIN NOMBRE";
        }

        if (nombreAlarma.length() > 30) {
            evento.getHook()
                    .sendMessage("La longitud del nombre debe ser inferior a 30 caracteres.")
                    .setEphemeral(true).queue();
            return;
        }

        if (segundosUnix > 365 * 86400) {
            evento.getHook()
                    .sendMessage("El tiempo máximo para el que puedes crear una alarma es 1 año (365 días).")
                    .setEphemeral(true).queue();
            return;
        } else {
            segundosUnix += Instant.now().getEpochSecond();
        }

        TemporizadorActivo temporizador = new TemporizadorActivo(autor.getId(), nombreAlarma, segundosUnix);
        TransEspBot.temporizadores.add(temporizador);
        TemporizadorActivoCRUD.insertar(temporizador);
        GestorTemporizadores.programarTarea(temporizador);

        evento.getHook()
                .sendMessage("¡Temporizador creado con éxito! Te avisaré dentro de " + cantidad + " " + unidad + ".")
                .setEphemeral(true).queue();
    }

    public static void consultarTemporizador(SlashCommandEvent evento) throws Exception {
        evento.deferReply().setEphemeral(true).queue();
        List<TemporizadorActivo> temporizadores = TemporizadorActivoCRUD.obtenerPorIdSolicitante(evento.getMember().getId());

        if (temporizadores.isEmpty()) {
            evento.getHook()
                    .sendMessage("¡No tienes temporizadores activos!")
                    .setEphemeral(true).queue();
            return;
        } else {
            StringBuilder textoEnviar = new StringBuilder("Tienes los siguientes temporizadores activos:");

            for (TemporizadorActivo t : temporizadores) {
                textoEnviar.append("\n");
                textoEnviar.append("-> ID ").append(t.getId()).append(" - ");
                textoEnviar.append(t.getNombre()).append(" - ");

                long segundosRestantes = t.getTiempoUnix() - Instant.now().getEpochSecond();

                if (segundosRestantes >= 59 && segundosRestantes < 3600) {
                    long minutosRestantes = segundosRestantes / 60;
                    textoEnviar.append("Se ejecutará en ").append(minutosRestantes).append(" minuto(s)");
                    if (segundosRestantes % 60 >= 1) {
                        textoEnviar.append(" y ").append(segundosRestantes % 60).append(" segundo(s)");
                    }
                } else if (segundosRestantes >= 3600 && segundosRestantes < 86400) {
                    long horasRestantes = segundosRestantes / 3600;
                    textoEnviar.append("Se ejecutará en ").append(horasRestantes).append(" hora(s)");
                    if ((segundosRestantes - horasRestantes * 3600) / 60 >= 1) {
                        textoEnviar.append(" y ").append((segundosRestantes - horasRestantes * 3600) / 60).append(" minuto(s)");
                    }
                } else if (segundosRestantes >= 86400) {
                    long diasRestantes = segundosRestantes / 86400;
                    textoEnviar.append("Se ejecutará en ").append(diasRestantes).append(" día(s)");
                    if ((segundosRestantes - diasRestantes * 86400) / 60 >= 1) {
                        textoEnviar.append(" y ").append((segundosRestantes - diasRestantes * 86400) / 3600).append(" hora(s)");
                    }
                } else {
                    textoEnviar.append("Se ejecutará en ").append(segundosRestantes).append(" segundo(s)");
                }

            }

            evento.getHook()
                    .sendMessage(textoEnviar.toString())
                    .setEphemeral(true).queue();
        }
    }

    public static void eliminarTemporizador(SlashCommandEvent evento) throws Exception {
        evento.deferReply().setEphemeral(true).queue();
        long id = evento.getOption("id").getAsLong();

        if (id < 1) {
            evento.getHook()
                    .sendMessage("¡El ID no es válido!")
                    .setEphemeral(true).queue();
            return;
        }

        TemporizadorActivo temporizador = null;

        for (TemporizadorActivo t : TransEspBot.temporizadores) {
            if (t.getId() == id) {
                temporizador = t;
                break;
            }
        }

        if (temporizador == null) {
            evento.getHook()
                    .sendMessage("¡El ID no es válido!")
                    .setEphemeral(true).queue();
        } else if (!temporizador.getIdSolicitante().equals(evento.getMember().getId())) {
            evento.getHook()
                    .sendMessage("¡No puedes eliminar el temporizador de otra persona!")
                    .setEphemeral(true).queue();
        } else {
            TransEspBot.temporizadores.remove(temporizador);
            GestorTemporizadores.cancelarTarea(temporizador);
            TemporizadorActivoCRUD.eliminar(temporizador);

            evento.getHook()
                    .sendMessage("¡Temporizador eliminado con éxito!")
                    .setEphemeral(true).queue();

        }
    }
}
