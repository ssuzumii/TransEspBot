package net.transespdiscord.procesadores;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.transespdiscord.TransEspBot;
import net.transespdiscord.crud.VariableCRUD;
import net.transespdiscord.entidades.Variable;
import net.transespdiscord.enums.IdCanales;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProcesadorEntradaServidor extends ListenerAdapter {
    public static void programarTarea(JDA jda) {
        log.debug("Iniciando proceso de programación de tarea de aviso a novates...");
        final TextChannel canal = jda.getTextChannelById(IdCanales.BIENVENIDES.id);

        long segundos = Long.parseLong(VariableCRUD.obtenerPorClave("tiempo_novates").getValor()) - Instant.now().getEpochSecond();

        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            canal.getGuild().findMembers(m -> m.getRoles().size() == 0)
                    .onSuccess((listaMiembres) -> {
                        for (Member m : listaMiembres) {
                            if (m.getUser().isBot()) {
                                continue;
                            }

                            long tiempoEntrada = m.getTimeJoined().toEpochSecond();
                            long tiempoLimite = tiempoEntrada + (3 * 24 * 60 * 60);
                            long tiempoAhora = OffsetDateTime.now().toEpochSecond();

                            if (tiempoLimite > tiempoAhora) {
                                continue;
                            }

                            boolean enviadoEscrito = false;

                            for (Message msg : canal.getIterableHistory().stream().toList()) {
                                if (msg.getAuthor().getId().equals(m.getId())) {
                                    if (msg.getContentDisplay().toLowerCase().startsWith("enviado")) {
                                        enviadoEscrito = true;
                                        break;
                                    }
                                }
                            }

                            if (!enviadoEscrito) {
                                canal.sendMessage("***RECORDATORIO:*** " + m.getAsMention() + ", han pasado más "
                                        + "de 3 días desde que te has unido y no has dicho *enviado*.").queue();
                            }
                        }
                    });

            VariableCRUD.actualizar(new Variable("tiempo_novates",
                    "" + (Instant.now().getEpochSecond() + 24 * 60 * 60)));
        }, segundos, TimeUnit.SECONDS);

        log.debug("... Finalizado proceso de programación de tarea de aviso a novates.");
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        TextChannel canal = event.getGuild().getTextChannelById(IdCanales.BIENVENIDES.id);

        log.debug("Enviando mensaje de bienvenida...");
        canal.sendMessage("¡Bienvenide " + event.getMember().getAsMention() + " a Trans en Español! Debes seguir"
                + " las instrucciones que se indican en los mensajes fijados de este canal para unirte.\n\n"
                + "Haz clic en el enlace a continuación para ir directamente a las instrucciones:\n"
                + "https://discord.com/channels/550033353437347866/550035389654761479/924736096192036904").queue();

        log.debug("... Enviado mensaje de bienvenida.");

        // LOG
        log.info("Entrada de persona en el servidor: " + event.getUser().getAsTag() + " (" + event.getUser().getId() + ").");

        long segundosUnixCuenta = event.getMember().getTimeCreated().toEpochSecond();
        String cuentaNueva = "";

        if (Instant.now().getEpochSecond() - segundosUnixCuenta < 2629744) {
            cuentaNueva = ":warning: :warning: :warning: **LA CUENTA ES NUEVA** :warning: :warning: :warning:";
        }

        TextChannel logs = TransEspBot.jda.getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Entrada de persona en el servidor")
                .setColor(Color.orange)
                .addField("Usuarie:", event.getUser().getAsMention() + "\n" +
                        event.getUser().getAsTag() + " (" + event.getUser().getId() + ")\n" +
                        "Cuenta creada en: <t:" + segundosUnixCuenta + ":F>\n" + cuentaNueva, false)
                .setTimestamp(Instant.now());

        logs.sendMessageEmbeds(eb.build()).queue();
    }
}
