package net.transespdiscord.funciones.comandos;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.transespdiscord.enums.IdCanales;
import net.transespdiscord.enums.IdRoles;
import net.transespdiscord.enums.TextosFijos;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

public class Administrativos {
    public static void bienvenide(SlashCommandEvent evento) {
        evento.deferReply(true).queue();
        Guild servidor = evento.getGuild();

        TextChannel canal = evento.getTextChannel();

        TextChannel presentaciones = servidor.getTextChannelById(IdCanales.PRESENTACIONES.id);
        TextChannel charla_bots = servidor.getTextChannelById(IdCanales.CHARLA_CON_BOTS.id);
        TextChannel roles = servidor.getTextChannelById(IdCanales.ROLES.id);
        Role ping_bienvenides = servidor.getRoleById(IdRoles.PING_BIENVENIDES.id);


        if (!evento.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            evento.getHook().sendMessage(TextosFijos.COMANDO_SIN_PERMISO.texto).queue();
            return;
        }

        Member novateMiembre = null;


        if (evento.getOption("usuarie") != null) {
            novateMiembre = evento.getOption("usuarie").getAsMember();
        }

        if (novateMiembre == null) {
            evento.getHook().sendMessage("La persona especificada no está en el servidor.").queue();
            return;
        }

        String pronombre = "e";

        if (evento.getOption("pronombre") == null) {
            for (Role r : novateMiembre.getRoles()) {
                String id = r.getId();
                if (IdRoles.PRONOMBRES_FEMENINOS.id.equals(id)) {
                    pronombre = "a";
                    break;
                } else if (IdRoles.PRONOMBRES_MASCULINOS.id.equals(id)) {
                    pronombre = "o";
                    break;
                } else if (IdRoles.PRONOMBRES_NEUTROS.id.equals(id)) {
                    pronombre = "e";
                    break;
                }
            }
        } else {
            if (evento.getOption("pronombre").getAsString().length() > 4) {
                evento.getHook().sendMessage("El pronombre debe ser de un máximo de 4 caracteres.").queue();
                return;
            } else {
                pronombre = evento.getOption("pronombre").getAsString().toLowerCase();
            }
        }

        try {
            canal.sendMessage("¡Bienvenid" + pronombre + " " + novateMiembre.getAsMention() + " a Trans en Español! \n" +
                    "Si lo deseas, puedes realizar una presentación de ti mism" + pronombre + " en el canal " + presentaciones.getAsMention() + "\n" +
                    "Para cambiar el color de tu nombre o autoasignarte roles, acude a " + charla_bots.getAsMention() + " o " + roles.getAsMention() + "\n" +
                    "Si tienes cualquier duda o problema con el servidor contacta con cualquier persona del Equipo Administrativo y se resolverá cuanto antes.\n" +
                    ping_bienvenides.getAsMention()).queue();
            evento.getHook().sendMessage("Bienvenida dada con éxito.").queue();
        } catch (NullPointerException e) {
            evento.getHook().sendMessage("NullPointerException en mensaje de bienvenida. Contacta con la desarrolladora.").queue();
            e.printStackTrace();
        }
    }

    public static void purgar(SlashCommandEvent evento) {
        evento.deferReply(true).queue();
        Guild servidor = evento.getGuild();
        Member miembre = evento.getMember();

        int cantidad = (int) evento.getOption("cantidad").getAsLong();

        User usuarieObjetivo = null;

        if (evento.getOption("usuarie") != null) {
            usuarieObjetivo = evento.getOption("usuarie").getAsUser();
        }

        TextChannel logs = servidor.getTextChannelById(IdCanales.LOGS.id);

        if (!miembre.hasPermission(Permission.MESSAGE_MANAGE)) {
            evento.getHook().sendMessage(TextosFijos.COMANDO_SIN_PERMISO.texto).queue();
            return;
        }

        if (evento.getChannel().getIterableHistory().stream().toList().isEmpty()) {
            evento.getHook().sendMessage("Imposible borrar: el canal está vacío.").queue();
            return;
        }

        if (usuarieObjetivo != null) {
            final User USUARIE_OBJETIVO = usuarieObjetivo;
            ArrayList<Message> mensajes = new ArrayList<>();

            evento.getChannel().getIterableHistory()
                    .stream()
                    .filter(msg -> msg.getAuthor().getId().equals(USUARIE_OBJETIVO.getId()))
                    .limit(cantidad)
                    .forEach(mensajeEliminar -> {
                        mensajes.add(mensajeEliminar);

                        EmbedBuilder eb = new EmbedBuilder()
                                .setTitle("Mensaje eliminado por PURGAR")
                                .setColor(Color.red)
                                .addField("Contenido:", mensajeEliminar.getContentDisplay(), false)
                                .addField("Escrito por:", mensajeEliminar.getAuthor().getAsTag() + " ("
                                        + mensajeEliminar.getAuthor().getId() + ")", false)
                                .addField("Borrado por:", evento.getMember().getUser().getAsTag() + " ("
                                        + evento.getMember().getUser().getId() + ")", false)
                                .setTimestamp(Instant.now());

                        logs.sendMessageEmbeds(eb.build()).queue();
                    });

            if (mensajes.size() > 0) {
                evento.getChannel().purgeMessages(mensajes);
                evento.getHook().sendMessage(mensajes.size() + " mensajes eliminados con éxito.").queue();
            } else {
                evento.getHook().sendMessage("No hay mensajes que borrar.").queue();
            }

        } else {
            evento.getChannel().getIterableHistory()
                    .takeAsync(cantidad)
                    .thenAccept(mensajes -> {
                                for (Message mensajeEliminar : mensajes) {
                                    EmbedBuilder eb = new EmbedBuilder()
                                            .setTitle("Mensaje eliminado por PURGAR")
                                            .setColor(Color.red)
                                            .addField("Contenido:", mensajeEliminar.getContentDisplay(), false)
                                            .addField("Escrito por:", mensajeEliminar.getAuthor().getAsTag() + " ("
                                                    + mensajeEliminar.getAuthor().getId() + ")", false)
                                            .addField("Borrado por:", evento.getMember().getUser().getAsTag() + " ("
                                                    + evento.getMember().getUser().getId() + ")", false)
                                            .setTimestamp(Instant.now());

                                    logs.sendMessageEmbeds(eb.build()).queue();
                                }

                                evento.getChannel().purgeMessages(mensajes);

                                evento.getHook().sendMessage(mensajes.size() + " mensajes eliminados con éxito.").queue();
                            }
                    );
        }
    }
}
