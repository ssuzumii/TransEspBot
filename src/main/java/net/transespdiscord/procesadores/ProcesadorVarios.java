package net.transespdiscord.procesadores;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.transespdiscord.TransEspBot;
import net.transespdiscord.enums.IdCanales;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;

import static net.transespdiscord.enums.TextosFijos.NOMBRE_BOT;

@Slf4j
public class ProcesadorVarios extends ListenerAdapter {
    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        super.onPrivateMessageReceived(event);

        if (!event.getAuthor().isBot()) {
            log.info("MD recibido: " + event.getMessage().getContentDisplay() + " | escrito por " +
                    event.getAuthor().getAsTag() + " (" + event.getAuthor().getId() + ")");

            TextChannel logs = TransEspBot.jda.getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("MD recibido en cuenta de " + NOMBRE_BOT.texto)
                    .setColor(Color.cyan)
                    .addField("Contenido:", event.getMessage().getContentDisplay(), false)
                    .addField("Escrito por:", event.getAuthor().getAsTag() + " ("
                            + event.getAuthor().getId() + ")", false)
                    .setTimestamp(Instant.now());

            logs.sendMessageEmbeds(eb.build()).queue();

            event.getMessage().reply("No se aceptan mensajes por este canal, por favor, habla con el equipo administrativo.").queue();
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);
        log.info(NOMBRE_BOT.texto + ": bot conectado y listo para funcionamiento.");

        TextChannel logs = event.getJDA().getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(NOMBRE_BOT.texto + ": bot conectado y listo para funcionamiento")
                .setColor(Color.yellow)
                .setTimestamp(Instant.now());

        logs.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onReconnected(@NotNull ReconnectedEvent event) {
        super.onReconnected(event);
        log.info(NOMBRE_BOT.texto + ": bot reconectado con éxito.");

        TextChannel logs = TransEspBot.jda.getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(NOMBRE_BOT.texto + ": bot reconectado con éxito")
                .setColor(Color.yellow)
                .setTimestamp(Instant.now());

        logs.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event) {
        super.onDisconnect(event);

        log.info(NOMBRE_BOT.texto + ": bot desconectado de Discord. Intentando reconectar...");
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        super.onShutdown(event);
        log.info(NOMBRE_BOT.texto + ": bot desconectado, imposible reconectar. Se requiere revisión y reinicio manual.");
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        super.onGuildMemberRemove(event);

        log.info("Alguien ha abandonado el servidor: " + event.getUser().getAsTag() + " (" + event.getUser().getId() + ").");

        TextChannel logs = TransEspBot.jda.getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Alguien ha abandonado el servidor")
                .setColor(Color.red)
                .addField("Usuarie:", event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", false)
                .setTimestamp(Instant.now());

        logs.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        super.onGuildMemberJoin(event);
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

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        super.onGuildBan(event);

        log.info("Baneo en servidor: " + event.getUser().getAsTag() + " (" + event.getUser().getId() + ").");

        TextChannel logs = TransEspBot.jda.getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Baneo en servidor")
                .setColor(Color.red)
                .addField("Usuarie:", event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", false)
                .addField("Responsable:", "Consultar en registro de auditoría", false)
                .setTimestamp(Instant.now());

        logs.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        super.onGuildUnban(event);

        log.info("Desbaneo en servidor: " + event.getUser().getAsTag() + " (" + event.getUser().getId() + ").");

        TextChannel logs = TransEspBot.jda.getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Desbaneo en servidor")
                .setColor(Color.red)
                .addField("Usuarie:", event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", false)
                .addField("Responsable:", "Consultar en registro de auditoría", false)
                .setTimestamp(Instant.now());

        logs.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        super.onGuildMemberUpdateNickname(event);

        String viejoNombre = event.getOldNickname() == null ? "*sin apodo*" : event.getOldNickname();
        String nuevoNombre = event.getNewNickname() == null ? "*sin apodo*" : event.getNewNickname();

        log.info("Cambio de apodo en servidor: " + viejoNombre + " es ahora " + nuevoNombre +
                ". Usuarie: " + event.getUser().getAsTag() + " (" + event.getUser().getId() + ").");

        TextChannel logs = TransEspBot.jda.getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Cambio de apodo en servidor")
                .setColor(Color.orange)
                .addField("Apodo antiguo:", viejoNombre, false)
                .addField("Apodo nuevo:", nuevoNombre, false)
                .addField("Usuarie:", event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", false)
                .setTimestamp(Instant.now());

        logs.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onUserUpdateName(@NotNull UserUpdateNameEvent event) {
        super.onUserUpdateName(event);
        log.info("Cambio de nombre de usuarie: " + event.getOldName() + " es ahora " + event.getNewName() +
                ". Usuarie: " + event.getUser().getAsTag() + " (" + event.getUser().getId() + ").");

        TextChannel logs = TransEspBot.jda.getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Cambio de nombre de usuarie")
                .setColor(Color.orange)
                .addField("Nombre antiguo:", event.getOldName(), false)
                .addField("Nombre nuevo:", event.getNewName(), false)
                .addField("Usuarie:", event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", false)
                .setTimestamp(Instant.now());

        logs.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onUserUpdateDiscriminator(@NotNull UserUpdateDiscriminatorEvent event) {
        super.onUserUpdateDiscriminator(event);

        log.info("Cambio de discriminador de usuarie: " + event.getOldDiscriminator() + " es ahora " + event.getNewDiscriminator() +
                ". Usuarie: " + event.getUser().getAsTag() + " (" + event.getUser().getId() + ").");

        TextChannel logs = TransEspBot.jda.getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Cambio de nombre de usuarie")
                .setColor(Color.orange)
                .addField("Discriminador antiguo:", event.getOldDiscriminator(), false)
                .addField("Discriminador nuevo:", event.getNewDiscriminator(), false)
                .addField("Usuarie:", event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", false)
                .setTimestamp(Instant.now());

        logs.sendMessageEmbeds(eb.build()).queue();
    }
}
