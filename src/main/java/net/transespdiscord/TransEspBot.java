package net.transespdiscord;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.transespdiscord.entidades.TemporizadorActivo;
import net.transespdiscord.procesadores.ProcesadorEntradaServidor;
import net.transespdiscord.procesadores.ProcesadorSlash;
import net.transespdiscord.procesadores.ProcesadorVarios;
import net.transespdiscord.utilidades.GestorTemporizadores;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

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

        // Carga de los comandos de Slash en el servidor para que así sean accesibles
        ProcesadorSlash.cargarComandos(jda);
        log.info("Comandos Slash cargados!");

        // Tareas programadas
        temporizadores = GestorTemporizadores.cargarLista();
        log.info("Temporizadores cargados desde BD!");

        ProcesadorEntradaServidor.programarTarea(jda);
        log.info("Tarea aviso a novates creada!");
    }
}
