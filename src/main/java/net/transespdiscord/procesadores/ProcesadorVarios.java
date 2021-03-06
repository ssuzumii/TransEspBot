package net.transespdiscord.procesadores;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.transespdiscord.TransEspBot;
import net.transespdiscord.crud.VariableCRUD;
import net.transespdiscord.enums.IdCanales;
import net.transespdiscord.enums.IdRoles;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;

import static net.transespdiscord.enums.TextosFijos.NOMBRE_BOT;

@Slf4j
public class ProcesadorVarios extends ListenerAdapter {
    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        super.onGuildMemberRoleAdd(event);

        // AVISO BOOST -> Detecta si Discord asignó el rol de booster.
        if (VariableCRUD.obtenerPorClave("aviso_boost").getValor().equals("1")
                && event.getRoles().stream().anyMatch(rol -> rol.getId().equals(IdRoles.BOOSTER.id))) {
            Guild servidor = TransEspBot.jda.getGuilds().get(0);

            TextChannel logs = servidor.getTextChannelById(IdCanales.LOGS.id);

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Servidor mejorado")
                    .setColor(Color.magenta)
                    .addField("Nombre:", event.getUser().getAsMention() + "\n" + event.getUser().getAsTag(), true)
                    .addField("Nivel actual:", "" + servidor.getBoostTier(), true)
                    .setTimestamp(Instant.now());

            logs.sendMessageEmbeds(eb.build()).queue();

            servidor.getTextChannelById(IdCanales.ANUNCIOS.id).sendMessage(
                    "¡Muchísimas gracias " + event.getUser().getAsMention() + " por mejorar el servidor!"
            ).queue();
        }
    }

    @Override
    public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent event) {
        super.onGuildMemberUpdateBoostTime(event);


    }

    @Override
    public void onTextChannelCreate(@NotNull TextChannelCreateEvent event) {
        super.onTextChannelCreate(event);

        TextChannel canal = event.getChannel();
        String nombreCanal = event.getChannel().getName();
        Role equipoAdministrativo = event.getGuild().getRoleById(IdRoles.EQUIPO_ADMINISTRATIVO.id);

        log.info("Detectada creación de canal: " + nombreCanal + " en categoría " + canal.getParent().getName() + ".");

        if (nombreCanal.startsWith("ticket-") && !(canal.getParent().getName().equalsIgnoreCase("entrada"))) {
            canal.sendMessage("Este es el canal privado generado por ticket, solo tú y el " +
                    equipoAdministrativo.getAsMention() + " pueden verlo, si quieres añadir a alguien más puedes pedirlo.\n" +
                    "\n" +
                    "Aquí puedes contar lo que sucedió, asegúrate de incluir:\n" +
                    "- Quiénes estuvieron involucrades.\n" +
                    "- Link a mensajes relevantes (puedes hacer clic derecho en el mensaje y luego \"copiar enlace del mensaje\").\n" +
                    "- Capturas de pantalla/screenshots (ten en cuenta que no los tomaremos como evidencia pura ya que " +
                    "pueden ser editados fácilmente).\n" +
                    "- Todos los demás detalles que sepas.\n" +
                    "\n" +
                    "Alguien del equipo administrativo responderá cuanto antes.").queue();
        }

        TextChannel logs = TransEspBot.jda.getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Canal creado")
                .setColor(Color.cyan)
                .addField("Nombre:", nombreCanal, true)
                .addField("Categoría:", canal.getParent().getName(), true)
                .setTimestamp(Instant.now());

        logs.sendMessageEmbeds(eb.build()).queue();
    }

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
                .addField("Usuarie:", event.getUser().getAsMention() + " " + event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", false)
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
                .addField("Usuarie:", event.getUser().getAsMention() + " " + event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", false)
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
