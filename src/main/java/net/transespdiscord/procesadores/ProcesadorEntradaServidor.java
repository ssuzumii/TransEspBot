package net.transespdiscord.procesadores;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.transespdiscord.crud.VariableCRUD;
import net.transespdiscord.entidades.Variable;
import net.transespdiscord.enums.IdCanales;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProcesadorEntradaServidor extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        TextChannel canal = event.getGuild().getTextChannelById(IdCanales.BIENVENIDES.id);

        canal.sendMessage("¡Bienvenide " + event.getMember().getAsMention() + " a Trans en Español! Debes seguir"
                + " las instrucciones que se indican en los mensajes fijados de este canal para unirte.").queue();

    }

    public static void programarTarea(JDA jda) {
        final TextChannel canal = jda.getTextChannelById(IdCanales.BIENVENIDES.id);

        long segundos = Long.parseLong(VariableCRUD.obtenerPorClave("tiempo_novates").getValor()) - Instant.now().getEpochSecond();

        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            canal.getGuild().findMembers(m -> m.getRoles().size() == 0)
                    .onSuccess((listaMiembres) -> {
                        for (Member m : listaMiembres) {
                            if(m.getUser().isBot()){
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
    }
}
