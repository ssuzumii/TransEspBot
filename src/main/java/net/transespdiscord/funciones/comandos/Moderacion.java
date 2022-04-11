package net.transespdiscord.funciones.comandos;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.transespdiscord.TransEspBot;
import net.transespdiscord.crud.AdvertenciaCRUD;
import net.transespdiscord.entidades.Advertencia;
import net.transespdiscord.enums.IdCanales;
import net.transespdiscord.enums.TextosFijos;
import net.transespdiscord.utilidades.GestorAdvertencias;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Moderacion {
    public static void crearAdvertencia(SlashCommandEvent evento) {
        evento.deferReply(true).queue();
        User autor = evento.getUser();

        if (!evento.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            evento.getHook().sendMessage(TextosFijos.COMANDO_SIN_PERMISO.texto).queue();
            return;
        }

        User usuarie = evento.getOption("usuarie").getAsUser();
        String motivo = evento.getOption("motivo").getAsString();

        if (usuarie.isBot()) {
            evento.getHook().sendMessage("No puedes poner una advertencia a un bot.").queue();
            return;
        }

        if (motivo.length() > 300) {
            evento.getHook().sendMessage("El motivo debe tener un máximo de 300 caracteres.").queue();
            return;
        }

        // Objeto para poder hacer null
        Long segundosUnix;

        if (evento.getOption("tiempo") != null && evento.getOption("unidad_tiempo") != null) {
            long tiempo = evento.getOption("tiempo").getAsLong();
            String unidad = evento.getOption("unidad_tiempo").getAsString();

            if (unidad.equalsIgnoreCase("h")) {
                segundosUnix = tiempo * 60 * 60;
            } else if (unidad.equalsIgnoreCase("d")) {
                segundosUnix = tiempo * 24 * 60 * 60;
            } else if (unidad.equalsIgnoreCase("m")) {
                segundosUnix = tiempo * 30 * 24 * 60 * 60;
            } else if (unidad.equalsIgnoreCase("a")) {
                segundosUnix = tiempo * 365 * 24 * 60 * 60;
            } else {
                evento.getHook().sendMessage("Las unidades admitidas son `h` (horas), `d` (días), `m` (meses) y `a` (años).").queue();
                return;
            }


            if (segundosUnix > 20 * 365 * 86400) {
                evento.getHook().sendMessage("El tiempo máximo que puede esperar una advertencia para expirar es 20 años.").queue();
                return;
            } else {
                segundosUnix += Instant.now().getEpochSecond();
            }
        } else if (evento.getOption("tiempo") != null || evento.getOption("unidad_tiempo") != null) {
            evento.getHook().sendMessage("Si especificas tiempo o unidad de tiempo debes especificar también el otro.").queue();
            return;
        } else {
            segundosUnix = null;
        }

        Advertencia advertencia = new Advertencia(usuarie.getId(), motivo, autor.getId(), Instant.now().getEpochSecond(), segundosUnix);
        AdvertenciaCRUD.insertar(advertencia);
        GestorAdvertencias.programarTarea(advertencia);

        // FORMACIÓN y ENVÍO DE EMBED Y MD
        Guild servidor = TransEspBot.jda.getGuilds().get(0);
        TextChannel logs = servidor.getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Alta de advertencia #" + advertencia.getId())
                .setColor(Color.red)
                .addField("Usuarie:", usuarie.getAsMention() + "\n" + usuarie.getAsTag(), true)
                .addField("Responsable:", autor.getAsMention() + "\n" + autor.getAsTag(), true)
                .addField("Motivo:", motivo, false)
                .setTimestamp(Instant.now());

        if (segundosUnix != null) {
            eb.addField("Expira:", "<t:" + segundosUnix + ":F>", false);
        }

        logs.sendMessageEmbeds(eb.build()).queue();
        log.info("Advertencia creada para " + usuarie.getAsTag() + " por " + autor.getAsTag() + ".");

        String extraMensaje = "";

        if (servidor.isMember(usuarie)) {
            usuarie.openPrivateChannel().queue((canalPrivado) -> canalPrivado.sendMessageEmbeds(eb.build()).queue());

            extraMensaje += "MD enviado. ";
        } else {
            extraMensaje += "MD no enviado. No está en servidor. ";
        }

        int resultadoSancion = GestorAdvertencias.comprobarSanciones(usuarie);

        if (resultadoSancion == 1) {
            extraMensaje += "Miembre baneade.";
        } else if (resultadoSancion == 0) {
            extraMensaje += "Aún no corresponde baneo automático.";
        } else if (resultadoSancion == -1) {
            extraMensaje += "Corresponde baneo automático pero miembre no está en servidor. Imposible banear.";
        } else if (resultadoSancion == -2) {
            extraMensaje += "Corresponde baneo automático pero usuarie es más poderose que yo. Imposible banear.";
        }

        evento.getHook().sendMessage("¡Advertencia dada con éxito! " + extraMensaje).queue();
    }

    public static void consultarAdvertencia(SlashCommandEvent evento) {
        evento.deferReply().setEphemeral(true).queue();

        if (!evento.getMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            evento.getHook().sendMessage(TextosFijos.COMANDO_SIN_PERMISO.texto).queue();
            return;
        }

        StringBuilder lista = new StringBuilder();
        List<Advertencia> advertencias;

        if (evento.getOption("usuarie") != null) { // Si no es nulo, quiere comprobar de usuarie...
            User usuarie = evento.getOption("usuarie").getAsUser();

            advertencias = AdvertenciaCRUD.obtenerPorIdUsuarie(usuarie.getId());

            if (advertencias.isEmpty()) {
                evento.getHook().sendMessage("¡Usuarie no tiene advertencias!").queue();
                return;
            } else {
                for (Advertencia a : advertencias) {
                    lista.append("**#" + a.getId() + " | Alta en <t:" + a.getUnixAlta() + ":F>**\n"
                            + "- Responsable: " + evento.getJDA().retrieveUserById(a.getIdResponsable()).complete().getAsMention() + ".\n"
                            + "- Motivo: " + a.getMotivo() + "\n");

                    if (a.getUnixBaja() != null) {
                        lista.append("- Expira: <t:" + a.getUnixBaja() + ":F>\n");
                    }

                    lista.append("\n");
                }
            }
        } else { // ... Si es nulo quiere comprobar de servidor
            advertencias = AdvertenciaCRUD.listar();

            if (advertencias.isEmpty()) {
                evento.getHook().sendMessage("¡No hay advertencias!").queue();
                return;
            } else {
                HashMap<String, Integer> mapa = new HashMap<>();

                for (Advertencia a : advertencias) {
                    if (mapa.containsKey(a.getIdUsuarie())) {
                        mapa.put(a.getIdUsuarie(), mapa.get(a.getIdUsuarie()) + 1);
                    } else {
                        mapa.put(a.getIdUsuarie(), 1);
                    }
                }

                for (Map.Entry<String, Integer> e : mapa.entrySet()) {
                    lista.append("- " + evento.getJDA().retrieveUserById(e.getKey()).complete().getAsMention()
                            + " (" + e.getKey() + ")"
                            + " - " + e.getValue().toString() + "\n");
                }
            }
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Consulta advertencias")
                .setColor(Color.cyan)
                .addField("Resultado | " + advertencias.size() + " encontradas", lista.toString(), true)
                .setTimestamp(Instant.now());

        evento.getHook().sendMessageEmbeds(eb.build()).queue();
    }

    public static void eliminarAdvertencia(SlashCommandEvent evento) {
        evento.deferReply().setEphemeral(true).queue();

        if (!evento.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            evento.getHook().sendMessage(TextosFijos.COMANDO_SIN_PERMISO.texto).queue();
            return;
        }

        int id = (int) evento.getOption("identificador").getAsLong();

        if (id < 1) {
            evento.getHook()
                    .sendMessage("¡El identificador no es válido!")
                    .setEphemeral(true).queue();
            return;
        }

        Advertencia advertencia = AdvertenciaCRUD.obtenerPorId(id);

        if (advertencia == null) {
            evento.getHook()
                    .sendMessage("¡El identificador no es válido!")
                    .setEphemeral(true).queue();
        } else {
            GestorAdvertencias.bajaAdvertencia(advertencia);
            evento.getHook().sendMessage("¡Advertencia eliminada con éxito!").queue();
        }
    }

    public static void limpiarAdvertencias(SlashCommandEvent evento) {
        evento.deferReply().setEphemeral(true).queue();

        List<Advertencia> advertencias = AdvertenciaCRUD.obtenerPorIdUsuarie(evento.getOption("usuarie").getAsUser().getId());

        if (!advertencias.isEmpty()) {
            for (Advertencia a : advertencias) {
                GestorAdvertencias.bajaAdvertencia(a);
            }

            evento.getHook().sendMessage("¡Advertencias de usuarie limpiadas con éxito!").queue();
        } else {
            evento.getHook().sendMessage("Imposible limpiar - usuarie no tiene advertencias.").queue();
        }
    }
}
