package net.transespdiscord;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.transespdiscord.entidades.TemporizadorActivo;
import net.transespdiscord.enums.IdCanales;
import net.transespdiscord.procesadores.ProcesadorEntradaServidor;
import net.transespdiscord.procesadores.ProcesadorSlash;
import net.transespdiscord.procesadores.ProcesadorVarios;
import net.transespdiscord.utilidades.GestorAdvertencias;
import net.transespdiscord.utilidades.GestorTemporizadores;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

import static net.transespdiscord.enums.TextosFijos.NOMBRE_BOT;

@Slf4j
public class TransEspBot {
    public static JDA jda = null;
    public static ArrayList<TemporizadorActivo> temporizadores = new ArrayList<>();

    public static void main(String[] args) {
        final String DISCORD_TOKEN = System.getenv("TRANSESPBOT_TOKEN");
        JDABuilder builder = JDABuilder.createDefault(DISCORD_TOKEN);

        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.addEventListeners(new ProcesadorSlash(), new ProcesadorEntradaServidor(), new ProcesadorVarios());
        builder.setActivity(Activity.playing("Trans en Español"));

        try {
            jda = builder.build().awaitReady();
        } catch (InterruptedException e) {
            log.error("ERROR: SE HA INTERRUMPIDO EL PROCESO. STACK TRACE:");
            e.printStackTrace();
            System.exit(1);
        } catch (LoginException | IllegalArgumentException e) {
            log.error("ERROR: TOKEN NO VALIDO. DEBE ESPECIFICARSE COMO UNA VARIABLE DE ENTORNO LLAMADA "
                    + "'DISCORD_TOKEN'.");
            System.exit(1);
        }

        // Tareas programadas
        temporizadores = GestorTemporizadores.cargarLista();
        log.info(NOMBRE_BOT.texto + ": temporizadores cargados desde BD!");
        GestorAdvertencias.cargarLista();
        log.info(NOMBRE_BOT.texto + ": advertencias cargadas desde BD!");

        ProcesadorEntradaServidor.programarTarea(jda);
        log.info(NOMBRE_BOT.texto + ": tarea aviso a novates creada!");

        // Carga de los comandos de Slash en el servidor para que así sean accesibles
        ProcesadorSlash.cargarComandos(jda);
        log.info(NOMBRE_BOT.texto + ": comandos Slash cargados!");

        // Si hemos llegado hasta aquí está, el bot está listo
        log.info(NOMBRE_BOT.texto + ": bot conectado y listo para funcionamiento.");

        TextChannel logs = jda.getGuilds().get(0).getTextChannelById(IdCanales.LOGS.id);

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(NOMBRE_BOT.texto + ": bot conectado y listo para funcionamiento")
                .setColor(Color.yellow)
                .setTimestamp(Instant.now());

        logs.sendMessageEmbeds(eb.build()).queue();
    }
}
